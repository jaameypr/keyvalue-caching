package boo.jamey.caching;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

public class KVCacheImpl<K, V> implements KVCache<K, V> {

    private final ConcurrentHashMap<K, KVCacheEntry<K, V>> store = new ConcurrentHashMap<>();
    private final List<Callback<K, V>> entryAddedListeners = new CopyOnWriteArrayList<>();
    private final List<Callback<K, V>> entryRemovedListeners = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService executorService;

    private final long defaultTtl;

    private final ConcurrentHashMap<K, ScheduledFuture<?>> removalTasks = new ConcurrentHashMap<>();

    public KVCacheImpl(long ttl) {
        this.defaultTtl = ttl;
        this.executorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    }

    @Override
    public V get(K key) {
        KVCacheEntry<K, V> ent = store.get(key);
        return ent != null ? ent.getValue() : null;
    }

    @Override
    public KVCacheEntry<K, V> getEntry(K key) {
        return store.get(key);
    }

    @Override
    public void put(K key, V value) {
        KVCacheEntry<K, V> newEntry = new KVCacheEntry<>(key, value);
        KVCacheEntry<K, V> oldEntry = store.put(key, newEntry);

        // Call entry added callback function
        entryAdded(newEntry);

        if (oldEntry != null) {
            // Call entry removed - because we've overwritten it
            entryRemoved(oldEntry);
        }

        this.insertScheduling(newEntry);
    }

    private void insertScheduling(KVCacheEntry<K, V> entry) {
        if (removalTasks.containsKey(entry.getKey())) removalTasks.get(entry.getKey()).cancel(true);
        ScheduledFuture<?> removalFuture = executorService.schedule(() -> {
            //store.remove(key, newEntry);
            this.remove(entry.getKey());
        }, defaultTtl, TimeUnit.MILLISECONDS);
        removalTasks.put(entry.getKey(), removalFuture);
    }

    @Override
    public boolean remove(K key) {
        KVCacheEntry<K, V> oldEntry = store.remove(key);
        if (oldEntry != null) {
            // Call entry removed - because we removed it
            entryRemoved(oldEntry);
            Optional.ofNullable(removalTasks.get(key)).ifPresent(task -> {
                removalTasks.remove(key);
                task.cancel(true);
            });
            return true;
        }
        return false;
    }

    @Override
    public boolean renew(K key) {
        // only returned if not expired
        KVCacheEntry<K, V> entry = this.getEntry(key);
        if (entry != null) {
            //entry.updateExpirationTime(System.currentTimeMillis() + ttl);
            this.insertScheduling(entry);
            //put(key, entry.getValue());
            return true;
        }
        return false;
    }

    @Override
    public boolean exists(K key) {
        return store.containsKey(key);
    }

    @Override
    public void clear() {
        store.forEach((k,v) -> this.remove(k));
    }

    @Override
    public void addEntryAddedListener(Callback<K, V> callback) {
        entryAddedListeners.add(callback);
    }

    @Override
    public void addEntryRemovedListener(Callback<K, V> callback) {
        entryRemovedListeners.add(callback);
    }

    @Override
    public List<KVCacheEntry<K, V>> entries() {
        return Collections.list(store.elements());
    }

    private void entryRemoved(KVCacheEntry<K, V> entry) {
        entryRemovedListeners.forEach(listener -> listener.call(entry));
    }

    private void entryAdded(KVCacheEntry<K, V> entry) {
        entryAddedListeners.forEach(listener -> listener.call(entry));
    }
}

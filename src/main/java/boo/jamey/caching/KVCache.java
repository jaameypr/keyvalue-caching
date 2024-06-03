package boo.jamey.caching;

import java.util.List;

public interface KVCache<K, V> {

    V get(K key);
    KVCacheEntry<K, V> getEntry(K key);
    void put(K key, V value);
    boolean remove(K key);
    boolean renew(K key);
    boolean exists(K key);
    void clear();

    List<KVCacheEntry<K, V>> entries();

    void addEntryAddedListener(Callback<K, V> callback);
    void addEntryRemovedListener(Callback<K, V> callback);

}

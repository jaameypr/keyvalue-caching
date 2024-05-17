package boo.jamey.caching;

public interface KVCache<K, V> {

    V get(K key);
    KVCacheEntry<K, V> getEntry(K key);
    void put(K key, V value);
    boolean remove(K key);
    boolean renew(K key);
    boolean exists(K key);
    void clear();

    void addEntryAddedListener(Callback<K, V> callback);
    void addEntryRemovedListener(Callback<K, V> callback);

}

package boo.jamey.caching;

public interface EvictionPolicy<K, V> {

    void entryAccessed(KVCacheEntry<K, V> entry);
    KVCacheEntry<K, V> evictEntry();

}

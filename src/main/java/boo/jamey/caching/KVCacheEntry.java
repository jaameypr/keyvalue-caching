package boo.jamey.caching;

public class KVCacheEntry<K, V> {

    private K key;
    private V value;

    public KVCacheEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

}

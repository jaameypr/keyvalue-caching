package boo.jamey.caching;

public interface Callback<K, V> {

    void call(KVCacheEntry<K, V> entry);

}

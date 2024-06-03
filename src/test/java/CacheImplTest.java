import boo.jamey.caching.KVCacheImpl;
import boo.jamey.caching.KVCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class CacheImplTest {

    private KVCache<String, String> cache;
    private final long ttlInMillis = 1000; // 1 second TTL for test

    @BeforeEach
    public void setUp() {
        cache = new KVCacheImpl<>(ttlInMillis);
    }

    @Test
    public void testEntryShouldExistBeforeTTLExpires() throws InterruptedException {
        String key = "testKey";
        String value = "testValue";

        cache.put(key, value);
        String cachedValue = cache.get(key);
        assertNotNull(cachedValue, "Cached value should be present before TTL expires.");

        // Wait for a time less than the default TTL and check again
        TimeUnit.MILLISECONDS.sleep(ttlInMillis / 2); // Wait for half the TTL
        cachedValue = cache.get(key);
        assertNotNull(cachedValue, "Cached value should still be present after half the TTL period.");
    }

    @Test
    public void testEntryShouldBeRemovedAfterTTLExpires() throws InterruptedException {
        String key = "testKey";
        String value = "testValue";

        cache.put(key, value);
        // Wait for a time longer than the TTL
        TimeUnit.MILLISECONDS.sleep(ttlInMillis + 100); // Wait for TTL + 100ms

        String cachedValue = cache.get(key);
        assertNull(cachedValue, "Cached value should be removed after the TTL period.");
    }

    @Test
    public void testRemoveMethodShouldRemoveEntry() {
        String key = "testKey";
        String value = "testValue";

        cache.put(key, value);
        assertTrue(cache.remove(key), "Cache entry should be removed successfully.");
        assertNull(cache.get(key), "Removed entry should not be retrievable from the cache.");
    }

    @Test
    public void testClearMethodShouldClearAllEntries() {
        cache.put("key1", "value1");
        cache.put("key2", "value2");

        cache.clear();
        assertNull(cache.get("key1"), "Cache should be cleared.");
        assertNull(cache.get("key2"), "Cache should be cleared.");
    }

    @Test
    public void testClearMethodEntryRemovedListenerShouldBeNotified() {
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        AtomicInteger deletedItems = new AtomicInteger(0);
        cache.addEntryRemovedListener(entry -> deletedItems.addAndGet(1));
        cache.clear();

        boolean zero = deletedItems.get() == 0;
        assertFalse(zero, "Entry removed listener should get called when clearing.");
    }


    @Test
    public void testEntryAddedListenerShouldBeNotified() {
        AtomicBoolean wasCalled = new AtomicBoolean(false);
        cache.addEntryAddedListener(entry -> wasCalled.set(true));

        cache.put("key1", "value1");
        assertTrue(wasCalled.get(), "Entry added listener should be called when new entry is added.");
    }

    @Test
    public void testEntryRemovedListenerShouldBeNotifiedOnRemoval() throws InterruptedException {
        AtomicBoolean wasCalled = new AtomicBoolean(false);
        cache.addEntryRemovedListener(entry -> wasCalled.set(true));

        String key = "key1";
        cache.put(key, "value1");
        cache.remove(key);

        assertTrue(wasCalled.get(), "Entry removed listener should be called when an entry is removed.");
    }

    @Test
    public void testEntryRemovedListenerShouldBeNotifiedOnExpiration() throws InterruptedException {
        AtomicBoolean wasCalled = new AtomicBoolean(false);
        cache.addEntryRemovedListener(entry -> wasCalled.set(true));

        cache.put("key1", "value1");
        // Wait for the TTL to expire and a small buffer to ensure the removal is processed
        TimeUnit.MILLISECONDS.sleep(ttlInMillis + 100);

        assertTrue(wasCalled.get(), "Entry removed listener should be notified when an entry expires.");
    }

    @Test
    public void testClearRemovesAllEntriesFromCache() {
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.clear();

        assertTrue(cache.entries().isEmpty());
    }

    @Test
    public void testEntriesLengthCorrect() {
        cache.put("key1", "value1");
        cache.put("key2", "value2");

        assertTrue(cache.entries().size() > 1);
    }

}

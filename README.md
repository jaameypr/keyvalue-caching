# TTL Cache System

## Overview

The TTL Cache System is a Java-based caching solution designed to temporarily store key-value pairs with a specified time-to-live (TTL) duration. Once an entry's TTL elapses, it is automatically evicted from the cache. This system is ideal for scenarios where data freshness is critical and where stale data must be removed proactively without user intervention.

## Features

- **Configurable TTL:** Each entry in the cache is associated with a TTL value, after which the entry is considered stale and is removed.
- **Automatic Eviction:** The system handles the eviction process automatically, ensuring that expired entries do not consume memory.
- **High Throughput:** Optimized for performance, the cache system can handle a high rate of read and write operations.
- **Thread-Safety:** Designed to be accessed by multiple threads concurrently without the risk of data corruption.
- **Event Notifications:** The system can be configured to notify external components when entries are added or removed.

## How to Use

### Creating a Cache Instance

First, instantiate the cache with the desired TTL for entries:

```java
import your.package.CacheImpl;

public class Main {
    public static void main(String[] args) {
        long defaultTtl = 5000; // 5 seconds

        KVCache<String, String> cache = new CacheImpl<>(defaultTtl);
        
        // Now you can use the cache instance in your application.
    }
}
```

### Adding Entries to the Cache

Add entries to the cache by specifying a key and value. The entry will automatically be marked for eviction after the TTL elapses:

```java
// overrides key if existing
cache.put("myKey", "myValue");
```

### Retrieving Entries from the Cache

Retrieve values by their keys. If the key is expired or does not exist, null is returned:

```java
String value = cache.get("myKey");
```

### Refreshing Entries TTL

Reset entries TTL by using their key. If the entrys TTL isnt expired, it gets reset to the defaultTtl:

```java
// returns boolean if successful
cache.renew("myKey");
```

### Removing Entries from the Cache

Manually remove entries before their TTL expires if needed:

```java
// returns boolean if successful
cache.remove("myKey");
```

### Clearing the Cache

Clear all entries from the cache at once:

```java
// when executed the entryRemovedEvent will be called
cache.clear();
```

### Adding Entry removed Callback (Listener)

Add an Callback function that gets called if an entry is removed:

```java
cache.addEntryRemovedListener(entry -> {/*YOUR METHOD IN HERE*/});
```

### Adding Entry removed Callback (Listener)

Add an Callback function that gets called if an entry is added:

```java
cache.addEntryAddedListener(entry -> {/*YOUR METHOD IN HERE*/});
```

---

### Todo | Thinking about...

- ☐ lazy eviction strategy, where entries are only checked upon access.

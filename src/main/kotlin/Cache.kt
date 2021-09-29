
interface Cache<K, V> {
    /**
     * Retrieve an item from cache, null if not found.
     * @param key The key of the item to retrieve.
     * @return The value retrieved from the cache. Possibly null if the key is not found.
     */
    fun get(key: K): V?

    /**
     * Add an item into the cache with a ttl limit
     * @param key The key to add to the map. Cannot be null.
     * @param value The value to add to the map. Cannot be null.
     * @param ttl The time to live in milliseconds since midnight (Epoch time milliseconds). Cannot be null.
     */
    fun put(key: K, value: V, ttl: Long? = null)

    /**
     * Deletes an item from the cache.
     * @param key The key of the item to remove from the cache.
     */
    fun delete(key: K)

    /**
     * Clear the items from the cache.
     */
    fun clear()

    /**
     * Return the size of the cache.
     */
    fun size(): Int
}
/**
 * Represents a cacheable value with a time to live
 *
 * value - the value to cache.
 * ttl - time to live in milliseconds since midnight (epoch time).
 */
data class CacheableValue<V> (val value: V, val ttl : Long? )
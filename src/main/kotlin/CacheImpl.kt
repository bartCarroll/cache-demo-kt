import java.time.Instant
import java.util.*
import kotlin.collections.LinkedHashMap


/**
 * As simple cache demo.
 * @param capacity The capacity of the cache. Defaults to 1000.
 * @param pruneMillis The period between pruning events in milliseconds. Defaults to 300000
 */
class CacheImpl<K, V>(private val capacity: Int = 1000,
                      private val pruneMillis: Long =300000) : Cache <K, V>{
    private var theCache: MutableMap<K, CacheableValue<V>> =
        Collections.synchronizedMap(LinkedHashMap(capacity))

    private val cleanupThread = Thread{
        while (true){
            cleanup()
            println("cleanup thread sleeping $pruneMillis")
            Thread.sleep(pruneMillis)
        }
    }
    init{
        this.cleanupThread.start()
    }

    @Synchronized override fun get(key: K): V? {
        return this.theCache[key]?.value
    }

    @Synchronized override fun put(key: K, value: V, ttl: Long?) {
        if(this.theCache.size >= this.capacity){
            val keyToRemove = this.theCache.iterator().next().key
            theCache.remove(keyToRemove)
        }
        this.theCache[key] = CacheableValue(value, ttl)
    }

    @Synchronized override fun delete(key: K){
        this.theCache.remove(key)
    }

    @Synchronized override fun clear() {
        this.theCache = Collections.synchronizedMap(LinkedHashMap(capacity))
    }

    @Synchronized private fun cleanup(){
        for ((k, v) in this.theCache) {
            v.ttl?.let{
                if(it < Instant.now().toEpochMilli()){
                    this.theCache.remove(k)
                }
            }
        }

    }

    override fun size(): Int{
        return this.theCache.size
    }
}
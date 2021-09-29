import org.junit.jupiter.api.Test
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.ceil

import kotlin.system.measureNanoTime

class CacheImplPerformanceTests {

    private var consumerStats : MutableMap<Long, Boolean> = Collections.synchronizedMap(HashMap(20000000))
    private var producerStats : MutableList<Long> = Collections.synchronizedList(ArrayList(20000000))
    private var doWork = true

    @Synchronized fun updateProducerStats(nanoSeconds: Long){
        this.producerStats.add(nanoSeconds)
    }

    @Synchronized fun updateConsumerStats(nanoSeconds: Long, cacheMiss: Boolean){
        this.consumerStats[nanoSeconds] = cacheMiss
    }

    private fun producer(key: Int, value: String, cache: Cache<Int, String>){
        val elapsed = measureNanoTime {
            cache.put(key, value)
        }
        updateProducerStats(elapsed)
    }

    private fun consumer(key: Int, cache: Cache<Int, String>){
        val retrieved: String?
        val elapsed = measureNanoTime {
            retrieved = cache.get(key)
        }
        updateConsumerStats(elapsed, retrieved == null)
    }

    @Test
    fun testGet(){
        val myCache: CacheImpl<Int, String> = CacheImpl(capacity = 2000000, pruneMillis = 60000 )
        val producerCnt = 10
        val consumerCnt = 10

        println("Pre-populating cache.")
        (0..20000000).forEach{myCache.put(it, UUID.randomUUID().toString())}

        val producerThreads: List<Thread> = (0..producerCnt).map{Thread {
            while(doWork){
                val key = ThreadLocalRandom.current().nextInt(0, 20000000)
                val value = UUID.randomUUID().toString()
                producer(key, value, myCache)
                Thread.sleep(ThreadLocalRandom.current().nextLong(500, 5000))
            }
        } }
        val consumerThreads: List<Thread> = (0..consumerCnt).map{Thread {
            while(doWork){
                val key = ThreadLocalRandom.current().nextInt(0, 20000000)
                consumer(key, myCache)
                Thread.sleep(ThreadLocalRandom.current().nextLong(0, 3000))
            }
        } }

        println("Launching producer threads..")
        producerThreads.forEach { it.start() }
        println("Launching consumer threads..")
        consumerThreads.forEach {it.start()}
        Thread.sleep( 300000)
        doWork = false
        producerThreads.forEach{it.join()}
        consumerThreads.forEach{it.join()}


        println("Retrieval Times (95th Percentile): ${percentile(consumerStats.keys.toMutableList(), 0.95).toDouble()/1000000.00 }ms")
        println("Retrieval Times (99th Percentile): ${percentile(producerStats, 0.99).toDouble()/1000000.00 }ms ")
    }

    private fun percentile(times: MutableList<Long>, percentile: Double): Long {
        times.sort()
        val index =  ceil(percentile / 100.0 * times.size).toInt()
        return times[index-1]
    }
}
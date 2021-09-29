import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class CacheImplTests {

    @Test
    fun testGet(){
        val myCache: CacheImpl<Long, String> = CacheImpl()

        myCache.put(1, "one")
        Assertions.assertEquals("one", myCache.get(1))
    }

    @Test
    fun testPut(){
        val myCache: CacheImpl<Long, String> = CacheImpl()

        myCache.put(1, "one")
        myCache.put(2, "two")
        Assertions.assertEquals("one", myCache.get(1))
        Assertions.assertEquals("two", myCache.get(2))
    }

    @Test
    fun testPutDuplicateKeys(){
        val myCache: CacheImpl<Long, String> = CacheImpl()

        myCache.put(1, "one")
        Assertions.assertEquals("one", myCache.get(1))
        myCache.put(1, "two")
        Assertions.assertEquals("two", myCache.get(1))
    }

    @Test
    fun testDelete(){
        val myCache: CacheImpl<Long, String> = CacheImpl()

        myCache.put(1, "one")
        Assertions.assertEquals("one", myCache.get(1))
        myCache.delete(1)
        Assertions.assertEquals(null, myCache.get(1))
    }

    @Test
    fun testPruneInterval(){
        val myCache: CacheImpl<Long, String> = CacheImpl(pruneMillis = 1000 )

        myCache.put(1, "one", 1000)
        Assertions.assertEquals("one", myCache.get(1))
        Thread.sleep(1050)
        Assertions.assertEquals(null, myCache.get(1))
    }

    @Test
    fun testCapacityEvict(){
        val myCache: CacheImpl<Long, String> = CacheImpl(capacity = 2)
        myCache.put(1, "one")
        myCache.put(2, "two")
        Assertions.assertEquals("one", myCache.get(1))
        Assertions.assertEquals("two", myCache.get(2))
        myCache.put(3, "three")
        Assertions.assertEquals(2, myCache.size())
        Assertions.assertEquals("three", myCache.get(3))
        Assertions.assertEquals(null, myCache.get(1))
        myCache.put(4, "four")
        Assertions.assertEquals(2, myCache.size())
        Assertions.assertEquals("three", myCache.get(3))
        Assertions.assertEquals("four", myCache.get(4))
        Assertions.assertEquals(null, myCache.get(1))
        Assertions.assertEquals(null, myCache.get(2))
    }


}
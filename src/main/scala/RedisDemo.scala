import com.redis._

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.concurrent.duration.{Duration, SECONDS}

object RedisDemo {
  val redisHost = "localhost"
  val redisPort = 6379
  val redisPassword = "insertAStrongRedisPasswordFromASecureStorage"

  val DEFAULT_EXPIRATION_SECS = 600
  val DEFAULT_EXPIRATION = Duration(DEFAULT_EXPIRATION_SECS, SECONDS)

  def main(args: Array[String]): Unit = {
    // Connect to Redis
    val r0 = connectToRedisDb()
    val r1 = connectToRedisDb(1)

    println("Strings: Saving, Modifying, and Retrieving")
    val stringKey = "greeting"
    println(r0.get(stringKey)) // Get the string, if pre-existing
    r0.set(stringKey, "Hello, world!", expire = DEFAULT_EXPIRATION) // Save a string
    r0.set(stringKey, "Hello, Redis!", expire = DEFAULT_EXPIRATION) // Replace the string
    println(r0.get(stringKey)) // Get the string
    printExpiration(r0, stringKey)
    println()

    println("Lists: Saving, Modifying, and Retrieving")
    val listKey = "mylist"
    println(r0.lrange(listKey, 0, -1)) // Get the list, if pre-existing
    r0.lpush(listKey, "one") // Save initial elements to a list
    r0.lpush(listKey, "two", "three") // Add more elements to the list
    r0.lset(listKey, 0, "four") // Replace an element in the list
    println(r0.lrange(listKey, 0, -1)) // Get the list
    r0.expire(listKey, DEFAULT_EXPIRATION_SECS) // Set expiration time
    printExpiration(r0, listKey)
    println()

    println("Hashes: Saving, Modifying, and Retrieving")
    val hashKey = "mymap"
    r0.hset(hashKey, "name", "John")
    r0.hset(hashKey, "age", "30")
    r0.hset(hashKey, "age", "31") // Modify the hash
    println(r0.hgetall(hashKey)) // Get the hash
    r0.pexpire(hashKey, 500) // Expire this after only 500 millisecs
    printExpiration(r0, hashKey)
    println()

    println("Sets: Saving, Modifying, and Retrieving")
    val setKey = "myset"
    r0.sadd(setKey, "a", "b", "c") // Save elements to a set
    r0.sadd(setKey, "d") // Add more elements to the set
    r0.srem(setKey, "a") // Remove an element from the set
    println(r0.smembers(setKey)) // Get the set
    r0.expireat(setKey, (System.currentTimeMillis() / 1000 + 60).toInt) // Expire in 1 minute from now
    printExpiration(r0, setKey)
    println()

    println("Sorted Sets: Saving, Modifying, and Retrieving")
    val sortedSetKey = "mysortedset"
    r0.zadd(sortedSetKey, 1d, "a") // Save element to a sorted set
    r0.zadd(sortedSetKey, 2d, "b") // Save element to a sorted set
    r0.zadd(sortedSetKey, 3d, "c") // Save element to a sorted set
    r0.zadd(sortedSetKey, 5d, "a") // Modify the score of an element
    println(r0.zrangeWithScore(sortedSetKey, 0, -1)) // Get the sorted set
    println()

    println("HyperLogLog: Adding elements and estimating cardinality")
    val hllKey = "hll_users"
    var estimatedUsers = r0.pfcount(hllKey) // Estimate cardinality
    println(s"Estimated number of unique users: $estimatedUsers")
    r0.pfadd(hllKey, "user1", "user2", "user3", "user2") // Add elements
    r0.pfadd(hllKey, "user1", "user5", "user7", "user8") // Add elements
    estimatedUsers = r0.pfcount(hllKey) // Estimate cardinality again
    println(s"Estimated number of unique users: $estimatedUsers")
    println()

    println("Geospatial Indexes: Adding, querying distance, and querying nearby members")
    val geoKey = "cities"
    r0.geoadd(geoKey, Seq(
      (13.361389, 38.115556, "Palermo"), (15.087269, 37.502669, "Catania"), (8.5417, 47.3769, "Zurich")
    ))
    val distance = r0.geodist(geoKey, "Palermo", "Catania", Option("km")) // Distance between cities
    val position = r0.geopos(geoKey, "Palermo") // Position of Palermo
    // Cities within 200 km
    val nearbyCities = r0.georadiusbymember(geoKey, "Catania", 200, "km",
      withCoord = false, withDist = true, withHash = false,
      count = None, sort = Option("ASC"), store = None, storeDist = None)
    println(s"Distance between Palermo and Catania: $distance")
    println(s"Position of Palermo: $position")
    println(s"Cities within 200 km of Catania, with distance: $nearbyCities")
    println()

    // Redis streams are currently not supported in scala-redis
  }

  def connectToRedisDb(dbNr: Int = 0): RedisClient = {
    if (dbNr < 0 || dbNr > 15) {
      throw new IllegalArgumentException("db_nr out of bounds")
    }
    new RedisClient(host = redisHost, port = redisPort, database = dbNr, secret = Some(redisPassword))
  }

  private val NA = "N/A"
  private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  def printExpiration(r0: RedisClient, key: String): Unit = {
    val maybePttl = r0.pttl(key)
    val maybeTtl = r0.ttl(key)
    val pttlStr = maybePttl.fold(NA)(_.toString)
    val ttlStr = maybeTtl.fold(NA)(_.toString)
    // TODO: directly get the expiration time from Redis
    val maybeExpirationTime =
      maybeTtl.map(LocalDateTime.now().plusSeconds) orElse maybePttl.map(ms => LocalDateTime.now().plusNanos(ms * 1000))
    val expirationTimeStr = maybeExpirationTime.fold(NA)(dateFormatter.format)
    println(
      s"The Time-To-Live of key '$key' is $pttlStr millisecs ~= $ttlStr secs. " +
        s"It will expire at time $expirationTimeStr."
    )
  }

}

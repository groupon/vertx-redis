vert.x-redis
============

<a href="https://raw.githubusercontent.com/groupon/vertx-redis/master/LICENSE">
    <img src="https://img.shields.io/hexpm/l/plug.svg"
         alt="License: Apache 2">
</a>
<a href="http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.groupon.vertx%22%20a%3A%22vertx-redis%22">
    <img src="https://img.shields.io/maven-central/v/com.groupon.vertx/vertx-redis.svg"
         alt="Maven Artifact">
</a>

This is an async library for sending commands to Redis.

Usage
-----

Configuration for Redis Verticle:

```json
{
    "redisConfig": {
        "host": "some-redis-server",
        "port": 6379,
        "eventBusAddress": "address_where_redis_handler_is_registered"
    }
}
```

Setting up a client and calling a simple get:

```java
    RedisClient redisClient = new RedisClient(eventBus, "addresss_where_redis_handler_is_registered", timeout);
    Future<JsonObject> result = redisClient.get("key");
```

The JsonObject in the future result will be in a [Jsend](https://labs.omniti.com/labs/jsend) format.  In the case of the get call above it will be similar to:

```json
{
    "status": "success",
    "data": {
        "key": "value"
    }
}
```

Results in the data block will vary based on the Redis method being called.

Building
--------

Prerequisites:
* [JDK8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Maven 3.3.3+](http://maven.apache.org/download.cgi)
* [Redis](http://redis.io/)

Building:

    vertx-redis> mvn verify

To use the local version you must first install it locally:

    vertx-redis> mvn install

You can determine the version of the local build from the pom file.  Using the local version is intended only for testing or development.


License
-------

Published under Apache Software License 2.0, see LICENSE

&copy; Groupon Inc., 2014

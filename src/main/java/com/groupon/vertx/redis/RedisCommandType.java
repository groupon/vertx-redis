/**
 * Copyright 2014 Groupon.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.groupon.vertx.redis;

/**
 * List of Redis commands.
 *
 * @author Stuart Siegrist (fsiegrist at groupon dot com)
 * @since 1.0.0
 */
public enum RedisCommandType {
    APPEND(RedisResponseType.INTEGER_REPLY),
    AUTH(RedisResponseType.STATUS_CODE),
    BITCOUNT(RedisResponseType.INTEGER_REPLY),
    BITOP(RedisResponseType.INTEGER_REPLY),
    BLPOP(RedisResponseType.MULTI_BULK_REPLY), // Binary
    BRPOP(RedisResponseType.MULTI_BULK_REPLY), // Binary
    BRPOPLPUSH(RedisResponseType.BULK_REPLY), // Binary
    DECR(RedisResponseType.INTEGER_REPLY),
    DECRBY(RedisResponseType.INTEGER_REPLY),
    DEL(RedisResponseType.INTEGER_REPLY),
    DISCARD(RedisResponseType.STATUS_CODE),
    DUMP(RedisResponseType.BULK_REPLY),
    ECHO(RedisResponseType.BULK_REPLY), // Binary
    EXEC(RedisResponseType.MULTI_BULK_REPLY),
    EXISTS(RedisResponseType.INTEGER_REPLY),
    EXPIRE(RedisResponseType.INTEGER_REPLY),
    EXPIREAT(RedisResponseType.INTEGER_REPLY),
    FLUSHALL(RedisResponseType.STATUS_CODE),
    FLUSHDB(RedisResponseType.STATUS_CODE),
    GET(RedisResponseType.BULK_REPLY), // Binary
    GETBIT(RedisResponseType.INTEGER_REPLY),
    GETRANGE(RedisResponseType.BULK_REPLY),
    GETSET(RedisResponseType.BULK_REPLY), // Binary
    HDEL(RedisResponseType.INTEGER_REPLY),
    HEXISTS(RedisResponseType.INTEGER_REPLY),
    HGET(RedisResponseType.BULK_REPLY), // Binary
    HGETALL(RedisResponseType.MULTI_BULK_REPLY), // Binary
    HINCRBY(RedisResponseType.INTEGER_REPLY),
    HINCRBYFLOAT(RedisResponseType.BULK_REPLY),
    HKEYS(RedisResponseType.MULTI_BULK_REPLY), // Binary
    HLEN(RedisResponseType.INTEGER_REPLY),
    HMGET(RedisResponseType.MULTI_BULK_REPLY), // Binary
    HMSET(RedisResponseType.STATUS_CODE),
    HSET(RedisResponseType.INTEGER_REPLY),
    HSETNX(RedisResponseType.INTEGER_REPLY),
    HVALS(RedisResponseType.MULTI_BULK_REPLY), // Binary
    INCR(RedisResponseType.INTEGER_REPLY),
    INCRBY(RedisResponseType.INTEGER_REPLY),
    INCRBYFLOAT(RedisResponseType.BULK_REPLY),
    KEYS(RedisResponseType.MULTI_BULK_REPLY), // Binary
    LINDEX(RedisResponseType.BULK_REPLY), // Binary
    LINSERT(RedisResponseType.INTEGER_REPLY),
    LLEN(RedisResponseType.INTEGER_REPLY),
    LPOP(RedisResponseType.BULK_REPLY), // Binary
    LPUSH(RedisResponseType.INTEGER_REPLY),
    LPUSHX(RedisResponseType.INTEGER_REPLY),
    LRANGE(RedisResponseType.MULTI_BULK_REPLY), // Binary
    LREM(RedisResponseType.INTEGER_REPLY),
    LSET(RedisResponseType.STATUS_CODE),
    LTRIM(RedisResponseType.STATUS_CODE),
    MGET(RedisResponseType.MULTI_BULK_REPLY), // Binary
    MSET(RedisResponseType.STATUS_CODE),
    MSETNX(RedisResponseType.INTEGER_REPLY),
    MULTI(RedisResponseType.STATUS_CODE),
    OBJECT_IDLETIME("OBJECT", RedisResponseType.INTEGER_REPLY),
    OBJECT_ENCODING("OBJECT", RedisResponseType.BULK_REPLY), // Binary
    OBJECT_REFCOUNT("OBJECT", RedisResponseType.INTEGER_REPLY),
    PERSIST(RedisResponseType.INTEGER_REPLY),
    PEXPIRE(RedisResponseType.INTEGER_REPLY),
    PEXPIREAT(RedisResponseType.INTEGER_REPLY),
    PING(RedisResponseType.STATUS_CODE),
    PSETEX(RedisResponseType.STATUS_CODE),
    PTTL(RedisResponseType.INTEGER_REPLY),
    PUBLISH(RedisResponseType.INTEGER_REPLY),
    RANDOMKEY(RedisResponseType.BULK_REPLY), // Binary
    RENAME(RedisResponseType.STATUS_CODE),
    RENAMENX(RedisResponseType.INTEGER_REPLY),
    RPOP(RedisResponseType.BULK_REPLY), // Binary
    RPOPLPUSH(RedisResponseType.BULK_REPLY), // Binary
    RPUSH(RedisResponseType.INTEGER_REPLY),
    RPUSHX(RedisResponseType.INTEGER_REPLY),
    SADD(RedisResponseType.INTEGER_REPLY),
    SCARD(RedisResponseType.INTEGER_REPLY),
    SDIFF(RedisResponseType.MULTI_BULK_REPLY), // Binary
    SDIFFSTORE(RedisResponseType.INTEGER_REPLY),
    SELECT(RedisResponseType.STATUS_CODE),
    SET(RedisResponseType.STATUS_CODE),
    SETBIT(RedisResponseType.INTEGER_REPLY),
    SETEX(RedisResponseType.STATUS_CODE),
    SETNX(RedisResponseType.INTEGER_REPLY),
    SETRANGE(RedisResponseType.INTEGER_REPLY),
    SINTER(RedisResponseType.MULTI_BULK_REPLY), // Binary
    SINTERSTORE(RedisResponseType.INTEGER_REPLY),
    SISMEMBER(RedisResponseType.INTEGER_REPLY),
    SMEMBERS(RedisResponseType.MULTI_BULK_REPLY), // Binary
    SMOVE(RedisResponseType.INTEGER_REPLY),
    SORT(RedisResponseType.MULTI_BULK_REPLY), // Binary
    SORT_WITH_STORE("SORT", RedisResponseType.INTEGER_REPLY),
    SPOP(RedisResponseType.BULK_REPLY), // Binary
    SRANDMEMBER(RedisResponseType.BULK_REPLY), // Binary
    SREM(RedisResponseType.INTEGER_REPLY),
    STRLEN(RedisResponseType.INTEGER_REPLY),
    SUNION(RedisResponseType.MULTI_BULK_REPLY), // Binary
    SUNIONSTORE(RedisResponseType.INTEGER_REPLY),
    TTL(RedisResponseType.INTEGER_REPLY),
    TYPE(RedisResponseType.STATUS_CODE),
    ZADD(RedisResponseType.INTEGER_REPLY),
    ZCARD(RedisResponseType.INTEGER_REPLY),
    ZCOUNT(RedisResponseType.INTEGER_REPLY),
    ZINCRBY(RedisResponseType.BULK_REPLY),
    ZINTERSTORE(RedisResponseType.INTEGER_REPLY),
    ZRANGE(RedisResponseType.MULTI_BULK_REPLY), // Binary
    ZRANGEBYSCORE(RedisResponseType.MULTI_BULK_REPLY), // Binary
    ZRANK(RedisResponseType.INTEGER_REPLY),
    ZREM(RedisResponseType.INTEGER_REPLY),
    ZREMRANGEBYRANK(RedisResponseType.INTEGER_REPLY),
    ZREMRANGEBYSCORE(RedisResponseType.INTEGER_REPLY),
    ZREVRANGE(RedisResponseType.MULTI_BULK_REPLY), // Binary
    ZREVRANGEBYSCORE(RedisResponseType.MULTI_BULK_REPLY), // Binary
    ZREVRANK(RedisResponseType.INTEGER_REPLY),
    ZSCORE(RedisResponseType.BULK_REPLY),
    ZUNIONSTORE(RedisResponseType.INTEGER_REPLY);
    private final String command;
    private final RedisResponseType responseType;

    RedisCommandType(RedisResponseType responseType) {
        this(null, responseType);
    }

    RedisCommandType(String command, RedisResponseType responseType) {
        this.command = command == null ? this.name() : command;
        this.responseType = responseType;
    }

    public String getCommand() {
        return command;
    }

    public RedisResponseType getResponseType() {
        return responseType;
    }
}

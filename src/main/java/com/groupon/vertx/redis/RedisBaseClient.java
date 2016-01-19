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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

/**
 * Implementation for RedisBaseCommandClient.
 *
 * @author Stuart Siegrist (fsiegrist at groupon dot com)
 * @since 1.0.0
 */
abstract class RedisBaseClient implements RedisBaseCommandClient {
    private static final HashSet<String> BIT_OPERATIONS = new HashSet<>(Arrays.asList(new String[]{"AND", "OR", "XOR", "NOT"}));
    private static final HashSet<String> SORT_ORDER = new HashSet<>(Arrays.asList(new String[]{"DESC", "ALPHA"}));
    protected final EventBus eventBus;
    protected final String eventBusAddress;
    protected final long replyTimeout;


    protected RedisBaseClient(EventBus eventBus, String eventBusAddress, long timeout) {
        this.eventBus = eventBus;
        this.eventBusAddress = eventBusAddress;
        this.replyTimeout = timeout;
    }

    public Future<JsonObject> append(String key, String value) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(value);
        return sendCommand(new RedisCommand(RedisCommandType.APPEND, arguments));
    }

    public Future<JsonObject> bitcount(String key) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        return sendCommand(new RedisCommand(RedisCommandType.BITCOUNT, arguments));
    }

    public Future<JsonObject> bitcount(String key, int start, int end) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(String.valueOf(start));
        arguments.add(String.valueOf(end));
        return sendCommand(new RedisCommand(RedisCommandType.BITCOUNT, arguments));
    }

    public Future<JsonObject> bitop(String operation, String destKey, List<String> keys) {
        if (!BIT_OPERATIONS.contains(operation)) {
            throw new IllegalArgumentException("Invalid BITOP operation: " + operation);
        }

        if (keys == null || keys.size() == 0) {
            throw new IllegalArgumentException("No keys provided for BITOP");
        }

        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(operation);
        arguments.add(destKey);

        for (String key : keys) {
            arguments.add(key);
        }
        return sendCommand(new RedisCommand(RedisCommandType.BITOP, arguments));
    }

    public Future<JsonObject> blpop(int timeout, List<String> keys) {
        if (keys == null || keys.size() == 0) {
            throw new IllegalArgumentException("No keys provided for BLPOP");
        }

        ArrayList<String> arguments = new ArrayList<>();

        for (String key : keys) {
            arguments.add(key);
        }

        arguments.add(String.valueOf(timeout));
        return sendCommand(new RedisCommand(RedisCommandType.BLPOP, arguments));
    }

    public Future<JsonObject> brpop(int timeout, List<String> keys) {
        if (keys == null || keys.size() == 0) {
            throw new IllegalArgumentException("No keys provided for BRPOP");
        }

        ArrayList<String> arguments = new ArrayList<>();
        for (String key : keys) {
            arguments.add(key);
        }
        arguments.add(String.valueOf(timeout));
        return sendCommand(new RedisCommand(RedisCommandType.BRPOP, arguments));
    }

    public Future<JsonObject> brpoplpush(String source, String destination, int timeout) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(source);
        arguments.add(destination);
        arguments.add(String.valueOf(timeout));
        return sendCommand(new RedisCommand(RedisCommandType.BRPOPLPUSH, arguments));
    }

    public Future<JsonObject> decr(String key) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        return sendCommand(new RedisCommand(RedisCommandType.DECR, arguments));
    }

    public Future<JsonObject> decrby(String key, int decrement) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(String.valueOf(decrement));
        return sendCommand(new RedisCommand(RedisCommandType.DECRBY, arguments));
    }

    public Future<JsonObject> del(String key) {
        return del(Arrays.asList(new String[]{key}));
    }

    public Future<JsonObject> del(List<String> keys) {
        if (keys == null || keys.size() == 0) {
            throw new IllegalArgumentException("No keys provided for DEL");
        }

        ArrayList<String> arguments = new ArrayList<>();

        for (String key : keys) {
            arguments.add(key);
        }
        return sendCommand(new RedisCommand(RedisCommandType.DEL, arguments));
    }

    public Future<JsonObject> echo(String key) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        return sendCommand(new RedisCommand(RedisCommandType.ECHO, arguments));
    }

    public Future<JsonObject> exists(String key) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        return sendCommand(new RedisCommand(RedisCommandType.EXISTS, arguments));
    }

    public Future<JsonObject> expire(String key, int seconds) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(String.valueOf(seconds));
        return sendCommand(new RedisCommand(RedisCommandType.EXPIRE, arguments));
    }

    public Future<JsonObject> expireat(String key, long unixTimestamp) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(String.valueOf(unixTimestamp));
        return sendCommand(new RedisCommand(RedisCommandType.EXPIREAT, arguments));
    }

    public Future<JsonObject> flushall() {
        ArrayList<String> arguments = new ArrayList<>();
        return sendCommand(new RedisCommand(RedisCommandType.FLUSHALL, arguments));
    }

    public Future<JsonObject> flushdb() {
        ArrayList<String> arguments = new ArrayList<>();
        return sendCommand(new RedisCommand(RedisCommandType.FLUSHDB, arguments));
    }

    public Future<JsonObject> get(String key) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        return sendCommand(new RedisCommand(RedisCommandType.GET, arguments));
    }

    public Future<JsonObject> getbit(String key, int offset) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(String.valueOf(offset));
        return sendCommand(new RedisCommand(RedisCommandType.GETBIT, arguments));
    }

    public Future<JsonObject> getrange(String key, long start, long end) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(String.valueOf(start));
        arguments.add(String.valueOf(end));
        return sendCommand(new RedisCommand(RedisCommandType.GETRANGE, arguments));
    }

    public Future<JsonObject> getset(String key, String value) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(value);
        return sendCommand(new RedisCommand(RedisCommandType.GETSET, arguments));
    }

    public Future<JsonObject> hdel(String key, String field) {
        return hdel(key, Arrays.asList(new String[]{field}));
    }

    public Future<JsonObject> hdel(String key, List<String> fields) {
        if (fields == null || fields.size() == 0) {
            throw new IllegalArgumentException("No fields provided for HDEL");
        }

        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);

        for (String field : fields) {
            arguments.add(field);
        }
        return sendCommand(new RedisCommand(RedisCommandType.HDEL, arguments));
    }

    public Future<JsonObject> hexists(String key, String field) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(field);
        return sendCommand(new RedisCommand(RedisCommandType.HEXISTS, arguments));
    }

    public Future<JsonObject> hget(String key, String field) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(field);
        return sendCommand(new RedisCommand(RedisCommandType.HGET, arguments));
    }

    public Future<JsonObject> hgetall(String key) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        return sendCommand(new RedisCommand(RedisCommandType.HGETALL, arguments));
    }

    public Future<JsonObject> hincrby(String key, String field, int increment) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(field);
        arguments.add(String.valueOf(increment));
        return sendCommand(new RedisCommand(RedisCommandType.HINCRBY, arguments));
    }

    public Future<JsonObject> hincrbyfloat(String key, String field, double increment) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(field);
        arguments.add(String.valueOf(increment));
        return sendCommand(new RedisCommand(RedisCommandType.HINCRBYFLOAT, arguments));
    }

    public Future<JsonObject> hkeys(String key) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        return sendCommand(new RedisCommand(RedisCommandType.HKEYS, arguments));
    }

    public Future<JsonObject> hlen(String key) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        return sendCommand(new RedisCommand(RedisCommandType.HLEN, arguments));
    }

    public Future<JsonObject> hmget(String key, String field) {
        return hmget(key, Arrays.asList(new String[]{field}));
    }

    public Future<JsonObject> hmget(String key, List<String> fields) {
        if (fields == null || fields.size() == 0) {
            throw new IllegalArgumentException("No fields provided for HMGET");
        }

        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);

        for (String field : fields) {
            arguments.add(field);
        }
        return sendCommand(new RedisCommand(RedisCommandType.HMGET, arguments));
    }

    public Future<JsonObject> hmset(String key, Map<String, String> fieldPairs) {
        if (fieldPairs == null || fieldPairs.size() == 0) {
            throw new IllegalArgumentException("Null or invalid field/value pair definitions.");
        }

        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);

        for (Map.Entry<String, String> fieldPair : fieldPairs.entrySet()) {
            arguments.add(fieldPair.getKey());
            arguments.add(fieldPair.getValue());
        }
        return sendCommand(new RedisCommand(RedisCommandType.HMSET, arguments));
    }

    public Future<JsonObject> hset(String key, String field, String value) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(field);
        arguments.add(value);
        return sendCommand(new RedisCommand(RedisCommandType.HSET, arguments));
    }

    public Future<JsonObject> hsetnx(String key, String field, String value) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(field);
        arguments.add(value);
        return sendCommand(new RedisCommand(RedisCommandType.HSETNX, arguments));
    }

    public Future<JsonObject> hvals(String key) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        return sendCommand(new RedisCommand(RedisCommandType.HVALS, arguments));
    }

    public Future<JsonObject> incr(String key) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        return sendCommand(new RedisCommand(RedisCommandType.INCR, arguments));
    }

    public Future<JsonObject> incrby(String key, int increment) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(String.valueOf(increment));
        return sendCommand(new RedisCommand(RedisCommandType.INCRBY, arguments));
    }

    public Future<JsonObject> incrbyfloat(String key, double increment) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(String.valueOf(increment));
        return sendCommand(new RedisCommand(RedisCommandType.INCRBYFLOAT, arguments));
    }

    public Future<JsonObject> keys(String pattern) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(pattern);
        return sendCommand(new RedisCommand(RedisCommandType.KEYS, arguments));
    }

    public Future<JsonObject> lindex(String key, int index) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(String.valueOf(index));
        return sendCommand(new RedisCommand(RedisCommandType.LINDEX, arguments));
    }

    public Future<JsonObject> linsertbefore(String key, String pivot, String value) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add("BEFORE");
        arguments.add(pivot);
        arguments.add(value);
        return sendCommand(new RedisCommand(RedisCommandType.LINSERT, arguments));
    }

    public Future<JsonObject> linsertafter(String key, String pivot, String value) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add("AFTER");
        arguments.add(pivot);
        arguments.add(value);
        return sendCommand(new RedisCommand(RedisCommandType.LINSERT, arguments));
    }

    public Future<JsonObject> llen(String key) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        return sendCommand(new RedisCommand(RedisCommandType.LLEN, arguments));
    }

    public Future<JsonObject> lpop(String key) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        return sendCommand(new RedisCommand(RedisCommandType.LPOP, arguments));
    }

    public Future<JsonObject> lpush(String key, String value) {
        return lpush(key, Arrays.asList(new String[]{value}));
    }

    public Future<JsonObject> lpush(String key, List<String> values) {
        if (values == null || values.size() == 0) {
            throw new IllegalArgumentException("No values provided for LPUSH");
        }

        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);

        for (String value : values) {
            arguments.add(value);
        }
        return sendCommand(new RedisCommand(RedisCommandType.LPUSH, arguments));
    }

    public Future<JsonObject> lpushx(String key, String value) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(value);
        return sendCommand(new RedisCommand(RedisCommandType.LPUSHX, arguments));
    }

    public Future<JsonObject> lrange(String key, int start, int stop) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(String.valueOf(start));
        arguments.add(String.valueOf(stop));
        return sendCommand(new RedisCommand(RedisCommandType.LRANGE, arguments));
    }

    public Future<JsonObject> lrem(String key, int count, String value) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(String.valueOf(count));
        arguments.add(value);
        return sendCommand(new RedisCommand(RedisCommandType.LREM, arguments));
    }

    public Future<JsonObject> lset(String key, int index, String value) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(String.valueOf(index));
        arguments.add(value);
        return sendCommand(new RedisCommand(RedisCommandType.LSET, arguments));
    }

    public Future<JsonObject> ltrim(String key, int start, int stop) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(String.valueOf(start));
        arguments.add(String.valueOf(stop));
        return sendCommand(new RedisCommand(RedisCommandType.LTRIM, arguments));
    }

    public Future<JsonObject> mget(String key) {
        return mget(Arrays.asList(new String[]{key}));
    }

    public Future<JsonObject> mget(List<String> keys) {
        if (keys == null || keys.size() == 0) {
            throw new IllegalArgumentException("No keys provided for MGET");
        }

        ArrayList<String> arguments = new ArrayList<>();

        for (String key : keys) {
            arguments.add(key);
        }
        return sendCommand(new RedisCommand(RedisCommandType.MGET, arguments));
    }

    public Future<JsonObject> mset(Map<String, String> keyPairs) {
        if (keyPairs == null || keyPairs.size() == 0) {
            throw new IllegalArgumentException("The key/values are missing or mismatched");
        }

        ArrayList<String> arguments = new ArrayList<>();

        for (Map.Entry<String, String> keyPair : keyPairs.entrySet()) {
            arguments.add(keyPair.getKey());
            arguments.add(keyPair.getValue());
        }
        return sendCommand(new RedisCommand(RedisCommandType.MSET, arguments));
    }

    public Future<JsonObject> msetnx(Map<String, String> keyPairs) {
        if (keyPairs == null || keyPairs.size() == 0) {
            throw new IllegalArgumentException("The key/values are missing or mismatched");
        }

        ArrayList<String> arguments = new ArrayList<>();

        for (Map.Entry<String, String> keyPair : keyPairs.entrySet()) {
            arguments.add(keyPair.getKey());
            arguments.add(keyPair.getValue());
        }
        return sendCommand(new RedisCommand(RedisCommandType.MSETNX, arguments));
    }

    public Future<JsonObject> persist(String key) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        return sendCommand(new RedisCommand(RedisCommandType.PERSIST, arguments));
    }

    public Future<JsonObject> pexpire(String key, long milliseconds) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(String.valueOf(milliseconds));
        return sendCommand(new RedisCommand(RedisCommandType.PEXPIRE, arguments));
    }

    public Future<JsonObject> pexpireat(String key, long unixTimestamp) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(String.valueOf(unixTimestamp));
        return sendCommand(new RedisCommand(RedisCommandType.PEXPIREAT, arguments));
    }

    public Future<JsonObject> ping() {
        ArrayList<String> arguments = new ArrayList<>();
        return sendCommand(new RedisCommand(RedisCommandType.PING, arguments));
    }

    public Future<JsonObject> psetex(String key, int expiration, String value) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(String.valueOf(expiration));
        arguments.add(value);
        return sendCommand(new RedisCommand(RedisCommandType.PSETEX, arguments));
    }

    public Future<JsonObject> pttl(String key) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        return sendCommand(new RedisCommand(RedisCommandType.PTTL, arguments));
    }

    public Future<JsonObject> randomkey() {
        ArrayList<String> arguments = new ArrayList<>();
        return sendCommand(new RedisCommand(RedisCommandType.RANDOMKEY, arguments));
    }

    public Future<JsonObject> rename(String key, String newkey) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(newkey);
        return sendCommand(new RedisCommand(RedisCommandType.RENAME, arguments));
    }

    public Future<JsonObject> renamenx(String key, String newkey) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(newkey);
        return sendCommand(new RedisCommand(RedisCommandType.RENAMENX, arguments));
    }

    public Future<JsonObject> rpop(String key) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        return sendCommand(new RedisCommand(RedisCommandType.RPOP, arguments));
    }

    public Future<JsonObject> rpoplpush(String source, String destination) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(source);
        arguments.add(destination);
        return sendCommand(new RedisCommand(RedisCommandType.RPOPLPUSH, arguments));
    }

    public Future<JsonObject> rpush(String key, String value) {
        return rpush(key, Arrays.asList(new String[]{value}));
    }

    public Future<JsonObject> rpush(String key, List<String> values) {
        if (values == null || values.size() == 0) {
            throw new IllegalArgumentException("No values provided for RPUSH");
        }

        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);

        for (String value : values) {
            arguments.add(value);
        }
        return sendCommand(new RedisCommand(RedisCommandType.RPUSH, arguments));
    }

    public Future<JsonObject> rpushx(String key, String value) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(value);
        return sendCommand(new RedisCommand(RedisCommandType.RPUSHX, arguments));
    }

    public Future<JsonObject> sadd(String key, String member) {
        return sadd(key, Arrays.asList(new String[]{member}));
    }

    public Future<JsonObject> sadd(String key, List<String> members) {
        if (members == null || members.size() == 0) {
            throw new IllegalArgumentException("No members provided for SADD");
        }

        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);

        for (String member : members) {
            arguments.add(member);
        }
        return sendCommand(new RedisCommand(RedisCommandType.SADD, arguments));
    }

    public Future<JsonObject> scard(String key) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        return sendCommand(new RedisCommand(RedisCommandType.SCARD, arguments));
    }

    public Future<JsonObject> sdiff(List<String> diffKeys) {
        if (diffKeys == null || diffKeys.size() == 0) {
            throw new IllegalArgumentException("No diff keys provided for SDIFF");
        }

        ArrayList<String> arguments = new ArrayList<>();

        for (String diffKey : diffKeys) {
            arguments.add(diffKey);
        }
        return sendCommand(new RedisCommand(RedisCommandType.SDIFF, arguments));
    }

    public Future<JsonObject> sdiffstore(String destination, List<String> diffKeys) {
        if (diffKeys == null || diffKeys.size() == 0) {
            throw new IllegalArgumentException("No diff keys provided for SDIFFSTORE");
        }

        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(destination);

        for (String diffKey : diffKeys) {
            arguments.add(diffKey);
        }
        return sendCommand(new RedisCommand(RedisCommandType.SDIFFSTORE, arguments));
    }

    public Future<JsonObject> set(String key, String value) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(value);
        return sendCommand(new RedisCommand(RedisCommandType.SET, arguments));
    }

    public Future<JsonObject> setex(String key, int expiration, String value) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(String.valueOf(expiration));
        arguments.add(value);
        return sendCommand(new RedisCommand(RedisCommandType.SETEX, arguments));
    }

    public Future<JsonObject> setnx(String key, String value) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(value);
        return sendCommand(new RedisCommand(RedisCommandType.SETNX, arguments));
    }

    public Future<JsonObject> setexnx(String key, int expiration, String value) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(value);
        arguments.add("NX");
        arguments.add("EX");
        arguments.add(String.valueOf(expiration));
        return sendCommand(new RedisCommand(RedisCommandType.SET, arguments));
    }

    public Future<JsonObject> setrange(String key, long offset, String value) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(String.valueOf(offset));
        arguments.add(value);
        return sendCommand(new RedisCommand(RedisCommandType.SETRANGE, arguments));
    }

    public Future<JsonObject> sinter(List<String> intersectKeys) {
        if (intersectKeys == null || intersectKeys.size() == 0) {
            throw new IllegalArgumentException("No intersect keys defined for SINTER");
        }

        ArrayList<String> arguments = new ArrayList<>();

        for (String intersectKey : intersectKeys) {
            arguments.add(intersectKey);
        }
        return sendCommand(new RedisCommand(RedisCommandType.SINTER, arguments));
    }

    public Future<JsonObject> sinterstore(String destination, List<String> intersectKeys) {
        if (intersectKeys == null || intersectKeys.size() == 0) {
            throw new IllegalArgumentException("No intersect keys defined for SINTERSTORE");
        }

        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(destination);

        for (String intersectKey : intersectKeys) {
            arguments.add(intersectKey);
        }
        return sendCommand(new RedisCommand(RedisCommandType.SINTERSTORE, arguments));
    }

    public Future<JsonObject> sismember(String key, String member) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(member);
        return sendCommand(new RedisCommand(RedisCommandType.SISMEMBER, arguments));
    }

    public Future<JsonObject> smembers(String key) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        return sendCommand(new RedisCommand(RedisCommandType.SMEMBERS, arguments));
    }

    public Future<JsonObject> smove(String source, String destination, String member) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(source);
        arguments.add(destination);
        arguments.add(member);
        return sendCommand(new RedisCommand(RedisCommandType.SMOVE, arguments));
    }

    public Future<JsonObject> sort(String key) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        return sendCommand(new RedisCommand(RedisCommandType.SORT, arguments));
    }

    public Future<JsonObject> sort(String key, int offset, int limit) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add("LIMIT");
        arguments.add(String.valueOf(offset));
        arguments.add(String.valueOf(limit));
        return sendCommand(new RedisCommand(RedisCommandType.SORT, arguments));
    }

    public Future<JsonObject> sort(String key, String sortOrder) {
        if (sortOrder == null) {
            sort(key);
        } else if (!SORT_ORDER.contains(sortOrder)) {
            throw new IllegalArgumentException("Invalid sort order: " + sortOrder);
        }

        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(sortOrder);
        return sendCommand(new RedisCommand(RedisCommandType.SORT, arguments));
    }

    public Future<JsonObject> sort(String key, String sortOrder, int offset, int limit) {
        if (sortOrder == null) {
            sort(key, offset, limit);
        } else if (!SORT_ORDER.contains(sortOrder)) {
            throw new IllegalArgumentException("Invalid sort order: " + sortOrder);
        }

        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(sortOrder);
        arguments.add("LIMIT");
        arguments.add(String.valueOf(offset));
        arguments.add(String.valueOf(limit));
        return sendCommand(new RedisCommand(RedisCommandType.SORT, arguments));
    }

    public Future<JsonObject> spop(String key) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        return sendCommand(new RedisCommand(RedisCommandType.SPOP, arguments));
    }

    public Future<JsonObject> srandmember(String key) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        return sendCommand(new RedisCommand(RedisCommandType.SRANDMEMBER, arguments));
    }

    public Future<JsonObject> srandmember(String key, int count) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(String.valueOf(count));
        return sendCommand(new RedisCommand(RedisCommandType.SRANDMEMBER, arguments));
    }

    public Future<JsonObject> srem(String key, String member) {
        return srem(key, Arrays.asList(new String[]{member}));
    }

    public Future<JsonObject> srem(String key, List<String> members) {
        if (members == null || members.size() == 0) {
            throw new IllegalArgumentException("No members provided for SREM");
        }

        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);

        for (String member : members) {
            arguments.add(member);
        }
        return sendCommand(new RedisCommand(RedisCommandType.SREM, arguments));
    }

    public Future<JsonObject> strlen(String key) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        return sendCommand(new RedisCommand(RedisCommandType.STRLEN, arguments));
    }

    public Future<JsonObject> sunion(List<String> unionKeys) {
        if (unionKeys == null || unionKeys.size() == 0) {
            throw new IllegalArgumentException("No union keys provided for SUNION");
        }

        ArrayList<String> arguments = new ArrayList<>();

        for (String unionKey : unionKeys) {
            arguments.add(unionKey);
        }
        return sendCommand(new RedisCommand(RedisCommandType.SUNION, arguments));
    }

    public Future<JsonObject> sunionstore(String destination, List<String> unionKeys) {
        if (unionKeys == null || unionKeys.size() == 0) {
            throw new IllegalArgumentException("No union keys provided for SUNIONSTORE");
        }

        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(destination);

        for (String unionKey : unionKeys) {
            arguments.add(unionKey);
        }
        return sendCommand(new RedisCommand(RedisCommandType.SUNIONSTORE, arguments));
    }

    public Future<JsonObject> ttl(String key) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        return sendCommand(new RedisCommand(RedisCommandType.TTL, arguments));
    }

    public Future<JsonObject> type(String key) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        return sendCommand(new RedisCommand(RedisCommandType.TYPE, arguments));
    }

    public Future<JsonObject> zadd(String key, double score, String member) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(String.valueOf(score));
        arguments.add(member);
        return sendCommand(new RedisCommand(RedisCommandType.ZADD, arguments));
    }

    public Future<JsonObject> zadd(String key, Map<Double, String> members) {
        if (members == null || members.size() == 0) {
            throw new IllegalArgumentException("No members or scores provided");
        }

        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);

        for (Map.Entry<Double, String> member : members.entrySet()) {
            arguments.add(String.valueOf(member.getKey()));
            arguments.add(member.getValue());
        }
        return sendCommand(new RedisCommand(RedisCommandType.ZADD, arguments));
    }

    public Future<JsonObject> zcard(String key) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        return sendCommand(new RedisCommand(RedisCommandType.ZCARD, arguments));
    }

    public Future<JsonObject> zcount(String key, double min, double max) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(String.valueOf(min));
        arguments.add(String.valueOf(max));
        return sendCommand(new RedisCommand(RedisCommandType.ZCOUNT, arguments));
    }

    public Future<JsonObject> zincrby(String key, double increment, String member) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(String.valueOf(increment));
        arguments.add(member);
        return sendCommand(new RedisCommand(RedisCommandType.ZINCRBY, arguments));
    }

    public Future<JsonObject> zinterstore(String destination, int keyCount, List<String> intersectKeys) {
        if (intersectKeys == null || intersectKeys.size() == 0) {
            throw new IllegalArgumentException("No intersect keys provided for ZINTERSTORE");
        }

        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(destination);
        arguments.add(String.valueOf(keyCount));

        for (String intersectKey : intersectKeys) {
            arguments.add(intersectKey);
        }
        return sendCommand(new RedisCommand(RedisCommandType.ZINTERSTORE, arguments));
    }

    public Future<JsonObject> zrange(String key, int start, int stop) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(String.valueOf(start));
        arguments.add(String.valueOf(stop));
        return sendCommand(new RedisCommand(RedisCommandType.ZRANGE, arguments));
    }

    public Future<JsonObject> zrangewithscores(String key, int start, int stop) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(String.valueOf(start));
        arguments.add(String.valueOf(stop));
        arguments.add("WITHSCORES");
        return sendCommand(new RedisCommand(RedisCommandType.ZRANGE, arguments));
    }

    public Future<JsonObject> zrangebyscore(String key, double min, double max) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(String.valueOf(min));
        arguments.add(String.valueOf(max));
        return sendCommand(new RedisCommand(RedisCommandType.ZRANGEBYSCORE, arguments));
    }

    public Future<JsonObject> zrangebyscore(String key, double min, double max, int offset, int limit) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(String.valueOf(min));
        arguments.add(String.valueOf(max));
        arguments.add("LIMIT");
        arguments.add(String.valueOf(offset));
        arguments.add(String.valueOf(limit));
        return sendCommand(new RedisCommand(RedisCommandType.ZRANGEBYSCORE, arguments));
    }

    public Future<JsonObject> zrangebyscorewithscores(String key, double min, double max) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(String.valueOf(min));
        arguments.add(String.valueOf(max));
        arguments.add("WITHSCORES");
        return sendCommand(new RedisCommand(RedisCommandType.ZRANGEBYSCORE, arguments));
    }

    public Future<JsonObject> zrangebyscorewithscores(String key, double min, double max, int offset, int limit) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(String.valueOf(min));
        arguments.add(String.valueOf(max));
        arguments.add("WITHSCORES");
        arguments.add("LIMIT");
        arguments.add(String.valueOf(offset));
        arguments.add(String.valueOf(limit));
        return sendCommand(new RedisCommand(RedisCommandType.ZRANGEBYSCORE, arguments));
    }

    public Future<JsonObject> zrank(String key, String message) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(message);
        return sendCommand(new RedisCommand(RedisCommandType.ZRANK, arguments));
    }

    public Future<JsonObject> zrem(String key, String message) {
        return zrem(key, Arrays.asList(new String[]{message}));
    }

    public Future<JsonObject> zrem(String key, List<String> messages) {
        if (messages == null || messages.size() == 0) {
            throw new IllegalArgumentException("No messages provided for ZREM");
        }

        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);

        for (String message : messages) {
            arguments.add(message);
        }
        return sendCommand(new RedisCommand(RedisCommandType.ZREM, arguments));
    }

    public Future<JsonObject> zremrangebyrank(String key, long start, long stop) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(String.valueOf(start));
        arguments.add(String.valueOf(stop));
        return sendCommand(new RedisCommand(RedisCommandType.ZREMRANGEBYRANK, arguments));
    }

    public Future<JsonObject> zremrangebyscore(String key, double min, double max) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(String.valueOf(min));
        arguments.add(String.valueOf(max));
        return sendCommand(new RedisCommand(RedisCommandType.ZREMRANGEBYSCORE, arguments));
    }

    public Future<JsonObject> zrevrange(String key, int start, int stop) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(String.valueOf(start));
        arguments.add(String.valueOf(stop));
        return sendCommand(new RedisCommand(RedisCommandType.ZREVRANGE, arguments));
    }

    public Future<JsonObject> zrevrangewithscores(String key, int start, int stop) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(String.valueOf(start));
        arguments.add(String.valueOf(stop));
        arguments.add("WITHSCORES");
        return sendCommand(new RedisCommand(RedisCommandType.ZREVRANGE, arguments));
    }

    public Future<JsonObject> zrevrangebyscore(String key, double max, double min) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(String.valueOf(max));
        arguments.add(String.valueOf(min));
        return sendCommand(new RedisCommand(RedisCommandType.ZREVRANGEBYSCORE, arguments));
    }

    public Future<JsonObject> zrevrangebyscorewithscores(String key, double max, double min) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(String.valueOf(max));
        arguments.add(String.valueOf(min));
        arguments.add("WITHSCORES");
        return sendCommand(new RedisCommand(RedisCommandType.ZREVRANGEBYSCORE, arguments));
    }

    public Future<JsonObject> zrevrangebyscore(String key, double max, double min, int offset, int limit) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(String.valueOf(max));
        arguments.add(String.valueOf(min));
        arguments.add("LIMIT");
        arguments.add(String.valueOf(offset));
        arguments.add(String.valueOf(limit));
        return sendCommand(new RedisCommand(RedisCommandType.ZREVRANGEBYSCORE, arguments));
    }

    public Future<JsonObject> zrevrangebyscorewithscores(String key, double max, double min, int offset, int limit) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(String.valueOf(max));
        arguments.add(String.valueOf(min));
        arguments.add("WITHSCORES");
        arguments.add("LIMIT");
        arguments.add(String.valueOf(offset));
        arguments.add(String.valueOf(limit));
        return sendCommand(new RedisCommand(RedisCommandType.ZREVRANGEBYSCORE, arguments));
    }

    public Future<JsonObject> zrevrank(String key, String member) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(member);
        return sendCommand(new RedisCommand(RedisCommandType.ZREVRANK, arguments));
    }

    public Future<JsonObject> zscore(String key, String member) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(member);
        return sendCommand(new RedisCommand(RedisCommandType.ZSCORE, arguments));
    }

    public Future<JsonObject> zunionstore(String destination, int numKeys, List<String> unionKeys) {
        if (unionKeys == null || unionKeys.size() == 0) {
            throw new IllegalArgumentException("No union keys provided for ZUNIONSTORE");
        }

        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(destination);
        arguments.add(String.valueOf(numKeys));

        for (String unionKey : unionKeys) {
            arguments.add(unionKey);
        }
        return sendCommand(new RedisCommand(RedisCommandType.ZUNIONSTORE, arguments));
    }

    protected abstract Future<JsonObject> sendCommand(RedisCommand command);
}

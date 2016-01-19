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

import java.util.List;
import java.util.Map;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;


/**
 * Interface for basic redis commands supported (excluding transaction related commands).
 *
 * @author Namrata Lele (nlele at groupon dot com)
 * @since 1.0.0
 */
public interface RedisBaseCommandClient {
    /**
     * If key already exists and is a string, this command appends the value at the end of the string. If key does not
     * exist it is created and set as an empty string, so APPEND will be similar to SET in this special case.
     *
     * @param key - String key
     * @param value - String value
     * @return Future result
     */
    Future<JsonObject> append(String key, String value);

    /**
     * Count the number of set bits (population counting) in a string.
     *
     * @param key - String key
     * @return Future result
     */
    Future<JsonObject> bitcount(String key);

    /**
     * Count the number of set bits (population counting) in a string using the start and end.
     *
     * @param key - String key
     * @param start - Integer start index
     * @param end - Integer end index
     * @return Future result
     */
    Future<JsonObject> bitcount(String key, int start, int end);

    /**
     * Perform a bitwise operation between multiple keys (containing string values) and store the result in the
     * destination key.  The BITOP command supports four bitwise operations: AND, OR, XOR and NOT.
     *
     * @param operation - String operation
     * @param destKey - String destination key
     * @param keys - List of keys
     * @return Future result
     */
    Future<JsonObject> bitop(String operation, String destKey, List<String> keys);

    /**
     * BLPOP is the blocking version of LPOP.
     *
     * @param timeout - Integer timeout
     * @param keys - List of keys
     * @return Future result
     */
    Future<JsonObject> blpop(int timeout, List<String> keys);

    /**
     * BRPOP is the blocking version of RPOP.
     *
     * @param timeout - Integer timeout
     * @param keys - List of keys
     * @return Future result
     */
    Future<JsonObject> brpop(int timeout, List<String> keys);

    /**
     * BRPOPLPUSH is the blocking variant of RPOPLPUSH.
     *
     * @param source - String source
     * @param destination - String destination
     * @param timeout - Integer timeout
     * @return Future result
     */
    Future<JsonObject> brpoplpush(String source, String destination, int timeout);

    /**
     * Decrements the number stored at key by one. If the key does not exist, it is set to 0 before performing the
     * operation. An error is returned if the key contains a value of the wrong type or contains a string that can
     * not be represented as integer. This operation is limited to 64 bit signed integers.
     * <br>
     * See INCR for extra information on increment/decrement operations.
     *
     * @param key - String key
     * @return Future result
     */
    Future<JsonObject> decr(String key);

    /**
     * Decrements the number stored at key by decrement. If the key does not exist, it is set to 0 before performing
     * the operation. An error is returned if the key contains a value of the wrong type or contains a string that can
     * not be represented as integer. This operation is limited to 64 bit signed integers.
     * <br>
     * See INCR for extra information on increment/decrement operations.
     *
     * @param key - String key
     * @param decrement - Integer decrement
     * @return Future result
     */
    Future<JsonObject> decrby(String key, int decrement);

    /**
     * Removes the specified key. The key is ignored if it does not exist.
     *
     * @param key - String key
     * @return Future result
     */
    Future<JsonObject> del(String key);

    /**
     * Removes the specified keys. A key is ignored if it does not exist.
     *
     * @param keys - String key
     * @return Future result
     */
    Future<JsonObject> del(List<String> keys);

    /**
     * Returns a message.
     *
     * @param key - String key
     * @return Future result
     */
    Future<JsonObject> echo(String key);

    /**
     * Returns if key exists.
     *
     * @param key - String key
     * @return Future result
     */
    Future<JsonObject> exists(String key);

    /**
     * Set a timeout on key.  After the timeout has expired, the key will automatically be deleted.
     *
     * @param key - String key
     * @param seconds - Integer seconds
     * @return Future result
     */
    Future<JsonObject> expire(String key, int seconds);

    /**
     * EXPIREAT has the same effect and semantic as EXPIRE, but instead of specifying the number of seconds
     * representing the TTL (time to live), it takes an absolute Unix timestamp (seconds since January 1, 1970).
     *
     * @param key - String key
     * @param unixTimestamp - Long unix timestamp
     * @return Future result
     */
    Future<JsonObject> expireat(String key, long unixTimestamp);

    /**
     * Delete all the keys of all the existing databases.
     * @return Future result
     */
    Future<JsonObject> flushall();

    /**
     * Delete all the keys of the currently selected DB.
     * @return Future result
     */
    Future<JsonObject> flushdb();

    /**
     * Get the value of key.
     *
     * @param key - String key
     * @return Future result
     */
    Future<JsonObject> get(String key);

    /**
     * Returns the bit value at offset in the string value stored at key.  When offset is beyond the string length,
     * the string is assumed to be a contiguous space with 0 bits. When key does not exist it is assumed to be an
     * empty string, so offset is always out of range and the value is also assumed to be a contiguous space with
     * 0 bits.
     *
     * @param key - String key
     * @param offset - Integer offset
     * @return Future result
     */
    Future<JsonObject> getbit(String key, int offset);

    /**
     * Returns the substring of the string value stored at key, determined by the offsets start and end (both are
     * inclusive).
     *
     * @param key - String key
     * @param start - Long start index
     * @param end - Long end index
     * @return Future result
     */
    Future<JsonObject> getrange(String key, long start, long end);

    /**
     * Atomically sets key to value and returns the old value stored at key. Returns an error when key exists but
     * does not hold a string value.
     *
     * @param key - String key
     * @param value - String value
     * @return Future result
     */
    Future<JsonObject> getset(String key, String value);

    /**
     * Removes the specified field from the hash stored at key.  If the specified field does not exist within this hash
     * it is ignored.  If key does not exist, it is treated as an empty hash and this command returns 0.
     *
     * @param key - String key
     * @param field - String field
     * @return Future result
     */
    Future<JsonObject> hdel(String key, String field);

    /**
     * Removes the specified fields from the hash stored at key.  Specified fields that do not exist within this hash
     * are ignored.  If key does not exist, it is treated as an empty hash and this command returns 0.
     *
     * @param key - String key
     * @param fields - List of fields
     * @return Future result
     */
    Future<JsonObject> hdel(String key, List<String> fields);

    /**
     * Returns if field is an existing field in the hash stored at key.
     *
     * @param key - String key
     * @param field - String field
     * @return Future result
     */
    Future<JsonObject> hexists(String key, String field);

    /**
     * Returns the value associated with field in the hash stored at key.
     *
     * @param key - String key
     * @param field - String field
     * @return Future result
     */
    Future<JsonObject> hget(String key, String field);

    /**
     * Returns all fields and values of the hash stored at key.
     *
     * @param key - String key
     * @return Future result
     */
    Future<JsonObject> hgetall(String key);

    /**
     * Increments the number stored at field in the hash stored at key by increment.
     *
     * @param key - String key
     * @param field - String field
     * @param increment - Integer increment
     * @return Future result
     */
    Future<JsonObject> hincrby(String key, String field, int increment);

    /**
     * Increment the specified field of a hash stored at key, and representing a floating point number, by the
     * specified increment.
     * <br>
     * The exact behavior of this command is identical to the one of the INCRBYFLOAT command, please refer to
     * the documentation of INCRBYFLOAT for further information.
     *
     * @param key - String key
     * @param field - String field
     * @param increment - Integer increment
     * @return Future result
     */
    Future<JsonObject> hincrbyfloat(String key, String field, double increment);

    /**
     * Returns all field names in the hash stored at key.
     *
     * @param key - String key
     * @return Future result
     */
    Future<JsonObject> hkeys(String key);

    /**
     * Returns the number of fields contained in the hash stored at key.
     *
     * @param key - String key
     * @return Future result
     */
    Future<JsonObject> hlen(String key);

    /**
     * Returns the values associated with the specified field in the hash stored at key.
     *
     * @param key - String key
     * @param field - String field
     * @return Future result
     */
    Future<JsonObject> hmget(String key, String field);

    /**
     * Returns the values associated with the specified fields in the hash stored at key.
     *
     * @param key - String key
     * @param fields - List of fields
     * @return Future result
     */
    Future<JsonObject> hmget(String key, List<String> fields);

    /**
     * Sets the specified fields to their respective values in the hash stored at key.
     *
     * @param key - String key
     * @param fieldPairs - Map of field value pairs
     * @return Future result
     */
    Future<JsonObject> hmset(String key, Map<String, String> fieldPairs);

    /**
     * Sets field in the hash stored at key to value.
     *
     * @param key - String key
     * @param field - String field
     * @param value - String value
     * @return Future result
     */
    Future<JsonObject> hset(String key, String field, String value);

    /**
     * Sets field in the hash stored at key to value, only if field does not yet exist.
     *
     * @param key - String key
     * @param field - String field
     * @param value - String value
     * @return Future result
     */
    Future<JsonObject> hsetnx(String key, String field, String value);

    /**
     * Returns all values in the hash stored at key.
     *
     * @param key - String key
     * @return Future result
     */
    Future<JsonObject> hvals(String key);

    /**
     * Increments the number stored at key by one.
     *
     * @param key - String key
     * @return Future result
     */
    Future<JsonObject> incr(String key);

    /**
     * Increments the number stored at key by increment.
     * <br>
     * See INCR for extra information on increment/decrement operations.
     *
     * @param key - String key
     * @param increment - Integer increment
     * @return Future result
     */
    Future<JsonObject> incrby(String key, int increment);

    /**
     * Increment the string representing a floating point number stored at key by the specified increment.
     *
     * @param key - String key
     * @param increment - Integer increment
     * @return Future result
     */
    Future<JsonObject> incrbyfloat(String key, double increment);

    /**
     * Returns all keys matching pattern.  Use \ to escape special characters if you want to match them verbatim.
     *
     * @param pattern - String pattern
     * @return Future result
     */
    Future<JsonObject> keys(String pattern);

    /**
     * Returns the element at specified index in the list stored at key.
     *
     * @param key - String key
     * @param index - Integer index
     * @return Future result
     */
    Future<JsonObject> lindex(String key, int index);

    /**
     * Inserts value in the list stored at key before the reference value pivot.
     *
     * @param key - String key
     * @param pivot - String pivot
     * @param value - String value
     * @return Future result
     */
    Future<JsonObject> linsertbefore(String key, String pivot, String value);

    /**
     * Inserts value in the list stored at key after the reference value pivot.
     *
     * @param key - String key
     * @param pivot - String pivot
     * @param value - String value
     * @return Future result
     */
    Future<JsonObject> linsertafter(String key, String pivot, String value);

    /**
     * Returns the length of the list stored at key.
     *
     * @param key - String key
     * @return Future result
     */
    Future<JsonObject> llen(String key);

    /**
     * Removes and returns the first element of the list stored at key.
     *
     * @param key - String key
     * @return Future result
     */
    Future<JsonObject> lpop(String key);

    /**
     * Insert the specified value at the head of the list stored at key.
     *
     * @param key - String key
     * @param value - String value
     * @return Future result
     */
    Future<JsonObject> lpush(String key, String value);

    /**
     * Insert all the specified values at the head of the list stored at key.
     *
     * @param key - String key
     * @param values - List of values
     * @return Future result
     */
    Future<JsonObject> lpush(String key, List<String> values);

    /**
     * Inserts value at the head of the list stored at key, only if key already exists and holds a list.  In contrary
     * to LPUSH, no operation will be performed when key does not yet exist.
     *
     * @param key - String key
     * @param value - String value
     * @return Future result
     */
    Future<JsonObject> lpushx(String key, String value);

    /**
     * Returns the specified elements of the list stored at key.
     *
     * @param key - String key
     * @param start - Integer start index
     * @param stop - Integer stop index
     * @return Future result
     */
    Future<JsonObject> lrange(String key, int start, int stop);

    /**
     * Removes the first count occurrences of elements equal to value from the list stored at key.
     *
     * @param key - String key
     * @param count - Integer count
     * @param value - String value
     * @return Future result
     */
    Future<JsonObject> lrem(String key, int count, String value);

    /**
     * Sets the list element at index to value.  For more information on the index argument, see LINDEX.  An error
     * is returned for out of range indexes.
     *
     * @param key - String key
     * @param index - Integer index
     * @param value - String value
     * @return Future result
     */
    Future<JsonObject> lset(String key, int index, String value);

    /**
     * Trim an existing list so that it will contain only the specified range of elements specified.
     *
     * @param key - String key
     * @param start - Integer start index
     * @param stop - Integer stop index
     * @return Future result
     */
    Future<JsonObject> ltrim(String key, int start, int stop);

    /**
     * Returns the value of the specified key.
     *
     * @param key - String key
     * @return Future result
     */
    Future<JsonObject> mget(String key);

    /**
     * Returns the values of all specified keys.
     *
     * @param keys - List of keys
     * @return Future result
     */
    Future<JsonObject> mget(List<String> keys);

    /**
     * Sets the given keys to their respective values.
     *
     * @param keyPairs - Map of key value pairs
     * @return Future result
     */
    Future<JsonObject> mset(Map<String, String> keyPairs);

    /**
     * Sets the given keys to their respective values.
     *
     * @param keyPairs - Map of key value pairs
     * @return Future result
     */
    Future<JsonObject> msetnx(Map<String, String> keyPairs);

    /**
     * Remove the existing timeout on key, turning the key from volatile (a key with an expire set) to persistent
     * (a key that will never expire as no timeout is associated).
     *
     * @param key - String key
     * @return Future result
     */
    Future<JsonObject> persist(String key);

    /**
     * This command works exactly like EXPIRE but the time to live of the key is specified in milliseconds instead
     * of seconds.
     *
     * @param key - String key
     * @param milliseconds - Long milliseconds to expire
     * @return Future result
     */
    Future<JsonObject> pexpire(String key, long milliseconds);

    /**
     * PEXPIREAT has the same effect and semantic as EXPIREAT, but the Unix time at which the key will expire is
     * specified in milliseconds instead of seconds.
     *
     * @param key - String key
     * @param unixTimestamp - Long unix timestamp
     * @return Future result
     */
    Future<JsonObject> pexpireat(String key, long unixTimestamp);

    /**
     * Returns PONG. This command is often used to test if a connection is still alive, or to measure latency.
     * @return Future result
     */
    Future<JsonObject> ping();

    /**
     * PSETEX works exactly like SETEX with the sole difference that the expire time is specified in milliseconds
     * instead of seconds.
     *
     * @param key - String key
     * @param value - String value
     * @param expiration - Integer expiration
     * @return Future result
     */
    Future<JsonObject> psetex(String key, int expiration, String value);

    /**
     * Like TTL this command returns the remaining time to live of a key that has an expire set, with the sole
     * difference that TTL returns the amount of remaining time in seconds while PTTL returns it in milliseconds.
     *
     * @param key - String key
     * @return Future result
     */
    Future<JsonObject> pttl(String key);

    /**
     * Return a random key from the currently selected database.
     * @return Future result
     */
    Future<JsonObject> randomkey();

    /**
     * Renames key to newkey.
     *
     * @param key - String key
     * @param newkey - String newkey
     * @return Future result
     */
    Future<JsonObject> rename(String key, String newkey);

    /**
     * Renames key to newkey if newkey does not yet exist.
     *
     * @param key - String key
     * @param newkey - String newkey
     * @return Future result
     */
    Future<JsonObject> renamenx(String key, String newkey);

    /**
     * Removes and returns the last element of the list stored at key.
     *
     * @param key - String key
     * @return Future result
     */
    Future<JsonObject> rpop(String key);

    /**
     * Atomically returns and removes the last element (tail) of the list stored at source, and pushes the element at
     * the first element (head) of the list stored at destination.
     *
     * @param source - String source
     * @param destination - String destination
     * @return Future result
     */
    Future<JsonObject> rpoplpush(String source, String destination);

    /**
     * Insert the specified value at the tail of the list stored at key.
     *
     * @param key - String key
     * @param value - String value
     * @return Future result
     */
    Future<JsonObject> rpush(String key, String value);

    /**
     * Insert all the specified values at the tail of the list stored at key.
     *
     * @param key - String key
     * @param values - List of values
     * @return Future result
     */
    Future<JsonObject> rpush(String key, List<String> values);

    /**
     * Inserts value at the tail of the list stored at key, only if key already exists and holds a list. In contrary to
     * RPUSH, no operation will be performed when key does not yet exist.
     *
     * @param key - String key
     * @param value - String value
     * @return Future result
     */
    Future<JsonObject> rpushx(String key, String value);

    /**
     * Add the specified member to the set stored at key.
     *
     * @param key - String key
     * @param member - String member
     * @return Future result
     */
    Future<JsonObject> sadd(String key, String member);

    /**
     * Add the specified members to the set stored at key.
     *
     * @param key - String key
     * @param members - List of members
     * @return Future result
     */
    Future<JsonObject> sadd(String key, List<String> members);

    /**
     * Returns the set cardinality (number of elements) of the set stored at key.
     *
     * @param key - String key
     * @return Future result
     */
    Future<JsonObject> scard(String key);

    /**
     * Returns the members of the set resulting from the difference between the first set and all the successive sets.
     *
     * @param diffKeys - List of keys
     * @return Future result
     */
    Future<JsonObject> sdiff(List<String> diffKeys);

    /**
     * This command is equal to SDIFF, but instead of returning the resulting set, it is stored in destination.  If
     * destination already exists, it is overwritten.
     *
     * @param destination - String destination
     * @param diffKeys - List of keys
     * @return Future result
     */
    Future<JsonObject> sdiffstore(String destination, List<String> diffKeys);

    /**
     * Set key to hold the string value.  If key already holds a value, it is overwritten, regardless of its type.
     * Any previous time to live associated with the key is discarded on successful SET operation.
     *
     * @param key - String key
     * @param value - String value
     * @return Future result
     */
    Future<JsonObject> set(String key, String value);

    /**
     * Set key to hold the string value and set key to timeout after a given number of seconds.
     *
     * @param key - String key
     * @param expiration - Integer expiration
     * @param value - String value
     * @return Future result
     */
    Future<JsonObject> setex(String key, int expiration, String value);

    /**
     * Set key to hold string value if key does not exist.  In that case, it is equal to SET.  When key already holds
     * a value, no operation is performed.
     *
     * @param key - String key
     * @param value - String value
     * @return Future result
     */
    Future<JsonObject> setnx(String key, String value);

    /**
     * Set key to hold string value and set key to timeout if key does not exist.
     *
     * @param key - String key
     * @param expiration - Integer expiration
     * @param value - String value
     * @return Future result
     */
    Future<JsonObject> setexnx(String key, int expiration, String value);

    /**
     * Overwrites part of the string stored at key, starting at the specified offset, for the entire length of value.
     *
     * @param key - String key
     * @param offset - Intger offset
     * @param value - String value
     * @return Future result
     */
    Future<JsonObject> setrange(String key, long offset, String value);

    /**
     * Returns the members of the set resulting from the intersection of all the given sets.
     *
     * @param intersectKeys - List of keys
     * @return Future result
     */
    Future<JsonObject> sinter(List<String> intersectKeys);

    /**
     * This command is equal to SINTER, but instead of returning the resulting set, it is stored in destination.
     * If destination already exists, it is overwritten.
     *
     * @param destination - String destination
     * @param intersectKeys - List of keys
     * @return Future result
     */
    Future<JsonObject> sinterstore(String destination, List<String> intersectKeys);

    /**
     * Returns if member is a member of the set stored at key.
     *
     * @param key - String key
     * @param member - String member
     * @return Future result
     */
    Future<JsonObject> sismember(String key, String member);

    /**
     * Returns all the members of the set value stored at key.  This has the same effect as running SINTER with
     * one argument key.
     *
     * @param key - String key
     * @return Future result
     */
    Future<JsonObject> smembers(String key);

    /**
     * Move member from the set at source to the set at destination.  This operation is atomic. In every given
     * moment the element will appear to be a member of source or destination for other clients.
     *
     * @param source - String source
     * @param destination - String destination
     * @param member - String member
     * @return Future result
     */
    Future<JsonObject> smove(String source, String destination, String member);

    /**
     * Returns or stores the elements contained in the list, set or sorted set at key.  By default, sorting is
     * numeric and elements are compared by their value interpreted as double precision floating point number.
     *
     * @param key - String key
     * @return Future result
     */
    Future<JsonObject> sort(String key);

    /**
     * Returns or stores the elements contained in the list, set or sorted set at key.  By default, sorting is
     * numeric and elements are compared by their value interpreted as double precision floating point number.
     *
     * @param key - String key
     * @param offset - Integer offset
     * @param limit - Integer limit
     * @return Future result
     */
    Future<JsonObject> sort(String key, int offset, int limit);

    /**
     * Returns or stores the elements contained in the list, set or sorted set at key.
     *
     * @param key - String key
     * @param sortOrder - String sort order
     * @return Future result
     */
    Future<JsonObject> sort(String key, String sortOrder);

    /**
     * Returns or stores the elements contained in the list, set or sorted set at key.
     *
     * @param key - String key
     * @param sortOrder - String sort order
     * @param offset - Integer offset
     * @param limit - Integer limit
     * @return Future result
     */
    Future<JsonObject> sort(String key, String sortOrder, int offset, int limit);

    /**
     * Removes and returns a random element from the set value stored at key.
     *
     * @param key - String key
     * @return Future result
     */
    Future<JsonObject> spop(String key);

    /**
     * Return a random element from the set value stored at key.
     *
     * @param key - String key
     * @return Future result
     */
    Future<JsonObject> srandmember(String key);

    /**
     * Return an array of elements from the set value stored at key.
     *
     * @param key - String key
     * @param count - Integer count
     * @return Future result
     */
    Future<JsonObject> srandmember(String key, int count);

    /**
     * Remove the specified member from the set stored at key.
     *
     * @param key - String key
     * @param member - String member
     * @return Future result
     */
    Future<JsonObject> srem(String key, String member);

    /**
     * Remove the specified members from the set stored at key.
     *
     * @param key - String key
     * @param members - List of members
     * @return Future result
     */
    Future<JsonObject> srem(String key, List<String> members);

    /**
     * Returns the length of the string value stored at key.
     *
     * @param key - String key
     * @return Future result
     */
    Future<JsonObject> strlen(String key);

    /**
     * Returns the members of the set resulting from the union of all the given sets.
     *
     * @param unionKeys - List of keys
     * @return Future result
     */
    Future<JsonObject> sunion(List<String> unionKeys);

    /**
     * This command is equal to SUNION, but instead of returning the resulting set, it is stored in destination.
     *
     * @param destination - String destination
     * @param unionKeys - List of keys
     * @return Future result
     */
    Future<JsonObject> sunionstore(String destination, List<String> unionKeys);

    /**
     * Returns the remaining time to live of a key that has a timeout.
     *
     * @param key - String key
     * @return Future result
     */
    Future<JsonObject> ttl(String key);

    /**
     * Returns the string representation of the type of the value stored at key.
     *
     * @param key - String key
     * @return Future result
     */
    Future<JsonObject> type(String key);

    /**
     * Adds the specified member with the specified score to the sorted set stored at key.
     *
     * @param key - String key
     * @param score - Double score
     * @param member - String member
     * @return Future result
     */
    Future<JsonObject> zadd(String key, double score, String member);

    /**
     * Adds all the specified members with the specified scores to the sorted set stored at key.
     *
     * @param key - String key
     * @param members - List of members
     * @return Future result
     */
    Future<JsonObject> zadd(String key, Map<Double, String> members);

    /**
     * Returns the sorted set cardinality (number of elements) of the sorted set stored at key.
     *
     * @param key - String key
     * @return Future result
     */
    Future<JsonObject> zcard(String key);

    /**
     * Returns the number of elements in the sorted set at key with a score between min and max.
     *
     * @param key - String key
     * @param min - Double min score
     * @param max - Double max score
     * @return Future result
     */
    Future<JsonObject> zcount(String key, double min, double max);

    /**
     * Increments the score of member in the sorted set stored at key by increment.
     *
     * @param key - String key
     * @param increment - Integer increment
     * @param member - String member
     * @return Future result
     */
    Future<JsonObject> zincrby(String key, double increment, String member);

    /**
     * Computes the intersection of keyCount sorted sets given by the specified keys, and stores the result in destination.
     *
     * @param destination - String destination
     * @param keyCount - Integer key count
     * @param intersectKeys - List of keys
     * @return Future result
     */
    Future<JsonObject> zinterstore(String destination, int keyCount, List<String> intersectKeys);

    /**
     * Returns the specified range of elements in the sorted set stored at key.  The elements are considered to be
     * ordered from the lowest to the highest score.  Lexicographical order is used for elements with equal score.
     *
     * @param key - String key
     * @param start - Integer start index
     * @param stop - Integer stop index
     * @return Future result
     */
    Future<JsonObject> zrange(String key, int start, int stop);

    /**
     * Returns the specified range of elements with their score in the sorted set stored at key.  The elements are
     * considered to be ordered from the lowest to the highest score.  Lexicographical order is used for elements
     * with equal score.
     *
     * @param key - String key
     * @param start - Integer start index
     * @param stop - Integer stop index
     * @return Future result
     */
    Future<JsonObject> zrangewithscores(String key, int start, int stop);

    /**
     * Returns all the elements in the sorted set at key with a score between min and max (including elements with
     * score equal to min or max).  The elements are considered to be ordered from low to high scores.
     *
     * @param key - String key
     * @param min - Double min score
     * @param max - Double max score
     * @return Future result
     */
    Future<JsonObject> zrangebyscore(String key, double min, double max);

    /**
     * Returns all the elements in the sorted set at key with a score between min and max (including elements with
     * score equal to min or max).  The elements are considered to be ordered from low to high scores.
     *
     * @param key - String key
     * @param min - Double min score
     * @param max - Double max score
     * @param offset - Integer offset
     * @param limit - Integer limit
     * @return Future result
     */
    Future<JsonObject> zrangebyscore(String key, double min, double max, int offset, int limit);

    /**
     * Returns all the elements in the sorted set at key with a score between min and max (including elements with
     * score equal to min or max).  The elements are considered to be ordered from low to high scores.
     *
     * @param key - String key
     * @param min - Double min score
     * @param max - Double max score
     * @return Future result
     */
    Future<JsonObject> zrangebyscorewithscores(String key, double min, double max);

    /**
     * Returns all the elements in the sorted set at key with a score between min and max (including elements with
     * score equal to min or max).  The elements are considered to be ordered from low to high scores.
     *
     * @param key - String key
     * @param min - Double min score
     * @param max - Double max score
     * @param offset - Integer offset
     * @param limit - Integer limit
     * @return Future result
     */
    Future<JsonObject> zrangebyscorewithscores(String key, double min, double max, int offset, int limit);

    /**
     * Returns the rank of member in the sorted set stored at key, with the scores ordered from low to high.
     *
     * @param key - String key
     * @param message - String message
     * @return Future result
     */
    Future<JsonObject> zrank(String key, String message);

    /**
     * Removes the specified member from the sorted set stored at key. Non existing members are ignored.
     *
     * @param key - String key
     * @param message - String message
     * @return Future result
     */
    Future<JsonObject> zrem(String key, String message);

    /**
     * Removes the specified members from the sorted set stored at key. Non existing members are ignored.
     *
     * @param key - String key
     * @param messages - List of messages
     * @return Future result
     */
    Future<JsonObject> zrem(String key, List<String> messages);

    /**
     * Removes all elements in the sorted set stored at key with rank between start and stop.
     *
     * @param key - String key
     * @param start - Long start score
     * @param stop - Long stop score
     * @return Future result
     */
    Future<JsonObject> zremrangebyrank(String key, long start, long stop);

    /**
     * Removes all elements in the sorted set stored at key with a score between min and max (inclusive).
     *
     * @param key - String key
     * @param min - Double min score
     * @param max - Double max score
     * @return Future result
     */
    Future<JsonObject> zremrangebyscore(String key, double min, double max);

    /**
     * Returns the specified range of elements in the sorted set stored at key.  The elements are considered to
     * be ordered from the highest to the lowest score.
     *
     * @param key - String key
     * @param start - Integer start index
     * @param stop - Integer stop index
     * @return Future result
     */
    Future<JsonObject> zrevrange(String key, int start, int stop);

    /**
     * Returns the specified range of elements in the sorted set stored at key.  The elements are considered to
     * be ordered from the highest to the lowest score.
     *
     * @param key - String key
     * @param start - Integer start index
     * @param stop - Integer stop index
     * @return Future result
     */
    Future<JsonObject> zrevrangewithscores(String key, int start, int stop);

    /**
     * Returns all the elements in the sorted set at key with a score between max and min (including elements with
     * score equal to max or min). In contrary to the default ordering of sorted sets, for this command the elements
     * are considered to be ordered from high to low scores.
     *
     * @param key - String key
     * @param max - Double max score
     * @param min - Double min score
     * @return Future result
     */
    Future<JsonObject> zrevrangebyscore(String key, double max, double min);

    /**
     * Returns all the elements in the sorted set at key with a score between max and min (including elements with
     * score equal to max or min). In contrary to the default ordering of sorted sets, for this command the elements
     * are considered to be ordered from high to low scores.
     *
     * @param key - String key
     * @param max - Double max score
     * @param min - Double min score
     * @return Future result
     */
    Future<JsonObject> zrevrangebyscorewithscores(String key, double max, double min);

    /**
     * Returns all the elements in the sorted set at key with a score between max and min (including elements with
     * score equal to max or min). In contrary to the default ordering of sorted sets, for this command the elements
     * are considered to be ordered from high to low scores.
     *
     * @param key - String key
     * @param max - Double max score
     * @param min - Double min score
     * @param offset - Integer offset
     * @param limit - Integer limit
     * @return Future result
     */
    Future<JsonObject> zrevrangebyscore(String key, double max, double min, int offset, int limit);

    /**
     * Returns all the elements in the sorted set at key with a score between max and min (including elements with
     * score equal to max or min). In contrary to the default ordering of sorted sets, for this command the elements
     * are considered to be ordered from high to low scores.
     *
     * @param key - String key
     * @param max - Double max score
     * @param min - Double min score
     * @param offset - Integer offset
     * @param limit - Integer limit
     * @return Future result
     */
    Future<JsonObject> zrevrangebyscorewithscores(String key, double max, double min, int offset, int limit);

    /**
     * Returns the rank of member in the sorted set stored at key, with the scores ordered from high to low.
     *
     * @param key - String key
     * @param member - String member
     * @return Future result
     */
    Future<JsonObject> zrevrank(String key, String member);

    /**
     * Returns the score of member in the sorted set at key.  If member does not exist in the sorted set, or key
     * does not exist, nil is returned.
     *
     * @param key - String key
     * @param member - String member
     * @return Future result
     */
    Future<JsonObject> zscore(String key, String member);

    /**
     * Returns the score of member in the sorted set at key.  If member does not exist in the sorted set, or key
     * does not exist, nil is returned.
     *
     * @param destination - String destination
     * @param numKeys - Integer number of keys
     * @param unionKeys - List of keys
     * @return Future result
     */
    Future<JsonObject> zunionstore(String destination, int numKeys, List<String> unionKeys);
}

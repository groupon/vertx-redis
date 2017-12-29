/**
 * Copyright 2014 Groupon.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.groupon.vertx.redis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.core.IsEqual;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.hamcrest.MockitoHamcrest;

/**
 * Tests for <code>RedisCommandTransaction</code>.
 *
 * @author Namrata Lele (nlele at groupon dot com)
 * @since 1.0.1
 */
public class RedisCommandTransactionTest {
    private static final long TIMEOUT = 1000;
    @Mock
    private EventBus eventBus;

    @Mock
    private AsyncResult<Message<JsonObject>> asyncResult;

    @Mock
    private Message<JsonObject> message;

    @Mock
    private Handler<AsyncResult<JsonObject>> handler;

    @Captor
    private ArgumentCaptor<Handler<AsyncResult<Message<JsonObject>>>> getCaptor;

    @Captor
    private ArgumentCaptor<AsyncResult<JsonObject>> resultCaptor;

    private RedisCommandTransaction transaction;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        RedisCommandClient client = new RedisClient(eventBus, "address", TIMEOUT);
        transaction = client.multi();
    }

    @Test
    public void testAppend() {
        transaction.append("key", "value");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"APPEND\",\"arguments\":[\"key\",\"value\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"APPEND\",\"arguments\":[\"key\",\"value\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testBitcount() {
        transaction.bitcount("key");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"BITCOUNT\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"BITCOUNT\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testBitcountWithStartStop() {
        transaction.bitcount("key", 0, 1);
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"BITCOUNT\",\"arguments\":[\"key\",\"0\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
        transaction.exec();

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"BITCOUNT\",\"arguments\":[\"key\",\"0\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testBitop() {
        transaction.bitop("OR", "key", Collections.singletonList("key2"));
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"BITOP\",\"arguments\":[\"OR\",\"key\",\"key2\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"BITOP\",\"arguments\":[\"OR\",\"key\",\"key2\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testBlpopList() {
        transaction.blpop(1, Arrays.asList("key", "key2"));
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"BLPOP\",\"arguments\":[\"key\",\"key2\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"BLPOP\",\"arguments\":[\"key\",\"key2\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testBrpopList() {
        transaction.brpop(1, Arrays.asList("key", "key2"));
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"BRPOP\",\"arguments\":[\"key\",\"key2\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"BRPOP\",\"arguments\":[\"key\",\"key2\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testBrpoplpush() {
        transaction.brpoplpush("src", "dest", 1);
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"BRPOPLPUSH\",\"arguments\":[\"src\",\"dest\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"BRPOPLPUSH\",\"arguments\":[\"src\",\"dest\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testDecr() {
        transaction.decr("key");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"DECR\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"DECR\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testDecrby() {
        transaction.decrby("key", 1);
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"DECRBY\",\"arguments\":[\"key\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"DECRBY\",\"arguments\":[\"key\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testDel() {
        transaction.del("key");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"DEL\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"DEL\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testDelList() {
        transaction.del(Arrays.asList(new String[]{"key", "key2"}));
        verify(eventBus, never()).send("address", new JsonObject().put("commands", new JsonArray().add(new JsonObject("{\"command\":\"DEL\",\"arguments\":[\"key\",\"key2\"]}"))));
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"DEL\",\"arguments\":[\"key\",\"key2\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"DEL\",\"arguments\":[\"key\",\"key2\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testDiscard() {
        transaction.discard();
        verify(eventBus, never()).send(anyString(), any(JsonObject.class));
    }

    @Test
    public void testEcho() {
        transaction.echo("key");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ECHO\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ECHO\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testExec() {
        transaction.set("key1", "value1");
        transaction.get("key1");
        transaction.exec().setHandler(handler);

        ArrayList<String> input = new ArrayList<>();
        input.add("{\"command\":\"SET\",\"arguments\":[\"key1\",\"value1\"]}");
        input.add("{\"command\":\"GET\",\"arguments\":[\"key1\"]}");
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(input)), withTimeout(TIMEOUT), getCaptor.capture());

        JsonArray resultArray = new JsonArray();
        resultArray.add("OK");
        resultArray.add("value1");
        JsonObject body = new JsonObject().put("data", resultArray);
        when(message.body()).thenReturn(body);
        when(asyncResult.result()).thenReturn(message);
        when(asyncResult.failed()).thenReturn(false);

        getCaptor.getValue().handle(asyncResult);

        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(body, result.result());
    }

    @Test
    public void testExecTimedout() {
        transaction.set("key1", "value1");
        transaction.get("key1");
        transaction.exec().setHandler(handler);

        ArrayList<String> input = new ArrayList<>();
        input.add("{\"command\":\"SET\",\"arguments\":[\"key1\",\"value1\"]}");
        input.add("{\"command\":\"GET\",\"arguments\":[\"key1\"]}");
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(input)), withTimeout(TIMEOUT), getCaptor.capture());

        when(asyncResult.result()).thenReturn(message);
        when(asyncResult.failed()).thenReturn(true);

        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject().put("status", "error").put("code", HttpURLConnection.HTTP_GATEWAY_TIMEOUT).encode(), result.result().encode());
    }

    @Test
    public void testExists() {
        transaction.exists("key");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"EXISTS\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"EXISTS\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testExpire() {
        transaction.expire("key", 1);
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"EXPIRE\",\"arguments\":[\"key\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"EXPIRE\",\"arguments\":[\"key\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testExpireat() {
        transaction.expireat("key", 1234);
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"EXPIREAT\",\"arguments\":[\"key\",\"1234\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"EXPIREAT\",\"arguments\":[\"key\",\"1234\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testFlushall() {
        transaction.flushall();
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"FLUSHALL\",\"arguments\":[]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"FLUSHALL\",\"arguments\":[]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testFlushdb() {
        transaction.flushdb();
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"FLUSHDB\",\"arguments\":[]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"FLUSHDB\",\"arguments\":[]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testGet() {
        transaction.get("key");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"GET\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"GET\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testGetbit() {
        transaction.getbit("key", 0);
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"GETBIT\",\"arguments\":[\"key\",\"0\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"GETBIT\",\"arguments\":[\"key\",\"0\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testGetRange() {
        transaction.getrange("key", 0, 1);
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"GETRANGE\",\"arguments\":[\"key\",\"0\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"GETRANGE\",\"arguments\":[\"key\",\"0\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testGetset() {
        transaction.getset("key", "value");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"GETSET\",\"arguments\":[\"key\",\"value\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"GETSET\",\"arguments\":[\"key\",\"value\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testHdel() {
        transaction.hdel("key", "field");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"HDEL\",\"arguments\":[\"key\",\"field\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"HDEL\",\"arguments\":[\"key\",\"field\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testHdelList() {
        transaction.hdel("key", Arrays.asList(new String[]{"field", "field2"}));
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"HDEL\",\"arguments\":[\"key\",\"field\",\"field2\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"HDEL\",\"arguments\":[\"key\",\"field\",\"field2\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testHexists() {
        transaction.hexists("key", "field");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"HEXISTS\",\"arguments\":[\"key\",\"field\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"HEXISTS\",\"arguments\":[\"key\",\"field\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testHget() {
        transaction.hget("key", "field");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"HGET\",\"arguments\":[\"key\",\"field\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"HGET\",\"arguments\":[\"key\",\"field\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testHgetall() {
        transaction.hgetall("key");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"HGETALL\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"HGETALL\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testHincrby() {
        transaction.hincrby("key", "field", 1);
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"HINCRBY\",\"arguments\":[\"key\",\"field\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"HINCRBY\",\"arguments\":[\"key\",\"field\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testHincrbyfloat() {
        transaction.hincrbyfloat("key", "field", 1.1);
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"HINCRBYFLOAT\",\"arguments\":[\"key\",\"field\",\"1.1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"HINCRBYFLOAT\",\"arguments\":[\"key\",\"field\",\"1.1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testHkeys() {
        transaction.hkeys("key");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"HKEYS\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"HKEYS\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testHlen() {
        transaction.hlen("key");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"HLEN\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"HLEN\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testHmget() {
        transaction.hmget("key", "field");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"HMGET\",\"arguments\":[\"key\",\"field\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"HMGET\",\"arguments\":[\"key\",\"field\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testHmgetList() {
        transaction.hmget("key", Arrays.asList(new String[]{"field", "field2"}));
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"HMGET\",\"arguments\":[\"key\",\"field\",\"field2\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"HMGET\",\"arguments\":[\"key\",\"field\",\"field2\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testHmset() {
        HashMap<String, String> map = new HashMap<>();
        map.put("field", "value");

        transaction.hmset("key", map);
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"HMSET\",\"arguments\":[\"key\",\"field\",\"value\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"HMSET\",\"arguments\":[\"key\",\"field\",\"value\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testHset() {
        transaction.hset("key", "field", "value");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"HSET\",\"arguments\":[\"key\",\"field\",\"value\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"HSET\",\"arguments\":[\"key\",\"field\",\"value\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testHsetnx() {
        transaction.hsetnx("key", "field", "value");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"HSETNX\",\"arguments\":[\"key\",\"field\",\"value\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"HSETNX\",\"arguments\":[\"key\",\"field\",\"value\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testHvals() {
        transaction.hvals("key");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"HVALS\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"HVALS\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testIncr() {
        transaction.incr("key");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"INCR\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"INCR\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testIncrby() {
        transaction.incrby("key", 1);
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"INCRBY\",\"arguments\":[\"key\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"INCRBY\",\"arguments\":[\"key\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testIncrbyfloat() {
        transaction.incrbyfloat("key", 1.1);
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"INCRBYFLOAT\",\"arguments\":[\"key\",\"1.1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"INCRBYFLOAT\",\"arguments\":[\"key\",\"1.1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testKeys() {
        transaction.keys("pattern");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"KEYS\",\"arguments\":[\"pattern\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"KEYS\",\"arguments\":[\"pattern\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testLindex() {
        transaction.lindex("key", 0);
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"LINDEX\",\"arguments\":[\"key\",\"0\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"LINDEX\",\"arguments\":[\"key\",\"0\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testLinsertbefore() {
        transaction.linsertbefore("key", "pivot", "value");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"LINSERT\",\"arguments\":[\"key\",\"BEFORE\",\"pivot\",\"value\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"LINSERT\",\"arguments\":[\"key\",\"BEFORE\",\"pivot\",\"value\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testLinsertafter() {
        transaction.linsertafter("key", "pivot", "value");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"LINSERT\",\"arguments\":[\"key\",\"AFTER\",\"pivot\",\"value\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"LINSERT\",\"arguments\":[\"key\",\"AFTER\",\"pivot\",\"value\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testLlen() {
        transaction.llen("key");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"LLEN\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"LLEN\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testLpop() {
        transaction.lpop("key");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"LPOP\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"LPOP\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testLpush() {
        transaction.lpush("key", "value");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"LPUSH\",\"arguments\":[\"key\",\"value\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"LPUSH\",\"arguments\":[\"key\",\"value\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testLpushList() {
        transaction.lpush("key", Arrays.asList(new String[]{"value", "value2"}));
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"LPUSH\",\"arguments\":[\"key\",\"value\",\"value2\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"LPUSH\",\"arguments\":[\"key\",\"value\",\"value2\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testLpushx() {
        transaction.lpushx("key", "value");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"LPUSHX\",\"arguments\":[\"key\",\"value\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"LPUSHX\",\"arguments\":[\"key\",\"value\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testLrange() {
        transaction.lrange("key", 0, 1);
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"LRANGE\",\"arguments\":[\"key\",\"0\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"LRANGE\",\"arguments\":[\"key\",\"0\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testLrem() {
        transaction.lrem("key", 1, "value");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"LREM\",\"arguments\":[\"key\",\"1\",\"value\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"LREM\",\"arguments\":[\"key\",\"1\",\"value\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testLset() {
        transaction.lset("key", 0, "value");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"LSET\",\"arguments\":[\"key\",\"0\",\"value\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"LSET\",\"arguments\":[\"key\",\"0\",\"value\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testLtrim() {
        transaction.ltrim("key", 0, 1);
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"LTRIM\",\"arguments\":[\"key\",\"0\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"LTRIM\",\"arguments\":[\"key\",\"0\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testMget() {
        transaction.mget("key");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"MGET\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"MGET\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testMgetList() {
        transaction.mget(Arrays.asList(new String[]{"key", "key2"}));
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"MGET\",\"arguments\":[\"key\",\"key2\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"MGET\",\"arguments\":[\"key\",\"key2\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testMset() {
        HashMap<String, String> map = new HashMap<>();
        map.put("key", "value");

        transaction.mset(map);
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"MSET\",\"arguments\":[\"key\",\"value\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"MSET\",\"arguments\":[\"key\",\"value\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testMsetnx() {
        HashMap<String, String> map = new HashMap<>();
        map.put("key", "value");

        transaction.msetnx(map);
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"MSETNX\",\"arguments\":[\"key\",\"value\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"MSETNX\",\"arguments\":[\"key\",\"value\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testPersist() {
        transaction.persist("key");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"PERSIST\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"PERSIST\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testPexpire() {
        transaction.pexpire("key", 12345);
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"PEXPIRE\",\"arguments\":[\"key\",\"12345\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"PEXPIRE\",\"arguments\":[\"key\",\"12345\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testPexpireat() {
        transaction.pexpireat("key", 12345);
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"PEXPIREAT\",\"arguments\":[\"key\",\"12345\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"PEXPIREAT\",\"arguments\":[\"key\",\"12345\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testPing() {
        transaction.ping();
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"PING\",\"arguments\":[]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"PING\",\"arguments\":[]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testPsetex() {
        transaction.psetex("key", 12345, "value");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"PSETEX\",\"arguments\":[\"key\",\"12345\",\"value\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"PSETEX\",\"arguments\":[\"key\",\"12345\",\"value\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testPttl() {
        transaction.pttl("key");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"PTTL\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"PTTL\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testRandomkey() {
        transaction.randomkey();
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"RANDOMKEY\",\"arguments\":[]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"RANDOMKEY\",\"arguments\":[]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testRename() {
        transaction.rename("key", "newkey");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"RENAME\",\"arguments\":[\"key\",\"newkey\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"RENAME\",\"arguments\":[\"key\",\"newkey\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testRenamenx() {
        transaction.renamenx("key", "newkey");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"RENAMENX\",\"arguments\":[\"key\",\"newkey\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"RENAMENX\",\"arguments\":[\"key\",\"newkey\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testRpop() {
        transaction.rpop("key");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"RPOP\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"RPOP\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testRpoplpush() {
        transaction.rpoplpush("source", "destination");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"RPOPLPUSH\",\"arguments\":[\"source\",\"destination\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"RPOPLPUSH\",\"arguments\":[\"source\",\"destination\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testRpush() {
        transaction.rpush("key", "value");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"RPUSH\",\"arguments\":[\"key\",\"value\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"RPUSH\",\"arguments\":[\"key\",\"value\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testRpushList() {
        transaction.rpush("key", Arrays.asList(new String[]{"value", "value2"}));
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"RPUSH\",\"arguments\":[\"key\",\"value\",\"value2\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"RPUSH\",\"arguments\":[\"key\",\"value\",\"value2\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testRpushx() {
        transaction.rpushx("key", "value");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"RPUSHX\",\"arguments\":[\"key\",\"value\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"RPUSHX\",\"arguments\":[\"key\",\"value\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testSadd() {
        transaction.sadd("key", "member");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SADD\",\"arguments\":[\"key\",\"member\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SADD\",\"arguments\":[\"key\",\"member\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testSaddList() {
        transaction.sadd("key", Arrays.asList(new String[]{"member", "member2"}));
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SADD\",\"arguments\":[\"key\",\"member\",\"member2\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SADD\",\"arguments\":[\"key\",\"member\",\"member2\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testScard() {
        transaction.scard("key");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SCARD\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SCARD\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testSdiffList() {
        transaction.sdiff(Arrays.asList("diffKey", "diffKey2"));
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SDIFF\",\"arguments\":[\"diffKey\",\"diffKey2\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SDIFF\",\"arguments\":[\"diffKey\",\"diffKey2\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testSdiffstoreList() {
        transaction.sdiffstore("destination", Arrays.asList("diffKey", "diffKey2"));
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SDIFFSTORE\",\"arguments\":[\"destination\",\"diffKey\",\"diffKey2\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SDIFFSTORE\",\"arguments\":[\"destination\",\"diffKey\",\"diffKey2\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testSet() {
        transaction.set("key", "value");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SET\",\"arguments\":[\"key\",\"value\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SET\",\"arguments\":[\"key\",\"value\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testSetex() {
        transaction.setex("key", 12345, "value");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SETEX\",\"arguments\":[\"key\",\"12345\",\"value\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SETEX\",\"arguments\":[\"key\",\"12345\",\"value\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testSetnx() {
        transaction.setnx("key", "value");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SETNX\",\"arguments\":[\"key\",\"value\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SETNX\",\"arguments\":[\"key\",\"value\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testSetexnx() {
        transaction.setexnx("key", 12345, "value");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SET\",\"arguments\":[\"key\",\"value\",\"NX\",\"EX\",\"12345\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SET\",\"arguments\":[\"key\",\"value\",\"NX\",\"EX\",\"12345\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testSetrange() {
        transaction.setrange("key", 0, "value");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SETRANGE\",\"arguments\":[\"key\",\"0\",\"value\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SETRANGE\",\"arguments\":[\"key\",\"0\",\"value\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testSinterList() {
        transaction.sinter(Arrays.asList("intersectKey", "intersectKey2"));
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SINTER\",\"arguments\":[\"intersectKey\",\"intersectKey2\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SINTER\",\"arguments\":[\"intersectKey\",\"intersectKey2\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testSinterstoreList() {
        transaction.sinterstore("destination", Arrays.asList("intersectKey", "intersectKey2"));
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SINTERSTORE\",\"arguments\":[\"destination\",\"intersectKey\",\"intersectKey2\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SINTERSTORE\",\"arguments\":[\"destination\",\"intersectKey\",\"intersectKey2\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testSismember() {
        transaction.sismember("key", "member");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SISMEMBER\",\"arguments\":[\"key\",\"member\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SISMEMBER\",\"arguments\":[\"key\",\"member\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testSmembers() {
        transaction.smembers("key");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SMEMBERS\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SMEMBERS\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testSmove() {
        transaction.smove("source", "destination", "member");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SMOVE\",\"arguments\":[\"source\",\"destination\",\"member\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SMOVE\",\"arguments\":[\"source\",\"destination\",\"member\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testSort() {
        transaction.sort("key");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SORT\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SORT\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testSortWithOffsetLimit() {
        transaction.sort("key", 0, 1);
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SORT\",\"arguments\":[\"key\",\"LIMIT\",\"0\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SORT\",\"arguments\":[\"key\",\"LIMIT\",\"0\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testSortWithSortOrder() {
        transaction.sort("key", "DESC");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SORT\",\"arguments\":[\"key\",\"DESC\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SORT\",\"arguments\":[\"key\",\"DESC\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testSortWithOffsetLimitAndSortOrder() {
        transaction.sort("key", "DESC", 0, 1);
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SORT\",\"arguments\":[\"key\",\"DESC\",\"LIMIT\",\"0\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SORT\",\"arguments\":[\"key\",\"DESC\",\"LIMIT\",\"0\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testSpop() {
        transaction.spop("key");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SPOP\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SPOP\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testSrandmember() {
        transaction.srandmember("key");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SRANDMEMBER\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SRANDMEMBER\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testSrandmemberWithCount() {
        transaction.srandmember("key", 1);
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SRANDMEMBER\",\"arguments\":[\"key\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SRANDMEMBER\",\"arguments\":[\"key\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testSrem() {
        transaction.srem("key", "member");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SREM\",\"arguments\":[\"key\",\"member\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SREM\",\"arguments\":[\"key\",\"member\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testSremList() {
        transaction.srem("key", Arrays.asList(new String[]{"member", "member2"}));
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SREM\",\"arguments\":[\"key\",\"member\",\"member2\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SREM\",\"arguments\":[\"key\",\"member\",\"member2\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testStrlen() {
        transaction.strlen("key");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"STRLEN\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"STRLEN\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testSunion() {
        transaction.sunion(Collections.singletonList("key"));
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SUNION\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SUNION\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testSunionstore() {
        transaction.sunionstore("destination", Collections.singletonList("key"));
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SUNIONSTORE\",\"arguments\":[\"destination\",\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"SUNIONSTORE\",\"arguments\":[\"destination\",\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testTtl() {
        transaction.ttl("key");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"TTL\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"TTL\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testType() {
        transaction.type("key");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"TYPE\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"TYPE\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testZadd() {
        transaction.zadd("key", 1, "member");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZADD\",\"arguments\":[\"key\",\"1.0\",\"member\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZADD\",\"arguments\":[\"key\",\"1.0\",\"member\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testZaddMultiple() {
        LinkedHashMap<Double, String> map = new LinkedHashMap<>();
        map.put(1.0, "member");
        map.put(2.0, "member");

        transaction.zadd("key", map);
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZADD\",\"arguments\":[\"key\",\"1.0\",\"member\",\"2.0\",\"member\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZADD\",\"arguments\":[\"key\",\"1.0\",\"member\",\"2.0\",\"member\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testZcard() {
        transaction.zcard("key");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZCARD\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZCARD\",\"arguments\":[\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testZcount() {
        transaction.zcount("key", 0, 1);
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZCOUNT\",\"arguments\":[\"key\",\"0.0\",\"1.0\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZCOUNT\",\"arguments\":[\"key\",\"0.0\",\"1.0\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testZincrby() {
        transaction.zincrby("key", 1, "member");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZINCRBY\",\"arguments\":[\"key\",\"1.0\",\"member\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZINCRBY\",\"arguments\":[\"key\",\"1.0\",\"member\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testZinterstore() {
        transaction.zinterstore("destination", 1, Collections.singletonList("key"));
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZINTERSTORE\",\"arguments\":[\"destination\",\"1\",\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZINTERSTORE\",\"arguments\":[\"destination\",\"1\",\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testZrange() {
        transaction.zrange("key", 0, 1);
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZRANGE\",\"arguments\":[\"key\",\"0\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZRANGE\",\"arguments\":[\"key\",\"0\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testZrangewithscores() {
        transaction.zrangewithscores("key", 0, 1);
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZRANGE\",\"arguments\":[\"key\",\"0\",\"1\",\"WITHSCORES\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZRANGE\",\"arguments\":[\"key\",\"0\",\"1\",\"WITHSCORES\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testZrangebyscore() {
        transaction.zrangebyscore("key", 0, 1);
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZRANGEBYSCORE\",\"arguments\":[\"key\",\"0.0\",\"1.0\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZRANGEBYSCORE\",\"arguments\":[\"key\",\"0.0\",\"1.0\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testZrangebyscoreWithOffsetLimit() {
        transaction.zrangebyscore("key", 0, 1, 0, 1);
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZRANGEBYSCORE\",\"arguments\":[\"key\",\"0.0\",\"1.0\",\"LIMIT\",\"0\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZRANGEBYSCORE\",\"arguments\":[\"key\",\"0.0\",\"1.0\",\"LIMIT\",\"0\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testZrangebyscorewithscores() {
        transaction.zrangebyscorewithscores("key", 0, 1);
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZRANGEBYSCORE\",\"arguments\":[\"key\",\"0.0\",\"1.0\",\"WITHSCORES\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZRANGEBYSCORE\",\"arguments\":[\"key\",\"0.0\",\"1.0\",\"WITHSCORES\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testZrangebyscorewithscoresWithOffsetLimit() {
        transaction.zrangebyscorewithscores("key", 0, 1, 0, 1);
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZRANGEBYSCORE\",\"arguments\":[\"key\",\"0.0\",\"1.0\",\"WITHSCORES\",\"LIMIT\",\"0\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZRANGEBYSCORE\",\"arguments\":[\"key\",\"0.0\",\"1.0\",\"WITHSCORES\",\"LIMIT\",\"0\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testZrank() {
        transaction.zrank("key", "message");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZRANK\",\"arguments\":[\"key\",\"message\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZRANK\",\"arguments\":[\"key\",\"message\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testZrem() {
        transaction.zrem("key", "message");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZREM\",\"arguments\":[\"key\",\"message\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZREM\",\"arguments\":[\"key\",\"message\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testZremList() {
        transaction.zrem("key", Arrays.asList(new String[]{"message", "message2"}));
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZREM\",\"arguments\":[\"key\",\"message\",\"message2\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZREM\",\"arguments\":[\"key\",\"message\",\"message2\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testZremrangebyrank() {
        transaction.zremrangebyrank("key", 0, 1);
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZREMRANGEBYRANK\",\"arguments\":[\"key\",\"0\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZREMRANGEBYRANK\",\"arguments\":[\"key\",\"0\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testZremrangebyscore() {
        transaction.zremrangebyscore("key", 0, 1);
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZREMRANGEBYSCORE\",\"arguments\":[\"key\",\"0.0\",\"1.0\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZREMRANGEBYSCORE\",\"arguments\":[\"key\",\"0.0\",\"1.0\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testZrevrange() {
        transaction.zrevrange("key", 0, 1);
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZREVRANGE\",\"arguments\":[\"key\",\"0\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZREVRANGE\",\"arguments\":[\"key\",\"0\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testZrevrangewithscores() {
        transaction.zrevrangewithscores("key", 0, 1);
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZREVRANGE\",\"arguments\":[\"key\",\"0\",\"1\",\"WITHSCORES\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZREVRANGE\",\"arguments\":[\"key\",\"0\",\"1\",\"WITHSCORES\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testZrevrangebyscore() {
        transaction.zrevrangebyscore("key", 1, 0);
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZREVRANGEBYSCORE\",\"arguments\":[\"key\",\"1.0\",\"0.0\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZREVRANGEBYSCORE\",\"arguments\":[\"key\",\"1.0\",\"0.0\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testZrevrangebyscorewithscores() {
        transaction.zrevrangebyscorewithscores("key", 1, 0);
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZREVRANGEBYSCORE\",\"arguments\":[\"key\",\"1.0\",\"0.0\",\"WITHSCORES\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZREVRANGEBYSCORE\",\"arguments\":[\"key\",\"1.0\",\"0.0\",\"WITHSCORES\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testZrevrangebyscoreWithOffsetLimit() {
        transaction.zrevrangebyscore("key", 1, 0, 0, 1);
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZREVRANGEBYSCORE\",\"arguments\":[\"key\",\"1.0\",\"0.0\",\"LIMIT\",\"0\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZREVRANGEBYSCORE\",\"arguments\":[\"key\",\"1.0\",\"0.0\",\"LIMIT\",\"0\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testZrevrangebyscorewithscoresWithOffsetLimit() {
        transaction.zrevrangebyscorewithscores("key", 1, 0, 0, 1);
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZREVRANGEBYSCORE\",\"arguments\":[\"key\",\"1.0\",\"0.0\",\"WITHSCORES\",\"LIMIT\",\"0\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZREVRANGEBYSCORE\",\"arguments\":[\"key\",\"1.0\",\"0.0\",\"WITHSCORES\",\"LIMIT\",\"0\",\"1\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testZrevrank() {
        transaction.zrevrank("key", "member");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZREVRANK\",\"arguments\":[\"key\",\"member\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZREVRANK\",\"arguments\":[\"key\",\"member\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testZscore() {
        transaction.zscore("key", "member");
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZSCORE\",\"arguments\":[\"key\",\"member\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZSCORE\",\"arguments\":[\"key\",\"member\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testZunionstore() {
        transaction.zunionstore("destination", 1, Collections.singletonList("key"));
        verify(eventBus, never()).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZUNIONSTORE\",\"arguments\":[\"destination\",\"1\",\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());

        transaction.exec();
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonTransaction(Collections.singletonList("{\"command\":\"ZUNIONSTORE\",\"arguments\":[\"destination\",\"1\",\"key\"]}"))), withTimeout(TIMEOUT), getCaptor.capture());
    }

    private JsonObject getJsonTransaction(List<String> jsonStrings) {
        JsonArray commands = new JsonArray();
        for (String jsonString : jsonStrings) {
            commands.add(new JsonObject(jsonString));
        }
        return new JsonObject().put("isTransaction", true).put("commands", commands);
    }

    private static DeliveryOptions withTimeout(final Long expectedTimeout) {
        return MockitoHamcrest.argThat(new FeatureMatcher<DeliveryOptions, Long>(IsEqual.equalTo(expectedTimeout), "timeout", "timeout") {
            @Override
            protected Long featureValueOf(DeliveryOptions deliveryOptions) {
                return deliveryOptions.getSendTimeout();
            }
        });
    }
}

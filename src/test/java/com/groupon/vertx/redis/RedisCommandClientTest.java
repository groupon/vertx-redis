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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;

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
 * Tests for <code>RedisCommandClient</code>.
 *
 * @author Stuart Siegrist (fsiegrist at groupon dot com)
 * @since 1.0.0
 */
public class RedisCommandClientTest {
    private static final long TIMEOUT = 1000;
    @Mock
    private EventBus eventBus;

    @Mock
    private Handler<AsyncResult<JsonObject>> handler;

    @Mock
    private Message<JsonObject> message;

    @Mock
    AsyncResult<Message<JsonObject>> asyncResult;

    @Captor
    private ArgumentCaptor<Handler<AsyncResult<Message<JsonObject>>>> getCaptor;

    @Captor
    private ArgumentCaptor<AsyncResult<JsonObject>> resultCaptor;

    @Captor
    private ArgumentCaptor<DeliveryOptions> deliveryOptionsCaptor;

    private RedisCommandClient factory;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        factory = new RedisClient(eventBus, "address", TIMEOUT);
        when(message.body()).thenReturn(new JsonObject());
        when(asyncResult.result()).thenReturn(message);
        when(asyncResult.succeeded()).thenReturn(true);
    }

    @Test
    public void testAppend() {
        factory.append("key", "value").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"APPEND\",\"arguments\":[\"key\",\"value\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testBitcount() {
        factory.bitcount("key").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"BITCOUNT\",\"arguments\":[\"key\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testBitcountWithStartStop() {
        factory.bitcount("key", 0, 1).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"BITCOUNT\",\"arguments\":[\"key\",\"0\",\"1\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testBitop() {
        factory.bitop("OR", "key", Collections.singletonList("key2")).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"BITOP\",\"arguments\":[\"OR\",\"key\",\"key2\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testBlpopList() {
        factory.blpop(1, Arrays.asList("key", "key2")).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"BLPOP\",\"arguments\":[\"key\",\"key2\",\"1\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testBrpopList() {
        factory.brpop(1, Arrays.asList("key", "key2")).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"BRPOP\",\"arguments\":[\"key\",\"key2\",\"1\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testBrpoplpush() {
        factory.brpoplpush("src", "dest", 1).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"BRPOPLPUSH\",\"arguments\":[\"src\",\"dest\",\"1\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testDecr() {
        factory.decr("key").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"DECR\",\"arguments\":[\"key\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testDecrby() {
        factory.decrby("key", 1).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"DECRBY\",\"arguments\":[\"key\",\"1\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testDel() {
        factory.del("key").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"DEL\",\"arguments\":[\"key\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testDelList() {
        factory.del(Arrays.asList(new String[]{"key", "key2"})).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"DEL\",\"arguments\":[\"key\",\"key2\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testEcho() {
        factory.echo("key").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"ECHO\",\"arguments\":[\"key\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testExists() {
        factory.exists("key").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"EXISTS\",\"arguments\":[\"key\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testExpire() {
        factory.expire("key", 1).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"EXPIRE\",\"arguments\":[\"key\",\"1\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testExpireat() {
        factory.expireat("key", 1234).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"EXPIREAT\",\"arguments\":[\"key\",\"1234\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testFlushall() {
        factory.flushall().setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"FLUSHALL\",\"arguments\":[]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testFlushdb() {
        factory.flushdb().setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"FLUSHDB\",\"arguments\":[]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testGet() {
        factory.get("key").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"GET\",\"arguments\":[\"key\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testGetbit() {
        factory.getbit("key", 0).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"GETBIT\",\"arguments\":[\"key\",\"0\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testGetRange() {
        factory.getrange("key", 0, 1).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"GETRANGE\",\"arguments\":[\"key\",\"0\",\"1\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testGetset() {
        factory.getset("key", "value").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"GETSET\",\"arguments\":[\"key\",\"value\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testHdel() {
        factory.hdel("key", "field").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"HDEL\",\"arguments\":[\"key\",\"field\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testHdelList() {
        factory.hdel("key", Arrays.asList(new String[]{"field", "field2"})).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"HDEL\",\"arguments\":[\"key\",\"field\",\"field2\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testHexists() {
        factory.hexists("key", "field").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"HEXISTS\",\"arguments\":[\"key\",\"field\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testHget() {
        factory.hget("key", "field").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"HGET\",\"arguments\":[\"key\",\"field\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testHgetall() {
        factory.hgetall("key").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"HGETALL\",\"arguments\":[\"key\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testHincrby() {
        factory.hincrby("key", "field", 1).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"HINCRBY\",\"arguments\":[\"key\",\"field\",\"1\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testHincrbyfloat() {
        factory.hincrbyfloat("key", "field", 1.1).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"HINCRBYFLOAT\",\"arguments\":[\"key\",\"field\",\"1.1\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testHkeys() {
        factory.hkeys("key").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"HKEYS\",\"arguments\":[\"key\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testHlen() {
        factory.hlen("key").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"HLEN\",\"arguments\":[\"key\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testHmget() {
        factory.hmget("key", "field").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"HMGET\",\"arguments\":[\"key\",\"field\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testHmgetList() {
        factory.hmget("key", Arrays.asList(new String[]{"field", "field2"})).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"HMGET\",\"arguments\":[\"key\",\"field\",\"field2\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testHmset() {
        HashMap<String, String> map = new HashMap<>();
        map.put("field", "value");

        factory.hmset("key", map).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"HMSET\",\"arguments\":[\"key\",\"field\",\"value\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testHset() {
        factory.hset("key", "field", "value").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"HSET\",\"arguments\":[\"key\",\"field\",\"value\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testHsetnx() {
        factory.hsetnx("key", "field", "value").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"HSETNX\",\"arguments\":[\"key\",\"field\",\"value\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testHvals() {
        factory.hvals("key").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"HVALS\",\"arguments\":[\"key\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testIncr() {
        factory.incr("key").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"INCR\",\"arguments\":[\"key\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testIncrby() {
        factory.incrby("key", 1).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"INCRBY\",\"arguments\":[\"key\",\"1\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testIncrbyfloat() {
        factory.incrbyfloat("key", 1.1).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"INCRBYFLOAT\",\"arguments\":[\"key\",\"1.1\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testKeys() {
        factory.keys("pattern").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"KEYS\",\"arguments\":[\"pattern\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testLindex() {
        factory.lindex("key", 0).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"LINDEX\",\"arguments\":[\"key\",\"0\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testLinsertbefore() {
        factory.linsertbefore("key", "pivot", "value").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"LINSERT\",\"arguments\":[\"key\",\"BEFORE\",\"pivot\",\"value\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testLinsertafter() {
        factory.linsertafter("key", "pivot", "value").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"LINSERT\",\"arguments\":[\"key\",\"AFTER\",\"pivot\",\"value\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testLlen() {
        factory.llen("key").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"LLEN\",\"arguments\":[\"key\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testLpop() {
        factory.lpop("key").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"LPOP\",\"arguments\":[\"key\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testLpush() {
        factory.lpush("key", "value").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"LPUSH\",\"arguments\":[\"key\",\"value\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testLpushList() {
        factory.lpush("key", Arrays.asList(new String[]{"value", "value2"})).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"LPUSH\",\"arguments\":[\"key\",\"value\",\"value2\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testLpushx() {
        factory.lpushx("key", "value").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"LPUSHX\",\"arguments\":[\"key\",\"value\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testLrange() {
        factory.lrange("key", 0, 1).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"LRANGE\",\"arguments\":[\"key\",\"0\",\"1\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testLrem() {
        factory.lrem("key", 1, "value").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"LREM\",\"arguments\":[\"key\",\"1\",\"value\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testLset() {
        factory.lset("key", 0, "value").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"LSET\",\"arguments\":[\"key\",\"0\",\"value\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testLtrim() {
        factory.ltrim("key", 0, 1).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"LTRIM\",\"arguments\":[\"key\",\"0\",\"1\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testMget() {
        factory.mget("key").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"MGET\",\"arguments\":[\"key\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testMgetList() {
        factory.mget(Arrays.asList(new String[]{"key", "key2"})).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"MGET\",\"arguments\":[\"key\",\"key2\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testMset() {
        HashMap<String, String> map = new HashMap<>();
        map.put("key", "value");

        factory.mset(map).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"MSET\",\"arguments\":[\"key\",\"value\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testMsetnx() {
        HashMap<String, String> map = new HashMap<>();
        map.put("key", "value");

        factory.msetnx(map).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"MSETNX\",\"arguments\":[\"key\",\"value\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testMulti() {
        factory.multi();

        verify(eventBus, never()).send(eq("address"), eq(getJsonCommand("{\"command\":\"MULTI\",\"arguments\":[]}")), withTimeout(TIMEOUT), getCaptor.capture());
    }

    @Test
    public void testPersist() {
        factory.persist("key").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"PERSIST\",\"arguments\":[\"key\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testPexpire() {
        factory.pexpire("key", 12345).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"PEXPIRE\",\"arguments\":[\"key\",\"12345\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testPexpireat() {
        factory.pexpireat("key", 12345).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"PEXPIREAT\",\"arguments\":[\"key\",\"12345\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testPing() {
        factory.ping().setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"PING\",\"arguments\":[]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testPsetex() {
        factory.psetex("key", 12345, "value").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"PSETEX\",\"arguments\":[\"key\",\"12345\",\"value\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testPttl() {
        factory.pttl("key").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"PTTL\",\"arguments\":[\"key\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testRandomkey() {
        factory.randomkey().setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"RANDOMKEY\",\"arguments\":[]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testRename() {
        factory.rename("key", "newkey").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"RENAME\",\"arguments\":[\"key\",\"newkey\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testRenamenx() {
        factory.renamenx("key", "newkey").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"RENAMENX\",\"arguments\":[\"key\",\"newkey\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testRpop() {
        factory.rpop("key").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"RPOP\",\"arguments\":[\"key\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testRpoplpush() {
        factory.rpoplpush("source", "destination").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"RPOPLPUSH\",\"arguments\":[\"source\",\"destination\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testRpush() {
        factory.rpush("key", "value").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"RPUSH\",\"arguments\":[\"key\",\"value\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testRpushList() {
        factory.rpush("key", Arrays.asList(new String[]{"value", "value2"})).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"RPUSH\",\"arguments\":[\"key\",\"value\",\"value2\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testRpushx() {
        factory.rpushx("key", "value").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"RPUSHX\",\"arguments\":[\"key\",\"value\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testSadd() {
        factory.sadd("key", "member").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"SADD\",\"arguments\":[\"key\",\"member\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testSaddList() {
        factory.sadd("key", Arrays.asList(new String[]{"member", "member2"})).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"SADD\",\"arguments\":[\"key\",\"member\",\"member2\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testScard() {
        factory.scard("key").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"SCARD\",\"arguments\":[\"key\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testSdiffList() {
        factory.sdiff(Arrays.asList("diffKey", "diffKey2")).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"SDIFF\",\"arguments\":[\"diffKey\",\"diffKey2\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testSdiffstoreList() {
        factory.sdiffstore("destination", Arrays.asList("diffKey", "diffKey2")).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"SDIFFSTORE\",\"arguments\":[\"destination\",\"diffKey\",\"diffKey2\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testSet() {
        factory.set("key", "value").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"SET\",\"arguments\":[\"key\",\"value\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testSetex() {
        factory.setex("key", 12345, "value").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"SETEX\",\"arguments\":[\"key\",\"12345\",\"value\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testSetnx() {
        factory.setnx("key", "value").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"SETNX\",\"arguments\":[\"key\",\"value\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testSetexnx() {
        factory.setexnx("key", 12345, "value").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"SET\",\"arguments\":[\"key\",\"value\",\"NX\",\"EX\",\"12345\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testSetrange() {
        factory.setrange("key", 0, "value").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"SETRANGE\",\"arguments\":[\"key\",\"0\",\"value\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testSinterList() {
        factory.sinter(Arrays.asList("intersectKey", "intersectKey2")).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"SINTER\",\"arguments\":[\"intersectKey\",\"intersectKey2\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testSinterstoreList() {
        factory.sinterstore("destination", Arrays.asList("intersectKey", "intersectKey2")).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"SINTERSTORE\",\"arguments\":[\"destination\",\"intersectKey\",\"intersectKey2\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testSismember() {
        factory.sismember("key", "member").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"SISMEMBER\",\"arguments\":[\"key\",\"member\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testSmembers() {
        factory.smembers("key").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"SMEMBERS\",\"arguments\":[\"key\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testSmove() {
        factory.smove("source", "destination", "member").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"SMOVE\",\"arguments\":[\"source\",\"destination\",\"member\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testSort() {
        factory.sort("key").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"SORT\",\"arguments\":[\"key\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testSortWithOffsetLimit() {
        factory.sort("key", 0, 1).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"SORT\",\"arguments\":[\"key\",\"LIMIT\",\"0\",\"1\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testSortWithSortOrder() {
        factory.sort("key", "DESC").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"SORT\",\"arguments\":[\"key\",\"DESC\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testSortWithOffsetLimitAndSortOrder() {
        factory.sort("key", "DESC", 0, 1).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"SORT\",\"arguments\":[\"key\",\"DESC\",\"LIMIT\",\"0\",\"1\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testSpop() {
        factory.spop("key").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"SPOP\",\"arguments\":[\"key\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testSrandmember() {
        factory.srandmember("key").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"SRANDMEMBER\",\"arguments\":[\"key\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testSrandmemberWithCount() {
        factory.srandmember("key", 1).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"SRANDMEMBER\",\"arguments\":[\"key\",\"1\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testSrem() {
        factory.srem("key", "member").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"SREM\",\"arguments\":[\"key\",\"member\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testSremList() {
        factory.srem("key", Arrays.asList(new String[]{"member", "member2"})).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"SREM\",\"arguments\":[\"key\",\"member\",\"member2\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testStrlen() {
        factory.strlen("key").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"STRLEN\",\"arguments\":[\"key\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testSunion() {
        factory.sunion(Collections.singletonList("key")).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"SUNION\",\"arguments\":[\"key\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testSunionstore() {
        factory.sunionstore("destination", Collections.singletonList("key")).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"SUNIONSTORE\",\"arguments\":[\"destination\",\"key\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testTtl() {
        factory.ttl("key").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"TTL\",\"arguments\":[\"key\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testType() {
        factory.type("key").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"TYPE\",\"arguments\":[\"key\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testZadd() {
        factory.zadd("key", 1, "member").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"ZADD\",\"arguments\":[\"key\",\"1.0\",\"member\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testZaddMultiple() {
        LinkedHashMap<Double, String> map = new LinkedHashMap<>();
        map.put(1.0, "member");
        map.put(2.0, "member");

        factory.zadd("key", map).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"ZADD\",\"arguments\":[\"key\",\"1.0\",\"member\",\"2.0\",\"member\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testZcard() {
        factory.zcard("key").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"ZCARD\",\"arguments\":[\"key\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testZcount() {
        factory.zcount("key", 0, 1).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"ZCOUNT\",\"arguments\":[\"key\",\"0.0\",\"1.0\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testZincrby() {
        factory.zincrby("key", 1, "member").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"ZINCRBY\",\"arguments\":[\"key\",\"1.0\",\"member\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testZinterstore() {
        factory.zinterstore("destination", 1, Collections.singletonList("key")).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"ZINTERSTORE\",\"arguments\":[\"destination\",\"1\",\"key\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testZrange() {
        factory.zrange("key", 0, 1).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"ZRANGE\",\"arguments\":[\"key\",\"0\",\"1\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testZrangewithscores() {
        factory.zrangewithscores("key", 0, 1).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"ZRANGE\",\"arguments\":[\"key\",\"0\",\"1\",\"WITHSCORES\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testZrangebyscore() {
        factory.zrangebyscore("key", 0, 1).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"ZRANGEBYSCORE\",\"arguments\":[\"key\",\"0.0\",\"1.0\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testZrangebyscoreWithOffsetLimit() {
        factory.zrangebyscore("key", 0, 1, 0, 1).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"ZRANGEBYSCORE\",\"arguments\":[\"key\",\"0.0\",\"1.0\",\"LIMIT\",\"0\",\"1\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testZrangebyscorewithscores() {
        factory.zrangebyscorewithscores("key", 0, 1).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"ZRANGEBYSCORE\",\"arguments\":[\"key\",\"0.0\",\"1.0\",\"WITHSCORES\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testZrangebyscorewithscoresWithOffsetLimit() {
        factory.zrangebyscorewithscores("key", 0, 1, 0, 1).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"ZRANGEBYSCORE\",\"arguments\":[\"key\",\"0.0\",\"1.0\",\"WITHSCORES\",\"LIMIT\",\"0\",\"1\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testZrank() {
        factory.zrank("key", "message").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"ZRANK\",\"arguments\":[\"key\",\"message\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testZrem() {
        factory.zrem("key", "message").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"ZREM\",\"arguments\":[\"key\",\"message\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testZremList() {
        factory.zrem("key", Arrays.asList(new String[]{"message", "message2"})).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"ZREM\",\"arguments\":[\"key\",\"message\",\"message2\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testZremrangebyrank() {
        factory.zremrangebyrank("key", 0, 1).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"ZREMRANGEBYRANK\",\"arguments\":[\"key\",\"0\",\"1\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testZremrangebyscore() {
        factory.zremrangebyscore("key", 0, 1).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"ZREMRANGEBYSCORE\",\"arguments\":[\"key\",\"0.0\",\"1.0\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testZrevrange() {
        factory.zrevrange("key", 0, 1).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"ZREVRANGE\",\"arguments\":[\"key\",\"0\",\"1\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testZrevrangewithscores() {
        factory.zrevrangewithscores("key", 0, 1).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"ZREVRANGE\",\"arguments\":[\"key\",\"0\",\"1\",\"WITHSCORES\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testZrevrangebyscore() {
        factory.zrevrangebyscore("key", 1, 0).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"ZREVRANGEBYSCORE\",\"arguments\":[\"key\",\"1.0\",\"0.0\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testZrevrangebyscorewithscores() {
        factory.zrevrangebyscorewithscores("key", 1, 0).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"ZREVRANGEBYSCORE\",\"arguments\":[\"key\",\"1.0\",\"0.0\",\"WITHSCORES\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testZrevrangebyscoreWithOffsetLimit() {
        factory.zrevrangebyscore("key", 1, 0, 0, 1).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"ZREVRANGEBYSCORE\",\"arguments\":[\"key\",\"1.0\",\"0.0\",\"LIMIT\",\"0\",\"1\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testZrevrangebyscorewithscoresWithOffsetLimit() {
        factory.zrevrangebyscorewithscores("key", 1, 0, 0, 1).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"ZREVRANGEBYSCORE\",\"arguments\":[\"key\",\"1.0\",\"0.0\",\"WITHSCORES\",\"LIMIT\",\"0\",\"1\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testZrevrank() {
        factory.zrevrank("key", "member").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"ZREVRANK\",\"arguments\":[\"key\",\"member\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testZscore() {
        factory.zscore("key", "member").setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"ZSCORE\",\"arguments\":[\"key\",\"member\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testZunionstore() {
        factory.zunionstore("destination", 1, Collections.singletonList("key")).setHandler(handler);

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"ZUNIONSTORE\",\"arguments\":[\"destination\",\"1\",\"key\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());

        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.succeeded());
        assertEquals(new JsonObject(), result.result());
    }

    @Test
    public void testTimedoutCommand() {
        factory.zunionstore("destination", 1, Collections.singletonList("key")).setHandler(handler);
        when(asyncResult.succeeded()).thenReturn(false);
        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"ZUNIONSTORE\",\"arguments\":[\"destination\",\"1\",\"key\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
        verify(handler, times(1)).handle(resultCaptor.capture());
        AsyncResult<JsonObject> result = resultCaptor.getValue();
        assertTrue(result.failed());
        assertNotNull(result.cause());
    }

    @Test
    public void testCommandWithNullHandler() {
        factory.zunionstore("destination", 1, Collections.singletonList("key"));

        verify(eventBus, times(1)).send(eq("address"), eq(getJsonCommand("{\"command\":\"ZUNIONSTORE\",\"arguments\":[\"destination\",\"1\",\"key\"]}")), withTimeout(TIMEOUT), getCaptor.capture());
        getCaptor.getValue().handle(asyncResult);
    }

    private JsonObject getJsonCommand(String command) {
        return new JsonObject().put("commands", new JsonArray().add(new JsonObject(command)));
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

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetClient;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Tests for <code>RedisVerticle</code>.
 *
 * @author Stuart Siegrist (fsiegrist at groupon dot com)
 * @since 1.0.0
 */
public class RedisVerticleTest extends TestCase {

    @Mock
    private Vertx vertx;

    @Mock
    private EventBus eventBus;

    @Mock
    private NetClient netClient;

    @Mock
    private Context context;

    @Mock
    private Future<Void> startFuture;

    private RedisVerticle verticle;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(vertx.eventBus()).thenReturn(eventBus);
        when(vertx.createNetClient()).thenReturn(netClient);
        doReturn(context).when(vertx).getOrCreateContext();

        verticle = new RedisVerticle();
        verticle.init(vertx, context);
    }

    @After
    public void tearDown() throws Exception {
        if (verticle != null) {
            verticle.stop();
        }
    }

    @Test
    public void testStartValidConfig() {
        JsonObject config = new JsonObject("{\"redisConfig\":{\"host\":\"foo\",\"port\":1234,\"eventBusAddress\":\"address\"}}");

        when(context.config()).thenReturn(config);

        verticle.start(startFuture);

        verify(context, times(1)).config();
        verify(vertx, times(1)).createNetClient();
        verify(netClient, times(1)).connect(eq(1234), eq("foo"), any());
    }

    @Test
    public void testStartInvalidConfig() {
        JsonObject config = new JsonObject("{\"redisConfig\":{\"hostname\":\"foo\",\"portNumber\":1234}}");

        when(context.config()).thenReturn(config);

        verticle.start(startFuture);

        verify(context, times(1)).config();
        verify(vertx, never()).createNetClient();
        verify(startFuture, times(1)).fail(any(Exception.class));
    }

    @Test
    public void testStartMissingConfig() {
        JsonObject config = new JsonObject("{}");

        when(context.config()).thenReturn(config);

        verticle.start(startFuture);

        verify(context, times(1)).config();
        verify(vertx, never()).createNetClient();
        verify(startFuture, times(1)).fail(any(Exception.class));
    }
}

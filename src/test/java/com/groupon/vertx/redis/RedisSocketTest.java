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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetSocket;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Tests for <code>RedisSocket</code>.
 *
 * @author Stuart Siegrist (fsiegrist at groupon dot com)
 * @since 1.0.0
 */
public class RedisSocketTest extends TestCase {

    @Mock
    private NetSocket netSocket;

    @Mock
    private RedisInputStream inputStream;

    @Captor
    private ArgumentCaptor<Handler<Buffer>> handlerCaptor;

    private RedisSocket redisSocket;
    private Field pendingCommandField;
    private Field inputStreamField;
    private ConcurrentLinkedQueue<RedisCommand> pendingCommands;
    private Handler<Buffer> dataHandler;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        redisSocket = new RedisSocket(netSocket);

        inputStreamField = RedisSocket.class.getDeclaredField("input");
        inputStreamField.setAccessible(true);
        inputStreamField.set(redisSocket, inputStream);

        pendingCommandField = RedisSocket.class.getDeclaredField("pendingCommands");
        pendingCommandField.setAccessible(true);

        verify(netSocket, times(1)).handler(handlerCaptor.capture());

        dataHandler = handlerCaptor.getValue();

        pendingCommands = (ConcurrentLinkedQueue<RedisCommand>) pendingCommandField.get(redisSocket);
    }

    @After
    public void tearDown() throws Exception {
        pendingCommandField.setAccessible(false);
        inputStreamField.setAccessible(false);
    }

    @Test
    public void testSocketDataHandler() {
        dataHandler.handle(Buffer.buffer());

        verify(inputStream, times(1)).processBuffer(Buffer.buffer());
    }

    @Test
    public void testSendCommandStatus() {
        RedisCommand command = createCommand(RedisCommandType.PING, new Object[]{});

        try {
            redisSocket.sendCommand(Collections.<RedisCommand>singletonList(command));

            assertEquals("Missing pending command", 1, pendingCommands.size());
            assertEquals("Incorrect command", command, pendingCommands.iterator().next());
        } catch (Exception ex) {
            assertNull("Unexpected exception", ex);
        }

        verify(netSocket, times(1)).write(Buffer.buffer().appendString("*1\r\n$4\r\nPING\r\n"));
    }

    @Test
    public void testSendCommandBulk() {
        RedisCommand command = createCommand(RedisCommandType.GET, new Object[]{"somekey"});

        try {
            redisSocket.sendCommand(Collections.<RedisCommand>singletonList(command));

            assertEquals("Missing pending command", 1, pendingCommands.size());
            assertEquals("Incorrect command", command, pendingCommands.iterator().next());
        } catch (Exception ex) {
            assertNull("Unexpected exception", ex);
        }

        verify(netSocket, times(1)).write(Buffer.buffer().appendString("*2\r\n$3\r\nGET\r\n$7\r\nsomekey\r\n"));
    }

    @Test
    public void testSendCommandMultiBulk() {
        RedisCommand command = createCommand(RedisCommandType.ZRANGE, new Object[]{"zsetkey", 0, -1});

        try {
            redisSocket.sendCommand(Collections.<RedisCommand>singletonList(command));

            assertEquals("Missing pending command", 1, pendingCommands.size());
            assertEquals("Incorrect command", command, pendingCommands.iterator().next());
        } catch (Exception ex) {
            assertNull("Unexpected exception", ex);
        }

        verify(netSocket, times(1)).write(Buffer.buffer().appendString("*4\r\n$6\r\nZRANGE\r\n$7\r\nzsetkey\r\n$1\r\n0\r\n$2\r\n-1\r\n"));
    }

    @Test
    public void testClose() {
        RedisCommand command = mock(RedisCommand.class);

        pendingCommands.add(command);

        redisSocket.close();

        verify(command, times(1)).setResponse(new JsonObject("{\"status\":\"error\",\"message\":\"Socket closed unexpectedly\"}"));
        verify(netSocket, times(1)).close();
    }

    private RedisCommand createCommand(RedisCommandType type, Object[] arguments) {
        JsonObject commandJson = new JsonObject();
        commandJson.put("command", type.getCommand());
        commandJson.put("arguments", new JsonArray(Arrays.asList(arguments)));
        return new RedisCommand(commandJson);
    }
}

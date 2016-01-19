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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetSocket;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Tests for <code>RedisCommandHandler</code>.
 *
 * @author Stuart Siegrist (fsiegrist at groupon dot com)
 * @since 1.0.0
 */
public class RedisCommandHandlerTest {
    @Mock
    private NetSocket socket;

    @Mock
    private Message<JsonObject> message;

    private RedisCommandHandler handler;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        handler = new RedisCommandHandler(socket);
    }

    @Test
    public void testHandleNullMessage() {
        stub(message.body()).toReturn(null);

        handler.handle(message);

        verify(message, times(1)).body();
        verify(message, times(1)).reply(buildReply("error", null, "Invalid message with null or empty."));
    }

    @Test
    public void testHandleEmptyMessage() {
        stub(message.body()).toReturn(new JsonObject());

        handler.handle(message);

        verify(message, times(2)).body();
        verify(message, times(1)).reply(buildReply("error", null, "Invalid message with null or empty."));
    }

    @Test
    public void testHandleInvalidMessage() {

        stub(message.body()).toReturn(new JsonObject().put("commands", new JsonArray().add(new JsonObject().put("command", "invalid").put("arguments", "invalid"))));

        handler.handle(message);

        verify(message, times(3)).body();
        verify(message, times(1)).reply(buildReply("error", null, "Invalid or unsupported command provided"));
    }

    @Test
    public void testHandleValidMessage() {
        stub(message.body()).toReturn(new JsonObject().put("commands", new JsonArray().add(new JsonObject().put("command", "GET").put("arguments", "somekey"))));

        handler.handle(message);

        verify(message, times(3)).body();
        verify(socket, times(1)).write(Buffer.buffer().appendString("*2\r\n$3\r\nGET\r\n$7\r\nsomekey\r\n"));
        verify(message, never()).reply(any(JsonObject.class));
    }

    @Test
    public void testHandleValidTransactionMessage() {
        JsonObject command = new JsonObject();
        command.put("command", "GET").put("arguments", "somekey");
        JsonArray commands = new JsonArray();
        commands.add(command);
        commands.add(command);
        JsonObject transactionCommands = new JsonObject();
        transactionCommands.put("isTransaction", true);
        transactionCommands.put("commands", commands);

        stub(message.body()).toReturn(transactionCommands);

        handler.handle(message);

        verify(message, times(3)).body();
        verify(socket, times(1)).write(Buffer.buffer().appendString("*1\r\n$5\r\nMULTI\r\n*2\r\n$3\r\nGET\r\n$7\r\nsomekey\r\n*2\r\n$3\r\nGET\r\n$7\r\nsomekey\r\n*1\r\n$4\r\nEXEC\r\n"));
        verify(message, never()).reply(any(JsonObject.class));
    }

    private static JsonObject buildReply(String status, JsonObject data, String message) {
        JsonObject jsonReply = new JsonObject();

        jsonReply.put("status", status);

        if ("success".equals(status) || data != null) {
            jsonReply.putNull("data");
        } else {
            jsonReply.put("message", message);
        }

        return jsonReply;
    }
}

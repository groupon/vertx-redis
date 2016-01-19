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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for <code>RedisInputStream</code>.
 *
 * @author Stuart Siegrist (fsiegrist at groupon dot com)
 * @since 1.0.0
 */
@SuppressWarnings("unchecked")
public class RedisInputStreamTest {
    private ConcurrentLinkedQueue<RedisCommand> pendingCommands = null;
    private Field bufferPosition = null;
    private Field expectedLines = null;
    private Field completedLines = null;

    @Before
    public void setUp() throws Exception {
        pendingCommands = new ConcurrentLinkedQueue<>();

        bufferPosition = RedisInputStream.class.getDeclaredField("bufferPosition");
        bufferPosition.setAccessible(true);

        expectedLines = RedisInputStream.class.getDeclaredField("expectedLines");
        expectedLines.setAccessible(true);

        completedLines = RedisInputStream.class.getDeclaredField("completedLines");
        completedLines.setAccessible(true);
    }

    @After
    public void tearDown() throws Exception {
        pendingCommands.clear();
        pendingCommands = null;

        bufferPosition.setAccessible(false);
        expectedLines.setAccessible(false);
    }

    @Test
    public void testProcessEmptyBuffer() {
        RedisInputStream input = new RedisInputStream(pendingCommands);

        try {
            input.processBuffer(Buffer.buffer());

            assertEquals("Invalid buffer position", 0, bufferPosition.getInt(input));
            assertEquals("Invalid expected lines", 0, expectedLines.getInt(input));
            assertEquals("Invalid completed line count", 0, ((Collection<byte[]>) completedLines.get(input)).size());
        } catch (Exception ex) {
            assertNull("Unexpected exception: " + ex.getMessage(), ex);
        }
    }

    @Test
    public void testProcessNullBuffer() {
        RedisInputStream input = new RedisInputStream(pendingCommands);

        try {
            input.processBuffer(null);

            assertEquals("Invalid buffer position", 0, bufferPosition.getInt(input));
            assertEquals("Invalid expected lines", 0, expectedLines.getInt(input));
            assertEquals("Invalid completed line count", 0, ((Collection<byte[]>) completedLines.get(input)).size());
        } catch (Exception ex) {
            assertNull("Unexpected exception: " + ex.getMessage(), ex);
        }
    }

    @Test
    public void testProcessSingleByteBuffer() {
        RedisInputStream input = new RedisInputStream(pendingCommands);

        try {
            Buffer buff = Buffer.buffer();
            buff.appendByte((byte) 'a');
            input.processBuffer(buff);

            assertEquals("Invalid buffer position", 1, bufferPosition.getInt(input));
            assertEquals("Invalid expected lines", 0, expectedLines.getInt(input));
            assertEquals("Invalid completed line count", 0, ((Collection<byte[]>) completedLines.get(input)).size());
        } catch (Exception ex) {
            assertNull("Unexpected exception: " + ex.getMessage(), ex);
        }
    }

    @Test
    public void testProcessSingleStatusLineBuffer() {
        RedisInputStream input = new RedisInputStream(pendingCommands);

        try {
            RedisCommand command = createCommand(RedisCommandType.PING, new Object[]{});

            Future<JsonObject> future = Future.future();
            future.setHandler(new Handler<AsyncResult<JsonObject>>() {
                public void handle(AsyncResult<JsonObject> asyncCommand) {
                    assertTrue(asyncCommand.succeeded());
                    assertNotNull(asyncCommand.result());
                    JsonObject command = asyncCommand.result();

                    assertNotNull("Invalid command json response", command);
                    assertEquals("Invalid status", "success", command.getString("status"));
                    assertEquals("Invalid data", "OK", command.getString("data"));
                }
            });
            command.commandResponse(future);

            pendingCommands.add(command);

            Buffer buff = Buffer.buffer();
            buff.appendString("+OK\r\n");
            input.processBuffer(buff);

            assertEquals("Invalid buffer position", 0, bufferPosition.getInt(input));
            assertEquals("Invalid expected lines", 0, expectedLines.getInt(input));
            assertEquals("Invalid completed line count", 0, ((Collection<byte[]>) completedLines.get(input)).size());
        } catch (Exception ex) {
            assertNull("Unexpected exception: " + ex.getMessage(), ex);
        }
    }

    @Test
    public void testProcessSingleIntegerLineBuffer() {
        RedisInputStream input = new RedisInputStream(pendingCommands);

        try {
            RedisCommand command = createCommand(RedisCommandType.SETNX, new Object[]{"somekey"});

            Future<JsonObject> future = Future.future();
            future.setHandler(new Handler<AsyncResult<JsonObject>>() {
                public void handle(AsyncResult<JsonObject> asyncCommand) {
                    assertTrue(asyncCommand.succeeded());
                    assertNotNull(asyncCommand.result());
                    JsonObject command = asyncCommand.result();

                    assertNotNull("Invalid command json response", command);
                    assertEquals("Invalid status", "success", command.getString("status"));
                    assertEquals("Invalid data", 123456, command.getInteger("data").intValue());
                }
            });
            command.commandResponse(future);

            pendingCommands.add(command);

            Buffer buff = Buffer.buffer();
            buff.appendString(":123456\r\n");
            input.processBuffer(buff);

            assertEquals("Invalid buffer position", 0, bufferPosition.getInt(input));
            assertEquals("Invalid expected lines", 0, expectedLines.getInt(input));
            assertEquals("Invalid completed line count", 0, ((Collection<byte[]>) completedLines.get(input)).size());
        } catch (Exception ex) {
            assertNull("Unexpected exception: " + ex.getMessage(), ex);
        }
    }

    @Test
    public void testProcessErrorBuffer() {
        RedisInputStream input = new RedisInputStream(pendingCommands);

        try {
            RedisCommand command = createCommand(RedisCommandType.SETNX, new Object[]{"somekey"});

            Future<JsonObject> future = Future.future();
            future.setHandler(new Handler<AsyncResult<JsonObject>>() {
                public void handle(AsyncResult<JsonObject> asyncCommand) {
                    assertTrue(asyncCommand.succeeded());
                    assertNotNull(asyncCommand.result());
                    JsonObject command = asyncCommand.result();

                    assertNotNull("Invalid command json response", command);
                    assertEquals("Invalid status", "fail", command.getString("status"));
                    assertEquals("Invalid data", "error here", command.getString("data"));
                }
            });
            command.commandResponse(future);

            pendingCommands.add(command);

            Buffer buff = Buffer.buffer();
            buff.appendString("-error here\r\n");
            input.processBuffer(buff);

            assertEquals("Invalid buffer position", 0, bufferPosition.getInt(input));
            assertEquals("Invalid expected lines", 0, expectedLines.getInt(input));
            assertEquals("Invalid completed line count", 0, ((Collection<byte[]>) completedLines.get(input)).size());
        } catch (Exception ex) {
            assertNull("Unexpected exception: " + ex.getMessage(), ex);
        }
    }

    @Test
    public void testProcessBulkLineBuffer() {
        RedisInputStream input = new RedisInputStream(pendingCommands);

        try {
            RedisCommand command = createCommand(RedisCommandType.GET, new Object[]{"keyname"});

            Future<JsonObject> future = Future.future();
            future.setHandler(new Handler<AsyncResult<JsonObject>>() {
                public void handle(AsyncResult<JsonObject> asyncCommand) {
                    assertTrue(asyncCommand.succeeded());
                    assertNotNull(asyncCommand.result());
                    JsonObject command = asyncCommand.result();

                    assertNotNull("Invalid command json response", command);
                    assertEquals("Invalid status", "success", command.getString("status"));
                    assertEquals("Invalid data", "foobar", command.getString("data"));
                }
            });
            command.commandResponse(future);

            pendingCommands.add(command);

            Buffer buff = Buffer.buffer();
            buff.appendString("$6\r\nfoobar\r\n");
            input.processBuffer(buff);

            assertEquals("Invalid buffer position", 0, bufferPosition.getInt(input));
            assertEquals("Invalid expected lines", 0, expectedLines.getInt(input));
            assertEquals("Invalid completed line count", 0, ((Collection<byte[]>) completedLines.get(input)).size());
        } catch (Exception ex) {
            assertNull("Unexpected exception: " + ex.getMessage(), ex);
        }
    }

    @Test
    public void testProcessMultiLineBuffer() {
        RedisInputStream input = new RedisInputStream(pendingCommands);

        try {
            RedisCommand command = createCommand(RedisCommandType.ZRANGE, new Object[]{"zsetname", 0, -1});

            Future<JsonObject> future = Future.future();
            future.setHandler(new Handler<AsyncResult<JsonObject>>() {
                public void handle(AsyncResult<JsonObject> asyncCommand) {
                    assertTrue(asyncCommand.succeeded());
                    assertNotNull(asyncCommand.result());
                    JsonObject command = asyncCommand.result();

                    assertNotNull("Invalid command json response", command);
                    assertEquals("Invalid status", "success", command.getString("status"));

                    JsonArray data = command.getJsonArray("data");
                    assertNotNull("Missing data", data);
                    assertEquals("Wrong number of results", 2, data.size());
                    assertEquals("Invalid data", "foo", data.getValue(0));
                    assertEquals("Invalid data", "bar", data.getValue(1));
                }
            });
            command.commandResponse(future);

            pendingCommands.add(command);

            Buffer buff = Buffer.buffer();
            buff.appendString("*2\r\n$3\r\nfoo\r\n$3\r\nbar\r\n");
            input.processBuffer(buff);

            assertEquals("Invalid buffer position", 0, bufferPosition.getInt(input));
            assertEquals("Invalid expected lines", 0, expectedLines.getInt(input));
            assertEquals("Invalid completed line count", 0, ((Collection<byte[]>) completedLines.get(input)).size());
        } catch (Exception ex) {
            assertNull("Unexpected exception: " + ex.getMessage(), ex);
        }
    }

    @Test
    public void testProcessMultiLineBufferForTransaction() throws Exception {
        RedisInputStream input = new RedisInputStream(pendingCommands);

        RedisCommand command1 = new RedisCommand(RedisCommandType.MULTI, null);
        RedisCommand command2 = createCommand(RedisCommandType.ZRANGE, new Object[]{"zsetname", 0, -1});
        RedisCommand command3 = new RedisCommand(RedisCommandType.EXEC, null);

        Future<JsonObject> future = Future.future();
        future.setHandler(new Handler<AsyncResult<JsonObject>>() {
            public void handle(AsyncResult<JsonObject> asyncCommand) {
                assertTrue(asyncCommand.succeeded());
                assertNotNull(asyncCommand.result());
                JsonObject command = asyncCommand.result();

                assertNotNull("Invalid command json response", command);
                assertEquals("Invalid status", "success", command.getString("status"));

                JsonArray data = command.getJsonArray("data");
                assertNotNull("Missing data", data);
                assertEquals("Wrong number of transaction results", 1, data.size());
                assertThat("ZRANGE result is wrong type", data.<Object>getValue(0), instanceOf(JsonArray.class));
                JsonArray zrangeResult = data.getJsonArray(0);
                assertEquals("Wrong number of zrange results", 2, zrangeResult.size());
                assertEquals("Invalid data", "foo", zrangeResult.getValue(0));
                assertEquals("Invalid data", "bar", zrangeResult.getValue(1));
            }
        });
        command3.commandResponse(future);

        pendingCommands.add(command1);
        pendingCommands.add(command2);
        pendingCommands.add(command3);

        Buffer buff = Buffer.buffer();
        buff.appendString("+OK\r\n+QUEUED\r\n*1\r\n*2\r\n$3\r\nfoo\r\n$3\r\nbar\r\n");
        input.processBuffer(buff);

        assertEquals("Invalid buffer position", 0, bufferPosition.getInt(input));
        assertEquals("Invalid expected lines", 0, expectedLines.getInt(input));
        assertEquals("Invalid completed line count", 0, ((Collection<byte[]>) completedLines.get(input)).size());
    }

    @Test
    public void testProcessMultiLineUnfinishedBuffer() {
        RedisInputStream input = new RedisInputStream(pendingCommands);

        try {
            RedisCommand command = createCommand(RedisCommandType.ZRANGE, new Object[]{"zsetname", 0, -1});

            Future<JsonObject> future = Future.future();
            future.setHandler(new Handler<AsyncResult<JsonObject>>() {
                public void handle(AsyncResult<JsonObject> asyncCommand) {
                    fail("Command response handler called unexpectedly");
                }
            });
            command.commandResponse(future);

            pendingCommands.add(command);

            Buffer buff = Buffer.buffer();
            buff.appendString("*2\r\n$3\r\nfoo\r\n$3\r\n");
            input.processBuffer(buff);

            assertEquals("Invalid buffer position", 0, bufferPosition.getInt(input));
            assertEquals("Invalid expected lines", 1, expectedLines.getInt(input));
            assertEquals("Invalid completed line count", 4, ((Collection<byte[]>) completedLines.get(input)).size());
        } catch (Exception ex) {
            assertNull("Unexpected exception: " + ex.getMessage(), ex);
        }
    }

    @Test
    public void testProcessInvalidLineBuffer() {
        RedisInputStream input = new RedisInputStream(pendingCommands);

        try {
            RedisCommand command = createCommand(RedisCommandType.ZRANGE, new Object[]{"zsetname", 0, -1});

            Future<JsonObject> future = Future.future();
            future.setHandler(new Handler<AsyncResult<JsonObject>>() {
                public void handle(AsyncResult<JsonObject> asyncCommand) {
                    fail("Command response handler called unexpectedly");
                }
            });
            command.commandResponse(future);

            pendingCommands.add(command);

            Buffer buff = Buffer.buffer();
            buff.appendString("$3\r\nfoo\r\n$4\r\n");
            input.processBuffer(buff);

            fail("Exception did not occur");
        } catch (Exception ex) {
            try {
                assertEquals("Invalid buffer position", 0, bufferPosition.getInt(input));
                assertEquals("Invalid expected lines", 0, expectedLines.getInt(input));
                assertEquals("Invalid completed line count", 1, ((Collection<byte[]>) completedLines.get(input)).size());
            } catch (Exception exc) {
                assertNull("Unexpected runtime exception", exc);
            }
        }
    }

    private RedisCommand createCommand(RedisCommandType type, Object[] arguments) {
        JsonObject commandJson = new JsonObject();
        commandJson.put("command", type.getCommand());
        commandJson.put("arguments", new JsonArray(Arrays.asList(arguments)));
        return new RedisCommand(commandJson);
    }
}


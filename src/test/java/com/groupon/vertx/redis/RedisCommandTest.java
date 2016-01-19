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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.Test;

/**
 * Tests for <code>RedisCommand</code>.
 *
 * @author Stuart Siegrist (fsiegrist at groupon dot com)
 * @since 1.0.0
 */
public class RedisCommandTest {

    @Test
    public void testValidCommandWithNoArguments() throws Exception {
        JsonObject object = new JsonObject();
        object.put("command", RedisCommandType.MULTI.getCommand());
        object.put("arguments", new JsonArray());

        RedisCommand command = new RedisCommand(object);

        assertEquals("Command type doesn't match", RedisCommandType.MULTI, command.getType());
        assertEquals("Commands don't match", command.getCommand(), RedisCommandType.MULTI.getCommand());
        assertEquals("Response type doesn't match", command.getResponseType(), RedisCommandType.MULTI.getResponseType());
        assertNotNull("Invalid arguments", command.getArguments());
        assertEquals("Wrong number of arguments", command.getArguments().size(), 0);
    }

    @Test
    public void testValidCommandWithSingleArgument() throws Exception {
        JsonObject object = new JsonObject();
        object.put("command", RedisCommandType.GET.getCommand());
        object.put("arguments", new JsonArray().add("somekey"));

        RedisCommand command = new RedisCommand(object);

        assertEquals("Command type doesn't match", RedisCommandType.GET, command.getType());
        assertEquals("Commands don't match", command.getCommand(), RedisCommandType.GET.getCommand());
        assertEquals("Response type doesn't match", command.getResponseType(), RedisCommandType.GET.getResponseType());
        assertNotNull("Invalid arguments", command.getArguments());
        assertEquals("Wrong number of arguments", command.getArguments().size(), 1);
        assertEquals("Arguments don't match", command.getArguments().get(0), "somekey");
    }

    @Test
    public void testValidCommandWithMultipleArguments() throws Exception {
        JsonObject object = new JsonObject();
        object.put("command", RedisCommandType.SET.getCommand());
        object.put("arguments", new JsonArray().add("somekey").add(12345));

        RedisCommand command = new RedisCommand(object);

        assertEquals("Command type doesn't match", RedisCommandType.SET, command.getType());
        assertEquals("Commands don't match", command.getCommand(), RedisCommandType.SET.getCommand());
        assertEquals("Response type doesn't match", command.getResponseType(), RedisCommandType.SET.getResponseType());
        assertNotNull("Invalid arguments", command.getArguments());
        assertEquals("Wrong number of arguments", command.getArguments().size(), 2);
        assertEquals("Arguments don't match", command.getArguments().get(0), "somekey");
        assertEquals("Arguments don't match", command.getArguments().get(1), "12345");
    }

    @Test
    public void testNullCommandJson() {
        try {
            RedisCommand command = new RedisCommand(null);
            assertNull("Exception should have been thrown", command);
        } catch (IllegalArgumentException ex) {
            // Success
            assertNotNull("Missing exception", ex);
        }
    }

    @Test
    public void testEmptyCommandJson() {
        try {
            RedisCommand command = new RedisCommand(new JsonObject());
            assertNull("Exception should have been thrown", command);
        } catch (IllegalArgumentException ex) {
            // Success
            assertNotNull("Missing exception", ex);
        }
    }

    @Test
    public void testInvalidCommandJson() {
        try {
            RedisCommand command = new RedisCommand(new JsonObject().put("hello", "world").put("foo", "bar"));
            assertNull("Exception should have been thrown", command);
        } catch (IllegalArgumentException ex) {
            // Success
            assertNotNull("Missing exception", ex);
        }
    }

    @Test
    public void testInvalidCommandInJson() {
        try {
            RedisCommand command = new RedisCommand(new JsonObject().put("command", "bogus").put("arguments", new JsonArray()));
            assertNull("Exception should have been thrown", command);
        } catch (IllegalArgumentException ex) {
            // Success
            assertNotNull("Missing exception", ex);
        }
    }
}

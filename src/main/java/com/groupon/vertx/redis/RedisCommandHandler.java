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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetSocket;

import com.groupon.vertx.utils.Logger;


/**
 * This handler listens for messages and sends commands to the Redis server.
 *
 * @author Stuart Siegrist (fsiegrist at groupon dot com)
 * @since 1.0.0
 */
public class RedisCommandHandler implements Handler<Message<JsonObject>> {
    private static final Logger log = Logger.getLogger(RedisCommandHandler.class);
    private final RedisSocket socket;

    /**
     * This handler listens for messages and sends commands to the Redis server.  The response
     * from the handler will be a JsonObject with the JSend format:
     * <br>
     * <code>
     * {
     *   'status': 'success',
     *   'data': null
     * }
     * </code>
     * <br>
     * or
     * <br>
     * <code>
     * {
     *   'status': 'fail',
     *   'data': {
     *     'command': 'GET',
     *     'arguments': 'somekey'
     *   }
     * }
     * </code>
     * <br>
     * or
     * <br>
     * <code>
     * {
     *   'status': 'error',
     *   'message': 'A server error occured'
     * }
     * </code>
     *
     * @param socket - The NetSocket which is currently connected to the Redis server.
     */
    public RedisCommandHandler(NetSocket socket) {
        this.socket = new RedisSocket(socket);
    }

    /**
     * This handles the incoming Redis command JSON.
     *
     * @param command - The JsonObject containing the commands to send to Redis.
     */
    public void handle(final Message<JsonObject> command) {
        if (command.body() == null || command.body().size() == 0) {
            log.warn("handleCommand", "failure", new String[]{"reason"}, "Missing message body");
            command.reply(buildReply("error", null, "Invalid message with null or empty."));
            return;
        }

        JsonObject inputJson = command.body();
        boolean isMulti = inputJson.getBoolean("isTransaction", false);
        JsonArray commands = inputJson.getJsonArray("commands", new JsonArray());
        if (commands.size() > 0) {
            LinkedList<RedisCommand> transactionRedisCommands = new LinkedList<>();
            for (Object jsonCommand : commands) {
                RedisCommand redisCommand = getRedisCommand((JsonObject) jsonCommand, command, isMulti);
                if (redisCommand == null) {
                    log.warn("handleCommand", "failure", new String[]{"reason"}, "Invalid redis command");
                    command.reply(buildReply("error", null, "Invalid redis command"));
                    return;
                }
                transactionRedisCommands.add(redisCommand);
            }
            if (isMulti) { //Wrap it with a  MULTI and EXEC block
                transactionRedisCommands.addFirst(new RedisCommand(RedisCommandType.MULTI, null));
                transactionRedisCommands.addLast(new RedisCommand(RedisCommandType.EXEC, null));
                setCommandResponseHandler(Collections.singletonList(transactionRedisCommands.getLast()), command, isMulti);
            } else {
                setCommandResponseHandler(transactionRedisCommands, command, isMulti);
            }
            socket.sendCommand(transactionRedisCommands);
        } else {
            log.warn("handleCommand", "failure", new String[]{"reason"}, "Missing commands");
            command.reply(buildReply("error", null, "Invalid message with no commands"));
        }
    }

    public void finish() {
        try {
            socket.close();
        } catch (Exception ex) {
            log.error("reset", "exception", "closingSocket", ex);
        }
    }

    private JsonObject buildReply(String status, JsonObject data, String message) {
        JsonObject reply = new JsonObject();

        reply.put("status", status);

        if ("success".equals(status) || data != null) {
            reply.putNull("data");
        } else {
            reply.put("message", message);
        }

        return reply;
    }

    private RedisCommand getRedisCommand(JsonObject jsonCommand, final Message<JsonObject> command, final boolean isMulti) {
        RedisCommand redisCommand = null;
        try {
            redisCommand = new RedisCommand(jsonCommand);
            log.trace("handleCommand", "createCommand", new String[]{"command", "isMulti"}, jsonCommand.encode(), isMulti);
        } catch (Exception ex) {
            log.error("handleCommand", "exception", "unknown", ex);
            command.reply(buildReply("error", null, ex.getMessage()));
        }
        return redisCommand;
    }

    private void setCommandResponseHandler(final List<RedisCommand> redisCommands, final Message<JsonObject> command, final boolean isMulti) {
        for (final RedisCommand redisCommand : redisCommands) {
            final Future<JsonObject> finalResult = Future.future();
            finalResult.setHandler(commandResponse -> {
                log.trace("handleCommand", "reply", new String[]{"command", "response", "isMulti"}, redisCommand.toString(), commandResponse, isMulti);
                if (commandResponse.succeeded()) {
                    command.reply(commandResponse.result());
                } else {
                    String cause = commandResponse.cause() != null ? commandResponse.cause().getMessage() : "unknown";
                    command.reply(buildReply("error", null, cause));
                }
            });
            redisCommand.commandResponse(finalResult);
        }
    }
}

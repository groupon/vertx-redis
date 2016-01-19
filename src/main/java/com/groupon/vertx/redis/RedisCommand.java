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
import java.util.List;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import com.groupon.vertx.utils.Logger;

/**
 * This class encapsulates the information necessary to send a command to the Redis server.
 *
 * @author Stuart Siegrist (fsiegrist at groupon dot com)
 * @since 1.0.0
 */
public class RedisCommand {
    private static final Logger log = Logger.getLogger(RedisCommand.class);
    private RedisCommandType type = null;
    private List<String> arguments = new ArrayList<>();
    private Future<JsonObject> commandResponse = null;
    private Future<JsonObject> clientCommandResponse = null;

    /**
     * If the command represented by the JsonObject doesn't come in the form:
     * <br>
     * <code>
     * {
     *   'command': 'GET',
     *   'arguments': [ 'somekey' ]
     * }
     * </code>
     * <br>
     * Then an exception will be thrown.
     *
     * @param commandJson - The command to be created.
     */
    public RedisCommand(JsonObject commandJson) {
        if (commandJson == null || commandJson.size() != 2) {
            log.warn("initRedisCommand", "failure", new String[]{"reason"}, "Invalid command format");
            throw new IllegalArgumentException("Invalid command format");
        }

        try {
            type = RedisCommandType.valueOf(commandJson.getString("command"));
        } catch (Exception ex) {
            log.warn("initRedisCommand", "failure", new String[]{"reason"}, "Invalid command");
            throw new IllegalArgumentException("Invalid or unsupported command provided");
        }

        Object objectArguments = commandJson.getValue("arguments");
        if (objectArguments != null) {
            if (objectArguments instanceof JsonArray) {
                for (Object arg : (JsonArray) objectArguments) {
                    if (arg instanceof String) {
                        arguments.add((String) arg);
                    } else {
                        arguments.add(arg.toString());
                    }
                }
            } else {
                arguments.add(objectArguments.toString());
            }
        }
    }

    public RedisCommand(RedisCommandType type, List<String> arguments) {
        if (type == null) {
            log.warn("initRedisCommand", "failure", new String[]{"reason"}, "Invalid command");
            throw new IllegalArgumentException("Invalid command");
        }

        this.type = type;

        if (arguments != null) {
            this.arguments = arguments;
        }
    }

    /**
     * The RedisCommandType enum which represents the command type being sent to Redis.
     *
     * @return - A RedisCommandType representing the current command.
     */
    public RedisCommandType getType() {
        return type;
    }

    /**
     * The String name for the command.
     *
     * @return - A String containing the name for the Redis command.
     */
    public String getCommand() {
        return type.getCommand();
    }

    /**
     * The list of arguments for the command.  If no arguments are provided it will
     * return an empty list.
     *
     * @return - An List of arguments.
     */
    public List<String> getArguments() {
        return arguments;
    }

    /**
     * This specifies the type of reply we expect from the Redis server.  We store the
     * possible results in the RedisResponseType enum.
     *
     * @return - The reply type for this command.
     */
    public RedisResponseType getResponseType() {
        return type.getResponseType();
    }

    /**
     * Renders the command into a JsonObject for transport across the event bus.
     *
     * @return - A JsonObject containing the command.
     */
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("command", type.getCommand());

        JsonArray arrayArgs = new JsonArray();
        for (String arg : arguments) {
            arrayArgs.add(arg);
        }
        jsonObject.put("arguments", arrayArgs);

        return jsonObject;
    }

    /**
     * Renders the command into a String for debug purpose
     *
     * @return - A String containing the command.
     */
    public String toString() {
        return toJson().encode();
    }


    /**
     * Calling this method will execute the handler associate with this command.  If no
     * handler is specified the response will be ignored.
     *
     * @param response - The Redis response for this command.
     */
    protected void setResponse(JsonObject response) {
        if (commandResponse != null) {
            commandResponse.complete(response);
        } else {
            log.warn("setResponse", "missingHandler", new String[]{"commandType"}, type.name());
        }
    }

    /**
     * This handler will be executed when the response has been received from Redis.
     *
     * @param pCommandResponse - A future for the JsonObject Redis response.
     */
    protected void commandResponse(Future<JsonObject> pCommandResponse) {
        this.commandResponse = pCommandResponse;
    }

    /**
     * This future will be completed when the response has been received from Redis for a transaction command.
     *
     * @param clientCommandResponse - A future for the JsonObject Redis response.
     */
    protected void setClientCommandResponse(Future<JsonObject> clientCommandResponse) {
        this.clientCommandResponse = clientCommandResponse;
    }

    /**
     * Get clientResponse.
     *
     * @return - The client response result.
     */
    protected Future<JsonObject> getClientCommandResponse() {
        return clientCommandResponse;
    }
}

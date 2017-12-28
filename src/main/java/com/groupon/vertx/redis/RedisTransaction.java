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

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Returns a stateful command client of type RedisCommandTransaction.
 *
 * @author Namrata Lele (nlele at groupon dot com)
 * @since 1.0.0
 */
final class RedisTransaction extends RedisBaseClient implements  RedisCommandTransaction {
    private final Queue<RedisCommand> pendingCommands;

    RedisTransaction(EventBus eventBus, String eventBusAddress, long timeout) {
        super(eventBus, eventBusAddress, timeout);
        this.pendingCommands = new LinkedList<>();
    }

    public void discard() {
        pendingCommands.clear();
    }

    public Future<JsonObject> exec() {
        final Future<JsonObject> finalResult = Future.future();
        if (!pendingCommands.isEmpty()) {
            JsonArray commands = new JsonArray();
            final List<Future<JsonObject>> clientCommandResponses = new ArrayList<>();
            clientCommandResponses.add(finalResult);

            RedisCommand command = pendingCommands.poll();
            while (command != null) {
                clientCommandResponses.add(command.getClientCommandResponse());
                commands.add(command.toJson());
                command = pendingCommands.poll();
            }

            JsonObject transactionCommands = new JsonObject();
            transactionCommands.put("isTransaction", true);
            transactionCommands.put("commands", commands);
            final DeliveryOptions deliveryOptions = new DeliveryOptions().setSendTimeout(replyTimeout);
            eventBus.send(eventBusAddress, transactionCommands, deliveryOptions, new Handler<AsyncResult<Message<JsonObject>>>() {
                @Override
                public void handle(AsyncResult<Message<JsonObject>> messageAsyncResult) {
                    JsonObject response;
                    if (messageAsyncResult.failed()) {
                        response = new JsonObject().put("status", "error").put("code", HttpURLConnection.HTTP_GATEWAY_TIMEOUT);
                    } else {
                        response = messageAsyncResult.result().body();
                    }

                    int index = 0;
                    executeResponse(clientCommandResponses.remove(0), response); // EXEC response
                    for (Future<JsonObject> clientCommandResponse : clientCommandResponses) {
                        if (clientCommandResponse != null) {
                            JsonObject result = constructTransactionCommandResult(response, index);
                            executeResponse(clientCommandResponse, result);
                        }
                        index++;
                    }
                }
            });
        } else {
            // Nothing to execute.
            finalResult.complete(null);
        }
        return finalResult;
    }

    private JsonObject constructTransactionCommandResult(JsonObject response, int index) {
        JsonArray responses = response.getJsonArray("data");
        if (responses != null && responses.size() > index) {
            Object commandResult = responses.getValue(index);
            JsonObject result = new JsonObject();
            result.put("status", response.getString("status"));
            if (commandResult instanceof JsonArray) {
                result.put("data", (JsonArray) commandResult);
            } else if (commandResult instanceof String) {
                result.put("data", (String) commandResult);
            } else if (commandResult instanceof Number) {
                result.put("data", (Number) commandResult);
            }
            return result;
        }
        return  response;
    }

    private void executeResponse(Future<JsonObject> response, JsonObject result) {
        if (response != null) {
            response.complete(result);
        }
    }

    @Override
    protected Future<JsonObject> sendCommand(RedisCommand command) {
        final Future<JsonObject> finalResult = Future.future();
        command.setClientCommandResponse(finalResult);
        pendingCommands.add(command);
        return finalResult;
    }
}

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

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Returns a stateful command client of type RedisCommandClient.
 *
 * @author Namrata Lele (nlele at groupon dot com)
 * @since 1.0.0
 */
public final class RedisClient extends RedisBaseClient implements  RedisCommandClient {
    public RedisClient(EventBus eventBus, String eventBusAddress, long timeout) {
        super(eventBus, eventBusAddress, timeout);
    }

    public RedisCommandTransaction multi() {
        return new RedisTransaction(eventBus, eventBusAddress, replyTimeout);
    }

    @Override
    protected Future<JsonObject> sendCommand(RedisCommand command) {
        final Future<JsonObject> finalResult = Future.future();
        final DeliveryOptions deliveryOptions = new DeliveryOptions().setSendTimeout(replyTimeout);
        eventBus.send(eventBusAddress, new JsonObject().put("commands", new JsonArray().add(command.toJson())), deliveryOptions, new Handler<AsyncResult<Message<JsonObject>>>() {
            @Override
            public void handle(AsyncResult<Message<JsonObject>> messageAsyncResult) {
                if (messageAsyncResult.succeeded() && messageAsyncResult.result() != null) {
                    finalResult.complete(messageAsyncResult.result().body());
                } else {
                    RedisCommandException exception;
                    if (messageAsyncResult.cause() != null) {
                        String errorMessage;
                        final Throwable cause = messageAsyncResult.cause();
                        if (cause instanceof ReplyException) {
                            errorMessage = createErrorJson(((ReplyException) cause).failureType().name());
                        } else {
                            errorMessage = createErrorJson(cause.getMessage());
                        }
                        exception = new RedisCommandException(errorMessage);
                        exception.addSuppressed(messageAsyncResult.cause());
                    } else {
                        exception = new RedisCommandException(createErrorJson(HttpResponseStatus.INTERNAL_SERVER_ERROR.reasonPhrase()));
                    }
                    finalResult.fail(exception);
                }
            }
        });
        return finalResult;
    }

    private String createErrorJson(String message) {
        return new JsonObject()
                .put("status", "error")
                .put("code", HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
                .put("message", message)
                .encode();
    }
}

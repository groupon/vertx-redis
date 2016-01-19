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

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import com.groupon.vertx.utils.Logger;

/**
 * This handler opens the connection to the Redis server and registers the message handler on
 * success.  If the connection fails or is closed, it unregisters the handler and attempts to
 * reconnect.
 *
 * @author Stuart Siegrist (fsiegrist at groupon dot com)
 * @since 1.0.0
 */
public class RedisSocketHandler implements Handler<Long> {
    private static final Logger log = Logger.getLogger(RedisSocketHandler.class);
    private static final long MAXIMUM_DELAY = 60000;

    private Vertx vertx;
    private String eventBusAddress;
    private String host;
    private int port;
    private NetClient netClient;
    private long delayFactor;
    private long currentDelay;

    public RedisSocketHandler(
            Vertx vertx,
            String eventBusAddress,
            String host,
            int port,
            NetClient netClient,
            long delayFactor) {
        this.vertx = vertx;
        this.eventBusAddress = eventBusAddress;
        this.host = host;
        this.port = port;
        this.netClient = netClient;
        this.delayFactor = delayFactor;
        this.currentDelay = delayFactor;
    }

    public void handle(Long time) {
        final Handler<Long> currentHandler = this;
        netClient.connect(port, host, socket -> {
            if (socket.succeeded()) {
                log.trace("establishSocket", "success");

                currentDelay = delayFactor;

                final NetSocket netSocket = socket.result();
                final RedisCommandHandler redisHandler = new RedisCommandHandler(netSocket);

                final MessageConsumer<JsonObject> consumer = vertx.eventBus().consumer(eventBusAddress, redisHandler);

                netSocket.exceptionHandler(ex -> {
                    log.error("establishSocket", "exception", "unknown", ex);
                    consumer.unregister();
                    redisHandler.finish();
                });

                netSocket.closeHandler(message -> {
                    log.warn("establishSocket", "socketClosed");
                    consumer.unregister();
                    redisHandler.finish();
                    vertx.setTimer(currentDelay, currentHandler);
                });

            } else {
                if (socket.result() != null) {
                    log.warn("establishSocket", "closeSocket");
                    socket.result().close();
                }
                currentDelay = Math.min(currentDelay * 2, MAXIMUM_DELAY);

                log.warn("establishSocket", "failed", new String[] {"eventBusAddress", "server"}, eventBusAddress, host);

                vertx.setTimer(currentDelay, currentHandler);
            }
        });
    }
}

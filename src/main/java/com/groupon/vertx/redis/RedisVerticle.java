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

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetClient;

import com.groupon.vertx.utils.Logger;

/**
 * This launches the handlers to listen for Redis requests.
 *
 * @author Stuart Siegrist (fsiegrist at groupon dot com)
 * @since 1.0.0
 */
public class RedisVerticle extends AbstractVerticle implements RedisKeys {
    private static final Logger log = Logger.getLogger(RedisVerticle.class);

    @Override
    public void start(Future<Void> startFuture) {
        log.info("start", "initializationStarted");

        RedisConfig redisConfig;

        JsonObject redisConfigObj = vertx.getOrCreateContext().config().getJsonObject(REDIS_KEY);
        if (redisConfigObj == null) {
            log.error("start", "exception", "No Redis config found.");
            startFuture.fail(new Exception("No Redis config found."));
            return;
        } else {
            try {
                redisConfig = new RedisConfig(redisConfigObj);
            } catch (Exception ex) {
                log.error("start", "exception", "Invalid Redis config defined");
                startFuture.fail(new Exception("Invalid Redis config defined"));
                return;
            }
        }

        NetClient netClient = vertx.createNetClient();
        establishSockets(redisConfig, netClient);

        log.info("start", "initializationCompleted");
        startFuture.complete(null);
    }

    /**
     * This method opens the connection to the Redis server and registers the message handler on
     * success.  If the connection fails or is closed, it unregisters the handler and attempts to
     * reconnect.
     *
     * @param redisConfig - The configuration for the connection to Redis
     * @param netClient   - The client for connecting to Redis.
     */
    private void establishSockets(final RedisConfig redisConfig, final NetClient netClient) {
        RedisSocketHandler handler = new RedisSocketHandler(vertx, redisConfig.getEventBusAddress(),
                redisConfig.getHost(), redisConfig.getPort(), netClient, redisConfig.getRetryInterval());
        handler.handle(System.currentTimeMillis());
    }
}

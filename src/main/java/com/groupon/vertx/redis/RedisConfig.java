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

import io.vertx.core.json.JsonObject;

/**
 * An object representing the required config for Redis.
 *
 * @author Stuart Siegrist (fsiegrist at groupon dot com)
 * @since 1.0.0
 */
public class RedisConfig implements RedisKeys {
    private static final int DEFAULT_REPLY_TIMEOUT = 1000;
    private static final int DEFAULT_PORT = 6379;
    private static final long DEFAULT_RETRY_INTERVAL = 50;

    private String eventBusAddress;
    private String host;
    private int port = DEFAULT_PORT;
    private long retryInterval = DEFAULT_RETRY_INTERVAL;
    private long replyTimeout = DEFAULT_REPLY_TIMEOUT;

    private RedisConfig() { }

    public RedisConfig(JsonObject redisConfigObj) throws Exception {
        this.host = redisConfigObj.getString(HOST_KEY);
        this.port = redisConfigObj.getInteger(PORT_KEY, port);
        this.eventBusAddress = redisConfigObj.getString(EVENT_BUS_ADDRESS_KEY);
        this.retryInterval = redisConfigObj.getLong(RETRY_INTERVAL_KEY, retryInterval);
        this.replyTimeout = redisConfigObj.getLong(REPLY_TIMEOUT_KEY, replyTimeout);

        if (host == null || host.isEmpty() || eventBusAddress == null || eventBusAddress.isEmpty()) {
            throw new Exception("Invalid Redis config.");
        }
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getEventBusAddress() {
        return eventBusAddress;
    }

    public long getRetryInterval() {
        return retryInterval;
    }

    public long getReplyTimeout() {
        return replyTimeout;
    }
}

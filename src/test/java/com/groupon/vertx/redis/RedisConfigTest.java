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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

/**
 * Tests for <code>RedisConfig</code>.
 *
 * @author Stuart Siegrist (fsiegrist at groupon dot com)
 * @since 1.0.0
 */
public class RedisConfigTest {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void testLoadWithValidJson() throws Exception {
        RedisConfig redisConfig = MAPPER.readValue("{\"host\":\"foo\",\"port\":1,\"eventBusAddress\":\"eventAddress\"}", RedisConfig.class);

        assertEquals("Host doesn't match", "foo", redisConfig.getHost());
        assertEquals("Port doesn't match", 1, redisConfig.getPort());
        assertEquals("EventBusAddress doesn't match", "eventAddress", redisConfig.getEventBusAddress());
    }
}

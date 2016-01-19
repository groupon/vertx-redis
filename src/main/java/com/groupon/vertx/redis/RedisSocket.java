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

import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetSocket;

import com.groupon.vertx.utils.Logger;

/**
 * This sends commands to the Redis server socket.
 *
 * @author Stuart Siegrist (fsiegrist at groupon dot com)
 * @since 1.0.0
 */
public class RedisSocket {
    private static final Logger log = Logger.getLogger(RedisSocket.class);
    private static final Charset ENCODING = Charset.forName("UTF-8");
    private static final byte ASTERISK = '*';
    private static final byte DOLLAR = '$';
    private final NetSocket socket;
    private final RedisOutputStream output;
    private final RedisInputStream input;
    private final ConcurrentLinkedQueue<RedisCommand> pendingCommands;

    public RedisSocket(final NetSocket socket) {
        this.socket = socket;
        this.output = new RedisOutputStream(socket);
        this.pendingCommands = new ConcurrentLinkedQueue<>();
        this.input = new RedisInputStream(pendingCommands);

        socket.handler(new Handler<Buffer>() {
            public void handle(Buffer buff) {
                try {
                    log.trace("handle", "beforeProcessBuffer");
                    input.processBuffer(buff);
                } catch (Exception ex) {
                    log.error("handle", "exception", "unknown", ex);
                    // Error processing the commands so close the socket.
                    socket.close();
                }
            }
        });
    }

    /**
     * This formats and writes the Redis command to the NetSocket.  Expected output
     * to the socket is:
     * <code>
     * '*{number of arguments}\r\n'
     * '${length of argument value}\r\n'
     * '{argument value}\r\n'
     * </code>
     * So for the command 'GET "somekey"' the output would be:
     * <code>
     * '*2\r\n$3\r\nGET\r\n$7\r\nsomekey\r\n'
     * </code>
     *
     * @param commands - The list of Redis commands
     */
    public void sendCommand(List<RedisCommand> commands) {
        for (RedisCommand command : commands) {
            byte[] commandBytes = command.getCommand().getBytes(ENCODING);
            output.write(ASTERISK);
            output.write(command.getArguments().size() + 1);
            output.writeCrlf();
            output.write(DOLLAR);
            output.write(commandBytes.length);
            output.writeCrlf();
            output.write(commandBytes);
            output.writeCrlf();

            for (String arg : command.getArguments()) {
                byte[] argBytes = arg.getBytes(ENCODING);
                output.write(DOLLAR);
                output.write(argBytes.length);
                output.writeCrlf();
                output.write(argBytes);
                output.writeCrlf();
            }
            pendingCommands.add(command);
            log.trace("sendCommand", "commandSent", new String[]{"command"}, command.getCommand());
        }
        output.flush();
    }

    public void close() {
        RedisCommand command = pendingCommands.poll();
        while (command != null) {
            command.setResponse(new JsonObject("{\"status\":\"error\",\"message\":\"Socket closed unexpectedly\"}"));
            command = pendingCommands.poll();
        }

        socket.close();
    }
}

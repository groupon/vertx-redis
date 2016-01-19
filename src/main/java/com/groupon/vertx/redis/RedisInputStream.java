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
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

import io.netty.buffer.ByteBuf;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import com.groupon.vertx.utils.Logger;

/**
 * The following code was based off of code from the Jedis library. The library can be found here:
 * <br>
 * https://github.com/xetorthio/jedis
 * <br>
 * The license is below:
 * <br>
 * Copyright (c) 2011 Jonathan Leibiusky
 * <br>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 * <br>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 * the Software.
 *
 * @author Stuart Siegrist (fsiegrist at groupon dot com)
 * @since 1.0.0
 */
public class RedisInputStream {
    private static final Logger log = Logger.getLogger(RedisInputStream.class);
    private static final Charset ENCODING = Charset.forName("UTF-8");
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private final ConcurrentLinkedQueue<RedisCommand> pendingCommands;
    private final byte[] buffer;
    private ConcurrentLinkedQueue<byte[]> completedLines = new ConcurrentLinkedQueue<>();
    private int bufferPosition = 0;
    private int expectedLines = 0;
    private boolean isInMulti = false;

    public RedisInputStream(ConcurrentLinkedQueue<RedisCommand> pendingCommands) {
        this(pendingCommands, DEFAULT_BUFFER_SIZE);
    }

    public RedisInputStream(ConcurrentLinkedQueue<RedisCommand> pendingCommands, int bufferSize) {
        this.pendingCommands = pendingCommands;
        this.buffer = new byte[bufferSize];
    }

    /**
     * This method handles processing the incoming Buffer from the NetSocket.  The Buffer
     * is not guaranteed to contain a whole message so this method tracks the current state
     * of the incoming data and notifies the pending commands when enough data has been sent
     * for a response.
     *
     * @param processBuffer - The Buffer containing the current set of bytes.
     */
    public void processBuffer(Buffer processBuffer) {
        if (processBuffer == null || processBuffer.length() == 0) {
            return;
        }

        byte first;
        byte second;

        ByteBuf byteBuf = processBuffer.getByteBuf();

        while (byteBuf.isReadable()) {
            first = byteBuf.readByte();
            if (first == '\r' && byteBuf.isReadable()) {
                second = byteBuf.readByte();
                if (second == '\n') {
                    byte[] line = new byte[bufferPosition];
                    System.arraycopy(buffer, 0, line, 0, line.length);
                    addCompletedLine(line);
                } else {
                    buffer[bufferPosition++] = first;
                    buffer[bufferPosition++] = second;
                }
            } else if (first == '\n' && buffer[bufferPosition - 1] == '\r') {
                byte[] line = new byte[bufferPosition - 1];
                System.arraycopy(buffer, 0, line, 0, line.length);
                addCompletedLine(line);
            } else {
                buffer[bufferPosition++] = first;
            }
        }
    }

    /**
     * This method is fired when enough data is in the Buffer to complete a command.  If the
     * command does not match the signature of the buffered data then an exception is thrown
     * and the socket should be closed as the command/response queues are no longer in sync.
     *
     * @param command - The command to process from the response buffer.
     */
    private void processCommand(RedisCommand command) {
        if (command == null) {
            // No command to process so return.  Should add log message here.
            log.warn("processCommand", "noCommandFound");
            return;
        }

        JsonObject response = new JsonObject();

        byte[] line = completedLines.poll();

        if (line == null) {
            log.warn("processCommand", "noCompletedLinesFound", new String[]{"command"}, command.getCommand());
            response.put("status", "error");
            response.put("message", "Unable to find completed line for command: " + command.getCommand());
        } else if (line[0] == RedisResponseType.ERROR.marker) {
            log.warn("processCommand", "redisReturnedError", new String[]{"command"}, command.getCommand());
            response.put("status", "fail");
            response.put("data", processLine(line));
        } else if (line[0] == RedisResponseType.BULK_REPLY.marker && line[1] == '-') {
            log.debug("processCommand", "redisReturnedNil", new String[]{"command"}, command.getCommand());
            response.put("status", "success");
            response.put("data", processBulkLine(line));
        } else if (line[0] != command.getResponseType().marker) {
            log.warn("processCommand", "mismatchedResponse", new String[]{"command", "expectedDelim", "foundDelim"},
                    command.getCommand(), (char) command.getResponseType().marker, (char) line[0]);
            throw new RedisCommandException("Invalid response found");
        } else {
            response.put("status", "success");

            if (command.getResponseType() == RedisResponseType.MULTI_BULK_REPLY) {
                response.put("data", processMultiLine(line));
            } else if (command.getResponseType() == RedisResponseType.BULK_REPLY) {
                response.put("data", processBulkLine(line));
            } else if (command.getResponseType() == RedisResponseType.INTEGER_REPLY) {
                response.put("data", processIntegerLine(line));
            } else {
                response.put("data", processLine(line));
            }

            log.trace("processCommand", "redisCommandSuccess", new String[]{"command", "data"}, command.getCommand(),
                    response.getValue("data"));
        }

        command.setResponse(response);
    }

    /**
     * When the crlf sequence has been received from the Buffer it is time to check if we
     * have enough data to complete a command and clear the line off of the current buffer.
     *
     * @param line - A byte[] representing a complete line which is terminated by a '\r\n'.
     */
    private void addCompletedLine(byte[] line) {
        if (expectedLines > 0) {
            expectedLines--;
        }

        if (line[0] == RedisResponseType.MULTI_BULK_REPLY.marker) {
            if (line[1] != '-') {
                expectedLines += processIntegerLine(line);
            }
        } else if (line[0] == RedisResponseType.BULK_REPLY.marker) {
            if (line[1] != '-') {
                expectedLines++;
            }
        }

        completedLines.add(line);
        bufferPosition = 0;

        if (expectedLines == 0 && pendingCommands.size() > 0) {
            //For a transaction we want to discard all but the last reply.
            RedisCommand pendingCommand = pendingCommands.poll();
            setIfInMultiMode(pendingCommand);
            if (isInMulti) {
                completedLines.poll();
            } else {
                processCommand(pendingCommand);
            }
        }
    }

    /**
     * Sets the isInMulti boolean when we are executing a transaction
     *
     * @param pendingCommand
     */
    private void setIfInMultiMode(RedisCommand pendingCommand) {
        if (pendingCommand.getType().equals(RedisCommandType.MULTI)) {
            isInMulti = true;
        } else if (pendingCommand.getType().equals(RedisCommandType.EXEC)) {
            isInMulti = false;
        }
    }

    /**
     * Single line responses are always defined by the marker in the first byte followed by the message.
     *
     * @param line
     * @return String
     */
    private String processLine(byte[] line) {
        String processed = new String(Arrays.copyOfRange(line, 1, line.length), ENCODING);
        log.trace("processLine", "success", new String[]{"line"}, processed);
        return processed;
    }

    /**
     * Integer replies are always defined by the marker in the first byte followed by the message.
     *
     * @param line
     * @return Integer
     */
    private Integer processIntegerLine(byte[] line) {
        return Integer.parseInt(processLine(line));
    }

    /**
     * The multi-line responses will be an array containing all of the subsequent responses for the
     * multi-line response.
     *
     * @param multiLine
     * @return JsonArray
     */
    private JsonArray processMultiLine(byte[] multiLine) {
        if (multiLine[1] == '-') {
            return null;
        }

        JsonArray result = new JsonArray();

        int lines = processIntegerLine(multiLine);
        while (lines > 0) {
            byte[] line = completedLines.poll();
            if (line == null) {
                // Unable to find completed line for multi command
                result.addNull();
            } else if (line[0] == RedisResponseType.MULTI_BULK_REPLY.marker) {
                result.add(processMultiLine(line));
            } else if (line[0] == RedisResponseType.BULK_REPLY.marker) {
                result.add(processBulkLine(line));
            } else if (line[0] == RedisResponseType.INTEGER_REPLY.marker) {
                result.add(processIntegerLine(line));
            } else {
                result.add(processLine(line));
            }

            lines--;
        }

        return result;
    }

    /**
     * The bulk reply is composed of two lines.  The first contains the size of the response and the
     * second line contains the value.
     *
     * @param bulkLine
     * @return String
     */
    private String processBulkLine(byte[] bulkLine) {
        if (bulkLine[1] == '-') {
            return null;
        }

        byte[] line = completedLines.poll();
        if (line == null) {
            // Unable to find completed line for bulk command
            return null;
        } else {
            return new String(Arrays.copyOfRange(line, 0, line.length), ENCODING);
        }
    }
}

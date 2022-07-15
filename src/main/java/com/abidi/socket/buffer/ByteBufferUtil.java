package com.abidi.socket.buffer;

import java.nio.ByteBuffer;

public class ByteBufferUtil {

    private static final ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);

    public static byte[] longToBytes(long x) {
        buffer.putLong(0, x);
        return buffer.array().clone();
    }

    public static long bytesToLong(byte[] msg) {
        buffer.position(0);
        buffer.put(msg, 0, msg.length);
        buffer.flip();
        return buffer.getLong();
    }
}
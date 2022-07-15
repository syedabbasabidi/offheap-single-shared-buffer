package com.abidi.socket.buffer;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.IntStream.rangeClosed;

public class Messages {

    private final List<byte[]> msgs;
    private int index;
    private final int size;

    public Messages(int size) {
        msgs = new ArrayList<>(size);
        this.size = size;
    }

    public void init() {
        rangeClosed(1, size).forEach(value -> msgs.add(ByteBufferUtil.longToBytes(value)));
    }


    public byte[] nextMsg() {
        return msgs.get(index++);
    }

    public void addMsg(byte[] msg) {
        msgs.add(msg);
      //  System.out.println(ByteBufferUtil.bytesToLong(msg));
    }

    public long getSum() {
        return msgs.stream().mapToLong(ByteBufferUtil::bytesToLong).sum();
    }
}
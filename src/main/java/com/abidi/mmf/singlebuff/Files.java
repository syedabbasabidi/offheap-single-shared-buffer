package com.abidi.mmf.singlebuff;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class Files {

    public static final int SIZE = 100_000_000;
    private final String buffer = "/tmp/buffer.txt";
    private final String writer = "/tmp/writer-seqnum.txt";
    private final String reader = "/tmp/reader-seqnum.txt";
    private final RandomAccessFile sharedBuffer;
    private final RandomAccessFile readerSeqNum;
    private final RandomAccessFile writerSeqNum;

    public Files() throws FileNotFoundException {
        sharedBuffer = new RandomAccessFile(buffer, "rw");
        readerSeqNum = new RandomAccessFile(reader, "rw");
        writerSeqNum = new RandomAccessFile(writer, "rw");
    }

    public MappedByteBuffer buffer() throws IOException {
        return sharedBuffer.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, 8);
    }

    public MappedByteBuffer getConsumerContext() throws IOException {
        return readerSeqNum.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, 8);
    }

    public MappedByteBuffer getProducerContext() throws IOException {
        return writerSeqNum.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, 8);
    }

    public void close() throws IOException {
        sharedBuffer.close();
        readerSeqNum.close();
        writerSeqNum.close();
    }

    public void reset() throws IOException {
        sharedBuffer.setLength(0);
        readerSeqNum.setLength(0);
        writerSeqNum.setLength(0);

    }
}

package com.abidi.mmf.singlebuff;

import java.nio.MappedByteBuffer;

public class MMFileReader {

    public static long val;
    private volatile int readerFlush;
    private long arr[] = new long[Files.SIZE + 10_000];


    public static void main(String[] args) {

        new MMFileReader().reader();
    }

    private void reader() {

        try {

            Files files = new Files();
            MappedByteBuffer dataBuffer = files.buffer();
            MappedByteBuffer producerContext = files.getProducerContext();
            MappedByteBuffer consumerContext = files.getConsumerContext();

            long lastProducerSeqNum = 0;
            long startTime = System.nanoTime();
            int data = 1;
            do {

                lastProducerSeqNum = checkIfProducerHasWrittenNewMessage(dataBuffer, producerContext, consumerContext, lastProducerSeqNum);
                val = dataBuffer.getLong();
                arr[data] = val;
                consumerContext.putLong(data++);
                this.readerFlush = data;
                printStatus(data);

            } while (!allMessagesRead(startTime, data));

            files.close();
        }
        catch (Exception exp) {
            System.out.println(exp);
        }
    }

    private boolean allMessagesRead(long startTime, int data) {
        if (data >= Files.SIZE) {
            System.out.println("Completed in " + ((System.nanoTime() - startTime) / 1_000_000) + " ms");
            return true;
        }
        return false;
    }

    private long checkIfProducerHasWrittenNewMessage(MappedByteBuffer buffer, MappedByteBuffer producerBuffer, MappedByteBuffer consumerBuffer, long lastProducerSeqNum) {
        while (true) {
            val = this.readerFlush;
            long producerSeqNumber = producerBuffer.getLong();
            producerBuffer.rewind();
            if (producerSeqNumber != 0 && producerSeqNumber > lastProducerSeqNum) {
                lastProducerSeqNum = producerSeqNumber;
                buffer.rewind();
                consumerBuffer.rewind();
                break;
            }
        }
        return lastProducerSeqNum;
    }

    public long[] getArr() {
        return arr;
    }

    private void printStatus(int msgCount) {
        if (msgCount % 1_000_000 == 0) {
            System.out.println("Read " + msgCount / 1_000_000 + "M messages");
        }
    }
}
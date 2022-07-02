package com.abidi.mmf.singlebuff;

import java.nio.MappedByteBuffer;

public class MMapFileWriter {

    public static long val;
    private volatile int producerFlush;

    public static void main(String[] args) {
        new MMapFileWriter().writer();
    }

    private void writer() {

        try {
            Files files = new Files();
            files.reset();
            MappedByteBuffer dataBuffer = files.buffer();
            MappedByteBuffer producerContext = files.getProducerContext();
            MappedByteBuffer consumerContext = files.getConsumerContext();

            int data = 1;
            long lastConsumedValue = 0;
            long startTime = System.nanoTime();

            do {

                dataBuffer.putLong(data);    //write data to shared buffer
                producerContext.putLong(data); //seq number that should be consumed by the reader
                this.producerFlush = data;  //ensure data is pushed to cache

                lastConsumedValue = hasConsumerRead(dataBuffer, producerContext, consumerContext, lastConsumedValue);
                data++;
                printStatus(data);

            } while (!allMessagesAreWritten(data, startTime));

            files.close();
        }
        catch (Exception exp) {
            System.out.println(exp);
        }
    }

    private long hasConsumerRead(MappedByteBuffer dataBuffer, MappedByteBuffer producerContext, MappedByteBuffer consumerBuffer, long lastConsumedValue) {

        while (true) { //busy wait until consumer consimes the messages
            val = this.producerFlush;  //read volatile to load the latest consumer stats
            long valueConsumed = consumerBuffer.getLong();  //if consumer has consumed it, it would increase it's counter for write to write new data
            consumerBuffer.rewind();
            if (valueConsumed != 0 && valueConsumed > lastConsumedValue) { //ensure consumer value is read by checking seq num
                lastConsumedValue = valueConsumed;
                dataBuffer.rewind();
                producerContext.rewind();
                break;
            }
        }
        return lastConsumedValue;
    }

    private boolean allMessagesAreWritten(int data, long startTime) {
        if (data >= Files.SIZE) {
            System.out.println("Completed in " + ((System.nanoTime() - startTime) / 1_000_000) + " ms");
            return true;
        }
        return false;
    }

    private void printStatus(int msgCount) {
        if (msgCount % 1_000_000 == 0) {
            System.out.println("Written " + msgCount / 1_000_000 + "M messages");
        }
    }
}
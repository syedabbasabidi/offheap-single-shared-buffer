package com.abidi.mmf.singlebuff;


import net.openhft.chronicle.jlbh.JLBH;
import net.openhft.chronicle.jlbh.JLBHOptions;
import net.openhft.chronicle.jlbh.JLBHTask;

import java.nio.MappedByteBuffer;

public class MMFWriterBenchmark implements JLBHTask {

    private JLBH lth;
    private MappedByteBuffer producerContext;
    private MappedByteBuffer consumerContext;
    private MappedByteBuffer dataBuffer;
    private long seqNumberOfLastConsumedMsg;
    private int data = 1;
    private Files files;
    private MMapFileWriter writer;

    public static void main(String[] args) {

        JLBHOptions jlbhOptions = new JLBHOptions().throughput(100_000_000).recordOSJitter(false)
                .iterations(10_000_000 - 1000).runs(10).warmUpIterations(10_000)
                .jlbhTask(new MMFWriterBenchmark());

        new JLBH(jlbhOptions).start();
    }

    private void write(int data) {
        seqNumberOfLastConsumedMsg = writer.writeAndWait(dataBuffer, producerContext, consumerContext, data, seqNumberOfLastConsumedMsg);
    }

    @Override
    public void run(long startTimeNS) {
        long start = System.nanoTime();
        write(data++);
        lth.sample(System.nanoTime() - start); //nanoTime() takes around 25ns on my machine
    }

    @Override
    public void init(JLBH jlbh) {
        try {

            lth = jlbh;
            files = new Files();
            files.reset();
            dataBuffer = files.buffer();
            producerContext = files.getProducerContext();
            consumerContext = files.getConsumerContext();
            writer = new MMapFileWriter();
            wait10Seconds();
            System.out.println("Starting writer benchmark...");
        }
        catch (Exception exp) {
            System.out.println("Unable to initialize JLBH test" + exp);
        }
    }

    @Override
    public void complete() {
        try {
            files.close();
        }
        catch (Exception exp) {
            System.out.println("Unable to initialize JLBH test" + exp);
        }
    }

    private void wait10Seconds() {
        try {
            Thread.sleep(10_000);
        }
        catch (Exception exp) {
            System.out.println("Initialize wait failed" + exp);
        }
    }

}
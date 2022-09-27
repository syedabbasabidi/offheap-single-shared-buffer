package com.abidi.socket.buffer;

import net.openhft.chronicle.jlbh.JLBH;
import net.openhft.chronicle.jlbh.JLBHOptions;
import net.openhft.chronicle.jlbh.JLBHTask;

import java.util.concurrent.atomic.AtomicInteger;

public class SocketWriterBenchmark implements JLBHTask {

    private JLBH lth;
    private SocketServerWriter socketServerWriter;
    private AtomicInteger msgCount = new AtomicInteger();

    public static void main(String[] args) {

        JLBHOptions jlbhOptions = new JLBHOptions().
                throughput(SocketServerWriter.SIZE).
                iterations(10_000_000 - 1_000).
                warmUpIterations(10_000).
                recordOSJitter(false).
                runs(10).jlbhTask(new SocketWriterBenchmark());

        new JLBH(jlbhOptions).start();
    }

    @Override
    public void init(JLBH jlbh) {
        this.lth = jlbh;
        this.socketServerWriter = new SocketServerWriter();
        socketServerWriter.startServer();
    }

    @Override
    public void run(long startTimeNS) {
        msgCount.getAndIncrement();
        long start = System.nanoTime();
        socketServerWriter.write(socketServerWriter.nextMsg(), msgCount.get());
        lth.sample(System.nanoTime() - start); //nanoTime() takes around 25ns on my machine
    }

}
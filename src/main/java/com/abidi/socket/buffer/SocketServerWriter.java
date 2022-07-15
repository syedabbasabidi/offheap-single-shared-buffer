package com.abidi.socket.buffer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import static java.lang.System.nanoTime;
import static java.util.stream.IntStream.rangeClosed;

public class SocketServerWriter {

    public static final int SIZE = 10_000_000;

    public static void main(String[] args) throws IOException {

        new SocketServerWriter().run();
    }


    private void run() throws IOException {

        Messages messages = new Messages(SIZE);
        messages.init();

        System.out.println("Socket server started ...");
        ServerSocket socketServer = new ServerSocket(9500);
        Socket clientSocket = socketServer.accept();
        OutputStream outputStream = clientSocket.getOutputStream();
        System.out.println("Client connected ...");

        long startTime = nanoTime();
        rangeClosed(1, SIZE).forEach(i -> write(messages.nextMsg(), outputStream, i));
        System.out.println("Completed writing in :" + (nanoTime() - startTime) / 1_000_000 + " ms");
    }

    private void write(byte[] b, OutputStream outputStream, int count) {
        try {
            outputStream.write(b);
            printStatus(count, b);
        }
        catch (Exception e) {
            System.out.println("Exception when writing to socket" + e);
        }
    }

    private void printStatus(int msgCount, final byte data[]) {
        if (msgCount % 1_000_000 == 0) {
            System.out.println("Written " + msgCount / 1_000_000 + "M messages,  last msg written is " +ByteBufferUtil.bytesToLong(data));
        }
    }
}
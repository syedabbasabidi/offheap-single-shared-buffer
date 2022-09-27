package com.abidi.socket.buffer;

import com.abidi.mmf.singlebuff.Files;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import static java.lang.System.nanoTime;
import static java.util.stream.IntStream.rangeClosed;

public class SocketServerWriter {

    public static final int SIZE = Files.SIZE;
    private Messages messages;
    private ServerSocket socketServer;
    private OutputStream outputStream;


    public static void main(String[] args) throws IOException {
        SocketServerWriter socketServerWriter = new SocketServerWriter();
        socketServerWriter.startServer();
        socketServerWriter.startWriting();
    }

    void startServer() {
        try {

            messages = new Messages(SIZE);
            messages.init();
            System.out.println("Socket server started ...");
            socketServer = new ServerSocket(9500);
            Socket clientSocket = socketServer.accept();
            outputStream = clientSocket.getOutputStream();
            System.out.println("Client connected ...");
        }
        catch (Exception exp) {
            System.out.println("Failed to start writer" + exp);
        }
    }

    private void startWriting() {
        long startTime = nanoTime();
        rangeClosed(1, SIZE).forEach(i -> write(nextMsg(), i));
        System.out.println("Completed writing in :" + (nanoTime() - startTime) / 1_000_000 + " ms");
    }

    byte[] nextMsg() {
        return messages.nextMsg();
    }

    void write(byte[] b, int count) {
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
            System.out.println("Written " + msgCount / 1_000_000 + "M messages,  last msg written is " + ByteBufferUtil.bytesToLong(data));
        }
    }

    public void stop() throws IOException {
        socketServer.close();
        outputStream.close();
    }
}
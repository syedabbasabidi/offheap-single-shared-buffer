package com.abidi.socket.buffer;

import com.abidi.mmf.singlebuff.Files;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import static java.lang.System.nanoTime;
import static java.util.stream.IntStream.rangeClosed;

public class SocketClientReader {

    public static final int SIZE = Files.SIZE;
    private final byte[] bytes = new byte[8];

    public static void main(String[] args) throws IOException {

        new SocketClientReader().run();
    }

    private void run() throws IOException {

        System.out.println("Starting Socket client...");

        Messages messageArray = new Messages(SIZE);
        Socket clientSocket = new Socket("127.0.0.1", 9500);
        InputStream inputStream = clientSocket.getInputStream();
        long startTime = nanoTime();

        rangeClosed(1, SIZE).forEach(i -> read(inputStream, i, messageArray));

        System.out.println("Completed Reading in :" + (nanoTime() - startTime) / 1_000_000 + " ms");
    }

    private void read(InputStream inputStream, int i, final Messages messages) {
        try {
            inputStream.read(bytes);
        //    messages.addMsg(bytes); uncomment to store received bytes
            printStatus(i, bytes);
        }
        catch (IOException e) {
            System.out.println("Failed to read from socket " + e);
        }
    }

    private void printStatus(int msgCount, final byte data[]) {
        if (msgCount % 1_000_000 == 0) {
            System.out.println("Read " + msgCount / 1_000_000 + "M messages, last msg was "+ ByteBufferUtil.bytesToLong(data));
        }
    }
}
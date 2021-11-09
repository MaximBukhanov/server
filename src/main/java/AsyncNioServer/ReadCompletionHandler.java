package AsyncNioServer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

public class ReadCompletionHandler implements CompletionHandler<Integer, Void> {
    private final AsynchronousSocketChannel socketChannel;
    private final ByteBuffer readBuffer;

    public ReadCompletionHandler(AsynchronousSocketChannel socketChannel, ByteBuffer readBuffer) {
        this.socketChannel = socketChannel;
        this.readBuffer = readBuffer;
    }

    @Override
    public void completed(Integer numRead, Void attachment) {
        System.out.println(Thread.currentThread() + " - Server read: " + numRead + " byte(s)");

        if (numRead == -1) {
            try {
                socketChannel.close();
                return;
            } catch (IOException e) {
                System.out.println("Connection close.");
                e.printStackTrace();
            }
        }

        readBuffer.flip();
        byte[] bytesRead = new byte[readBuffer.limit()];
        byte[] bytesResponse = " - Server response\n".getBytes(StandardCharsets.UTF_8);
        readBuffer.get(bytesRead);
        String message = new String(bytesRead, StandardCharsets.UTF_8);

        System.out.println(Thread.currentThread() + " - Received message:" + message);

        WriteCompletionHandler writeCompletionHandler = new WriteCompletionHandler(socketChannel);
        readBuffer.clear().limit(bytesRead.length + bytesResponse.length).put(bytesRead).put(bytesResponse);
        readBuffer.flip();
        socketChannel.write(readBuffer, null, writeCompletionHandler);
    }

    @Override
    public void failed(Throwable exc, Void attachment) {
        System.out.println("Error reading data.");
        exc.printStackTrace();
    }
}

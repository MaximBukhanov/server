package AsyncNioServer;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class WriteCompletionHandler implements CompletionHandler<Integer, Void> {
    private final AsynchronousSocketChannel asynchronousSocketChannel;

    public WriteCompletionHandler(AsynchronousSocketChannel asynchronousSocketChannel) {
        this.asynchronousSocketChannel = asynchronousSocketChannel;
    }

    @Override
    public void completed(Integer numWrite, Void attachment) {
        System.out.println(Thread.currentThread() + " - Server write: " + numWrite + " byte(s)\n");

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        ReadCompletionHandler readCompletionHandler = new ReadCompletionHandler(asynchronousSocketChannel, buffer);
        asynchronousSocketChannel.read(buffer, null, readCompletionHandler);
    }

    @Override
    public void failed(Throwable exc, Void attachment) {
        System.out.println("Data write error.");
        exc.printStackTrace();
    }
}

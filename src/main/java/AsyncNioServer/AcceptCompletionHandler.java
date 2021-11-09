package AsyncNioServer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, Void> {
    private final AsynchronousServerSocketChannel serverChannel;

    public AcceptCompletionHandler(AsynchronousServerSocketChannel serverChannel) {
        this.serverChannel = serverChannel;
    }

    @Override
    public void completed(AsynchronousSocketChannel asynchronousSocketChannel, Void attachment) {
        try {
            System.out.println(Thread.currentThread() + " - Connection: " + asynchronousSocketChannel.getRemoteAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
        serverChannel.accept(null, this);
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        ReadCompletionHandler readCompletionHandler = new ReadCompletionHandler(asynchronousSocketChannel, readBuffer);
        asynchronousSocketChannel.read(readBuffer, null, readCompletionHandler);
    }

    @Override
    public void failed(Throwable exc, Void attachment) {
        System.out.println("Connection error.");
        exc.printStackTrace();
    }
}

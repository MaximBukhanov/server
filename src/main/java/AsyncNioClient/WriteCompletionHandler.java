package AsyncNioClient;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class WriteCompletionHandler implements CompletionHandler<Integer, Attachment> {
    private final AsynchronousSocketChannel asynchronousSocketChannel;

    public WriteCompletionHandler(AsynchronousSocketChannel asynchronousSocketChannel) {
        this.asynchronousSocketChannel = asynchronousSocketChannel;
    }

    @Override
    public void completed(Integer numWrite, Attachment attachment) {
        System.out.println(Thread.currentThread().getName() + " - Client write: " + numWrite + " byte(s)");

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        ReadCompletionHandler readCompletionHandler = new ReadCompletionHandler(buffer);
        asynchronousSocketChannel.read(buffer, attachment, readCompletionHandler);
    }

    @Override
    public void failed(Throwable exc, Attachment attachment) {
        System.out.println("Data write error.");
        exc.printStackTrace();
    }
}

package AsyncNioClient;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

public class AcceptCompletionHandler implements CompletionHandler<Void, Attachment> {
    private final AsynchronousSocketChannel asynchronousSocketChannel;

    public AcceptCompletionHandler(AsynchronousSocketChannel asynchronousSocketChannel) {
        this.asynchronousSocketChannel = asynchronousSocketChannel;
    }

    @Override
    public void completed(Void result, Attachment attachment) {
        try {
            System.out.println(Thread.currentThread().getName() + " - Connection: " + asynchronousSocketChannel.getRemoteAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String message = attachment.getMessage();
        ByteBuffer writeBuffer = ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8));

        WriteCompletionHandler writeCompletionHandler = new WriteCompletionHandler(asynchronousSocketChannel);
        asynchronousSocketChannel.write(writeBuffer, attachment, writeCompletionHandler);
    }

    @Override
    public void failed(Throwable exc, Attachment attachment) {
        System.out.println("Connection error.");
        exc.printStackTrace();
    }
}

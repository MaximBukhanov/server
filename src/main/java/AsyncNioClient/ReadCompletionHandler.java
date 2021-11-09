package AsyncNioClient;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

public class ReadCompletionHandler implements CompletionHandler<Integer, Attachment> {

    private final ByteBuffer readBuffer;

    public ReadCompletionHandler(ByteBuffer readBuffer) {
        this.readBuffer = readBuffer;
    }

    @Override
    public void completed(Integer numRead, Attachment attachment) {
        System.out.println(Thread.currentThread().getName() + " - Client read: " + numRead + " byte(s)");

        readBuffer.flip();
        System.out.println(Thread.currentThread().getName() + " - Received message: " + StandardCharsets.UTF_8.decode(readBuffer));
    }

    @Override
    public void failed(Throwable exc, Attachment attachment) {
        System.out.println("Error reading data.");
        exc.printStackTrace();
    }
}

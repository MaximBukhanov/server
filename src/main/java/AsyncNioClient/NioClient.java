package AsyncNioClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousSocketChannel;

public class NioClient {

    static final int PORT = 9090;

    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String message;

        while ((message = in.readLine()) != null) {
            AsynchronousSocketChannel asynchronousSocketChannel = AsynchronousSocketChannel.open();
            asynchronousSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 1024);
            asynchronousSocketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 1024);
            asynchronousSocketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

            Attachment attachment = new Attachment(message);
            AcceptCompletionHandler acceptCompletionHandler = new AcceptCompletionHandler(asynchronousSocketChannel);
            asynchronousSocketChannel.connect(new InetSocketAddress(PORT), attachment, acceptCompletionHandler);
        }
    }
}

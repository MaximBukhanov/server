package AsyncNioServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;

public class AsyncNioServer {

    static final int PORT = 9090;

    public static void main(String[] args) {
        try {
            InetSocketAddress address = new InetSocketAddress(PORT);
            AsynchronousServerSocketChannel serverChannel = AsynchronousServerSocketChannel.open();
            serverChannel.bind(address);
            AcceptCompletionHandler acceptCompletionHandler = new AcceptCompletionHandler(serverChannel);
            serverChannel.accept(null, acceptCompletionHandler);

            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

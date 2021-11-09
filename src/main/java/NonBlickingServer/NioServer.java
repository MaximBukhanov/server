package NonBlickingServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;

public class NioServer {

    HashMap<SocketChannel, ByteBuffer> sessions = new HashMap<>();
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;

    NioServer(InetSocketAddress address) throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(address);

        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    public void start() throws IOException {
        while(true) {
            selector.selectNow();
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            while(keys.hasNext()) {
                SelectionKey key = keys.next();
                keys.remove();
                if (key.isValid()) {
                    if (key.isAcceptable()) {
                        accept(key);
                    } else if (key.isReadable()) {
                        read(key);
                    } else if (key.isWritable()) {
                        write(key);
                    }
                }
            }
            selector.selectedKeys().clear();
        }
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        socketChannel.configureBlocking(false);
        ByteBuffer writeBuffer = sessions.get(socketChannel);
        sessions.remove(socketChannel);
        writeBuffer.flip();
        socketChannel.write(ByteBuffer.wrap("Server response - ".getBytes(StandardCharsets.UTF_8)));
        socketChannel.write(writeBuffer);
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        socketChannel.configureBlocking(false);
        int numRead = -1;
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        numRead = socketChannel.read(readBuffer);
        System.out.println("Server read: " + numRead + " byte(s)");
        if (numRead == -1) {
            System.out.println("Disconnect..." + socketChannel.getRemoteAddress());
            key.cancel();
            socketChannel.close();
            return;
        }
        byte[] bytesRead = readBuffer.array();
        System.out.println("Received message: " + new String(bytesRead, StandardCharsets.UTF_8).trim());
        sessions.put(socketChannel, readBuffer);
        socketChannel.register(selector, SelectionKey.OP_WRITE);
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        System.out.println("New Connection: " + socketChannel.getRemoteAddress());
    }

    public static void main(String[] args) {
        final int PORT = 9090;
        try {
            new NioServer(new InetSocketAddress(PORT)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

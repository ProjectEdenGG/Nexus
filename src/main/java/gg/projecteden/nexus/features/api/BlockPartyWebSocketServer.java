package gg.projecteden.nexus.features.api;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.Utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BlockPartyWebSocketServer {
    static final int PORT = 8182;
    private static ServerSocketChannel serverSocket;
    private static Selector selector;
    private static final Set<SocketChannel> clients = Collections.synchronizedSet(new HashSet<>());
    private static volatile boolean running = true;

    public static void start() {
        try {
            selector = Selector.open();
            serverSocket = ServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress(PORT));
            serverSocket.configureBlocking(false);
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);

            while (running) {
                selector.select();
                for (SelectionKey key : selector.selectedKeys()) {
                    if (key.isAcceptable()) {
                        acceptConnection();
                    } else if (key.isReadable()) {
                        handleMessage((SocketChannel) key.channel());
                    }
                }
                selector.selectedKeys().clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void stop() {
        running = false;
        try {
            selector.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void acceptConnection() throws IOException {
        SocketChannel client = serverSocket.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
        clients.add(client);

        // Perform WebSocket handshake
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        client.read(buffer);
        String request = new String(buffer.array()).trim();

        // Extract WebSocket key
        String webSocketKey = request.split("Sec-WebSocket-Key: ")[1].split("\r\n")[0];
        String acceptKey = generateWebSocketAcceptKey(webSocketKey);

        // Send handshake response
        String handshakeResponse = 
            "HTTP/1.1 101 Switching Protocols\r\n" +
            "Upgrade: websocket\r\n" +
            "Connection: Upgrade\r\n" +
            "Sec-WebSocket-Accept: " + acceptKey + "\r\n\r\n";
        client.write(ByteBuffer.wrap(handshakeResponse.getBytes()));

        log("New WebSocket client connected.");
    }

    private static void handleMessage(SocketChannel client) {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        try {
            int bytesRead = client.read(buffer);
            if (bytesRead == -1) {
                clients.remove(client);
                client.close();
                return;
            }
            buffer.flip();
//            String message = decodeWebSocketFrame(buffer);
//            log("Received: " + message);
        } catch (IOException e) {
            clients.remove(client);
        }
    }

    public static void broadcast(Object message) {
        synchronized (clients) {
			String string = message instanceof String ? (String) message : Utils.getGson().toJson(message);
            ByteBuffer buffer = ByteBuffer.wrap(encodeWebSocketFrame(string));

            for (SocketChannel client : clients) {
                try {
                    client.write(buffer.duplicate());
                } catch (IOException e) {
                    try {
                        client.close();
                    } catch (IOException ignored) {}
                }
            }
        }
    }

    private static byte[] encodeWebSocketFrame(String message) {
        byte[] messageBytes = message.getBytes();
        int frameSize = messageBytes.length + 2;
        byte[] frame = new byte[frameSize];

        frame[0] = (byte) 0x81; // FIN + text frame
        frame[1] = (byte) messageBytes.length; // No masking

        System.arraycopy(messageBytes, 0, frame, 2, messageBytes.length);
        return frame;
    }

    private static String decodeWebSocketFrame(ByteBuffer buffer) {
        buffer.position(2); // Skip the first two bytes (FIN + opcode and length)
        byte[] messageBytes = new byte[buffer.remaining()];
        buffer.get(messageBytes);
        return new String(messageBytes);
    }

    private static String generateWebSocketAcceptKey(String key) {
        try {
            String magic = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest((key + magic).getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error generating WebSocket key", e);
        }
    }
	
	private static void log(String message) {
		Nexus.log("[WebSocket] " + message);
	}
	
}

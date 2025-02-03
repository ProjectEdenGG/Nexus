package gg.projecteden.nexus.features.api;

import gg.projecteden.api.common.utils.UUIDUtils;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BlockPartyWebSocketServer {
	static final int PORT = 8182;
	private static ServerSocketChannel serverSocket;
	private static Selector selector;
	private static final Map<UUID, SocketChannel> clients = new ConcurrentHashMap<>();
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
		}
		catch (ClosedSelectorException ignored) {}
		catch (IOException e) {
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
		clients.values().forEach(client -> {
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		clients.clear();
	}

	@SneakyThrows
	private static void acceptConnection() {
		SocketChannel client = serverSocket.accept();
		client.configureBlocking(false);
		client.register(selector, SelectionKey.OP_READ);

		ByteBuffer buffer = ByteBuffer.allocate(1024);
		client.read(buffer);
		String request = new String(buffer.array()).trim();

		String webSocketKey = extractHeader(request);

		UUID uuid = UUID.fromString(extractUUID(request));
		clients.put(uuid, client);

		String acceptKey = generateWebSocketAcceptKey(webSocketKey);

		String handshakeResponse =
			"HTTP/1.1 101 Switching Protocols\r\n" +
				"Upgrade: websocket\r\n" +
				"Connection: Upgrade\r\n" +
				"Sec-WebSocket-Accept: " + acceptKey + "\r\n\r\n";
		client.write(ByteBuffer.wrap(handshakeResponse.getBytes()));

		log("New WebSocket client connected with UUID: " + uuid);
		Tasks.async(() -> syncClient(uuid));
	}

	private static String extractHeader(String request) {
		for (String line : request.split("\r\n")) {
			if (line.startsWith("Sec-WebSocket-Key")) {
				return line.split(": ")[1];
			}
		}
		return null;
	}

	private static String extractUUID(String request) {
		String firstLine = request.split("\r\n")[0];
		if (firstLine.contains("?uuid=")) {
			return firstLine.split("\\?uuid=")[1].split(" ")[0];
		}
		return UUIDUtils.UUID0.toString();
	}

	private static void syncClient(UUID uuid) {
		BlockPartyClientConnectedEvent event = new BlockPartyClientConnectedEvent(uuid);
		event.callEvent();

		broadcast(uuid, event.toJson());
	}

	private static void handleMessage(SocketChannel client) {
		try {
			client.close();
		} catch (IOException ignored) { }
	}

	public static void broadcast(Object message) {
		synchronized (clients) {
			String string = message instanceof String ? (String) message : Utils.getGson().toJson(message);
			ByteBuffer buffer = ByteBuffer.wrap(encodeWebSocketFrame(string));

			for (Map.Entry<UUID, SocketChannel> client : clients.entrySet()) {
				try {
					ByteBuffer sendBuffer = buffer.duplicate();
					while (sendBuffer.hasRemaining()) {
						client.getValue().write(sendBuffer);
					}
				} catch (IOException e) {
					try {
						client.getValue().close();
						clients.remove(client.getKey());
					} catch (IOException ignored) { }
				}
			}
		}
	}

	public static void broadcast(UUID uuid, Object message) {
		synchronized (clients) {
			SocketChannel client = clients.get(uuid);
			if (client == null) return;
			String string = message instanceof String ? (String) message : Utils.getGson().toJson(message);
			ByteBuffer buffer = ByteBuffer.wrap(encodeWebSocketFrame(string));

			try {
				while (buffer.hasRemaining())
					client.write(buffer);
			} catch (IOException e) {
				try {
					client.close();
					clients.remove(uuid);
				} catch (IOException ignored) { }
			}
		}
	}

	private static byte[] encodeWebSocketFrame(String message) {
		byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
		int messageLength = messageBytes.length;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		// FIN + Text Frame opcode
		outputStream.write(0x81);

		if (messageLength <= 125) {
			outputStream.write(messageLength);
		} else if (messageLength <= 65535) {
			outputStream.write(126);
			outputStream.write((messageLength >> 8) & 0xFF);
			outputStream.write((messageLength) & 0xFF);
		} else {
			outputStream.write(127);
			for (int i = 7; i >= 0; i--) {
				outputStream.write((messageLength >> (i * 8)) & 0xFF);
			}
		}

		try {
			outputStream.write(messageBytes);
		} catch (IOException e) {
			throw new RuntimeException("Failed to encode WebSocket frame", e);
		}

		return outputStream.toByteArray();
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

	public static boolean isConnected(@NotNull UUID uniqueId) {
		return clients.containsKey(uniqueId) && clients.get(uniqueId) != null && clients.get(uniqueId).isOpen();
	}

	public static class BlockPartyClientConnectedEvent extends Event {

		@Getter
		final UUID uuid;

		private BlockPartyClientConnectedEvent(UUID uuid) {
			super(true);
			this.uuid = uuid;
		}

		private final List<BlockPartyClientMessage> messages = new ArrayList<>();

		public void addSong(List<UUID> uuids, String title, String artist, double time, String url, boolean playing) {
			BlockPartyClientMessage message = BlockPartyClientMessage.to(uuids)
				.song(new Song(title, artist, time, url));
			if (playing)
				message.play();
			messages.add(message);
		}

		public void setBlock(List<UUID> uuids, String block) {
			messages.add(BlockPartyClientMessage.to(uuids).block(block));
		}

		private String toJson() {
			BlockPartyClientMessage[] messageArray = messages.toArray(new BlockPartyClientMessage[0]);
			return Utils.getGson().toJson(messageArray);
		}

		@Getter
		private static final HandlerList handlerList = new HandlerList();

		@Override
		public @NotNull HandlerList getHandlers() {
			return handlerList;
		}

	}

	@Getter
	public static class BlockPartyClientMessage {

		private List<UUID> uuids;
		private String action;
		private Song song;
		private double time;
		private String block;

		public static BlockPartyClientMessage to(List<UUID> uuids) {
			BlockPartyClientMessage message = new BlockPartyClientMessage();
			message.uuids = uuids;
			return message;
		}

		public static BlockPartyClientMessage to(UUID uuids) {
			BlockPartyClientMessage message = new BlockPartyClientMessage();
			message.uuids = List.of(uuids);
			return message;
		}

		public BlockPartyClientMessage song(Song song) {
			this.song = song;
			return this;
		}

		public BlockPartyClientMessage time(double time) {
			this.time = time;
			return this;
		}

		public BlockPartyClientMessage play() {
			this.action = "play";
			return this;
		}

		public BlockPartyClientMessage pause() {
			this.action = "pause";
			return this;
		}

		public BlockPartyClientMessage stop() {
			this.action = "stop";
			return this;
		}

		public BlockPartyClientMessage block(String block) {
			this.action = "block";
			this.block = block;
			return this;
		}

		public void send() {
			if (uuids.size() > 1)
				BlockPartyWebSocketServer.broadcast(this);
			else if (!uuids.isEmpty())
				BlockPartyWebSocketServer.broadcast(this.uuids.get(0), this);
		}
	}

	@AllArgsConstructor
	public static class Song {
		String title;
		String artist;
		double time;
		String url;
	}

}

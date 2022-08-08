package gg.projecteden.nexus.features;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.IOUtils;
import net.minecraft.network.chat.LastSeenMessages.Entry;
import net.minecraft.network.chat.LastSeenMessages.Update;
import net.minecraft.network.protocol.game.ClientboundChatPreviewPacket;
import net.minecraft.network.protocol.game.ClientboundCustomChatCompletionsPacket;
import net.minecraft.network.protocol.game.ClientboundDeleteChatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerChatHeaderPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundSetDisplayChatPreviewPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ServerboundChatAckPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundChatPreviewPacket;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class ChatPacketListener {

	public static void init() {}

	static {
		final List<PacketType> packets = List.of(
			PacketType.fromClass(ClientboundChatPreviewPacket.class),
			PacketType.fromClass(ClientboundCustomChatCompletionsPacket.class),
			PacketType.fromClass(ClientboundDeleteChatPacket.class),
			PacketType.fromClass(ClientboundPlayerChatPacket.class),
			PacketType.fromClass(ClientboundSystemChatPacket.class),
			PacketType.fromClass(ClientboundPlayerChatHeaderPacket.class),
			PacketType.fromClass(ClientboundSetDisplayChatPreviewPacket.class),
			PacketType.fromClass(ServerboundChatAckPacket.class),
			PacketType.fromClass(ServerboundChatPacket.class),
			PacketType.fromClass(ServerboundChatCommandPacket.class),
			PacketType.fromClass(ServerboundChatPreviewPacket.class)
		);

		Nexus.getProtocolManager().addPacketListener(new PacketAdapter(Nexus.getInstance(), packets) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				final Object nms = event.getPacket().getHandle();
				final AtomicReference<String> message = new AtomicReference<>("Packet: " + nms.getClass() + System.lineSeparator());
				final Consumer<String> append = msg -> message.set(message.get() + "  " + msg + System.lineSeparator());

				append.accept("Player: " + event.getPlayer());

				final Consumer<Entry> signatureConsumer = entry -> {
					append.accept("  entry.profileId(): " + entry.profileId());
					append.accept("  entry.lastSignature(): " + entry.lastSignature());
					append.accept("  entry.lastSignature().isEmpty(): " + entry.lastSignature().isEmpty());
				};

				final Consumer<Update> lastSeenMessagesFormatter = lastSeenMessages -> {
					append.accept("lastSeenMessages().lastReceived().isPresent(): " + lastSeenMessages.lastReceived().isPresent());
					if (lastSeenMessages.lastReceived().isPresent())
						signatureConsumer.accept(lastSeenMessages.lastReceived().get());

					append.accept("for (entry : lastSeenMessages().lastSeen().entries()):");
					for (var entry : lastSeenMessages.lastSeen().entries())
						signatureConsumer.accept(entry);
				};

				if (nms instanceof ServerboundChatAckPacket serverboundChatAckPacket) {
					try {
						lastSeenMessagesFormatter.accept(serverboundChatAckPacket.lastSeenMessages());
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

				if (nms instanceof ServerboundChatPacket serverboundChatPacket) {
					try {
						append.accept("message(): " + serverboundChatPacket.message());
						append.accept("salt(): " + serverboundChatPacket.salt());
						append.accept("signature(): " + serverboundChatPacket.signature());
						append.accept("signedPreview(): " + serverboundChatPacket.signedPreview());
						append.accept("timeStamp(): " + serverboundChatPacket.timeStamp());
						lastSeenMessagesFormatter.accept(serverboundChatPacket.lastSeenMessages());
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

				if (nms instanceof ServerboundChatCommandPacket serverboundChatCommandPacket) {
					try {
						append.accept("command(): " + serverboundChatCommandPacket.command());
						append.accept("salt(): " + serverboundChatCommandPacket.salt());
						append.accept("timeStamp(): " + serverboundChatCommandPacket.timeStamp());
						append.accept("signedPreview(): " + serverboundChatCommandPacket.signedPreview());

						append.accept("for (entry : argumentSignatures().entries()):");
						for (var entry : serverboundChatCommandPacket.argumentSignatures().entries()) {
							append.accept("  entry.name(): " + entry.name());
							append.accept("  entry.signature(): " + entry.signature());
							append.accept("  entry.signature().isEmpty(): " + entry.signature().isEmpty());
						}

						lastSeenMessagesFormatter.accept(serverboundChatCommandPacket.lastSeenMessages());
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

				IOUtils.fileAppend("chatpackets", message.get());
			}

			@Override
			public void onPacketSending(PacketEvent event) {
				final Object nms = event.getPacket().getHandle();
				final AtomicReference<String> message = new AtomicReference<>("Packet: " + nms.getClass() + System.lineSeparator());
				final Consumer<String> append = msg -> message.set(message.get() + "  " + msg + System.lineSeparator());

				append.accept("Player: " + event.getPlayer());

				final Consumer<Entry> signatureConsumer = entry -> {
					append.accept("  entry.profileId(): " + entry.profileId());
					append.accept("  entry.lastSignature(): " + entry.lastSignature());
					append.accept("  entry.lastSignature().isEmpty(): " + entry.lastSignature().isEmpty());
				};

				if (nms instanceof ClientboundPlayerChatPacket clientboundPlayerChatPacket) {
					append.accept("chatType().chatType(): " + clientboundPlayerChatPacket.chatType().chatType());
					append.accept("chatType().name().getString(): " + clientboundPlayerChatPacket.chatType().name().getString());
					if (clientboundPlayerChatPacket.chatType().targetName() != null)
						append.accept("chatType().targetName().getString(): " + clientboundPlayerChatPacket.chatType().targetName().getString());

					append.accept("message().salt(): " + clientboundPlayerChatPacket.message().salt());
					append.accept("message().serverContent().getString(): " + clientboundPlayerChatPacket.message().serverContent().getString());
					append.accept("message().signedContent().plain(): " + clientboundPlayerChatPacket.message().signedContent().plain());
					append.accept("message().signedContent().isDecorated(): " + clientboundPlayerChatPacket.message().signedContent().isDecorated());
					append.accept("message().signedContent().decorated().getString(): " + clientboundPlayerChatPacket.message().signedContent().decorated().getString());
					append.accept("message().timeStamp(): " + clientboundPlayerChatPacket.message().timeStamp());
					append.accept("message().signer().salt(): " + clientboundPlayerChatPacket.message().signer().salt());
					append.accept("message().signer().isSystem(): " + clientboundPlayerChatPacket.message().signer().isSystem());
					append.accept("message().signer().timeStamp(): " + clientboundPlayerChatPacket.message().signer().timeStamp());
					append.accept("message().signer().profileId(): " + clientboundPlayerChatPacket.message().signer().profileId());
					append.accept("message().headerSignature().isEmpty(): " + clientboundPlayerChatPacket.message().headerSignature().isEmpty());
					append.accept("message().headerSignature(): " + clientboundPlayerChatPacket.message().headerSignature());
					append.accept("message().serverContent().getString(): " + clientboundPlayerChatPacket.message().serverContent().getString());
					append.accept("message().isFullyFiltered(): " + clientboundPlayerChatPacket.message().isFullyFiltered());
					append.accept("message().signedBody().salt(): " + clientboundPlayerChatPacket.message().signedBody().salt());
					append.accept("message().signedBody().timeStamp(): " + clientboundPlayerChatPacket.message().signedBody().timeStamp());
					append.accept("message().filterMask().isEmpty(): " + clientboundPlayerChatPacket.message().filterMask().isEmpty());
					append.accept("message().filterMask().isFullyFiltered(): " + clientboundPlayerChatPacket.message().filterMask().isFullyFiltered());
					append.accept("message().signedHeader().sender(): " + clientboundPlayerChatPacket.message().signedHeader().sender());
					append.accept("message().signedHeader().previousSignature(): " + clientboundPlayerChatPacket.message().signedHeader().previousSignature());
					append.accept("message().unsignedContent().isPresent(): " + clientboundPlayerChatPacket.message().unsignedContent().isPresent());
					if (clientboundPlayerChatPacket.message().unsignedContent().isPresent())
						append.accept("message().unsignedContent().get().getString(): " + clientboundPlayerChatPacket.message().unsignedContent().get().getString());

					append.accept("for (entry : message().signedBody().lastSeen().entries()): ");
					for (Entry entry : clientboundPlayerChatPacket.message().signedBody().lastSeen().entries())
						signatureConsumer.accept(entry);
				}

				if (nms instanceof ClientboundPlayerChatHeaderPacket clientboundPlayerChatHeaderPacket) {
					append.accept("headerSignature().isEmpty(): " + clientboundPlayerChatHeaderPacket.headerSignature().isEmpty());
					append.accept("headerSignature(): " + clientboundPlayerChatHeaderPacket.headerSignature());
					append.accept("header().previousSignature(): " + clientboundPlayerChatHeaderPacket.header().previousSignature());
					append.accept("header().sender(): " + clientboundPlayerChatHeaderPacket.header().sender());

					event.setCancelled(true);
				}

				IOUtils.fileAppend("chatpackets", message.get());
			}
		});
	}

}

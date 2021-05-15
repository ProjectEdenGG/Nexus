package me.pugabyte.nexus.features.chat.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.pugabyte.nexus.models.chat.Chatter;
import me.pugabyte.nexus.models.chat.PrivateChannel;

import java.util.Set;

@Data
@AllArgsConstructor
public class PrivateChatEvent extends MinecraftChatEvent {
	private final Chatter chatter;
	private PrivateChannel channel;
	private final String originalMessage;
	private String message;
	private boolean filtered;

	private Set<Chatter> recipients;

	public PrivateChatEvent(Chatter chatter, PrivateChannel channel, String originalMessage, String message, Set<Chatter> recipients) {
		this.chatter = chatter;
		this.channel = channel;
		this.originalMessage = originalMessage;
		this.message = message;
		this.recipients = recipients;
	}

	@Override
	public boolean wasSeen() {
		return true;
	}

	public String getRecipientNames() {
		return String.join(", ", channel.getOthersNames(chatter));
	}

}

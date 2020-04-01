package me.pugabyte.bncore.features.chat.models.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.pugabyte.bncore.features.chat.models.Chatter;
import me.pugabyte.bncore.features.chat.models.PublicChannel;
import me.pugabyte.bncore.utils.Utils;

import java.util.Set;

@Data
@AllArgsConstructor
public class PublicChatEvent extends MinecraftChatEvent {
	private final Chatter chatter;
	private PublicChannel channel;
	private String message;

	private Set<Chatter> recipients;

	public boolean wasSeen() {
		if (channel.getDiscordChannel() != null) return true;
		return recipients.stream().anyMatch(recipient -> Utils.canSee(chatter.getOfflinePlayer(), recipient.getOfflinePlayer()));
	}

}

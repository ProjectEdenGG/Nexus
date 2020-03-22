package me.pugabyte.bncore.features.chat.models.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.pugabyte.bncore.features.chat.models.Chatter;
import me.pugabyte.bncore.features.chat.models.PrivateChannel;

import java.util.Set;

@Data
@AllArgsConstructor
public class PrivateChatEvent extends ChatEvent {
	private final Chatter chatter;
	private PrivateChannel channel;
	private String message;

	private Set<Chatter> recipients;

}

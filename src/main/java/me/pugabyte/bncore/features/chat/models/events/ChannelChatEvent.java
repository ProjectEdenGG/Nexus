package me.pugabyte.bncore.features.chat.models.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.pugabyte.bncore.features.chat.models.Channel;
import me.pugabyte.bncore.features.chat.models.Chatter;

@Data
@AllArgsConstructor
public class ChannelChatEvent extends ChatEvent {
	private Chatter chatter;
	private Channel channel;
	private String message;
}

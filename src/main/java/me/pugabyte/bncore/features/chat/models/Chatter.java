package me.pugabyte.bncore.features.chat.models;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.features.chat.Chat;
import me.pugabyte.bncore.models.nerds.Nerd;

@Data
@RequiredArgsConstructor
public class Chatter {
	@NonNull
	private Nerd nerd;
	private Channel activeChannel;

	public void setActiveChannel(Channel channel) {
		this.activeChannel = channel;
		nerd.send(Chat.PREFIX + "Now chatting in " + channel.getColor() + channel.getName());
	}

}

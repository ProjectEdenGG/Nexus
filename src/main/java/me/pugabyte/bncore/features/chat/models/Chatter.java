package me.pugabyte.bncore.features.chat.models;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.features.chat.Chat;
import me.pugabyte.bncore.models.nerd.Nerd;
import me.pugabyte.bncore.utils.JsonBuilder;
import me.pugabyte.bncore.utils.SoundUtils.Jingle;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static me.pugabyte.bncore.features.chat.ChatManager.getChannels;
import static me.pugabyte.bncore.utils.StringUtils.colorize;

@Data
@RequiredArgsConstructor
public class Chatter {
	@NonNull
	private Player player;
	private Channel activeChannel;
	private List<PublicChannel> joinedChannels = new ArrayList<>();

	public String getUuid() {
		return player.getUniqueId().toString();
	}

	public void playSound() {
		Jingle.PING.play(player);
	}

	public void setActiveChannel(Channel channel) {
		this.activeChannel = channel;
		new Nerd(player).send(Chat.PREFIX + channel.getAssignMessage(this));
	}

	public void join(PublicChannel channel) {
		joinedChannels.add(channel);
	}

	public boolean hasJoined(PublicChannel channel) {
		return joinedChannels.contains(channel);
	}

	public void leave(PublicChannel channel) {
		joinedChannels.remove(channel);
	}

	public void send(String message) {
		player.sendMessage(colorize(message));
	}

	public void send(JsonBuilder message) {
		player.spigot().sendMessage(message.build());
	}

	public void updateChannels() {
		getChannels().forEach(channel -> {
			if (player.hasPermission(channel.getPermission())) {
				if (!hasJoined(channel))
					join(channel);
			} else {
				leave(channel);
			}
		});
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Chatter chatter = (Chatter) o;
		return Objects.equals(player.getUniqueId(), chatter.getPlayer().getUniqueId());
	}

}

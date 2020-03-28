package me.pugabyte.bncore.features.chat.models;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.features.chat.Chat;
import me.pugabyte.bncore.models.nerd.Nerd;
import me.pugabyte.bncore.utils.JsonBuilder;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.features.chat.ChatManager.getChannels;
import static me.pugabyte.bncore.utils.StringUtils.colorize;

@Data
@RequiredArgsConstructor
public class Chatter {
	@NonNull
	private Player player;
	private Channel activeChannel;
	private List<PublicChannel> joinedChannels = new ArrayList<>();

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
		WorldGroup worldGroup = WorldGroup.get(player);

		List<PublicChannel> toLeave = joinedChannels.stream()
				.filter(channel -> channel.getWorldGroup() != null)
				.filter(channel -> channel.getWorldGroup() != worldGroup)
				.collect(Collectors.toList());
		List<PublicChannel> toJoin = getChannels(worldGroup);

		toLeave.forEach(this::leave);
		toJoin.forEach(this::join);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Chatter chatter = (Chatter) o;
		return Objects.equals(player.getUniqueId(), chatter.getPlayer().getUniqueId());
	}

}

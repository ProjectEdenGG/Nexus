package me.pugabyte.bncore.features.chat.models;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.features.chat.Chat;
import me.pugabyte.bncore.features.chat.ChatManager;
import me.pugabyte.bncore.framework.exceptions.postconfigured.PlayerNotOnlineException;
import me.pugabyte.bncore.models.nerd.Nerd;
import me.pugabyte.bncore.utils.JsonBuilder;
import me.pugabyte.bncore.utils.SoundUtils.Jingle;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

@Data
@RequiredArgsConstructor
public class Chatter {
	@NonNull
	private OfflinePlayer offlinePlayer;
	private Channel activeChannel;
	private List<PublicChannel> joinedChannels = new ArrayList<>();

	public String getUuid() {
		return offlinePlayer.getUniqueId().toString();
	}

	public Player getPlayer() {
		if (!offlinePlayer.isOnline())
			throw new PlayerNotOnlineException(offlinePlayer);
		return offlinePlayer.getPlayer();
	}

	public void playSound() {
		if (offlinePlayer.isOnline())
			Jingle.PING.play(offlinePlayer.getPlayer());
	}

	public void setActiveChannel(Channel channel) {
		this.activeChannel = channel;
		if (channel instanceof PublicChannel)
			join((PublicChannel) channel);
		new Nerd(offlinePlayer).send(Chat.PREFIX + channel.getAssignMessage(this));
	}

	public void join(PublicChannel channel) {
		joinedChannels.add(channel);
	}

	public boolean hasJoined(PublicChannel channel) {
		return joinedChannels.contains(channel);
	}

	public void leave(PublicChannel channel) {
		joinedChannels.remove(channel);
		if (activeChannel == channel && channel != ChatManager.getMainChannel())
			setActiveChannel(ChatManager.getMainChannel());
	}

	public void send(String message) {
		if (offlinePlayer.isOnline())
			offlinePlayer.getPlayer().sendMessage(colorize(message));
	}

	public void send(JsonBuilder message) {
		if (offlinePlayer.isOnline())
			offlinePlayer.getPlayer().spigot().sendMessage(message.build());
	}

	public void updateChannels() {
		if (offlinePlayer.isOnline())
			ChatManager.getChannels().forEach(channel -> {
				if (offlinePlayer.getPlayer().hasPermission(channel.getPermission())) {
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
		return Objects.equals(offlinePlayer.getUniqueId(), chatter.getOfflinePlayer().getUniqueId());
	}

}

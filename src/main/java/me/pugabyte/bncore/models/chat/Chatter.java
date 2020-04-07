package me.pugabyte.bncore.models.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.chat.Chat;
import me.pugabyte.bncore.features.chat.ChatManager;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.models.PlayerOwnedObject;
import me.pugabyte.bncore.models.nerd.Nerd;
import me.pugabyte.bncore.utils.JsonBuilder;
import me.pugabyte.bncore.utils.SoundUtils.Jingle;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Chatter extends PlayerOwnedObject {
	@NonNull
	private UUID uuid;
	private Channel activeChannel;
	private Set<PublicChannel> joinedChannels = new HashSet<>();
	private PrivateChannel lastPrivateMessage;

	public void playSound() {
		if (getOfflinePlayer().isOnline())
			Jingle.PING.play(getOfflinePlayer().getPlayer());
	}

	public void say(String message) {
		say(getActiveChannel(), message);
	}

	public void say(Channel channel, String message) {
		ChatManager.process(this, channel, message);
	}

	public void setActiveChannel(Channel channel) {
		if (channel == null)
			new Nerd(getOfflinePlayer()).send(Chat.PREFIX + "You are no longer speaking in a channel");
		else {
			if (channel instanceof PublicChannel)
				join((PublicChannel) channel);
			new Nerd(getOfflinePlayer()).send(Chat.PREFIX + channel.getAssignMessage(this));
		}
		this.activeChannel = channel;
	}

	public boolean canJoin(PublicChannel channel) {
		if (getOfflinePlayer().isOnline())
			return getOfflinePlayer().getPlayer().hasPermission(channel.getPermission());
		else
			return BNCore.getPex().playerHas(null, getOfflinePlayer(), channel.getPermission());
	}

	public boolean hasJoined(PublicChannel channel) {
		if (joinedChannels != null)
			return joinedChannels.contains(channel);
		return false;
	}

	public void join(PublicChannel channel) {
		if (!canJoin(channel))
			throw new InvalidInputException("You do not have permission to join that channel");
		if (joinedChannels == null)
			joinedChannels = new HashSet<>();
		joinedChannels.add(channel);
	}

	public void leave(PublicChannel channel) {
		if (joinedChannels == null)
			joinedChannels = new HashSet<>();
		joinedChannels.remove(channel);
		if (activeChannel == channel && channel != ChatManager.getMainChannel())
			setActiveChannel(ChatManager.getMainChannel());
	}

	public void send(String message) {
		if (getOfflinePlayer().isOnline())
			getOfflinePlayer().getPlayer().sendMessage(colorize(message));
	}

	public void send(JsonBuilder message) {
		if (getOfflinePlayer().isOnline())
			getOfflinePlayer().getPlayer().spigot().sendMessage(message.build());
	}

	public void updateChannels() {
		if (getOfflinePlayer().isOnline())
			ChatManager.getChannels().forEach(channel -> {
				if (canJoin(channel)) {
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
		return Objects.equals(getOfflinePlayer().getUniqueId(), chatter.getOfflinePlayer().getUniqueId());
	}

}

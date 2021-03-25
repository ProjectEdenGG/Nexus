package me.pugabyte.nexus.models.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.features.chat.ChatManager;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.SoundUtils.Jingle;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static me.pugabyte.nexus.features.chat.Chat.PREFIX;
import static me.pugabyte.nexus.utils.StringUtils.colorize;
import static me.pugabyte.nexus.utils.StringUtils.trimFirst;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Chatter extends PlayerOwnedObject {
	@NonNull
	private UUID uuid;
	private Channel activeChannel;
	private Set<PublicChannel> joinedChannels = new HashSet<>();
	private Set<PublicChannel> leftChannels = new HashSet<>();
	private PrivateChannel lastPrivateMessage;

	public void playSound() {
		if (isOnline())
			Jingle.PING.play(getOfflinePlayer().getPlayer());
	}

	public void say(String message) {
		if (message.startsWith("/"))
			PlayerUtils.runCommand(getPlayer(), trimFirst(message));
		else
			say(getActiveChannel(), message);
	}

	public void say(Channel channel, String message) {
		ChatManager.process(this, channel, message);
	}

	public void setActiveChannel(Channel channel) {
		if (channel == null)
			Nerd.of(getOfflinePlayer()).send(PREFIX + "You are no longer speaking in a channel");
		else {
			if (channel instanceof PublicChannel)
				join((PublicChannel) channel);
			Nerd.of(getOfflinePlayer()).send(PREFIX + channel.getAssignMessage(this));
		}
		this.activeChannel = channel;
	}

	public boolean canJoin(PublicChannel channel) {
		boolean hasPerm;
		if (isOnline())
			hasPerm = getOfflinePlayer().getPlayer().hasPermission(channel.getPermission());
		else
			hasPerm = Nexus.getPerms().playerHas(null, getOfflinePlayer(), channel.getPermission());

		if (!hasPerm)
			return false;

		if (channel.getRank() != null)
			return Nerd.of(getOfflinePlayer()).getRank().gte(channel.getRank());

		return true;
	}

	public boolean hasJoined(PublicChannel channel) {
		if (!canJoin(channel))
			return false;
		fixChannelSets();
		if (leftChannels.contains(channel))
			return false;
		return joinedChannels.contains(channel);
	}

	public boolean hasLeft(PublicChannel channel) {
		fixChannelSets();
		return leftChannels.contains(channel);
	}

	private void fixChannelSets() {
		if (joinedChannels == null)
			joinedChannels = new HashSet<>();
		if (leftChannels == null)
			leftChannels = new HashSet<>();
	}

	public void join(PublicChannel channel) {
		if (!canJoin(channel))
			throw new InvalidInputException("You do not have permission to join that channel");
		fixChannelSets();
		leftChannels.remove(channel);
		joinedChannels.add(channel);
	}

	public void leave(PublicChannel channel) {
		fixChannelSets();
		joinedChannels.remove(channel);
		leftChannels.add(channel);
		send(PREFIX + "Left " + channel.getColor() + channel.getName() + " &3channel");
		if (channel.equals(activeChannel))
			if (!joinedChannels.isEmpty())
				setActiveChannel(joinedChannels.iterator().next());
			else
				setActiveChannel(null);
	}

	public void send(String message) {
		if (isOnline())
			getOfflinePlayer().getPlayer().sendMessage(colorize(message));
	}

	public void send(JsonBuilder message) {
		if (isOnline())
			getOfflinePlayer().getPlayer().spigot().sendMessage(message.build());
	}

	public void updateChannels() {
		if (isOnline())
			ChatManager.getChannels().forEach(channel -> {
				if (canJoin(channel)) {
					if (!hasJoined(channel) && !hasLeft(channel))
						join(channel);
				} else if (hasJoined(channel)) {
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

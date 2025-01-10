package gg.projecteden.nexus.models.chat;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.chat.Chat;
import gg.projecteden.nexus.features.chat.Chat.StaticChannel;
import gg.projecteden.nexus.features.chat.ChatManager;
import gg.projecteden.nexus.features.chat.translator.Language;
import gg.projecteden.nexus.features.titan.ClientMessage;
import gg.projecteden.nexus.features.titan.clientbound.UpdateState;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.ChannelConverter;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.PrivateChannelConverter;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.PublicChannelConverter;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.party.PartyManager;
import gg.projecteden.nexus.utils.LuckPermsUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundUtils.Jingle;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.*;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Data
@Entity(value = "chatter", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, ChannelConverter.class, PrivateChannelConverter.class, PublicChannelConverter.class})
public class Chatter implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Channel activeChannel;
	private Set<PublicChannel> joinedChannels = new HashSet<>();
	private Set<PublicChannel> leftChannels = new HashSet<>();
	private PrivateChannel lastPrivateMessage;
	private Language language;

	public static Chatter of(UUID uuid) {
		return uuid == null ? null : new ChatterService().get(uuid);
	}

	public static Chatter of(HasUniqueId hasUniqueId) {
		return hasUniqueId == null ? null : new ChatterService().get(hasUniqueId);
	}

	public void playSound() {
		Player player = getPlayer();
		if (player != null)
			Jingle.PING.play(player);
	}

	public void say(String message) {
		if (message.startsWith("/"))
			PlayerUtils.runCommand(getOnlinePlayer(), StringUtils.trimFirst(message));
		else
			say(getActiveChannel(), message);
	}

	public void say(Channel channel, String message) {
		ChatManager.process(this, channel, message);
	}

	public void setActiveChannel(Channel channel) {
		setActiveChannel(channel, false);
	}

	public void setActiveChannel(Channel channel, boolean silent) {
		if (channel == null) {
			if (!silent)
				sendMessage(Chat.PREFIX + "You are no longer speaking in a channel");
		} else {
			if (channel instanceof PublicChannel publicChannel)
				joinSilent(publicChannel);

			if (!silent)
				sendMessage(Chat.PREFIX + channel.getAssignMessage(this));
		}

		this.activeChannel = channel;
		save();
		notifyTitanOfChannelChange();
	}

	public void notifyTitanOfChannelChange() {
		if (this.getPlayer() == null)
			return;

		String channelName = "Unknown";
		if (this.activeChannel != null)
			if (this.activeChannel instanceof PublicChannel publicChannel)
				channelName = publicChannel.getName();
			else
				channelName = "Private";

		ClientMessage.builder()
			.players(this.getOnlinePlayer())
			.message(UpdateState.builder()
				.channel(channelName)
				.build())
			.send();
	}

	public boolean canJoin(PublicChannel channel) {
		if (channel.getPermission().isEmpty() && channel.getRank() == null)
			return true;

		if (!LuckPermsUtils.hasPermission(this, channel.getPermission()))
			return false;

		if (channel.getRank() != null)
			return Nerd.of(getUuid()).getRank().gte(channel.getRank());

		if ("P".equalsIgnoreCase(channel.getNickname()))
			return PartyManager.of(getOnlinePlayer()) != null;

		return true;
	}

	public boolean isInValidWorld(PublicChannel publicChannel) {
		return !publicChannel.getDisabledWorldGroups().contains(WorldGroup.of(getOnlinePlayer()));
	}

	public boolean hasJoined(PublicChannel channel) {
		if (!canJoin(channel))
			return false;
		return hasJoinedRaw(channel);
	}

	private boolean hasJoinedRaw(PublicChannel channel) {
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
		else
			joinedChannels.removeIf(Objects::isNull);

		if (leftChannels == null)
			leftChannels = new HashSet<>();
		else
			joinedChannels.removeIf(Objects::isNull);
	}

	public void join(PublicChannel channel) {
		joinSilent(channel);
		sendMessage(Chat.PREFIX + "Joined " + channel.getColor() + channel.getName() + " &3channel");
	}

	public void joinSilent(PublicChannel channel) {
		if (!canJoin(channel))
			throw new InvalidInputException(channel.getJoinError());
		fixChannelSets();
		leftChannels.remove(channel);
		joinedChannels.add(channel);
		save();
	}

	public void leave(PublicChannel channel) {
		fixChannelSets();
		joinedChannels.remove(channel);
		leftChannels.add(channel);
		sendMessage(Chat.PREFIX + "Left " + channel.getColor() + channel.getName() + " &3channel");

		if (channel.equals(activeChannel))
			findNewActiveChannel();
		save();
	}

	public void leaveSilent(PublicChannel channel) {
		fixChannelSets();
		joinedChannels.remove(channel);

		if (channel.equals(activeChannel))
			findNewActiveChannel();
		save();
	}

	private void findNewActiveChannel() {
		if (!joinedChannels.isEmpty()) {
			for (StaticChannel staticChannel : StaticChannel.values()) {
				final PublicChannel channel = staticChannel.getChannel();

				if (!joinedChannels.contains(channel))
					continue;

				if (!canJoin(channel))
					continue;

				setActiveChannel(channel);
				return;
			}
		}

		setActiveChannel(null);
	}

	public void updateChannels() {
		if (isOnline())
			ChatManager.getChannels().forEach(channel -> {
				if (canJoin(channel)) {
					if (!hasJoined(channel) && !hasLeft(channel))
						joinSilent(channel);
				} else if (hasJoinedRaw(channel)) {
					leaveSilent(channel);
				}
			});
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Chatter chatter = (Chatter) o;
		return Objects.equals(getUniqueId(), chatter.getUniqueId());
	}

	private void save() {
		new ChatterService().queueSave(5, this);
	}
}


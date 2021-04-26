package me.pugabyte.nexus.models.chat;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.UUIDConverter;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.features.chat.ChatManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Entity("chatter")
@NoArgsConstructor
@Converters(UUIDConverter.class)
class DatabaseChatter {
	@Id
	@NonNull
	private UUID uuid;
	private String activePublicChannel;
	private List<String> activePrivateChannel;
	private List<String> joinedChannels;
	private List<String> leftChannels;
	private List<String> lastPrivateMessage;

	public DatabaseChatter(Chatter chatter) {
		uuid = chatter.getUuid();
		if (chatter.getActiveChannel() instanceof PublicChannel) {
			if (((PublicChannel) chatter.getActiveChannel()).isPersistent())
				activePublicChannel = ((PublicChannel) chatter.getActiveChannel()).getName();
		} else if (chatter.getActiveChannel() instanceof PrivateChannel)
			activePrivateChannel = getRecipients((PrivateChannel) chatter.getActiveChannel());
		if (chatter.getJoinedChannels() != null)
			joinedChannels = chatter.getJoinedChannels().stream().filter(PublicChannel::isPersistent).map(PublicChannel::getName).collect(Collectors.toList());
		if (chatter.getLeftChannels() != null)
			leftChannels = chatter.getLeftChannels().stream().filter(PublicChannel::isPersistent).map(PublicChannel::getName).collect(Collectors.toList());
		lastPrivateMessage = getRecipients(chatter.getLastPrivateMessage());
	}

	public Chatter deserialize() {
		Channel activeChannel = null;
		Set<PublicChannel> joinedChannels = new HashSet<>();
		Set<PublicChannel> leftChannels = new HashSet<>();
		PrivateChannel lastPrivateMessage = null;
		if (this.activePublicChannel != null)
			activeChannel = ChatManager.getChannel(this.activePublicChannel);
		else if (this.activePrivateChannel != null)
			activeChannel = new PrivateChannel(this.lastPrivateMessage);
		if (this.joinedChannels != null)
			joinedChannels = this.joinedChannels.stream().map(ChatManager::getChannel).collect(Collectors.toSet());
		if (this.leftChannels != null)
			leftChannels = this.leftChannels.stream().map(ChatManager::getChannel).collect(Collectors.toSet());
		if (this.lastPrivateMessage != null)
			lastPrivateMessage = new PrivateChannel(this.lastPrivateMessage);

		return new Chatter(uuid, activeChannel, joinedChannels, leftChannels, lastPrivateMessage);
	}

	public List<String> getRecipients(PrivateChannel privateChannel) {
		if (privateChannel != null)
			return privateChannel.getRecipients().stream()
					.map(_chatter -> _chatter.getUuid().toString())
					.collect(Collectors.toList());
		return null;
	}
}

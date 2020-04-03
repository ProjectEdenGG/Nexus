package me.pugabyte.bncore.models.chat;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.bncore.features.chat.ChatManager;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.UUIDConverter;

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
	private List<String> lastPrivateMessage;

	public DatabaseChatter(Chatter chatter) {
		uuid = chatter.getUuid();
		if (chatter.getActiveChannel() instanceof PublicChannel)
			activePublicChannel = ((PublicChannel) chatter.getActiveChannel()).getName();
		else if (chatter.getActiveChannel() instanceof PrivateChannel)
			activePrivateChannel = getRecipients((PrivateChannel) chatter.getActiveChannel());
		joinedChannels = chatter.getJoinedChannels().stream().map(PublicChannel::getName).collect(Collectors.toList());
		lastPrivateMessage = getRecipients(chatter.getLastPrivateMessage());
	}

	public Chatter deserialize() {
		Channel activeChannel = null;
		if (this.activePublicChannel != null)
			activeChannel = ChatManager.getChannel(this.activePublicChannel);
		else if (this.activePrivateChannel != null)
			activeChannel = getPrivateChannel(this.activePrivateChannel);
		Set<PublicChannel> joinedChannels = this.joinedChannels.stream().map(ChatManager::getChannel).collect(Collectors.toSet());
		PrivateChannel lastPrivateMessage = getPrivateChannel(this.lastPrivateMessage);
		return new Chatter(uuid, activeChannel, joinedChannels, lastPrivateMessage);
	}

	public PrivateChannel getPrivateChannel(List<String> uuids) {
		if (uuids != null)
			return new PrivateChannel(uuids);
		return null;
	}

	public List<String> getRecipients(PrivateChannel privateChannel) {
		if (privateChannel != null)
			return privateChannel.getRecipients().stream()
					.map(_chatter -> _chatter.getUuid().toString())
					.collect(Collectors.toList());
		return null;
	}
}

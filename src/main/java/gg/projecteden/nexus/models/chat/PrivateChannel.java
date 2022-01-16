package gg.projecteden.nexus.models.chat;

import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.Name;
import lombok.Data;
import lombok.ToString;
import net.md_5.bungee.api.ChatColor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static gg.projecteden.utils.Nullables.isNullOrEmpty;

@Data
public class PrivateChannel implements Channel {
	@ToString.Exclude
	private Set<UUID> recipients = new HashSet<>();

	public PrivateChannel(List<String> recipients) {
		if (!isNullOrEmpty(recipients))
			this.recipients = recipients.stream().map(UUID::fromString).collect(Collectors.toSet());
	}

	public PrivateChannel(Set<Chatter> recipients) {
		if (!isNullOrEmpty(recipients))
			this.recipients = recipients.stream().map(Chatter::getUuid).collect(Collectors.toSet());
	}

	public PrivateChannel(Chatter... recipients) {
		this.recipients = Arrays.stream(recipients).map(Chatter::getUuid).collect(Collectors.toSet());
	}

	public Set<Chatter> getRecipients() {
		return getRecipients(null);
	}

	@Override
	public Set<Chatter> getRecipients(Chatter chatter) {
		return recipients.stream().map(uuid -> new ChatterService().get(uuid)).collect(Collectors.toSet());
	}

	@ToString.Include
	public Set<String> getRecipientsNames() {
		return recipients.stream()
				.map(Name::of)
				.collect(Collectors.toSet());
	}

	public Set<Chatter> getOthers(Chatter chatter) {
		return recipients.stream()
				.filter(uuid -> !uuid.equals(chatter.getUuid()))
				.map(uuid -> new ChatterService().get(uuid))
				.collect(Collectors.toSet());
	}

	public Set<String> getOthersNames(Chatter chatter) {
		return getOthers(chatter).stream()
				.map(Nickname::of)
				.collect(Collectors.toSet());
	}

	@Override
	public String getAssignMessage(Chatter chatter) {
		return "Now chatting with &f" + String.join(", ", getOthersNames(chatter));
	}

	@Override
	public ChatColor getMessageColor() {
		return ChatColor.YELLOW;
	}

}

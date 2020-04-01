package me.pugabyte.bncore.features.chat.models;

import lombok.Data;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class PrivateChannel implements Channel {
	private Set<Chatter> recipients = new HashSet<>();

	public PrivateChannel(Chatter... recipients) {
		this.recipients = new HashSet<>(Arrays.asList(recipients));
	}

	public Set<Chatter> getOthers(Chatter chatter) {
		return recipients.stream()
				.filter(recipient -> !recipient.equals(chatter))
				.collect(Collectors.toSet());
	}

	public Set<String> getOthersNames(Chatter chatter) {
		return getOthers(chatter).stream()
				.map(recipient -> recipient.getOfflinePlayer().getName())
				.collect(Collectors.toSet());
	}

	@Override
	public Set<Chatter> getRecipients(Chatter chatter) {
		return recipients;
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

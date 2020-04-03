package me.pugabyte.bncore.features.chat;

import lombok.Getter;
import lombok.Setter;
import me.pugabyte.bncore.features.chat.events.PrivateChatEvent;
import me.pugabyte.bncore.features.chat.events.PublicChatEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.framework.exceptions.postconfigured.PlayerNotOnlineException;
import me.pugabyte.bncore.models.chat.Channel;
import me.pugabyte.bncore.models.chat.Chatter;
import me.pugabyte.bncore.models.chat.PrivateChannel;
import me.pugabyte.bncore.models.chat.PublicChannel;
import me.pugabyte.bncore.models.nerd.Nerd;
import me.pugabyte.bncore.utils.JsonBuilder;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static me.pugabyte.bncore.features.chat.Chat.PREFIX;
import static me.pugabyte.bncore.utils.StringUtils.stripColor;
import static me.pugabyte.bncore.utils.Utils.canSee;

public class ChatManager {
	@Getter
	private static List<PublicChannel> channels = new ArrayList<>();

	@Getter
	@Setter
	private static PublicChannel mainChannel;

	public static PublicChannel getChannel(String id) {
		Optional<PublicChannel> channel = channels.stream().filter(_channel -> _channel.getNickname().equalsIgnoreCase(id)).findFirst();
		if (!channel.isPresent())
			channel = channels.stream().filter(_channel -> _channel.getName().equalsIgnoreCase(id)).findFirst();
		if (!channel.isPresent())
			channel = channels.stream().filter(_channel -> _channel.getName().toLowerCase().startsWith(id.toLowerCase())).findFirst();

		return channel.orElseThrow(() -> new InvalidInputException("Channel not found"));
	}

	public static Optional<PublicChannel> getChannelByDiscordId(String id) {
		return channels.stream().filter(_channel -> _channel.getDiscordChannel() != null && _channel.getDiscordChannel().getId().equalsIgnoreCase(id)).findFirst();
	}

	public static void addChannel(PublicChannel channel) {
		channels.add(channel);
	}

	public static void process(Chatter chatter, Channel channel, String message) {
		if (chatter == null || message == null)
			return;

		message = message.trim();

		if (!chatter.getOfflinePlayer().getPlayer().hasPermission("group.admin"))
			message = stripColor(message);

		if (message.length() == 0)
			return;

		if (channel == null) {
			chatter.send(PREFIX + "&cYou are not speaking in a channel. &3Use &c/ch g &3to return to Global chat.");
			return;
		}

		Set<Chatter> recipients = channel.getRecipients(chatter);
		if (channel instanceof PublicChannel) {
			PublicChatEvent event = new PublicChatEvent(chatter, (PublicChannel) channel, message, recipients);
			Utils.callEvent(event);
			if (!event.isCancelled())
				process(event);
		} else if (channel instanceof PrivateChannel) {
			PrivateChatEvent event = new PrivateChatEvent(chatter, (PrivateChannel) channel, message, recipients);
			Utils.callEvent(event);
			if (!event.isCancelled())
				process(event);
		}
	}

	public static void process(PublicChatEvent event) {
		if (!event.wasSeen())
			Tasks.wait(1, () -> event.getChatter().send("&eNo one can hear you! Type &c/ch g &eto talk globally"));

		JsonBuilder json = new JsonBuilder()
				.next(event.getChannel().getColor() + "[" + event.getChannel().getNickname().toUpperCase() + "] ")
				.next(new Nerd(event.getChatter().getOfflinePlayer()).getChatFormat().trim())
				.next(" " + event.getChannel().getColor() + ChatColor.BOLD + "> ")
				.next(event.getChannel().getMessageColor() + event.getMessage());

		event.getRecipients().forEach(recipient -> recipient.send(json));

		Bukkit.getConsoleSender().sendMessage(stripColor(json.toString()));
	}

	public static void process(PrivateChatEvent event) {
		Set<String> othersNames = event.getChannel().getOthersNames(event.getChatter());

		JsonBuilder to = new JsonBuilder("&3&l[&bPM&3&l] &eTo &3" + String.join(", ", othersNames) + " &b&l> "
				+ event.getChannel().getMessageColor() + event.getMessage());
		JsonBuilder from = new JsonBuilder("&3&l[&bPM&3&l] &eFrom &3" + event.getChatter().getOfflinePlayer().getName() + " &b&l> "
				+ event.getChannel().getMessageColor() + event.getMessage());

		int seen = 0;
		for (Chatter recipient : event.getRecipients()) {
			recipient.setLastPrivateMessage(event.getChannel());

			if (!recipient.equals(event.getChatter())) {
				boolean canSee = canSee(event.getChatter().getOfflinePlayer(), recipient.getOfflinePlayer());
				String notOnline = new PlayerNotOnlineException(recipient.getOfflinePlayer()).getMessage();
				if (!recipient.getOfflinePlayer().isOnline())
					event.getChatter().send(PREFIX + notOnline);
				else {
					recipient.send(from);
					if (canSee)
						++seen;
					else
						event.getChatter().send(PREFIX + notOnline);
				}
			}
		}

		if (seen > 0)
			event.getChatter().send(to);

		Bukkit.getConsoleSender().sendMessage(event.getChatter().getOfflinePlayer().getName() + " -> " + String.join(", ", othersNames) + ": " + event.getMessage());
	}

}

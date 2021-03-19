package me.pugabyte.nexus.features.chat;

import lombok.Getter;
import lombok.Setter;
import me.pugabyte.nexus.features.chat.events.PrivateChatEvent;
import me.pugabyte.nexus.features.chat.events.PublicChatEvent;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.framework.exceptions.postconfigured.PlayerNotOnlineException;
import me.pugabyte.nexus.models.chat.Channel;
import me.pugabyte.nexus.models.chat.Chatter;
import me.pugabyte.nexus.models.chat.PrivateChannel;
import me.pugabyte.nexus.models.chat.PublicChannel;
import me.pugabyte.nexus.models.cooldown.CooldownService;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Time;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static me.pugabyte.nexus.utils.PlayerUtils.canSee;
import static me.pugabyte.nexus.utils.StringUtils.stripColor;

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

		return channel.orElseThrow(() -> new InvalidInputException("Channel '" + id + "' not found"));
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

		if (!PlayerUtils.isAdminGroup(chatter.getPlayer()))
			message = stripColor(message);

		if (message.length() == 0)
			return;

		if (channel == null) {
			chatter.send(Chat.PREFIX + "&cYou are not speaking in a channel. &3Use &c/ch g &3to return to Global chat.");
			return;
		}

		try {
			Set<Chatter> recipients = channel.getRecipients(chatter);
			if (channel instanceof PublicChannel) {
				PublicChannel publicChannel = (PublicChannel) channel;
				if (!chatter.canJoin(publicChannel))
					throw new InvalidInputException("You do not have permission to speak in that channel");

				if ("G".equals(publicChannel.getNickname())) {
					if ("Rai_Rai_".equals(chatter.getName())) {
						String id = "chat-" + publicChannel.getName().toLowerCase();
						CooldownService service = new CooldownService();
						if (!service.check(chatter.getUuid(), id, Time.SECOND.x(15)))
							throw new InvalidInputException("You are talking too fast! (&e" + service.getDiff(chatter.getUuid(), id) + " left&c)");
					}
				}

				PublicChatEvent event = new PublicChatEvent(chatter, publicChannel, message, message, recipients);
				if (event.callEvent())
					process(event);
			} else if (channel instanceof PrivateChannel) {
				PrivateChatEvent event = new PrivateChatEvent(chatter, (PrivateChannel) channel, message, message, recipients);
				if (event.callEvent())
					process(event);
			}
		} catch (InvalidInputException ex) {
			PlayerUtils.send(chatter.getPlayer(), Chat.PREFIX + "&c" + ex.getMessage());
		}
	}

	public static void process(PublicChatEvent event) {
		if (!event.wasSeen())
			Tasks.wait(1, () -> event.getChatter().send(Chat.PREFIX + "No one can hear you! Type &c/ch g &3to talk globally"));

		JsonBuilder json = new JsonBuilder()
				.next(event.getChannel().getChatterFormat(event.getChatter()))
				.next(event.getChannel().getMessageColor() + event.getMessage());

		event.getRecipients().forEach(recipient -> {
			if (PlayerUtils.isStaffGroup(recipient.getPlayer()) && !stripColor(event.getMessage()).equalsIgnoreCase(stripColor(event.getOriginalMessage()))) {
				JsonBuilder builder = new JsonBuilder()
						.next(event.getChannel().getChatterFormat(event.getChatter()))
						.next(event.getChannel().getMessageColor() + event.getMessage()).group();
				builder.next(" &7&o[Filtered]").hover(event.getOriginalMessage());
				recipient.send(builder);
			} else
				recipient.send(json);
		});

		Bukkit.getConsoleSender().sendMessage(stripColor(json.toString()));
	}

	public static void process(PrivateChatEvent event) {
		Set<String> othersNames = event.getChannel().getOthersNames(event.getChatter());

		JsonBuilder to = new JsonBuilder("&3&l[&bPM&3&l] &eTo &3" + String.join(", ", othersNames) + " &b&l> ")
				.next(event.getChannel().getMessageColor() + event.getMessage());
		JsonBuilder from = new JsonBuilder("&3&l[&bPM&3&l] &eFrom &3" + event.getChatter().getOfflinePlayer().getName() + " &b&l> ")
				.next(event.getChannel().getMessageColor() + event.getMessage());

		int seen = 0;
		for (Chatter recipient : event.getRecipients()) {
			recipient.setLastPrivateMessage(event.getChannel());

			if (!recipient.equals(event.getChatter())) {
				boolean canSee = canSee(event.getChatter().getOfflinePlayer(), recipient.getOfflinePlayer());
				String notOnline = new PlayerNotOnlineException(recipient.getOfflinePlayer()).getMessage();
				if (!recipient.getOfflinePlayer().isOnline())
					event.getChatter().send(Chat.PREFIX + notOnline);
				else {
					recipient.send(from);
					if (canSee)
						++seen;
					else
						event.getChatter().send(Chat.PREFIX + notOnline);
				}
			}
		}

		if (seen > 0)
			event.getChatter().send(to);

		Bukkit.getConsoleSender().sendMessage(event.getChatter().getOfflinePlayer().getName() + " -> " + String.join(", ", othersNames) + ": " + event.getMessage());
	}

}

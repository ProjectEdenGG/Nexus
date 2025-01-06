package gg.projecteden.nexus.features.chat;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.chat.events.PrivateChatEvent;
import gg.projecteden.nexus.features.chat.events.PublicChatEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.exceptions.postconfigured.PlayerNotOnlineException;
import gg.projecteden.nexus.models.chat.Channel;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.chat.PrivateChannel;
import gg.projecteden.nexus.models.chat.PublicChannel;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.audience.MessageType;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ChatManager {
	@Getter
	private static List<PublicChannel> channels = new ArrayList<>();

	@Getter
	@Setter
	private static PublicChannel mainChannel;

	public static PublicChannel getChannel(String id) {
		if (id == null) return null;
		Optional<PublicChannel> channel = channels.stream().filter(_channel -> _channel.getNickname().equalsIgnoreCase(id)).findFirst();
		if (!channel.isPresent())
			channel = channels.stream().filter(_channel -> _channel.getName().equalsIgnoreCase(id)).findFirst();
		if (!channel.isPresent())
			channel = channels.stream().filter(_channel -> _channel.getName().toLowerCase().startsWith(id.toLowerCase())).findFirst();

		return channel.orElseThrow(() -> new InvalidInputException("Channel '" + id + "' not found"));
	}

	public static Optional<PublicChannel> getChannelByDiscordId(String id) {
		return channels.stream().filter(_channel -> _channel.getDiscordTextChannel() != null && _channel.getDiscordTextChannel().getId().equalsIgnoreCase(id)).findFirst();
	}

	public static void addChannel(PublicChannel channel) {
		channels.add(channel);
	}

	public static void process(Chatter chatter, Channel channel, String originalMessage) {
		Tasks.async(() -> {
			String message = originalMessage;
			if (chatter == null || message == null)
				return;

			message = message.trim();

			if (!Rank.of(chatter.getOnlinePlayer()).isAdmin())
				message = StringUtils.decolorize(message).replaceAll("&", "&" + channel.getMessageColor());

			if (message.isEmpty())
				return;

			if (channel == null) {
				chatter.sendMessage(Chat.PREFIX + "&cYou are not speaking in a channel. &3Use &c/ch g &3to return to Global chat.");
				return;
			}

			try {
				Set<Chatter> recipients = channel.getRecipients(chatter);
				if (channel instanceof PublicChannel publicChannel) {
					if (!chatter.canJoin(publicChannel))
						throw new InvalidInputException(publicChannel.getJoinError());
					if (!chatter.isInValidWorld(publicChannel))
						throw new InvalidInputException("You cannot join that channel in this world");

					if ("G".equals(publicChannel.getNickname())) {
						if ("Tuniab".equals(chatter.getName())) {
							String id = "chat-" + publicChannel.getName().toLowerCase();
							CooldownService service = new CooldownService();
							if (!service.check(chatter.getUuid(), id, TickTime.SECOND.x(5)))
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
				PlayerUtils.send(chatter.getOnlinePlayer(), Chat.PREFIX + "&c" + ex.getMessage());
			}
		});
	}

	public static void process(PublicChatEvent event) {
		event.checkWasSeen();

		event.getRecipients().forEach(recipient -> {
			JsonBuilder json = event.getChannel().getChatterFormat(event.getChatter(), recipient, false)
				.color(event.getChannel().getMessageColor())
				.group()
				.next(event.getMessage());

			if (event.isFiltered())
				if (Rank.of(recipient).isStaff())
					json.next(" ")
						.group()
						.next("&c&l*")
						.hover("&cChat message was filtered")
						.hover("&cClick to see original message")
						.command("/echo &3Original message: &f" + event.getOriginalMessage());

			// TODO - 1.19.2 Chat Validation Kick
			// recipient.sendMessage(event, json, MessageType.CHAT);
			recipient.sendMessage(json);
		});

		JsonBuilder json = event.getChannel().getChatterFormat(event.getChatter(), null, false)
			.color(event.getChannel().getMessageColor())
			.group()
			.next(event.getMessage());

		if (event.isFiltered())
			json.next(" *");

		Bukkit.getConsoleSender().sendMessage(StringUtils.stripColor(json.toString()));
	}

	public static void process(PrivateChatEvent event) {
		JsonBuilder to = new JsonBuilder("&3&l[&bPM&3&l] &eTo &3" + event.getRecipientNames() + " &b&l> ")
				.next(event.getChannel().getMessageColor() + event.getMessage());
		JsonBuilder from = new JsonBuilder("&3&l[&bPM&3&l] &eFrom &3" + Nickname.of(event.getChatter()) + " &b&l> ")
				.next(event.getChannel().getMessageColor() + event.getMessage());

		int seen = 0;
		for (Chatter recipient : event.getRecipients()) {
			recipient.setLastPrivateMessage(event.getChannel());

			if (!recipient.equals(event.getChatter())) {
				boolean canSee = PlayerUtils.canSee(event.getChatter(), recipient);
				JsonBuilder notOnline = new JsonBuilder(Chat.PREFIX).next(new PlayerNotOnlineException(recipient).getJson());

				if (!recipient.isOnline())
					event.getChatter().sendMessage(notOnline);
				else {
					// TODO - 1.19.2 Chat Validation Kick
					// recipient.sendMessage(event, from, MessageType.CHAT);
					recipient.sendMessage(from);
					if (canSee)
						++seen;
					else
						event.getChatter().sendMessage(notOnline);
				}
			}
		}

		if (seen > 0) {
			// TODO - 1.19.2 Chat Validation Kick
			// event.getChatter().sendMessage(event, to, MessageType.CHAT);
			event.getChatter().sendMessage(to, MessageType.SYSTEM);
		}

		Bukkit.getConsoleSender().sendMessage(Nickname.of(event.getChatter()) + " -> " + event.getRecipientNames() + ": " + event.getMessage());
	}

}

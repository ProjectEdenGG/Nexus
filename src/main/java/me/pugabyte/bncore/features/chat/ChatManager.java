package me.pugabyte.bncore.features.chat;

import lombok.Getter;
import lombok.Setter;
import me.pugabyte.bncore.features.chat.models.Channel;
import me.pugabyte.bncore.features.chat.models.Chatter;
import me.pugabyte.bncore.features.chat.models.PrivateChannel;
import me.pugabyte.bncore.features.chat.models.PublicChannel;
import me.pugabyte.bncore.features.chat.models.events.ChannelChatEvent;
import me.pugabyte.bncore.features.chat.models.events.ChatEvent;
import me.pugabyte.bncore.features.chat.models.events.PrivateChatEvent;
import me.pugabyte.bncore.models.nerd.NerdService;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.features.chat.Chat.PREFIX;

public class ChatManager {
	@Getter
	private static Map<Player, Chatter> chatters = new HashMap<>();
	@Getter
	private static List<PublicChannel> channels = new ArrayList<>();

	@Getter
	@Setter
	private static PublicChannel mainChannel;

	public static Chatter getChatter(Player player) {
		chatters.computeIfAbsent(player, $ -> new Chatter(new NerdService().get(player)));
		return chatters.get(player);
	}

	public static Optional<PublicChannel> getChannel(String id) {
		Optional<PublicChannel> channel = channels.stream().filter(_channel -> _channel.getNickname().equalsIgnoreCase(id)).findFirst();
		if (!channel.isPresent())
			channel = channels.stream().filter(_channel -> _channel.getName().equalsIgnoreCase(id)).findFirst();
		if (!channel.isPresent())
			channel = channels.stream().filter(_channel -> _channel.getName().toLowerCase().startsWith(id.toLowerCase())).findFirst();

		return channel;
	}

	public static List<PublicChannel> getChannels(WorldGroup worldGroup) {
		return channels.stream()
				.filter(channel -> channel.getWorldGroup() == worldGroup)
				.collect(Collectors.toList());
	}

	public static void addChannel(PublicChannel channel) {
		channels.add(channel);
	}

	public static void process(Chatter chatter, Channel channel, String message) {
		if (channel == null) {
			chatter.send(PREFIX + "You are not speaking in a channel");
			return;
		}

		ChatEvent event = null;
		Set<Chatter> recipients = channel.getRecipients(chatter);
		if (channel instanceof PublicChannel)
			event = new ChannelChatEvent(chatter, (PublicChannel) channel, message, recipients);
		else if (channel instanceof PrivateChannel)
			event = new PrivateChatEvent(chatter, (PrivateChannel) channel, message, recipients);

		if (event != null)
			Utils.callEvent(event);
	}

}

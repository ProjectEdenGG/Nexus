package me.pugabyte.bncore.features.chat;

import lombok.Getter;
import lombok.Setter;
import me.pugabyte.bncore.features.chat.models.Channel;
import me.pugabyte.bncore.features.chat.models.Chatter;
import me.pugabyte.bncore.features.chat.models.events.ChatEvent;
import me.pugabyte.bncore.models.nerds.NerdService;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ChatManager {
	@Getter
	private static Map<Player, Chatter> chatters = new HashMap<>();
	@Getter
	private static List<Channel> channels = new ArrayList<>();

	@Getter
	@Setter
	private static Channel mainChannel;

	public static Chatter getChatter(Player player) {
		chatters.computeIfAbsent(player, $ -> new Chatter(new NerdService().get(player)));
		return chatters.get(player);
	}

	public static Optional<Channel> getChannel(String id) {
		Optional<Channel> channel = channels.stream().filter(_channel -> _channel.getNickname().equalsIgnoreCase(id)).findFirst();
		if (!channel.isPresent())
			channel = channels.stream().filter(_channel -> _channel.getName().equalsIgnoreCase(id)).findFirst();
		if (!channel.isPresent())
			channel = channels.stream().filter(_channel -> _channel.getName().toLowerCase().startsWith(id.toLowerCase())).findFirst();

		return channel;
	}

	public static void handleChat(ChatEvent event) {

	}

	public static void addChannel(Channel channel) {
		channels.add(channel);
	}

}

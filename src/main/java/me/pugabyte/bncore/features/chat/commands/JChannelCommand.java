package me.pugabyte.bncore.features.chat.commands;

import lombok.NonNull;
import me.pugabyte.bncore.features.chat.Chat;
import me.pugabyte.bncore.features.chat.ChatManager;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.PlayerNotOnlineException;
import me.pugabyte.bncore.models.chat.Channel;
import me.pugabyte.bncore.models.chat.ChatService;
import me.pugabyte.bncore.models.chat.Chatter;
import me.pugabyte.bncore.models.chat.PublicChannel;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.stream.Collectors;

@Aliases("jch")
public class JChannelCommand extends CustomCommand {
	private Chatter chatter;

	public JChannelCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = Chat.PREFIX;
		chatter = new ChatService().get(player());
	}

	@Path("<channel> [message...]")
	void changeChannel(PublicChannel channel, String message) {
		if (channel.equals(chatter.getActiveChannel()))
			error("You are already in that channel");

		if (isNullOrEmpty(message))
			chatter.setActiveChannel(channel);
		else
			quickMessage(channel, message);
	}

	@Path("qm <channel> <message...>")
	void quickMessage(PublicChannel channel, String message) {
		chatter.say(channel, message);
	}

	@ConverterFor({Channel.class, PublicChannel.class})
	PublicChannel convertToChannel(String value) {
		return ChatManager.getChannel(value);
	}

	@TabCompleterFor({Channel.class, PublicChannel.class})
	List<String> tabCompleteChannel(String filter) {
		return ChatManager.getChannels().stream()
				.filter(channel -> channel.getNickname().toLowerCase().startsWith(filter.toLowerCase()) ||
						channel.getName().toLowerCase().startsWith(filter.toLowerCase()))
				.map(PublicChannel::getNickname)
				.collect(Collectors.toList());
	}

	@ConverterFor(Chatter.class)
	Chatter convertToChatter(String value) {
		OfflinePlayer player = convertToOfflinePlayer(value);
		if (!player.isOnline())
			throw new PlayerNotOnlineException(player);
		return new ChatService().get(player);
	}

	@TabCompleterFor(Chatter.class)
	List<String> tabCompleteChatter(String filter) {
		return tabCompletePlayer(filter);
	}

}

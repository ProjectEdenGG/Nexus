package me.pugabyte.bncore.features.chat.commands;

import lombok.NonNull;
import me.pugabyte.bncore.features.chat.Chat;
import me.pugabyte.bncore.features.chat.ChatManager;
import me.pugabyte.bncore.features.chat.models.Channel;
import me.pugabyte.bncore.features.chat.models.Chatter;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.utils.WorldGroup;

import java.util.List;
import java.util.stream.Collectors;

@Aliases("jch")
public class JChannelCommand extends CustomCommand {
	private Chatter chatter;

	public JChannelCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = Chat.PREFIX;
		chatter = ChatManager.getChatter(player());
	}

	@Path("<channel>")
	void changeChannel(Channel channel) {
		if (channel.equals(chatter.getActiveChannel()))
			error("You are already in that channel");

		chatter.setActiveChannel(channel);
	}

	@Path("qm <channel> <message...>")
	void quickMessage(Channel channel, String message) {

	}

	@ConverterFor(Channel.class)
	Channel convertToChannel(String value) {
		return ChatManager.getChannel(value).orElseThrow(() -> new InvalidInputException("Channel not found"));
	}

	@TabCompleterFor(WorldGroup.class)
	List<String> tabCompleteWorldGroup(String filter) {
		return ChatManager.getChannels().stream()
				.filter(channel -> channel.getNickname().toLowerCase().startsWith(filter.toLowerCase()) ||
						channel.getName().toLowerCase().startsWith(filter.toLowerCase()))
				.map(Channel::getNickname)
				.collect(Collectors.toList());
	}

}

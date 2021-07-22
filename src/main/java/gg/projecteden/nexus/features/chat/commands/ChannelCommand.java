package gg.projecteden.nexus.features.chat.commands;

import gg.projecteden.nexus.features.chat.Chat;
import gg.projecteden.nexus.features.chat.ChatManager;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.chat.Channel;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.chat.ChatterService;
import gg.projecteden.nexus.models.chat.PublicChannel;
import lombok.NonNull;

import java.util.List;
import java.util.stream.Collectors;

@Aliases({"ch", "chat"})
@Redirect(from = "/qm", to = "/ch qm")
public class ChannelCommand extends CustomCommand {
	private final Chatter chatter;

	public ChannelCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = Chat.PREFIX;
		chatter = new ChatterService().get(player());
	}

	@Path("<channel> [message...]")
	void changeChannel(PublicChannel channel, String message) {
		if (!isNullOrEmpty(message))
			quickMessage(channel, message);
		else if (channel.equals(chatter.getActiveChannel()))
			error("You are already in that channel");
		else
			chatter.setActiveChannel(channel);
	}

	@Path("list [filter]")
	void list(String filter) {
		ChatManager.getChannels().forEach(channel -> {
			if (!isNullOrEmpty(filter) && !channel.getName().toLowerCase().startsWith(filter))
				return;

			if (chatter.canJoin(channel))
				send(channel.getColor() + "[" + channel.getNickname().toUpperCase() + "] " + channel.getName() + " " +
						(chatter.hasJoined(channel) ? chatter.getActiveChannel().equals(channel) ? "&a(Active)" : "&7(Joined)" : "&c(Left)"));
		});
	}

	@Path("qm <channel> <message...>")
	void quickMessage(PublicChannel channel, String message) {
		chatter.say(channel, message);
	}

	@Path("join <channel>")
	void join(PublicChannel channel) {
		chatter.join(channel);
	}

	@Path("leave <channel>")
	void leave(PublicChannel channel) {
		chatter.leave(channel);
	}

	@ConverterFor({Channel.class, PublicChannel.class})
	PublicChannel convertToChannel(String value) {
		return ChatManager.getChannel(value);
	}

	@TabCompleterFor({Channel.class, PublicChannel.class})
	List<String> tabCompleteChannel(String filter) {
		return ChatManager.getChannels().stream()
				.filter(channel -> {
					if (!new ChatterService().get(player()).canJoin(channel))
						return false;
					return channel.getNickname().toLowerCase().startsWith(filter.toLowerCase()) ||
							channel.getName().toLowerCase().startsWith(filter.toLowerCase());
				})
				.map(PublicChannel::getNickname)
				.collect(Collectors.toList());
	}

	@ConverterFor(Chatter.class)
	Chatter convertToChatter(String value) {
		return new ChatterService().get(convertToPlayer(value));
	}

}

package gg.projecteden.nexus.features.chat.commands;

import gg.projecteden.nexus.features.chat.Chat;
import gg.projecteden.nexus.features.chat.ChatManager;
import gg.projecteden.nexus.features.chat.events.ChannelChangeEvent;
import gg.projecteden.nexus.framework.commandsv2.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commandsv2.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Redirects.Redirect;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Vararg;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.HideFromHelp;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.TabCompleteIgnore;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.HideFromWiki;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.models.chat.Channel;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.chat.ChatterService;
import gg.projecteden.nexus.models.chat.PublicChannel;
import lombok.NonNull;

import java.util.List;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.utils.Nullables.isNullOrEmpty;

@Aliases({"ch", "channel"})
@Redirect(from = "/qm", to = "/ch qm")
public class ChatCommand extends CustomCommand {
	private final Chatter chatter;

	public ChatCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = Chat.PREFIX;
		chatter = new ChatterService().get(player());
	}

	@NoLiterals
	@Description("Switch to a channel or send a message without switching")
	void changeChannel(PublicChannel channel, @Optional String message) {
		if (!isNullOrEmpty(message)) {
			qm(channel, message);
			return;
		}

		final Channel currentChannel = chatter.getActiveChannel();
		if (channel.equals(currentChannel))
			error("You are already in that channel");

		if (!chatter.isInValidWorld(channel))
			error("You cannot join that channel in this world");

		chatter.setActiveChannel(channel);
		new ChannelChangeEvent(chatter, currentChannel, channel).callEvent();
	}

	@Description("List available channels")
	void list(@Optional String filter) {
		ChatManager.getChannels().forEach(channel -> {
			if (!isNullOrEmpty(filter) && !channel.getName().toLowerCase().startsWith(filter))
				return;

			if (chatter.canJoin(channel))
				send(channel.getColor() + "[" + channel.getNickname().toUpperCase() + "] " + channel.getName() + " " +
						(chatter.hasJoined(channel) ? chatter.getActiveChannel().equals(channel) ? "&a(Active)" : "&7(Joined)" : "&c(Left)"));
		});
	}

	@HideFromWiki
	@HideFromHelp
	@TabCompleteIgnore
	@Description("Send a message to a channel")
	void qm(PublicChannel channel, @Vararg String message) {
		chatter.say(channel, message);
	}

	@Description("Join a channel")
	void join(PublicChannel channel) {
		final Channel currentChannel = chatter.getActiveChannel();
		if (channel.equals(currentChannel))
			error("You are already in that channel");
		if (!chatter.isInValidWorld(channel))
			error("You cannot join that channel in this world");
		chatter.join(channel);
	}

	@Description("Leave a channel")
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

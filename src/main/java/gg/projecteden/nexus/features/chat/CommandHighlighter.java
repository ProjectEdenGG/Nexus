package gg.projecteden.nexus.features.chat;

import gg.projecteden.nexus.features.chat.events.ChatEvent;
import gg.projecteden.nexus.framework.commands.Commands;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandHighlighter {

	public static void process(ChatEvent event) {
		String message = event.getMessage();
		if (!message.contains("/"))
			return;

		final Pattern pattern = Pattern.compile("/[a-zA-Z\\d_-]+");
		final Matcher matcher = pattern.matcher(message);

		while (matcher.find()) {
			final String group = matcher.group();
			for (CustomCommand command : Commands.getUniqueCommands()) {
				if (!command.getAllAliases().contains(group.replaceFirst("/", "")))
					continue;

				message = message.replace(group, "&c" + group + event.getChannel().getMessageColor());
//				for (Method method : command.getPathMethods()) {
//					final String path = method.getAnnotation(Path.class).value();
//
//				}

				break;
			}
		}

		event.setMessage(message);
	}
}

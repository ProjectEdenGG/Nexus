package gg.projecteden.nexus.features.chat.commands;

import gg.projecteden.nexus.features.chat.Chat;
import gg.projecteden.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.PlayerNotOnlineException;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.chat.ChatterService;
import gg.projecteden.nexus.models.chat.PrivateChannel;
import gg.projecteden.nexus.models.mutemenu.MuteMenuUser;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.Nullables;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;

@Aliases({"m", "msg", "w", "whisper", "t", "tell", "pm", "dm"})
public class MessageCommand extends CustomCommand {
	private final Chatter chatter;

	public MessageCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = Chat.PREFIX;
		chatter = new ChatterService().get(player());
	}

	@Path("<player> [message...]")
	@Description("Private message a player")
	void message(OfflinePlayer to, String message) {
		// TODO New class, "HideablePlayer"?
		if (!to.isOnline())
			throw new PlayerNotOnlineException(to);

		if (isSelf(to))
			error("You cannot message yourself");

		if (MuteMenuUser.hasMuted(player(), MuteMenuItem.MESSAGES))
			error("You have messages disabled!");

		if (MuteMenuUser.hasMuted(to, MuteMenuItem.MESSAGES))
			error(Nickname.of(to) + " has messages disabled!");

		PrivateChannel dm = new PrivateChannel(chatter, new ChatterService().get(to));
		if (Nullables.isNullOrEmpty(message))
			chatter.setActiveChannel(dm);
		else
			chatter.say(dm, message);
	}
}

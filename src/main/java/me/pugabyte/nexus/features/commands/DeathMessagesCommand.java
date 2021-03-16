package me.pugabyte.nexus.features.commands;

import com.google.common.base.Strings;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.features.chat.Chat.StaticChannel;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.chat.ChatService;
import me.pugabyte.nexus.models.chat.Chatter;
import me.pugabyte.nexus.models.deathmessages.DeathMessages;
import me.pugabyte.nexus.models.deathmessages.DeathMessages.Behavior;
import me.pugabyte.nexus.models.deathmessages.DeathMessagesService;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import static me.pugabyte.nexus.features.discord.Discord.discordize;
import static me.pugabyte.nexus.utils.StringUtils.colorize;

@NoArgsConstructor
public class DeathMessagesCommand extends CustomCommand implements Listener {
	private final DeathMessagesService service = new DeathMessagesService();

	public DeathMessagesCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("behavior <behavior> [player]")
	void toggle(Behavior behavior, @Arg(value = "self", permission = "group.staff") OfflinePlayer player) {
		final DeathMessages deathMessages = service.get(player);

		deathMessages.setBehavior(behavior);
		service.save(deathMessages);
		send(PREFIX + "Set " + (isSelf(deathMessages) ? "your" : "&e" + player.getName() + "'s") + " &3death message behavior to &e" + camelCase(behavior));
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		final DeathMessagesService service = new DeathMessagesService();
		DeathMessages deathMessages = service.get(event.getEntity());

		String message = "&c☠ " + event.getDeathMessage();
		message = message.replaceFirst(event.getEntity().getName(), "&e" + event.getEntity().getName() + "&c");

		if (deathMessages.getBehavior() == Behavior.HIDDEN)
			event.setDeathMessage(null);

		if (Strings.isNullOrEmpty(event.getDeathMessage())) return;

		event.setDeathMessage(null);

		if (deathMessages.getBehavior() == Behavior.SHOWN) {
			Chat.broadcastIngame(colorize(message));

			if (WorldGroup.get(event.getEntity()) == WorldGroup.SURVIVAL)
				Chat.broadcastDiscord(discordize(message));
		} else if (deathMessages.getBehavior() == Behavior.LOCAL) {
			Chatter chatter = new ChatService().get(event.getEntity());
			for (Chatter recipient : StaticChannel.LOCAL.getChannel().getRecipients(chatter))
				recipient.send(message);
		}
	}

}

package me.pugabyte.nexus.features.commands;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.deathmessages.DeathMessages;
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

	@Path("toggle [enable] [player]")
	void toggle(Boolean enable, @Arg(value = "self", permission = "group.staff") OfflinePlayer player) {
		final DeathMessages deathMessages = service.get(player);

		if (enable == null)
			enable = !deathMessages.isShow();

		deathMessages.setShow(enable);
		service.save(deathMessages);
		send(PREFIX + (isSelf(deathMessages) ? "Your" : "&e" + player.getName() + "'s") + " &3death messages are now " + (enable ? "&ashown" : "&chidden"));
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		final DeathMessagesService service = new DeathMessagesService();
		DeathMessages deathMessages = service.get(event.getEntity());

		if (!deathMessages.isShow())
			event.setDeathMessage(null);

		if (event.getDeathMessage() == null)
			return;

		event.setDeathMessage("&c☠ " + event.getDeathMessage());
		event.setDeathMessage(event.getDeathMessage().replaceFirst(event.getEntity().getName(), "&e" + event.getEntity().getName() + "&c"));
		event.setDeathMessage(colorize(event.getDeathMessage()));

		if (WorldGroup.get(event.getEntity()) == WorldGroup.SURVIVAL)
			Chat.broadcastDiscord(discordize(event.getDeathMessage()));
	}

}

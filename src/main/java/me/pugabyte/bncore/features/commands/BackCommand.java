package me.pugabyte.bncore.features.commands;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.back.Back;
import me.pugabyte.bncore.models.back.BackService;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

@Aliases("return")
@NoArgsConstructor
public class BackCommand extends CustomCommand implements Listener {
	BackService service = new BackService();
	Back back;

	public BackCommand(CommandEvent event) {
		super(event);
		back = service.get(player());
	}

	static {
		BNCore.registerListener(new BackCommand());
	}

	@Path("[count]")
	void help(int count) {
		runCommand("essentials:back");
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		Location from = event.getFrom();
		Location to = event.getTo();

		if (Utils.isNPC(player)) return;
		if (TeleportCause.COMMAND != event.getCause()) return;

		if (!player.hasPermission("group.staff"))
			if (from.getWorld().equals(Minigames.getGameworld()))
				return;

		Back back = service.get(player);
		back.add(from);
		service.save(back);
	}

}

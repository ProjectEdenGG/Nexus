package me.pugabyte.bncore.features.commands;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.back.Back;
import me.pugabyte.bncore.models.back.BackService;
import me.pugabyte.bncore.utils.JsonBuilder;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.text.DecimalFormat;

@Aliases("return")
@NoArgsConstructor
public class BackCommand extends CustomCommand implements Listener {
	BackService service = new BackService();
	Back back;

	public BackCommand(CommandEvent event) {
		super(event);
		back = service.get(player());
	}

	@Path("[count]")
	void back(@Arg("1") int count) {
		if (!player().hasPermission("group.staff"))
			count = 1;

		Location location = null;
		if (back.getLocations().size() >= count)
			location = back.getLocations().get(count - 1);

		if (location == null)
			error("You have no back location");

		player().teleport(location, TeleportCause.COMMAND);
	}

	@Path("locations")
	@Permission("group.staff")
	void view() {
		if (back.getLocations() == null || back.getLocations().size() == 0)
			error("You have no back locations");

		int i = 0;
		JsonBuilder json = json(PREFIX + "Locations (&eClick to go&3):");

		for (Location location : back.getLocations()) {
			++i;
			int x = (int) location.getX(), y = (int) location.getY(), z = (int) location.getZ(),
					yaw = (int) location.getYaw(), pitch = (int) location.getPitch();
			json.newline()
					.next("&3" + new DecimalFormat("#00").format(i) + " &e" + location.getWorld().getName() +
							" &7/ &e" + x + " &7/ &e" + y + " &7/ &e" + z)
					.command("/tppos " + x + " " + y + " " + z + " " + yaw + " " + pitch + " " + location.getWorld().getName())
					.hover("&eClick to teleport");
		}

		send(json);
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		Location from = event.getFrom();
		Location to = event.getTo();

		if (Utils.isNPC(player)) return;
		if (TeleportCause.COMMAND != event.getCause()) return;

		if (!player.hasPermission("group.staff"))
			if (from.getWorld().equals(Minigames.getWorld()))
				return;

		Back back = new BackService().get(player);
		back.add(from);
		new BackService().save(back);
	}

}

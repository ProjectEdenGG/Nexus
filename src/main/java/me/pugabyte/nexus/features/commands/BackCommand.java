package me.pugabyte.nexus.features.commands;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.minigames.Minigames;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Description;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.back.Back;
import me.pugabyte.nexus.models.back.BackService;
import me.pugabyte.nexus.models.nerd.Rank;
import me.pugabyte.nexus.utils.CitizensUtils;
import me.pugabyte.nexus.utils.JsonBuilder;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.text.DecimalFormat;

@Aliases("return")
@NoArgsConstructor
@Description("Return to your previous location after teleporting")
public class BackCommand extends CustomCommand implements Listener {
	private final BackService service = new BackService();
	private Back back;

	public BackCommand(CommandEvent event) {
		super(event);
		back = service.get(player());
	}

	@Path("[count]")
	void back(@Arg(value = "1", permission = "group.staff", min = 1, max = 10) int count) {
		Location location = null;
		if (back.getLocations().size() >= count)
			location = back.getLocations().get(count - 1);

		if (location == null)
			error("You have no back location");

		player().teleportAsync(location, TeleportCause.COMMAND);
	}

	@Path("locations [player]")
	@Permission("group.staff")
	void view(@Arg("self") Back back) {
		if (back.getLocations() == null || back.getLocations().size() == 0)
			error("You have no back locations");

		int i = 0;
		JsonBuilder json = json(PREFIX + "Locations (&eClick to go&3):");

		for (Location location : back.getLocations()) {
			++i;
			int x = (int) location.getX(), y = (int) location.getY(), z = (int) location.getZ(),
					yaw = (int) location.getYaw(), pitch = (int) location.getPitch();
			json.group().newline()
					.next("&3" + new DecimalFormat("#00").format(i) + " &e" + location.getWorld().getName() +
							" &7/ &e" + x + " &7/ &e" + y + " &7/ &e" + z)
					.command("/tppos " + x + " " + y + " " + z + " " + yaw + " " + pitch + " " + location.getWorld().getName())
					.hover("&eClick to teleport");
		}

		send(json);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onTeleport(PlayerTeleportEvent event) {
		if (event.isCancelled()) return;
		Player player = event.getPlayer();
		Location location = event.getFrom();

		if (CitizensUtils.isNPC(player)) return;
		if (TeleportCause.COMMAND != event.getCause()) return;

		if (!Rank.of(player).isStaff())
			if (Minigames.isMinigameWorld(player.getWorld()))
				return;

		Back back = new BackService().get(player);
		back.add(location);
		new BackService().save(back);
	}

	@EventHandler
	public void onDeath(EntityDeathEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (!Rank.of(player).isStaff()) return;
		if (CitizensUtils.isNPC(player)) return;

		Back back = new BackService().get(player);
		back.add(player.getLocation());
		new BackService().save(back);
	}

}

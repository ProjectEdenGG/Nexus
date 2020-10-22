package me.pugabyte.bncore.features.commands.poof;

import de.bluecolored.bluemap.api.BlueMapAPI;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.annotations.Redirects;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.PlayerNotOnlineException;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.utils.Utils.RelativeLocation;
import me.pugabyte.bncore.utils.Utils.RelativeLocation.Modify;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static me.pugabyte.bncore.utils.Utils.getLocation;

@NoArgsConstructor
@Aliases({"tp", "tppos"})
@Redirects.Redirect(from = "/tpo", to = "/tp override")
public class TeleportCommand extends CustomCommand implements Listener {

	public TeleportCommand(@NonNull CommandEvent event) {
		super(event);
	}

	private boolean isCoord(String arg) {
		if (isNullOrEmpty(arg)) return false;
		if (arg.equals("~")) return true;
		arg = arg.replace("~", "");
		return isDouble(arg);
	}

	@Path("getCoords")
	void getCoords() {
		Location location = player().getLocation();
		String message = "/tppos " + (int) location.getX() + " " + (int) location.getY() + " " + (int) location.getZ() + " " + location.getWorld().getName();
		send(json(message).suggest(message));
	}

	@Path("override <player>")
	@Permission("group.seniorstaff")
	void override(Player player) {
		player().teleport(player);
		send(PREFIX + "Overriding teleport to &e" + player.getName());
	}

	@Path("<player> [player]")
	void run(@Arg(tabCompleter = OfflinePlayer.class) String arg1, @Arg(tabCompleter = OfflinePlayer.class) String arg2) {
		if (!player().hasPermission("group.staff")) {
			runCommand("tpa " + argsString());
			return;
		}

		if (arg1.matches("(http(s)?:\\/\\/)?(blue|staff)?map.bnn.gg/#[a-zA-Z0-9_]+(:-?[0-9]+(\\.[0-9]+)?){6}.*")) {
			String[] split = arg1.split("#");
			String[] coords = split[1].split(":");
			AtomicReference<String> worldName = new AtomicReference<>(coords[0]);
			BlueMapAPI.getInstance().flatMap(api -> api.getMap(worldName.get())).ifPresent(map -> worldName.set(map.getWorld().getSaveFolder().toFile().getName()));

			World world = Bukkit.getWorld(worldName.get());
			if (world == null)
				error("World &e" + worldName + " &cnot found");

			double x = Double.parseDouble(coords[1]);
			double z = Double.parseDouble(coords[2]);
			double terrainHeight = Double.parseDouble(coords[6]);
			double aboveGround = Double.parseDouble(coords[4]);
			double y = terrainHeight + aboveGround;
			if (y > 275) y = 275;

			Location location = new Location(world, x, y, z, 0, 90);
			player().teleport(location, TeleportCause.COMMAND);
			return;
		}

		if (isCoord(arg(1)) && isCoord(arg(2)) && isCoord(arg(3))) {
			Location location = player().getLocation();
			Modify modifier = RelativeLocation.modify(location).x(arg(1)).y(arg(2)).z(arg(3));
			if (arg(4) != null && arg(5) != null && isCoord(arg(4)) && isCoord(arg(5))) {
				modifier.yaw(arg(4)).pitch(arg(5));
				if (arg(6) != null) {
					if (Bukkit.getWorld(arg(6)) == null)
						error("World &e" + arg(6) + " &cnot found");
					location.setWorld(Bukkit.getWorld(arg(6)));
				}
			} else if (!isNullOrEmpty(arg(4))) {
				if (Bukkit.getWorld(arg(4)) == null)
					error("World &e" + arg(4) + " &cnot found");
				else
					location.setWorld(Bukkit.getWorld(arg(4)));
			}

			player().teleport(modifier.update(), TeleportCause.COMMAND);
		} else if (isOfflinePlayerArg(1)) {
			OfflinePlayer player1 = offlinePlayerArg(1);
			Location location1 = getLocation(player1);
			if (isOfflinePlayerArg(2)) {
				OfflinePlayer player2 = offlinePlayerArg(2);
				if (player1.isOnline() && player1.getPlayer() != null) {
					if (checkTeleportDisabled(player1.getPlayer(), player2))
						return;

					player1.getPlayer().teleport(getLocation(player2), TeleportCause.COMMAND);
					send(PREFIX + "Poofing to &e" + player2.getName() + (player2.isOnline() ? "" : " &3(Offline)"));
				} else
					throw new PlayerNotOnlineException(player1);
			} else {
				if (checkTeleportDisabled(player(), player1))
					return;

				player().teleport(location1, TeleportCause.COMMAND);
				send(PREFIX + "Poofing to &e" + player1.getName() + (player1.isOnline() ? "" : " &3(Offline)"));
			}
		} else {
			send("&c/" + getAliasUsed() + " <player> [player]");
			send("&c/" + getAliasUsed() + " <x> <y> <z> [yaw] [pitch] [world]");
		}
	}

	private boolean checkTeleportDisabled(Player from, OfflinePlayer to) {
		SettingService settingService = new SettingService();
		Setting setting = settingService.get(to, "tpDisable");
		if (setting.getBoolean()) {
			send(PREFIX + "&cThat player has teleports disabled. Sending a request instead");
			runCommand(from, "tpa " + argsString());
			return true;
		}
		return false;
	}

	private static final Set<UUID> lockTeleports = new HashSet<>();

	@Path("toggle <player> [enable]")
	@Permission("group.admin")
	void lock(Player player, Boolean enable) {
		UUID uuid = player.getUniqueId();
		if (enable == null)
			enable = !lockTeleports.contains(uuid);

		if (enable) {
			lockTeleports.add(uuid);
			send(PREFIX + "&cPreventing &3teleports from &e" + player.getName());
		} else {
			lockTeleports.remove(uuid);
			send(PREFIX + "&aAllowing &3teleports from &e" + player.getName());
		}
	}

	@Path("disable")
	@Permission("ladder.architect")
	void disable() {
		SettingService settingService = new SettingService();
		Setting setting = settingService.get(player(), "tpDisable");
		boolean bol = setting.getBoolean();
		setting.setBoolean(!bol);
		settingService.save(setting);
		send(PREFIX + "Teleports to you have been " + (bol ? "&aenabled" : "&cdisabled"));
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		if (lockTeleports.contains(event.getPlayer().getUniqueId()))
			event.setCancelled(true);
	}

}

package gg.projecteden.nexus.features.commands.poof;

import de.bluecolored.bluemap.api.BlueMapAPI;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.PlayerNotOnlineException;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.setting.Setting;
import gg.projecteden.nexus.models.setting.SettingService;
import gg.projecteden.nexus.utils.LocationUtils.RelativeLocation;
import gg.projecteden.nexus.utils.LocationUtils.RelativeLocation.Modify;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static gg.projecteden.nexus.utils.StringUtils.getTeleportCommand;

@NoArgsConstructor
@Aliases({"tp", "tppos"})
@Redirect(from = "/tpo", to = "/tp override")
public class TeleportCommand extends CustomCommand implements Listener {
	private static final String mapLinkRegex = "(http(s)?:\\/\\/)?(blue|staff)?map." + Nexus.DOMAIN + "/#[a-zA-Z\\d_]+(:-?[\\d]+(\\.[\\d]+)?){8}.*";

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
	@Permission(Group.STAFF)
	void getCoords() {
		String message = getTeleportCommand(location());
		send(json(PREFIX + "Click to copy").copy(message).hover(message));
	}

	@Path("<player> [player]")
	void run(@Arg(tabCompleter = OfflinePlayer.class) String arg1, @Arg(tabCompleter = OfflinePlayer.class) String arg2) {
		if (!isStaff()) {
			runCommand("tpa " + argsString());
			return;
		}

		if (arg1.matches(mapLinkRegex))
			map();
		else if (getAliasUsed().equalsIgnoreCase("tppos") || (isCoord(arg(1)) && isCoord(arg(2)) && isCoord(arg(3))))
			coordinates();
		else if (isOfflinePlayerArg(1))
			players();
		else {
			send("&c/" + getAliasUsed() + " <player> [player]");
			send("&c/" + getAliasUsed() + " <x> <y> <z> [yaw] [pitch] [world]");
		}
	}

	private void map() {
		String[] split = arg(1).split("#");
		String[] coords = split[1].split(":");
		AtomicReference<String> worldName = new AtomicReference<>(coords[0]);
		BlueMapAPI.getInstance().flatMap(api -> api.getMap(worldName.get())).ifPresent(map -> worldName.set(map.getWorld().getSaveFolder().toFile().getName()));

		World world = Bukkit.getWorld(worldName.get());
		if (world == null)
			error("World &e" + worldName + " &cnot found");

		double x = Double.parseDouble(coords[1]);
		double z = Double.parseDouble(coords[3]);
		double terrainHeight = Double.parseDouble(coords[2]);
		double aboveGround = Double.parseDouble(coords[4]);
		double y = terrainHeight + aboveGround;
		if (y > 275) y = 275;

		Location location = new Location(world, x, y, z, 0, 90);
		player().teleportAsync(location, TeleportCause.COMMAND);
	}

	private void players() {
		OfflinePlayer player1 = offlinePlayerArg(1);
		Location location1 = Nerd.of(player1).getLocation();
		if (isOfflinePlayerArg(2)) {
			OfflinePlayer player2 = offlinePlayerArg(2);
			if (player1.isOnline() && player1.getPlayer() != null) {
				if (checkTeleportDisabled(player1.getPlayer(), player2))
					return;

				player1.getPlayer().teleportAsync(Nerd.of(player2).getLocation(), TeleportCause.COMMAND);
				send(PREFIX + "Poofing to &e" + Nickname.of(player2) + (player2.isOnline() ? "" : " &3(Offline)"));
			} else
				throw new PlayerNotOnlineException(player1);
		} else {
			if (checkTeleportDisabled(player(), player1))
				return;

			player().teleportAsync(location1, TeleportCause.COMMAND);
			send(PREFIX + "Poofing to &e" + Nickname.of(player1) + (player1.isOnline() ? "" : " &3(Offline)"));
		}
	}

	private void coordinates() {
		Location location = location();
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

		player().teleportAsync(modifier.update(), TeleportCause.COMMAND);
	}

	private boolean checkTeleportDisabled(Player from, OfflinePlayer to) {
		SettingService settingService = new SettingService();
		Setting setting = settingService.get(to, "tpDisable");
		if (setting.getBoolean()) {
			Rank fromRank = Rank.of(from);
			Rank toRank = Rank.of(to);
			if (fromRank.ordinal() > toRank.ordinal())
				if (!(Arrays.asList(Rank.BUILDER, Rank.ARCHITECT).contains(toRank) && fromRank == Rank.MODERATOR))
					return false;

			PlayerUtils.send(to, PREFIX + "&c" + from.getName() + " tried to teleport to you, but you have teleports disabled");

			send(PREFIX + "&cThat player has teleports disabled. Sending a request instead");
			runCommand(from, "tpa " + argsString());
			return true;
		}
		return false;
	}

	private static final Set<UUID> preventTeleports = new HashSet<>();

	@Path("freeze <player> [enable]")
	@Permission(Group.ADMIN)
	void lock(Player player, Boolean enable) {
		UUID uuid = player.getUniqueId();
		if (enable == null)
			enable = !preventTeleports.contains(uuid);

		if (enable) {
			preventTeleports.add(uuid);
			send(PREFIX + "&cPreventing &3teleports from &e" + player.getName());
		} else {
			preventTeleports.remove(uuid);
			send(PREFIX + "&aAllowing &3teleports from &e" + player.getName());
		}
	}

	@Path("toggle")
	@Permission("ladder.builder")
	void disable() {
		SettingService settingService = new SettingService();
		Setting setting = settingService.get(player(), "tpDisable");
		boolean enable = setting.getBoolean();
		setting.setBoolean(!enable);
		settingService.save(setting);
		send(PREFIX + "Teleports to you have been " + (enable ? "&aenabled" : "&cdisabled"));
	}

	@Path("override <player>")
	@Permission(Group.SENIOR_STAFF)
	void override(Player player) {
		player().teleportAsync(player.getLocation());
		send(PREFIX + "Overriding teleport to &e" + player.getName());
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		if (preventTeleports.contains(event.getPlayer().getUniqueId()))
			event.setCancelled(true);
	}

}

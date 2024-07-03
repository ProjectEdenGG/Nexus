package gg.projecteden.nexus.features.commands.teleport;

import de.bluecolored.bluemap.api.BlueMapAPI;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.commands.models.events.CommandRunEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.CommandCooldownException;
import gg.projecteden.nexus.framework.exceptions.postconfigured.PlayerNotOnlineException;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.teleport.TeleportUser;
import gg.projecteden.nexus.models.teleport.TeleportUserService;
import gg.projecteden.nexus.utils.LocationUtils.RelativeLocation;
import gg.projecteden.nexus.utils.LocationUtils.RelativeLocation.Modify;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
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
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import static gg.projecteden.nexus.utils.Nullables.isNullOrEmpty;
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
		if ("~".equals(arg)) return true;
		arg = arg.replace("~", "");
		return isDouble(arg);
	}

	@Path("generateCommand")
	@Permission(Group.STAFF)
	@Description("Print a copyable command that teleports you to your current location")
	void getCoords() {
		String message = getTeleportCommand(location());
		send(json(PREFIX + "Click to copy").copy(message).hover(message));
	}

	@Path("<player> [player] [--keepVelocity]")
	@Description("Teleport using a player name, coordinates, or a map link")
	void run(@Arg(tabCompleter = OfflinePlayer.class) String arg1, @Arg(tabCompleter = OfflinePlayer.class) String arg2, @Switch boolean keepVelocity) {
		if (!isStaff()) {
			runCommand("tpa " + argsString());
			return;
		}

		final Vector velocity = player().getVelocity();

		final CompletableFuture<Boolean> teleporter;

		if (arg1.matches(mapLinkRegex))
			teleporter = map();
		else if (getAliasUsed().equalsIgnoreCase("tppos") || (isCoord(arg(1)) && isCoord(arg(2)) && isCoord(arg(3))))
			teleporter = coordinates();
		else if (isOfflinePlayerArg(1))
			teleporter = players();
		else {
			send("&c/" + getAliasUsed() + " <player> [player]");
			send("&c/" + getAliasUsed() + " <x> <y> <z> [yaw] [pitch] [world]");
			return;
		}

		teleporter.thenRun(() -> {
			if (keepVelocity)
				player().setVelocity(velocity);
		});
	}

	private @NotNull CompletableFuture<Boolean> map() {
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
		return player().teleportAsync(location, TeleportCause.COMMAND);
	}

	private @NotNull CompletableFuture<Boolean> players() {
		String cooldownType = this.getName() + "#" + ((CommandRunEvent) this.getEvent()).getMethod().getName();

		OfflinePlayer player1 = offlinePlayerArg(1);
		Location location1 = Nerd.of(player1).getLocation();
		if (isOfflinePlayerArg(2)) {
			OfflinePlayer player2 = offlinePlayerArg(2);
			if (player1.isOnline() && player1.getPlayer() != null) {
				checkTeleportDisabled(player1.getPlayer(), player2);

				if (rank().lt(Rank.of(player2))) {
					if (!new CooldownService().check(player(), "command:" + cooldownType, TickTime.SECOND.x(5)))
						throw new CommandCooldownException(uuid(), cooldownType);
				}

				send(PREFIX + "Poofing to &e" + Nickname.of(player2) + (player2.isOnline() ? "" : " &3(Offline)"));
				return player1.getPlayer().teleportAsync(Nerd.of(player2).getLocation(), TeleportCause.COMMAND);
			} else
				throw new PlayerNotOnlineException(player1);
		} else {
			checkTeleportDisabled(player(), player1);

			if (rank().lt(Rank.of(player1))) {
				if (!new CooldownService().check(player(), "command:" + cooldownType, TickTime.SECOND.x(5)))
					throw new CommandCooldownException(uuid(), cooldownType);
			}

			send(PREFIX + "Poofing to &e" + Nickname.of(player1) + (player1.isOnline() ? "" : " &3(Offline)"));
			return player().teleportAsync(location1, TeleportCause.COMMAND);
		}
	}

	private @NotNull CompletableFuture<Boolean> coordinates() {
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

		return player().teleportAsync(modifier.update(), TeleportCause.COMMAND);
	}

	private void checkTeleportDisabled(Player from, OfflinePlayer to) {
		final TeleportUser user = new TeleportUserService().get(to);
		if (user.canBeTeleportedTo())
			return;

		if (Rank.of(from).isSeniorStaff() && Rank.of(from).gt(Rank.of(to)))
			return;

		PlayerUtils.send(to, PREFIX + "&c" + from.getName() + " tried to teleport to you, but you have teleports disabled");

		runCommand(from, "tpa " + argsString());
		error("&cThat player has teleports disabled. Sending a request instead");
	}

	private static final Set<UUID> preventTeleports = new HashSet<>();

	@Path("freeze <player> [enable]")
	@Permission(Group.ADMIN)
	@Description("Prevent a player from teleporting")
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
	@Permission(Group.STAFF)
	@Description("Toggle preventing lower ranked staff from teleporting to you")
	void disable() {
		new TeleportUserService().edit(player(), user -> {
			user.canBeTeleportedTo(!user.canBeTeleportedTo());
			send(PREFIX + "Teleports to you have been " + (user.canBeTeleportedTo() ? "&aenabled" : "&cdisabled"));
		});
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		if (preventTeleports.contains(event.getPlayer().getUniqueId())) {
			event.setCancelled(true);
			return;
		}

		// Best way I could think of respecting disabled teleports
		if (WorldGroup.of(event.getPlayer()) != WorldGroup.MINIGAMES && event.getCause() == TeleportCause.SPECTATE) {
			event.setCancelled(true);
			PlayerUtils.send(event.getPlayer(), PREFIX + "This feature has been disabled");
		}
	}

}

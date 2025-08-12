package gg.projecteden.nexus.features.commands;

import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.nexus.features.commands.staff.CheatsCommand;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Cooldown;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.WikiConfig;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.mode.ModeUser;
import gg.projecteden.nexus.models.mode.ModeUser.FlightMode;
import gg.projecteden.nexus.models.mode.ModeUserService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.IOUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Set;

@Permission("essentials.fly")
@WikiConfig(rank = "Guest", feature = "Creative")
@NoArgsConstructor
@Cooldown(value = TimeUtils.TickTime.SECOND, x = 10, bypass = Group.SENIOR_STAFF)
public class FlyCommand extends CustomCommand implements Listener {
	private final ModeUserService service = new ModeUserService();
	private ModeUser user;

	@Getter
	public static Set<Player> debuggers = new HashSet<>();

	public FlyCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			user = service.get(player());
	}

	@Permission(Group.STAFF)
	@Path("debug [enable]")
	@Description("Toggle debug mode for flight")
	void debug(Boolean enable) {
		if (enable == null)
			enable = !debuggers.contains(player());

		if (enable)
			debuggers.add(player());
		else
			debuggers.remove(player());

		send(PREFIX + "Debugging " + (enable ? "&aenabled" : "&cdisabled"));
	}

	@Path("[enable] [player]")
	@Description("Toggle flight mode")
	void run(Boolean enable, @Arg(value = "self", permission = Group.SENIOR_STAFF) Player player) {
		if (!isSelf(player))
			user = service.get(player);

		if (enable == null)
			enable = !player.getAllowFlight();

		if (!enable && GameMode.SPECTATOR.equals(player.getGameMode()))
			error("You cannot disable fly in spectator mode");

		if (Minigamer.of(player).isPlaying() && !Rank.of(player).isSeniorStaff())
			error("Cannot use in minigames");

		if (enable && !CheatsCommand.canEnableCheats(player))
			throw new InvalidInputException("You cannot enable cheats in this world");

		if (enable)
			on(player);
		else
			off(player);

		send(player, PREFIX + (enable ? "&aEnabled" : "&cDisabled"));
		if (!isSelf(player))
			send(PREFIX + "Fly " + (enable ? "&aenabled" : "&cdisabled") + " &3for &e" + player.getName());
	}

	public static void off(Player player) {
		player.setFallDistance(0);
		PlayerUtils.setAllowFlight(player, false, FlyCommand.class);
		PlayerUtils.setFlying(player, false, FlyCommand.class);
		IOUtils.fileAppend("cheats", Nickname.of(player) + " disabled fly at " + StringUtils.xyzw(player.getLocation()));
	}

	public static void on(Player player) {
		player.setFallDistance(0);
		PlayerUtils.setAllowFlight(player, true, FlyCommand.class);

		var service = new ModeUserService();
		var user = service.get(player);
		var worldGroup = WorldGroup.of(player);
		if (worldGroup != WorldGroup.MINIGAMES && user.getRank().isStaff()) {
			user.setFlightMode(worldGroup);
			service.save(user);
		}

		IOUtils.fileAppend("cheats", Nickname.of(player) + " enabled fly at " + StringUtils.xyzw(player.getLocation()));
	}

	@EventHandler
	void on(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (!Rank.of(player).isStaff())
			return;

		ModeUserService userService = new ModeUserService();
		ModeUser user = userService.get(player);
		WorldGroup worldGroup = WorldGroup.of(player);

		user.setFlightMode(worldGroup);
		userService.save(user);
	}

	@EventHandler
	void on(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (!Rank.of(player).isStaff())
			return;

		ModeUserService userService = new ModeUserService();
		WorldGroup worldGroup = WorldGroup.of(player);

		ModeUser user = userService.get(player);
		FlightMode flightMode = user.getFlightMode(worldGroup);

		Tasks.wait(1, () -> { // Necessary
			if (flightMode.isAllowFlight())
				PlayerUtils.setAllowFlight(player, true, FlyCommand.class);

			if (flightMode.isFlying())
				PlayerUtils.setFlying(player, true, FlyCommand.class);
		});
	}

}

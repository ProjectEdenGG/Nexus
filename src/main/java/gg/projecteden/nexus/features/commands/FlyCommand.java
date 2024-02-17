package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.WikiConfig;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.mode.ModeUser;
import gg.projecteden.nexus.models.mode.ModeUser.FlightMode;
import gg.projecteden.nexus.models.mode.ModeUserService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.IOUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@Permission("essentials.fly")
@WikiConfig(rank = "Guest", feature = "Creative")
@NoArgsConstructor
public class FlyCommand extends CustomCommand implements Listener {
	private final ModeUserService service = new ModeUserService();
	private ModeUser user;

	public FlyCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			user = service.get(player());
	}

	@Path("[enable] [player]")
	@Description("Toggle flight mode")
	void run(Boolean enable, @Arg(value = "self", permission = Group.STAFF) Player player) {
		if (!isSelf(player))
			user = service.get(player);

		if (enable == null)
			enable = !player.getAllowFlight();

		if (!enable && GameMode.SPECTATOR.equals(player.getGameMode()))
			error("You cannot disable fly in spectator mode");

		if (Minigamer.of(player).isPlaying() && !Rank.of(player).isSeniorStaff())
			error("Cannot use in minigames");

		if (enable)
			on(player);
		else
			off(player);

		send(player, PREFIX + (enable ? "&aEnabled" : "&cDisabled"));
		if (!isSelf(player))
			send(PREFIX + "Fly " + (enable ? "&aenabled" : "&cdisabled") + " &3for &e" + player.getName());

		if (!(worldGroup() == WorldGroup.MINIGAMES) && user.getRank().isStaff()) {
			user.setFlightMode(worldGroup(), player.getAllowFlight(), player.isFlying());
			service.save(user);
		}
	}

	public static void off(Player player) {
		player.setFallDistance(0);
		player.setAllowFlight(false);
		player.setFlying(false);
		IOUtils.fileAppend("cheats", Nickname.of(player) + " disabled fly at " + StringUtils.getShortLocationString(player.getLocation()));
	}

	public static void on(Player player) {
		player.setFallDistance(0);
		player.setAllowFlight(true);
		IOUtils.fileAppend("cheats", Nickname.of(player) + " enabled fly at " + StringUtils.getShortLocationString(player.getLocation()));
	}

	@EventHandler
	void on(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (!Rank.of(player).isStaff())
			return;

		ModeUserService userService = new ModeUserService();
		ModeUser user = userService.get(player);
		WorldGroup worldGroup = WorldGroup.of(player);

		user.setFlightMode(worldGroup, player.getAllowFlight(), player.isFlying());
		userService.save(user);
	}

	@EventHandler
	void on(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		if (!Rank.of(player).isStaff())
			return;

		ModeUserService userService = new ModeUserService();
		WorldGroup worldGroup = WorldGroup.of(player);

		ModeUser user = userService.get(player);
		FlightMode flightMode = user.getFlightMode(worldGroup);

		Tasks.wait(1, () -> { // Necessary
			if (flightMode.isAllowFlight())
				player.setAllowFlight(true);

			if (flightMode.isFlying())
				player.setFlying(true);
		});
	}

}

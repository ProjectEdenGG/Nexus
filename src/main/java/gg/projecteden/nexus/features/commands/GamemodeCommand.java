package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.features.listeners.events.PlayerChangingWorldEvent;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromHelp;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.WikiConfig;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.mode.ModeUser;
import gg.projecteden.nexus.models.mode.ModeUser.FlightMode;
import gg.projecteden.nexus.models.mode.ModeUserService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static gg.projecteden.nexus.features.vanish.Vanish.isUnvanished;

@Aliases("gm")
@NoArgsConstructor
@Permission("essentials.gamemode")
@Redirect(from = {"/gms", "/gm0"}, to = "/gm s")
@Redirect(from = {"/gmc", "/gm1"}, to = "/gm c")
@Redirect(from = {"/gma", "/gm2", "/adventure"}, to = "/gm a")
@Redirect(from = {"/gmsp", "/gm3", "/spectator", "/spec"}, to = "/gm sp")
@WikiConfig(rank = "Guest", feature = "Creative")
public class GamemodeCommand extends CustomCommand implements Listener {

	private final ModeUserService service = new ModeUserService();
	private ModeUser user;

	public GamemodeCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent()) {
			user = service.get(player());

			if (rank() == Rank.GUEST && worldGroup() != WorldGroup.CREATIVE)
				permissionError();
		}
	}

	@Path("<gamemode> [player]")
	@Description("Change your gamemode")
	void run(GameMode gamemode, @Arg("self") Player player) {
		if (!isSelf(player)) {
			checkPermission("essentials.gamemode.others");
			user = service.get(player);
		}

		checkPermission("essentials.gamemode." + gamemode.name().toLowerCase());

		if (Minigamer.of(player).isPlaying() && !Rank.of(player).isSeniorStaff())
			error("Cannot use in minigames");

		setGamemode(player, gamemode);

		send(player, PREFIX + "Switched to &e" + camelCase(gamemode));
		if (!isSelf(player))
			send(PREFIX + "Switched to &e" + camelCase(gamemode) + " &3for &e" + player.getName());

		if (user.getRank().isStaff()) {
			user.setGameMode(worldGroup(), gamemode);
			service.save(user);
		}
	}

	public static void setGamemode(Player player, GameMode gamemode) {
		boolean flight = player.getAllowFlight();
		boolean flying = player.isFlying();
		player.setGameMode(gamemode);
		PlayerUtils.setAllowFlight(player, flight, "GamemodeCommand#setGameMode");
		PlayerUtils.setFlying(player, flying, "GamemodeCommand#setGameMode");

		if (gamemode == GameMode.CREATIVE || gamemode == GameMode.SPECTATOR) {
			PlayerUtils.setAllowFlight(player, true, "GamemodeCommand#setGameMode(" + gamemode + ")");
			PlayerUtils.setFlying(player, true, "GamemodeCommand#setGameMode(" + gamemode + ")");
		}

		if (gamemode == GameMode.SURVIVAL && isUnvanished(player)) {
			PlayerUtils.setAllowFlight(player, false, "GamemodeCommand#setGameMode(" + gamemode + ")");
			PlayerUtils.setFlying(player, false, "GamemodeCommand#setGameMode(" + gamemode + ")");
		}
	}

	@ConverterFor(GameMode.class)
	GameMode convertToGameMode(String value) {
		if (value != null)
			if ("3".equals(value) || value.startsWith("sp")) return GameMode.SPECTATOR;
			else if ("2".equals(value) || value.startsWith("a")) return GameMode.ADVENTURE;
			else if ("1".equals(value) || value.startsWith("c")) return GameMode.CREATIVE;
			else if ("0".equals(value) || value.startsWith("s")) return GameMode.SURVIVAL;
		throw new InvalidInputException("Invalid gamemode");
	}

	@TabCompleterFor(GameMode.class)
	List<String> tabCompleteGameMode(String filter) {
		return Arrays.stream(GameMode.values())
			.filter(gamemode -> player().hasPermission("essentials.gamemode." + gamemode.name().toLowerCase()))
			.map(gamemode -> gamemode.name().toLowerCase())
			.filter(name -> name.toLowerCase().startsWith(filter.toLowerCase()))
			.toList();
	}

	@Path("debug")
	@HideFromWiki
	@HideFromHelp
	@TabCompleteIgnore
	@Permission(Group.STAFF)
	void debug() {
		send(new ModeUserService().get(player()).toPrettyString());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void on(PlayerChangingWorldEvent event) {
		Player player = event.getPlayer();
		final WorldGroup newWorldGroup = WorldGroup.of(event.getToWorld());

		if (Minigamer.of(player).isPlaying())
			return;

		final Consumer<Boolean> flying = state -> {
			PlayerUtils.setAllowFlight(player, state, "GamemodeCommand#onWorldChanged 1");
			PlayerUtils.setFlying(player, state, "GamemodeCommand#onWorldChanged 1");
		};

		if (!Rank.of(player).isStaff()) {
			SpeedCommand.resetSpeed(player);

			if (newWorldGroup == WorldGroup.CREATIVE)
				flying.accept(true);
			else
				flying.accept(false);

			return;
		}

		if (player.getGameMode().equals(GameMode.SPECTATOR)) {
			player.setFallDistance(0);
			flying.accept(true);
			return;
		}

		Tasks.wait(5, () -> {
			ModeUser mode = new ModeUserService().get(player);
			GameMode gameMode = mode.getGamemode(newWorldGroup);
			FlightMode flightMode = mode.getFlightMode(newWorldGroup);

			setGamemode(player, gameMode);

			if (Vanish.isVanished(player)) {
				flightMode.setAllowFlight(true);
				flightMode.setFlying(true);
			}

			PlayerUtils.setAllowFlight(player, flightMode.isAllowFlight(), "GamemodeCommand#onWorldChanged 2");
			PlayerUtils.setFlying(player, flightMode.isFlying(), "GamemodeCommand#onWorldChanged 2");
		});

//		var player = event.getPlayer();
//		var newWorldGroup = WorldGroup.of(event.getToWorld());
//
//		Consumer<String> debug = message -> {};
//
//		if (Minigamer.of(player).isPlaying())
//			return;
//
//		final Consumer<Boolean> flying = state -> {
//			debug.accept("GamemodeCommand#onWorldChanged flying " + state);
//			PlayerUtils.setAllowFlight(player, state, "GamemodeCommand#onWorldChanged 1");
//			PlayerUtils.setFlying(player, state, "GamemodeCommand#onWorldChanged 1");
//		};
//
//		if (player.getGameMode().equals(GameMode.SPECTATOR)) {
//			player.setGameMode(GameMode.SPECTATOR);
//			debug.accept("GamemodeCommand#onWorldChanged spectator");
//			player.setFallDistance(0);
//			flying.accept(true);
//			return;
//		}
//
//		if (!Rank.of(player).isStaff()) {
//			debug.accept("GamemodeCommand#onWorldChanged is not staff");
//			if (WorldGroup.of(event.getPlayer()).isSurvivalMode()) {
//				debug.accept("GamemodeCommand#onWorldChanged survival world");
//				setGamemode(player, GameMode.SURVIVAL);
//			}
//
//			debug.accept("GamemodeCommand#onWorldChanged reset speed");
//			SpeedCommand.resetSpeed(player);
//		}
//
//		ModeUser mode = new ModeUserService().get(player);
//		GameMode gameMode = mode.getGamemode(newWorldGroup);
//		FlightMode flightMode = mode.getFlightMode(newWorldGroup);
//		debug.accept("GamemodeCommand#onWorldChanged setting gamemode " + gameMode);
//
//		setGamemode(player, gameMode);
//
//		if (Vanish.isVanished(player)) {
//			debug.accept("GamemodeCommand#onWorldChanged is vanished");
//			flightMode.setAllowFlight(true);
//			flightMode.setFlying(true);
//		}
//
//		debug.accept("GamemodeCommand#onWorldChanged setting flightmode " + flightMode);
//		PlayerUtils.setAllowFlight(player, flightMode.isAllowFlight(), "GamemodeCommand#onWorldChanged 2");
//		PlayerUtils.setFlying(player, flightMode.isFlying(), "GamemodeCommand#onWorldChanged 2");
	}
}



package me.pugabyte.bncore.features.minigames.commands;

import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.managers.MatchManager;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.mechanics.common.CheckpointMechanic;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.Team;
import me.pugabyte.bncore.features.minigames.models.matchdata.CheckpointMatchData;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.framework.exceptions.postconfigured.PlayerNotOnlineException;
import me.pugabyte.bncore.framework.exceptions.preconfigured.MustBeIngameException;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.Utils.RelativeLocation;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.utils.StringUtils.stripColor;

@Aliases({"mgm", "mg"})
@Permission("minigames")
public class MinigamesCommand extends CustomCommand {
	private Minigamer minigamer;

	public MinigamesCommand(CommandEvent event) {
		super(event);
		PREFIX = Minigames.PREFIX;
		if (sender() instanceof Player)
			minigamer = PlayerManager.get(player());
	}

	@Path
	@Permission("use")
	void warp() {
		runCommand("warp minigames");
	}

	@Path("help")
	@Permission("use")
	void help() {
		send(PREFIX + "Help menu");
	}

	@Path("list [filter]")
	@Permission("use")
	void list(String filter) {
		send(PREFIX + ArenaManager.getAll(filter).stream()
				.map(arena -> (MatchManager.find(arena) != null ? "&e" : "&3") + arena.getName())
				.collect(Collectors.joining("&3, ")));
	}

	@Path("join <arena>")
	@Permission("use")
	void join(Arena arena) {
		minigamer.join(arena);
	}

	@Path("(quit|leave)")
	@Permission("use")
	void quit() {
		minigamer.quit();
	}

	@Path("start [arena]")
	@Permission("manage")
	void start(@Arg("current") Arena arena) {
		getRunningMatch(arena).start();
	}

	@Path("end [arena]")
	@Permission("manage")
	void end(@Arg("current") Arena arena) {
		getRunningMatch(arena).end();
	}

	@Permission("manage")
	@Path("signs join <arena>")
	void joinSign(Arena arena) {
		Sign sign = getTargetSign(player());
		sign.setLine(0, StringUtils.colorize("&0&l< &1Minigames &0&l>"));
		sign.setLine(1, StringUtils.colorize("&aJoin"));
		String arenaName = arena.getName();
		if (arenaName.length() > 15) {
			sign.setLine(2, arenaName.substring(0, 15));
			sign.setLine(3, arenaName.substring(15));
		} else {
			sign.setLine(2, arena.getName());
			sign.setLine(3, "");
		}

		sign.update();
	}

	@Permission("manage")
	@Path("signs quit")
	void quitSign() {
		Sign sign = getTargetSign(player());
		sign.setLine(0, StringUtils.colorize("&0&l< &1Minigames &0&l>"));
		sign.setLine(1, StringUtils.colorize("&aQuit"));
		sign.setLine(2, "");
		sign.setLine(3, "");
		sign.update();
	}

	@Permission("manage")
	@Path("signs lobby")
	void lobbySign() {
		Sign sign = getTargetSign(player());
		sign.setLine(0, StringUtils.colorize("&0&l< &1Minigames &0&l>"));
		sign.setLine(1, StringUtils.colorize("&aLobby"));
		sign.setLine(2, "");
		sign.setLine(3, "");
		sign.update();
	}

	@Path("setTime <seconds>")
	@Permission("manage")
	void setTime(int seconds) {
		if (minigamer.getMatch() == null)
			error("You are not in a match");
		minigamer.getMatch().getTimer().setTime(seconds);
		minigamer.getMatch().getTimer().broadcastTimeLeft();
	}

	@Path("create <name>")
	@Permission("manage")
	void create(String name) {
		if (ArenaManager.exists(name))
			send(PREFIX + "Editing arena &e" + name + "&3");
		else {
			Arena arena = new Arena(name);
			arena.write();
			send(PREFIX + "Creating arena &e" + name + "&3");
		}

		Minigames.getMenus().openArenaMenu(player(), ArenaManager.get(name));
	}

	@Path("copy <from> <to>")
	@Permission("manage")
	void copy(Arena arena, String name) {
		if (ArenaManager.exists(name))
			error(PREFIX + "&e" + name + " already exists");

		Arena copy = ArenaManager.convert(arena, arena.getClass());
		copy.setId(ArenaManager.getNextId());
		copy.setName(name);
		copy.setDisplayName(name);
		copy.write();
		send(PREFIX + "Creating arena &e" + name + "&3");
		send(PREFIX + "&cRecommended: &3Edit .yml file to remove locations");
		Minigames.getMenus().openArenaMenu(player(), ArenaManager.get(name));
	}

	@Path("edit <arena>")
	@Permission("manage")
	void edit(Arena arena) {
		Minigames.getMenus().openArenaMenu(player(), arena);
	}

	@Path("warp <arena>")
	@Permission("manage")
	void teleport(Arena arena) {
		arena.teleport(minigamer);
	}

	@Path("(tp|teleport) <player> [player]")
	@Permission("manage")
	void teleport(Minigamer minigamer1, Minigamer minigamer2) {
		if (minigamer2 == null)
			minigamer.teleport(minigamer1.getPlayer().getLocation());
		else
			minigamer1.teleport(minigamer2.getPlayer().getLocation());
	}

	@Path("tppos <player> <x> <y> <z> [yaw] [pitch]")
	@Permission("manage")
	void teleport(Minigamer minigamer, String x, String y, String z, String yaw, String pitch) {
		Location location = minigamer.getPlayer().getLocation();
		RelativeLocation.modify(location).x(x).y(y).z(z).yaw(yaw).pitch(pitch).update();
		minigamer.teleport(location);
	}

	@Path("(delete|remove) <arena>")
	@Permission("manage")
	void remove(Arena arena) {
		Minigames.getMenus().openDeleteMenu(player(), arena);
	}

	@Path("(reload|read) [arena]")
	@Permission("manage")
	void reload(@Arg(tabCompleter = Arena.class) String arena) {
		long startTime = System.currentTimeMillis();

		if (arena == null)
			ArenaManager.read();
		else
			ArenaManager.read(arena);

		send(PREFIX + "Reload time took " + (System.currentTimeMillis() - startTime) + "ms");
	}

	@Path("(save|write) [arena]")
	@Permission("manage")
	void save(Arena arena) {
		Tasks.async(() -> {
			long startTime = System.currentTimeMillis();

			if (arena == null)
				ArenaManager.write();
			else
				ArenaManager.write(arena);

			send(PREFIX + "Save time took " + (System.currentTimeMillis() - startTime) + "ms");
		});
	}

	@Path("autoreset [boolean]")
	@Permission("use")
	void autoreset(Boolean autoreset) {
		Match match = minigamer.getMatch();
		if (!minigamer.isPlaying())
			error("You must be playing a checkpoint game to use that command");

		if (!(match.getArena().getMechanic() instanceof CheckpointMechanic))
			error("You are not in a checkpoint game");

		CheckpointMatchData matchData = match.getMatchData();
		matchData.autoreset(minigamer, autoreset);
		if (matchData.isAutoresetting(minigamer))
			send(PREFIX + "Enabled &eAuto Reset");
		else
			send(PREFIX + "Disabled &eAuto Reset");
	}

	@Path("addSpawnpoint <arena> [team]")
	void addSpawnpoint(Arena arena, @Arg(contextArg = 1) Team team) {
		List<Team> teams = arena.getTeams();

		if (team == null) {
			if (teams.size() != 1)
				error("There is more than one team in that arena, you must specify which one");

			teams.get(0).getSpawnpoints().add(player().getLocation());
			arena.write();
			send(PREFIX + "Spawnpoint added");
			return;
		}

		team.getSpawnpoints().add(player().getLocation());
		arena.write();
		send(PREFIX + "Spawnpoint added");
	}

	private static String inviteCommand;

	@Path("invite")
	void invite() {
		boolean isMinigameNight = false;
		LocalDateTime date = LocalDateTime.now();
		DayOfWeek dow = date.getDayOfWeek();

		if (dow.equals(DayOfWeek.SATURDAY)) {
			int hour = date.getHour();
			if (hour > 15 && hour < 18) {
				isMinigameNight = true;
			}
		}

		boolean canUse = false;
		if (!isMinigameNight)
			canUse = true;
		if (player().hasPermission("mginvite.use"))
			canUse = true;

		if (!canUse)
			error("You do not have permission to use this command!");

		WorldGuardUtils WGUtils = new WorldGuardUtils(player().getWorld());
		if (!WGUtils.isInRegion(player().getLocation(), "minigamelobby"))
			error("You must be in the Minigame Lobby to use this command");

		Collection<Player> players = WGUtils.getPlayersInRegion("minigamelobby");
		int count = players.size() - 1;
		if (count == 0)
			error("There is no one to invite!");

		String message;
		if (WGUtils.isInRegion(player().getLocation(), "screenshot")) {
			inviteCommand = "warp screenshot";
			message = "take a screenshot";
		} else {
			Sign sign = getTargetSign(player());
			String line2 = stripColor(sign.getLine(1)).toLowerCase();
			if (line2.contains("screenshot"))
				error("Stand in the screenshot area then run the command (sign not needed)");
			if (!line2.contains("join"))
				error("Cannot parse sign. If you believe this is an error, make a GitHub ticket with information and screenshots.");

			String prefix = "";
			String line1 = stripColor(sign.getLine(0)).toLowerCase();
			if (line1.contains("[minigame]"))
				prefix = "mgm";
			else if (line1.contains("< minigames >"))
				prefix = "newmgm";
			else
				error("Cannot parse sign. If you believe this is an error, make a GitHub ticket with information and screenshots.");

			String line3 = stripColor(sign.getLine(2)) + stripColor(sign.getLine(3));
			inviteCommand = prefix + " join " + line3;
			message = line3;
		}

		String sender = player().getName();
		send("&3Invite sent to &e" + count + " &3players for &e" + message);
		for (Player player : players) {
			if (player.equals(player()))
				continue;

			send(player, json("")
					.newline()
					.next(" &e" + sender + " &3has invited you to &e" + message).group()
					.newline()
					.next("&e Click here to &a&laccept")
					.command("/mgaccept")
					.hover("&eClick &3to accept"));
		}
	}

	@Path("accept")
	void acceptInvite() {
		if (inviteCommand == null)
			error("There is no pending game invite");
		runCommand(inviteCommand);
	}

	private Sign getTargetSign(Player player) {
		Block targetBlock = player.getTargetBlockExact(10);
		Material material = targetBlock.getType();
		if (Utils.isNullOrAir(material) || !Utils.isSign(material))
			error("Look at a sign!");
		return (Sign) targetBlock.getState();
	}

	private Match getRunningMatch(Arena arena) {
		if (arena == null)
			if (arg(2) == null)
				error("You must supply an arena name");
			else
				error("Arena not found");

		Match match = MatchManager.find(arena);

		if (match == null)
			error("There is no match running for that arena");

		return match;
	}

	@ConverterFor(Arena.class)
	Arena convertToArena(String value) {
		if ("current".equalsIgnoreCase(value))
			if (minigamer != null)
				if (minigamer.getMatch() != null)
					return minigamer.getMatch().getArena();
				else
					throw new InvalidInputException("You are not currently in a match");
			else
				throw new MustBeIngameException();
		else
			return ArenaManager.find(value);
	}

	@TabCompleterFor(Arena.class)
	List<String> arenaTabComplete(String filter) {
		return ArenaManager.getNames(filter);
	}

	@ConverterFor(Minigamer.class)
	Minigamer convertToMinigamer(String value) {
		if ("self".equalsIgnoreCase(value))
			return minigamer;
		OfflinePlayer player = Utils.getPlayer(value);
		if (!player.isOnline())
			throw new PlayerNotOnlineException(player);
		return PlayerManager.get(player.getPlayer());
	}

	@TabCompleterFor(Minigamer.class)
	List<String> tabCompleteMinigamer(String filter) {
		return tabCompletePlayer(filter);
	}

	@ConverterFor(Team.class)
	Team convertToTeam(String value, Arena context) {
		if ("current".equalsIgnoreCase(value))
			return minigamer.getTeam();

		return context.getTeams().stream()
				.filter(team -> team.getName().startsWith(value))
				.findFirst()
				.orElseThrow(() -> new InvalidInputException("Team not found"));
	}

	@TabCompleterFor(Team.class)
	List<String> tabCompleteTeam(String filter, Arena context) {
		return context.getTeams().stream()
				.map(Team::getName)
				.filter(name -> name.startsWith(filter))
				.collect(Collectors.toList());
	}
}

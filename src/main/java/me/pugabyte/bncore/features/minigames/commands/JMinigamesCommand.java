package me.pugabyte.bncore.features.minigames.commands;

import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.managers.MatchManager;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.mechanics.common.CheckpointMechanic;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
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
import me.pugabyte.bncore.framework.exceptions.preconfigured.MustBeIngameException;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

@Aliases({"jmgm", "newmgm", "newminigames"})
@Permission("minigames")
public class JMinigamesCommand extends CustomCommand {
	Minigamer minigamer;

	public JMinigamesCommand(CommandEvent event) {
		super(event);
		if (sender() instanceof Player)
			minigamer = PlayerManager.get(player());
	}

	@Path
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

	@Path("(teleport|tp) <arena>")
	@Permission("manage")
	void teleport(Arena arena) {
		arena.teleport(minigamer);
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
	void autoreset(@Arg("true") boolean autoreset) {
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
			send(PREFIX + "Disabled Auto Reset");
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
		return PlayerManager.get(Utils.getPlayer(value).getPlayer());
	}

	@TabCompleterFor(Minigamer.class)
	List<String> tabCompleteMinigamer(String value) {
		return tabCompletePlayer(value);
	}
}

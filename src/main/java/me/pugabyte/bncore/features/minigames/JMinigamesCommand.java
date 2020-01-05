package me.pugabyte.bncore.features.minigames;

import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.managers.MatchManager;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.preconfigured.MustBeIngameException;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.entity.Player;

import java.util.List;

@Aliases({"newmgm", "newminigames"})
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
		reply(PREFIX + "Help menu");
	}

	@Path("list {string}")
	@Permission("use")
	void list(@Arg String filter) {
		reply(PREFIX + String.join(", ", ArenaManager.getNames(filter)));
	}

	@Path("join {arena}")
	@Permission("use")
	void join(@Arg Arena arena) {
		minigamer.join(arena);
	}

	@Path("quit")
	@Permission("use")
	void quit() {
		minigamer.quit();
	}

	@Path("create {string}")
	@Permission("manage")
	void create(@Arg String string){
		Arena arena;
		if(!getNames().contains(string)){
			Arena newArena = Arena.builder()
					.id(getNextID())
					.name(arg(2).toLowerCase())
					.displayName(arg(2))
					.mechanicType(MechanicType.CAPTURE_THE_FLAG)
					.seconds(20)
					.minPlayers(2)
					.maxPlayers(16)
					.winningScore(1)
					.minWinningScore(0)
					.maxWinningScore(0)
					.canJoinLate(false)
					.respawnLocation(player().getLocation())
					.spectatePosition(player().getLocation())
					.hasScoreboard(true)
					.lobby(Lobby.builder()
							.location(player().getLocation())
							.waitTime(5)
							.build())
					.teams(Arrays.asList(Team.builder()
							.name("Players")
							.color(ChatColor.WHITE)
							.objective("Beat the other players in the game.")
							.loadout(Loadout.builder()
									.inventoryContents(new ItemStack[]{})
									.potionEffects(new ArrayList<PotionEffect>())
									.build())
							.spawnpoints(new ArrayList<Location>())
							.build()))
					.build();
			reply(PREFIX + "Creating arena " + arg(2) + "&3.");
			reply(PREFIX + newArena.toString());
			write(newArena);
			arena = newArena;
		}
		else {
			reply(PREFIX + "Editing arena " + arg(2) + "&3.");
			arena = ArenaManager.get(arg(2));
		}
		if(arena == null)
			read();
		else
			read(arena.getName());
		new MinigamesMenus().openArenaMenu(player(), arena);
	}

	@Path("manage {arena}")
	@Permission("manage")
	void manage(@Arg String arena){
		new MinigamesMenus().openArenaMenu(player(), ArenaManager.get(arena));
	}

	@Path("start {arena}")
	@Permission("manage")
	void start(@Arg("current") Arena arena) {
		getRunningMatch(arena).start();
	}

	@Path("end {arena}")
	@Permission("manage")
	void end(@Arg("current") Arena arena) {
		getRunningMatch(arena).end();
	}

	@Path("(reload|read) {string}")
	@Permission("manage")
	void reload(@Arg(tabCompleter = Arena.class) String arena) {
		long startTime = System.currentTimeMillis();

		if (arena == null)
			ArenaManager.read();
		else
			ArenaManager.read(arena);

		reply(PREFIX + "Reload time took " + (System.currentTimeMillis() - startTime) + "ms");
	}

	@Path("(save|write) {arena}")
	@Permission("manage")
	void save(@Arg Arena arena) {
		long startTime = System.currentTimeMillis();

		if (arena == null)
			ArenaManager.write();
		else
			ArenaManager.write(arena);

		reply(PREFIX + "Save time took " + (System.currentTimeMillis() - startTime) + "ms");
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
	Object convertToArena(String value) {
		if ("current".equalsIgnoreCase(value))
			if (minigamer != null)
				if (minigamer.getMatch() != null)
					return minigamer.getMatch().getArena();
				else
					error("You are not currently in a match");
			else
				throw new MustBeIngameException();
		else
			return ArenaManager.find(value);
		return null;
	}

	@TabCompleterFor(Arena.class)
	List<String> arenaTabComplete(String filter) {
		return ArenaManager.getNames(filter);
	}

	@ConverterFor(Minigamer.class)
	Object convertToMinigamer(String value) {
		if ("self".equalsIgnoreCase(value))
			return minigamer;
		return PlayerManager.get(Utils.getPlayer(value).getPlayer());
	}

	@TabCompleterFor(Minigamer.class)
	List<String> tabCompleteMinigamer(String value) {
		return tabCompletePlayer(value);
	}
}

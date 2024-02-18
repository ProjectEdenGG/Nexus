package gg.projecteden.nexus.features.minigames.commands;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.lobby.MinigameInviter;
import gg.projecteden.nexus.features.minigames.lobby.exchange.MGMExchange;
import gg.projecteden.nexus.features.minigames.lobby.exchange.MGMExchangeMenu;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.managers.MatchManager;
import gg.projecteden.nexus.features.minigames.mechanics.Mastermind;
import gg.projecteden.nexus.features.minigames.mechanics.common.CheckpointMechanic;
import gg.projecteden.nexus.features.minigames.menus.ArenaMenu;
import gg.projecteden.nexus.features.minigames.menus.LeaderboardMenu;
import gg.projecteden.nexus.features.minigames.menus.PerkMenu;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.features.minigames.models.arenas.CheckpointArena;
import gg.projecteden.nexus.features.minigames.models.matchdata.CheckpointMatchData;
import gg.projecteden.nexus.features.minigames.models.matchdata.MastermindMatchData;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicGroup;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.features.minigames.models.modifiers.MinigameModifiers;
import gg.projecteden.nexus.features.minigames.models.perks.HideParticle;
import gg.projecteden.nexus.features.minigames.models.perks.PerkType;
import gg.projecteden.nexus.features.minigames.models.scoreboards.MinigameScoreboard;
import gg.projecteden.nexus.features.minigames.utils.MinigameNight;
import gg.projecteden.nexus.features.warps.commands._WarpSubCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Confirm;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromHelp;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.exceptions.postconfigured.PlayerNotOnlineException;
import gg.projecteden.nexus.framework.exceptions.preconfigured.MustBeIngameException;
import gg.projecteden.nexus.models.customboundingbox.CustomBoundingBoxEntity;
import gg.projecteden.nexus.models.customboundingbox.CustomBoundingBoxEntityService;
import gg.projecteden.nexus.models.minigamersetting.MinigamerSetting;
import gg.projecteden.nexus.models.minigamersetting.MinigamerSettingService;
import gg.projecteden.nexus.models.minigamessetting.MinigamesConfig;
import gg.projecteden.nexus.models.minigamessetting.MinigamesConfigService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.perkowner.PerkOwner;
import gg.projecteden.nexus.models.perkowner.PerkOwnerService;
import gg.projecteden.nexus.models.punishments.Punishment;
import gg.projecteden.nexus.models.punishments.PunishmentType;
import gg.projecteden.nexus.models.punishments.Punishments;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.LocationUtils.RelativeLocation;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;
import static gg.projecteden.nexus.utils.StringUtils.stripColor;

@Aliases({"mgm", "mg"})
@Redirect(from = "/mgn", to = "/mgm night")
public class MinigamesCommand extends _WarpSubCommand {
	public static final String MINIGAME_SIGN_HEADER = "&0&l< &1Minigames &0&l>";
	public static final String OLD_MGM_SIGN_HEADER = "&1[Minigame]";

	private Minigamer minigamer;
	private final MinigamesConfigService configService = new MinigamesConfigService();

	public MinigamesCommand(CommandEvent event) {
		super(event);
		PREFIX = Minigames.PREFIX;
		if (sender() instanceof Player)
			minigamer = Minigamer.of(player());
	}

	@Override
	public WarpType getWarpType() {
		return WarpType.MINIGAMES;
	}

	@Path
	@Description("Teleport to the minigame lobby")
	void warp() {
		runCommand("warp minigames");
	}

	@Path("night")
	@Description("View when the next Minigame Night occurs")
	void night() {
		MinigameNight mgn = isPlayer() ? new MinigameNight(player()) : new MinigameNight();

		line();
		if (mgn.isNow())
			send("&3Minigame night is happening right now! Join with &e/gl");
		else
			send("&3The next &eMinigame Night &3will be hosted on &e" + mgn.getDateFormatted() + "&3 at &e"
				+ mgn.getTimeFormatted() + "&3. That is in &e" + mgn.getUntil());
	}

	@Path("list [filter] [--mechanic]")
	@Permission(Group.MODERATOR)
	@Description("List minigame maps, optionally filtered by name and/or mechanic")
	void list(String filter, @Switch MechanicType mechanic) {
		JsonBuilder json = json(PREFIX);
		final List<Arena> arenas = ArenaManager.getAll(filter).stream()
			.filter(arena -> mechanic == null || arena.getMechanicType() == mechanic)
			.sorted(Comparator.comparing(Arena::getName).thenComparing(arena -> MatchManager.find(arena) != null))
			.toList();

		final Iterator<Arena> iterator = arenas.iterator();
		while (iterator.hasNext()) {
			Arena arena = iterator.next();

			Match match = MatchManager.find(arena);
			if (match == null)
				json.next("&3" + arena.getName());
			else
				json.next("&e" + arena.getName()).hover(match.getMinigamers().stream().map(Minigamer::getNickname).collect(Collectors.joining(", ")));

			if (iterator.hasNext())
				json.group().next("&3, ").group();
		}

		send(json);
	}

	@Path("mechanics [filter] [--group]")
	@Description("List mechanics, optionally filtered by name and/or group")
	void list(String filter, @Switch MechanicGroup group) {
		JsonBuilder json = json(PREFIX);
		final List<MechanicType> mechanics = Arrays.stream(MechanicType.values())
			.filter(mechanic -> group == null || mechanic.getGroup() == group)
			.filter(mechanic -> mechanic.name().toLowerCase().startsWith(filter.toLowerCase()))
			.toList();

		final Iterator<MechanicType> iterator = mechanics.iterator();
		while (iterator.hasNext()) {
			MechanicType mechanicType = iterator.next();

			json.next("&3" + camelCase(mechanicType));

			if (iterator.hasNext())
				json.group().next("&3, ").group();
		}

		send(json);
	}

	@Path("join <arena>")
	@Description("Join a minigame")
	void join(Arena arena) {
		minigamer.join(arena);
	}

	@Path("join random [mechanic]")
	@Description("Join a random minigame")
	Arena join(MechanicType mechanic) {
		final Optional<Match> mostPlayers = ArenaManager.getAllEnabled(mechanic).stream()
			.map(MatchManager::get)
			.filter(match -> match.getMinigamers().size() > 0)
			.filter(match -> !match.isStarted() || match.getArena().canJoinLate())
			.max(Comparator.comparingInt(match -> match.getMinigamers().size()));

		final Arena arena;
		if (mostPlayers.isPresent())
			arena = mostPlayers.get().getArena();
		else
			arena = RandomUtils.randomElement(ArenaManager.getAllEnabled(mechanic));

		minigamer.join(arena);
		return arena;
	}

	@Path("allJoin <arena>")
	@Permission(Group.ADMIN)
	@Environments({Env.DEV, Env.TEST})
	void allJoin(Arena arena) {
		for (Player player : OnlinePlayers.getAll())
			Minigamer.of(player).join(arena);
	}

	@Path("(quit|leave)")
	@Description("Quit your current minigame")
	void quit() {
		minigamer.quit();
	}

	@Path("warn <player> [reason]")
	@Permission(Group.MODERATOR)
	@Description("Warn a player for not obeying minigame rules and strike them with lightning")
	void warn(Player player, String reason) {
		if (!Minigames.isMinigameWorld(player.getWorld()))
			error("Target player is not in minigames");

		player.getWorld().strikeLightningEffect(player.getLocation());
		Punishments.of(player).add(Punishment.ofType(PunishmentType.WARN).punisher(uuid())
				.input("Please obey the rules of our minigames" + (isNullOrEmpty(reason) ? "" : ": " + reason)));
	}

	@Path("settings bowInOffHand [boolean]")
	@Description("Toggle whether bows spawn in your offhand")
	void settings_bowInOffHand(Boolean offHand) {
		MinigamerSettingService service = new MinigamerSettingService();
		MinigamerSetting settings = service.get(player());
		if (offHand == null)
			offHand = !settings.isBowInOffHand();

		settings.setBowInOffHand(offHand);
		send(PREFIX + "Bows will now spawn in your " + (offHand ? "offhand" : "hotbar"));
		service.save(settings);
	}

	@Path("start [arena]")
	@Permission(Group.MODERATOR)
	@Description("Force start a match")
	void start(@Arg("current") Arena arena) {
		getRunningMatch(arena).start();
	}

	@Confirm
	@Path("end [arena]")
	@Permission(Group.MODERATOR)
	@Description("Force end a match")
	void end(@Arg("current") Arena arena) {
		getRunningMatch(arena).end();
	}

	@Path("debug [state]")
	@Permission(Group.ADMIN)
	@Description("Toggle debug")
	void debug(Boolean state) {
		if (state == null)
			state = !Minigames.isDebug();

		Minigames.setDebug(state);
		send(PREFIX + "Debug " + (state ? "&aenabled" : "&cdisabled"));
	}

	@Path("debug arena [arena]")
	@Permission(Group.ADMIN)
	@Description("Print an arena's properties")
	void debug(@Arg("current") Arena arena) {
		send(arena.toString());
	}

	void updateSign(String... lines) {
		String[] trueLines = Arrays.copyOf(lines, 4);
		Sign sign = getTargetSignRequired();
		for (int l = 0; l < Math.min(trueLines.length, 4); l++) {
			sign.setLine(l, StringUtils.colorize(trueLines[l]));
		}
		sign.update();
	}

	@Permission(Group.MODERATOR)
	@Path("signs join <arena>")
	@Description("Create a join sign")
	void joinSign(Arena arena) {
		String[] lines = new String[4];
		lines[0] = MINIGAME_SIGN_HEADER;
		lines[1] = "&aJoin";
		String arenaName = arena.getName();
		if (arenaName.length() > 15) {
			lines[2] = arenaName.substring(0, 15);
			lines[3] = arenaName.substring(15);
		} else {
			lines[2] = arena.getName();
		}
		updateSign(lines);
	}

	@Permission(Group.MODERATOR)
	@Path("signs join random <mechanic>")
	@Description("Create a join random sign")
	void signs_join_random(MechanicType mechanic) {
		updateSign(MINIGAME_SIGN_HEADER, "&aJoin Random", camelCase(mechanic));
	}

	@Permission(Group.MODERATOR)
	@Path("signs quit")
	@Description("Create a quit sign")
	void quitSign() {
		updateSign(MINIGAME_SIGN_HEADER, "&aQuit");
	}

	@Permission(Group.MODERATOR)
	@Path("signs lobby")
	@Description("Create a lobby sign")
	void lobbySign() {
		updateSign(MINIGAME_SIGN_HEADER, "&aLobby");
	}

	@Permission(Group.MODERATOR)
	@Path("signs flag <team>")
	@Description("Create a flag sign")
	void flagSign(String team) {
		updateSign(OLD_MGM_SIGN_HEADER, "&aFlag", team);
	}

	@Permission(Group.MODERATOR)
	@Path("signs flag capture <team>")
	@Description("Create a flag sign")
	void flagCaptureSign(String team) {
		updateSign(OLD_MGM_SIGN_HEADER, "&aFlag", "&aCapture", team);
	}

	@Path("setTime <seconds>")
	@Permission(Group.MODERATOR)
	@Description("Set the time left in a match")
	void setTime(int seconds) {
		if (minigamer.getMatch() == null)
			error("You are not in a match");
		minigamer.getMatch().getTimer().setTime(seconds);
		minigamer.getMatch().getTimer().broadcastTimeLeft();
	}

	@Path("create <name>")
	@Permission(Group.MODERATOR)
	@Description("Create a new arena")
	void create(String name) {
		if (ArenaManager.exists(name))
			send(PREFIX + "Editing arena &e" + name + "&3");
		else {
			Arena arena = new Arena(name);
			arena.write();
			send(PREFIX + "Creating arena &e" + name + "&3");
		}

		new ArenaMenu(ArenaManager.get(name)).open(player());
	}

	@Path("copy <from> <to>")
	@Permission(Group.MODERATOR)
	@Description("Copy some settings from one arena to another")
	void copy(Arena arena, String name) {
		if (ArenaManager.exists(name))
			error("&e" + name + " &calready exists");

		Arena copy = ArenaManager.convert(arena, arena.getClass());
		copy.setId(ArenaManager.getNextId());
		copy.setName(name);
		copy.setDisplayName(name);
		copy.write();
		copy.setRespawnLocation(null);
		copy.setSpectateLocation(null);
		copy.getTeams().forEach(team -> team.setSpawnpoints(new ArrayList<>()));
		copy.getBlockList().clear();

		send(PREFIX + "Creating arena &e" + name + "&3");
		new ArenaMenu(ArenaManager.get(name)).open(player());
	}

	@Path("edit <arena>")
	@Permission(Group.MODERATOR)
	@Description("Open the arena config menu")
	void edit(Arena arena) {
		new ArenaMenu(arena).open(player());
	}

	@Path("warp arena <arena>")
	@Permission(Group.MODERATOR)
	@Description("Warp to an arena")
	void warp_arena(Arena arena) {
		arena.teleport(minigamer);
	}

	@Path("(tp|teleport) <player> [player]")
	@Permission(Group.MODERATOR)
	@Description("Teleport a minigamer")
	void teleport(Minigamer minigamer1, Minigamer minigamer2) {
		if (minigamer2 == null)
			minigamer.teleportAsync(minigamer1.getPlayer().getLocation());
		else
			minigamer1.teleportAsync(minigamer2.getPlayer().getLocation());
	}

	@Path("tppos <player> <x> <y> <z> [yaw] [pitch]")
	@Permission(Group.MODERATOR)
	@Description("Teleport a minigamer to coordinates")
	void teleport(Minigamer minigamer, String x, String y, String z, String yaw, String pitch) {
		Location location = minigamer.getOnlinePlayer().getLocation();
		RelativeLocation.modify(location).x(x).y(y).z(z).yaw(yaw).pitch(pitch).update();
		minigamer.teleportAsync(location);
	}

	@Confirm
	@Path("(delete|remove) <arena>")
	@Permission(Group.MODERATOR)
	@Description("Delete an arena")
	void remove(Arena arena) {
		arena.delete();
		send(PREFIX + "Arena &e" + arena.getName() + " &3deleted");
	}

	@Path("(reload|read) [arena]")
	@Permission(Group.MODERATOR)
	@Description("Reload an arena from disk")
	void reload(@Arg(tabCompleter = Arena.class) String arena) {
		long startTime = System.currentTimeMillis();

		if (arena == null)
			ArenaManager.read();
		else
			ArenaManager.read(arena);

		send(PREFIX + "Reload time took " + (System.currentTimeMillis() - startTime) + "ms");
	}

	@Async
	@Path("(save|write) [arena]")
	@Permission(Group.MODERATOR)
	@Description("Write an arena to disk")
	void save(Arena arena) {
		long startTime = System.currentTimeMillis();

		if (arena == null)
			ArenaManager.write();
		else
			ArenaManager.write(arena);

		send(PREFIX + "Save time took " + (System.currentTimeMillis() - startTime) + "ms");
	}

	@Path("autoreset [boolean]")
	@Description("Enable auto-reset in checkpoint minigames")
	void autoreset(Boolean autoreset) {
		Match match = minigamer.getMatch();
		if (!minigamer.isPlaying())
			error("You must be playing a checkpoint game to use that command");

		if (!(match.getMechanic() instanceof CheckpointMechanic))
			error("You are not in a checkpoint game");

		CheckpointMatchData matchData = match.getMatchData();
		matchData.autoreset(minigamer, autoreset);
		if (matchData.isAutoresetting(minigamer))
			send(PREFIX + "Enabled &eAuto Reset");
		else
			send(PREFIX + "Disabled &eAuto Reset");
	}

	@Path("addSpawnpoint [arena] [team]")
	@Permission(Group.MODERATOR)
	@Description("Add a team spawnpoint")
	void addSpawnpoint(@Arg("current") Arena arena, @Arg(context = 1) Team team) {
		if (team == null) {
			List<Team> teams = arena.getTeams();

			if (teams.size() != 1)
				error("There is more than one team in that arena, you must specify which one");

			team = teams.get(0);
		}

		team.getSpawnpoints().add(location());
		send(PREFIX + "Spawnpoint added added for team " + team.getColoredName() + " &3on &e" + arena.getDisplayName() + "&3. Total spawnpoints: &e" + team.getSpawnpoints().size());

		arena.write();
	}

	@Path("linkSpawnpoints [arena] [fromTeam] [toTeam]")
	@Permission(Group.MODERATOR)
	@Description("Link two teams' spawnpoint lists together so they are shared")
	void linkSpawnpoints(@Arg("current") Arena arena, @Arg(context = 1) Team fromTeam, @Arg(context = 1) Team toTeam) {
		toTeam.setSpawnpoints(fromTeam.getSpawnpoints());
		arena.write();
		send(PREFIX + "Spawnpoints linked");
	}

	@Path("setLobbyLocation [arena]")
	@Permission(Group.MODERATOR)
	@Description("Set the lobby location of an arena")
	void setLobbyLocation(@Arg("current") Arena arena) {
		arena.getLobby().setLocation(location());
		arena.write();
		send(PREFIX + "Set lobby location of &e" + arena.getName() + " &3to current location");
	}

	@Path("setSpectateLocation [arena]")
	@Permission(Group.MODERATOR)
	@Description("Set the spectate location of an arena")
	void setSpectateLocation(@Arg("current") Arena arena) {
		arena.setSpectateLocation(location());
		arena.write();
		send(PREFIX + "Set spectate location of &e" + arena.getName() + " &3to current location");
	}

	@Path("addSelectedBlocksToArena [arena]")
	@Permission(Group.MODERATOR)
	@Description("Add all materials within your selection to the arena block list")
	void addSelectedBlocksToArena(@Arg("current") Arena arena) {
		final var worldedit = new WorldEditUtils(player());
		final var blocks = worldedit.getBlocks(worldedit.getPlayerSelection(player()));

		if (blocks.isEmpty())
			error("No blocks found in selection");

		final Set<Material> materials = new HashSet<>() {{
			for (Block block : blocks)
				if (!isNullOrAir(block))
					add(block.getType());
		}};

		if (materials.isEmpty())
			error("No non-air materials found");

		materials.removeAll(arena.getBlockList());

		if (materials.isEmpty())
			error("No new materials to be added");

		arena.getBlockList().addAll(materials);
		arena.write();
		send(PREFIX + "Added " + materials.size() + " materials to &e" + arena.getName() + " block list");
	}

	@Path("createRegion <arena> <name>")
	@Permission(Group.MODERATOR)
	@Description("Create a region for an arena")
	void createRegion(Arena arena, String name) {
		var worldguard = new WorldGuardUtils(player());
		final String regionName = arena.getRegionBaseName() + "_" + name;
		try {
			worldguard.getRegion(regionName);
			runCommand("rg redefine " + regionName);
		} catch (Exception ignore) {
			runCommand("rg define " + regionName);
		}
	}

	@Path("schem save <arena> <name> [--createRegion]")
	@Permission(Group.MODERATOR)
	@Description("Create an arena schematic from your selection and optionally create a region")
	void schemSave(Arena arena, String name, @Switch boolean createRegion) {
		var worldedit = new WorldEditUtils(player());
		var worldguard = worldedit.worldguard();
		GameMode originalGameMode = player().getGameMode();
		Location originalLocation = location().clone();
		Location location = worldedit.toLocation(worldedit.getPlayerSelection(player()).getMinimumPoint());
		player().setGameMode(GameMode.SPECTATOR);
		player().teleportAsync(location);
		runCommand("mcmd /copy ;; wait 10 ;; /schem save " + (arena.getSchematicBaseName() + name) + " -f");
		if (createRegion) {
			final String regionName = arena.getRegionBaseName() + "_" + name;
			try {
				worldguard.getRegion(regionName);
				runCommand("rg redefine " + regionName);
			} catch (Exception ignore) {
				runCommand("rg define " + regionName);
			}
		}
		Tasks.wait(20, () -> {
			player().teleportAsync(originalLocation);
			player().setGameMode(originalGameMode);
		});

		send(PREFIX + "Saved schematic " + name);
	}

	@Path("schem paste <arena> <name>")
	@Permission(Group.MODERATOR)
	@Description("Paste an arena's schematic at your location")
	void schemPaste(Arena arena, String name) {
		String schematicName = arena.getSchematicName(name);
		new WorldEditUtils(world()).paster().file(schematicName).at(location()).pasteAsync();
		send(PREFIX + "Pasted schematic " + schematicName);
	}

	@Async
	@Path("topic update")
	@Permission(Group.ADMIN)
	@Description("Update the Discord #minigames channel topic")
	void topic_update() {
		Minigames.updateTopic();
	}

	@SuppressWarnings("deprecation")
	private MinigameInviter createInvite(Arena arena) {
		final Set<ProtectedRegion> screenshotRegions = new WorldGuardUtils(player()).getRegionsLikeAt(".*screenshot.*", location());
		if (!screenshotRegions.isEmpty()) {
			final String warp = screenshotRegions.iterator().next().getId().replace("lobby_", "").replace("_", "");
			return Minigames.inviter().create(player(), WarpType.MINIGAMES.get(warp).getLocation(), "take a screenshot");
		}

		if (arena == null) {
			final CustomBoundingBoxEntity entity = new CustomBoundingBoxEntityService().getTargetEntity(player());
			if (entity != null) {
				var mechanic = MechanicType.from(entity);
				var arenas = ArenaManager.getAllEnabled(mechanic);
				switch (arenas.size()) {
					case 0 -> error("No arenas found for " + camelCase(mechanic));
					case 1 -> arena = arenas.get(0);
					default -> error("Found multiple arenas for mechanic " + mechanic.get().getName() + ", should have opened menu?");
				}
			} else {
				Sign sign = getTargetSignRequired();
				String line2 = stripColor(sign.getLine(1)).toLowerCase();
				if (line2.contains("screenshot"))
					error("Stand in the screenshot area then run the command (sign not needed)");

				String line1 = stripColor(sign.getLine(0)).toLowerCase();
				if (!line1.contains("[minigame]") && !line1.contains("< minigames >"))
					error("Cannot parse sign. If you believe this is an error, make a bug report with information and screenshots.");

				switch (line2) {
					case "join" -> arena = ArenaManager.get(stripColor(sign.getLine(2)) + stripColor(sign.getLine(3)));
					case "join random" -> arena = join(MechanicType.valueOf(sign.getLine(2).toUpperCase()));
					default -> error("Cannot parse minigame sign. If you believe this is an error, make a bug report with information and screenshots.");
				}
			}
		}

		return Minigames.inviter().create(player(), arena);
	}

	@Path("invite [arena] [--mechanic]")
	@Description("Invite players to a match")
	void invite(Arena arena, @Switch MechanicType mechanic) {
		if (arena == null && mechanic != null)
			arena = RandomUtils.randomElement(ArenaManager.getAll(mechanic));

		createInvite(arena).inviteLobby();
	}

	@Permission(Group.MODERATOR)
	@Path("inviteAll [arena] [--mechanic]")
	@Description("Invite all players to a match")
	void inviteAll(Arena arena, @Switch MechanicType mechanic) {
		if (arena == null && mechanic != null)
			arena = RandomUtils.randomElement(ArenaManager.getAll(mechanic));

		createInvite(arena).inviteAll();
	}

	@Path("accept")
	@Description("Accept the last match invite")
	void acceptInvite() {
		if (world().equals(Minigames.getWorld()))
			Minigames.inviter().accept(player());
		else
			WarpType.NORMAL.get("minigames").teleportAsync(player()).thenRun(() ->
				Tasks.wait(2, this::acceptInvite));
	}

	static {
		Arrays.asList(MinigamePodiumPosition.values()).forEach(position ->
			Nexus.getInstance().addConfigDefault("minigames.podiums." + position.name().toLowerCase(), 0));
	}

	@Path("(podium|podiums) <position> <player> <title...>")
	@Permission(Group.MODERATOR)
	@Description("Update the weekly podiums")
	void podium(MinigamePodiumPosition position, Nerd nerd, String title) {
		CitizensUtils.updateHologram(position.getNPC(), 1, title);
		CitizensUtils.updateHologram(position.getNPC(), 0, "&l" + nerd.getNickname());
		CitizensUtils.updateSkin(position.getNPC(), nerd.getName());
		send(PREFIX + "Podium updated");
	}

	@Path("(podium|podiums) (getId|getIds) [position]")
	@Permission(Group.MODERATOR)
	@Description("Get the ids of the podium NPCs")
	void podium_getId(MinigamePodiumPosition position) {
		if (position == null) {
			send(PREFIX + "Podium IDs:");
			Arrays.asList(MinigamePodiumPosition.values()).forEach(_position ->
				send("&3" + StringUtils.camelCase(_position.name()) + ": &e" + _position.getId()));
		} else
			send(PREFIX + StringUtils.camelCase(position.name()) + ": &e" + position.getId());
	}

	@Path("(podium|podiums) setId <position> <id>")
	@Permission(Group.MODERATOR)
	@Description("Set the NPC id of a podium position")
	void podium_setId(MinigamePodiumPosition position, int id) {
		position.setId(id);
		send(PREFIX + StringUtils.camelCase(position.name()) + " podium ID updated to " + id);
	}

	@Path("(podium|podiums) tp <position>")
	@Permission(Group.MODERATOR)
	@Description("Teleport to a podium NPC")
	void tp(MinigamePodiumPosition position) {
		player().teleportAsync(position.getNPC().getEntity().getLocation(), TeleportCause.COMMAND);
	}

	@Path("(podium|podiums) (s|summon) <position>")
	@Permission(Group.MODERATOR)
	@Description("Teleport a podium NPC to you")
	void tphere(MinigamePodiumPosition position) {
		runMultiCommand("blockcenter", "npc sel " + position.getId(), "npc tphere");
	}

	public enum MinigamePodiumPosition {
		LEFT,
		RIGHT,
		MIDDLE;

		public static MinigamePodiumPosition get(String position) {
			if (position != null)
				switch (position.toLowerCase()) {
					case "l": case "left": return LEFT;
					case "r": case "right": return RIGHT;
					case "m": case "middle": return MIDDLE;
				}
			throw new InvalidInputException("Invalid podium position");
		}

		public void setId(int id) {
			Nexus.getInstance().getConfig().set("minigames.podiums." + name().toLowerCase(), id);
			Nexus.getInstance().saveConfig();
		}

		private int getId() {
			return Nexus.getInstance().getConfig().getInt("minigames.podiums." + name().toLowerCase());
		}

		public NPC getNPC() {
			return CitizensAPI.getNPCRegistry().getById(getId());
		}
	}

	@ConverterFor(MinigamePodiumPosition.class)
	MinigamePodiumPosition convertToPosition(String value) {
		return MinigamePodiumPosition.get(value);
	}

	@TabCompleterFor(MinigamePodiumPosition.class)
	public List<String> tabCompletePosition(String filter) {
		List<String> completions = Arrays.stream(MinigamePodiumPosition.values()).map(position -> position.name().toLowerCase()).collect(Collectors.toList());
		new ArrayList<>(completions).forEach(position -> completions.add(String.valueOf(position.charAt(0))));
		return completions;
	}

	@Path("collectibles")
	@Description("View the minigame collectibles menu")
	void collectibles() {
		if (player().getWorld() != Minigames.getWorld())
			error("You must be in the gameworld to use this command");
		new PerkMenu().open(player());
	}

	@Path("tokens [user]")
	@Description("View a player's minigame tokens")
	void getTokens(@Arg("self") Nerd nerd) {
		PerkOwnerService service = new PerkOwnerService();
		PerkOwner perkOwner = service.get(nerd);
		String username = nerd.getUuid().equals(player().getUniqueId()) ? "You have" : (perkOwner.getNickname() + " has");
		send(PREFIX + username + " " + perkOwner.getTokens() + plural(" token", perkOwner.getTokens()));
	}

	@Path("tokens set <amount> [user]")
	@Permission(Group.SENIOR_STAFF)
	@Description("Modify a player's minigame tokens")
	void setTokens(int amount, @Arg("self") Nerd nerd) {
		new PerkOwnerService().edit(nerd, perkOwner -> {
			perkOwner.setTokens(amount);
			String username = nerd.getUuid().equals(player().getUniqueId()) ? "Your" : (perkOwner.getNickname() + "'s");
			send(PREFIX + username + " tokens were set to " + amount);
		});
	}

	@Path("tokens add <amount> [user]")
	@Permission(Group.SENIOR_STAFF)
	@Description("Modify a player's minigame tokens")
	void addTokens(int amount, @Arg("self") Nerd nerd) {
		new PerkOwnerService().edit(nerd, perkOwner -> {
			perkOwner.setTokens(amount + perkOwner.getTokens());
			String username = nerd.getUuid().equals(player().getUniqueId()) ? "You now have" : (perkOwner.getNickname() + " now has");
			send(PREFIX + username + " " + perkOwner.getTokens() + plural(" token", perkOwner.getTokens()));
		});
	}

	@Path("tokens remove <amount> [user]")
	@Permission(Group.SENIOR_STAFF)
	@Description("Modify a player's minigame tokens")
	void removeTokens(int amount, @Arg("self") Nerd nerd) {
		addTokens(-1 * amount, nerd);
	}

	@HideFromWiki
	@Path("mastermind answer")
	@Permission(Group.ADMIN)
	void mastermind_answer() {
		if (!minigamer.isPlaying(Mastermind.class))
			error("You must be playing Mastermind to use this command");

		MastermindMatchData matchData = minigamer.getMatch().getMatchData();
		send(matchData.getAnswer().toString());
	}

	@HideFromWiki
	@HideFromHelp
	@TabCompleteIgnore
	@Path("mastermind reset")
	@Permission(Group.ADMIN)
	void mastermind_reset() {
		if (!minigamer.isPlaying(Mastermind.class))
			error("You must be playing Mastermind to use this command");

		MastermindMatchData matchData = minigamer.getMatch().getMatchData();
		matchData.reset(minigamer);
	}

	@Path("hideParticles <type>")
	@Description("Toggle hiding collectibles particles")
	void hideParticles(HideParticle type) {
		new PerkOwnerService().edit(player(), owner -> owner.setHideParticle(type));
		send(Minigames.PREFIX + "Now hiding " + type.toString().toLowerCase() + " particles");
	}

	@Path("modifier <modifier>")
	@Permission(Group.MODERATOR)
	@Description("Activate a minigame modifier")
	void modifier(MinigameModifiers modifier) {
		MinigamesConfig setting = configService.get0();
		setting.setModifier(modifier);
		configService.save(setting);
		send(PREFIX + "Minigame modifier set to &e" + modifier.get().getName());
	}

	@Path("modifier random")
	@Description("Activate a random minigame modifier")
	@Permission(Group.MODERATOR)
	void modifierRandom() {
		modifier(RandomUtils.randomElement(List.of(MinigameModifiers.values())));
	}

	@HideFromWiki
	@Path("scoreboard refresh")
	@Permission(Group.ADMIN)
	@Description("Refresh all scoreboards")
	void refreshNameColors(@Arg("current") Arena arena) {
		final Match match = MatchManager.find(arena);
		if (match == null)
			error("Match not started");

		MinigameScoreboard scoreboard = match.getScoreboard();
		if (scoreboard == null)
			error("No scoreboard found");

		scoreboard.update();
		send(PREFIX + "Refreshed scoreboard of " + match.getArena().getDisplayName());
	}

	@Path("leaderboard [arena]")
	@Description("View the leaderboard for timed minigames")
	void leaderboard(@Arg("current") CheckpointArena arena) {
		new LeaderboardMenu(arena).open(player());
	}

	private Match getRunningMatch(Arena arena) {
		Match match = MatchManager.find(arena);

		if (match == null)
			error("There is no match running for that arena");

		return match;
	}

	@HideFromWiki
	@HideFromHelp
	@Permission(Group.ADMIN)
	@Path("collectibles give <player> <perkType>")
	void givePerk(Player player, PerkType type) {
		PerkOwnerService service = new PerkOwnerService();
		PerkOwner perkOwner = service.get(player);
		perkOwner.getPurchasedPerks().put(type, false);
		service.save(perkOwner);
	}

	@Path("tokenExchange")
	@Description("Opens the Token Exchange Menu")
	void tokenExchange() {
		if (!Minigames.isInMinigameLobby(player()))
			error("You must be in the Game Lobby to use this command");
		MGMExchange.open(player());
	}

	@ConverterFor(Arena.class)
	Arena convertToArena(String value) {
		if ("current".equalsIgnoreCase(value))
			if (minigamer != null && minigamer.getMatch() != null) {
				return minigamer.getMatch().getArena();
			} else {
				Arena arena = ArenaManager.getFromLocation(location());
				if (arena != null)
					return arena;

				throw new InvalidInputException("You are not in an arena (region created?)");
			}

		return ArenaManager.find(value);
	}

	@TabCompleterFor(Arena.class)
	List<String> arenaTabComplete(String filter) {
		return ArenaManager.getNames(filter);
	}

	@ConverterFor(CheckpointArena.class)
	CheckpointArena convertToCheckpointArena(String value) {
		Arena arena;
		if ("current".equalsIgnoreCase(value)) {
			if (minigamer != null) {
				if (minigamer.getMatch() != null) {
					arena = minigamer.getMatch().getArena();
				} else
					throw new InvalidInputException("You are not currently in a match");
			} else
				throw new MustBeIngameException();
		} else {
			arena = ArenaManager.find(value);
		}

		if (!(arena instanceof CheckpointArena checkpointArena))
			throw new InvalidInputException("Sorry! At this time, only games with checkpoints have a leaderboard.");

		return checkpointArena;
	}

	@TabCompleterFor(CheckpointArena.class)
	List<String> checkpointArenaTabComplete(String filter) {
		return ArenaManager.getNamesStream(filter).filter(name -> ArenaManager.find(name) instanceof CheckpointArena).collect(Collectors.toList());
	}

	@ConverterFor(Minigamer.class)
	Minigamer convertToMinigamer(String value) {
		if ("self".equalsIgnoreCase(value))
			return minigamer;
		OfflinePlayer player = PlayerUtils.getPlayer(value);
		if (!player.isOnline())
			throw new PlayerNotOnlineException(player);
		return Minigamer.of(player.getPlayer());
	}

	@TabCompleterFor(Minigamer.class)
	List<String> tabCompleteMinigamer(String filter) {
		return tabCompletePlayer(filter);
	}

	@ConverterFor(Team.class)
	Team convertToTeam(String value, Arena context) {
		if ("current".equalsIgnoreCase(value))
			return minigamer.getTeam();

		if (context == null)
			context = minigamer.getMatch().getArena();

		return context.getTeams().stream()
				.filter(team -> team.getName().startsWith(value))
				.findFirst()
				.orElseThrow(() -> new InvalidInputException("Team not found"));
	}

	@TabCompleterFor(Team.class)
	List<String> tabCompleteTeam(String filter, Arena context) {
		if (context == null)
			context = minigamer.getMatch().getArena();

		if (context == null)
			return new ArrayList<>();

		return context.getTeams().stream()
				.map(Team::getName)
				.filter(name -> name.toLowerCase().startsWith(filter.toLowerCase()))
				.collect(Collectors.toList());
	}
}

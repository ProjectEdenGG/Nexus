package gg.projecteden.nexus.features.minigames.commands;

import com.sk89q.worldguard.protection.flags.Flag;
import gg.projecteden.annotations.Async;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.managers.MatchManager;
import gg.projecteden.nexus.features.minigames.managers.PlayerManager;
import gg.projecteden.nexus.features.minigames.mechanics.Mastermind;
import gg.projecteden.nexus.features.minigames.mechanics.common.CheckpointMechanic;
import gg.projecteden.nexus.features.minigames.menus.ArenaMenu;
import gg.projecteden.nexus.features.minigames.menus.PerkMenu;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.features.minigames.models.matchdata.CheckpointMatchData;
import gg.projecteden.nexus.features.minigames.models.matchdata.MastermindMatchData;
import gg.projecteden.nexus.features.minigames.models.modifiers.MinigameModifiers;
import gg.projecteden.nexus.features.minigames.models.perks.HideParticle;
import gg.projecteden.nexus.features.minigames.models.scoreboards.MinigameScoreboard;
import gg.projecteden.nexus.features.minigames.utils.MinigameNight.NextMGN;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Confirm;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromHelp;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.exceptions.postconfigured.PlayerNotOnlineException;
import gg.projecteden.nexus.framework.exceptions.preconfigured.MustBeIngameException;
import gg.projecteden.nexus.models.minigamersetting.MinigamerSetting;
import gg.projecteden.nexus.models.minigamersetting.MinigamerSettingService;
import gg.projecteden.nexus.models.minigamessetting.MinigamesConfig;
import gg.projecteden.nexus.models.minigamessetting.MinigamesConfigService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.perkowner.PerkOwner;
import gg.projecteden.nexus.models.perkowner.PerkOwnerService;
import gg.projecteden.nexus.models.punishments.Punishment;
import gg.projecteden.nexus.models.punishments.PunishmentType;
import gg.projecteden.nexus.models.punishments.Punishments;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.LocationUtils.RelativeLocation;
import gg.projecteden.nexus.utils.Name;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.nexus.utils.WorldGroup;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.utils.Env;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.utils.Nullables.isNullOrEmpty;
import static gg.projecteden.nexus.utils.StringUtils.stripColor;

@Aliases({"mgm", "mg"})
@Redirect(from = "/mgn", to = "/mgm night")
@Permission(MinigamesCommand.PERMISSION_USE)
public class MinigamesCommand extends CustomCommand {
	public static final String PERMISSION_USE = "minigames.use";
	public static final String PERMISSION_MANAGE = "minigames.manage";

	public static final String MINIGAME_SIGN_HEADER = "&0&l< &1Minigames &0&l>";
	public static final String OLD_MGM_SIGN_HEADER = "&1[Minigame]";

	private Minigamer minigamer;
	private final MinigamesConfigService configService = new MinigamesConfigService();

	public MinigamesCommand(CommandEvent event) {
		super(event);
		PREFIX = Minigames.PREFIX;
		if (sender() instanceof Player)
			minigamer = PlayerManager.get(player());
	}

	@Path
	@Permission(PERMISSION_USE)
	void warp() {
		runCommand("warp minigames");
	}

	@Path("night")
	void night() {
		NextMGN mgn = isPlayer() ? new NextMGN(player()) : new NextMGN();

		line();
		if (mgn.isNow())
			send("&3Minigame night is happening right now! Join with &e/gl");
		else
			send("&3The next &eMinigame Night &3will be hosted on &e" + mgn.getDateFormatted() + "&3 at &e"
				+ mgn.getTimeFormatted() + "&3. That is in &e" + mgn.getUntil());
	}

	@Path("list [filter]")
	@Permission(PERMISSION_USE)
	void list(String filter) {
		JsonBuilder json = json(PREFIX);
		final List<Arena> arenas = ArenaManager.getAll(filter).stream()
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

	@Path("join <arena>")
	@Permission(PERMISSION_USE)
	void join(Arena arena) {
		minigamer.join(arena);
	}

	@Path("allJoin <arena>")
	@Permission(Group.ADMIN)
	void allJoin(Arena arena) {
		if (Nexus.getEnv() == Env.PROD)
			error("Cannot use this command on production server");

		for (Player player : OnlinePlayers.getAll())
			PlayerManager.get(player).join(arena);
	}

	@Path("(quit|leave)")
	@Permission(PERMISSION_USE)
	void quit() {
		minigamer.quit();
	}

	@Path("warn <player> [reason]")
	@Permission(Group.MODERATOR)
	void warn(Player player, String reason) {
		if (!Minigames.isMinigameWorld(player.getWorld()))
			error("Target player is not in minigames");

		player.getWorld().strikeLightningEffect(player.getLocation());
		Punishments.of(player).add(Punishment.ofType(PunishmentType.WARN).punisher(uuid())
				.input("Please obey the rules of our minigames" + (isNullOrEmpty(reason) ? "" : ": " + reason)));
	}

	@Path("testMode [boolean]")
	@Permission(PERMISSION_MANAGE)
	void testMode(Boolean enable) {
		if (enable == null)
			enable = !Minigames.getTestModePlayers().contains(uuid());

		if (enable)
			Minigames.getTestModePlayers().add(uuid());
		else
			Minigames.getTestModePlayers().remove(uuid());

		send(PREFIX + "Testing mode " + (enable ? "&aenabled" : "&cdisabled"));
	}

	@Path("settings bowInOffHand [boolean]")
	@Permission(PERMISSION_USE)
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
	@Permission(PERMISSION_MANAGE)
	void start(@Arg("current") Arena arena) {
		getRunningMatch(arena).start();
	}

	@Path("end [arena]")
	@Permission(PERMISSION_MANAGE)
	void end(@Arg("current") Arena arena) {
		getRunningMatch(arena).end();
	}

	@Path("debug [arena]")
	@Permission(PERMISSION_MANAGE)
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

	@Permission(PERMISSION_MANAGE)
	@Path("signs join <arena>")
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

	@Permission(PERMISSION_MANAGE)
	@Path("signs quit")
	void quitSign() {
		updateSign(MINIGAME_SIGN_HEADER, "&aQuit");
	}

	@Permission(PERMISSION_MANAGE)
	@Path("signs lobby")
	void lobbySign() {
		updateSign(MINIGAME_SIGN_HEADER, "&aLobby");
	}

	@Permission(PERMISSION_MANAGE)
	@Path("signs flag <team>")
	void flagSign(String team) {
		updateSign(OLD_MGM_SIGN_HEADER, "&aFlag", team);
	}

	@Permission(PERMISSION_MANAGE)
	@Path("signs flag capture <team>")
	void flagCaptureSign(String team) {
		updateSign(OLD_MGM_SIGN_HEADER, "&aFlag", "&aCapture", team);
	}

	@Path("setTime <seconds>")
	@Permission(PERMISSION_MANAGE)
	void setTime(int seconds) {
		if (minigamer.getMatch() == null)
			error("You are not in a match");
		minigamer.getMatch().getTimer().setTime(seconds);
		minigamer.getMatch().getTimer().broadcastTimeLeft();
	}

	@Path("flagParticle")
	@Permission(PERMISSION_MANAGE)
	void flagParticle() {
		gg.projecteden.nexus.features.minigames.models.matchdata.Flag.particle(minigamer);
	}

	@Path("create <name>")
	@Permission(PERMISSION_MANAGE)
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
	@Permission(PERMISSION_MANAGE)
	void copy(Arena arena, String name) {
		if (ArenaManager.exists(name))
			error("&e" + name + " already exists");

		Arena copy = ArenaManager.convert(arena, arena.getClass());
		copy.setId(ArenaManager.getNextId());
		copy.setName(name);
		copy.setDisplayName(name);
		copy.write();
		send(PREFIX + "Creating arena &e" + name + "&3");
		send(PREFIX + "&cRecommended: &3Edit .yml file to remove locations");
		new ArenaMenu(ArenaManager.get(name)).open(player());
	}

	@Path("edit <arena>")
	@Permission(PERMISSION_MANAGE)
	void edit(Arena arena) {
		new ArenaMenu(arena).open(player());
	}

	@Path("warp <arena>")
	@Permission(PERMISSION_MANAGE)
	void teleport(Arena arena) {
		arena.teleport(minigamer);
	}

	@Path("(tp|teleport) <player> [player]")
	@Permission(PERMISSION_MANAGE)
	void teleport(Minigamer minigamer1, Minigamer minigamer2) {
		if (minigamer2 == null)
			minigamer.teleportAsync(minigamer1.getPlayer().getLocation());
		else
			minigamer1.teleportAsync(minigamer2.getPlayer().getLocation());
	}

	@Path("tppos <player> <x> <y> <z> [yaw] [pitch]")
	@Permission(PERMISSION_MANAGE)
	void teleport(Minigamer minigamer, String x, String y, String z, String yaw, String pitch) {
		Location location = minigamer.getPlayer().getLocation();
		RelativeLocation.modify(location).x(x).y(y).z(z).yaw(yaw).pitch(pitch).update();
		minigamer.teleportAsync(location);
	}

	@Confirm
	@Path("(delete|remove) <arena>")
	@Permission(PERMISSION_MANAGE)
	void remove(Arena arena) {
		arena.delete();
		send(PREFIX + "Arena &e" + arena.getName() + " &3deleted");
	}

	@Path("(reload|read) [arena]")
	@Permission(PERMISSION_MANAGE)
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
	@Permission(PERMISSION_MANAGE)
	void save(Arena arena) {
		long startTime = System.currentTimeMillis();

		if (arena == null)
			ArenaManager.write();
		else
			ArenaManager.write(arena);

		send(PREFIX + "Save time took " + (System.currentTimeMillis() - startTime) + "ms");
	}

	@Path("autoreset [boolean]")
	@Permission(PERMISSION_USE)
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

	@Path("addSpawnpoint <arena> [team]")
	@Permission(PERMISSION_MANAGE)
	void addSpawnpoint(Arena arena, @Arg(context = 1) Team team) {
		List<Team> teams = arena.getTeams();

		if (team == null) {
			if (teams.size() != 1)
				error("There is more than one team in that arena, you must specify which one");

			teams.get(0).getSpawnpoints().add(location());
			arena.write();
			send(PREFIX + "Spawnpoint added");
			return;
		}

		team.getSpawnpoints().add(location());
		arena.write();
		send(PREFIX + "Spawnpoint added");
	}

	@Path("schem save <arena> <name>")
	@Permission(PERMISSION_MANAGE)
	void schemSave(Arena arena, String name) {
		WorldEditUtils worldedit = new WorldEditUtils(player());
		GameMode originalGameMode = player().getGameMode();
		Location originalLocation = location().clone();
		Location location = worldedit.toLocation(worldedit.getPlayerSelection(player()).getMinimumPoint());
		player().setGameMode(GameMode.SPECTATOR);
		player().teleportAsync(location);
		runCommand("mcmd /copy ;; wait 10 ;; /schem save " + (arena.getSchematicBaseName() + name) + " -f");
		Tasks.wait(20, () -> {
			player().teleportAsync(originalLocation);
			player().setGameMode(originalGameMode);
		});

		send(PREFIX + "Saved schematic " + name);
	}

	@Path("schem paste <arena> <name>")
	@Permission(PERMISSION_MANAGE)
	void schemPaste(Arena arena, String name) {
		String schematicName = arena.getSchematicName(name);
		new WorldEditUtils(world()).paster().file(schematicName).at(location()).pasteAsync();
		send(PREFIX + "Pasted schematic " + schematicName);
	}

	@Async
	@Path("topic update")
	@Permission(Group.ADMIN)
	void topic_update() {
		Minigames.updateTopic();
	}

	private static String inviteCommand;
	private static String inviteMessage;

	private void updateInvite(Arena arena) {
		final boolean noStaffInMinigames = OnlinePlayers.where()
			.worldGroup(WorldGroup.MINIGAMES)
			.rank(Rank::isStaff)
			.get().isEmpty();

		boolean canUse = false;
		if (!new NextMGN().isNow() || noStaffInMinigames)
			canUse = true;
		if (player().hasPermission("minigames.invite"))
			canUse = true;

		if (!canUse)
			permissionError();

		WorldGuardUtils worldguard = new WorldGuardUtils(player());
		if (!worldguard.isInRegion(location(), "minigamelobby"))
			error("You must be in the Minigame Lobby to use this command");

		if (worldguard.isInRegion(location(), "screenshot")) {
			inviteCommand = "warp screenshot";
			inviteMessage = "take a screenshot";
		} else {
			if (arena == null) {
				Sign sign = getTargetSignRequired();
				String line2 = stripColor(sign.getLine(1)).toLowerCase();
				if (line2.contains("screenshot"))
					error("Stand in the screenshot area then run the command (sign not needed)");
				if (!line2.contains("join"))
					error("Cannot parse sign. If you believe this is an error, make a GitHub ticket with information and screenshots.");

				String line1 = stripColor(sign.getLine(0)).toLowerCase();
				if (!line1.contains("[minigame]") && !line1.contains("< minigames >"))
					error("Cannot parse sign. If you believe this is an error, make a GitHub ticket with information and screenshots.");

				String line3 = stripColor(sign.getLine(2)) + stripColor(sign.getLine(3));
				arena = ArenaManager.get(line3);
			}

			inviteCommand = "mgm join " + arena.getName();

			String mechanic = arena.getMechanic().getName();
			inviteMessage = mechanic + " &3on &e" + arena.getName();
			if (arena.getName().equalsIgnoreCase(mechanic))
				inviteMessage = arena.getName();
		}
	}

	private void sendInvite(Collection<? extends Player> players) {
		String sender = nickname();
		send("&3Invite sent to &e" + (players.size() - 1) + " &3players for &e" + inviteMessage);
		for (Player player : players) {
			if (player.equals(player()))
				continue;

			send(player, json("")
				.newline()
				.next(" &e" + sender + " &3has invited you to play &e" + inviteMessage).group()
				.newline()
				.next("&e Click here to &a&laccept")
				.command("/mgm accept")
				.hover("&eClick &3to accept"));
		}
	}

	@Path("invite [arena]")
	void invite(Arena arena) {
		Collection<Player> players = new WorldGuardUtils(player()).getPlayersInRegion("minigamelobby");
		int count = players.size() - 1;
		if (count == 0)
			error("There is no one to invite!");

		updateInvite(arena);
		sendInvite(new WorldGuardUtils(player()).getPlayersInRegion("minigamelobby"));
	}

	@Permission(PERMISSION_MANAGE)
	@Path("inviteAll [arena]")
	void inviteAll(Arena arena) {
		updateInvite(arena);
		sendInvite(OnlinePlayers.getAll());
	}

	@Path("accept")
	void acceptInvite() {
		if (inviteCommand == null)
			error("There is no pending game invite");

		if (world() != Minigames.getWorld()) {
			WarpType.NORMAL.get("minigames").teleportAsync(player());
			Tasks.wait(5, this::acceptInvite);
		} else
			runCommand(inviteCommand);
	}

	static {
		Arrays.asList(MinigamePodiumPosition.values()).forEach(position ->
			Nexus.getInstance().addConfigDefault("minigames.podiums." + position.name().toLowerCase(), 0));
	}

	@Path("(podium|podiums) <position> <player> <title...>")
	void update(MinigamePodiumPosition position, OfflinePlayer player, String title) {
		CitizensUtils.updateName(position.getNPC(), "&l" + Nickname.of(player));
		CitizensUtils.updateSkin(position.getNPC(), Name.of(player));
		PlayerUtils.runCommandAsConsole("hd setline podium_" + position + " 1 " + title);
		send(PREFIX + "Podium updated");
	}

	@Path("(podium|podiums) (getId|getIds) [position]")
	void getId(MinigamePodiumPosition position) {
		if (position == null) {
			send(PREFIX + "Podium IDs:");
			Arrays.asList(MinigamePodiumPosition.values()).forEach(_position ->
				send("&3" + StringUtils.camelCase(_position.name()) + ": &e" + _position.getId()));
		} else
			send(PREFIX + StringUtils.camelCase(position.name()) + ": &e" + position.getId());
	}

	@Path("(podium|podiums) setId <position> <id>")
	void setId(MinigamePodiumPosition position, int id) {
		position.setId(id);
		send(PREFIX + StringUtils.camelCase(position.name()) + " podium ID updated to " + id);
	}

	@Path("(podium|podiums) tp <position>")
	void tp(MinigamePodiumPosition position) {
		player().teleportAsync(position.getNPC().getEntity().getLocation(), TeleportCause.COMMAND);
	}

	@Path("(podium|podiums) (s|summon) <position>")
	void tphere(MinigamePodiumPosition position) {
		runCommand("blockcenter");
		position.getNPC().getEntity().teleportAsync(location());
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

	@Path("holeinthewall flag <arena> <regionType> <flag> <setting...>")
	void holeInTheWallFlag(Arena arena, String regionType, Flag<?> flag, String setting) {
		for (int i = 1; i <= arena.getMaxPlayers(); i++)
			runCommand("rg flag holeinthewall_" + arena.getName() + "_" + regionType + "_" + i + " " + flag + " " + setting);
	}

	@Path("collectibles")
	void collectibles() {
		if (player().getWorld() != Minigames.getWorld())
			error("You must be in the gameworld to use this command");
		new PerkMenu().open(player());
	}

	@Path("tokens [user]")
	void getTokens(@Arg("self") Nerd nerd) {
		PerkOwnerService service = new PerkOwnerService();
		PerkOwner perkOwner = service.get(nerd);
		String username = nerd.getUuid().equals(player().getUniqueId()) ? "You have" : (perkOwner.getNickname() + " has");
		send(PREFIX + username + " " + perkOwner.getTokens() + plural(" token", perkOwner.getTokens()));
	}

	@Path("tokens set <amount> [user]")
	@Permission(Group.SENIOR_STAFF)
	void setTokens(int amount, @Arg("self") Nerd nerd) {
		PerkOwnerService service = new PerkOwnerService();
		PerkOwner perkOwner = service.get(nerd);
		perkOwner.setTokens(amount);
		service.save(perkOwner);
		String username = nerd.getUuid().equals(player().getUniqueId()) ? "Your" : (perkOwner.getNickname() + "'s");
		send(PREFIX + username + " tokens were set to " + amount);
	}

	@Path("tokens add <amount> [user]")
	@Permission(Group.SENIOR_STAFF)
	void addTokens(int amount, @Arg("self") Nerd nerd) {
		PerkOwnerService service = new PerkOwnerService();
		PerkOwner perkOwner = service.get(nerd);
		perkOwner.setTokens(amount + perkOwner.getTokens());
		service.save(perkOwner);
		String username = nerd.getUuid().equals(player().getUniqueId()) ? "You now have" : (perkOwner.getNickname() + " now has");
		send(PREFIX + username + " " + perkOwner.getTokens() + plural(" token", perkOwner.getTokens()));
	}

	@Path("tokens remove <amount> [user]")
	@Permission(Group.SENIOR_STAFF)
	void removeTokens(int amount, @Arg("self") Nerd nerd) {
		addTokens(-1 * amount, nerd);
	}

	@Path("mastermind showAnswer")
	@Permission(Group.ADMIN)
	void mastermindShowAnswer() {
		if (!minigamer.isPlaying(Mastermind.class))
			error("You must be playing Mastermind to use this command");

		MastermindMatchData matchData = minigamer.getMatch().getMatchData();
		send(matchData.getAnswer().toString());
	}

	@HideFromHelp
	@TabCompleteIgnore
	@Path("mastermind playAgain")
	void mastermindPlayAgain() {
		if (!minigamer.isPlaying(Mastermind.class))
			error("You must be playing Mastermind to use this command");

		MastermindMatchData matchData = minigamer.getMatch().getMatchData();
		matchData.reset(minigamer);
	}

	@Path("hideParticles <type>")
	void hideParticles(HideParticle type) {
		PerkOwnerService service = new PerkOwnerService();
		PerkOwner owner = service.get(player());
		owner.setHideParticle(type);
		service.save(owner);
		send(Minigames.PREFIX + "Now hiding "+type.toString().toLowerCase()+" particles");
	}

	@Path("modifier <modifier>")
	@Permission(PERMISSION_MANAGE)
	void modifier(MinigameModifiers modifier) {
		MinigamesConfig setting = configService.get0();
		setting.setModifier(modifier);
		configService.save(setting);
		send(PREFIX + "Minigame modifier set to &e" + modifier.get().getName());
	}

	@Path("modifier random")
	@Permission(PERMISSION_MANAGE)
	void modifierRandom() {
		modifier(RandomUtils.randomElement(List.of(MinigameModifiers.values())));
	}

	@Path("refreshNameColors")
	@Permission(PERMISSION_MANAGE)
	void refreshNameColors() {
		MatchManager.getAll().forEach(match -> {
			MinigameScoreboard sb = match.getScoreboard();
			if (sb != null) {
				sb.update();
				send(PREFIX + "Refreshed " + match.getArena().getDisplayName());
			}
		});
	}

	private Match getRunningMatch(Arena arena) {
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
		OfflinePlayer player = PlayerUtils.getPlayer(value);
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

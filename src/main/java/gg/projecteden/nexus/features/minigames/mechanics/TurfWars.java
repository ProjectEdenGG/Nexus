package gg.projecteden.nexus.features.minigames.mechanics;

import com.destroystokyo.paper.event.entity.ProjectileCollideEvent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.TimeUtils.Timespan.FormatType;
import gg.projecteden.api.common.utils.TimeUtils.Timespan.TimespanBuilder;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.features.minigames.models.annotations.Regenerating;
import gg.projecteden.nexus.features.minigames.models.arenas.TurfWarsArena;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchRegeneratedEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.minigames.models.matchdata.TurfWarsMatchData;
import gg.projecteden.nexus.features.minigames.models.matchdata.TurfWarsMatchData.FloorRow;
import gg.projecteden.nexus.features.minigames.models.matchdata.TurfWarsMatchData.State;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.LocationUtils.Axis;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.MathUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.TitleBuilder;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.parchment.event.entity.PreEntityShootBowEvent;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

@Regenerating("floor")
public class TurfWars extends TeamMechanic {

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.BOW);
	}

	@Override
	public @NotNull String getName() {
		return "Turf Wars";
	}

	@Override
	public @NotNull String getDescription() {
		return "Shoot players to gain turf. First team to control the whole map wins!";
	}

	@Override
	public @NotNull GameMode getGameMode() {
		return GameMode.SURVIVAL;
	}

	private static void debug(String message) {
		Minigames.debug("[TurfWars] " + message);
	}

	@EventHandler
	public void on(MatchRegeneratedEvent event) {
		debug("MatchRegeneratedEvent(" + event.getMatch().getArena().getDisplayName() + ")");
		if (!event.getMatch().isMechanic(this))
			return;

		Tasks.wait(TickTime.SECOND, () -> setup(event.getMatch()));
	}

	@Override
	public void onStart(@NotNull MatchStartEvent event) {
		super.onStart(event);

		Match match = event.getMatch();
		final TurfWarsMatchData matchData = match.getMatchData();

		setState(match, State.BUILD);

		match.getTasks().repeat(TickTime.SECOND.x(2), TickTime.SECOND.x(2), () -> {
			match.getAlivePlayers().forEach(this::giveArrow);
		});

		match.getTasks().repeat(TickTime.SECOND.x(3), TickTime.SECOND.x(3), () -> {
			if (matchData.getState() == State.FIGHT)
				match.getAlivePlayers().forEach(player -> giveWool(player, 1));
		});

		match.getTasks().repeat(1, 1, () -> {
			match.getAlivePlayers().forEach(player -> {
				if (!isInValidTeamRegion(player))
					borderFunction.accept(player);
			});
		});

		// Main heartbeat of timer
		match.getTasks().repeat(TickTime.SECOND, TickTime.SECOND, () -> {
			matchData.setTime(matchData.getTime() - 1);
			if (matchData.getTime() > 0) {
				if (matchData.getTime() >= 4 || matchData.getState() != State.BUILD)
					return;

				for (Player player : match.getOnlinePlayers())
					player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, .5f, 1.3f);
			} else
				setState(match, matchData.getState().nextWithLoop());
		});
	}

	@Override
	public boolean useScoreboardNumbers() {
		return false;
	}

	@Override
	public @NotNull LinkedHashMap<String, Integer> getScoreboardLines(@NotNull Match match) {
		if (!match.isStarted())
			return super.getScoreboardLines(match);

		final Team team1 = match.getArena().getTeams().get(0);
		final Team team2 = match.getArena().getTeams().get(1);
		final TurfWarsMatchData matchData = match.getMatchData();

		return new LinkedHashMap<>() {{
			put("&f", 8);
			put(team1.getChatColor() + "&l" + team1.getName(), 7);
			put("&f" + match.getScores().get(team1), 6);
			put("&f&f", 5);
			put(team2.getChatColor() + "&l" + team2.getName(), 4);
			put("&f&f" + match.getScores().get(team2), 3);
			put("&f&f&f", 2);
			put(matchData.getState().getTitle(), 1);
			put(TimespanBuilder.ofSeconds(matchData.getTime()).format(FormatType.SHORT_NO_YEARS), 0);
		}};
	}

	Consumer<Player> borderFunction = player -> {
		Match match = Minigamer.of(player).getMatch();
		if (match == null)
			return;

		if (!new CooldownService().check(player.getUniqueId(), "turf-border", 5))
			return;

		applyBorderVelocity(player);
		match.getTasks().wait(TickTime.SECOND, () -> {
			if (!isInValidTeamRegion(player)) {
				applyBorderVelocity(player);
			}
		});
	};

	public void setState(Match match, State state) {
		TurfWarsMatchData matchData = match.getMatchData();

		if (matchData.getPhase() == 6) {
			match.end();
			return;
		}

		matchData.setState(state);
		matchData.setTime(state == State.FIGHT ? 180 : 20);

		for (Player player : match.getOnlinePlayers())
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, .5f, 1f);

		TitleBuilder builder = new TitleBuilder();
		if (state == State.BUILD) {
			for (Player player : match.getOnlinePlayers())
				giveWool(player, 16);
			builder.title("&3Build Time");
			builder.subtitle("Fighting will " + (matchData.getPhase() > 1 ? "continue" : "start") + " in 20 seconds");
		}
		if (state == State.FIGHT) {
			builder.title("&3Fight Time");
			if (matchData.getPhase() > 2)
				builder.subtitle("Kills are now worth &e" + (matchData.getFloorWorth() + 1) + " &frows");
		}

		builder.times(5, 40, 5);
		builder.players(match.getOnlinePlayers().toArray(new Player[0])).send();
		matchData.setPhase(matchData.getPhase() + 1);
	}

	private boolean isInValidTeamRegion(Player player) {
		Minigamer minigamer = Minigamer.of(player);
		Match match = minigamer.getMatch();
		if (match == null)
			return true;

		final TurfWarsMatchData matchData = match.getMatchData();
		final TurfWarsArena arena = match.getArena();
		final BlockVector3 position = WorldGuardUtils.toBlockVector3(player.getLocation());
		if (minigamer.getTeam() == arena.getTeams().get(0)) {
			if (matchData.getTeam2Region().contains(position))
				return false;

			if (arena.getRegion(arena.getTeams().get(1).getName() + "_spawn").contains(position))
				return false;
		} else {
			if (matchData.getTeam1Region().contains(position))
				return false;

			if (arena.getRegion(arena.getTeams().get(0).getName() + "_spawn").contains(position))
				return false;
		}

		return match.worldguard().isInRegion(player, arena.getProtectedRegion("turf")) ||
			arena.getRegion(minigamer.getTeam().getName() + "_spawn").contains(position);
	}

	private boolean isInValidTeamSpawn(Minigamer minigamer) {
		Match match = minigamer.getMatch();
		if (match == null)
			return true;

		TurfWarsArena arena = match.getArena();

		return arena.getRegion(minigamer.getTeam().getName() + "_spawn").contains(WorldGuardUtils.toBlockVector3(minigamer.getLocation()));
	}

	public void applyBorderVelocity(Player player) {
		Match match = Minigamer.of(player).getMatch();
		if (match == null)
			return;
		TurfWarsArena arena = match.getArena();
		Location endLoc = Minigamer.of(player).getTeam() == arena.getTeams().get(0) ? arena.getTeam1FloorEnd() : arena.getTeam2FloorEnd();

		Vector vector = endLoc.toVector().subtract(player.getLocation().toVector());
		debug("endLoc: " + endLoc);
		debug("vector: " + vector);

		Vector red = arena.getTeam1FloorEnd().toVector();
		Vector blue = arena.getTeam2FloorEnd().toVector();
		Vector addedVelocity = Minigamer.of(player).getTeam() == arena.getTeams().get(0) ? red.subtract(blue) : blue.subtract(red);
		debug("red: " + red);
		debug("blue: " + blue);
		debug("added: " + addedVelocity);

		vector.normalize().multiply(.7);
		vector.add(addedVelocity.normalize().multiply(.7));
		vector.setY(player.getLocation().getY() < arena.getProtectedRegion("turf").getMaximumPoint().getY() ? 1 : -1); // If above region, knock them back down
		debug("vector final: " + vector);

		debug(vector.toString());

		if (!NumberConversions.isFinite(vector.getX()))
			vector.setX(0);
		if (!NumberConversions.isFinite(vector.getY()))
			vector.setY(0);
		if (!NumberConversions.isFinite(vector.getY()))
			vector.setZ(0);

		player.setVelocity(vector);
		player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, .8f, .5f);
	}

	public void setup(Match match) {
		debug("setup(" + match.getArena().getDisplayName() + ")");
		TurfWarsArena arena = match.getArena();
		arena.setDirection();
		setupFloorRows(match);
		updateTeamRegions(match);
	}

	private List<Location> getFloorLocations(Match match) {
		return new ArrayList<>() {{
			for (ProtectedRegion region : match.getArena().getRegionsLike("floor"))
				match.getArena().worldedit().getBlocks(region).forEach(block -> {
					if (block.getType() == Material.BLACK_GLAZED_TERRACOTTA)
						add(block.getLocation());
				});
		}};
	}

	private void setupFloorRows(Match match) {
		debug("setupFloorRows(" + match.getArena().getDisplayName() + ")");
		final List<Location> floorLocations = getFloorLocations(match);
		final TurfWarsArena arena = match.getArena();
		final TurfWarsMatchData matchData = match.getMatchData();

		if (arena.getDirection() == Axis.X) {
			int min = Collections.min(floorLocations, Comparator.comparingInt(Location::getBlockX)).getBlockX();
			int max = Collections.max(floorLocations, Comparator.comparingInt(Location::getBlockX)).getBlockX();

			for (int x = min; x <= max; x++) {
				int i = x;
				matchData.getRows().add(new FloorRow(floorLocations.stream()
					.map(Location::getBlock)
					.filter(block -> block.getX() == i)
					.sorted(Comparator.comparingInt(Block::getZ))
					.toList()));
			}
		} else {
			int min = Collections.min(floorLocations, Comparator.comparingInt(Location::getBlockZ)).getBlockZ();
			int max = Collections.max(floorLocations, Comparator.comparingInt(Location::getBlockZ)).getBlockZ();

			for (int z = min; z <= max; z++) {
				int i = z;
				matchData.getRows().add(new FloorRow(floorLocations.stream()
					.map(Location::getBlock)
					.filter(block -> block.getZ() == i)
					.sorted(Comparator.comparingInt(Block::getX))
					.toList()));
			}
		}

		debug("Setup " + matchData.getRows().size() + " rows");
		setDefaultRows(match);
	}

	public void setDefaultRows(Match match) {
		final TurfWarsArena arena = match.getArena();
		final TurfWarsMatchData matchData = match.getMatchData();

		final Location firstBlock = matchData.getRows().get(0).getBlockList().get(0).getLocation();

		if (arena.getDirection() == Axis.X)
			if (firstBlock.getBlockX() == arena.getTeam1FloorEnd().getBlockX())
				setTeam1First(match);
			else
				setTeam2First(match);
		else
			if (firstBlock.getBlockZ() == arena.getTeam1FloorEnd().getBlockZ())
				setTeam1First(match);
			else
				setTeam2First(match);
	}

	public void updateScores(Match match) {
		final TurfWarsMatchData matchData = match.getMatchData();

		final Team team1 = match.getArena().getTeams().get(0);
		final Team team2 = match.getArena().getTeams().get(1);

		match.setScore(team1, (int) matchData.getRows().stream()
			.filter(row -> row.getTeam() == team1)
			.count());

		match.setScore(team2, (int) matchData.getRows().stream()
			.filter(row -> row.getTeam() == team2)
			.count());
	}

	public void setTeam1First(Match match) {
		final TurfWarsMatchData matchData = match.getMatchData();
		final List<FloorRow> rows = matchData.getRows();

		debug("Setting rows " + 0 + " thru " + ((rows.size() / 2) - 1));
		for (int i = 0; i < rows.size() / 2; i++)
			rows.get(i).setTeam(match.getArena().getTeams().get(0));

		debug("Setting rows " + rows.size() / 2 + " thru " + rows.size());
		for (int i = rows.size() / 2; i < rows.size(); i++)
			rows.get(i).setTeam(match.getArena().getTeams().get(1));

		updateScores(match);
	}

	public void setTeam2First(Match match) {
		final TurfWarsMatchData matchData = match.getMatchData();
		final List<FloorRow> rows = matchData.getRows();

		debug("Setting rows " + 0 + " thru " + ((rows.size() / 2) - 1));
		for (int i = 0; i < rows.size() / 2; i++)
			rows.get(i).setTeam(match.getArena().getTeams().get(1));

		debug("Setting rows " + rows.size() / 2 + " thru " + rows.size());
		for (int i = rows.size() / 2; i < rows.size(); i++)
			rows.get(i).setTeam(match.getArena().getTeams().get(0));

		updateScores(match);
	}

	public void moveFloor(Match match, Team team, int amount) {
		final TurfWarsMatchData matchData = match.getMatchData();
		final Function<Integer, Integer> clamp = newIndex -> MathUtils.clamp(newIndex, 0, matchData.getRows().size() - 1);
		final List<FloorRow> rows = matchData.getRows();

		int index = clamp.apply(rows.get(0).getTeam() == team ? 0 : rows.size() - 1);
		while (rows.get(index).getTeam() == team)
			index = clamp.apply(index + (rows.get(0).getTeam() == team ? 1 : -1));

		amount = (int) Math.min(amount, rows.size() - rows.stream().filter(row -> row.getTeam() != team).count());
		for (int i = 0; i < amount; i++)
			rows.get(index + (rows.get(0).getTeam() == team ? i : -i)).setTeam(team);

		updateScores(match);
		updateTeamRegions(match);
	}

	public void updateTeamRegions(Match match) {
		debug("updateTeamRegions(" + match.getArena().getDisplayName() + ")");
		TurfWarsMatchData matchData = match.getMatchData();
		for (Team team : match.getArena().getTeams()) {
			List<FloorRow> teamRows = matchData.getRows().stream().filter(row -> row.getTeam() == team).toList();
			if (teamRows.isEmpty()) continue;
			Location min = teamRows.get(0).getBlockList().get(0).getLocation().toCenterLocation();
			min.setY(match.getArena().getRegion("turf").getMinimumY());
			FloorRow row = teamRows.get(teamRows.size() - 1);
			Location max = row.getBlockList().get(row.getBlockList().size() - 1).getLocation().toCenterLocation();
			max.setY(match.getArena().getRegion("turf").getMaximumY());
			CuboidRegion region = new CuboidRegion(WorldGuardUtils.toBlockVector3(min), WorldGuardUtils.toBlockVector3(max));

			if (team == match.getArena().getTeams().get(0))
				matchData.setTeam1Region(region);
			else
				matchData.setTeam2Region(region);

			for (Player player : match.getOnlinePlayers()) {
				if (!isInValidTeamRegion(player))
					borderFunction.accept(player);
			}
		}
	}

	public Location getTurfLocation(Arena arena, Location base) {
		for (ProtectedRegion region : arena.getRegionsLike("floor"))
			for (int i = 1; i < 6; i++) {
				Location clone = base.clone().subtract(0, i, 0);
				if (!region.contains(WorldGuardUtils.toBlockVector3(clone)))
					continue;

				if (!MaterialTag.TERRACOTTA.isTagged(clone.getBlock().getType()))
					continue;

				return clone;
			}

		return null;
	}

	private void giveWool(Player player, int amount) {
		Minigamer minigamer = Minigamer.of(player);
		if (!minigamer.isPlaying(this))
			return;

		Material woolMat = minigamer.getTeam().getColorType().getWool();

		for (int i = 0; i < amount; i++) {
			if (player.getInventory().contains(woolMat, 64))
				return;

			if (!player.getInventory().contains(woolMat, 1))
				player.getInventory().setItem(1, new ItemStack(woolMat));
			else
				player.getInventory().addItem(new ItemStack(woolMat));
		}
	}

	private void giveArrow(Player player) {
		if (!Minigamer.of(player).isPlaying(this))
			return;

		if (!player.getInventory().contains(Material.ARROW, 2))
			if (!player.getInventory().contains(Material.ARROW, 1))
				player.getInventory().setItem(8, new ItemStack(Material.ARROW));
			else
				player.getInventory().addItem(new ItemStack(Material.ARROW));
	}

	@EventHandler
	public void on(PreEntityShootBowEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		if (Minigamer.of(player).isPlaying(this))
			event.setRelative(false);
	}

	// Build State Shoot Arrow Handler
	@EventHandler
	public void onShootArrow(ProjectileLaunchEvent event) {
		if (!(event.getEntity() instanceof Arrow arrow))
			return;

		if (!(arrow.getShooter() instanceof Player player))
			return;

		Minigamer minigamer = Minigamer.of(player);
		if (!minigamer.isPlaying(this))
			return;

		TurfWarsMatchData matchData = minigamer.getMatch().getMatchData();
		switch (matchData.getState()) {
			case FIGHT -> {
				if (isInValidTeamSpawn(minigamer)) {
					event.setCancelled(true);
					ActionBarUtils.sendActionBar(player, "&cYou must be in the play area to shoot arrows");
				}
			}

			case BUILD -> {
				event.setCancelled(true);
				ActionBarUtils.sendActionBar(player, "&cYou cannot shoot arrows during build time");
			}
		}
	}

	@EventHandler
	public void onPlaceBlock(BlockPlaceEvent event) {
		final Minigamer minigamer = Minigamer.of(event.getPlayer());
		if (!minigamer.isPlaying(this))
			return;

		Location turf = getTurfLocation(minigamer.getMatch().getArena(), event.getBlock().getLocation());
		if (turf == null) {
			errorBlockPlace(event);
			return;
		}
		if (minigamer.getTeam().getColorType().getTerracotta() != turf.getBlock().getType()) {
			event.setCancelled(true);
			errorBlockPlace(event);
		}
	}

	public void errorBlockPlace(BlockPlaceEvent event) {
		event.setCancelled(true);
		ActionBarUtils.sendActionBar(event.getPlayer(), "&cYou cannot shoot arrows during build time");
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		final Minigamer minigamer = Minigamer.of(event.getPlayer());
		if (!minigamer.isPlaying(this))
			return;

		Location turf = getTurfLocation(minigamer.getMatch().getArena(), event.getBlock().getLocation());
		if (turf == null) {
			event.setCancelled(true);
			return;
		}
		if (minigamer.getTeam().getColorType().getWool() != event.getBlock().getType())
			event.setCancelled(true);
	}

	Map<Arrow, Player> playerArrowMap = new HashMap<>();

	@EventHandler
	public void onShootArrowMap(ProjectileLaunchEvent event) {
		if (!(event.getEntity() instanceof Arrow arrow))
			return;

		if (!(arrow.getShooter() instanceof Player player))
			return;

		if (!Minigamer.of(player).isPlaying(this))
			return;


		this.playerArrowMap.put(arrow, player);
	}

	@EventHandler
	public void onArrowCollide(ProjectileCollideEvent event) {
		if (!(event.getEntity() instanceof Arrow arrow))
			return;

		if (!(event.getCollidedWith() instanceof Player collidedWith))
			return;

		if (!(arrow.getShooter() instanceof Player shooter))
			return;

		if (!Minigamer.of(shooter).isPlaying(this))
			return;

		if (Minigamer.of(collidedWith).getTeam() == null)
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerShootPlayer(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Arrow arrow))
			return;

		if (!(event.getEntity() instanceof Player player))
			return;

		if (!(arrow.getShooter() instanceof Player arrowShooter))
			return;

		Minigamer shooter = Minigamer.of(arrowShooter);
		Minigamer victim = Minigamer.of(player);
		Player shooterPlayer = shooter.getOnlinePlayer();
		Player victimPlayer = victim.getOnlinePlayer();

		if (!shooter.isPlaying(this) || !victim.isPlaying(this))
			return;

		if (shooter.getTeam() == victim.getTeam() || victim.getTeam() == null) {
			event.setCancelled(true);
			return;
		}

		if (isInValidTeamSpawn(shooter) || isInValidTeamSpawn(victim)) {
			if (isInValidTeamSpawn(victim))
				ActionBarUtils.sendActionBar(arrowShooter, "&cThis player has spawn protection.");

			event.setCancelled(true);
			return;
		}

		event.setCancelled(true);
		arrow.remove();

		victimPlayer.playEffect(EntityEffect.HURT);

		shooterPlayer.playSound(shooterPlayer.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1f, 1f);

		this.playerArrowMap.remove(arrow);

		final Match match = victim.getMatch();
		final TurfWarsMatchData matchData = match.getMatchData();

		victim.respawn();
		match.broadcast(shooter.getColoredName() + " &3killed " + victim.getColoredName() + shooter.getTeam().getChatColor() + " (+" + matchData.getFloorWorth() + ")");
		victimPlayer.playSound(victimPlayer.getLocation(), Sound.ENTITY_PLAYER_HURT, 1f, 1f);

		for (Arrow arrow1 : new ArrayList<>(this.playerArrowMap.keySet())) {
			if (this.playerArrowMap.get(arrow1) != victimPlayer)
				continue;

			arrow1.remove();
			this.playerArrowMap.remove(arrow1);
		}

		moveFloor(match, shooter.getTeam(), matchData.getFloorWorth());
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerShootBlock(ProjectileHitEvent event) {
		if (!(event.getEntity() instanceof Arrow arrow)) {
			debug("not arrow");
			return;
		}

		if (event.getHitBlock() == null) {
			debug("Block is null");
			return;
		}

		if (!(arrow.getShooter() instanceof Player arrowShooter)) {
			debug("shooter not player");
			return;
		}

		final Minigamer shooter = Minigamer.of(arrowShooter);
		if (!shooter.isPlaying(this))
			return;

		final Block block = event.getHitBlock();
		final Location location = block.getLocation();

		arrow.remove();
		this.playerArrowMap.remove(arrow);

		if (!MaterialTag.WOOL.isTagged(block)) {
			debug("not wool");
			return;
		}

		if (!shooter.isPlaying(this)) {
			debug("not playing");
			return;
		}

		Location floorLoc = getTurfLocation(shooter.getMatch().getArena(), location);
		if (floorLoc == null) {
			debug("floor null");
			return;
		}

		location.getWorld().spawnParticle(Particle.BLOCK_CRACK, location.toCenterLocation(), 50, block.getType().createBlockData());
		block.setType(Material.AIR);
	}

	@EventHandler
	public void on(BlockBreakEvent event) {
		final Minigamer minigamer = Minigamer.of(event.getPlayer());
		if (!minigamer.isPlaying(this))
			return;

		event.setDropItems(false);
	}

}

package gg.projecteden.nexus.features.minigames.mechanics;

import com.destroystokyo.paper.event.entity.ProjectileCollideEvent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.TimeUtils.Timespan.FormatType;
import gg.projecteden.api.common.utils.TimeUtils.Timespan.TimespanBuilder;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.features.minigames.models.annotations.Regenerating;
import gg.projecteden.nexus.features.minigames.models.arenas.TurfWarsArena;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchInitializeEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.minigames.models.matchdata.TurfWarsMatchData;
import gg.projecteden.nexus.features.minigames.models.matchdata.TurfWarsMatchData.State;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.LocationUtils.Axis;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

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

	@Override
	public void onInitialize(@NotNull MatchInitializeEvent event) {
		super.onInitialize(event);
		setup(event.getMatch());
	}

	@Override
	public void onStart(@NotNull MatchStartEvent event) {
		super.onStart(event);

		Match match = event.getMatch();

		setState(State.BUILD, match);

		match.getTasks().repeat(TickTime.SECOND.x(2), TickTime.SECOND.x(2), () -> {
			match.getAlivePlayers().forEach(this::giveArrow);
		});
		match.getTasks().repeat(TickTime.SECOND.x(3), TickTime.SECOND.x(3), () -> {
			if (((TurfWarsMatchData) match.getMatchData()).getState() == State.FIGHT)
				match.getAlivePlayers().forEach(player -> giveWool(player, 1));
		});
		match.getTasks().repeat(1, 1, () -> {
			match.getAlivePlayers().forEach(player -> {
				if (!isInValidTeamRegion(player))
					borderFunction.accept(player);
			});
		});

		// Main heartbeat of timer
		match.getTasks().repeat(20, 20, () -> {
			TurfWarsMatchData matchData = match.getMatchData();
			matchData.setTime(matchData.getTime() - 1);
			if (matchData.getTime() > 0) {
				if (matchData.getTime() < 4 && matchData.getState() == State.BUILD) {
					for (Player player : match.getOnlinePlayers())
						player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, .5f, 1.3f);
				}
			}
			else
				setState(EnumUtils.nextWithLoop(State.class, matchData.getState().ordinal()), match);
		});
	}

	@Override
	public @NotNull Map<String, Integer> getScoreboardLines(@NotNull Match match) {
		if (!match.isStarted()) {
			return super.getScoreboardLines(match);
		}

		Team team1 = match.getArena().getTeams().get(0);
		Team team2 = match.getArena().getTeams().get(1);
		return new HashMap<>() {{
			put("&f", 8);
			put(team1.getChatColor() + "&l" + team1.getName(), 7);
			put("&f" + match.getScores().get(team1), 6);
			put("&f&f", 5);
			put(team2.getChatColor() + "&l" + team2.getName(), 4);
			put("&f&f" + match.getScores().get(team2), 3);
			put("&f&f&f", 2);
			put(((TurfWarsMatchData) match.getMatchData()).getState().getTitle(), 1);
			put(TimespanBuilder.ofSeconds(((TurfWarsMatchData) match.getMatchData()).getTime()).format(FormatType.SHORT_NO_YEARS), 0);
		}};
	}

	Consumer<Player> borderFunction = player -> {
		Match match = Minigamer.of(player).getMatch();
		if (match == null)
			return;
		if (!new CooldownService().check(player.getUniqueId(), "turf-border", 5))
			return;
		applyBorderVelocity(player);
		match.getTasks().wait(20, () -> {
			if (!isInValidTeamRegion(player)) {
				applyBorderVelocity(player);
			}
		});
	};

	public void setState(State state, Match match) {
		TurfWarsMatchData matchData = match.getMatchData();

		if (matchData.getPhase() == 6) {
			match.end();
			return;
		}

		matchData.setState(state);
		matchData.setTime(state == State.FIGHT ? 120 : 20);

		for (Player player : match.getOnlinePlayers())
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, .5f, 1f);

		if (state == State.BUILD) {
			for (Player player : match.getOnlinePlayers())
				giveWool(player, 16);
			match.broadcast("&3Build time has started! Fighting will " + (matchData.getPhase() > 1 ? "continue" : "start") + " in 20 seconds");
		}
		if (state == State.FIGHT)
			if (matchData.getPhase() > 2)
				match.broadcast("&3Build time is now over! Kills are now worth &e" + (matchData.getFloorWorth() + 1) + " &3rows");
			else
				match.broadcast("&3The fight has begun!");

		matchData.setPhase(matchData.getPhase() + 1);
	}

	private boolean isInValidTeamRegion(Player player) {
		Match match = Minigamer.of(player).getMatch();
		if (match == null)
			return true;
		TurfWarsMatchData matchData = match.getMatchData();
		TurfWarsArena arena = match.getArena();
		if (Minigamer.of(player).getTeam() == arena.getTeams().get(0)) {
			if (matchData.getTeam2Region().contains(WorldGuardUtils.toBlockVector3(player.getLocation())))
				return false;
			if (arena.getRegion(arena.getTeams().get(1).getName() + "_spawn").contains(WorldGuardUtils.toBlockVector3(player.getLocation())))
				return false;
		}
		else {
			if (matchData.getTeam1Region().contains(WorldGuardUtils.toBlockVector3(player.getLocation())))
				return false;
			if (arena.getRegion(arena.getTeams().get(0).getName() + "_spawn").contains(WorldGuardUtils.toBlockVector3(player.getLocation())))
				return false;
		}
		return match.worldguard().isInRegion(player, arena.getProtectedRegion("turf")) ||
			       arena.getRegion(Minigamer.of(player).getTeam().getName() + "_spawn").contains(WorldGuardUtils.toBlockVector3(player.getLocation()));
	}

	public void applyBorderVelocity(Player player) {
		Match match = Minigamer.of(player).getMatch();
		if (match == null)
			return;
		TurfWarsArena arena = match.getArena();
		Location endLoc = Minigamer.of(player).getTeam() == arena.getTeams().get(0) ? arena.getTeam1FloorEnd() : arena.getTeam2FloorEnd();
		Nexus.debug("endLoc" + endLoc.toString());

		Vector vector = endLoc.toVector().subtract(player.getLocation().toVector());
		Nexus.debug("vector: " + vector);

		Vector red = arena.getTeam1FloorEnd().toVector();
		Nexus.debug("red: " + red);
		Vector blue = arena.getTeam2FloorEnd().toVector();
		Nexus.debug("blue: " + blue);
		Vector addedVelocity = Minigamer.of(player).getTeam() == arena.getTeams().get(0) ? red.subtract(blue) : blue.subtract(red);
		Nexus.debug("added: " + addedVelocity);

		vector.normalize().multiply(.7);
		vector.add(addedVelocity.normalize().multiply(.7));
		vector.setY(1);
		Nexus.debug("vector final: " + vector);

		Nexus.debug(vector.toString());

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
		TurfWarsArena arena = match.getArena();
		arena.setDirection();

		setupFloorRows(match);
		updateTeamRegions(match);
	}

	private List<Location> getFloorLocations(Match match) {
		Set<ProtectedRegion> regions = match.getArena().getRegionsLike("floor");
		List<Location> floorLocations = new ArrayList<>();

		for (ProtectedRegion region : regions) {
			BlockVector3 min = region.getMinimumPoint();
			BlockVector3 max = region.getMaximumPoint();
			World world = match.getWorld();
			for (int x = min.getBlockX(); x <= max.getBlockX(); x++)
				for (int y = min.getBlockY(); y <= max.getBlockY(); y++)
					for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++)
						if (world.getBlockAt(x, y, z).getType() == Material.BLACK_GLAZED_TERRACOTTA)
							floorLocations.add(new Location(world, x, y, z));
		}
		return floorLocations;
	}


	private void setupFloorRows(Match match) {
		List<Location> floorLocations = getFloorLocations(match);
		if (((TurfWarsArena) match.getArena()).getDirection() == Axis.X) {

			floorLocations.sort(Comparator.comparingInt(Location::getBlockX));
			int min = floorLocations.get(0).getBlockX();
			int max = floorLocations.get(floorLocations.size() - 1).getBlockX();

			for (int x = min; x <= max; x++) {
				int i = x;
				List<Block> blockList = new ArrayList<>();
				FloorRow row = new FloorRow();
				floorLocations.stream().filter(loc -> loc.getBlockX() == i).forEach(loc -> blockList.add(loc.getBlock()));
				row.getBlockList().addAll(blockList);
				row.getBlockList().sort(Comparator.comparingInt(block -> block.getLocation().getBlockZ()));
				((TurfWarsMatchData) match.getMatchData()).getRows().add(row);
			}
		}
		else {

			floorLocations.sort(Comparator.comparingInt(Location::getBlockZ));
			int min = floorLocations.get(0).getBlockZ();
			int max = floorLocations.get(floorLocations.size() - 1).getBlockZ();

			for (int z = min; z <= max; z++) {
				int i = z;
				List<Block> blockList = new ArrayList<>();
				FloorRow row = new FloorRow();
				floorLocations.stream().filter(loc -> loc.getBlockZ() == i).forEach(loc -> blockList.add(loc.getBlock()));
				row.getBlockList().addAll(blockList);
				row.getBlockList().sort(Comparator.comparingInt(block -> block.getLocation().getBlockX()));
				((TurfWarsMatchData) match.getMatchData()).getRows().add(row);
			}
		}

		Nexus.debug("Setup " + ((TurfWarsMatchData) match.getMatchData()).getRows().size() + " rows");
		setDefaultRows(match);
	}

	public void setDefaultRows(Match match) {
		if (((TurfWarsArena) match.getArena()).getDirection() == Axis.X)
			if (((TurfWarsMatchData) match.getMatchData()).getRows().get(0).getBlockList().get(0).getLocation().getBlockX() == ((TurfWarsArena) match.getArena()).getTeam1FloorEnd().getBlockX())
				setTeam1First(match);
			else
				setTeam2First(match);
		else
			if (((TurfWarsMatchData) match.getMatchData()).getRows().get(0).getBlockList().get(0).getLocation().getBlockZ() == ((TurfWarsArena) match.getArena()).getTeam1FloorEnd().getBlockZ())
				setTeam1First(match);
			else
				setTeam2First(match);
	}

	public void updateScores(Match match) {
		TurfWarsMatchData matchData = match.getMatchData();
		match.setScore(match.getArena().getTeams().get(0), (int) matchData.getRows().stream().filter(row -> row.getTeam() == match.getArena().getTeams().get(0)).count());
		match.setScore(match.getArena().getTeams().get(1), (int) matchData.getRows().stream().filter(row -> row.getTeam() == match.getArena().getTeams().get(1)).count());
	}

	public void setTeam1First(Match match) {
		List<FloorRow> rows = ((TurfWarsMatchData) match.getMatchData()).getRows();
		Nexus.debug("Setting rows " + 0 + " thru " + ((rows.size() / 2) - 1));
		for (int i = 0; i < rows.size() / 2; i++) {
			rows.get(i).setTeam(match.getArena().getTeams().get(0));
		}
		Nexus.debug("Setting rows " + rows.size() / 2 + " thru " + rows.size());
		for (int i = rows.size() / 2; i < rows.size(); i++) {
			rows.get(i).setTeam(match.getArena().getTeams().get(1));
		}
		updateScores(match);
	}

	public void setTeam2First(Match match) {
		List<FloorRow> rows = ((TurfWarsMatchData) match.getMatchData()).getRows();
		Nexus.debug("Setting rows " + 0 + " thru " + ((rows.size() / 2) - 1));
		for (int i = 0; i < rows.size() / 2; i++) {
			rows.get(i).setTeam(match.getArena().getTeams().get(1));
		}
		Nexus.debug("Setting rows " + rows.size() / 2 + " thru " + rows.size());
		for (int i = rows.size() / 2; i < rows.size(); i++) {
			rows.get(i).setTeam(match.getArena().getTeams().get(0));
		}
		updateScores(match);
	}

	public void moveFloor(Match match, Team team, int amount) {
		TurfWarsMatchData matchData = match.getMatchData();
		List<FloorRow> rows = matchData.getRows();
		int index = rows.get(0).getTeam() == team ? 0 : rows.size() - 1;
		while (rows.get(index).getTeam() == team) {
			index = index + (rows.get(0).getTeam() == team ? 1 : -1);
		}
		for (int i = 0; i < amount; i++) {
			rows.get(index + (rows.get(0).getTeam() == team ? i : -i)).setTeam(team);
		}

		updateScores(match);
		updateTeamRegions(match);
	}

	public void updateTeamRegions(Match match) {
		TurfWarsMatchData matchData = match.getMatchData();
		for (Team team : match.getArena().getTeams()) {
			List<FloorRow> teamRows = matchData.getRows().stream().filter(row -> row.getTeam() == team).toList();
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

	public Location getTurfLocation(Location base) {
		Set<ProtectedRegion> regions = ArenaManager.getFromLocation(base).getRegionsLike("floor");
		for (ProtectedRegion region : regions) {
			for (int i = 1; i < 6; i++) {
				Location clone = base.clone().subtract(0, i, 0);
				if (region.contains(WorldGuardUtils.toBlockVector3(clone))) {
					return clone;
				}
			}
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

	// Build State Shoot Arrow Handler
	@EventHandler
	public void onShootArrow(ProjectileLaunchEvent event) {
		if (!(event.getEntity() instanceof Arrow arrow)) return;
		if (!(arrow.getShooter() instanceof Player player)) return;

		Minigamer minigamer = Minigamer.of(player);
		if (!minigamer.isPlaying(this))
			return;
		if (((TurfWarsMatchData) minigamer.getMatch().getMatchData()).getState() != State.BUILD)
			return;

		event.setCancelled(true);
		player.sendActionBar("§cYou cannot shoot arrows during build time");
	}

	@EventHandler
	public void onPlaceBlock(BlockPlaceEvent event) {
		if (!Minigamer.of(event.getPlayer()).isPlaying(this)) return;

		Location turf = getTurfLocation(event.getBlock().getLocation());
		if (turf == null) {
			errorBlockPlace(event);
			return;
		}
		if (Minigamer.of(event.getPlayer()).getTeam().getColorType().getTerracotta() != turf.getBlock().getType()) {
			event.setCancelled(true);
			errorBlockPlace(event);
		}
	}

	public void errorBlockPlace(BlockPlaceEvent event) {
		event.setCancelled(true);
		event.getPlayer().sendActionBar("§cYou can only place a block above your turf");
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (!Minigamer.of(event.getPlayer()).isPlaying(this)) return;

		Location turf = getTurfLocation(event.getBlock().getLocation());
		if (turf == null) {
			event.setCancelled(true);
			return;
		}
		if (Minigamer.of(event.getPlayer()).getTeam().getColorType().getWool() != event.getBlock().getType())
			event.setCancelled(true);
	}

	Map<Arrow, Player> playerArrowMap = new HashMap<>();

	@EventHandler
	public void onShootArrowMap(ProjectileLaunchEvent event) {
		if (!(event.getEntity() instanceof Arrow arrow)) return;
		if (!(arrow.getShooter() instanceof Player player)) return;
		if (!Minigamer.of(player).isPlaying(this)) return;

		this.playerArrowMap.put(arrow, player);
	}

	@EventHandler
	public void onArrowCollide(ProjectileCollideEvent event) {
		if (!(event.getEntity() instanceof Arrow)) return;
		if (!(event.getCollidedWith() instanceof Player collidedWith)) return;
		if (!(event.getEntity().getShooter() instanceof Player shooter)) return;
		if (!Minigamer.of(shooter).isPlaying(this)) return;

		if (Minigamer.of(collidedWith).getTeam() == null)
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerShootPlayer(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Arrow arrow)) return;
		if (!(event.getEntity() instanceof Player player)) return;
		if (!(arrow.getShooter() instanceof Player arrowShooter)) return;

		Minigamer shooter = Minigamer.of(arrowShooter);
		Minigamer hitPlayer = Minigamer.of(player);

		if (!shooter.isPlaying(this) || !hitPlayer.isPlaying(this))
			return;

		if (shooter.getTeam() == hitPlayer.getTeam() || hitPlayer.getTeam() == null) {
			event.setCancelled(true);
			return;
		}

		if (hitPlayer.getMatch().getArena().getRegion(hitPlayer.getTeam().getName() + "_spawn").contains(WorldGuardUtils.toBlockVector3(hitPlayer.getLocation()))) {
			event.setCancelled(true);
			return;
		}
		if (shooter.getMatch().getArena().getRegion(shooter.getTeam().getName() + "_spawn").contains(WorldGuardUtils.toBlockVector3(shooter.getLocation()))) {
			event.setCancelled(true);
			return;
		}

		event.setCancelled(true);
		arrow.remove();

		hitPlayer.getPlayer().playEffect(EntityEffect.HURT);

		shooter.getPlayer().playSound(shooter.getPlayer().getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1f, 1f);

		this.playerArrowMap.remove(arrow);

		TurfWarsMatchData matchData = shooter.getMatch().getMatchData();

		hitPlayer.respawn();
		hitPlayer.getMatch().broadcast(shooter.getColoredName() + " &3killed " + hitPlayer.getColoredName() + shooter.getTeam().getChatColor() + " (+" + matchData.getFloorWorth() + ")");
		hitPlayer.getPlayer().playSound(hitPlayer.getPlayer().getLocation(), Sound.ENTITY_PLAYER_HURT, 1f, 1f);

		for (Arrow arrow1 : new ArrayList<>(Arrays.asList(this.playerArrowMap.keySet().toArray(new Arrow[0])))) {
			if (this.playerArrowMap.get(arrow1) == hitPlayer.getPlayer()) {
				arrow1.remove();
				this.playerArrowMap.remove(arrow1);
			}
		}

		moveFloor(shooter.getMatch(), shooter.getTeam(), matchData.getFloorWorth());
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerShootBlock(ProjectileHitEvent event) {
		if (!(event.getEntity() instanceof Arrow arrow)) {
			Nexus.debug("not arrow");
			return;
		}
		if (event.getHitBlock() == null) {
			Nexus.debug("Block is null");
			return;
		}
		if (!(arrow.getShooter() instanceof Player arrowShooter)) {
			Nexus.debug("shooter not player");
			return;
		}

		Block block = event.getHitBlock();
		arrow.remove();
		this.playerArrowMap.remove(arrow);
		if (!MaterialTag.WOOL.isTagged(block)) {
			Nexus.debug("not wool");
			return;
		}

		Minigamer shooter = Minigamer.of(arrowShooter);
		if (!shooter.isPlaying(this)) {
			Nexus.debug("not playing");
			return;
		}

		Location floorLoc = getTurfLocation(block.getLocation());
		if (floorLoc == null) {
			Nexus.debug("floor null");
			return;
		}

		block.getLocation().getWorld().spawnParticle(Particle.BLOCK_CRACK, block.getLocation().toCenterLocation(), 50, block.getType().createBlockData());
		block.setType(Material.AIR);
	}

	public class FloorRow {

		public Team team;
		public List<Block> blockList = new ArrayList<>();

		public Team getTeam() {
			return this.team;
		}

		public List<Block> getBlockList() {
			return this.blockList;
		}

		public void setTeam(Team team) {
			if (this.team == null || this.team != team) {
				for (Block block : this.blockList) {
					block.setType(team.getColorType().getTerracotta());
					for (int i = 1; i <= 5; i++) {
						Block relative = block.getRelative(0, i, 0);
						if (MaterialTag.WOOL.isTagged(relative)) {
							relative.setType(Material.AIR);
						}
					}
				}
			}
			this.team = team;
		}

	}

}

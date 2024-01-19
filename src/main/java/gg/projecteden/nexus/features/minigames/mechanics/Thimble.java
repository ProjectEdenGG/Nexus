package gg.projecteden.nexus.features.minigames.mechanics;

import com.sk89q.worldedit.world.block.BlockTypes;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.managers.MatchManager;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Match.MatchTasks.MatchTaskType;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.arenas.ThimbleArena;
import gg.projecteden.nexus.features.minigames.models.arenas.ThimbleMap;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchEndEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchInitializeEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchJoinEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchQuitEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerDamageEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import gg.projecteden.nexus.features.minigames.models.matchdata.ThimbleMatchData;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.FireworkLauncher;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import lombok.Getter;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static gg.projecteden.nexus.utils.StringUtils.camelCase;
import static gg.projecteden.nexus.utils.StringUtils.plural;

//TODO: Show turn timer to turn player
//TODO: Gamemode description messages onm lobby join

public final class Thimble extends TeamlessMechanic {

	@Getter
	private final List<Material> COLOR_CHOICES = List.of(MaterialTag.CONCRETES.exclude(Material.BLUE_CONCRETE, Material.LIGHT_BLUE_CONCRETE, Material.CYAN_CONCRETE).toArray());

	@Getter
	private final int MAX_TURNS = 49;

	@Override
	public @NotNull String getName() {
		return "Thimble";
	}

	@Override
	public @NotNull String getDescription() {
		return "Earn points by landing in the water-- but watch out, each successful drop fills in the pool a bit more";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.CHAINMAIL_HELMET);
	}

	@Override
	public boolean shouldClearInventory() {
		return false;
	}

	@Override
	public boolean canMoveArmor() {
		return false;
	}

	@Override
	public void onInitialize(@NotNull MatchInitializeEvent event) {
		Match match = event.getMatch();
		ThimbleArena arena = match.getArena();
		arena.setGamemode(arena.getNextGamemode());
		arena.getGamemode().onInitialize(match);
		arena.getGamemode().editPool(match);
	}

	@Override
	public void onJoin(@NotNull MatchJoinEvent event) {
		super.onJoin(event);
		Minigamer minigamer = event.getMinigamer();
		Player player = minigamer.getOnlinePlayer();

		ItemStack menuItem = new ItemBuilder(Material.BLUE_CONCRETE).name("Choose A Block!").build();
		player.getInventory().setItem(0, menuItem);

		minigamer.getMatch().getTasks().wait(30, () -> minigamer.tell("Click a block to select it!"));
	}

	@Override
	public void tellMapAndMechanic(@NotNull Minigamer minigamer) {
		ThimbleArena arena = minigamer.getMatch().getArena();
		minigamer.tell("You are playing &eThimble&3: &e" + arena.getGamemode().getName());
		tellDescriptionAndModifier(minigamer);
	}

	@Override
	public void onQuit(@NotNull MatchQuitEvent event) {
		Minigamer minigamer = event.getMinigamer();
		ThimbleMatchData matchData = minigamer.getMatch().getMatchData();

		matchData.getTurnMinigamerList().remove(minigamer);

		if (minigamer.equals(matchData.getTurnMinigamer()))
			kill(minigamer);

		minigamer.getOnlinePlayer().getInventory().clear();

		super.onQuit(event);
	}

	@Override
	public @NotNull String getScoreboardTitle(@NotNull Match match) {
		ThimbleArena arena = match.getArena();
		String scoreboardTitle = super.getScoreboardTitle(match);

		if (arena.getGamemode() != null)
			scoreboardTitle += ": " + arena.getGamemode().getScoreboardTitle();

		return scoreboardTitle ;
	}

	@Override
	public @NotNull LinkedHashMap<String, Integer> getScoreboardLines(@NotNull Match match) {
		LinkedHashMap<String, Integer> lines = new LinkedHashMap<>();
		ThimbleMatchData matchData = match.getMatchData();

		if (match.isStarted()) {
			lines.put("&1", Integer.MIN_VALUE);
			lines.put("&2Jumping:", Integer.MIN_VALUE);
			if (matchData.getTurnMinigamer() != null) {
				lines.put("&a" + matchData.getTurnMinigamer().getVanillaColoredName(), Integer.MIN_VALUE);
			} else {
				lines.put("&f", Integer.MIN_VALUE);
			}
		}

		if (!match.isStarted()) {
			// Shows players in lobby
			for (Minigamer minigamer : match.getMinigamers())
				lines.put(minigamer.getVanillaColoredName(), Integer.MIN_VALUE);
		} else {
			// Shows players scores
			for (Minigamer minigamer : match.getMinigamers())
				if (minigamer.getScore() >= 1)
					if (minigamer.isAlive())
						lines.put(minigamer.getNickname(), minigamer.getScore());
					else
						lines.put("&c&m" + minigamer.getNickname(), minigamer.getScore());
		}

		return lines;
	}

	@Override
	public void onStart(@NotNull MatchStartEvent event) {
		super.onStart(event);

		Match match = event.getMatch();

		List<Minigamer> minigamers = match.getMinigamers();
		setPlayerBlocks(minigamers, match);

		// Adds time to the game by players * total turn length
		match.getTimer().addTime(minigamers.size() * 17);

		// Teleport all players in minigame to spectate location of current map
		ThimbleArena arena = match.getArena();
		Location spectateLocation = arena.getCurrentMap().getSpectateLocation();
		for (Minigamer minigamer : minigamers) {
			minigamer.teleportAsync(spectateLocation);
			minigamer.clearInventory();
		}

		match.getTasks().wait(60, () -> nextTurn(match));
		match.getTasks().repeat(TickTime.SECOND, TickTime.TICK.x(2), () -> {
			for (Minigamer minigamer : match.getAliveMinigamers())
				checkInWater(minigamer);
		});
	}

	@Override
	public void onEnd(@NotNull MatchEndEvent event) {
		ThimbleArena arena = event.getMatch().getArena();

		event.getMatch().worldedit().set(arena.getRegion("pool"), BlockTypes.WATER);

		super.onEnd(event);
	}

	@Override
	public void onDamage(@NotNull MinigamerDamageEvent event) {
		super.onDamage(event);
		event.setCancelled(true);
		if (event.getMatch().isEnded())
			return;

		ThimbleArena arena = event.getMatch().getArena();
		ThimbleMatchData matchData = event.getMatch().getMatchData();
		if (event.getMinigamer().equals(matchData.getTurnMinigamer()))
			arena.getGamemode().kill(event.getMinigamer());
	}

	@Override
	public void onDeath(@NotNull Minigamer victim) {}

	@Override
	public void onDeath(@NotNull MinigamerDeathEvent event) {
		super.onDeath(event);
		Minigamer minigamer = event.getMinigamer();
		ThimbleMatchData matchData = minigamer.getMatch().getMatchData();
		ThimbleArena arena = minigamer.getMatch().getArena();
		if (minigamer.equals(matchData.getTurnMinigamer())) {
			arena.getGamemode().kill(minigamer);
			minigamer.getMatch().getTasks().wait(30, () -> nextTurn(MatchManager.get(arena)));
			event.setDeathMessage(minigamer.getColoredName() + " missed");
		}
	}

	private void score(Minigamer minigamer, Location blockLocation) {
		ThimbleArena arena = minigamer.getMatch().getArena();
		arena.getGamemode().score(minigamer, blockLocation);

		minigamer.getMatch().getTasks().wait(30, () -> nextTurn(minigamer.getMatch()));
	}

	public void nextRound(Match match) {
		ThimbleMatchData matchData = match.getMatchData();

		if (match.getAliveMinigamers().size() <= 1) {
			match.end();
			return;
		}

		if (matchData.getTurns() >= MAX_TURNS) {
			match.end();
			return;
		}

		if (matchData.isEnding()) {
			match.end();
			return;
		}

		if (match.isEnded())
			return;

		match.broadcast("New Round!");
		matchData.setTurnMinigamerList(new ArrayList<>(match.getAliveMinigamers()));
		Collections.shuffle(matchData.getTurnMinigamerList());
		match.getTasks().wait(30, () -> nextTurn(match));
	}

	public void nextTurn(@NotNull Match match) {
		ThimbleArena arena = match.getArena();
		ThimbleMatchData matchData = match.getMatchData();

		if (match.isEnded() || matchData == null)
			return;

		if (matchData.getTurns() >= MAX_TURNS) {
			match.broadcast("Max turns reached, ending game");
			match.end();
			return;
		}

		if (matchData.getTurnMinigamerList().size() == 0) {
			nextRound(match);
			return;
		}

		Match.MatchTasks tasks = match.getTasks();
		tasks.cancel(MatchTaskType.TURN);

		matchData.setTurnMinigamer(matchData.getTurnMinigamerList().get(0));
		match.getScoreboard().update();

		final Minigamer finalNextMinigamer = matchData.getTurnMinigamer();
		Player player = finalNextMinigamer.getOnlinePlayer();

		finalNextMinigamer.teleportAsync(arena.getCurrentMap().getNextTurnLocation(), true);

		player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.MASTER, 10.0F, 1.0F);
		tasks.wait(3, () -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.MASTER, 10.0F, 1.2F));

		// wait 10 seconds, if the player's y-value is >=  nextTurnLoc y-value, kill them
		int taskId = tasks.wait(10 * 20, () -> {
			if (player.getLocation().getY() >= arena.getCurrentMap().getNextTurnLocation().getY())
				kill(finalNextMinigamer);
			else {
				// wait 5 more seconds, if the turnPlayer is still equal to player, kill them
				int taskId2 = tasks.wait(5 * 20, () -> {
					if (matchData.getTurnMinigamer() != null && matchData.getTurnMinigamer().equals(finalNextMinigamer)) {
						kill(finalNextMinigamer);
					}
				});
				tasks.register(MatchTaskType.TURN, taskId2);
			}
		});
		tasks.register(MatchTaskType.TURN, taskId);
	}

	// Auto-select unique color for players who have not themselves
	private void setPlayerBlocks(List<Minigamer> minigamers, Match match) {
		ThimbleMatchData matchData = match.getMatchData();

		for (Minigamer minigamer : minigamers) {
			Material colorType = matchData.getColor(minigamer);
			if (colorType == null) {
				Material next = matchData.getAvailableColorId();
				matchData.setColor(minigamer, next);
				colorType = next;
			}
			minigamer.getOnlinePlayer().getInventory().setHelmet(new ItemStack(colorType));
		}
	}

	// Select unique color
	@EventHandler
	public void setPlayerBlock(PlayerInteractEvent event) {
		if (event.getItem() == null) return;
		if (!MaterialTag.CONCRETES.isTagged(event.getItem().getType())) return;
		if (!ActionGroup.CLICK_AIR.applies(event)) return;

		Player player = event.getPlayer();
		if (!player.getWorld().equals(Minigames.getWorld())) return;

		Minigamer minigamer = Minigamer.of(player);
		if (!minigamer.isInLobby(this)) return;

		Match match = minigamer.getMatch();
		if (match.isStarted()) return;

		new ColorPickMenu(getCOLOR_CHOICES(), "_CONCRETE").open(event.getPlayer());
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onEnterRegion(PlayerEnteredRegionEvent event) {
		Player player = event.getPlayer();
		Minigamer minigamer = Minigamer.of(player);
		if (!minigamer.isPlaying(this))
			return;

		ThimbleArena arena = minigamer.getMatch().getArena();
		if (event.getRegion().getId().equalsIgnoreCase(arena.getProtectedRegion("pool").getId()))
			checkInWater(minigamer);
	}

	private static void checkInWater(Minigamer minigamer) {
		final Player player = minigamer.getOnlinePlayer();
		final Thimble mechanic = minigamer.getMatch().getMechanic();
		if (!player.isInWater())
			return;

		final ItemStack helmet = player.getInventory().getHelmet();
		if (helmet == null)
			return;

		final ThimbleMatchData matchData = minigamer.getMatch().getMatchData();
		if (!minigamer.isAlive())
			return;
		if (matchData.getTurnMinigamer() == null || !matchData.getTurnMinigamer().equals(minigamer))
			return;
		if (!minigamer.isInRegion("pool"))
			return;

		Location blockLocation = player.getLocation();
		if (!Material.WATER.equals(blockLocation.getBlock().getType())) {
			Location locationBelow = blockLocation.subtract(0, 1, 0);
			if (!Material.WATER.equals(locationBelow.getBlock().getType())) {
				mechanic.kill(minigamer);
				return;
			}
			blockLocation = locationBelow;
		}

		blockLocation.getBlock().setType(helmet.getType());

		new FireworkLauncher(blockLocation.clone().add(0, 2, 0))
				.type(FireworkEffect.Type.BALL)
				.color(ColorType.of(helmet.getType()).getBukkitColor())
				.power(0)
				.detonateAfter(1L)
				.launch();

		minigamer.teleportAsync(((ThimbleArena) minigamer.getMatch().getArena()).getCurrentMap().getSpectateLocation());

		mechanic.score(minigamer, blockLocation);
	}

	@Override
	public boolean shouldBeOver(@NotNull Match match) {
		ThimbleMatchData matchData = match.getMatchData();

		if (matchData.getTurns() >= MAX_TURNS)
			return true;

		return super.shouldBeOver(match);
	}

	@EventHandler
	public void onMatchEnd(MatchEndEvent event) {
		Match match = event.getMatch();
		if (!match.isMechanic(this)) return;

		ThimbleMatchData matchData = match.getMatchData();

		if (matchData == null)
			return;

		if (!matchData.isEnding() && !shouldBeOver(match)) {
			if (match.getTimer().getTime() == 0)
				match.broadcast("Time is up, match will end after this round.");
			matchData.isEnding(true);
			event.setCancelled(true);
		}
	}

	public static abstract class ThimbleGamemode {

		abstract String getName();

		String getScoreboardTitle() {
			return getName();
		}

		void onInitialize(Match match) {
			ThimbleArena arena = match.getArena();

			// Setup next map
			List<ThimbleMap> thimbleMaps = arena.getThimbleMaps();
			ThimbleMap previousMap = new ThimbleMap();
			for (ThimbleMap map : thimbleMaps) {
				// currentMap hasn't been set, so it is still the previous map
				if (map.getName().equalsIgnoreCase(arena.getCurrentMap().getName())) {
					previousMap = map;
					break;
				}
			}

			int ndx = thimbleMaps.indexOf(previousMap);
			if (ndx >= thimbleMaps.size() - 1) {
				ndx = 0;
			} else {
				ndx += 1;
			}
			arena.setCurrentMap(thimbleMaps.get(ndx));

			match.worldedit().getBlocks(arena.getRegion("pool")).forEach(block -> block.setType(Material.WATER));
		}

		// Randomly place blocks in pool
		void editPool(Match match) {
			ThimbleArena arena = match.getArena();
			ThimbleMatchData matchData = match.getMatchData();

			int BLOCKS_TO_CHANGE = 3;
			List<Block> blocks = match.worldguard().getRandomBlocks(arena.getProtectedRegion("pool"), Material.WATER, BLOCKS_TO_CHANGE);
			blocks.forEach(block -> {
				block.setType(Material.PISTON);
				Directional directional = ((Directional) block.getBlockData());
				directional.setFacing(BlockFace.DOWN);
				block.setBlockData(directional);
			});

			matchData.setTurns(blocks.size());
		}

		void score(Minigamer minigamer, Location blockLocation) {
			ThimbleMatchData matchData = minigamer.getMatch().getMatchData();
			matchData.setTurnMinigamer(null);
			minigamer.getMatch().getScoreboard().update();
			matchData.setTurns(matchData.getTurns() + 1);
			matchData.getTurnMinigamerList().remove(minigamer);
		}

		void kill(Minigamer minigamer) {
			ThimbleMatchData matchData = minigamer.getMatch().getMatchData();
			ThimbleArena arena = minigamer.getMatch().getArena();

			matchData.setTurnMinigamer(null);
			minigamer.getMatch().getScoreboard().update();
			matchData.getTurnMinigamerList().remove(minigamer);
			minigamer.teleportAsync(arena.getCurrentMap().getSpectateLocation());
		}
	}

	public static class ClassicGamemode extends ThimbleGamemode {
		@Override
		String getName() {
			return "Classic";
		}

		@Override
		void score(Minigamer minigamer, Location blockLocation) {
			super.score(minigamer, blockLocation);
			minigamer.scored();
		}
	}

	public static class RiskGamemode extends ThimbleGamemode {
		@Override
		String getName() {
			return "Risk";
		}

		@Override
		void score(Minigamer minigamer, Location blockLocation) {
			super.score(minigamer, blockLocation);
			// initial score for landing in water
			int points = 1;
			Block block = blockLocation.getBlock();

			// bonus points for adjacent blocks
			if (!Material.WATER.equals(block.getRelative(1, 0, 0).getType()))
				points++;
			if (!Material.WATER.equals(block.getRelative(0, 0, 1).getType()))
				points++;
			if (!Material.WATER.equals(block.getRelative(-1, 0, 0).getType()))
				points++;
			if (!Material.WATER.equals(block.getRelative(0, 0, -1).getType()))
				points++;

			minigamer.scored(points);
			if (points > 1) {
				minigamer.tell("You recieved " + (points - 1) + plural(" bonus point", points - 1) + "!");
			}
		}

	}

	// when 1 hole is remaining, do not fill, finish the round letting everyone go for that one hole, and those who are still alive by the end, win.
	// let last players finish the round
	public static class LastManStandingGamemode extends ThimbleGamemode {
		@Override
		String getName() {
			return "Last Man Standing";
		}

		@Override
		String getScoreboardTitle() {
			return "LMS";
		}

		// Place x water holes randomly in pool
		@Override
		void editPool(Match match) {
			ThimbleArena arena = match.getArena();
			ThimbleMatchData matchData = match.getMatchData();
			Thimble mechanic = arena.getMechanic();

			match.worldedit().getBlocks(arena.getProtectedRegion("pool")).forEach(block -> {
				block.setType(Material.PISTON);
				Directional directional = ((Directional) block.getBlockData());
				directional.setFacing(BlockFace.DOWN);
				block.setBlockData(directional);
			});

			int playerCount = match.getMinigamers().size();
			int maxPlayers = arena.getMaxPlayers();
			int BLOCKS_TO_CHANGE = ((playerCount * 2) + (maxPlayers - playerCount));

			List<Block> blocks = match.worldguard().getRandomBlocks(arena.getProtectedRegion("pool"), Material.PISTON, BLOCKS_TO_CHANGE);
			blocks.forEach(block -> block.setType(Material.WATER));

			matchData.setTurns(mechanic.getMAX_TURNS() - blocks.size());
		}

		@Override
		void score(Minigamer minigamer, Location blockLocation) {
			super.score(minigamer, blockLocation);
			minigamer.scored();
		}

		@Override
		void kill(Minigamer minigamer) {
			minigamer.setAlive(false);
			super.kill(minigamer);
		}

	}

	@Rows(2)
	@Title("Select Your Color")
	private static class ColorPickMenu extends InventoryProvider {
		private final List<Material> COLOR_CHOICES;
		private final String filter;

		public ColorPickMenu(@NotNull List<Material> choices, String filter) {
			COLOR_CHOICES = choices;
			this.filter = filter;
		}

		@Override
		public void init() {
			Minigamer minigamer = Minigamer.of(viewer);
			Match match = minigamer.getMatch();
			ThimbleMatchData matchData = match.getMatchData();

			int row = 0;
			int col = 0;
			for (Material colorType : COLOR_CHOICES) {
				ItemStack colorItem = new ItemStack(colorType);

				if (!matchData.containsColor(colorType)) {
					if (col > 8) {
						++row;
						col = 0;
					}

					contents.set(new SlotPos(row, col++), ClickableItem.of(colorItem, e -> pickColor(colorItem, viewer)));
				}
			}
		}

		public void pickColor(ItemStack colorItem, Player player) {
			Minigamer minigamer = Minigamer.of(player);
			Match match = minigamer.getMatch();
			ThimbleMatchData matchData = match.getMatchData();

			if (matchData.containsColor(colorItem.getType())) {
				minigamer.tell("&cThis color has already been chosen.");
				init();
				return;
			}

			matchData.removeColor(minigamer);

			player.getInventory().setHelmet(colorItem);
			player.getInventory().setItem(0, colorItem);
			matchData.setColor(minigamer, colorItem.getType());

			minigamer.tell("You chose " + camelCase(colorItem.getType().name().replace(filter, "")) + "!");

			player.closeInventory();
		}

	}

}

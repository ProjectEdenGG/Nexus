package me.pugabyte.bncore.features.minigames.mechanics;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import lombok.Getter;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.MatchManager;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.arenas.ThimbleArena;
import me.pugabyte.bncore.features.minigames.models.arenas.ThimbleMap;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchInitializeEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchJoinEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchQuitEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.bncore.features.minigames.models.matchdata.ThimbleMatchData;
import me.pugabyte.bncore.features.minigames.models.mechanics.MechanicType;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.FireworkLauncher;
import me.pugabyte.bncore.utils.ItemStackBuilder;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO: Show turn timer to turn player
//TODO: Gamemode description messages onm lobby join

public final class Thimble extends TeamlessMechanic {

	@Getter
	private final short[] CONCRETE_IDS = {14, 1, 4, 5, 13, 10, 2, 6, 12, 15, 7, 8, 0};
	@Getter
	private final int MAX_TURNS = 49;

	@Override
	public String getName() {
		return "Thimble";
	}

	@Override
	public String getDescription() {
		return "TODO";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.CHAINMAIL_HELMET);
	}

	@Override
	public boolean shouldClearInventory() {
		return false;
	}

	@Override
	public void onInitialize(MatchInitializeEvent event) {
		Match match = event.getMatch();
		ThimbleArena arena = match.getArena();
		arena.setGamemode(arena.getNextGamemode());
		arena.getGamemode().onInitialize(match);
		arena.getGamemode().editPool(match);
	}

	@Override
	public void onJoin(MatchJoinEvent event) {
		super.onJoin(event);
		Minigamer minigamer = event.getMinigamer();
		Player player = minigamer.getPlayer();
		ItemStack menuItem = new ItemStackBuilder(Material.CONCRETE).durability((short) 11).name("Choose A Block!").build();
		player.getInventory().setItem(0, menuItem);
		minigamer.getMatch().getTasks().wait(30, () -> minigamer.tell("Click a block to select it!"));
	}

	@Override
	public void tellMapAndMechanic(Minigamer minigamer) {
		ThimbleArena arena = minigamer.getMatch().getArena();
		minigamer.tell("You are playing &eThimble&3: &e" + arena.getGamemode().getName());
	}

	@Override
	public void onQuit(MatchQuitEvent event) {
		Minigamer minigamer = event.getMinigamer();
		ThimbleMatchData matchData = minigamer.getMatch().getMatchData();
		matchData.getTurnList().remove(minigamer);
		if (minigamer.equals(matchData.getTurnPlayer()))
			kill(minigamer);
		super.onQuit(event);
	}

	@Override
	public String getScoreboardTitle(Match match) {
		ThimbleArena arena = match.getArena();
		return super.getScoreboardTitle(match) + ": " + arena.getGamemode().getScoreboardTitle();
	}

	@Override
	public Map<String, Integer> getScoreboardLines(Match match) {
		Map<String, Integer> lines = new HashMap<>();
		ThimbleMatchData matchData = match.getMatchData();

		if (match.isStarted()) {
			lines.put("&1", 0);
			lines.put("&2Jumping:", 0);
			if (matchData.getTurnPlayer() != null) {
				lines.put("&a" + matchData.getTurnPlayer().getColoredName(), 0);
			} else {
				lines.put("&f", 0);
			}
		}

		if (!match.isStarted()) {
			// Shows players in lobby
			for (Minigamer minigamer : match.getMinigamers())
				lines.put(minigamer.getColoredName(), 0);
		} else {
			// Shows players scores
			for (Minigamer minigamer : match.getMinigamers())
				if (minigamer.getScore() >= 1)
					if (minigamer.isAlive())
						lines.put(minigamer.getName(), minigamer.getScore());
					else
						lines.put("&c&m" + minigamer.getName(), minigamer.getScore());
		}

		return lines;
	}

	@Override
	public void onStart(MatchStartEvent event) {
		super.onStart(event);

		Match match = event.getMatch();
		ThimbleArena arena = match.getArena();

		List<Minigamer> minigamers = match.getMinigamers();
		setPlayerBlocks(minigamers, match);

		// Adds time to the game by players * total turn length
		match.getTimer().addTime(minigamers.size() * 17);

		// Teleport all players in minigame to spectate location of current map
		Location specLoc = arena.getCurrentMap().getSpectateLocation();
		for (Minigamer minigamer : minigamers) {
			minigamer.teleport(specLoc);
			minigamer.getPlayer().getInventory().setStorageContents(new ItemStack[36]);
		}

		match.getTasks().wait(60, () -> nextTurn(match));
	}

	@Override
	public void onEnd(MatchEndEvent event) {
		ThimbleArena arena = event.getMatch().getArena();
		Minigames.getWorldEditUtils().fill(arena.getRegion("pool"), Material.WATER);
		super.onEnd(event);
	}

	@Override
	public void onDamage(Minigamer victim, EntityDamageEvent event) {
		super.onDamage(victim, event);
		ThimbleMatchData matchData = victim.getMatch().getMatchData();
		if (victim.equals(matchData.getTurnPlayer()))
			kill(victim);
	}

	@Override
	public void onDeath(MinigamerDeathEvent event) {
		super.onDeath(event);
		ThimbleMatchData matchData = event.getMinigamer().getMatch().getMatchData();
		if (event.getMinigamer().equals(matchData.getTurnPlayer()))
			event.setDeathMessage(event.getMinigamer().getColoredName() + " missed");
	}

	@Override
	public void kill(Minigamer minigamer) {
		super.kill(minigamer);
		ThimbleMatchData matchData = minigamer.getMatch().getMatchData();
		ThimbleArena arena = minigamer.getMatch().getArena();
		if (minigamer.equals(matchData.getTurnPlayer())) {
			arena.getGamemode().kill(minigamer);
			minigamer.getMatch().getTasks().wait(30, () -> nextTurn(MatchManager.get(arena)));
		}
	}

	private void score(Minigamer minigamer, Location blockLocation) {
		ThimbleArena arena = minigamer.getMatch().getArena();
		arena.getGamemode().score(minigamer, blockLocation);

		minigamer.getMatch().getTasks().wait(30, () -> nextTurn(minigamer.getMatch()));
	}

	private void newRound(Match match) {
		ThimbleMatchData matchData = match.getMatchData();

		if (match.getAlivePlayers().size() <= 1) {
			match.broadcast("Alive Players <= 1, Ending game.");
			match.end();
			return;
		}

		if (matchData.getTurns() >= MAX_TURNS) {
			match.broadcast("Max turns reached, Ending game. (newround)");
			match.end();
			return;
		}

		if (matchData.isEnding()) {
			match.broadcast("Match End.");
			match.end();
			return;
		}

		if (match.isEnded())
			return;

		match.broadcast("New Round!");
		matchData.setTurnList(new ArrayList<>(match.getAlivePlayers()));
		Collections.shuffle(matchData.getTurnList());
		match.getTasks().wait(30, () -> nextTurn(match));
	}

	private void nextTurn(Match match) {
		ThimbleArena arena = match.getArena();
		ThimbleMatchData matchData = match.getMatchData();

		if (match.isEnded() || matchData == null)
			return;

		if (matchData.getTurns() >= MAX_TURNS) {
			match.broadcast("Max turns reached, Ending game. (nextturn)");
			match.end();
			return;
		}

		if (matchData.getTurnList().size() == 0) {
			newRound(match);
			return;
		}

		Match.MatchTasks tasks = match.getTasks();
		tasks.cancel(matchData.getTurnWaitTaskId());

		matchData.setTurnPlayer(matchData.getTurnList().get(0));
		match.getScoreboard().update();

		final Minigamer finalNextMinigamer = matchData.getTurnPlayer();
		Player player = finalNextMinigamer.getPlayer();

		finalNextMinigamer.teleport(arena.getCurrentMap().getNextTurnLocation(), true);

		player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, SoundCategory.MASTER, 10.0F, 1.0F);
		tasks.wait(3, () -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, SoundCategory.MASTER, 10.0F, 1.2F));

		// wait 10 seconds, if the player's y-value is >=  nextTurnLoc y-value, kill them
		int taskId = tasks.wait(10 * 20, () -> {
			if (player.getLocation().getY() >= arena.getCurrentMap().getNextTurnLocation().getY())
				kill(finalNextMinigamer);
			else {
				// wait 5 more seconds, if the turnPlayer is still equal to player, kill them
				int taskId2 = tasks.wait(5 * 20, () -> {
					if (matchData.getTurnPlayer() != null && matchData.getTurnPlayer().equals(finalNextMinigamer)) {
						kill(finalNextMinigamer);
					}
				});
				matchData.setTurnWaitTaskId(taskId2);
			}
		});
		matchData.setTurnWaitTaskId(taskId);
	}

	// Auto-select unique concrete blocks for players who have not themselves
	private void setPlayerBlocks(List<Minigamer> minigamers, Match match) {
		ThimbleMatchData matchData = match.getMatchData();

		for (Minigamer minigamer : minigamers) {
			Short concreteId = matchData.getChosenConcrete().get(minigamer.getPlayer());
			if (concreteId == null) {
				Short next = matchData.getAvailableConcreteId();
				matchData.getChosenConcrete().put(minigamer.getPlayer(), next);
				concreteId = next;
			}
			minigamer.getPlayer().getInventory().setHelmet(new ItemStack(Material.CONCRETE, 1, concreteId));
		}
	}

	// Select unique concrete blocks
	@EventHandler
	public void setPlayerBlock(PlayerInteractEvent event) {
		if (event.getItem() == null) return;
		if (!event.getItem().getType().equals(Material.CONCRETE)) return;
		if (!(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)))
			return;

		Player player = event.getPlayer();
		if (!player.getWorld().equals(Minigames.getGameworld())) return;

		Minigamer minigamer = PlayerManager.get(player);
		if (!minigamer.isInLobby(this)) return;

		Match match = minigamer.getMatch();
		if (match.isStarted()) return;

		SmartInventory.builder()
				.provider(new ThimbleMenu())
				.title("Select Your Concrete Block")
				.size(2, 9)
				.build()
				.open(event.getPlayer());
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onEnterRegion(RegionEnteredEvent event) {
		Player player = event.getPlayer();
		Minigamer minigamer = PlayerManager.get(player);
		if (!minigamer.isPlaying(this)) return;

		ThimbleArena arena = minigamer.getMatch().getArena();
		if (event.getRegion().getId().equalsIgnoreCase(arena.getProtectedRegion("pool").getId())) {
			if (!Utils.isInWater(player)) return;
			if (player.getInventory().getHelmet() == null) return;

			ThimbleMatchData matchData = minigamer.getMatch().getMatchData();
			if (!minigamer.isAlive()) return;
			if (matchData.getTurnPlayer() == null || !matchData.getTurnPlayer().equals(minigamer)) return;

			Location blockLocation = player.getLocation();
			if (!Utils.isWater(blockLocation.getBlock().getType())) {
				Location locationBelow = blockLocation.subtract(0.0, 1.0, 0.0);
				if (!Utils.isWater(locationBelow.getBlock().getType())) {
					kill(minigamer);
					return;
				}
				blockLocation = locationBelow;
			}

			short durability = player.getInventory().getHelmet().clone().getDurability();

			blockLocation.getBlock().setType(Material.CONCRETE);
			blockLocation.getBlock().setData(Byte.parseByte(Short.toString(durability)));

			Color color = ColorType.fromDurability(durability).getColor();
			Location fireworkLocation = blockLocation.clone().add(0.0, 2.0, 0.0);

			new FireworkLauncher(fireworkLocation)
					.type(FireworkEffect.Type.BALL)
					.colors(Collections.singletonList(color))
					.power(0)
					.detonateAfter(1)
					.launch();

			minigamer.teleport(((ThimbleArena) minigamer.getMatch().getArena()).getCurrentMap().getSpectateLocation());

			score(minigamer, blockLocation);
		}
	}

	@EventHandler
	public void onMatchEnd(MatchEndEvent event) {
		Match match = event.getMatch();
		if (!match.isMechanic(this)) return;

		ThimbleMatchData matchData = match.getMatchData();

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

			Minigames.getWorldEditUtils().fill(arena.getRegion("pool"), Material.STATIONARY_WATER);
		}

		// Randomly place blocks in pool
		void editPool(Match match) {
			ThimbleArena arena = match.getArena();
			ThimbleMatchData matchData = match.getMatchData();

			int BLOCKS_TO_CHANGE = 3;
			List<Block> blocks = Minigames.getWorldGuardUtils().getRandomBlocks(arena.getProtectedRegion("pool"), Material.STATIONARY_WATER, BLOCKS_TO_CHANGE);
			blocks.forEach(block -> {
				block.setType(Material.PISTON_BASE);
				block.setData((byte) 0);
			});

			matchData.setTurns(blocks.size());
		}

		void score(Minigamer minigamer, Location blockLocation) {
			ThimbleMatchData matchData = minigamer.getMatch().getMatchData();
			matchData.setTurnPlayer(null);
			minigamer.getMatch().getScoreboard().update();
			matchData.setTurns(matchData.getTurns() + 1);
			matchData.getTurnList().remove(minigamer);
		}

		void kill(Minigamer minigamer) {
			ThimbleMatchData matchData = minigamer.getMatch().getMatchData();
			ThimbleArena arena = minigamer.getMatch().getArena();

			matchData.setTurnPlayer(null);
			minigamer.getMatch().getScoreboard().update();
			matchData.getTurnList().remove(minigamer);
			minigamer.teleport(arena.getCurrentMap().getSpectateLocation());
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
			if (!Utils.isWater(block.getRelative(1, 0, 0).getType()))
				points++;
			if (!Utils.isWater(block.getRelative(0, 0, 1).getType()))
				points++;
			if (!Utils.isWater(block.getRelative(-1, 0, 0).getType()))
				points++;
			if (!Utils.isWater(block.getRelative(0, 0, -1).getType()))
				points++;

			minigamer.scored(points);
			if (points > 1) {
				minigamer.tell("You recieved " + (points - 1) + " bonus points!");
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

		@Override
		void onInitialize(Match match) {
			ThimbleArena arena = match.getArena();
			super.onInitialize(match);

			Minigames.getWorldEditUtils().fill(arena.getRegion("pool"), Material.PISTON_BASE);
		}

		// Place x water holes randomly in pool
		@Override
		void editPool(Match match) {
			ThimbleArena arena = match.getArena();
			ThimbleMatchData matchData = match.getMatchData();
			Thimble mechanic = (Thimble) arena.getMechanic();

			int playerCount = match.getMinigamers().size();
			int maxPlayers = arena.getMaxPlayers();
			int BLOCKS_TO_CHANGE = ((playerCount * 2) + (maxPlayers - playerCount));

			List<Block> blocks = Minigames.getWorldGuardUtils().getRandomBlocks(arena.getProtectedRegion("pool"), Material.PISTON_BASE, BLOCKS_TO_CHANGE);
			blocks.forEach(block -> block.setType(Material.STATIONARY_WATER));

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

	class ThimbleMenu extends MenuUtils implements InventoryProvider {
		final short[] CONCRETE_IDS = ((Thimble) MechanicType.THIMBLE.get()).getCONCRETE_IDS();

		@Override
		public void init(Player player, InventoryContents contents) {
			Minigamer minigamer = PlayerManager.get(player);
			Match match = minigamer.getMatch();
			ThimbleMatchData matchData = match.getMatchData();

			int row = 0;
			int col = 0;
			for (short concrete_id : CONCRETE_IDS) {
				ItemStack concrete = new ItemStack(Material.CONCRETE, 1, concrete_id);

				if (!matchData.concreteIsChosen(concrete.getDurability())) {
					if (col > 8) {
						++row;
						col = 0;
					}

					contents.set(new SlotPos(row, col++), ClickableItem.from(concrete, e -> pickColor(concrete, player)));
				}
			}
		}

		public void pickColor(ItemStack concrete, Player player) {
			Minigamer minigamer = PlayerManager.get(player);
			Match match = minigamer.getMatch();
			ThimbleMatchData matchData = match.getMatchData();

			matchData.getChosenConcrete().remove(minigamer.getPlayer());

			player.getInventory().setHelmet(concrete);
			player.getInventory().setItem(0, concrete);
			matchData.getChosenConcrete().put(minigamer.getPlayer(), concrete.getDurability());

			String chosenColor = ColorType.fromDurability(concrete.getDurability()).getName();
			minigamer.tell("You chose " + chosenColor + "!");

			player.closeInventory();
		}


		@Override
		public void update(Player player, InventoryContents inventoryContents) {
		}
	}

}

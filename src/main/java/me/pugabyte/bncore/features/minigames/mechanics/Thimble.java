package me.pugabyte.bncore.features.minigames.mechanics;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.util.EditSessionBuilder;
import com.google.common.primitives.Shorts;
import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.MatchManager;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.arenas.ThimbleArena;
import me.pugabyte.bncore.features.minigames.models.arenas.ThimbleMap;
import me.pugabyte.bncore.features.minigames.models.matchdata.ThimbleMatchData;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import me.pugabyte.bncore.utils.FireworkLauncher;
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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

//TODO:
//	- Scoreboards
// 	- GUI for picking block
//	- Finish LMS gamemode: setup pool on start
//	- Give players slowness when they are picked after they are teleported so they don't accidently fall off (10 ticks)
//	- timeleft < ((total players * 17)*2) --> at the end of the round, broadcast Last Round, and no matter how much time is left, end the game
public final class Thimble extends TeamlessMechanic {

	private final short CONCRETE_IDS[] = {14,1,4,5,13,10,2,6,12,15,7,8,0};
	private final int MAX_TURNS = 49;

	@Override
	public String getName() {
		return "Thimble";
	}

	@Override
	public String getDescription() {
		return "Description here.";
	}

	@Override
	public void onQuit(Minigamer minigamer) {
		ThimbleMatchData matchData = (ThimbleMatchData) minigamer.getMatch().getMatchData();
		matchData.getAlivePlayers().remove(minigamer);
		if(minigamer.equals(matchData.getTurnPlayer())){
			onDeath(minigamer);
		}
		super.onQuit(minigamer);
	}

	@Override
	public void onJoin(Minigamer minigamer) {
		minigamer.getMatch().broadcast("&e" + minigamer.getPlayer().getName() + " &3has joined");
		ThimbleArena arena = (ThimbleArena) minigamer.getMatch().getArena();
		minigamer.tell("You are playing Thimble: " + arena.getGamemode().getName());
		Player player = minigamer.getPlayer();
		ItemStack item = new ItemStack(Material.CONCRETE, 1);
		for(int i = 0; i < 9; i++){
			item.setDurability(CONCRETE_IDS[i]);
			player.getInventory().setItem(i, item);
		}

		minigamer.getMatch().getTasks().wait(30, () -> minigamer.tell("Click a block to select it!"));
	}

	@Override
	public void onInitialize(Match match) {
		ThimbleArena arena = (ThimbleArena) match.getArena();

		// Select next gamemode
		arena.setGamemode(arena.getNextGamemode());

		match.setMatchData(new ThimbleMatchData(match));

		arena.getGamemode().onInitialize(match);
	}

	@Override
	public void onStart(Match match) {
		ThimbleMatchData matchData = (ThimbleMatchData) match.getMatchData();
		ThimbleArena arena = (ThimbleArena) match.getArena();

		List<Minigamer> minigamers = match.getMinigamers();
		setPlayerBlocks(minigamers, match);
		matchData.getAlivePlayers().addAll(minigamers);
		Collections.shuffle(matchData.getAlivePlayers());
		matchData.setTurns(0);

		// add x amount of seconds to the game, Each turn is 15, +2 for extra waits
		// adds the time for the max length of a round by how many players there are
		//arena.setSeconds(arena.getSeconds() + (minigamers.size()*17));

		// Teleport all players in minigame to spectate location of current map
		Location specLoc = arena.getCurrentMap().getSpectateLocation();
		for (Minigamer minigamer: minigamers) {
			minigamer.teleport(specLoc);
		}

		match.getTasks().wait(60, () -> nextTurn(match));
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onEnd(Match match) {
		super.onEnd(match);

		ThimbleArena arena = (ThimbleArena) match.getArena();
		ThimbleMatchData matchData = (ThimbleMatchData) match.getMatchData();

		// Reset Pool
		World world = FaweAPI.getWorld((Minigames.getGameworld().getName()));
		EditSession editSession = new EditSessionBuilder(world).fastmode(true).build();
		RegionManager regionManager = WGBukkit.getRegionManager(Minigames.getGameworld());
		if(regionManager.getRegion(arena.getPoolRegionStr()) != null) {
			Vector max = regionManager.getRegion(arena.getPoolRegionStr()).getMaximumPoint();
			Vector min = regionManager.getRegion(arena.getPoolRegionStr()).getMinimumPoint();
			Region poolRegion = new CuboidRegion(max, min);
			BaseBlock baseBlock = new BaseBlock(9, 0);

			editSession.setBlocks(poolRegion, baseBlock);
			editSession.flushQueue();
		}
	}

	@Override
	public void onDeath(Minigamer victim) {
		ThimbleMatchData matchData = (ThimbleMatchData) victim.getMatch().getMatchData();
		ThimbleArena arena = (ThimbleArena) victim.getMatch().getArena();
		if(matchData.getTurnPlayer() != null && matchData.getTurnPlayer().equals(victim)) {
			arena.getGamemode().onDeath(victim);
			victim.getMatch().getTasks().wait(30, () -> nextTurn(MatchManager.get(arena)));
		}
	}

	@Override
	public void kill(Minigamer minigamer) {
		//
	}

	private void score(Minigamer minigamer, Location blockLocation){
		ThimbleArena arena = (ThimbleArena) minigamer.getMatch().getArena();
		arena.getGamemode().score(minigamer, blockLocation);

		minigamer.getMatch().getTasks().wait(30, () -> nextTurn(minigamer.getMatch()));
	}

	private void newRound(Match match) {
		ThimbleArena arena = (ThimbleArena) match.getArena();
		ThimbleMatchData matchData = (ThimbleMatchData) match.getMatchData();

		if (matchData.getAlivePlayers().size() <= 1) {
			match.broadcast("Alive Players <= 1, Ending game.");
			match.end();
			return;
		}

		if(matchData.getTurns() >= MAX_TURNS){
			match.broadcast("Max turns reached, Ending game. (newround)");
			match.end();
			return;
		}

//		if(arena.getSeconds() < (match.getMinigamers().size() * 17)){
//			match.broadcast("Time left < " + (match.getMinigamers().size() * 17) + " (newround)");
//			match.end();
//			return;
//		}

		if(match.isEnded()) {
			return;
		}

		match.broadcast("New Round!");
		matchData.getTurnList().clear();
		Collections.shuffle(matchData.getAlivePlayers());
		match.getTasks().wait(30, () -> nextTurn(match));
	}

	private void nextTurn(Match match){
		ThimbleMatchData matchData = (ThimbleMatchData) match.getMatchData();
		ThimbleArena arena = (ThimbleArena) match.getArena();

		if(match.isEnded())
			return;

		if(matchData.getTurns() >= MAX_TURNS) {
			match.broadcast("Max turns reached, Ending game. (nextturn)");
			match.end();
			return;
		}

		if(matchData.getTurnList().size() == matchData.getAlivePlayers().size()) {
			newRound(match);
			return;
		}

		Minigamer nextMinigamer = null;
		for (Minigamer minigamer:  matchData.getAlivePlayers()) {
			if(!matchData.getTurnList().contains(minigamer)){
				nextMinigamer = minigamer;
				break;
			}
		}

		// this should never happen, maybe throw an error instead?
		if(nextMinigamer == null) {
			match.broadcast("nextMinigamer = null, Ending game.");
			match.end();
			return;
		}

		Match.MatchTasks tasks = match.getTasks();
		tasks.cancel(matchData.getTurnWaitTaskId());
		matchData.setTurnPlayer(nextMinigamer);

		Minigamer finalNextMinigamer = nextMinigamer;
		Player player = finalNextMinigamer.getPlayer();

		finalNextMinigamer.teleport(arena.getCurrentMap().getNextTurnLocation());

		player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, SoundCategory.MASTER, 10.0F, 1.0F);
		tasks.wait(3, () -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, SoundCategory.MASTER, 10.0F, 1.2F));

		// wait 10 seconds, if the player's y-value is >=  nextTurnLoc y-value, kill them
		int taskId = tasks.wait(10 * 20, () -> {
			if(player.getLocation().getY() >= arena.getCurrentMap().getNextTurnLocation().getY())
				onDeath(finalNextMinigamer);
			else {
				// wait 5 more seconds, if the turnPlayer is still equal to player, kill them
				int taskId2 = tasks.wait(5 * 20, () -> {
					if (matchData.getTurnPlayer() != null && matchData.getTurnPlayer().equals(finalNextMinigamer)) {
						onDeath(finalNextMinigamer);
					}
				});
				matchData.setTurnWaitTaskId(taskId2);
			}
		});
		matchData.setTurnWaitTaskId(taskId);
	}

	// Auto-select unique concrete blocks for players who have not themselves
	private void setPlayerBlocks(List<Minigamer> minigamers, Match match){
		ThimbleMatchData matchData = (ThimbleMatchData) match.getMatchData();
		for (Minigamer minigamer: minigamers) {
			Player player = minigamer.getPlayer();
			ItemStack helmetItem = player.getInventory().getHelmet();
			player.getInventory().clear();
			if(Utils.isNullOrAir(helmetItem)) {
				Optional<Short> first = Shorts.asList(CONCRETE_IDS).stream().filter(id -> !matchData.getChosenConcrete().contains(id)).findFirst();
				if (first.isPresent()) {
					ItemStack concrete = new ItemStack(Material.CONCRETE, 1);
					concrete.setDurability(first.get());
					matchData.getChosenConcrete().add(first.get());
					helmetItem = concrete;
				}
			}
			player.getInventory().setHelmet(helmetItem);
		}
	}

	// Select unique concrete blocks
	@EventHandler
	public void setPlayerBlock(PlayerInteractEvent event){
		Player player = event.getPlayer();
		if(!player.getWorld().equals(Minigames.getGameworld()))
			return;

		Minigamer minigamer = PlayerManager.get(player);
		if(!minigamer.isInLobby(this))
			return;

		Match match = minigamer.getMatch();
		ThimbleMatchData matchData = (ThimbleMatchData) match.getMatchData();

		if (!match.isStarted()) {
			PlayerInventory playerInv = player.getInventory();
			ItemStack heldItem = playerInv.getItemInMainHand();
			if(Utils.isNullOrAir(heldItem)) {
				heldItem = playerInv.getItemInOffHand();
			}

			if (heldItem != null && heldItem.getType().equals(Material.CONCRETE)) {
				short itemDurability = heldItem.getDurability();
				// Test if selected concrete is already chosen
				if(!matchData.getChosenConcrete().contains(itemDurability)){
					// Remove item on head from chosenIDs
					if (playerInv.getHelmet() != null && playerInv.getHelmet().getType().equals(Material.CONCRETE)) {
						Short helmetDurability = playerInv.getHelmet().getDurability();
						matchData.getChosenConcrete().remove(helmetDurability);
					}
					// Add new item on head to chosenIDs
					playerInv.setHelmet(heldItem);
					matchData.getChosenConcrete().add(itemDurability);
					minigamer.tell("You chose " + Utils.getColor((int)itemDurability) + "!");
				}else{
					minigamer.tell("&cThat block is already chosen!");
				}

			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onEnterRegion(RegionEnteredEvent event){
		Player player = event.getPlayer();
		Minigamer minigamer = PlayerManager.get(player);
		if(!minigamer.isPlaying(this)) return;

		ThimbleArena arena = (ThimbleArena) minigamer.getMatch().getArena();
		if(event.getRegion().getId().equals(arena.getPoolRegionStr())) {
			if(!Utils.isInWater(player)) return;
			if(player.getInventory().getHelmet() == null) return;

			ThimbleMatchData matchData = (ThimbleMatchData) minigamer.getMatch().getMatchData();
			if(!matchData.getAlivePlayers().contains(minigamer)) return;
			if(matchData.getTurnPlayer() == null || !matchData.getTurnPlayer().equals(minigamer)) return;

			Location blockLocation = player.getLocation();
			if(!Utils.isWater(blockLocation.getBlock().getType())){
				Location locationBelow = blockLocation.subtract(0.0,1.0,0.0);
				if(!Utils.isWater(locationBelow.getBlock().getType())){
					onDeath(minigamer);
					return;
				}
				blockLocation = locationBelow;
			}

			ItemStack item = player.getInventory().getHelmet();
			short durability = item.getDurability();

			blockLocation.getBlock().setType(Material.CONCRETE);
			blockLocation.getBlock().setData(Byte.parseByte(Short.toString(durability)));

			Color color = Utils.getColor(Utils.getColor((int) durability));
			Location fireworkLocation = blockLocation.clone().add(0.0,2.0,0.0);

			new FireworkLauncher(fireworkLocation)
					.type(FireworkEffect.Type.BALL)
					.color(color)
					.power(0)
					.detonateAfter(1)
					.launch();

			minigamer.teleport(((ThimbleArena) minigamer.getMatch().getArena()).getCurrentMap().getSpectateLocation());

			score(minigamer, blockLocation);
		}
	}

	public static abstract class ThimbleGamemode {
		void onInitialize(Match match){
			ThimbleArena arena = (ThimbleArena) match.getArena();

			// Setup next map
			List<ThimbleMap> thimbleMaps = arena.getThimbleMaps();
			ThimbleMap previousMap = new ThimbleMap();
			for (ThimbleMap map: thimbleMaps) {
				// currentMap hasn't been set, so it is still the previous map
				if(map.getName().equalsIgnoreCase(arena.getCurrentMap().getName())){
					previousMap = map;
					break;
				}
			}

			int ndx = thimbleMaps.indexOf(previousMap);
			if(ndx >= thimbleMaps.size()-1){
				ndx = 0;
			}else{
				ndx += 1;
			}
			arena.setCurrentMap(thimbleMaps.get(ndx));

			// Setup next pool region string
			arena.setPoolRegionStr("thimble_" + arena.getCurrentMap().getName().toLowerCase() + "_pool");
		}

		void editPool(Match match){
			ThimbleArena arena = (ThimbleArena) match.getArena();
			RegionManager regionManager = WGBukkit.getRegionManager(Minigames.getGameworld());
			ProtectedCuboidRegion editRegion = (ProtectedCuboidRegion) regionManager.getRegion(arena.getPoolRegionStr());

			int BLOCKS_TO_CHANGE = 3;
			int ATTEMPTS = 3;
			for (int i = 0; i < BLOCKS_TO_CHANGE; i++) {
				for (int j = 0; j < ATTEMPTS; j++) {
					int x = editRegion.getMinimumPoint().getBlockX();
					int y = editRegion.getMinimumPoint().getBlockY();
					int z = editRegion.getMinimumPoint().getBlockZ();

					x += Utils.randomInt(0, 6);
					z += Utils.randomInt(0, 6);
					Block block = Minigames.getGameworld().getBlockAt(x, y, z);

					if (!Utils.isWater(block.getType()))
						continue;

					block.setType(Material.PISTON_BASE);
					block.setData((byte) 0);
					break;
				}
			}
		}

		void score(Minigamer minigamer, Location blockLocation) {
			ThimbleMatchData matchData = (ThimbleMatchData) minigamer.getMatch().getMatchData();
			matchData.setTurnPlayer(null);
			matchData.setTurns(matchData.getTurns()+1);
			matchData.getTurnList().add(minigamer);
		}

		void onDeath(Minigamer minigamer) {
			ThimbleMatchData matchData = (ThimbleMatchData) minigamer.getMatch().getMatchData();
			ThimbleArena arena = (ThimbleArena) minigamer.getMatch().getArena();

			matchData.setTurnPlayer(null);
			matchData.getTurnList().add(minigamer);
			minigamer.teleport(arena.getCurrentMap().getSpectateLocation());
			Match match = minigamer.getMatch();
			match.broadcast(minigamer.getColoredName() + " missed.");
		}

		abstract String getName();
	}

	public static class ClassicGamemode extends ThimbleGamemode {
		@Override
		void onInitialize(Match match) {
			super.onInitialize(match);
			editPool(match);
		}

		@Override
		void score(Minigamer minigamer, Location blockLocation) {
			super.score(minigamer, blockLocation);
			minigamer.scored();
		}

		@Override
		String getName() {
			return "Classic";
		}
	}

	public static class RiskGamemode extends ThimbleGamemode {
		@Override
		void onInitialize(Match match) {
			super.onInitialize(match);
			editPool(match);
		}

		@Override
		void score(Minigamer minigamer, Location blockLocation) {
			super.score(minigamer, blockLocation);
			// initial score for landing in water
			minigamer.scored();
			int bonus = 0;
			Block block = blockLocation.getBlock();

			// bonus points for adjacent blocks
			if(!Utils.isWater(block.getRelative(1,0,0).getType()))
				bonus++;
			if(!Utils.isWater(block.getRelative(0,0,1).getType()))
				bonus++;
			if(!Utils.isWater(block.getRelative(-1,0,0).getType()))
				bonus++;
			if(!Utils.isWater(block.getRelative(0,0,-1).getType()))
				bonus++;

			if(bonus > 0) {
				minigamer.tell("You recieved " + bonus + " bonus points!");
				minigamer.scored(bonus);
			}
		}

		@Override
		String getName() {
			return "Risk";
		}
	}

	public static class LastManStandingGamemode extends ThimbleGamemode {
		@Override
		void onInitialize(Match match){
			ThimbleArena arena = (ThimbleArena) match.getArena();

			// Fill Pool
			World world = FaweAPI.getWorld((Minigames.getGameworld().getName()));
			EditSession editSession = new EditSessionBuilder(world).fastmode(true).build();
			RegionManager regionManager = WGBukkit.getRegionManager(Minigames.getGameworld());
			if(regionManager.getRegion(arena.getPoolRegionStr()) != null) {
				Vector max = regionManager.getRegion(arena.getPoolRegionStr()).getMaximumPoint();
				Vector min = regionManager.getRegion(arena.getPoolRegionStr()).getMinimumPoint();
				Region poolRegion = new CuboidRegion(max, min);
				BaseBlock baseBlock = new BaseBlock(33, 0);

				editSession.setBlocks(poolRegion, baseBlock);
				editSession.flushQueue();
			}

			super.onInitialize(match);
			editPool(match);
		}

		@Override
		void editPool(Match match) {
			ThimbleArena arena = (ThimbleArena) match.getArena();
			RegionManager regionManager = WGBukkit.getRegionManager(Minigames.getGameworld());
			ProtectedCuboidRegion editRegion = (ProtectedCuboidRegion) regionManager.getRegion(arena.getPoolRegionStr());

			int BLOCKS_TO_CHANGE = 5;
			int ATTEMPTS = 3;
			for (int i = 0; i < BLOCKS_TO_CHANGE; i++) {
				for (int j = 0; j < ATTEMPTS; j++) {
					int x = editRegion.getMinimumPoint().getBlockX();
					int y = editRegion.getMinimumPoint().getBlockY();
					int z = editRegion.getMinimumPoint().getBlockZ();

					x += Utils.randomInt(0, 6);
					z += Utils.randomInt(0, 6);
					Block block = Minigames.getGameworld().getBlockAt(x, y, z);

					if (Utils.isWater(block.getType()))
						continue;

					block.setType(Material.STATIONARY_WATER);
					break;
				}
			}
		}

		@Override
		void score(Minigamer minigamer, Location blockLocation) {
			super.score(minigamer, blockLocation);
			minigamer.scored();
		}

		@Override
		void onDeath(Minigamer minigamer) {
			super.onDeath(minigamer);
			ThimbleMatchData matchData = (ThimbleMatchData) minigamer.getMatch().getMatchData();
			matchData.getAlivePlayers().remove(minigamer);
		}

		@Override
		String getName() {
			return "Last Man Standing";
		}

	}
}

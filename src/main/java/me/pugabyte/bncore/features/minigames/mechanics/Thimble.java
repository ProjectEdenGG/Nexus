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
import me.pugabyte.bncore.features.minigames.Minigames;
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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

//TODO:
// 	- GUI for picking block
// 	- Changing gamemodes (logic)
//	- Changing maps (locations)
//	- 10 seconds then check if player has not jumped yet --> "Kill"
//	- Display score after each round

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
		super.onQuit(minigamer);
		ThimbleMatchData matchData = (ThimbleMatchData) minigamer.getMatch().getMatchData();
		matchData.getAlivePlayers().remove(minigamer);
	}

	@Override
	public void onJoin(Minigamer minigamer) {
		super.onJoin(minigamer);
		Player player = minigamer.getPlayer();
		ItemStack item = new ItemStack(Material.CONCRETE, 1);
		for(int i = 0; i < 9; i++){
			item.setDurability(CONCRETE_IDS[i]);
			player.getInventory().setItem(i, item);
		}
		Utils.wait(30, () -> minigamer.tell("Click a block to select it!"));
	}

	@Override
	public void onInitialize(Match match) {
		match.setMatchData(new ThimbleMatchData(match));
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

		// Select next gamemode
		arena.setGameMode("1");

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
		if(ndx >= thimbleMaps.size()){
			ndx = 0;
		}else{
			ndx+=1;
		}
		arena.setCurrentMap(thimbleMaps.get(ndx-1));

		// Setup next pool region string
		arena.setPoolRegionStr("thimble_" + arena.getCurrentMap().getName().toLowerCase() + "_pool");

		nextTurn(match);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onEnd(Match match) {
		super.onEnd(match);

		ThimbleArena arena = (ThimbleArena) match.getArena();

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

		// Most Points & Risky Reward, keep the player in the game
		if(!matchData.getTurnList().contains(victim)) {
			matchData.getTurnList().add(victim);
			victim.teleport(arena.getCurrentMap().getSpectateLocation());
			Match match = victim.getMatch();
			match.broadcast(victim.getColoredName() + " missed.");

			Utils.wait(30, () -> nextTurn(match));
		}
	}

	@Override
	public void kill(Minigamer minigamer) {
		//
	}

	private void score(Minigamer minigamer){
		ThimbleMatchData matchData = (ThimbleMatchData) minigamer.getMatch().getMatchData();

		matchData.setTurns(matchData.getTurns()+1);
		matchData.getTurnList().add(minigamer);
		minigamer.scored();
		Match match = minigamer.getMatch();
		Utils.wait(30, () -> nextTurn(match));
	}

	private void newRound(Match match) {
		ThimbleMatchData matchData = (ThimbleMatchData) match.getMatchData();

		if (matchData.getAlivePlayers().size() <= 1) {
			match.end();
			return;
		}

		if(matchData.getTurns() >= MAX_TURNS){
			match.end();
			return;
		}

		if(match.isEnded()) {
			return;
		}

		match.broadcast("New Round!");
		matchData.getTurnList().clear();
		Collections.shuffle(matchData.getAlivePlayers());
		Utils.wait(30, () -> nextTurn(match));
	}

	private void nextTurn(Match match){
		ThimbleMatchData matchData = (ThimbleMatchData) match.getMatchData();
		ThimbleArena arena = (ThimbleArena) match.getArena();

		if(match.isEnded()) {
			return;
		}

		if(matchData.getTurns() >= MAX_TURNS){
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
			match.end();
			return;
		}

		Minigamer finalNextMinigamer = nextMinigamer;
		Player player = finalNextMinigamer.getPlayer();

		player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, SoundCategory.MASTER, 10.0F, 1.0F);
		finalNextMinigamer.teleport(arena.getCurrentMap().getNextTurnLocation());
		Utils.wait(3, () -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, SoundCategory.MASTER, 10.0F, 1.2F));
	}

	// Auto-select unique concrete blocks for players who have not themselves
	private void setPlayerBlocks(List<Minigamer> minigamers, Match match){
		ThimbleMatchData matchData = (ThimbleMatchData) match.getMatchData();
		for (Minigamer minigamer: minigamers) {
			Player player = minigamer.getPlayer();
			ItemStack helmetItem = player.getInventory().getHelmet();
			player.getInventory().clear();
			if(helmetItem == null || helmetItem.getType().equals(Material.AIR)) {
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
		if(!minigamer.isInLobby(Thimble.class))
			return;

		Match match = minigamer.getMatch();
		ThimbleMatchData matchData = (ThimbleMatchData) match.getMatchData();

		if (!match.isStarted()) {
			PlayerInventory playerInv = player.getInventory();
			ItemStack heldItem = playerInv.getItemInMainHand();
			if(heldItem == null || heldItem.getType().equals(Material.AIR)) {
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
		if(!minigamer.isPlaying(Thimble.class)) return;

		ThimbleArena arena = (ThimbleArena) minigamer.getMatch().getArena();
		if(event.getRegion().getId().equals(arena.getPoolRegionStr())) {
			if(!Utils.isInWater(player)) return;
			if(player.getInventory().getHelmet() == null) return;

			ThimbleMatchData matchData = (ThimbleMatchData) minigamer.getMatch().getMatchData();
			if(!matchData.getAlivePlayers().contains(minigamer)) return;

			ItemStack item = player.getInventory().getHelmet();
			short durability = item.getDurability();

			Location location = player.getLocation();
			location.getBlock().setType(Material.CONCRETE);
			location.getBlock().setData(Byte.parseByte(Short.toString(durability)));

			Color color = Utils.getColor(Utils.getColor((int) durability));
			Location fireworkLocation = location.add(0.0,2.0,0.0);

			new FireworkLauncher(fireworkLocation)
					.type(FireworkEffect.Type.BALL)
					.color(color)
					.power(0)
					.detonateAfter(1)
					.launch();

			minigamer.teleport(minigamer.getMatch().getArena().getLobby().getLocation());

			score(minigamer);
		}
	}
}

package me.pugabyte.bncore.features.minigames.mechanics;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.util.EditSessionBuilder;
import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.managers.RegionManager;
import me.pugabyte.bncore.Utils;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//TODO:
// 	- GUI for picking block
// 	- Changing gamemodes (logic)
//	- Changing maps (locations)
//	- 10 seconds then check if player has not jumped yet --> "Kill"
//	- Display score after each round

public final class Thimble extends TeamlessMechanic {

	private String poolRegionStr = "thimble_wooden_pool";
	private ArrayList<Minigamer> turnList = new ArrayList<>();
	private ArrayList<Minigamer> alivePlayers = new ArrayList<>();
	private Location nextPlayerLoc = new Location(Minigames.getGameworld(), 2503.5, 38.5, -220.5, -90, 0);
	private Location spectateLoc = new Location(Minigames.getGameworld(), 2519.5, 4.5, -221.5, 90, 0);
	private final short CONCRETE_IDS[] = {14,1,4,5,13,10,2,6,12,15,7,8,0};
	private ArrayList<Short> chosenConcrete = new ArrayList<>();
//	private ArrayList<ItemStack> chosenConcrete = new ArrayList<>();
	private int turns;
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
		alivePlayers.remove(minigamer);
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
		minigamer.tell("Click a block to select it!");
	}

	@Override
	public void onStart(Match match) {
		List<Minigamer> minigamers = match.getMinigamers();
		setPlayerBlocks(minigamers);
		alivePlayers.addAll(minigamers);
		Collections.shuffle(alivePlayers);
		turns = 0;

		nextTurn(match);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onEnd(Match match) {
		super.onEnd(match);

		// Clear data lists
		alivePlayers.clear();
		turnList.clear();
		chosenConcrete.clear();
		// Reset Pool
		World world = FaweAPI.getWorld((Minigames.getGameworld().getName()));
		EditSession editSession = new EditSessionBuilder(world).fastmode(true).build();
		RegionManager regionManager = WGBukkit.getRegionManager(Minigames.getGameworld());
		if(regionManager.getRegion(poolRegionStr) != null) {
			Vector max = regionManager.getRegion(poolRegionStr).getMaximumPoint();
			Vector min = regionManager.getRegion(poolRegionStr).getMinimumPoint();
			Region poolRegion = new CuboidRegion(max, min);
			BaseBlock baseBlock = new BaseBlock(9, 0);

			editSession.setBlocks(poolRegion, baseBlock);
			editSession.flushQueue();
		}
		// TODO: Select next gamemode
		// TODO: Select & Setup next map
		// Setup next pool region string
		String nextMapName = "Wooden";
		poolRegionStr = "thimble_" + nextMapName.toLowerCase() + "_pool";
	}

	@Override
	public void onDeath(Minigamer victim) {
		// Most Points & Risky Reward, keep the player in the game
		if(!turnList.contains(victim)) {
			turnList.add(victim);
			victim.teleport(spectateLoc);
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
		turns++;
		turnList.add(minigamer);
		minigamer.scored();
		Match match = minigamer.getMatch();
		Utils.wait(30, () -> nextTurn(match));
	}

	private void newRound(Match match) {
		if (alivePlayers.size() <= 1) {
			match.end();
			return;
		}

		if(turns >= MAX_TURNS){
			match.end();
			return;
		}

		if(match.isEnded()) {
			return;
		}

		match.broadcast("New Round!");
		turnList.clear();
		Collections.shuffle(alivePlayers);
		Utils.wait(30, () -> nextTurn(match));
	}

	private void nextTurn(Match match){
		if(match.isEnded()) {
			return;
		}

		if(turnList.size() == alivePlayers.size()) {
			newRound(match);
			return;
		}

		Minigamer nextMinigamer = null;
		for (Minigamer minigamer: alivePlayers) {
			if(!turnList.contains(minigamer)){
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
		finalNextMinigamer.teleport(nextPlayerLoc);
		Utils.wait(3, () -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, SoundCategory.MASTER, 10.0F, 1.2F));
	}

	// Auto-select unique concrete blocks for players who have not themselves
	private void setPlayerBlocks(List<Minigamer> minigamers){
		for (Minigamer minigamer: minigamers) {
			Player player = minigamer.getPlayer();
			ItemStack helmetItem = player.getInventory().getHelmet();
			player.getInventory().clear();
			if(helmetItem == null || helmetItem.getType().equals(Material.AIR)) {
				for(int i = 0; i < CONCRETE_IDS.length-1; i++){
					if(chosenConcrete.get(i) == CONCRETE_IDS[i]){
						short durability = CONCRETE_IDS[i];
						ItemStack concrete = new ItemStack(Material.CONCRETE, 1);
						concrete.setDurability(durability);
						chosenConcrete.add(durability);
						helmetItem = concrete;
						break;
					}
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
		Match match = minigamer.getMatch();
		if(!minigamer.isInLobby(Thimble.class))
			return;

		if (!match.isStarted()) {
			PlayerInventory playerInv = player.getInventory();
			ItemStack heldItem = playerInv.getItemInMainHand();
			if(heldItem == null || heldItem.getType().equals(Material.AIR)) {
				heldItem = playerInv.getItemInOffHand();
			}

			if (heldItem != null && heldItem.getType().equals(Material.CONCRETE)) {
				short itemDurability = heldItem.getDurability();
				// Test if selected concrete is already chosen
				if(!chosenConcrete.contains(itemDurability)){
					// Remove item on head from chosenIDs
					if (playerInv.getHelmet() != null && playerInv.getHelmet().getType().equals(Material.CONCRETE)) {
						for (Short durability: chosenConcrete){
							if(durability.equals(itemDurability)){
								chosenConcrete.remove(itemDurability);
								break;
							}
						}
					}
					// Add new item on head to chosenIDs
					playerInv.setHelmet(heldItem);
					chosenConcrete.add(itemDurability);
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
		if(event.getRegion().getId().equals(poolRegionStr)) {
			Player player = event.getPlayer();
			Minigamer minigamer = PlayerManager.get(player);

			if(!minigamer.isPlaying(Thimble.class)) return;
			if(!Utils.isInWater(player)) return;
			if(player.getInventory().getHelmet() == null) return;
			if(!alivePlayers.contains(minigamer)) return;

			ItemStack item = player.getInventory().getHelmet();
			short durability = item.getDurability();

			Location location = player.getLocation();
			location.getBlock().setType(Material.CONCRETE);
			location.getBlock().setData(Byte.parseByte(Short.toString(durability)));

			Color color = Utils.getColor(Utils.getColor((int) durability));
			Location fireworkLocation = location.add(0.0,2.0,0.0);

			Firework firework = (Firework) player.getWorld().spawnEntity(fireworkLocation, EntityType.FIREWORK);
			FireworkEffect effect = FireworkEffect.builder()
					.with(FireworkEffect.Type.BALL)
					.withColor(color)
					.build();

			FireworkMeta meta = firework.getFireworkMeta();
			meta.setPower(0);

			meta.addEffect(effect);
			firework.setFireworkMeta(meta);

			minigamer.teleport(minigamer.getMatch().getArena().getLobby().getLocation());
			Utils.wait(1, firework::detonate);

			score(minigamer);
		}
	}
}

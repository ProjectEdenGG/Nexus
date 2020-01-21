package me.pugabyte.bncore.features.minigames.mechanics;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.matchdata.GrabAJumbuckMatchData;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GrabAJumbuck extends TeamlessMechanic {

	@Override
	public String getName() {
		return "Grab-A-Jumbuck";
	}

	@Override
	public String getDescription() {
		return "Grab as many sheep as possible! Colored sheep are worth more points!";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.WOOL);
	}

	@Override
	public void onStart(Match match) {
		match.setMatchData(new GrabAJumbuckMatchData(match));
		GrabAJumbuckMatchData matchData = (GrabAJumbuckMatchData) match.getMatchData();
		matchData.setRegion(new WorldGuardUtils(Minigames.getGameworld()).getProtectedRegion("grabajumbuck_" + match.getArena().getName() + "_sheepRegion"));
		matchData.setMax(matchData.getRegion().getMaximumPoint());
		matchData.setMin(matchData.getRegion().getMinimumPoint());
		spawn(match, 20);
	}

	@Override
	public void onEnd(Match match) {
		match.getMinigamers().forEach((player) -> removeAllPassangers(player.getPlayer(), match));
		GrabAJumbuckMatchData matchData = (GrabAJumbuckMatchData) match.getMatchData();
		try {
			matchData.getSheeps().forEach(Entity::remove);
			matchData.getSheeps().clear();
			matchData.getItems().forEach(Entity::remove);
			matchData.getItems().clear();
		} catch (Exception ignore) {}
		super.onEnd(match);
	}

	public void spawn(Match match, int sheepAmount){
		GrabAJumbuckMatchData matchData = (GrabAJumbuckMatchData) match.getMatchData();
		while (sheepAmount != 0) {
			Sheep sheep = Minigames.getGameworld().spawn(randomGrassBlock(match).clone().add(0, 1, 0), Sheep.class);
			DyeColor color = ColorType.values()[Utils.randomInt(1, ColorType.values().length - 1)].getDyeColor();
			if (Utils.randomInt(0, 100) > 80) sheep.setColor(color);
			matchData.getSheeps().add(sheep);
			sheepAmount--;
		}
	}

	public Location randomGrassBlock(Match match) {
		GrabAJumbuckMatchData matchData = (GrabAJumbuckMatchData) match.getMatchData();
		int x;
		int z;
		int attempts = 10;
		for (int j = 0; j < attempts; j++) {
			x = Utils.randomInt((int) matchData.getMin().getX(), (int) matchData.getMax().getX());
			z = Utils.randomInt((int) matchData.getMin().getZ(), (int) matchData.getMax().getZ());
			Block block = Minigames.getGameworld().getHighestBlockAt(x, z).getLocation().subtract(0, 1, 0).getBlock();
			if (block.getType() != Material.GRASS) continue;
			return block.getLocation();
		}
		return null;
	}

	public void addSheep(Minigamer minigamer, Sheep sheep) {
		if (getTopPassenger(minigamer) == minigamer.getPlayer()) {
			GrabAJumbuckMatchData matchData = (GrabAJumbuckMatchData) minigamer.getMatch().getMatchData();
			Item item = spawnItem(minigamer.getPlayer().getLocation());
			minigamer.getPlayer().addPassenger(item);
			item.addPassenger(sheep);
			matchData.getItems().add(item);
		} else {
			getTopPassenger(minigamer).addPassenger(sheep);
		}
	}

	public Item spawnItem(Location loc){
		Item item = Minigames.getGameworld().dropItem(loc, new ItemStack(Material.STONE_BUTTON));
		item.setItemStack(new ItemStack(Material.STONE_BUTTON));
		item.setInvulnerable(true);
		item.setPickupDelay(99999999);
		return item;
	}

	public Entity getTopPassenger(Minigamer minigamer) {
		if (minigamer.getPlayer().getPassengers().size() == 0) return minigamer.getPlayer();
		if (minigamer.getPlayer().getPassengers().get(0).getPassengers().size() == 0) return minigamer.getPlayer().getPassengers().get(0);
		return getSheep(minigamer).get(getSheep(minigamer).size() - 1);
	}

	public List<Sheep> getSheep(Minigamer minigamer) {
		List<Sheep> sheep = new ArrayList<>();
		Entity entity = minigamer.getPlayer();
		while (entity.getPassengers().size() > 0) {
			if (entity.getPassengers().get(0).getType() == EntityType.SHEEP) {
				sheep.add((Sheep) entity.getPassengers().get(0));
			}
			entity = entity.getPassengers().get(0);
		}
		return sheep;
	}

	public void removeAllPassangers(Entity entity, Match match) {
		GrabAJumbuckMatchData matchData = (GrabAJumbuckMatchData) match.getMatchData();
		if (entity.getPassengers().size() > 0) {
			Entity newEntity = entity.getPassengers().get(0);
			entity.removePassenger(newEntity);
			BNCore.log("Removed " + newEntity.getType() + " from " + entity.getType());
			if (entity.getType() == EntityType.DROPPED_ITEM) {
				matchData.getItems().remove(entity);
				entity.remove();
			}
			removeAllPassangers(newEntity, match);
		}
	}

	@EventHandler
	public void onSheepClick(PlayerInteractEntityEvent event) {
		if(!event.getHand().equals(EquipmentSlot.HAND)) return;
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;
		GrabAJumbuckMatchData matchData = (GrabAJumbuckMatchData) minigamer.getMatch().getMatchData();
		if (!matchData.getSheeps().contains(event.getRightClicked())) return;
		if (getSheep(minigamer).size() == 3) {
			minigamer.getPlayer().sendMessage(Minigames.PREFIX + "You can only carry three sheep at a time!");
			return;
		}
		Sheep sheep = (Sheep) event.getRightClicked();
		if(sheep.isInsideVehicle()) return;
		addSheep(minigamer, sheep);
		minigamer.getMatch().getTasks().wait(8 * 20, () -> {
			if(minigamer.getMatch().isEnded()) return;
			spawn(minigamer.getMatch(), 1);
		});
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		Set<ProtectedRegion> regions = new WorldGuardUtils(Minigames.getGameworld()).getRegionsAt(event.getDamager().getLocation());
		for (ProtectedRegion region : regions) {
			if (region.getId().contains("grabajumbuck_captureregion")) {
				event.setCancelled(true);
				return;
			}
		}
		if (!(event.getEntity() instanceof Player)) return;
		Player player = (Player) event.getEntity();
		Minigamer minigamer = PlayerManager.get(player);
		if (!minigamer.isPlaying(this));
		removeAllPassangers(event.getEntity(), minigamer.getMatch());
	}

	@EventHandler
	public void onRegionEnter(RegionEnteredEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;
		String regionName = event.getRegion().getId().toLowerCase();
		if (!event.getRegion().getId().contains("grabajumbuck_captureregion")) return;
		if(getTopPassenger(minigamer) == minigamer.getPlayer()) return;
		int score = 0;
		for (Sheep sheep : getSheep(minigamer)) {
			if(sheep.getColor() == DyeColor.WHITE) {
				score++;
			} else {
				score = score + 3;
			}
			sheep.remove();
		}
		removeAllPassangers(minigamer.getPlayer(), minigamer.getMatch());
		minigamer.scored(score);
		minigamer.getMatch().broadcast(minigamer.getColoredName() + " has scored " + score + " point" + ((score == 1) ? "." : "s."));
	}

}

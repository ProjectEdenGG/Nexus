package me.pugabyte.bncore.features.minigames.mechanics;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.SneakyThrows;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.arenas.GrabAJumbuckArena;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.bncore.features.minigames.models.matchdata.GrabAJumbuckMatchData;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
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
	public void onStart(MatchStartEvent event) {
		super.onStart(event);
		spawnSheep(event.getMatch(), 20);
	}

	@Override
	public void onEnd(MatchEndEvent event) {
		Match match = event.getMatch();
		match.getMinigamers().forEach((player) -> removeAllPassengers(player.getPlayer(), match));
		GrabAJumbuckMatchData matchData = match.getMatchData();
		try {
			matchData.getSheeps().forEach(Entity::remove);
			matchData.getItems().forEach(Entity::remove);
		} catch (Exception ignore) {}
		match.getMinigamers().forEach(Minigamer::toGamelobby);
		super.onEnd(event);
	}

	public void spawnSheep(Match match, int sheepAmount) {
		GrabAJumbuckMatchData matchData = match.getMatchData();
		while (sheepAmount != 0) {
			Sheep sheep = Minigames.getGameworld().spawn(getRandomSheepSpawnLocation(match), Sheep.class);
			sheep.setInvulnerable(true);
			DyeColor color = ColorType.values()[Utils.randomInt(1, ColorType.values().length - 1)].getDyeColor();
			if (Utils.randomInt(0, 100) > 80) sheep.setColor(color);
			matchData.getSheeps().add(sheep);
			sheepAmount--;
		}
	}

	public Location getRandomSheepSpawnLocation(Match match) {
		Block block = Minigames.getWorldGuardUtils().getRandomBlock(match.getArena().getProtectedRegion("sheep"));
		if (block == null)
			return getRandomSheepSpawnLocation(match);
		block = Minigames.getGameworld().getHighestBlockAt((int) block.getLocation().getX(), (int) block.getLocation().getZ()).getLocation().clone().subtract(0, 1, 0).getBlock();
		GrabAJumbuckArena arena = (GrabAJumbuckArena) ArenaManager.convert(match.getArena(), GrabAJumbuckArena.class);
		if (!arena.getSheepSpawnBlocks().contains(block.getType()))
			return getRandomSheepSpawnLocation(match);
		return block.getLocation().clone().add(0, 2, 0);
	}

	@SneakyThrows
	public void addSheep(Minigamer minigamer, Sheep sheep) {
		if (getTopPassenger(minigamer) == minigamer.getPlayer()) {
			GrabAJumbuckMatchData matchData = minigamer.getMatch().getMatchData();
			Item item = spawnItem(minigamer.getPlayer().getLocation());
			minigamer.getPlayer().addPassenger(item);
			item.addPassenger(sheep);
			matchData.getItems().add(item);
		} else {
			getTopPassenger(minigamer).addPassenger(sheep);
		}
		minigamer.getPlayer().setLevel(getSheep(minigamer).size());
	}

	public Item spawnItem(Location loc) {
		Item item = Minigames.getGameworld().dropItem(loc, new ItemStack(Material.STONE_BUTTON));
		item.setItemStack(new ItemStack(Material.STONE_BUTTON));
		item.setInvulnerable(true);
		item.setPickupDelay(99999999);
		return item;
	}

	public Entity getTopPassenger(Minigamer minigamer) {
		if (minigamer.getPlayer().getPassengers().size() == 0) return minigamer.getPlayer();
		if (minigamer.getPlayer().getPassengers().get(0).getPassengers().size() == 0)
			return minigamer.getPlayer().getPassengers().get(0);
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

	public void removeAllPassengers(Entity entity, Match match) {
		GrabAJumbuckMatchData matchData = match.getMatchData();
		if (entity.getPassengers().size() > 0) {
			if (entity instanceof Player) ((Player) entity).setLevel(0);
			Entity newEntity = entity.getPassengers().get(0);
			entity.removePassenger(newEntity);
			if (entity.getType() == EntityType.DROPPED_ITEM) {
				matchData.getItems().remove(entity);
				entity.remove();
			}
			removeAllPassengers(newEntity, match);
		}
	}

	@EventHandler
	public void onSheepClick(PlayerInteractEntityEvent event) {
		if (!event.getHand().equals(EquipmentSlot.HAND)) return;
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;
		GrabAJumbuckMatchData matchData = minigamer.getMatch().getMatchData();
		if (!matchData.getSheeps().contains(event.getRightClicked())) return;
		if (getSheep(minigamer).size() == 3) {
			minigamer.getPlayer().sendMessage(Minigames.PREFIX + "You can only carry three sheep at a time!");
			return;
		}
		Sheep sheep = (Sheep) event.getRightClicked();
		if (sheep.isInsideVehicle()) return;
		addSheep(minigamer, sheep);
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		Set<ProtectedRegion> regions = Minigames.getWorldGuardUtils().getRegionsAt(event.getDamager().getLocation());
		for (ProtectedRegion region : regions) {
			Arena arena = ArenaManager.getFromRegion(region.getId());
			if (arena != null && arena.ownsRegion(region.getId(), "capture")) {
				event.setCancelled(true);
				return;
			}
		}

		if (!(event.getEntity() instanceof Player)) return;
		Player player = (Player) event.getEntity();
		Minigamer minigamer = PlayerManager.get(player);
		if (!minigamer.isPlaying(this)) return;
		removeAllPassengers(player, minigamer.getMatch());
	}

	@EventHandler
	public void onRegionEnter(RegionEnteredEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;
		Arena arena = minigamer.getMatch().getArena();
		if (!arena.ownsRegion(event.getRegion().getId(), "capture")) return;
		if (getTopPassenger(minigamer) == minigamer.getPlayer()) return;
		int score = 0;
		for (Sheep sheep : getSheep(minigamer)) {
			score += sheep.getColor() == DyeColor.WHITE ? 1 : 3;
			sheep.remove();
		}
		removeAllPassengers(minigamer.getPlayer(), minigamer.getMatch());
		minigamer.scored(score);
		minigamer.getPlayer().sendMessage(Minigames.PREFIX + "You scored " + score + " point" + ((score == 1) ? "" : "s"));
		minigamer.getMatch().getTasks().wait(8 * 20, () -> {
			if (minigamer.getMatch().isEnded()) return;
			spawnSheep(minigamer.getMatch(), 1);
		});
	}

}

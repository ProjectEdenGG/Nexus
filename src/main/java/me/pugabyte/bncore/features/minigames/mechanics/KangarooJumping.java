package me.pugabyte.bncore.features.minigames.mechanics;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.arenas.KangarooJumpingArena;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.ItemStackBuilder;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class KangarooJumping extends TeamlessMechanic {

	@Override
	public String getName() {
		return "Kangaroo Jumping";
	}

	@Override
	public String getDescription() {
		return "Jump higher and higher and be the first to the finish!";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.LEATHER_BOOTS);
	}

	@Override
	public void onStart(Match match) {
		super.onStart(match);
		KangarooJumpingArena arena = (KangarooJumpingArena) match.getArena();
		Utils.wait(5 * 20, () -> {
			for (Location location : arena.getTeams().get(0).getSpawnpoints())
				spawnPowerUp(match, location);
		});
	}

	private void spawnPowerUp(Match match, Location location) {
		Hologram hologram = HologramsAPI.createHologram(BNCore.getInstance(), location.clone().add(0, 2, 0));
		hologram.appendTextLine(Utils.colorize("&3Power Up"));
		ItemLine itemLine = hologram.appendItemLine(new ItemStackBuilder(Material.POTION).effectColor(ColorType.PINK.getColor()).build());
		itemLine.setPickupHandler(player -> {
			player.sendMessage("You picked up a power up!");
			match.getHolograms().remove(hologram);
			hologram.delete();
			Utils.wait(10 * 20, ()->spawnPowerUp(match, location));
		});
		match.getHolograms().add(hologram);
	}

	@EventHandler
	public void onPlayerPressurePlate(PlayerInteractEvent event) {
		if (!event.getAction().equals(Action.PHYSICAL)) return;
		if (event.getClickedBlock().getType() != Material.STONE_PLATE) return;
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;
		minigamer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 10 * 20, 10));
		minigamer.getPlayer().sendMessage(Utils.getPrefix("Kangaroo Jump") + "Boooiiinnnggg!");
		event.getClickedBlock().getWorld().getBlockAt(event.getClickedBlock().getLocation()).setType(Material.AIR);
		minigamer.getMatch().getTasks().wait(10 * 20, () -> event.getClickedBlock().getWorld().getBlockAt(event.getClickedBlock().getLocation()).setType(Material.STONE_PLATE));
	}

	@EventHandler
	public void onEnterWinningArea(RegionEnteredEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;
		KangarooJumpingArena arena = (KangarooJumpingArena) minigamer.getMatch().getArena();
		if (!arena.ownsRegion(event.getRegion().getId(), "winningregion")) return;
		if (hasAnyoneScored(minigamer.getMatch())) return;
		minigamer.scored();
		minigamer.getMatch().broadcast("&e" + minigamer.getColoredName() + " has reached the finish area!");
		minigamer.getMatch().getTasks().wait(5 * 20, () -> minigamer.getMatch().end());
	}

	boolean hasAnyoneScored(Match match) {
		for (Minigamer minigamer : match.getMinigamers()) {
			if (minigamer.getScore() > 0)
				return true;
		}
		return false;
	}

}


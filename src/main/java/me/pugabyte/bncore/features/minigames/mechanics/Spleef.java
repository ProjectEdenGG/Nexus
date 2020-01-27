package me.pugabyte.bncore.features.minigames.mechanics;

import me.pugabyte.bncore.features.minigames.mechanics.common.SpleefMechanic;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public final class Spleef extends SpleefMechanic {

	@Override
	public String getName() {
		return "Spleef";
	}

	@Override
	public String getDescription() {
		return "Spleef other players off the map to win!";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.DIAMOND_SPADE);
	}

	@Override
	public void onPlayerInteract(Minigamer minigamer, PlayerInteractEvent event) {
		super.onPlayerInteract(minigamer, event);
		if (event.isCancelled()) return;

		Location location = event.getClickedBlock().getLocation();

		if (breakBlock(minigamer.getMatch(), location))
			event.setCancelled(true);
	}

	@Override
	public void playBlockBreakSound(Location location) {
		Material material = location.getBlock().getType();
		Sound sound = Sound.BLOCK_STONE_BREAK;
		if (material.equals(Material.SNOW))
			sound = Sound.BLOCK_SNOW_BREAK;
		if (material.equals(Material.WOOL))
			sound = Sound.BLOCK_CLOTH_BREAK;

		location.getWorld().playSound(location, sound, 1.0F, 0.75F);
	}

}

package gg.projecteden.nexus.features.events.y2024.pugmas24.fairgrounds.reflection;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class Pugmas24ReflectionListeners implements Listener {

	public Pugmas24ReflectionListeners() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onRegionEnter(PlayerEnteredRegionEvent event) {
		String regionId = event.getRegion().getId();
		if (regionId.equalsIgnoreCase(Pugmas24Reflection.getGameRg()))
			Pugmas24.get().sendNoPrefix(event.getPlayer(), Pugmas24Reflection.getPrefix() + Pugmas24Reflection.getMessage());
	}

	@EventHandler
	public void onButtonPress(PlayerInteractEvent event) {
		if (!Pugmas24.get().isAtEvent(event))
			return;

		if (event.getClickedBlock() == null)
			return;

		if (!MaterialTag.BUTTONS.isTagged(event.getClickedBlock().getType()))
			return;

		Block button = event.getClickedBlock();
		BlockData blockData = button.getBlockData();
		Directional directional = (Directional) blockData;
		Block block = button.getRelative(0, -1, 0).getRelative(directional.getFacing().getOppositeFace());
		Material type = block.getType();

		if (type.equals(Material.IRON_BLOCK))
			Pugmas24ReflectionUtils.rotateBanner(block.getRelative(0, 2, 0));
		else if (type.equals(Material.NETHERITE_BLOCK) && !Pugmas24Reflection.isActive()) {
			Location skullLoc = LocationUtils.getCenteredLocation(block.getRelative(0, 3, 0).getLocation());
			skullLoc.setY(skullLoc.getY() + 0.25);
			Pugmas24Reflection.setLaserStart(skullLoc);

			BlockData blockDataDir = skullLoc.getBlock().getBlockData();
			if (!(blockDataDir instanceof Rotatable skullDir))
				return;

			BlockFace skullFace = skullDir.getRotation().getOppositeFace();

			Pugmas24Reflection.startLaser(event.getPlayer(), skullFace);
			Pugmas24Reflection.setButtonPresser(event.getPlayer());
		}
	}
}

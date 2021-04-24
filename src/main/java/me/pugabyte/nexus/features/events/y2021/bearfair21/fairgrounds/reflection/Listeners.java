package me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.reflection;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21;
import me.pugabyte.nexus.utils.LocationUtils;
import me.pugabyte.nexus.utils.MaterialTag;
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
import org.bukkit.inventory.EquipmentSlot;

import static me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21.send;

public class Listeners implements Listener {

	public Listeners() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onRegionEnter(RegionEnteredEvent event) {
		String regionId = event.getRegion().getId();
		if (regionId.equalsIgnoreCase(ReflectionGame.getGameRg()))
			send(ReflectionGame.getPrefix() + ReflectionGame.getMessage(), event.getPlayer());
	}

	@EventHandler
	public void onButtonPress(PlayerInteractEvent event) {
		if (event.getClickedBlock() == null) return;
		if (event.getHand() == null) return;
		if (!event.getHand().equals(EquipmentSlot.HAND)) return;
		if (!MaterialTag.BUTTONS.isTagged(event.getClickedBlock().getType())) return;

		Block button = event.getClickedBlock();
		if (!BearFair21.isAtBearFair(button.getLocation())) return;

		BlockData blockData = button.getBlockData();
		Directional directional = (Directional) blockData;
		Block block = button.getRelative(0, -1, 0).getRelative(directional.getFacing().getOppositeFace());
		Material type = block.getType();

		if (type.equals(Material.IRON_BLOCK))
			ReflectionGameUtils.rotateBanner(block.getRelative(0, 2, 0));
		else if (type.equals(Material.NETHERITE_BLOCK) && !ReflectionGame.isActive()) {
			Location skullLoc = LocationUtils.getCenteredLocation(block.getRelative(0, 3, 0).getLocation());
			skullLoc.setY(skullLoc.getY() + 0.25);
			ReflectionGame.setLaserStart(skullLoc);

			BlockData blockDataDir = skullLoc.getBlock().getBlockData();
			if (!(blockDataDir instanceof Rotatable))
				return;

			Rotatable skullDir = (Rotatable) blockDataDir;
			BlockFace skullFace = skullDir.getRotation().getOppositeFace();

			ReflectionGame.startLaser(event.getPlayer(), skullFace);
			ReflectionGame.setButtonPresser(event.getPlayer());
		}
	}
}

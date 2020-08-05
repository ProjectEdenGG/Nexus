package me.pugabyte.bncore.features.holidays.aeveonproject.effects;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.utils.MaterialTag;
import me.pugabyte.bncore.utils.RandomUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

import static me.pugabyte.bncore.features.holidays.aeveonproject.AeveonProject.*;
import static me.pugabyte.bncore.features.holidays.aeveonproject.sets.sialia.Regions.*;

public class ClientsideBlocks implements Listener {
	// check on world change, and on login to update region

	public ClientsideBlocks() {
		BNCore.registerListener(this);
	}

	@EventHandler
	public void onEnterRegion_Update(RegionEnteredEvent event) {
		Player player = event.getPlayer();
		if (!isInWorld(player)) return;

		String id = event.getRegion().getId();
		if (!id.contains("update")) return;

		// Sialia
		if (id.contains(shipColor)) {
			// Ship Color
			Material concreteType = RandomUtils.randomMaterial(MaterialTag.CONCRETES);
			List<Block> blocks = WEUtils.getBlocks(WGUtils.getRegion(shipColor));

			for (Block block : blocks) {
				if (block.getType().equals(Material.WHITE_CONCRETE))
					player.sendBlockChange(block.getLocation(), concreteType.createBlockData());
			}

			// Docking Ports Water
			blocks.clear();
			for (int i = 1; i <= dockingport_count; i++) {
				blocks = WEUtils.getBlocks(WGUtils.getRegion(dockingports.replaceAll("#", String.valueOf(i))));
				for (Block block : blocks) {
					if (block.getType().equals(Material.WATER))
						player.sendBlockChange(block.getLocation(), Material.AIR.createBlockData());
				}
			}
		}
	}
}

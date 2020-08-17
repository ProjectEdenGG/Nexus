package me.pugabyte.bncore.features.holidays.aeveonproject.effects;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.aeveonproject.Regions;
import me.pugabyte.bncore.features.holidays.aeveonproject.sets.sialia.Sialia;
import me.pugabyte.bncore.models.aeveonproject.AeveonProjectService;
import me.pugabyte.bncore.models.aeveonproject.AeveonProjectUser;
import me.pugabyte.bncore.utils.ColorType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

import static me.pugabyte.bncore.features.holidays.aeveonproject.AeveonProject.*;

public class ClientsideBlocks implements Listener {
	AeveonProjectService service = new AeveonProjectService();
	// check on world change, and on login to update region

	public ClientsideBlocks() {
		BNCore.registerListener(this);
	}

	@EventHandler
	public void onEnterRegion_Update(RegionEnteredEvent event) {
		Player player = event.getPlayer();
		if (!isInWorld(player)) return;

		String id = event.getRegion().getId();
		if (!Regions.group_shipColor_Update.contains(id)) return;

		if (!service.hasStarted(player)) return;
		AeveonProjectUser user = service.get(player);

		// Any Ship Color Region
		if (Regions.group_shipColor_Update.contains(id)) {
			Material concreteType = ColorType.of(user.getShipColor()).getConcrete();
			List<Block> blocks = WEUtils.getBlocks(WGUtils.getRegion(Regions.getShipColorRegion(id)));

			for (Block block : blocks) {
				if (block.getType().equals(Material.WHITE_CONCRETE))
					player.sendBlockChange(block.getLocation(), concreteType.createBlockData());
			}
		}


		// Sialia Docking Ports Water
		if (id.contains(Regions.sialia_shipColor_update)) {
			for (int i = 1; i <= Sialia.dockingport_count; i++) {
				List<Block> blocks = WEUtils.getBlocks(WGUtils.getRegion(Regions.sialia_dockingports.replaceAll("#", String.valueOf(i))));
				for (Block block : blocks) {
					if (block.getType().equals(Material.WATER))
						player.sendBlockChange(block.getLocation(), Material.AIR.createBlockData());
				}
			}
		}
	}
}

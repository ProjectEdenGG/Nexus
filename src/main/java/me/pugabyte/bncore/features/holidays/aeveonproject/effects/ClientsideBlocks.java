package me.pugabyte.bncore.features.holidays.aeveonproject.effects;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.sk89q.worldedit.regions.Region;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.aeveonproject.AeveonProject;
import me.pugabyte.bncore.features.holidays.aeveonproject.sets.APSet;
import me.pugabyte.bncore.features.holidays.aeveonproject.sets.APSetType;
import me.pugabyte.bncore.models.aeveonproject.AeveonProjectService;
import me.pugabyte.bncore.models.aeveonproject.AeveonProjectUser;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Collection;
import java.util.List;

import static me.pugabyte.bncore.features.holidays.aeveonproject.AeveonProject.*;

public class ClientsideBlocks implements Listener {
	AeveonProjectService service = new AeveonProjectService();
	private final int minBoundary = 10;
	private final int maxBoundary = 30;
	// check on world change, and on login to update region

	public ClientsideBlocks() {
		BNCore.registerListener(this);

		updateTask();
	}

	private void updateTask() {
		Tasks.repeat(0, Time.SECOND.x(2), () -> {
			for (APSetType APSetType : APSetType.values()) {
				APSet set = APSetType.get();
				List<String> updateRegions = set.getUpdateRegions();
				if (updateRegions == null)
					continue;
				Collection<Player> setPlayers = WGUtils.getPlayersInRegion(set.getRegion());

				for (String updateRegion : updateRegions) {
					Region region = WGUtils.getRegion(updateRegion);
					Location rgCenter = WEUtils.toLocation(region.getCenter());
					double rgBoundary = Math.max(Math.min(Math.max(region.getWidth(), region.getLength()), maxBoundary), minBoundary);

					for (Player player : setPlayers) {
						if (!player.getWorld().equals(WORLD))
							continue;

						double distance = player.getLocation().distance(rgCenter);
						if (distance <= rgBoundary)
							update(player, updateRegion);
					}
				}
			}
		});
	}

	public void update(Player player, String region) {

		if (!service.hasStarted(player)) return;
		AeveonProjectUser user = service.get(player);

		// Any Ship Color Region
		if (region.contains("shipcolor")) {
			Material concreteType = ColorType.of(user.getShipColor()).getConcrete();
			if (concreteType == null)
				concreteType = Material.BLACK_CONCRETE;

			List<Block> blocks = WEUtils.getBlocks(WGUtils.getRegion(AeveonProject.getShipColorRegion(region)));

			for (Block block : blocks) {
				if (block.getType().equals(Material.WHITE_CONCRETE))
					user.getPlayer().sendBlockChange(block.getLocation(), concreteType.createBlockData());
			}
		}

		// Any Docking Ports Region
		if (region.contains("dockingport")) {
			List<Block> blocks = WEUtils.getBlocks(WGUtils.getRegion(region));
			for (Block block : blocks) {
				if (block.getType().equals(Material.WATER))
					player.sendBlockChange(block.getLocation(), Material.AIR.createBlockData());
			}
		}
	}

	@EventHandler
	public void onEnterRegion_Update(RegionEnteredEvent event) {
		Player player = event.getPlayer();
		if (!isInWorld(player)) return;

		String id = event.getRegion().getId();
		for (APSetType APSetType : APSetType.values()) {
			APSet set = APSetType.get();
			if (id.equalsIgnoreCase(set.getRegion())) {
				update(player, set);
				return;
			}
		}

	}

	public void update(Player player, APSet set) {
		for (String updateRegion : set.getUpdateRegions()) {
			update(player, updateRegion);
		}
	}
}

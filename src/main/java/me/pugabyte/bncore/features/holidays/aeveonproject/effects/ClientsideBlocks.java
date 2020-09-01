package me.pugabyte.bncore.features.holidays.aeveonproject.effects;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.sk89q.worldedit.regions.Region;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.aeveonproject.APUtils;
import me.pugabyte.bncore.features.holidays.aeveonproject.sets.APSet;
import me.pugabyte.bncore.features.holidays.aeveonproject.sets.APSetType;
import me.pugabyte.bncore.models.aeveonproject.AeveonProjectService;
import me.pugabyte.bncore.models.aeveonproject.AeveonProjectUser;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Collection;
import java.util.List;

import static me.pugabyte.bncore.features.holidays.aeveonproject.AeveonProject.*;

public class ClientsideBlocks implements Listener {
	AeveonProjectService service = new AeveonProjectService();
	private final int minBoundary = 20;
	private final int maxBoundary = 40;

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

	@EventHandler
	public void onEnterRegion(RegionEnteredEvent event) {
		Player player = event.getPlayer();
		if (!APUtils.isInWorld(player)) return;

		APSet set = APSetType.getFromRegion(event.getRegion());
		if (set != null)
			update(player, set);
	}

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		if (!APUtils.isInWorld(player)) return;

		APSet set = APSetType.getFromLocation(player.getLocation());
		if (set != null)
			update(player, set);
	}

	@EventHandler
	public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		if (!APUtils.isInWorld(player)) return;

		APSet set = APSetType.getFromLocation(player.getLocation());
		if (set != null)
			update(player, set);
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (!APUtils.isInWorld(event.getTo()))
			return;

		APSet set = APSetType.getFromLocation(event.getTo());
		if (set != null)
			update(event.getPlayer(), set);
	}

	public void update(Player player, APSet set) {
		for (String updateRegion : set.getUpdateRegions()) {
			update(player, updateRegion);
		}
	}

	public void update(Player player, String region) {

		if (!service.hasStarted(player)) return;
		AeveonProjectUser user = service.get(player);

		// Any Ship Color Region
		if (region.contains("shipcolor")) {
			Color shipColor = user.getShipColor();
			Material concreteType = Material.BLACK_CONCRETE;
			if (shipColor != null) {
				Material concrete = ColorType.of(shipColor).getConcrete();
				if (concrete != null)
					concreteType = concrete;
			}

			List<Block> blocks = WEUtils.getBlocks(WGUtils.getRegion(APUtils.getShipColorRegion(region)));

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
}

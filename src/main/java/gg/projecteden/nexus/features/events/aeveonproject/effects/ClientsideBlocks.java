package gg.projecteden.nexus.features.events.aeveonproject.effects;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.aeveonproject.APUtils;
import gg.projecteden.nexus.features.events.aeveonproject.AeveonProject;
import gg.projecteden.nexus.features.events.aeveonproject.sets.APSet;
import gg.projecteden.nexus.features.events.aeveonproject.sets.APSetType;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.models.aeveonproject.AeveonProjectService;
import gg.projecteden.nexus.models.aeveonproject.AeveonProjectUser;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.Distance;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Collection;
import java.util.List;

public class ClientsideBlocks implements Listener {
	private static final AeveonProjectService service = new AeveonProjectService();
	private final int minBoundary = 20;
	private final int maxBoundary = 40;

	public ClientsideBlocks() {
		Nexus.registerListener(this);

		updateTask();
	}

	private void updateTask() {
		Tasks.repeat(0, TickTime.SECOND.x(2), () -> {
			if (APUtils.getPlayersInAPWorld() == 0)
				return;

			for (APSetType APSetType : APSetType.values()) {
				APSet set = APSetType.get();
				List<String> updateRegions = set.getUpdateRegions();
				if (updateRegions == null)
					continue;

				Collection<Player> setPlayers = APUtils.getPlayersInSet(set);
				if (setPlayers.size() == 0)
					continue;

				for (String updateRegion : updateRegions) {
					Region region = AeveonProject.worldguard().getRegion(updateRegion);
					Location rgCenter = AeveonProject.worldedit().toLocation(region.getCenter());
					double rgBoundary = Math.max(Math.min(Math.max(region.getWidth(), region.getLength()), maxBoundary), minBoundary);

					for (Player player : setPlayers) {
						if (!APUtils.isInWorld(player))
							continue;

						if (Distance.distance(player, rgCenter).lte(rgBoundary))
							update(player, updateRegion);
					}
				}
			}
		});
	}

	@EventHandler
	public void onEnterRegion(PlayerEnteredRegionEvent event) {
		Player player = event.getPlayer();
		if (!APUtils.isInWorld(player)) return;

		update(player, event.getRegion());
	}

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		if (!APUtils.isInWorld(player)) return;

		update(player);
	}

	@EventHandler
	public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		if (!APUtils.isInWorld(player)) return;

		update(player);
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (!APUtils.isInWorld(event.getTo()))
			return;

		update(event.getPlayer(), event.getTo());
	}

	//

	public static void update(Player player, ProtectedRegion region) {
		APSet set = APSetType.getFromRegion(region);
		if (set != null)
			update(player, set);
	}

	public static void update(Player player) {
		update(player, player.getLocation());
	}

	public static void update(Player player, Location location) {
		APSet set = APSetType.getFromLocation(location);
		if (set != null)
			update(player, set);
	}

	//

	public static void update(Player player, APSet set) {
		for (String updateRegion : set.getUpdateRegions()) {
			update(player, updateRegion);
		}
	}

	public static void update(Player player, String region) {
		if (!service.hasStarted(player)) return;
		AeveonProjectUser user = service.get(player);

		// Any Ship Color Region
		if (region.contains("shipcolor")) {
			Color shipColor = user.getShipColor();
			ColorType colorType;

			Material concreteType = Material.BLACK_CONCRETE;
			Material bedType = Material.BLACK_BED;

			if (shipColor != null) {
				colorType = ColorType.of(shipColor);

				if (colorType != null) {
					concreteType = colorType.getConcrete();
					bedType = colorType.getBed();
				}
			}

			List<Block> blocks = AeveonProject.worldedit().getBlocks(AeveonProject.worldguard().getRegion(APUtils.getShipColorRegion(region)));

			for (Block block : blocks) {
				if (block.getType().equals(Material.WHITE_CONCRETE))
					user.getOnlinePlayer().sendBlockChange(block.getLocation(), concreteType.createBlockData());

				else if (block.getType().equals(Material.WHITE_BED)) {
					BlockData blockData = bedType.createBlockData();

					Bed newBed = (Bed) blockData;
					Bed oldBed = (Bed) block.getBlockData();

					if (oldBed.getPart().equals(Bed.Part.HEAD))
						newBed.setPart(Bed.Part.HEAD);
					else
						newBed.setPart(Bed.Part.FOOT);

					newBed.setFacing(oldBed.getFacing());

					blockData = newBed;
					user.getOnlinePlayer().sendBlockChange(block.getLocation(), blockData);
				}
			}
		}

		// Any Docking Ports Region
		if (region.contains("dockingport") || region.contains("vent_door")) {
			List<Block> blocks = AeveonProject.worldedit().getBlocks(AeveonProject.worldguard().getRegion(region));
			for (Block block : blocks) {
				if (block.getType().equals(Material.WATER))
					player.sendBlockChange(block.getLocation(), Material.AIR.createBlockData());
			}
		}
	}
}

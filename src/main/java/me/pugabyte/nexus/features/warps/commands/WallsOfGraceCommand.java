package me.pugabyte.nexus.features.warps.commands;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.commands.staff.WorldGuardEditCommand;
import me.pugabyte.nexus.features.menus.MenuUtils.ConfirmationMenu;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.wallsofgrace.WallsOfGrace;
import me.pugabyte.nexus.models.wallsofgrace.WallsOfGraceService;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.WorldGuardUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

@Aliases("wog")
@NoArgsConstructor
public class WallsOfGraceCommand extends CustomCommand implements Listener {
	private final WallsOfGraceService service = new WallsOfGraceService();
	private static final String PREFIX = StringUtils.getPrefix("WallsOfGrace");

	public WallsOfGraceCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void wog() {
		runCommand("warp wallsofgrace");
	}

	@Path("removesigns")
	void removeSigns() {
		WallsOfGrace wallsOfGrace = service.get(event.getPlayer());
		if (wallsOfGrace.get(1) == null && wallsOfGrace.get(2) == null)
			error("You have not created any signs");

		ConfirmationMenu.builder()
				.onConfirm(e -> {
					wallsOfGrace.get(1).getBlock().setType(Material.AIR);
					wallsOfGrace.get(2).getBlock().setType(Material.AIR);
					wallsOfGrace.set(1, null);
					wallsOfGrace.set(2, null);
					service.save(wallsOfGrace);
					send(PREFIX + "Removed signs");
				})
				.open(player());
	}

	@Path("removesign <id>")
	void removeSign(int id) {
		WallsOfGrace wallsOfGrace = service.get(event.getPlayer());
		Location location = wallsOfGrace.get(id);
		if (location == null)
			error("You have not created that sign");

		ConfirmationMenu.builder()
				.onConfirm(e -> {
					location.getBlock().setType(Material.AIR);
					wallsOfGrace.set(id, null);
					service.save(wallsOfGrace);
					send(PREFIX + "Removed sign #" + id);
				})
				.open(player());
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		WorldGuardUtils WGUtils = new WorldGuardUtils(event.getBlock());
		if (WGUtils.getRegionsLikeAt("wallsofgrace", event.getBlock().getLocation()).size() == 0) return;

		if (!MaterialTag.SIGNS.isTagged(event.getBlock().getType())) {
			if (!event.getPlayer().hasPermission(WorldGuardEditCommand.getPermission()))
				event.setCancelled(true);
			return;
		}

		final WallsOfGraceService service = new WallsOfGraceService();
		WallsOfGrace wallsOfGrace = service.get(event.getPlayer());
		Location loc1 = wallsOfGrace.get(1);
		Location loc2 = wallsOfGrace.get(2);

		if (loc1 != null && loc1.equals(event.getBlock().getLocation())) {
			wallsOfGrace.set(1, null);
			send(event.getPlayer(), PREFIX + "Removed sign #1");
		} else if (loc2 != null && loc2.equals(event.getBlock().getLocation())) {
			wallsOfGrace.set(2, null);
			send(event.getPlayer(), PREFIX + "Removed sign #2");
		} else {
			event.setCancelled(true);
			return;
		}

		service.save(wallsOfGrace);
	}

	@EventHandler
	public void onBucketEmpty(PlayerBucketEmptyEvent event) {
		WorldGuardUtils WGUtils = new WorldGuardUtils(event.getBlock());
		if (WGUtils.getRegionsLikeAt("wallsofgrace", event.getBlock().getLocation()).size() == 0)
			return;

		if (event.getPlayer().hasPermission(WorldGuardEditCommand.getPermission()))
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		WorldGuardUtils WGUtils = new WorldGuardUtils(event.getBlock());
		if (WGUtils.getRegionsLikeAt("wallsofgrace", event.getBlock().getLocation()).size() == 0) return;

		if (MaterialTag.SIGNS.isTagged(event.getBlock().getType())) {
			// Sign must be placed on concrete
			if (!MaterialTag.CONCRETES.isTagged(event.getBlockAgainst().getType())) {
				event.setCancelled(true);
				send(event.getPlayer(), "&cYou must place your sign on concrete");
				return;
			}
		} else {
			if (!event.getPlayer().hasPermission(WorldGuardEditCommand.getPermission()))
				event.setCancelled(true);
			return;
		}

		final WallsOfGraceService service = new WallsOfGraceService();
		WallsOfGrace wallsOfGrace = service.get(event.getPlayer());
		Location loc1 = wallsOfGrace.get(1);
		Location loc2 = wallsOfGrace.get(2);

		if (loc1 != null && loc2 != null) {
			event.getPlayer().closeInventory();
			event.setCancelled(true);
			send(event.getPlayer(), PREFIX + "You can only place 2 signs. " +
					"Remove your previous signs with &c/wallsofgrace removesign <id>");
			return;
		}

		if (loc1 == null) {
			wallsOfGrace.set(1, event.getBlock().getLocation());
			send(event.getPlayer(), PREFIX + "Placed sign #1");
		} else {
			wallsOfGrace.set(2, event.getBlock().getLocation());
			send(event.getPlayer(), PREFIX + "Placed sign #2");
		}

		service.save(wallsOfGrace);
	}

}

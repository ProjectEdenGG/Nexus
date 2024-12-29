package gg.projecteden.nexus.features.warps.commands;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.nexus.features.commands.staff.WorldGuardEditCommand;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.*;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.wallsofgrace.WallsOfGrace;
import gg.projecteden.nexus.models.wallsofgrace.WallsOfGraceService;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

@HideFromWiki // TODO
@Disabled
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

	@Path("set <player>")
	@Permission(Group.ADMIN)
	void set(WallsOfGrace player) {
		if (player.get(1) == null)
			player.set(1, getTargetSignRequired().getLocation());
		else if (player.get(2) == null)
			player.set(2, getTargetSignRequired().getLocation());
		else
			error(player.getName() + " already has both signs set");

		new WallsOfGraceService().save(player);
		send(PREFIX + "Saved");
	}

	@Path("removeSigns [player]")
	void removeSigns(@Arg(value = "self", permission = Group.STAFF) WallsOfGrace wallsOfGrace) {
		if (wallsOfGrace.get(1) == null && wallsOfGrace.get(2) == null)
			error((isSelf(wallsOfGrace) ? "You have" : wallsOfGrace.getNickname() + " has") + " not created any signs");

		ConfirmationMenu.builder()
			.onConfirm(e -> {
				removeSign(wallsOfGrace, 1);
				removeSign(wallsOfGrace, 2);
				service.save(wallsOfGrace);
				send(PREFIX + "Removed signs");
			})
			.open(player());
	}

	@Path("removeSign <id> [player]")
	void removeSign(int id, @Arg(value = "self", permission = Group.STAFF) WallsOfGrace wallsOfGrace) {
		if (wallsOfGrace.get(id) == null)
			error((isSelf(wallsOfGrace) ? "You have" : wallsOfGrace.getNickname() + " has") + " not created that sign");

		ConfirmationMenu.builder()
			.onConfirm(e -> {
				removeSign(wallsOfGrace, id);
				service.save(wallsOfGrace);
				send(PREFIX + "Removed sign #" + id);
			})
			.open(player());
	}

	private void removeSign(WallsOfGrace wallsOfGrace, int i) {
		// Give the executor the sign, instead of the owner
		PlayerUtils.giveItem(player(), wallsOfGrace.get(i).getBlock().getType());
		wallsOfGrace.get(i).getBlock().setType(Material.AIR);
		wallsOfGrace.set(i, null);
	}

	private boolean isInRegion(Block block) {
		return !new WorldGuardUtils(block).getRegionsLikeAt("wallsofgrace", block.getLocation()).isEmpty();
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (!isInRegion(event.getBlock()))
			return;

		if (!MaterialTag.SIGNS.isTagged(event.getBlock().getType())) {
			if (!WorldGuardEditCommand.canWorldGuardEdit(event.getPlayer()))
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
		if (!isInRegion(event.getBlock()))
			return;

		if (WorldGuardEditCommand.canWorldGuardEdit(event.getPlayer()))
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (!isInRegion(event.getBlock()))
			return;

		if (!MaterialTag.SIGNS.isTagged(event.getBlock().getType())) {
			if (!WorldGuardEditCommand.canWorldGuardEdit(event.getPlayer()))
				event.setCancelled(true);
			return;
		}

		if (!MaterialTag.CONCRETES.isTagged(event.getBlockAgainst().getType())) {
			event.setCancelled(true);
			send(event.getPlayer(), "&cYou must place your sign on concrete");
			return;
		}

		if (Rank.of(event.getPlayer()).lt(Rank.TRUSTED)) {
			event.setCancelled(true);
			send(event.getPlayer(), "&cYou must be Trusted rank or higher to place signs");
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

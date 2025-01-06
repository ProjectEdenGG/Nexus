package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.Distance;
import gg.projecteden.nexus.utils.GlowUtils;
import gg.projecteden.nexus.utils.GlowUtils.GlowColor;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.PlayerMovementUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collections;

@Permission(Group.STAFF)
public class NearestBlockCommand extends CustomCommand {

	public NearestBlockCommand(CommandEvent event) {
		super(event);
	}

	@Path("<material> <radius>")
	@Description("Find the nearest block of a certain type")
	void nearestBlock(Material material, Integer radius) {
		if (radius > 100) {
			send(PREFIX + "Max radius is 100, limiting radius");
			radius = 100;
		}

		int finalRadius = radius;
		Tasks.async(() -> {
			Location location = location();
			double minDistance = finalRadius;
			Block nearestBlock = null;
			for (int x = -finalRadius; x <= finalRadius; x++) {
				for (int z = -finalRadius; z <= finalRadius; z++) {
					for (int y = -finalRadius; y < finalRadius; y++) {
						Block tempBlock = location.getBlock().getRelative(x, y, z);
						if (tempBlock.getType().equals(material)) {
							final Distance tempDistance = distanceTo(tempBlock);
							if (tempDistance.lt(minDistance)) {
								minDistance = tempDistance.get();
								nearestBlock = tempBlock;
							}
						}
					}
				}
			}

			Block block = nearestBlock;
			Tasks.sync(() -> {
				if (block != null) {
					Location blockLoc = LocationUtils.getCenteredLocation(block.getLocation());
					World blockWorld = blockLoc.getWorld();
					FallingBlock fallingBlock = blockWorld.spawnFallingBlock(blockLoc, block.getType().createBlockData());
					fallingBlock.setDropItem(false);
					fallingBlock.setGravity(false);
					fallingBlock.setInvulnerable(true);
					fallingBlock.setVelocity(new Vector(0, 0, 0));

					PlayerMovementUtils.lookAt(player(), block.getLocation());
					StringUtils.sendJsonLocation(PREFIX + "&3&l[Click to Teleport]", block.getLocation(), player());

					GlowUtils.GlowTask.builder()
							.duration(TickTime.SECOND.x(10))
							.entity(fallingBlock)
							.color(GlowColor.RED)
							.viewers(Collections.singletonList(player()))
							.onComplete(() -> {
								fallingBlock.remove();
								for (Player player : OnlinePlayers.where().world(blockWorld).get())
									player.sendBlockChange(blockLoc, block.getType().createBlockData());
							})
							.start();

				} else
					error(player(), StringUtils.camelCase(material.toString()) + " not found");
			});
		});
	}
}

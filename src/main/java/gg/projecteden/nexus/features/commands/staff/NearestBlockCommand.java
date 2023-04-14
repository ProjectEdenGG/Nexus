package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.utils.Distance;
import gg.projecteden.nexus.utils.GlowUtils;
import gg.projecteden.nexus.utils.GlowUtils.GlowColor;
import gg.projecteden.nexus.utils.LocationUtils;
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

import static gg.projecteden.nexus.utils.LocationUtils.getCenteredLocation;

@Permission(Group.STAFF)
public class NearestBlockCommand extends CustomCommand {

	public NearestBlockCommand(CommandEvent event) {
		super(event);
	}

	@Async
	@NoLiterals
	@Description("Find the nearest block of a certain type")
	void nearestBlock(Material material, int radius) {
		if (radius > 100) {
			send(PREFIX + "Max radius is 100, limiting radius");
			radius = 100;
		}

		Location location = location();
		double minDistance = radius;
		Block nearestBlock = null;
		for (int x = -radius; x <= radius; x++) {
			for (int z = -radius; z <= radius; z++) {
				for (int y = -radius; y < radius; y++) {
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
				Location blockLoc = getCenteredLocation(block.getLocation());
				World blockWorld = blockLoc.getWorld();
				FallingBlock fallingBlock = blockWorld.spawnFallingBlock(blockLoc, block.getType().createBlockData());
				fallingBlock.setDropItem(false);
				fallingBlock.setGravity(false);
				fallingBlock.setInvulnerable(true);
				fallingBlock.setVelocity(new Vector(0, 0, 0));

				LocationUtils.lookAt(player(), block.getLocation());
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
	}
}

package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.inventivetalent.glow.GlowAPI;

@Permission("group.staff")
public class NearestBlockCommand extends CustomCommand {

	//TODO: for player head in direction of the found block

	public NearestBlockCommand(CommandEvent event) {
		super(event);
	}

	@ConverterFor(Material.class)
	Material convertToMaterial(String value) {
		Material material = Material.matchMaterial(value);
		if (material == null)
			throw new InvalidInputException("Material from " + value + " not found");
		return material;
	}

	@Path("[material] [radius]")
	void nearestBlock(@Arg Material material, @Arg Integer radius) {
		if (material == null || radius == null) {
			error("/nearestblock <material> <radius>");
			return;
		}

		Utils.async(() -> {
			Location location = player().getLocation();
			double minDistance = radius;
			Block nearestBlock = null;
			for (int x = -radius; x <= radius; x++) {
				for (int z = -radius; z <= radius; z++) {
					for (int y = -radius; y < radius; y++) {
						Block tempBlock = location.getBlock().getRelative(x, y, z);
						if (tempBlock.getType().equals(material)) {
							double tempDistance = location.distance(tempBlock.getLocation());
							if (tempDistance < minDistance) {
								minDistance = tempDistance;
								nearestBlock = tempBlock;
							}
						}
					}
				}
			}

			Block block = nearestBlock;
			Utils.sync(() -> {
				if (block != null) {
					Location blockLoc = Utils.getCenteredLocation(block.getLocation());
					World blockWorld = blockLoc.getWorld();
					FallingBlock fallingBlock = blockWorld.spawnFallingBlock(blockLoc, block.getType(), block.getData());
					fallingBlock.setDropItem(false);
					fallingBlock.setGravity(false);
					fallingBlock.setInvulnerable(true);

					GlowAPI.setGlowing(fallingBlock, GlowAPI.Color.RED, player());
					send(PREFIX + "Loc: " + block.getLocation());
					Utils.wait(10 * 20, () -> {
						fallingBlock.remove();
						Bukkit.getOnlinePlayers().stream().filter(player -> player.getWorld() == blockWorld).forEach(player -> {
							player.sendBlockChange(blockLoc, block.getType(), block.getData());
						});
					});
				} else
					send(PREFIX + Utils.camelCase(material.toString()) + " not found");
			});
		});
	}
}

package me.pugabyte.nexus.features.minigolf.models.blocks;

import me.pugabyte.nexus.features.minigolf.models.GolfBall;
import me.pugabyte.nexus.utils.LocationUtils;
import me.pugabyte.nexus.utils.MaterialTag;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.util.Vector;

import java.util.Set;

public class TeleportBlock extends ModifierBlock {
	@Override
	public void handleRoll(GolfBall golfBall) {
		golfBall.getUser().debug("&oon roll on teleport block");

		Block below = golfBall.getBlockBelow();
		Block under = below.getRelative(BlockFace.DOWN);

		if (!MaterialTag.SIGNS.isTagged(under.getType()))
			return;

		Sign sign = (Sign) under.getState();
		String line4 = sign.getLine(3);
		String[] split = line4.split(",");
		if (split.length == 3) {
			try {
				int x = Integer.parseInt(split[0]);
				int y = Integer.parseInt(split[1]);
				int z = Integer.parseInt(split[2]);
				Location newLoc = new Location(golfBall.getLocation().getWorld(), x, y, z);

				golfBall.setVelocity(new Vector(0, 0, 0));
				golfBall.teleport(LocationUtils.getCenteredLocation(newLoc));
				golfBall.setGravity(true);
			} catch (Exception ignored) {

			}
		}

	}

	@Override
	public Set<Material> getMaterials() {
		return Set.of(Material.DISPENSER);
	}
}

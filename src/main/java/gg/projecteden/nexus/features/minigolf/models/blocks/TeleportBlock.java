package gg.projecteden.nexus.features.minigolf.models.blocks;

import gg.projecteden.nexus.features.minigolf.models.GolfBall;
import gg.projecteden.nexus.utils.MaterialTag;
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
		rollDebug(golfBall);

		Block below = golfBall.getBlockBelow();
		Block under = below.getRelative(BlockFace.DOWN);

		if (!MaterialTag.SIGNS.isTagged(under.getType()))
			return;

		Sign sign = (Sign) under.getState();

		try {
			double x = Double.parseDouble(sign.getLine(1));
			double y = Double.parseDouble(sign.getLine(2));
			double z = Double.parseDouble(sign.getLine(3));
			Location newLoc = new Location(golfBall.getLocation().getWorld(), x, y, z);

			golfBall.setVelocity(new Vector(0, 0, 0));
			golfBall.teleportAsync(newLoc);
			golfBall.setGravity(true);
		} catch (Exception ignored) {
		}

	}

	@Override
	public Set<Material> getMaterials() {
		return Set.of(Material.DISPENSER);
	}
}

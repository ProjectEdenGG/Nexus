package gg.projecteden.nexus.features.minigolf.models.blocks;

import gg.projecteden.nexus.features.minigolf.models.GolfBall;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class TNTBlock extends ModifierBlock {

	@Override
	public void handleRoll(GolfBall golfBall) {
		Vector velocity = golfBall.getVelocity();
		Vector randomDir = explode(golfBall);
		velocity.add(randomDir);
		golfBall.setVelocity(velocity);

	}

	@Override
	public void handleBounce(GolfBall golfBall, Block block, BlockFace blockFace) {
		Vector velocity = golfBall.getVelocity();
		Vector randomDir = explode(golfBall);
		velocity.multiply(-1).add(randomDir);
		golfBall.setVelocity(velocity);

	}

	@Override
	public Set<Material> getMaterials() {
		return Set.of(Material.TNT);
	}

	@NotNull
	private static Vector explode(GolfBall golfBall) {
		new SoundBuilder(Sound.ENTITY_GENERIC_EXPLODE).location(golfBall.getLocation()).volume(0.5).play();
		return new Vector(RandomUtils.randomDouble(-1, 1), RandomUtils.randomDouble(0.5, 1), RandomUtils.randomDouble(-1, 1));
	}
}

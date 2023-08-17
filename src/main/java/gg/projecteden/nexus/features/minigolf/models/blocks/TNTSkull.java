package gg.projecteden.nexus.features.minigolf.models.blocks;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.minigolf.models.GolfBall;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Set;

public class TNTSkull extends ModifierBlock {

	@Override
	public void handleRoll(GolfBall golfBall) {
		Vector velocity = golfBall.getVelocity();

		Vector randomDir = new Vector(RandomUtils.randomDouble(-0.5, 0.5), RandomUtils.randomDouble(0.2, 0.6), RandomUtils.randomDouble(-0.5, 0.5));
		velocity.add(randomDir);

		golfBall.setVelocity(velocity);
		new SoundBuilder(Sound.ENTITY_GENERIC_EXPLODE).location(golfBall.getLocation()).volume(0.5).play();
	}

	@Override
	public void handleBounce(GolfBall golfBall, Block block, BlockFace blockFace) {
		Vector velocity = golfBall.getVelocity();

		Vector randomDir = new Vector(RandomUtils.randomDouble(-0.5, 0.5), RandomUtils.randomDouble(0.2, 0.6), RandomUtils.randomDouble(-0.5, 0.5));
		velocity.multiply(-1).add(randomDir);

		golfBall.setVelocity(velocity);
		new SoundBuilder(Sound.ENTITY_GENERIC_EXPLODE).location(golfBall.getLocation()).volume(0.5).play();
	}

	@Override
	public Set<Material> getMaterials() {
		return Set.of(Material.PLAYER_HEAD, Material.PLAYER_WALL_HEAD);
	}

	@Override
	public boolean additionalContext(Block block) {
		ItemStack itemStack = ItemUtils.getItem(block);
		return "229".equals(Nexus.getHeadAPI().getItemID(itemStack));
	}
}

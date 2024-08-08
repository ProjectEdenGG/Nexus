package gg.projecteden.nexus.features.minigolf.models.blocks;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.minigolf.models.GolfBall;
import gg.projecteden.nexus.features.minigolf.models.events.MiniGolfBallSinkEvent;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.FireworkLauncher;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Snowball;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.Set;

public class HoleBlock extends ModifierBlock {

	@Override
	public ColorType getDebugDotColor() {
		return ColorType.PURPLE;
	}

	@Override
	public void handleRoll(GolfBall golfBall, Block below) {
		rollDebug(golfBall);

		Snowball snowball = golfBall.getSnowball();
		Vector velocity = golfBall.getVelocity();
		if (velocity.getY() >= 0 && velocity.length() > 0.34)
			return;


		if (!golfBall.isInBounds()) {
			golfBall.respawn();
			return;
		}

		MiniGolfBallSinkEvent ballSinkEvent = new MiniGolfBallSinkEvent(golfBall, golfBall.getHoleRegion(), golfBall.getStrokes(), golfBall.getPar());
		if (!ballSinkEvent.callEvent())
			return;

		// Halt velocity
		snowball.setVelocity(new Vector(0, snowball.getVelocity().getY(), 0));

		// Pickup snowball
		golfBall.pickup();

		// Spawn firework
		Tasks.wait(TickTime.TICK, () -> new FireworkLauncher(snowball.getLocation())
			.power(0)
			.detonateAfter(TickTime.TICK.x(2))
			.type(Type.BURST)
				.colors(golfBall.getUser().getGolfBallColor().getFireworkColors())
			.fadeColors(Collections.singletonList(Color.WHITE))
			.launch());

		// Send Message
		ballSinkEvent.sendScore();

		// Reset Variables
		golfBall.setHoleRegion(null);
		golfBall.setStrokes(0);
	}

	@Override
	public void handleBounce(GolfBall golfBall, Block block, BlockFace blockFace) {
		if (blockFace.equals(BlockFace.UP)) {
			handleRoll(golfBall, block);
			return;
		}

		super.handleBounce(golfBall, block, blockFace);
	}

	@Override
	public Set<Material> getMaterials() {
		return Set.of(Material.CAULDRON);
	}
}

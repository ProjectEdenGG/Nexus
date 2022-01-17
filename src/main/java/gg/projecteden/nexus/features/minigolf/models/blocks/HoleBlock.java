package gg.projecteden.nexus.features.minigolf.models.blocks;

import gg.projecteden.nexus.features.minigolf.models.GolfBall;
import gg.projecteden.nexus.features.minigolf.models.events.MiniGolfBallSinkEvent;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.FireworkLauncher;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Snowball;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.Set;

public class HoleBlock extends ModifierBlock {

	@Override
	public void handleRoll(GolfBall golfBall) {
		golfBall.getUser().debug("&oon roll on hole block");

		Snowball snowball = golfBall.getSnowball();
		Vector velocity = golfBall.getVelocity();
		if (velocity.getY() >= 0 && velocity.length() > 0.34)
			return;

		if (!golfBall.isInBounds()) // TODO: Kill snowball?
			return;

		MiniGolfBallSinkEvent ballSinkEvent = new MiniGolfBallSinkEvent(golfBall, golfBall.getHoleRegion(), golfBall.getStrokes(), golfBall.getPar());
		if (!ballSinkEvent.callEvent())
			return;

		// Halt velocity
		snowball.setVelocity(new Vector(0, snowball.getVelocity().getY(), 0));

		// Remove snowball
		golfBall.remove();

		// Spawn firework
		Tasks.wait(TickTime.TICK, () -> new FireworkLauncher(snowball.getLocation())
			.power(0)
			.detonateAfter(TickTime.TICK.x(2))
			.type(Type.BURST)
			.colors(Collections.singletonList(ColorType.RED.getBukkitColor()))
			.fadeColors(Collections.singletonList(Color.WHITE))
			.launch());

		// Send Message
		ballSinkEvent.sendScore();

		// Reset Variables
		golfBall.setHoleRegion(null);
		golfBall.setStrokes(0);
	}

	@Override
	public void handleBounce(GolfBall golfBall, BlockFace blockFace) {
		if (blockFace.equals(BlockFace.UP))
			handleRoll(golfBall);

		super.handleBounce(golfBall, blockFace);
	}

	@Override
	public Set<Material> getMaterials() {
		return Set.of(Material.CAULDRON);
	}
}

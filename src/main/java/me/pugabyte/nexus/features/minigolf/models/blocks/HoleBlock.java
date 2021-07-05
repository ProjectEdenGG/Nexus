package me.pugabyte.nexus.features.minigolf.models.blocks;

import eden.utils.TimeUtils.Time;
import me.pugabyte.nexus.features.minigolf.MiniGolfUtils;
import me.pugabyte.nexus.features.minigolf.models.GolfBall;
import me.pugabyte.nexus.features.minigolf.models.events.MiniGolfBallSinkEvent;
import me.pugabyte.nexus.utils.ColorType;
import me.pugabyte.nexus.utils.FireworkLauncher;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.entity.Snowball;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.Set;

public class HoleBlock extends ModifierBlock {

	@Override
	public void handle(GolfBall golfBall) {
		golfBall.getUser().debug("on hole block");

		Snowball ball = golfBall.getBall();
		Vector vel = golfBall.getVelocity();
		if (vel.getY() >= 0 && vel.length() > 0.34)
			return;

		if (!golfBall.isInBounds()) // TODO: Kill ball?
			return;

		MiniGolfBallSinkEvent ballSinkEvent = new MiniGolfBallSinkEvent(golfBall, golfBall.getStrokes(), golfBall.getPar());
		if (!ballSinkEvent.callEvent())
			return;

		// Halt velocity
		ball.setVelocity(new Vector(0, ball.getVelocity().getY(), 0));

		// Remove ball
		MiniGolfUtils.removeBall(golfBall);

		// Spawn firework
		Tasks.wait(Time.TICK, () -> new FireworkLauncher(ball.getLocation())
			.power(0)
			.detonateAfter(Time.TICK.x(2))
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
	public Set<Material> getMaterials() {
		return Set.of(Material.CAULDRON);
	}
}

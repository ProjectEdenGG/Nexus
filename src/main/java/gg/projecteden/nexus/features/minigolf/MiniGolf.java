package gg.projecteden.nexus.features.minigolf;

import gg.projecteden.annotations.Environments;
import gg.projecteden.nexus.features.minigolf.listeners.InteractListener;
import gg.projecteden.nexus.features.minigolf.listeners.ProjectileListener;
import gg.projecteden.nexus.features.minigolf.models.GolfBall;
import gg.projecteden.nexus.features.minigolf.models.MiniGolfUser;
import gg.projecteden.nexus.features.minigolf.models.blocks.ModifierBlock;
import gg.projecteden.nexus.features.minigolf.models.blocks.ModifierBlockType;
import gg.projecteden.nexus.features.minigolf.models.events.MiniGolfBallModifierBlockEvent;
import gg.projecteden.nexus.features.minigolf.models.events.MiniGolfBallMoveEvent;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.Env;
import gg.projecteden.utils.TimeUtils.Time;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Snowball;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Environments({Env.TEST, Env.DEV})
public class MiniGolf extends Feature {
	@Getter
	private static final double maxVelocity = 1.5;
	@Getter
	private static final double minVelocity = 0.01;
	@Getter
	private static final double floorOffset = 0.05;
	@Getter
	private static final Set<MiniGolfUser> users = new HashSet<>();
	@Getter
	private static final Set<GolfBall> golfBalls = new HashSet<>();
	public static final String holeRegionRegex = ".*minigolf_hole_[\\d]+$";


	@Override
	public void onStart() {
		new InteractListener();
		new ProjectileListener();
		miniGolfTask();
	}

	@Override
	public void onStop() {
		for (GolfBall golfBall : new ArrayList<>(golfBalls)) {
			if (golfBall.isAlive()) {
				golfBall.getSnowball().remove();
			}
		}
	}

	public static void join(MiniGolfUser user) {
		getUsers().add(user);
	}

	public static void quit(MiniGolfUser user) {
		if (user.getGolfBall() != null) {
			user.getGolfBall().remove();
			user.setGolfBall(null);
		}

		getGolfBalls().remove(user.getGolfBall());
		getUsers().remove(user);
	}

	private void miniGolfTask() {
		Tasks.repeat(0, Time.TICK, () -> {
			if (golfBalls.isEmpty())
				return;

			for (GolfBall golfBall : new ArrayList<>(golfBalls)) {
				Snowball ball = golfBall.getSnowball();
				if (ball == null)
					continue;

				if (!ball.isValid()) {
					golfBall.remove();
					continue;
				}

//				Location location = ball.getLocation();
//				if (golfBall.getLastLocation().equals(location))
//					continue;

				MiniGolfBallMoveEvent ballMoveEvent = new MiniGolfBallMoveEvent(golfBall, golfBall.getLastLocation(), ball.getLocation());
				if (!ballMoveEvent.callEvent()) {
					ball.teleportAsync(golfBall.getLastLocation());
					ball.setVelocity(ball.getVelocity());
				}

				Block below = golfBall.getBlockBelow();
				Material belowType = below.getType();

				for (ModifierBlockType modifierBlockType : ModifierBlockType.values()) {
					ModifierBlock modifierBlock = modifierBlockType.getModifierBlock();
					if (modifierBlockType.equals(ModifierBlockType.DEFAULT) || modifierBlock.getMaterials().contains(belowType)) {
						MiniGolfBallModifierBlockEvent modifierBlockEvent = new MiniGolfBallModifierBlockEvent(golfBall, modifierBlockType);
						if (modifierBlockEvent.callEvent()) {
							modifierBlock.handleRoll(golfBall);
							break;
						}
					}
				}
			}
		});
	}

}

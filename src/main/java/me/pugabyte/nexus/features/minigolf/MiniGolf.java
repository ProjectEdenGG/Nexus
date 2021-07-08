package me.pugabyte.nexus.features.minigolf;

import eden.annotations.Environments;
import eden.utils.Env;
import eden.utils.TimeUtils.Time;
import lombok.Getter;
import me.pugabyte.nexus.features.minigolf.listeners.InteractListener;
import me.pugabyte.nexus.features.minigolf.listeners.ProjectileListener;
import me.pugabyte.nexus.features.minigolf.models.GolfBall;
import me.pugabyte.nexus.features.minigolf.models.MiniGolfUser;
import me.pugabyte.nexus.features.minigolf.models.blocks.ModifierBlock;
import me.pugabyte.nexus.features.minigolf.models.blocks.ModifierBlockType;
import me.pugabyte.nexus.features.minigolf.models.events.MiniGolfBallModifierBlockEvent;
import me.pugabyte.nexus.features.minigolf.models.events.MiniGolfBallMoveEvent;
import me.pugabyte.nexus.framework.features.Feature;
import me.pugabyte.nexus.utils.Tasks;
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
	public static final String holeRegionRegex = ".*minigolf_hole_[0-9]+$";


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
		user.getGolfBall().remove();
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
					ball.teleport(golfBall.getLastLocation());
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

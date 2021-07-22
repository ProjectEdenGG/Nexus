package gg.projecteden.nexus.features.ambience;

import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.Time;
import lombok.Getter;

public class Wind {
	@Getter
	private static boolean blowing = true;
	@Getter
	private static double direction;
	@Getter
	private static double X;
	@Getter
	private static double Z;

	private static final int blowingUpdateInterval = Time.MINUTE.x(5);
	private static final Runnable blowingUpdate = () -> {
		blowing = RandomUtils.chanceOf(25);
		Ambience.sendDebug("Blowing: " + blowing);
	};


	private static final int directionUpdateInterval = Time.MINUTE.get();
	private static final Runnable directionUpdate = () -> {
		direction = Math.random() * 2 * Math.PI;
		X = Math.sin(direction);
		Z = Math.cos(direction);
		Ambience.sendDebug("Wind dir: " + direction);
	};

	static {
		blowingUpdate.run();
		directionUpdate.run();

		Tasks.repeat(blowingUpdateInterval, blowingUpdateInterval, blowingUpdate);
		Tasks.repeat(directionUpdateInterval, directionUpdateInterval, directionUpdate);
	}
}

package me.pugabyte.nexus.features.ambience;

import eden.utils.TimeUtils.Time;
import lombok.Getter;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.Tasks;

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
	private static final Runnable blowingUpdate = () ->
		blowing = RandomUtils.chanceOf(25);

	private static final int directionUpdateInterval = Time.MINUTE.get();
	private static final Runnable directionUpdate = () -> {
		direction = Math.random() * 2 * Math.PI;
		X = Math.sin(direction);
		Z = Math.cos(direction);
	};

	static {
		blowingUpdate.run();
		directionUpdate.run();

		Tasks.repeat(blowingUpdateInterval, blowingUpdateInterval, blowingUpdate);
		Tasks.repeat(directionUpdateInterval, directionUpdateInterval, directionUpdate);
	}
}

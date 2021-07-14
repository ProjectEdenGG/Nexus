package me.pugabyte.nexus.features.ambience;

import eden.utils.TimeUtils.Time;
import lombok.Getter;
import me.pugabyte.nexus.utils.Tasks;

public class Wind {
	@Getter
	private static final boolean blowing = true;
	@Getter
	public static double direction = Math.random() * 2 * Math.PI;
	@Getter
	public static double X = Math.sin(direction);
	@Getter
	public static double Z = Math.cos(direction);

	static {
		// change wind direction
		Tasks.repeat(0, Time.MINUTE.x(1), () -> direction = Math.random() * 2 * Math.PI);
	}
}

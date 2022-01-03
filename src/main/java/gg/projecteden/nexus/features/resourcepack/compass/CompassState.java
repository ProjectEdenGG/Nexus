package gg.projecteden.nexus.features.resourcepack.compass;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import static gg.projecteden.nexus.utils.LocationUtils.toDegree;

@Getter
@AllArgsConstructor
public enum CompassState {
	COMPASS_00("鄀"),
	COMPASS_01("鄁"),
	COMPASS_02("鄂"),
	COMPASS_03("鄃"),
	COMPASS_04("鄄"),
	COMPASS_05("鄅"),
	COMPASS_06("鄆"),
	COMPASS_07("鄇"),
	COMPASS_08("鄈"),
	COMPASS_09("鄉"),
	COMPASS_10("鄊"),
	COMPASS_11("鄋"),
	COMPASS_12("鄌"),
	COMPASS_13("鄍"),
	COMPASS_14("鄎"),
	COMPASS_15("鄏"),
	COMPASS_16("鄐"),
	COMPASS_17("鄑"),
	COMPASS_18("鄒"),
	COMPASS_19("鄓"),
	COMPASS_20("鄔"),
	COMPASS_21("鄕"),
	COMPASS_22("鄖"),
	COMPASS_23("鄗"),
	COMPASS_24("鄘"),
	COMPASS_25("鄙"),
	COMPASS_26("鄚"),
	COMPASS_27("鄿"),
	COMPASS_28("鄛"),
	COMPASS_29("鄝"),
	COMPASS_30("鄞"),
	COMPASS_31("鄟"),
	;

	public static final String COMPASS_EMPTY = "邿";

	private final String character;

	public static CompassState of(Player player, Location objective) {
		Vector direction = player.getEyeLocation().toVector().subtract(objective.add(0.5, 0.5, 0.5).toVector()).normalize();
		final float heading = toDegree(Math.atan2(direction.getX(), direction.getZ()));

		float yaw = Location.normalizeYaw(player.getLocation().getYaw());

		int index = (int) ((yaw + heading) / (360d / 32));

		if (index < 0)
			index += 32;
		else if (index > 32)
			index -= 32;

		return values()[31 - index];
	}
}

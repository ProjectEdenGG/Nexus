package gg.projecteden.nexus.features.events.mobevents.types.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.World;

@Getter
@AllArgsConstructor
public enum DayPhase {
	DAY(0, 11999),
	NIGHT(12000, 23999),
	;

	private final int start;
	private final int end;

	public static DayPhase of(World world) {
		return at(world.getTime());
	}

	public static DayPhase at(long time) {
		for (DayPhase part : values())
			if (time >= part.getStart() && time <= part.getEnd())
				return part;
		return null;
	}
}

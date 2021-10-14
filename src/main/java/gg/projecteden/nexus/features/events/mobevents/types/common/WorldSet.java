package gg.projecteden.nexus.features.events.mobevents.types.common;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;

@EqualsAndHashCode
@RequiredArgsConstructor
public class WorldSet {
	private final World overworld;

	public World get(Dimension dimension) {
		return Bukkit.getWorld(overworld.getName() + dimension.getSuffix());
	}

	@Getter
	@AllArgsConstructor
	public enum Dimension {
		OVERWORLD(Environment.NORMAL, ""),
		NETHER(Environment.NETHER, "_nether"),
		END(Environment.THE_END, "_the_end"),
		;

		private final Environment dimension;
		private final String suffix;

		public static Dimension of(World world) {
			return of(world.getEnvironment());
		}

		public static Dimension of(Environment environment) {
			return switch (environment) {
				case NORMAL -> OVERWORLD;
				case NETHER -> NETHER;
				case THE_END -> END;
				default -> null;
			};
		}
	}

}

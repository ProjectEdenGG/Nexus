package gg.projecteden.nexus.models.blockorientation;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.common.utils.EnumUtils.IterableEnum;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@Entity(value = "block_orientation_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class BlockOrientationUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Logs logs = new Logs();
	private Stairs stairs = new Stairs();

	@Data
	@NoArgsConstructor
	public static class Logs {
		private boolean normal = true;
	}

	@Data
	@NoArgsConstructor
	public static class Stairs {
		private boolean enabled = false;
		private StairAction action = null;
		private StairDirection direction = null;
		private StairSlope slope = null;

		public enum StairAction {
			COPY, COPY_DIRECTION, COPY_SLOPE
		}

		@Getter
		@AllArgsConstructor
		public enum StairModification {
			DIRECTION(StairAction.COPY_DIRECTION),
			SLOPE(StairAction.COPY_DIRECTION),
			;

			private StairAction copyAction;
		}

		public enum StairDirection implements IterableEnum {
			NORTH, EAST, SOUTH, WEST;

			@Override
			public String toString() {
				return name().toLowerCase();
			}
		}

		@Getter
		@AllArgsConstructor
		public enum StairSlope {
			NORMAL("bottom"),
			INVERTED("top"),
			;

			private String nbt;

			public static StairSlope from(String value) {
				for (StairSlope slope : values())
					if (slope.getNbt().equalsIgnoreCase(value))
						return slope;

				throw new InvalidInputException("Slope from " + value + " not found");
			}

			@Override
			public String toString() {
				return name().toLowerCase();
			}
		}
	}

}

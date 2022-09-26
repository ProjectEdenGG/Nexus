package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.RotationType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableFloorThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Getter;

public class Counter extends DyeableFloorThing {
	@Getter
	private final CounterType counterType;
	@Getter
	private final CounterMaterial counterMaterial;
	@Getter
	private final HandleType handleType;

	public Counter(CustomMaterial material, HandleType handleType, CounterMaterial counterMaterial, CounterType counterType) {
		super(getName(counterType, handleType, counterMaterial), material, ColorableType.STAIN);
		this.counterType = counterType;
		this.counterMaterial = counterMaterial;
		this.handleType = handleType;
		this.rotationType = RotationType.DEGREE_90;
		this.hitboxes = Hitbox.single();
	}

	public static String getName(CounterType counterType, HandleType handleType, CounterMaterial counterMaterial) {
		return counterMaterial.getName() + " " + counterType.getName() + " Counter (" + handleType.getName() + ")";
	}

	public enum CounterType {
		COUNTER,
		CORNER,
		DRAWER,
		CABINET,
		SINK,
		OVEN,
		ISLAND,
		;

		public String getName() {
			return StringUtils.camelCase(this);
		}
	}

	public enum CounterMaterial {
		MARBLE,
		SOAPSTONE,
		STONE,
		WOODEN,
		;

		public String getName() {
			return StringUtils.camelCase(this);
		}
	}

	public enum HandleType {
		STEEL,
		BRASS,
		BLACK,
		;

		public String getName() {
			return StringUtils.camelCase(this);
		}
	}
}

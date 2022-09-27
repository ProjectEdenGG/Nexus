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

	public Counter(CustomMaterial customMaterial, HandleType handle, CounterMaterial material, CounterType type) {
		super(getName(type, handle, material), customMaterial, ColorableType.STAIN);

		this.counterType = type;
		this.counterMaterial = material;
		this.handleType = handle;

		this.rotationType = RotationType.DEGREE_90;
		this.hitboxes = Hitbox.single();
	}

	private static String getName(CounterType type, HandleType handle, CounterMaterial material) {
		String materialStr = getName(material == CounterMaterial.NONE, material.getName());
		String typeStr = getName(type == CounterType.NONE, type.getName());
		String handleStr = getName(handle == HandleType.NONE, handle.getName());

		return materialStr + " " + typeStr + " Counter (" + handleStr + ")";
	}

	public static String getName(boolean condition, String name) {
		return condition ? "" : name;
	}

	public enum CounterType {
		NONE,
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
		NONE,
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
		NONE,
		STEEL,
		BRASS,
		BLACK,
		;

		public String getName() {
			return StringUtils.camelCase(this);
		}
	}
}

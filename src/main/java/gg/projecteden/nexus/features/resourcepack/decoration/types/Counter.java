package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxSingle;
import gg.projecteden.nexus.features.resourcepack.decoration.common.RotationSnap;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableFloorThing;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Getter;

public class Counter extends DyeableFloorThing {
	@Getter
	private final CounterType counterType;
	@Getter
	private final CounterMaterial counterMaterial;
	@Getter
	private final HandleType handleType;

	public Counter(ItemModelType itemModelType, HandleType handle, CounterMaterial material, CounterType type) {
		super(false, getName(type, handle, material), itemModelType, ColorableType.STAIN, HitboxSingle._1x1_BARRIER);

		this.counterType = type;
		this.counterMaterial = material;
		this.handleType = handle;

		this.rotationSnap = RotationSnap.DEGREE_90;
	}

	private static String getName(CounterType type, HandleType handle, CounterMaterial material) {
		String materialStr = getName(material == CounterMaterial.NONE, material.getName());
		String typeStr = getName(type == CounterType.NONE, type.getName());
		String handleStr = getName(handle == HandleType.NONE, handle.getName());

		if (!materialStr.equals(""))
			materialStr += " ";

		if (!typeStr.equals(""))
			typeStr += " ";

		if (!handleStr.equals(""))
			handleStr = " (" + handleStr + ")";

		return materialStr + typeStr + "Counter" + handleStr;
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
		BAR,
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

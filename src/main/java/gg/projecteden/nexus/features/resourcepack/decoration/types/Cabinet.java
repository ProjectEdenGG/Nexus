package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxSingle;
import gg.projecteden.nexus.features.resourcepack.decoration.common.RotationSnap;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Counter.HandleType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableWallThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class Cabinet extends DyeableWallThing {
	private final CabinetType type;
	private final CabinetMaterial counterMaterial;
	private final HandleType handleType;

	public Cabinet(CustomMaterial customMaterial, CabinetMaterial material, HandleType handle, CabinetType type) {
		super(getName(type, handle, material), customMaterial, ColorableType.STAIN, HitboxSingle._1x1);

		this.type = type;
		this.counterMaterial = material;
		this.handleType = handle;
		this.rotationSnap = RotationSnap.DEGREE_90;
	}

	public static String getName(CabinetType type, HandleType handle, CabinetMaterial material) {
		String materialStr = Counter.getName(material == CabinetMaterial.NONE, material.getName());
		String typeStr = Counter.getName(type == CabinetType.NONE || type == CabinetType.CABINET, type.getName());
		String handleStr = Counter.getName(handle == HandleType.NONE, handle.getName());

		if (!materialStr.equals(""))
			materialStr += " ";

		if (!typeStr.equals(""))
			typeStr += " ";

		if (!handleStr.equals(""))
			handleStr = " (" + handleStr + ")";

		return materialStr + typeStr + "Cabinet" + handleStr;
	}

	public enum CabinetType {
		NONE,
		CABINET,
		CORNER,
		HOOD,
		SHORT,
		SHORT_CORNER,
		;

		public @NotNull String getName() {
			return StringUtils.camelCase(this);
		}
	}

	public enum CabinetMaterial {
		NONE,
		WOODEN,
		;

		public String getName() {
			return StringUtils.camelCase(this);
		}
	}
}

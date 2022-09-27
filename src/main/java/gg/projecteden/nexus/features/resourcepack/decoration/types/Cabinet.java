package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.RotationType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Counter.HandleType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableWallThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class Cabinet extends DyeableWallThing {
	@Getter
	private final CabinetType type;
	@Getter
	private final CabinetMaterial counterMaterial;
	@Getter
	private final HandleType handleType;

	public Cabinet(CustomMaterial customMaterial, CabinetMaterial material, HandleType handle, CabinetType type) {
		super(getName(type, handle, material), customMaterial, ColorableType.STAIN);

		this.type = type;
		this.counterMaterial = material;
		this.handleType = handle;

		this.rotationType = RotationType.DEGREE_90;
		this.hitboxes = Hitbox.single();
	}

	public static String getName(CabinetType type, HandleType handle, CabinetMaterial material) {
		String materialStr = Counter.getName(material == CabinetMaterial.NONE, material.getName());
		String typeStr = Counter.getName(type == CabinetType.NONE || type == CabinetType.CABINET, type.getName());
		String handleStr = Counter.getName(handle == HandleType.NONE, handle.getName());

		return materialStr + " " + typeStr + " Cabinet (" + handleStr + ")";
	}

	public enum CabinetType {
		NONE,
		CABINET,
		CORNER,
		HOOD,
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

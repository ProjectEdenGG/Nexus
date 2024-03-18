package gg.projecteden.nexus.features.resourcepack.decoration.types.seats;

import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Colorable;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import lombok.Getter;

public class Couch extends DyeableChair implements Colorable {
	@Getter
	private final CouchPart couchPart;

	public Couch(boolean multiblock, String name, CustomMaterial material, ColorableType colorableType, CouchPart couchPart) {
		this(multiblock, name, material, colorableType, couchPart, null);
	}

	public Couch(boolean multiblock, String name, CustomMaterial material, ColorableType colorableType, CouchPart couchPart, Double sitHeight) {
		super(multiblock, false, name, material, colorableType, sitHeight);
		this.couchPart = couchPart;
	}

	public enum CouchPart {
		STRAIGHT,
		END,
		CORNER,
		;
	}

	@Override
	public boolean isBackless() {
		return true;
	}
}

package gg.projecteden.nexus.features.resourcepack.decoration.types.seats;

import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import lombok.Getter;

public class DyeableCouch extends DyeableChair {
	@Getter
	private final CouchPart couchPart;

	public DyeableCouch(boolean multiblock, String name, ItemModelType itemModelType, ColorableType colorableType, CouchPart couchPart) {
		this(multiblock, name, itemModelType, colorableType, couchPart, null);
	}

	public DyeableCouch(boolean multiblock, String name, ItemModelType itemModelType, ColorableType colorableType, CouchPart couchPart, Double sitHeight) {
		super(multiblock, false, name, itemModelType, colorableType, sitHeight);
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

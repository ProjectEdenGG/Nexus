package gg.projecteden.nexus.features.resourcepack.decoration.types.seats;

import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.ICouch;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import lombok.Getter;

public class Couch extends Chair implements ICouch {
	@Getter
	private final CouchPart couchPart;

	public Couch(boolean multiblock, String name, ItemModelType itemModelType, CouchPart couchPart) {
		this(multiblock, name, itemModelType, couchPart, null);
	}

	public Couch(boolean multiblock, String name, ItemModelType itemModelType, CouchPart couchPart, Double sitHeight) {
		super(multiblock, false, name, itemModelType, sitHeight);
		this.couchPart = couchPart;
	}

	@Override
	public boolean isBackless() {
		return true;
	}
}

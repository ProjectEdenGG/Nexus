package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Colorable;
import lombok.Getter;

public class Couch extends Chair implements Colorable {
	@Getter
	private final CouchPart couchPart;

	public Couch(String name, int modelData, Colorable.Type type, CouchPart couchPart) {
		super(name, modelData, type);
		this.couchPart = couchPart;
	}

	public enum CouchPart {
		STRAIGHT,
		END,
		CORNER,
		;
	}
}

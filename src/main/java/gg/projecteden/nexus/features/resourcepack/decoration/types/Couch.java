package gg.projecteden.nexus.features.resourcepack.decoration.types;

import lombok.Getter;

public class Couch extends Chair {
	@Getter
	private final CouchPart couchPart;

	public Couch(String name, int modelData, CouchPart couchPart) {
		super(name, modelData, DyedPart.CUSHION);
		this.couchPart = couchPart;
	}

	public enum CouchPart {
		STRAIGHT,
		END,
		CORNER,
		;
	}
}

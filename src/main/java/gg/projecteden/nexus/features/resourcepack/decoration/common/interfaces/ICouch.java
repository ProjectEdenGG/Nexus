package gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces;

public interface ICouch {

	CouchPart getCouchPart();

	enum CouchPart {
		STRAIGHT,
		END,
		CORNER,
		;
	}
}

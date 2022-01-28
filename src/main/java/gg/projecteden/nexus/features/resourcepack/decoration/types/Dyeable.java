package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Colorable;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;


public class Dyeable extends Decoration implements Colorable {
	private final Colorable.Type type;

	public Dyeable(String name, int modelData, Colorable.Type type) {
		super(name, modelData, Colorable.getTypeMaterial(type));
		this.type = type;
	}

	@Override
	public Type getType() {
		return this.type;
	}
}

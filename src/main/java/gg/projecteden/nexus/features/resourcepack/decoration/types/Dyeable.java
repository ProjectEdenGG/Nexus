package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Colorable;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

public class Dyeable extends DecorationConfig implements Colorable {
	private final Colorable.Type type;

	public Dyeable(String name, CustomMaterial material, Colorable.Type type) {
		super(name, material);
		this.type = type;
	}

	@Override
	public Type getType() {
		return this.type;
	}
}

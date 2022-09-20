package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.WallThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import lombok.AllArgsConstructor;

public class Art extends WallThing {
	ArtSize size;

	public Art(String name, CustomMaterial material, ArtSize size) {
		super(name, material);
		this.size = size;
	}

	@AllArgsConstructor
	public enum ArtSize {
		_1x1(),
		_1x2h(),
		_1x2v(),
		_2x2(),
		;

	}
}

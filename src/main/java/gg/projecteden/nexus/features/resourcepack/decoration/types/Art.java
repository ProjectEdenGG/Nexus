package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.WallThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class Art extends WallThing {
	@Getter
	private final ArtSize size;

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
		_1x3h(),
		_1x3v(),
		_2x3h(),
		_2x3v(),
		_3x3(),
		;

	}
}

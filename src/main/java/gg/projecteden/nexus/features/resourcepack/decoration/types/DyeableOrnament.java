package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.types.Ornament.OrnamentType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableCeilingThing;

public class DyeableOrnament extends DyeableCeilingThing {

	public DyeableOrnament(OrnamentType type, boolean giant) {
		super(false, type.getName(giant), type.getModel(giant), ColorableType.DYE);
	}
}

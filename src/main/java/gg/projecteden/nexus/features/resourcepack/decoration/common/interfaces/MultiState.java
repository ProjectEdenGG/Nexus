package gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces;

import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

// Decorations like bird houses, or curtains that have multiple states for player/internal use
// Used for converting the internal item to the desired item on ItemSpawnEvent
public interface MultiState {

	CustomMaterial getBaseMaterial();
}

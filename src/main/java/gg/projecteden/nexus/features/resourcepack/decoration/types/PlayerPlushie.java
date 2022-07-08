package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.playerplushies.Pose;
import lombok.Getter;

public class PlayerPlushie extends DecorationConfig {
	@Getter
	private final Pose pose;

	public PlayerPlushie(String name, CustomMaterial material, Pose pose) {
		super(name, material, Hitbox.NONE());
		this.pose = pose;
	}
}

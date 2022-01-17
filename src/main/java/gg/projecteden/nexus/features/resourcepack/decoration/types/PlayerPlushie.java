package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.playerplushies.Pose;
import lombok.Getter;
import org.bukkit.Material;

public class PlayerPlushie extends Decoration {
	@Getter
	Pose pose;

	public PlayerPlushie(String name, int modelData, Pose pose) {
		super(name, modelData, Material.LAPIS_LAZULI, Hitbox.NONE());
		this.pose = pose;
	}
}

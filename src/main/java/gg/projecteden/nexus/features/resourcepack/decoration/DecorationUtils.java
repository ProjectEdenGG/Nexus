package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.nexus.features.custombenches.DyeStation.DyeStationMenu.StainChoice;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

public class DecorationUtils {
	@Getter
	public static Set<Material> hitboxTypes;

	static {
		Set<Material> materials = new HashSet<>();
		for (Decorations decor : Decorations.values()) {
			for (Hitbox hitbox : decor.getDecoration().getHitboxes()) {
				materials.add(hitbox.getMaterial());
			}
		}
		hitboxTypes = materials;
	}

	public static Color getDefaultWoodColor() {
		return StainChoice.OAK.getButton().getColor();
	}
}

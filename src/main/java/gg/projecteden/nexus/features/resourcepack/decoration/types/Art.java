package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.MultiBlock;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.WallThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

import java.util.ArrayList;
import java.util.List;

@MultiBlock
public class Art extends WallThing {

	public Art(String name, CustomMaterial material, CustomHitbox hitbox) {
		this(name, material, hitbox, false);
	}

	public Art(String name, CustomMaterial material, CustomHitbox hitbox, boolean vanilla) {
		super(name, material, hitbox);

		this.name = "Custom Painting";
		if (vanilla)
			this.name = "Custom Painting (Vanilla)";

		String size = hitbox.getName().substring(1, 5);    // _1x3H_xxx -> 1x3H
		String sizeDir = size.substring(3);        // 1x3H -> H
		String sizeWidth = size.substring(0, 1);            // 1x3H -> 1
		String sizeHeight = size.substring(2, 3);            // 1x3H -> 3

		String sizeFinal = sizeWidth + "x" + sizeHeight;
		if (sizeDir.equalsIgnoreCase("H"))
			sizeFinal = sizeHeight + "x" + sizeWidth;

		this.lore = new ArrayList<>(List.of("&f" + name, "&7" + sizeFinal, decorLore));
	}
}

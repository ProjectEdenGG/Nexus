package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.DecorationTagType;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.WallThing;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Getter
public class Art extends WallThing {

	String artTitle;
	int width;
	int height;
	String direction;

	public Art(String name, ItemModelType itemModelType, CustomHitbox hitbox) {
		this(name, itemModelType, hitbox, true);
	}

	public Art(String name, ItemModelType itemModelType, CustomHitbox hitbox, boolean vanilla) {
		super(true, name, itemModelType, hitbox);

		this.artTitle = name;
		this.name = "Custom Painting";
		if (vanilla)
			this.name = "Custom Painting (Vanilla)";

		// @formatter:off
		String artSize = hitbox.getName().substring(1, 5);			// _1x3H_xxx -> 1x3H
		this.direction = artSize.substring(3);			// 1x3H -> H
		this.width = Integer.parseInt(artSize.substring(0, 1));		// 1x3H -> 1
		this.height = Integer.parseInt(artSize.substring(2, 3));	// 1x3H -> 3
		// @formatter:on

		String sizeFinal = this.width + "x" + this.height;
		if (this.direction.equalsIgnoreCase("H"))
			sizeFinal = this.height + "x" + this.width;

		DecorationTagType.setLore(List.of("&f" + name, "&7" + sizeFinal), this);
	}

	public static ItemStack tabIcon_custom = new ItemBuilder(ItemModelType.ART_PAINTING_CUSTOM_SKYBLOCK.getItem())
		.lore("&732x32, Framed")
		.build();

	public static ItemStack tabIcon_vanilla = new ItemBuilder(new ItemStack(Material.PAINTING))
		.lore("&716x16, Vanilla-esque")
		.build();
}

package gg.projecteden.nexus.features.store.gallery;


import gg.projecteden.nexus.features.store.gallery.annotations.Category;
import gg.projecteden.nexus.features.store.gallery.annotations.Category.GalleryCategory;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public enum GalleryPackage {
	@Category(GalleryCategory.VISUALS)
	COSTUMES,

	@Category(GalleryCategory.VISUALS)
	WINGS,

	@Category(GalleryCategory.VISUALS)
	INVISIBLE_ARMOR,

	@Category(GalleryCategory.VISUALS)
	PLAYER_PLUSHIES,

	@Category(GalleryCategory.VISUALS)
	NPCS,

	@Category(GalleryCategory.VISUALS)
	FIREWORKS,

	@Category(GalleryCategory.VISUALS)
	RAINBOW_ARMOR,

	@Category(GalleryCategory.VISUALS)
	PLAYER_TIME,

	@Category(GalleryCategory.VISUALS)
	ENTITY_NAME,

	@Category(GalleryCategory.VISUALS)
	RAINBOW_BEACON,

	@Category(GalleryCategory.CHAT)
	PREFIXES,

	@Category(GalleryCategory.CHAT)
	NICKNAMES,

	@Category(GalleryCategory.CHAT)
	JOIN_QUIT,

	@Category(GalleryCategory.CHAT)
	EMOTES,

	@Category(GalleryCategory.PETS_DISGUISES)
	PETS,

	@Category(GalleryCategory.PETS_DISGUISES)
	DISGUISES,

	@Category(GalleryCategory.INVENTORY)
	AUTOSORT,

	@Category(GalleryCategory.INVENTORY)
	AUTOTORCH,

	@Category(GalleryCategory.INVENTORY)
	VAULTS,

	@Category(GalleryCategory.INVENTORY)
	WORKBENCHES,

	@Category(GalleryCategory.INVENTORY)
	ITEM_NAME,

	@Category(GalleryCategory.INVENTORY)
	HAT,

	@Category(GalleryCategory.INVENTORY)
	FIREWORK_BOW,

	@Category(GalleryCategory.INVENTORY)
	SKULL,

	@Category(GalleryCategory.MISC)
	CUSTOM_CONTRIBUTION,

	@Category(GalleryCategory.MISC)
	PLUS_5_HOMES,

	@Category(GalleryCategory.MISC)
	PLOTS,

	@Category(GalleryCategory.MISC)
	BOOSTS,
	;

	@SneakyThrows
	public Field getField() {
		return getClass().getField(name());
	}

	@Nullable
	public GalleryCategory getCategory() {
		Category annotation = getField().getAnnotation(Category.class);
		return annotation == null ? null : annotation.value();
	}

}

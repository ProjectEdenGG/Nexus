package gg.projecteden.nexus.features.mobheads.common;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.mobheads.MobHeadType;
import gg.projecteden.nexus.utils.ItemUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static gg.projecteden.nexus.utils.StringUtils.camelCase;

public interface MobHead {

	@NotNull
	String name();

	@NotNull
	EntityType getEntityType();

	@NotNull MobHeadType getType();

	@Nullable
	default MobHeadVariant getVariant() {
		return null;
	}

	@Nullable
	ItemStack getBaseSkull();

	@Nullable
	ItemStack getNamedSkull();

	default String getDisplayName() {
		return camelCase(getType());
	}

	static @Nullable MobHead of(Entity entity) {
		MobHeadType type = MobHeadType.of(entity.getType());
		if (type == null)
			return null;

		MobHeadVariant variant = type.getVariant(entity);
		return variant == null ? type : variant;
	}

	static @Nullable MobHead from(Block block) {
		String id = Nexus.getHeadAPI().getItemID(ItemUtils.getItem(block));
		if (id == null)
			return null;

		return MobHeadType.of(id);
	}

}

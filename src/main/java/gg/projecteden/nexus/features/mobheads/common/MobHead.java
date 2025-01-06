package gg.projecteden.nexus.features.mobheads.common;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.mobheads.MobHeadType;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

	default Sound getAmbientSound() {
		return null;
	}

	default String getDisplayName() {
		return StringUtils.camelCase(getType());
	}

	static @Nullable MobHead of(Entity entity) {
		MobHeadType type = MobHeadType.of(entity.getType());
		if (type == null)
			return null;

		MobHeadVariant variant = type.getVariant(entity);
		return variant == null ? type : variant;
	}

	static @Nullable MobHead from(Block block) {
		ItemStack item = ItemUtils.getItem(block);
		if (Nullables.isNullOrAir(item))
			return null;

		for (MobHead vanillaHead : MobHeadType.getVanillaHeads()) {
			if (vanillaHead.getType().getHeadType() == item.getType())
				return vanillaHead;
		}

		String id = Nexus.getHeadAPI().getItemID(item);
		if (id == null)
			return null;

		return MobHeadType.of(id);
	}


}

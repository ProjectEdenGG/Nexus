package me.pugabyte.nexus.features.mobheads.common;

import me.pugabyte.nexus.features.mobheads.MobHeadType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static eden.utils.StringUtils.camelCase;
import static me.pugabyte.nexus.utils.ItemUtils.isNullOrAir;

public interface MobHeadVariant extends MobHead {

	@Override
	default @NotNull MobHeadType getType() {
		return MobHeadType.of(getEntityType());
	}

	@Override
	default @NotNull MobHeadVariant getVariant() {
		return this;
	}

	ItemStack getItemStack();

	default @Nullable ItemStack getSkull() {
		ItemStack skull = getItemStack();
		if (isNullOrAir(skull))
			return MobHeadType.of(getEntityType()).getSkull();
		return skull;
	}

	void setItemStack(ItemStack itemStack);

	default String getDisplayName() {
		return "&e" + camelCase((Enum<?>) this) + " " + camelCase(getEntityType()) + " Head";
	}

}

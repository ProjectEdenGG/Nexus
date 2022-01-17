package gg.projecteden.nexus.features.mobheads.common;

import gg.projecteden.nexus.features.mobheads.MobHeadType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;
import static gg.projecteden.nexus.utils.StringUtils.camelCase;

public interface MobHeadVariant extends MobHead {

	@Override
	default @NotNull MobHeadType getType() {
		return Objects.requireNonNull(MobHeadType.of(getEntityType()));
	}

	@Override
	default @NotNull MobHeadVariant getVariant() {
		return this;
	}

	ItemStack getItemStack();

	default @Nullable ItemStack getSkull() {
		ItemStack skull = getItemStack();
		return isNullOrAir(skull) ? getType().getSkull() : skull.clone();
	}

	void setItemStack(ItemStack itemStack);

	@Override
	default String getDisplayName() {
		final String type = camelCase(getEntityType());
		final String variant = camelCase((Enum<?>) this);

		if (variant.equalsIgnoreCase("none"))
			return type;
		else
			return variant + " " + type;
	}

}

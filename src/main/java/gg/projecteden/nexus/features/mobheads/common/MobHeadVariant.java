package gg.projecteden.nexus.features.mobheads.common;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.mobheads.MobHeadType;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;
import static gg.projecteden.nexus.utils.StringUtils.camelCase;

public interface MobHeadVariant extends MobHead {

	String getHeadId();

	@Override
	default @NotNull MobHeadType getType() {
		return Objects.requireNonNull(MobHeadType.of(getEntityType()));
	}

	@Override
	default @NotNull MobHeadVariant getVariant() {
		return this;
	}

	default @NotNull ItemStack getItemStack() {
		final ItemStack itemHead = Nexus.getHeadAPI().getItemHead(getHeadId());
		return new ItemBuilder(itemHead).name("&e" + getDisplayName() + " Head").lore("&3Mob Head").build();
	}

	default @Nullable ItemStack getSkull() {
		ItemStack skull = getItemStack();
		return isNullOrAir(skull) ? getType().getSkull() : skull.clone();
	}

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

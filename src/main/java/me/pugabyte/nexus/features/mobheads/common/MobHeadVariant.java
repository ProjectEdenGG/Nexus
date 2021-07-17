package me.pugabyte.nexus.features.mobheads.common;

import me.pugabyte.nexus.features.mobheads.MobHeadType;
import org.bukkit.inventory.ItemStack;

import static eden.utils.StringUtils.camelCase;
import static me.pugabyte.nexus.utils.ItemUtils.isNullOrAir;

public interface MobHeadVariant extends MobHead {

	ItemStack getItemStack();

	default ItemStack getSkull() {
		ItemStack skull = getItemStack();
		if (isNullOrAir(skull))
			return MobHeadType.of(getEntityType()).getGenericSkull();
		return skull;
	}

	void setItemStack(ItemStack itemStack);

	default String getDisplayName() {
		return "&e" + camelCase((Enum<?>) this) + " " + camelCase(getEntityType()) + " Head";
	}

	@Override
	default MobHeadVariant getVariant() {
		return this;
	}

}

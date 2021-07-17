package me.pugabyte.nexus.features.mobheads.variants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.pugabyte.nexus.features.mobheads.common.MobHeadVariant;
import me.pugabyte.nexus.utils.RandomUtils;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static eden.utils.StringUtils.camelCase;

@Getter
@RequiredArgsConstructor
public enum TropicalFishVariant implements MobHeadVariant {
	RANDOM,
	;

	@Setter
	private List<ItemStack> itemStacks = new ArrayList<>();

	public ItemStack getItemStack() {
		return RandomUtils.randomElement(itemStacks);
	}

	@Override
	public EntityType getEntityType() {
		return EntityType.TROPICAL_FISH;
	}

	public void setItemStack(ItemStack itemStack) {
		itemStacks.add(itemStack);
	}

	@Override
	public String getDisplayName() {
		return "&e" + camelCase(getEntityType()) + " Head";
	}
}

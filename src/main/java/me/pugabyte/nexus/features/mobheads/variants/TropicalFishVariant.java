package me.pugabyte.nexus.features.mobheads.variants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.pugabyte.nexus.features.mobheads.common.MobHeadVariant;
import me.pugabyte.nexus.utils.RandomUtils;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static eden.utils.StringUtils.camelCase;

@Getter
@RequiredArgsConstructor
public enum TropicalFishVariant implements MobHeadVariant {
	ONE,
	TWO,
	THREE,
	FOUR,
	FIVE,
	SIX,
	SEVEN,
	EIGHT,
	NINE,
	TEN,
	ELEVEN,
	TWELVE,
	THIRTEEN,
	FOURTEEN,
	;

	@Setter
	private ItemStack itemStack;

	@Override
	public @NotNull EntityType getEntityType() {
		return EntityType.TROPICAL_FISH;
	}

	@Override
	public String getDisplayName() {
		return "&e" + camelCase(getEntityType()) + " Head";
	}

	public static TropicalFishVariant random() {
		return RandomUtils.randomElement(List.of(values()));
	}
}

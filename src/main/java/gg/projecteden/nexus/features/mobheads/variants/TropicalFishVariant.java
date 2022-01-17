package gg.projecteden.nexus.features.mobheads.variants;

import gg.projecteden.nexus.features.mobheads.common.MobHeadVariant;
import gg.projecteden.nexus.utils.RandomUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static gg.projecteden.nexus.utils.StringUtils.camelCase;

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

package gg.projecteden.nexus.features.mobheads.variants;

import gg.projecteden.nexus.features.mobheads.common.MobHeadVariant;
import io.papermc.paper.world.WeatheringCopperState;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.CopperGolem;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum CopperGolemVariant implements MobHeadVariant {
	UNAFFECTED("123445", WeatheringCopperState.UNAFFECTED),
	EXPOSED("123446", WeatheringCopperState.EXPOSED),
	WEATHERED("123447", WeatheringCopperState.WEATHERED),
	OXIDIZED("123448", WeatheringCopperState.OXIDIZED)
	;

	private final String headId;
	private final WeatheringCopperState copperState;

	@Override
	public @NotNull EntityType getEntityType() {
		return EntityType.COPPER_GOLEM;
	}

	public static CopperGolemVariant of(CopperGolem copperGolem) {
		return Arrays.stream(values()).filter(entry -> copperGolem.getWeatheringState() == entry.getCopperState()).findFirst().orElse(null);
	}
}

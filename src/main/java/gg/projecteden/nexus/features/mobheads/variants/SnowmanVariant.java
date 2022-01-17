package gg.projecteden.nexus.features.mobheads.variants;

import gg.projecteden.nexus.features.mobheads.common.MobHeadVariant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Snowman;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public enum SnowmanVariant implements MobHeadVariant {
	NONE,
	DERP,
	;

	@Setter
	private ItemStack itemStack;

	@Override
	public @NotNull EntityType getEntityType() {
		return EntityType.SNOWMAN;
	}

	public static SnowmanVariant of(Snowman snowman) {
		return snowman.isDerp() ? DERP : NONE;
	}
}

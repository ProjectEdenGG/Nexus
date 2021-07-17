package me.pugabyte.nexus.features.mobheads.variants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.pugabyte.nexus.features.mobheads.common.MobHeadVariant;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Snowman;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public enum SnowmanVariant implements MobHeadVariant {
	DERP,
	;

	@Setter
	private ItemStack itemStack;

	@Override
	public @NotNull EntityType getEntityType() {
		return EntityType.SNOWMAN;
	}

	public static SnowmanVariant of(Snowman snowman) {
		return snowman.isDerp() ? DERP : null;
	}
}

package me.pugabyte.nexus.features.mobheads.variants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.pugabyte.nexus.features.mobheads.common.MobHeadVariant;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Snowman;
import org.bukkit.inventory.ItemStack;

@Getter
@RequiredArgsConstructor
public enum SnowmanType implements MobHeadVariant {
	DERP,
	;

	@Setter
	private ItemStack itemStack;

	@Override
	public EntityType getEntityType() {
		return EntityType.SNOWMAN;
	}

	public static SnowmanType of(Snowman snowman) {
		return snowman.isDerp() ? DERP : null;
	}
}

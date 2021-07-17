package me.pugabyte.nexus.features.mobheads.variants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.pugabyte.nexus.features.mobheads.common.MobHeadVariant;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum FoxType implements MobHeadVariant {
	RED(Fox.Type.RED),
	SNOW(Fox.Type.SNOW),
	;

	private final Fox.Type type;
	@Setter
	private ItemStack itemStack;

	@Override
	public EntityType getEntityType() {
		return EntityType.FOX;
	}

	public static FoxType of(Fox fox) {
		return Arrays.stream(values()).filter(entry -> fox.getFoxType() == entry.getType()).findFirst().orElse(null);
	}
}

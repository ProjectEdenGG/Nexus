package me.pugabyte.nexus.features.mobheads.variants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.pugabyte.nexus.features.mobheads.common.MobHeadVariant;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Fox.Type;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum FoxVariant implements MobHeadVariant {
	RED(Type.RED),
	SNOW(Type.SNOW),
	;

	private final Type bukkitType;
	@Setter
	private ItemStack itemStack;

	@Override
	public @NotNull EntityType getEntityType() {
		return EntityType.FOX;
	}

	public static FoxVariant of(Fox fox) {
		return Arrays.stream(values()).filter(entry -> fox.getFoxType() == entry.getBukkitType()).findFirst().orElse(null);
	}
}

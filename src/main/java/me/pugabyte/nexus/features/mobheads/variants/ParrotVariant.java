package me.pugabyte.nexus.features.mobheads.variants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.pugabyte.nexus.features.mobheads.common.MobHeadVariant;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Parrot.Variant;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ParrotVariant implements MobHeadVariant {
	BLUE(Variant.BLUE),
	RED(Variant.RED),
	CYAN(Variant.CYAN),
	GRAY(Variant.GRAY),
	GREEN(Variant.GREEN),
	;

	private final Variant bukkitType;
	@Setter
	private ItemStack itemStack;

	@Override
	public @NotNull EntityType getEntityType() {
		return EntityType.PARROT;
	}

	public static ParrotVariant of(Parrot parrot) {
		return Arrays.stream(values()).filter(entry -> parrot.getVariant() == entry.getBukkitType()).findFirst().orElse(null);
	}
}

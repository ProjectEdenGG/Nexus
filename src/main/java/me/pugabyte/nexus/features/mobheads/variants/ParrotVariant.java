package me.pugabyte.nexus.features.mobheads.variants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.pugabyte.nexus.features.mobheads.common.MobHeadVariant;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Parrot;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ParrotVariant implements MobHeadVariant {
	BLUE(Parrot.Variant.BLUE),
	RED(Parrot.Variant.RED),
	CYAN(Parrot.Variant.CYAN),
	GRAY(Parrot.Variant.GRAY),
	GREEN(Parrot.Variant.GREEN),
	;

	private final Parrot.Variant bukkitType;
	@Setter
	private ItemStack itemStack;

	@Override
	public EntityType getEntityType() {
		return EntityType.PARROT;
	}

	public static ParrotVariant of(Parrot parrot) {
		return Arrays.stream(values()).filter(entry -> parrot.getVariant() == entry.getBukkitType()).findFirst().orElse(null);
	}
}

package me.pugabyte.nexus.features.mobheads.variants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.pugabyte.nexus.features.mobheads.common.MobHeadVariant;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Axolotl.Variant;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum AxolotlVariant implements MobHeadVariant {
	LUCY(Variant.LUCY),
	WILD(Variant.WILD),
	GOLD(Variant.GOLD),
	CYAN(Variant.CYAN),
	BLUE(Variant.BLUE),
	;

	private final Variant bukkitType;
	@Setter
	private ItemStack itemStack;

	@Override
	public @NotNull EntityType getEntityType() {
		return EntityType.AXOLOTL;
	}

	public static AxolotlVariant of(Axolotl axolotl) {
		return Arrays.stream(values()).filter(entry -> axolotl.getVariant() == entry.getBukkitType()).findFirst().orElse(null);
	}
}

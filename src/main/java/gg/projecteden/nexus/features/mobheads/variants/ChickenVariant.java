package gg.projecteden.nexus.features.mobheads.variants;

import gg.projecteden.nexus.features.mobheads.common.MobHeadVariant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Chicken.Variant;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ChickenVariant implements MobHeadVariant {
	WARM("112539", Variant.WARM),
	TEMPERATE("336", Variant.TEMPERATE),
	COLD("112538", Variant.COLD),
	;

	private final String headId;
	private final Variant bukkitType;

	@Override
	public @NotNull EntityType getEntityType() {
		return EntityType.CHICKEN;
	}

	public static ChickenVariant of(Chicken chicken) {
		return Arrays.stream(values()).filter(entry -> chicken.getVariant() == entry.getBukkitType()).findFirst().orElse(null);
	}
}

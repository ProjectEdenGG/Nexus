package gg.projecteden.nexus.features.mobheads.variants;

import gg.projecteden.nexus.features.mobheads.common.MobHeadVariant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Frog;
import org.bukkit.entity.Frog.Variant;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum FrogVariant implements MobHeadVariant {
	COLD("51342", Variant.COLD),
	WARM("51344", Variant.WARM),
	TEMPERATE("51343", Variant.TEMPERATE),
	;

	private final String headId;
	private final Variant bukkitType;

	@Override
	public @NotNull EntityType getEntityType() {
		return EntityType.FROG;
	}

	public static FrogVariant of(Frog frog) {
		return Arrays.stream(values()).filter(entry -> frog.getVariant() == entry.getBukkitType()).findFirst().orElse(null);
	}
}

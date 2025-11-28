package gg.projecteden.nexus.features.mobheads.variants;

import gg.projecteden.nexus.features.mobheads.common.MobHeadVariant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Pig.Variant;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum PigVariant implements MobHeadVariant {
	WARM("112537", Variant.WARM),
	TEMPERATE("337", Variant.TEMPERATE),
	COLD("112536", Variant.COLD),
	;

	private final String headId;
	private final Variant bukkitType;

	@Override
	public @NotNull EntityType getEntityType() {
		return EntityType.PIG;
	}

	public static PigVariant of(Pig pig) {
		return Arrays.stream(values()).filter(entry -> pig.getVariant() == entry.getBukkitType()).findFirst().orElse(null);
	}
}

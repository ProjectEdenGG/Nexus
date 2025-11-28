package gg.projecteden.nexus.features.mobheads.variants;

import gg.projecteden.nexus.features.mobheads.common.MobHeadVariant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Cow.Variant;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum CowVariant implements MobHeadVariant {
	WARM("112543", Variant.WARM),
	TEMPERATE("22866", Variant.TEMPERATE),
	COLD("115919", Variant.COLD),
	;

	private final String headId;
	private final Variant bukkitType;

	@Override
	public @NotNull EntityType getEntityType() {
		return EntityType.COW;
	}

	public static CowVariant of(Cow cow) {
		return Arrays.stream(values()).filter(entry -> cow.getVariant() == entry.getBukkitType()).findFirst().orElse(null);
	}
}

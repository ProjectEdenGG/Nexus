package gg.projecteden.nexus.features.mobheads.variants;

import gg.projecteden.nexus.features.mobheads.common.MobHeadVariant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.MushroomCow.Variant;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum MooshroomVariant implements MobHeadVariant {
	RED("339", Variant.RED),
	BROWN("26552", Variant.BROWN),
	;

	private final String headId;
	private final Variant bukkitType;

	@Override
	public @NotNull EntityType getEntityType() {
		return EntityType.MOOSHROOM;
	}

	public static MooshroomVariant of(MushroomCow mushroomCow) {
		return Arrays.stream(values()).filter(entry -> mushroomCow.getVariant() == entry.getBukkitType()).findFirst().orElse(null);
	}
}

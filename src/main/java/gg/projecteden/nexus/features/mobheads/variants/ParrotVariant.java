package gg.projecteden.nexus.features.mobheads.variants;

import gg.projecteden.nexus.features.mobheads.common.MobHeadVariant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Parrot.Variant;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ParrotVariant implements MobHeadVariant {
	BLUE("6816", Variant.BLUE),
	RED("8493", Variant.RED),
	CYAN("25382", Variant.CYAN),
	GRAY("6536", Variant.GRAY),
	GREEN("6535", Variant.GREEN),
	;

	private final String headId;
	private final Variant bukkitType;

	@Override
	public @NotNull EntityType getEntityType() {
		return EntityType.PARROT;
	}

	public static ParrotVariant of(Parrot parrot) {
		return Arrays.stream(values()).filter(entry -> parrot.getVariant() == entry.getBukkitType()).findFirst().orElse(null);
	}
}

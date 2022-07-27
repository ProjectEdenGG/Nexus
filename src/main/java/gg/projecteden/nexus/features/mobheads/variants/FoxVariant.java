package gg.projecteden.nexus.features.mobheads.variants;

import gg.projecteden.nexus.features.mobheads.common.MobHeadVariant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Fox.Type;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum FoxVariant implements MobHeadVariant {
	RED("33923", Type.RED),
	SNOW("26329", Type.SNOW),
	;

	private final String headId;
	private final Type bukkitType;

	@Override
	public @NotNull EntityType getEntityType() {
		return EntityType.FOX;
	}

	public static FoxVariant of(Fox fox) {
		return Arrays.stream(values()).filter(entry -> fox.getFoxType() == entry.getBukkitType()).findFirst().orElse(null);
	}
}

package gg.projecteden.nexus.features.mobheads.variants;

import gg.projecteden.nexus.features.mobheads.common.MobHeadVariant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Rabbit.Type;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum RabbitVariant implements MobHeadVariant {
	BLACK("6814", Type.BLACK),
	WHITE("49681", Type.WHITE),
	BROWN("6812", Type.BROWN),
	BLACK_AND_WHITE("6813", Type.BLACK_AND_WHITE),
	GOLD("3932", Type.GOLD),
	SALT_AND_PEPPER("3933", Type.SALT_AND_PEPPER),
	;

	private final String headId;
	private final Type bukkitType;

	@Override
	public @NotNull EntityType getEntityType() {
		return EntityType.RABBIT;
	}

	public static RabbitVariant of(Rabbit rabbit) {
		return Arrays.stream(values()).filter(entry -> rabbit.getRabbitType() == entry.getBukkitType()).findFirst().orElse(null);
	}
}

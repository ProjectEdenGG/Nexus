package me.pugabyte.nexus.features.mobheads.variants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.pugabyte.nexus.features.mobheads.common.MobHeadVariant;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Rabbit.Type;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum RabbitVariant implements MobHeadVariant {
	BLACK(Type.BLACK),
	WHITE(Type.WHITE),
	BROWN(Type.BROWN),
	BLACK_AND_WHITE(Type.BLACK_AND_WHITE),
	GOLD(Type.GOLD),
	SALT_AND_PEPPER(Type.SALT_AND_PEPPER),
	;

	private final Type bukkitType;
	@Setter
	private ItemStack itemStack;

	@Override
	public @NotNull EntityType getEntityType() {
		return EntityType.RABBIT;
	}

	public static RabbitVariant of(Rabbit rabbit) {
		return Arrays.stream(values()).filter(entry -> rabbit.getRabbitType() == entry.getBukkitType()).findFirst().orElse(null);
	}
}

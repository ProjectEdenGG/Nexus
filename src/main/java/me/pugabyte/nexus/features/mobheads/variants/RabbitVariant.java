package me.pugabyte.nexus.features.mobheads.variants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.pugabyte.nexus.features.mobheads.common.MobHeadVariant;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Rabbit;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum RabbitVariant implements MobHeadVariant {
	BLACK(Rabbit.Type.BLACK),
	WHITE(Rabbit.Type.WHITE),
	BROWN(Rabbit.Type.BROWN),
	BLACK_AND_WHITE(Rabbit.Type.BLACK_AND_WHITE),
	GOLD(Rabbit.Type.GOLD),
	SALT_AND_PEPPER(Rabbit.Type.SALT_AND_PEPPER),
	;

	private final Rabbit.Type type;
	@Setter
	private ItemStack itemStack;

	@Override
	public EntityType getEntityType() {
		return EntityType.RABBIT;
	}

	public static RabbitVariant of(Rabbit rabbit) {
		return Arrays.stream(values()).filter(entry -> rabbit.getRabbitType() == entry.getType()).findFirst().orElse(null);
	}
}

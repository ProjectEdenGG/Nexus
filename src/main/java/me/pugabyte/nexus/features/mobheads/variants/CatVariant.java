package me.pugabyte.nexus.features.mobheads.variants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.pugabyte.nexus.features.mobheads.common.MobHeadVariant;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Cat.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum CatVariant implements MobHeadVariant {
	BLACK(Type.BLACK),
	WHITE(Type.WHITE),
	ALL_BLACK(Type.ALL_BLACK),
	RED(Type.RED),
	BRITISH_SHORTHAIR(Type.BRITISH_SHORTHAIR),
	CALICO(Type.CALICO),
	JELLIE(Type.JELLIE),
	PERSIAN(Type.PERSIAN),
	RAGDOLL(Type.RAGDOLL),
	SIAMESE(Type.SIAMESE),
	TABBY(Type.TABBY),
	;

	private final Type bukkitType;
	@Setter
	private ItemStack itemStack;

	@Override
	public @NotNull EntityType getEntityType() {
		return EntityType.CAT;
	}

	public static CatVariant of(Cat cat) {
		return Arrays.stream(values()).filter(entry -> cat.getCatType() == entry.getBukkitType()).findFirst().orElse(null);
	}
}

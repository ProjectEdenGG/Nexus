package me.pugabyte.nexus.features.mobheads.variants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.pugabyte.nexus.features.mobheads.common.MobHeadVariant;
import org.bukkit.entity.Cat;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum CatType implements MobHeadVariant {
	BLACK(Cat.Type.BLACK),
	WHITE(Cat.Type.WHITE),
	ALL_BLACK(Cat.Type.ALL_BLACK),
	RED(Cat.Type.RED),
	BRITISH_SHORTHAIR(Cat.Type.BRITISH_SHORTHAIR),
	CALICO(Cat.Type.CALICO),
	JELLIE(Cat.Type.JELLIE),
	PERSIAN(Cat.Type.PERSIAN),
	RAGDOLL(Cat.Type.RAGDOLL),
	SIAMESE(Cat.Type.SIAMESE),
	TABBY(Cat.Type.TABBY),
	;

	@Override
	public EntityType getEntityType() {
		return EntityType.CAT;
	}

	private final Cat.Type type;
	@Setter
	private ItemStack itemStack;

	public static CatType of(Cat cat) {
		return Arrays.stream(values()).filter(entry -> cat.getCatType() == entry.getType()).findFirst().orElse(null);
	}
}

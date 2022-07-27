package gg.projecteden.nexus.features.mobheads.variants;

import gg.projecteden.nexus.features.mobheads.common.MobHeadVariant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Cat.Type;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum CatVariant implements MobHeadVariant {
	BLACK("23858", Type.BLACK),
	WHITE("23857", Type.WHITE),
	ALL_BLACK("23862", Type.ALL_BLACK),
	RED("23859", Type.RED),
	BRITISH_SHORTHAIR("23861", Type.BRITISH_SHORTHAIR),
	CALICO("23860", Type.CALICO),
	JELLIE("25037", Type.JELLIE),
	PERSIAN("24186", Type.PERSIAN),
	RAGDOLL("23855", Type.RAGDOLL),
	SIAMESE("24185", Type.SIAMESE),
	TABBY("23856", Type.TABBY),
	;

	private final String headId;
	private final Type bukkitType;

	@Override
	public @NotNull EntityType getEntityType() {
		return EntityType.CAT;
	}

	public static CatVariant of(Cat cat) {
		return Arrays.stream(values()).filter(entry -> cat.getCatType() == entry.getBukkitType()).findFirst().orElse(null);
	}
}

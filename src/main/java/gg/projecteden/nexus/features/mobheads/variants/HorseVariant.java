package gg.projecteden.nexus.features.mobheads.variants;

import gg.projecteden.nexus.features.mobheads.common.MobHeadVariant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Color;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum HorseVariant implements MobHeadVariant {
	WHITE("38015", Color.WHITE),
	CREAMY("38012", Color.CREAMY),
	CHESTNUT("38009", Color.CHESTNUT),
	BROWN("38010", Color.BROWN),
	BLACK("38013", Color.BLACK),
	GRAY("38014", Color.GRAY),
	DARK_BROWN("38011", Color.DARK_BROWN),
	;

	private final String headId;
	private final Color bukkitType;

	@Override
	public @NotNull EntityType getEntityType() {
		return EntityType.HORSE;
	}

	public static HorseVariant of(Horse horse) {
		return Arrays.stream(values()).filter(entry -> horse.getColor() == entry.getBukkitType()).findFirst().orElse(null);
	}
}

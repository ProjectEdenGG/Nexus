package gg.projecteden.nexus.features.mobheads.variants;

import gg.projecteden.nexus.features.mobheads.common.MobHeadVariant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Llama.Color;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum LlamaVariant implements MobHeadVariant {
	GRAY("3930", Color.GRAY),
	WHITE("3931", Color.WHITE),
	BROWN("3929", Color.BROWN),
	CREAMY("26964", Color.CREAMY),
	;

	private final String headId;
	private final Color bukkitType;

	@Override
	public @NotNull EntityType getEntityType() {
		return EntityType.LLAMA;
	}

	public static LlamaVariant of(Llama llama) {
		return Arrays.stream(values()).filter(entry -> llama.getColor() == entry.getBukkitType()).findFirst().orElse(null);
	}
}

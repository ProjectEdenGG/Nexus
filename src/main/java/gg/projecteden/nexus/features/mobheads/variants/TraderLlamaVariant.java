package gg.projecteden.nexus.features.mobheads.variants;

import gg.projecteden.nexus.features.mobheads.common.MobHeadVariant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Llama.Color;
import org.bukkit.entity.TraderLlama;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum TraderLlamaVariant implements MobHeadVariant {
	GRAY("26962", Color.GRAY),
	WHITE("26961", Color.WHITE),
	BROWN("26960", Color.BROWN),
	CREAMY("26963", Color.CREAMY),
	;

	private final String headId;
	private final Color bukkitType;

	@Override
	public @NotNull EntityType getEntityType() {
		return EntityType.TRADER_LLAMA;
	}

	public static TraderLlamaVariant of(TraderLlama traderLlama) {
		return Arrays.stream(values()).filter(entry -> traderLlama.getColor() == entry.getBukkitType()).findFirst().orElse(null);
	}
}

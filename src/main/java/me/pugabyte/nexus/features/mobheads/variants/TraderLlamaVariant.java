package me.pugabyte.nexus.features.mobheads.variants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.pugabyte.nexus.features.mobheads.common.MobHeadVariant;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Llama.Color;
import org.bukkit.entity.TraderLlama;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum TraderLlamaVariant implements MobHeadVariant {
	GRAY(Color.GRAY),
	WHITE(Color.WHITE),
	BROWN(Color.BROWN),
	CREAMY(Color.CREAMY),
	;

	private final Color bukkitType;
	@Setter
	private ItemStack itemStack;

	@Override
	public EntityType getEntityType() {
		return EntityType.TRADER_LLAMA;
	}

	public static TraderLlamaVariant of(TraderLlama traderLlama) {
		return Arrays.stream(values()).filter(entry -> traderLlama.getColor() == entry.getBukkitType()).findFirst().orElse(null);
	}

}

package gg.projecteden.nexus.features.mobheads.variants;

import gg.projecteden.nexus.features.mobheads.common.MobHeadVariant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Llama.Color;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum LlamaVariant implements MobHeadVariant {
	GRAY(Color.GRAY),
	WHITE(Color.WHITE),
	BROWN(Color.BROWN),
	CREAMY(Color.CREAMY),
	;

	private final Color bukkitType;
	@Setter
	private ItemStack itemStack;

	@Override
	public @NotNull EntityType getEntityType() {
		return EntityType.LLAMA;
	}

	public static LlamaVariant of(Llama llama) {
		return Arrays.stream(values()).filter(entry -> llama.getColor() == entry.getBukkitType()).findFirst().orElse(null);
	}
}

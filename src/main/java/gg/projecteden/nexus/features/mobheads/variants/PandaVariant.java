package gg.projecteden.nexus.features.mobheads.variants;

import gg.projecteden.nexus.features.mobheads.common.MobHeadVariant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Panda.Gene;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum PandaVariant implements MobHeadVariant {
	BROWN(Gene.BROWN),
	AGGRESSIVE(Gene.AGGRESSIVE),
	LAZY(Gene.LAZY),
	NORMAL(Gene.NORMAL),
	PLAYFUL(Gene.PLAYFUL),
	WEAK(Gene.WEAK),
	WORRIED(Gene.WORRIED),
	;

	private final Gene bukkitType;
	@Setter
	private ItemStack itemStack;

	@Override
	public @NotNull EntityType getEntityType() {
		return EntityType.PANDA;
	}

	public static PandaVariant of(Panda panda) {
		return Arrays.stream(values()).filter(entry -> panda.getMainGene() == entry.getBukkitType()).findFirst().orElse(null);
	}
}

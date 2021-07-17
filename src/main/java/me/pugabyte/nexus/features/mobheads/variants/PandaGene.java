package me.pugabyte.nexus.features.mobheads.variants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.pugabyte.nexus.features.mobheads.common.MobHeadVariant;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Panda.Gene;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum PandaGene implements MobHeadVariant {
	BROWN(Gene.BROWN),
	AGGRESSIVE(Gene.AGGRESSIVE),
	LAZY(Gene.LAZY),
	NORMAL(Gene.NORMAL),
	PLAYFUL(Gene.PLAYFUL),
	WEAK(Gene.WEAK),
	WORRIED(Gene.WORRIED),
	;

	private final Gene type;
	@Setter
	private ItemStack itemStack;

	@Override
	public EntityType getEntityType() {
		return EntityType.PANDA;
	}

	public static PandaGene of(Panda panda) {
		return Arrays.stream(values()).filter(entry -> panda.getMainGene() == entry.getType()).findFirst().orElse(null);
	}
}

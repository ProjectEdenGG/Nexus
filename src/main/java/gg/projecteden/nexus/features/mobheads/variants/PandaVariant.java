package gg.projecteden.nexus.features.mobheads.variants;

import gg.projecteden.nexus.features.mobheads.common.MobHeadVariant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Panda.Gene;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum PandaVariant implements MobHeadVariant {
	BROWN("23594", Gene.BROWN),
	AGGRESSIVE("23595", Gene.AGGRESSIVE),
	LAZY("23593", Gene.LAZY),
	NORMAL("6538", Gene.NORMAL),
	PLAYFUL("23597", Gene.PLAYFUL),
	WEAK("23592", Gene.WEAK),
	WORRIED("23793", Gene.WORRIED),
	;

	private final String headId;
	private final Gene bukkitType;

	@Override
	public @NotNull EntityType getEntityType() {
		return EntityType.PANDA;
	}

	public static PandaVariant of(Panda panda) {
		return Arrays.stream(values()).filter(entry -> panda.getMainGene() == entry.getBukkitType()).findFirst().orElse(null);
	}
}

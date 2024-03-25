package gg.projecteden.nexus.features.customblocks.models;

import gg.projecteden.nexus.features.customblocks.models.common.IHarvestable;
import gg.projecteden.nexus.utils.MaterialTag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@AllArgsConstructor
public enum CustomToolBlock implements IHarvestable {
	DEEPSLATE(MaterialTag.ALL_DEEPSLATE.exclude(Material.INFESTED_DEEPSLATE).getValues(), Material.DIAMOND_PICKAXE),
	;

	@Getter
	@NonNull
	private final Set<Material> affected;
	@Getter
	@NonNull
	private final Material minimumRequiredTool;

	public static void init() {
	}

	public static @Nullable CustomToolBlock of(Block block) {
		for (CustomToolBlock changedBlock : values())
			if (changedBlock.getAffected().contains(block.getType()))
				return changedBlock;

		return null;
	}
}

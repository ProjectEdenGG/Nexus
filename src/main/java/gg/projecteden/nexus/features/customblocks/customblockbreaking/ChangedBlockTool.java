package gg.projecteden.nexus.features.customblocks.customblockbreaking;

import gg.projecteden.nexus.features.customblocks.CustomBlockUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@AllArgsConstructor
public enum ChangedBlockTool {
	DEEPSLATE(MaterialTag.ALL_DEEPSLATE.exclude(Material.INFESTED_DEEPSLATE).getValues(), Material.DIAMOND_PICKAXE),
	;

	@Getter
	@NonNull
	private final Set<Material> affected;
	@Getter
	@NonNull
	private final Material minimumTool;

	public static @Nullable ChangedBlockTool of(Block block) {
		for (ChangedBlockTool changedBlock : values()) {
			if (changedBlock.getAffected().contains(block.getType()))
				return changedBlock;
		}
		return null;
	}


	public boolean isAcceptableTool(ItemStack tool) {
		return CustomBlockUtils.isAcceptableTool(tool, this.minimumTool);
	}
}

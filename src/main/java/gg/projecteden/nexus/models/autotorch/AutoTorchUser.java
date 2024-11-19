package gg.projecteden.nexus.models.autotorch;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.parchment.HasHumanEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Data
@Entity(value = "autotorch", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class AutoTorchUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private int lightLevel = 0;
	private Material torchMaterial = Material.TORCH;
	private boolean enabled = false;

	/**
	 * Whether auto torches should apply at the supplied light level
	 * @param lightLevel int from 0 to 15
	 * @return whether to use auto torches
	 */
	public boolean applies(int lightLevel) {
		return enabled && lightLevel <= this.lightLevel;
	}

	/**
	 * Whether auto torches apply to the specified block. Considers the block's light level,
	 * if it's replaceable (i.e. air or grass), and if the block below supports placing torches.
	 * @param block block where you want to place the torch
	 * @return whether you can place an auto torch
	 */
	public boolean applies(HasHumanEntity player, Block block) {
		return applies(block.getLightFromBlocks()) &&
				!block.isLiquid() &&
			Bukkit.getUnsafe().canPlaceItemOn(new ItemStack(torchMaterial), player, block.getRelative(BlockFace.DOWN), BlockFace.UP).join();
	}

	public String getTorchMaterialName() {
		return StringUtils.camelCase(this.torchMaterial);
	}
}

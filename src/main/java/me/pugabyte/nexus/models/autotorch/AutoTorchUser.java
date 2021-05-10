package me.pugabyte.nexus.models.autotorch;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.lexikiq.HasHumanEntity;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Data
@Entity("autotorch")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class AutoTorchUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private int lightLevel = 7;
	private boolean enabled = false;

	/**
	 * Whether or not auto torches should apply at the supplied light level
	 * @param lightLevel int from 0 to 15
	 * @return whether or not to use auto torches
	 */
	public boolean applies(int lightLevel) {
		return enabled && lightLevel <= this.lightLevel;
	}

	/**
	 * Whether or not auto torches apple to the specified block. Considers the block's light level,
	 * if it's replaceable (i.e. air or grass), and if the block below supports placing torches.
	 * @param block any block
	 * @return whether or not to place an auto torch
	 */
	public boolean applies(HasHumanEntity player, Block block) {
		return applies(block.getLightFromBlocks()) &&
				/* todo: remove this check next parchment update */ block.isReplaceable() &&
				CraftItemStack.asCraftCopy(new ItemStack(Material.TORCH)).canPlaceOn(player, block.getRelative(BlockFace.DOWN), BlockFace.UP);
	}
}

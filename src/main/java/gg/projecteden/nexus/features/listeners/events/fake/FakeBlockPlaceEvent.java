package gg.projecteden.nexus.features.listeners.events.fake;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FakeBlockPlaceEvent extends BlockPlaceEvent implements FakeEvent {

	public FakeBlockPlaceEvent(@NotNull Block placedBlock, @NotNull BlockState replacedBlockState,
							   @NotNull Block placedAgainst, @NotNull ItemStack itemInHand, @NotNull Player player,
							   boolean canBuild, @NotNull EquipmentSlot slot) {
		super(placedBlock, replacedBlockState, placedAgainst, itemInHand, player, canBuild, slot);
	}
}

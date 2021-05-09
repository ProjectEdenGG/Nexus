package me.pugabyte.nexus.features.listeners;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.models.autotorch.AutoTorchService;
import me.pugabyte.nexus.models.autotorch.AutoTorchUser;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@NoArgsConstructor
public class AutoTorch implements Listener {
	private static final AutoTorchService service = new AutoTorchService();

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (!player.hasPermission("store.autosort")) return;
		AutoTorchUser autoTorchUser = service.get(player);
		if (!player.getInventory().containsAtLeast(new ItemStack(Material.TORCH), 1)) return;
		Block block = player.getLocation().getBlock();
		if (!autoTorchUser.applies(block));
		BlockState currentState = block.getState();
		BlockData currentData = currentState.getBlockData();
		block.setType(Material.TORCH);
		BlockPlaceEvent placeEvent = new BlockPlaceEvent(block, currentState, block.getRelative(0, -1, 0), player.getInventory().getItemInMainHand(), player, true, EquipmentSlot.HAND);
		if (!placeEvent.callEvent() || !placeEvent.canBuild()) {
			block.setBlockData(currentData);
			return;
		}
		ItemStack item = Arrays.stream(player.getInventory().getStorageContents()).filter(itemStack -> itemStack.getType() == Material.TORCH && itemStack.getAmount() > 0).findFirst().orElse(null);
		if (item == null) {
			block.setBlockData(currentData);
			return;
		}
		item.setAmount(item.getAmount()-1);
	}
}

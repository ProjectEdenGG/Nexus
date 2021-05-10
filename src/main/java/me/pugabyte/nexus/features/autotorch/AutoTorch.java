package me.pugabyte.nexus.features.autotorch;

import eden.utils.TimeUtils;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.commands.AutoTorchCommand;
import me.pugabyte.nexus.framework.features.Feature;
import me.pugabyte.nexus.models.autotorch.AutoTorchService;
import me.pugabyte.nexus.models.autotorch.AutoTorchUser;
import me.pugabyte.nexus.utils.GameModeWrapper;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.WorldGroup;
import me.pugabyte.nexus.utils.WorldGuardFlagUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

@NoArgsConstructor
public class AutoTorch extends Feature {
	private static final AutoTorchService service = new AutoTorchService();
	private int taskId = -1;

	@Override
	public void onStart() {
		taskId = Tasks.repeatAsync(5, TimeUtils.Time.SECOND.x(1/3), () -> {
			Bukkit.getOnlinePlayers().forEach(player -> {
				GameModeWrapper gameMode = GameModeWrapper.of(player);
				if (!gameMode.canBuild() || !WorldGroup.SURVIVAL.contains(player.getWorld()) || !player.hasPermission(AutoTorchCommand.PERMISSION) || !WorldGuardFlagUtils.canPlace(player)) return;

				AutoTorchUser autoTorchUser = service.get(player);

				ItemStack item = PlayerUtils.getNonNullInventoryContents(player).stream().filter(itemStack -> itemStack.getType() == Material.TORCH && itemStack.getAmount() > 0).findAny().orElse(null);
				if (item == null) return;

				Block block = player.getLocation().getBlock();
				if (!autoTorchUser.applies(block)) return; // checks light level and if block is replaceable

				BlockState currentState = block.getState();
				BlockData currentData = currentState.getBlockData();
				Tasks.sync(() -> {
					block.setType(Material.TORCH);
					BlockPlaceEvent placeEvent = new BlockPlaceEvent(block, currentState, block.getRelative(0, -1, 0), player.getInventory().getItemInMainHand(), player, true, EquipmentSlot.HAND);
					if (!placeEvent.callEvent() || !placeEvent.canBuild()) {
						block.setBlockData(currentData);
						return;
					}

					if (gameMode.isSurvival())
						item.setAmount(item.getAmount()-1);
				});
			});
		});
	}

	@Override
	public void onStop() {
		if (taskId != -1) {
			Tasks.cancel(taskId);
			taskId = -1;
		}
	}
}

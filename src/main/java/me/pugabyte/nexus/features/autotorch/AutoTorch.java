package me.pugabyte.nexus.features.autotorch;

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
		taskId = Tasks.repeatAsync(5, 5, () -> {
			Bukkit.getOnlinePlayers().forEach(player -> {
				GameModeWrapper gameMode = GameModeWrapper.of(player);
				// basic checks to ensure player can use the command and is in survival + the survival world.
				// also checks world guard to avoid spam in player's chat of "hey! you can't do that here"
				if (!gameMode.canBuild() || !WorldGroup.SURVIVAL.contains(player.getWorld()) || !player.hasPermission(AutoTorchCommand.PERMISSION) || !WorldGuardFlagUtils.canPlace(player)) return;

				// ensures the player has a torch
				ItemStack item = PlayerUtils.getNonNullInventoryContents(player).stream().filter(itemStack -> itemStack.getType() == Material.TORCH && itemStack.getAmount() > 0).findAny().orElse(null);
				if (item == null) return;

				AutoTorchUser autoTorchUser = service.get(player);
				Block block = player.getLocation().getBlock();

				Tasks.sync(() -> {
					if (!autoTorchUser.applies(player, block)) return; // checks light level and if a torch can be placed here

					// copies current data to send in event and to restore if event is cancelled
					BlockState currentState = block.getState();
					BlockData currentData = currentState.getBlockData();
					block.setType(Material.TORCH);

					// ensure no plugins are blocking placing here
					BlockPlaceEvent placeEvent = new BlockPlaceEvent(block, currentState, block.getRelative(0, -1, 0), player.getInventory().getItemInMainHand(), player, true, EquipmentSlot.HAND);
					if (!placeEvent.callEvent() || !placeEvent.canBuild()) {
						block.setBlockData(currentData);
						return;
					}

					// remove a torch from player's inventory
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

package me.pugabyte.nexus.features.autosort.features;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.autosort.AutoSort;
import me.pugabyte.nexus.features.autosort.AutoSortFeature;
import me.pugabyte.nexus.models.autosort.AutoSortUser;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

import static me.pugabyte.nexus.utils.ItemUtils.isNullOrAir;
import static me.pugabyte.nexus.utils.PlayerUtils.getHotbarContents;

@NoArgsConstructor
public class AutoTool implements Listener {

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();

		if (event.getAction() != Action.LEFT_CLICK_BLOCK)
			return;
		if (block == null)
			return;
		if (event.getHand() != EquipmentSlot.HAND)
			return;
		if (!(player.hasPermission(AutoSort.PERMISSION) || player.hasPermission("autotool.use")))
			return;
		if (!AutoSortUser.of(player).hasFeatureEnabled(AutoSortFeature.AUTO_TOOL))
			return;

		ItemStack[] contents = getHotbarContents(player);
		ItemStack bestTool = getBestTool(Set.of(contents), block);

		if (isNullOrAir(bestTool))
			return;
		if (bestTool.equals(event.getItem()))
			return;

		for (int i = 0; i <= contents.length; i++)
			if (bestTool.equals(contents[i])) {
				player.getInventory().setHeldItemSlot(i);
				return;
			}
	}

	@Nullable
	private ItemStack getBestTool(Set<ItemStack> items, Block block) {
		return Collections.max(items, Comparator.comparingDouble(item -> {
			if (isNullOrAir(item))
				return 0;
			if (!block.isValidTool(item))
				return 0;
			if (item.getType().name().contains("GOLDEN"))
				return 0;

			float speed = block.getDestroySpeed(item);
			if (speed > 1)
				return speed;

			return 0;
		}));
	}

}

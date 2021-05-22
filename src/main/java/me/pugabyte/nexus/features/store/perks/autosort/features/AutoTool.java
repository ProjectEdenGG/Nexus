package me.pugabyte.nexus.features.store.perks.autosort.features;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.store.perks.autosort.AutoSort;
import me.pugabyte.nexus.features.store.perks.autosort.AutoSortFeature;
import me.pugabyte.nexus.models.autosort.AutoSortUser;
import me.pugabyte.nexus.utils.Enchant;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.Tool;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static me.pugabyte.nexus.utils.ItemUtils.isNullOrAir;
import static me.pugabyte.nexus.utils.PlayerUtils.getHotbarContents;

@NoArgsConstructor
public class AutoTool implements Listener {
	public static final String PERMISSION = "autotool.use";

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();

		if (player.getTargetEntity(5) != null)
			return; // could try to find the "best" weapon but that's kinda subjective, given different mcMMO perks, enchants, attack speeds, etc.
		if (event.getAction() != Action.LEFT_CLICK_BLOCK)
			return;
		if (block == null)
			return;
		if (event.getHand() != EquipmentSlot.HAND)
			return;
		if (!(player.hasPermission(AutoSort.PERMISSION) || player.hasPermission(PERMISSION)))
			return;
		if (!AutoSortUser.of(player).hasFeatureEnabled(AutoSortFeature.AUTOTOOL))
			return;

		ItemStack[] contents = getHotbarContents(player);
		ItemStack bestTool = getBestTool(Arrays.asList(contents), block);

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
	private ItemStack getBestTool(List<ItemStack> items, Block block) {
		List<ItemStack> itemStacks = new ArrayList<>(items);
		itemStacks.add(null);

		return Collections.max(itemStacks, Comparator.comparingDouble(item -> {
			if (isNullOrAir(item))
				return 0;

			if (!block.isValidTool(item))
				return -1;

			if (MaterialTag.ALL_GLASS.isTagged(block.getType())) {
				if (item.containsEnchantment(Enchant.SILK_TOUCH)) {
					Tool tool = Tool.of(item);
					return (Tool.values().length - tool.ordinal()) + tool.getTools().indexOf(item.getType());
				}

				return -1;
			}

			if (item.getType().name().contains("GOLDEN"))
				return -1;

			float speed = block.getDestroySpeed(item);
			if (speed > 1)
				return speed;

			return -1;
		}));
	}

}

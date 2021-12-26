package gg.projecteden.nexus.features.store.perks.autosort.features;

import gg.projecteden.nexus.features.store.perks.autosort.AutoSort;
import gg.projecteden.nexus.features.store.perks.autosort.AutoSortFeature;
import gg.projecteden.nexus.models.autosort.AutoSortUser;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.Tool;
import lombok.NoArgsConstructor;
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
import java.util.function.Consumer;

import static gg.projecteden.nexus.utils.ItemUtils.isNullOrAir;
import static gg.projecteden.nexus.utils.PlayerUtils.getHotbarContents;
import static gg.projecteden.utils.StringUtils.camelCase;

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
		if (!MaterialTag.TOOLS.isTagged(player.getInventory().getItemInMainHand()))
			return;

		ItemStack[] contents = getHotbarContents(player);
		ItemStack bestTool = getBestTool(Arrays.asList(contents), block, null);

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
	public static ItemStack getBestTool(List<ItemStack> items, Block block, Dev debugger) {
		List<ItemStack> itemStacks = new ArrayList<>(items);
		itemStacks.add(null);

		Consumer<String> debug = message -> {
			if (debugger != null)
				debugger.send(message);
		};

		debug.accept("Block: " + camelCase(block.getType()));
		return Collections.max(itemStacks, Comparator.comparingDouble(item -> {
			debug.accept("Item: " + (item == null ? "null" : camelCase(item.getType())));
			if (isNullOrAir(item)) {
				debug.accept("  0 (is null or air)");
				return 0;
			}

			if (!block.isValidTool(item)) {
				if (!MaterialTag.INFESTED_STONE.isTagged(block.getType()) || !MaterialTag.PICKAXES.isTagged(item)) {
					debug.accept("  -1 (invalid tool)");
					return -1;
				}
			}

			if (MaterialTag.ALL_GLASS.isTagged(block.getType())) {
				if (item.containsEnchantment(Enchant.SILK_TOUCH)) {
					Tool tool = Tool.of(item);
					final int i = (Tool.values().length - tool.ordinal()) + tool.getTools().indexOf(item.getType());
					debug.accept("  %d (silk touch)".formatted(i));
					return i;
				}

				debug.accept("  -1 (glass)");
				return -1;
			}

			if (item.getType().name().contains("GOLDEN")) {
				debug.accept("  -1 (golden tool)");
				return -1;
			}

			float speed = block.getDestroySpeed(item);
			if (speed > 1) {
				debug.accept("  %s (speed)".formatted(speed));
				return speed;
			}

			debug.accept("  -1 (default)");
			return -1;
		}));
	}

}

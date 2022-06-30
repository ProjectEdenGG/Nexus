package gg.projecteden.nexus.features.store.perks.autoinventory.features;

import gg.projecteden.nexus.features.listeners.events.PlayerBlockDigEvent;
import gg.projecteden.nexus.features.store.perks.autoinventory.AutoInventory;
import gg.projecteden.nexus.features.store.perks.autoinventory.AutoInventoryFeature;
import gg.projecteden.nexus.models.autoinventory.AutoInventoryUser;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.ToolType;
import lombok.NoArgsConstructor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;
import static gg.projecteden.nexus.utils.PlayerUtils.getHotbarContents;
import static gg.projecteden.nexus.utils.StringUtils.camelCase;

@NoArgsConstructor
public class AutoTool implements Listener {
	public static final String PERMISSION = "autotool.use";

	@EventHandler
	public void on(PlayerBlockDigEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();

		if (player.getTargetEntity(5) != null)
			return;
		if (block == null)
			return;
		if (!(player.hasPermission(AutoInventory.PERMISSION) || player.hasPermission(PERMISSION)))
			return;

		final AutoInventoryUser user = AutoInventoryUser.of(player);

		if (!user.hasFeatureEnabled(AutoInventoryFeature.AUTOTOOL))
			return;

		final ItemStack mainHand = player.getInventory().getItemInMainHand();

		if (!MaterialTag.TOOLS.isTagged(mainHand)) {
			if (!MaterialTag.SWORDS.isTagged(mainHand))
				return;

			if (!user.isAutoToolIncludeSword())
				return;
		}

		ItemStack[] contents = getHotbarContents(player);
		ItemStack bestTool = getBestTool(Arrays.asList(contents), block, null);

		if (isNullOrAir(bestTool))
			return;
		if (bestTool.equals(mainHand))
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
					ToolType tool = ToolType.of(item);
					final int i = (ToolType.values().length - tool.ordinal()) + tool.getTools().indexOf(item.getType());
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

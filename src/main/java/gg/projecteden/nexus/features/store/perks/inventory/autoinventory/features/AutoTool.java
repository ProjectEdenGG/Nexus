package gg.projecteden.nexus.features.store.perks.inventory.autoinventory.features;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.skills.woodcutting.WoodcuttingManager;
import com.gmail.nossr50.util.player.UserManager;
import gg.projecteden.nexus.features.listeners.events.PlayerBlockDigEvent;
import gg.projecteden.nexus.features.resourcepack.customblocks.customblockbreaking.BrokenBlock;
import gg.projecteden.nexus.features.store.perks.inventory.autoinventory.AutoInventory;
import gg.projecteden.nexus.features.store.perks.inventory.autoinventory.AutoInventoryFeature;
import gg.projecteden.nexus.models.autoinventory.AutoInventoryUser;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.ToolType.ToolGrade;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
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
import java.util.function.Function;

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

			if (!user.getActiveProfile().isAutoToolIncludeSword())
				return;
		}

		wait(player, block, 0);
	}

	void wait(Player player, Block block, int attempts) {
		if (block.getType() == Material.AIR && attempts < 10)
			Tasks.wait(1, () -> wait(player, block, attempts + 1));
		else
			process(player, block);
	}

	void process(Player player, Block block) {
		List<ItemStack> contents = Arrays.stream(PlayerUtils.getHotbarContents(player)).toList();
		PlayerUtils.selectHotbarItem(player, getBestTool(player, contents, block));
	}

	@Nullable
	public static ItemStack getBestTool(Player player, List<ItemStack> items, Block block) {
		List<ItemStack> hotbar = new ArrayList<>(items);
		hotbar.add(null);

		Consumer<String> debug = message -> {
			if (Dev.exists(player))
				PlayerUtils.debug(player, message);
		};

		final Function<ItemStack, Double> getBreakTime = tool -> {
			final McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
			if (mcMMOPlayer.getPlayer() != null)
				if (new WoodcuttingManager(mcMMOPlayer).canUseLeafBlower(tool))
					if (MaterialTag.LEAVES.isTagged(block.getType()))
						return 2d; // Same as shears

			return Double.valueOf(new BrokenBlock(block, player, tool).getBreakTicks());
		};

		final ItemStack currentItem = player.getInventory().getItemInMainHand();
		final double currentToolBreakTime = getBreakTime.apply(currentItem);

		debug.accept("Block: " + StringUtils.camelCase(block.getType()));

		final ItemStack bestTool = Collections.min(hotbar, Comparator.comparingDouble(item -> {
			debug.accept("");
			debug.accept("Item: " + (item == null ? "&cnull" : "&e" + StringUtils.camelCase(item.getType())));
			if (Nullables.isNullOrAir(item)) {
				debug.accept("  MAX_VALUE - 1 (is null or air)");
				return Integer.MAX_VALUE - 1;
			}

			if (!block.isPreferredTool(item)) {
				if (!MaterialTag.INFESTED_STONE.isTagged(block.getType()) || !MaterialTag.PICKAXES.isTagged(item)) {
					debug.accept("  MAX_VALUE (unpreferred tool)");
					return Integer.MAX_VALUE;
				}
			}

			if (MaterialTag.ALL_GLASS.isTagged(block.getType())) {
				if (item.containsEnchantment(Enchant.SILK_TOUCH)) {
					final ToolGrade toolGrade = ToolGrade.of(item);
					if (toolGrade != null) {
						debug.accept("  %d (silk touch)".formatted(toolGrade.ordinal()));
						return toolGrade.ordinal();
					} else {
						debug.accept("  MAX_VALUE - 1 (Unknown tool with silk touch)");
						return Integer.MAX_VALUE - 1;
					}
				}

				debug.accept("  MAX_VALUE (glass)");
				return Integer.MAX_VALUE;
			}

			if (MaterialTag.TOOLS_GOLD.isTagged(item.getType())) {
				debug.accept("  MAX_VALUE (golden tool)");
				return Integer.MAX_VALUE;
			}

			if (MaterialTag.CROPS.isTagged(block.getType()))
				if (MaterialTag.HOES.isNotTagged(item.getType()))
					return Integer.MAX_VALUE;

			final double breakTime = getBreakTime.apply(item);
			if (breakTime >= 1) {
				if (!item.equals(currentItem) && breakTime == currentToolBreakTime) {
					debug.accept("  MAX_VALUE (break time same as current tool)");
					return Integer.MAX_VALUE;
				}

				debug.accept("  %s (breakTime)".formatted(breakTime));
				return breakTime;
			}

			debug.accept("  MAX_VALUE (default)");
			return Integer.MAX_VALUE;
		}));

		debug.accept("");
		debug.accept("");
		debug.accept("Best tool: " + (bestTool == null ? "null" : StringUtils.pretty(bestTool)));
		return bestTool;
	}

}

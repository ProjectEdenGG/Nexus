package gg.projecteden.nexus.features.workbenches;

import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.Consumer;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

// TODO:
// 	- optional crafting recipe
public abstract class CustomBench extends Feature implements Listener {

	@Getter
	@AllArgsConstructor
	public enum CustomBenchType {
		DYE_STATION("Dye Station", CustomMaterial.DYE_STATION, Map.of(BlockFace.DOWN, 1), DyeStation::open),
		;

		private final String name;
		private final CustomMaterial material;
		private final Map<BlockFace, Integer> offsets;
		private final Consumer<Player> interact;

		public void interact(Player player) {
			interact.accept(player);
		}
	}

	@Getter
	public ItemStack item = new ItemBuilder(getBenchType().getMaterial())
		.name(getBenchType().getName())
		.build();

	abstract CustomBenchType getBenchType();

	public static CustomBenchType getCustomBench(ItemStack item) {
		if (isNullOrAir(item))
			return null;

		for (CustomBenchType customBenchType : CustomBenchType.values())
			if (CustomMaterial.of(item) == customBenchType.getMaterial())
				return customBenchType;

		return null;
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (!EquipmentSlot.HAND.equals(event.getHand()))
			return;

		if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
			return;

		Block block = event.getClickedBlock();
		if (isNullOrAir(block))
			return;

		if (!block.getType().equals(Material.BARRIER))
			return;

		Player player = event.getPlayer();
		if (player.isSneaking() && !isNullOrAir(ItemUtils.getTool(player)))
			return;

		ItemFrame itemFrame = PlayerUtils.getTargetItemFrame(player, 5, Map.of(BlockFace.DOWN, 1));

		if (itemFrame == null || isNullOrAir(itemFrame.getItem()))
			return;

		CustomBenchType customBenchType = getCustomBench(itemFrame.getItem());
		if (customBenchType == null)
			return;

		event.setCancelled(true);
		customBenchType.interact(event.getPlayer());
	}

	/*
	 * 	List<ItemFrame> itemFrames = getPossibleItemFrames(block);
	 * 	for (ItemFrame itemFrame : itemFrames) {
	 * 		CustomBenchType customBenchType = getCustomBench(itemFrame.getItem());
	 * 		if (customBenchType == null)
	 * 			return;
	 *
	 * 		event.setCancelled(true);
	 * 		customBenchType.interact(event.getPlayer());
	 * 		return;
	 * 	}
	 *
	 * 	private List<ItemFrame> getPossibleItemFrames(Block clicked){
	 * 		final double searchRadius = 0.5;
	 *
	 * 		List<ItemFrame> found = new ArrayList<>();
	 * 		for (CustomBenchType benchType : CustomBenchType.values()) {
	 * 			Map<BlockFace, Integer> offsets = benchType.getOffsets();
	 * 			if (offsets == null || offsets.isEmpty())
	 * 				continue;
	 *
	 * 			List<Block> blocks = new ArrayList<>();
	 * 			for (BlockFace blockFace : offsets.keySet()) {
	 * 				for (int i = 1; i <= offsets.get(blockFace); i++)
	 * 					blocks.add(clicked.getRelative(blockFace, i));
	 *                        }
	 *
	 * 			for (Block block : blocks) {
	 * 				Collection<ItemFrame> itemFrames = block.getLocation().toCenterLocation().getNearbyEntitiesByType(ItemFrame.class, searchRadius);
	 * 				if (itemFrames.isEmpty())
	 * 					continue;
	 *
	 * 				for (ItemFrame itemFrame : itemFrames) {
	 * 					if (isNullOrAir(itemFrame.getItem()))
	 * 						continue;
	 *
	 * 					found.add(itemFrame);
	 *                }
	 *            }* 		}
	 *
	 * 		return found;
	 * 	}
	 *
	 */
}

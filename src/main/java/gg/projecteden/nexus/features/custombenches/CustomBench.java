package gg.projecteden.nexus.features.custombenches;

import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.CustomModelData;
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

// TODO:
// 	- optional crafting recipe
public abstract class CustomBench extends Feature implements Listener {

	@Getter
	@AllArgsConstructor
	public enum CustomBenchType {
		DYE_STATION("Dye Station", Material.CRAFTING_TABLE, 1, DyeStation::open),
		;

		private final String name;
		private final Material material;
		private final int modelData;
		private final Consumer<Player> interact;

		public void interact(Player player) {
			interact.accept(player);
		}
	}

	@Getter
	public ItemStack item = new ItemBuilder(getBenchType().getMaterial())
		.name(getBenchType().getName())
		.customModelData(getBenchType().getModelData())
		.build();

	abstract CustomBenchType getBenchType();

	public static CustomBenchType getCustomBench(ItemStack item) {
		if (ItemUtils.isNullOrAir(item))
			return null;

		int modelData = CustomModelData.of(item);
		Material material = item.getType();

		for (CustomBenchType customBenchType : CustomBenchType.values()) {
			if (customBenchType.getMaterial().equals(material) && customBenchType.getModelData() == modelData)
				return customBenchType;
		}

		return null;
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (!EquipmentSlot.HAND.equals(event.getHand()))
			return;

		if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
			return;

		Block block = event.getClickedBlock();
		if (BlockUtils.isNullOrAir(block))
			return;

		if (!block.getType().equals(Material.BARRIER))
			return;

		Player player = event.getPlayer();
		if (player.isSneaking() && !ItemUtils.isNullOrAir(ItemUtils.getTool(player)))
			return;


		ItemFrame itemFrame = PlayerUtils.getTargetItemFrame(player, 5, Map.of(BlockFace.DOWN, 1));

		if (itemFrame == null || ItemUtils.isNullOrAir(itemFrame.getItem()))
			return;

		CustomBenchType customBenchType = getCustomBench(itemFrame.getItem());
		if (customBenchType == null)
			return;

		event.setCancelled(true);
		customBenchType.interact(event.getPlayer());
	}
}

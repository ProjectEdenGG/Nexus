package me.pugabyte.nexus.features.listeners;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.MaterialTag.MatchMode;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
public class AutoTool implements Listener {

	public static final List<String> materialOrder = Arrays.asList("NETHERITE", "DIAMOND", "IRON", "GOLDEN", "STONE", "WOODEN");

	@Getter
	public enum Tool {
		PICKAXE(MaterialTag.PICKAXES, MaterialTag.ALL_STONE, MaterialTag.MINERAL_ORES, MaterialTag.SANDSTONES, MaterialTag.FURNACES,
				MaterialTag.ALL_TERRACOTTAS, MaterialTag.CONCRETES, MaterialTag.PRISMARINE, MaterialTag.ALL_CORALS, MaterialTag.PURPURS, MaterialTag.END_STONES,
				new MaterialTag("NETHER_BRICK", MatchMode.CONTAINS).append(Material.NETHERRACK, Material.MAGMA_BLOCK), new MaterialTag(Material.HONEY_BLOCK, Material.OBSIDIAN, Material.ANVIL)),
		AXE(MaterialTag.AXES, MaterialTag.ALL_WOOD, MaterialTag.CHESTS, new MaterialTag(Material.MELON, Material.PUMPKIN, Material.JACK_O_LANTERN, Material.BOOKSHELF)),
		SHOVEL(MaterialTag.SHOVELS, MaterialTag.ALL_DIRT, new MaterialTag(Material.SAND, Material.GRAVEL, Material.CLAY, Material.SOUL_SAND, Material.SOUL_SOIL, Material.SNOW_BLOCK)),
		SHEARS(new MaterialTag(Material.SHEARS), new MaterialTag(Material.COBWEB));

		private final Set<Material> tools = new HashSet<>();
		private final Set<Material> materials = new HashSet<>();

		Tool(MaterialTag tools, MaterialTag... materialTags) {
			this.tools.addAll(tools.getValues());
			for (MaterialTag materialTag : materialTags)
				materials.addAll(materialTag.getValues());
		}

		public static Tool forBlock(Material material) {
			for (Tool tool : values())
				if (tool.getMaterials().contains(material))
					return tool;
			return null;
		}

		public static Tool fromTool(Material material) {
			String[] split = material.name().split("_");
			try {
				return valueOf(split[split.length - 1]);
			} catch (IllegalArgumentException ex) {
				return null;
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;
		if (event.getClickedBlock() == null) return;
		if (!player.hasPermission("autotool.use")) return;
		if (WorldGroup.get(player) != WorldGroup.ONEBLOCK) return;
		if (event.getHand() != EquipmentSlot.HAND) return;

		Tool correctTool = Tool.forBlock(event.getClickedBlock().getType());
		if (correctTool == null)
			return;

		if (!ItemUtils.isNullOrAir(event.getItem())) {
			Tool currentTool = Tool.fromTool(event.getItem().getType());
			if (currentTool == correctTool)
				return;
		}

		ItemStack[] contents = player.getInventory().getContents();

		for (int i = 0; i <= 8; i++) {
			ItemStack slot = contents[i];
			if (ItemUtils.isNullOrAir(slot))
				continue;

			Tool slotTool = Tool.fromTool(slot.getType());
			if (slotTool == correctTool) {
				player.getInventory().setHeldItemSlot(i);
				return;
			}
		}
	}

}

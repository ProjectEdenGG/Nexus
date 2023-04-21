package gg.projecteden.nexus.features.resourcepack.decoration.types.special;

import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.MultiBlock;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableFloorThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.workbenches.DyeStation;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.ItemBuilder.ModelId;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Bed.Part;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@MultiBlock
public class BedAddition extends DyeableFloorThing {
	AdditionType additionType;

	public BedAddition(String name, CustomMaterial material, AdditionType additionType, ColorableType colorableType) {
		super(name, material, colorableType);

		this.additionType = additionType;
	}

	@AllArgsConstructor
	public enum AdditionType {
		FRAME(0),
		CANOPY(1),
		;

		@Getter
		private final int modY;
	}

	static {
		//Nexus.registerListener(new BedAdditionListener());
	}

	private static class BedAdditionListener implements Listener {

		@EventHandler
		public void on(PlayerBedEnterEvent event) { // TODO: figure out what the player is trying to do
			Block block = event.getBed();

			Player player = event.getPlayer();
			ItemStack tool = ItemUtils.getTool(player);
			if (Nullables.isNullOrAir(tool))
				return;

			boolean isPaintbrush = ModelId.of(tool) == DyeStation.getPaintbrush().modelId();
			if (isPaintbrush) { // TODO: on click bed holding paintbrush - if additions > 1, open a menu to select the one they wish to dye
				PlayerUtils.send(player, DecorationUtils.getPrefix() + " this feature doesn't work properly yet.");
				return;
			}

			DecorationConfig config = DecorationConfig.of(tool);
			if (!(config instanceof BedAddition))
				return;

			Map<ItemFrame, DecorationConfig> additions = findBedAdditions(block, player);
			if (additions == null)
				return;

		}

		private static @Nullable Map<ItemFrame, DecorationConfig> findBedAdditions(Block block, Player debugger) {
			Material material = block.getType();

			if (MaterialTag.BEDS.isNotTagged(material))
				return null;

			Bed.Part bedPart = ((Bed) block.getBlockData()).getPart();
			Block block1 = null;
			Bed.Part bedPart1 = null;

			List<Block> adjacentBlocks = BlockUtils.getAdjacentBlocks(block, List.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST));
			for (Block adjacent : adjacentBlocks) {
				if (!adjacent.getType().equals(material))
					continue;

				Bed _bed = (Bed) adjacent.getBlockData();
				if (_bed.getPart() != bedPart) {
					block1 = adjacent;
					bedPart1 = ((Bed) block1.getBlockData()).getPart();
					break;
				}
			}

			if (block1 == null)
				return null;

			Block search = block;
			if (bedPart1 == Part.HEAD)
				search = block1;

			List<ItemFrame> frames = new ArrayList<>();

			for (AdditionType type : AdditionType.values()) {
				Block _block = search.getRelative(BlockFace.UP, type.getModY());
				frames.add((ItemFrame) DecorationUtils.findNearbyItemFrame(_block.getLocation(), false, debugger));
			}

			if (frames.isEmpty())
				return null;

			Map<ItemFrame, DecorationConfig> additions = new HashMap<>();

			for (ItemFrame frame : frames) {
				ItemStack item = frame.getItem();

				if (Nullables.isNullOrAir(item))
					continue;

				DecorationConfig config = DecorationConfig.of(item);
				if (!(config instanceof BedAddition))
					continue;

				additions.put(frame, config);
			}

			if (additions.isEmpty())
				return null;

			return additions;
		}
	}
}

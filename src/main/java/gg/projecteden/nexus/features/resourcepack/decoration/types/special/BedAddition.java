package gg.projecteden.nexus.features.resourcepack.decoration.types.special;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.MultiBlock;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableFloorThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.workbenches.dyestation.DyeStation;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.ItemBuilder.ModelId;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
		Nexus.registerListener(new BedAdditionListener());
	}

	private static class BedAdditionListener implements Listener {

		@EventHandler
		public void on(PlayerBedEnterEvent event) { // TODO: figure out what the player is trying to do
			Player player = event.getPlayer();
			//
			if (!PlayerUtils.Dev.WAKKA.is(player))
				return;
			//

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

			Block block = event.getBed();
			Map<ItemFrame, DecorationConfig> additions = findBedAdditions(block, player);
			if (additions == null)
				return;

			additions.forEach((itemFrame, _config) -> PlayerUtils.Dev.WAKKA.send("- " + _config.getName()));

		}

		private static @Nullable Map<ItemFrame, DecorationConfig> findBedAdditions(Block head, Player debugger) {
			if (MaterialTag.BEDS.isNotTagged(head.getType()))
				return null;

			Bed bed = (Bed) head.getBlockData();
			if (bed.getPart() == Part.FOOT) {
				head = head.getRelative(bed.getFacing().getOppositeFace());
			}

			return getBedAdditions(head, bed, debugger);
		}

		@Nullable
		private static Map<ItemFrame, DecorationConfig> getBedAdditions(Block head, Bed bed, Player debugger) {
			Map<ItemFrame, DecorationConfig> result = new HashMap<>();
			List<ItemFrame> frames = findItemFrames(head, bed, debugger);
			if (frames == null)
				return null;

			for (ItemFrame frame : frames) {
				ItemStack item = frame.getItem();

				if (Nullables.isNullOrAir(item))
					continue;

				DecorationConfig config = DecorationConfig.of(item);
				if (!(config instanceof BedAddition))
					continue;

				result.put(frame, config);
			}

			if (result.isEmpty())
				return null;

			return result;
		}

		@Nullable
		private static List<ItemFrame> findItemFrames(Block head, Bed bed, Player debugger) {
			List<ItemFrame> frames = new ArrayList<>();

			for (AdditionType type : AdditionType.values()) {
				Block _block = head.getRelative(BlockFace.UP, type.getModY());
				frames.add((ItemFrame) DecorationUtils.findNearbyItemFrame(_block.getLocation(), false, debugger));
			}

			if (frames.isEmpty()) {
				// Try searching for a bed on the "left"
				BlockFace left = BlockUtils.rotateCounterClockwise(bed.getFacing());
				head = head.getRelative(left);
				if (MaterialTag.BEDS.isNotTagged(head.getType()))
					return null;

				bed = (Bed) head.getBlockData();
				if (bed.getPart() == Part.FOOT)
					return null;

				for (AdditionType type : AdditionType.values()) {
					Block _block = head.getRelative(BlockFace.UP, type.getModY());
					frames.add((ItemFrame) DecorationUtils.findNearbyItemFrame(_block.getLocation(), false, debugger));
				}

				if (frames.isEmpty())
					return null;
			}
			return frames;
		}
	}
}

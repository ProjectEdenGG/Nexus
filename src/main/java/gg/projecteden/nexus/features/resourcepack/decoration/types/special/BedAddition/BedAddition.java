package gg.projecteden.nexus.features.resourcepack.decoration.types.special.BedAddition;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Addition;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.MultiBlock;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableFloorThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.workbenches.dyestation.DyeStation;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Utils.ItemFrameRotation;
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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: WHEN SWAPPING / PLACING AND CHECKING FOR A DOUBLE BED, MAKE SURE THEY ARE FACING THE SAME DIRECTION
// TODO: WHEN PLACING A SINGLE, MAKE SURE THERE'S NOT ALSO A DOUBLE PLACED ON THE BED NEXT TO IT
@Addition
@MultiBlock
public class BedAddition extends DyeableFloorThing {
	boolean isDouble;
	AdditionType additionType;

	public BedAddition(String name, CustomMaterial material, AdditionType additionType, ColorableType colorableType) {
		this(name, material, additionType, false, colorableType);
	}

	public BedAddition(String name, CustomMaterial material, AdditionType additionType, boolean isDouble, ColorableType colorableType) {
		super(name, material, colorableType);

		this.isDouble = isDouble;
		this.additionType = additionType;

		this.lore = new ArrayList<>(List.of("&3Can only be placed on a bed", decorLore));
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

	public static class BedAdditionListener implements Listener {

		// TODO: ALSO BREAK THE 2ND BED?
		@EventHandler
		public void on(BlockBreakEvent event) {
			Player player = event.getPlayer();
			Block block = event.getBlock();

			if (MaterialTag.BEDS.isNotTagged(block))
				return;

			DecorationUtils.debug(player, "BedBreakEvent");

			org.bukkit.block.Bed bed = (org.bukkit.block.Bed) block.getState();
			Bed bedData = (Bed) bed.getBlockData();
			if (bedData.getPart() == Part.FOOT) {
				block = block.getRelative(bedData.getFacing());
				bed = (org.bukkit.block.Bed) block.getState();
				bedData = (Bed) bed.getBlockData();
			}

			Map<ItemFrame, DecorationConfig> additions = getBedAdditions(block, bedData, player);
			if (Nullables.isNullOrEmpty(additions)) {
				DecorationUtils.debug(player, "No bed additions to destroy");
				return;
			}

			for (ItemFrame itemFrame : additions.keySet()) {
				Decoration decoration = new Decoration(additions.get(itemFrame), itemFrame);

				boolean destroyed = decoration.destroy(player, BlockFace.UP, player);
				if (!destroyed) {
					DecorationUtils.debug(player, "not destroyed");
					event.setCancelled(true);
					return;
				}
			}
		}

		@EventHandler
		public void on(PlayerBedEnterEvent event) {
			Player player = event.getPlayer();
			Block block = event.getBed();
			Material material = block.getType();

			if (MaterialTag.BEDS.isNotTagged(material))
				return;

			DecorationUtils.debug(player, "BedEnterEvent");

			ItemStack tool = ItemUtils.getTool(player);
			if (Nullables.isNullOrAir(tool))
				return;

			boolean isPaintbrush = DyeStation.isMagicPaintbrush(tool);

			DecorationConfig config = DecorationConfig.of(tool);
			boolean isBedAddition = (config instanceof BedAddition);

			if (!isPaintbrush && !isBedAddition)
				return;

			org.bukkit.block.Bed bed = (org.bukkit.block.Bed) block.getState();
			Bed bedData = (Bed) bed.getBlockData();
			if (bedData.getPart() == Part.FOOT) {
				block = block.getRelative(bedData.getFacing());
				bed = (org.bukkit.block.Bed) block.getState();
				bedData = (Bed) bed.getBlockData();
			}

			Map<ItemFrame, DecorationConfig> additions = getBedAdditions(block, bedData, player);

			if (!Nullables.isNullOrEmpty(additions)) {
				if (isPaintbrush) {
					tryPaint(event, player, block, tool, additions);
					return;
				}

				DecorationUtils.debug(player, "Edit bed additions");
				BedAddition toolAddition = (BedAddition) config;

				if (!additions.isEmpty()) {
					DecorationUtils.debug(player, "trying swapping frames");

					if (trySwap(event, player, tool, additions, toolAddition)) {
						return;
					}
				}
			}

			DecorationUtils.debug(player, "trying placing frame...");
			tryPlace(event, player, block, tool, config, bedData);
		}

		@Nullable
		public static Map<ItemFrame, DecorationConfig> getBedAdditions(Block head, Bed bedData, Player debugger) {
			Map<ItemFrame, DecorationConfig> result = new HashMap<>();
			List<ItemFrame> frames = findItemFrames(head, bedData, debugger);
			if (Nullables.isNullOrEmpty(frames))
				return null;

			for (ItemFrame frame : frames) {
				if (frame == null)
					continue;

				ItemStack item = frame.getItem();
				if (Nullables.isNullOrAir(item))
					continue;

				DecorationConfig config = DecorationConfig.of(item);
				if (!(config instanceof BedAddition))
					continue;

				result.put(frame, config);
			}

			return result;
		}

		@Nullable
		private static List<ItemFrame> findItemFrames(Block head, Bed bedData, Player debugger) {
			List<ItemFrame> frames = getItemFramesAt(head, debugger);

			if (frames.isEmpty()) {
				// Try searching for a bed on the "left"
				BlockFace left = BlockUtils.rotateCounterClockwise(bedData.getFacing());
				head = head.getRelative(left);
				if (MaterialTag.BEDS.isNotTagged(head.getType()))
					return null;

				bedData = (Bed) head.getBlockData();
				if (bedData.getPart() == Part.FOOT)
					return null;

				frames = getItemFramesAt(head, debugger);
			}
			return frames;
		}

		@NotNull
		private static List<ItemFrame> getItemFramesAt(Block head, Player debugger) {
			List<ItemFrame> frames = new ArrayList<>();

			for (AdditionType type : AdditionType.values()) {
				Block _block = head.getRelative(BlockFace.UP, type.getModY());
				ItemFrame itemFrame = (ItemFrame) DecorationUtils.findNearbyItemFrame(_block.getLocation(), false, debugger);
				if (itemFrame != null)
					frames.add(itemFrame);
			}

			return frames;
		}

		private static void tryPaint(PlayerBedEnterEvent event, Player player, Block block, ItemStack tool, Map<ItemFrame, DecorationConfig> additions) {
			DecorationUtils.debug(player, "Dyeing something...");
			if (additions.size() > 1) {
				event.setCancelled(true);
				new BedPaintProvider(tool, block, additions).open(player);
				return;
			}

			ItemFrame itemFrame = additions.keySet().stream().toList().get(0);

			Decoration decoration = new Decoration(additions.get(itemFrame), itemFrame);
			boolean painted = decoration.paint(player, tool);

			if (painted)
				event.setCancelled(true);
		}

		private static boolean trySwap(PlayerBedEnterEvent event, Player player, ItemStack tool, Map<ItemFrame, DecorationConfig> additions, BedAddition toolAddition) {
			for (ItemFrame itemFrame : additions.keySet()) {
				Decoration decoration = new Decoration(additions.get(itemFrame), itemFrame);

				DecorationConfig config = decoration.getConfig();
				BedAddition frameAddition = (BedAddition) config;
				if (frameAddition.isDouble && !toolAddition.isDouble)
					continue;

				if (!frameAddition.isDouble && toolAddition.isDouble)
					continue;

				event.setCancelled(true);

				ItemStack item = decoration.getItemDrop(player);
				ItemStack frameItem = config.getFrameItem(player, tool);

				decoration.getItemFrame().setItem(frameItem, false);
				tool.subtract();
				PlayerUtils.giveItem(player, item);
				return true;
			}
			return false;
		}

		private static void tryPlace(PlayerBedEnterEvent event, Player player, Block block, ItemStack tool, DecorationConfig config, Bed bedData) {
			BedAddition bedAddition = (BedAddition) config;

			if (bedAddition.isDouble) {
				BlockFace left = BlockUtils.rotateCounterClockwise(bedData.getFacing());
				Block _block = block.getRelative(left);
				if (MaterialTag.BEDS.isTagged(_block.getType())) {
					Bed _bedData = (Bed) _block.getBlockData();
					if (_bedData.getPart() == Part.HEAD) {
						block = _block;
					}
				}
			}

			Block below = block.getRelative(BlockFace.DOWN);
			ItemFrameRotation rotation = ItemFrameRotation.of(bedData.getFacing());
			boolean placed = config.place(player, below, BlockFace.UP, tool, rotation, true);
			if (placed)
				event.setCancelled(true);
		}
	}
}

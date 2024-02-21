package gg.projecteden.nexus.features.resourcepack.decoration.types.special.BedAddition;

import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.BedAddition.BedAddition.AdditionType;
import gg.projecteden.nexus.features.workbenches.dyestation.DyeStation;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Utils.ItemFrameRotation;
import lombok.Builder;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Bed.Part;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BedAdditionUtils {

	@Data
	public static class BedInteractionData {
		Player player;
		Block origin;

		Block bedRight;
		Bed bedRightData;
		Map<ItemFrame, DecorationConfig> additionsRight;

		Block bedLeft;
		Bed bedLeftData;
		Map<ItemFrame, DecorationConfig> additionsLeft;

		Map<ItemFrame, DecorationConfig> additionsBoth;

		ItemStack tool;
		boolean isPaintbrush = false;
		DecorationConfig toolConfig;
		BedAddition toolAddition;

		boolean adjustBeds;

		@Builder
		public BedInteractionData(Player player, Block origin, ItemStack tool, boolean adjustBeds) {
			this.player = player;
			this.origin = origin;
			this.tool = tool;
			this.adjustBeds = adjustBeds;

			// Tool
			if (this.tool != null) {
				this.isPaintbrush = DyeStation.isMagicPaintbrush(this.tool);
				this.toolConfig = DecorationConfig.of(this.tool);
				if (this.toolConfig instanceof BedAddition bedAddition)
					this.toolAddition = bedAddition;
			}

			// Bed Right
			this.bedRight = origin;
			this.bedRightData = (Bed) origin.getBlockData();
			if (this.bedRightData.getPart() == Part.FOOT) {
				this.bedRight = this.bedRight.getRelative(this.bedRightData.getFacing());
				this.bedRightData = (Bed) this.bedRight.getBlockData();
			}

			// Bed Left
			BlockFace leftFace = BlockUtils.rotateCounterClockwise(this.bedRightData.getFacing());
			Block leftBlock = this.bedRight.getRelative(leftFace);
			if (MaterialTag.BEDS.isTagged(leftBlock.getType())) {
				Bed _bedData = (Bed) leftBlock.getBlockData();
				if (_bedData.getPart() == Part.HEAD && this.bedRightData.getFacing().equals(_bedData.getFacing())) {
					this.bedLeft = leftBlock;
					this.bedLeftData = _bedData;
				}
			}

			// Bed Adjustments
			if (adjustBeds && this.bedLeft == null) {
				BlockFace rightFace = leftFace.getOppositeFace();
				Block rightBlock = this.bedRight.getRelative(rightFace);
				if (MaterialTag.BEDS.isTagged(rightBlock.getType())) {
					Bed _bedData = (Bed) rightBlock.getBlockData();
					if (_bedData.getPart() == Part.HEAD && this.bedRightData.getFacing().equals(_bedData.getFacing())) {
						this.bedLeft = this.bedRight;
						this.bedLeftData = this.bedRightData;
						this.bedRight = rightBlock;
						this.bedRightData = _bedData;
					}
				}
			}

			refreshAdditions();
		}

		public boolean isToolUnrelated() {
			if (tool == null)
				return true;

			return !this.isPaintbrush && this.toolAddition == null;
		}

		public void refreshAdditions() {
			this.additionsLeft = new HashMap<>();
			this.additionsRight = new HashMap<>();
			this.additionsBoth = new HashMap<>();

			if (this.bedLeft != null)
				this.additionsLeft = getBedAdditions(this.bedLeft);
			if (this.bedRight != null)
				this.additionsRight = getBedAdditions(this.bedRight);

			this.additionsBoth.putAll(additionsRight);
			this.additionsBoth.putAll(additionsLeft);
		}

		public boolean tryPaint(PlayerBedEnterEvent event) {
			if (!this.isPaintbrush) {
				DecorationUtils.debug(player, "- not a paintbrush");
				return false;
			}

			if (this.additionsBoth.isEmpty()) {
				DecorationUtils.debug(player, "- nothing to paint");
				return false;
			}

			if (additionsBoth.size() > 1) {
				DecorationUtils.debug(player, "- opening menu");
				event.setCancelled(true);
				new BedPaintProvider(this).open(player);
				return true;
			}

			ItemFrame itemFrame = this.additionsBoth.keySet().stream().toList().get(0);
			DecorationConfig config = this.additionsBoth.get(itemFrame);
			if (config == null) {
				DecorationUtils.debug(player, "- config is null ??");
				return false;
			}

			Decoration decoration = new Decoration(config, itemFrame);
			boolean painted = decoration.paint(player, tool);

			if (painted) {
				event.setCancelled(true);
				return true;
			}

			return false;
		}

		public boolean trySwap(PlayerBedEnterEvent event) {
			if (this.toolAddition == null) {
				DecorationUtils.debug(this.player, "- tool is not a bed addition");
				return false;
			}

			if (this.additionsBoth.isEmpty()) {
				DecorationUtils.debug(this.player, "- nothing to swap 1");
				return false;
			}

			boolean isToolWide = this.toolAddition.isWide();
			Map<ItemFrame, DecorationConfig> additions = new HashMap<>(this.additionsBoth);

			for (ItemFrame _itemFrame : additions.keySet()) {
				DecorationConfig _config = additions.get(_itemFrame);
				BedAddition bedAddition = (BedAddition) _config;
				boolean isFrameWide = bedAddition.isWide();

				if (isFrameWide && !isToolWide) { // trying to place a single over a wide
					DecorationUtils.debug(this.player, "- something's in the way 1");
					return false;
				}

				if (!isFrameWide && isToolWide) { // trying to place a wide over a single
					DecorationUtils.debug(this.player, "- something's in the way 2");
					return false;
				}

				if (this.toolAddition.getAdditionType() != bedAddition.getAdditionType()) { // can only swap of same type
					DecorationUtils.debug(this.player, "-- not of same type, ignoring");
					additions.remove(_itemFrame);
					continue;
				}

				if (!isToolWide) { // skip other single beds on wide locations
					Location bedLoc = this.getBedRight().getLocation().toBlockLocation();
					Location frameLoc = _itemFrame.getLocation().toBlockLocation();

					if (!LocationUtils.isFuzzyEqual(bedLoc, frameLoc)) {
						DecorationUtils.debug(this.player, "-- not fuzzy equal, ignoring");
						additions.remove(_itemFrame);
						continue;
					}
				}
			}

			if (additions.isEmpty()) {
				DecorationUtils.debug(this.player, "- nothing to swap 2");
				return false;
			}

			if (additions.size() > 1) {
				DecorationUtils.error(player, DecorationUtils.getPrefix() + "&cThis message should never occur. (BedAdditions)");
				return false;
			}

			ItemFrame itemFrame = additions.keySet().stream().toList().get(0);

			if (itemFrame == null) {
				DecorationUtils.debug(this.player, "- couldn't find a decoration to swap");
				return false;
			}

			DecorationConfig config = additions.get(itemFrame);
			Decoration decoration = new Decoration(config, itemFrame);
			ItemStack item = decoration.getItemDrop(player);
			ItemStack frameItem = config.getFrameItem(player, tool);

			decoration.getItemFrame().setItem(frameItem, false);
			tool.subtract();
			PlayerUtils.giveItem(player, item);

			event.setCancelled(true);
			return true;
		}

		// TODO: ONLY ALLOW PLACEMENTS OF WIDE BEDS, IF
		//  	- THERE ARE 2 BEDS
		// 		- BOTH BEDS ARE FACING THE SAME DIRECTION
		public boolean tryPlace(PlayerBedEnterEvent event) {
			if (this.toolAddition == null) {
				DecorationUtils.debug(this.player, "- tool is not a bed addition");
				return false;
			}

			boolean isToolWide = this.toolAddition.isWide();
			Map<ItemFrame, DecorationConfig> additions = new HashMap<>(this.additionsBoth);

			if (!additions.isEmpty()) { // check if we can place here
				for (ItemFrame _itemFrame : additions.keySet()) {
					DecorationConfig _config = additions.get(_itemFrame);
					BedAddition bedAddition = (BedAddition) _config;
					boolean isFrameWide = bedAddition.isWide();

					if (isFrameWide && !isToolWide) { // trying to place a single over a wide
						DecorationUtils.debug(this.player, "- something's in the way 1");
						return false;
					}

					if (!isFrameWide && isToolWide) { // trying to place a wide over a single
						DecorationUtils.debug(this.player, "- something's in the way 2");
						return false;
					}

					Location bedLoc = this.getBedRight().getLocation().clone().add(0, bedAddition.getAdditionType().getModY(), 0).toBlockLocation();
					Location frameLoc = _itemFrame.getLocation().toBlockLocation();
					if (LocationUtils.isFuzzyEqual(bedLoc, frameLoc)) {
						DecorationUtils.debug(this.player, "- something's in the way 3");
						additions.remove(_itemFrame);
						return false;
					}
				}
			}

			Block block = this.bedRight;
			Bed blockData = this.bedRightData;
			if (isToolWide) {
				if (this.bedLeft == null) {
					DecorationUtils.debug(this.player, "- missing a bed");
					return false;
				}

				block = this.bedLeft;
				blockData = this.bedLeftData;
			}

			Block below = block.getRelative(BlockFace.DOWN);
			ItemFrameRotation rotation = ItemFrameRotation.of(blockData.getFacing());
			boolean placed = toolConfig.place(event.getPlayer(), below, BlockFace.UP, tool, rotation, true);

			if (placed) {
				event.setCancelled(true);
				return true;
			}

			return false;
		}

		Map<ItemFrame, DecorationConfig> getBedAdditions(Block head) {
			Map<ItemFrame, DecorationConfig> result = new HashMap<>();
			List<ItemFrame> frames = BedAdditionUtils.getItemFramesAt(head, this.player);
			if (Nullables.isNullOrEmpty(frames))
				return new HashMap<>();

			for (ItemFrame frame : frames) {
				if (frame == null)
					continue;

				ItemStack item = frame.getItem();
				if (Nullables.isNullOrAir(item))
					continue;

				DecorationConfig config = DecorationConfig.of(item);
				if (config == null)
					continue;

				if (!(config instanceof BedAddition))
					continue;

				result.put(frame, config);
			}

			return result;
		}
	}

	@NotNull
	static List<ItemFrame> getItemFramesAt(Block head, Player debugger) {
		List<ItemFrame> frames = new ArrayList<>();

		for (AdditionType type : AdditionType.values()) {
			Block _block = head.getRelative(BlockFace.UP, type.getModY());
			ItemFrame itemFrame = (ItemFrame) DecorationUtils.findNearbyItemFrame(_block.getLocation(), false, debugger);
			if (itemFrame != null)
				frames.add(itemFrame);
		}

		return frames;
	}

	@Nullable
	static List<ItemFrame> findItemFrames(Block head, Bed bedData, Player debugger) {
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
}
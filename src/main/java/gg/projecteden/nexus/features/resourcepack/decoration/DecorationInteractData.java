package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.nexus.features.clientside.models.ClientSideItemFrame;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Interactable;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent.InteractType;
import gg.projecteden.nexus.features.workbenches.dyestation.DyeStation;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ItemSetting;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Slab.Type;
import org.bukkit.block.data.type.Snow;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

@Data
public class DecorationInteractData {
	public static final int MAX_RADIUS = 4; // Since model max size = 3x3x3 blocks, 4 should be enough
	private Player player;
	private EquipmentSlot hand;
	private Decoration decoration;
	private Block block;
	private BlockFace blockFace;
	private ItemStack tool;
	private BlockFace blockFaceOverride;

	public DecorationInteractData(Block block, BlockFace blockFace) {
		this(null, EquipmentSlot.HAND, null, block, blockFace, null, null);
	}

	@Builder
	public DecorationInteractData(Player player, EquipmentSlot hand, Decoration decoration, Block block, BlockFace blockFace, BlockFace blockFaceOverride, ItemStack tool) {
		this.player = player;
		this.hand = hand;
		this.decoration = decoration;
		this.block = block;
		this.blockFace = blockFace;
		this.blockFaceOverride = blockFaceOverride;
		this.tool = tool;

		if (this.decoration == null) {
			ItemFrame itemFrame = (ItemFrame) DecorationUtils.getItemFrame(block, MAX_RADIUS, blockFaceOverride, player, false);
			ItemStack item;
			if (itemFrame != null) {
				item = itemFrame.getItem();
				if (Nullables.isNullOrAir(item))
					return;

				final DecorationConfig config = DecorationConfig.of(item);
				if (config != null)
					this.decoration = new Decoration(config, itemFrame);

			} else { // Clientside Entities
				ClientSideItemFrame clientSideItemFrame = (ClientSideItemFrame) DecorationUtils.getItemFrame(block, MAX_RADIUS, blockFaceOverride, player, true);
				if (clientSideItemFrame == null)
					return;

				final DecorationConfig config = DecorationConfig.of(clientSideItemFrame.content());
				if (config != null) {
					Rotation rotation = clientSideItemFrame.getBukkitRotation();
					this.decoration = new Decoration(config, null, rotation, null);
				}
			}
		}
	}

	public boolean interact(InteractType type) {
		DecorationLang.debug(player, "interacting...");
		return decoration.interact(player, block, type, getTool());
	}

	public boolean destroy() {
		DecorationLang.debug(player, "destroying...");
		return decoration.destroy(player, getBlockFaceOverride());
	}

	public boolean place() {
		DecorationLang.debug(player, "placing...");
		boolean placed = decoration.place(player, hand, block, blockFace, tool, null, false);
		if (!placed) {
			DecorationLang.debug(player, "failed to place decoration");
			player.swingMainHand();
		}

		return placed;
	}

	boolean isDecorationValid() {
		return decoration != null && decoration.getBukkitRotation() != null;
	}

	public @NonNull Location getLocation() {
		if (decoration.getItemFrame() != null)
			return decoration.getItemFrame().getLocation();
		if (block != null)
			return block.getLocation();

		return player.getLocation();
	}

	private static final Set<Material> GSitMaterials = new MaterialTag()
			.append(Tag.STAIRS)
			.append(Tag.SLABS)
			.append(Tag.WOOL_CARPETS)
			.append(Material.MOSS_CARPET)
			.append(Material.SNOW)
			.getValues();

	// change interactable depending on interacted blockface
	public boolean isInteractable() {
		Block _block = getBlock();
		Material material = _block.getType();

		CustomBlock customBlock = CustomBlock.from(_block);
		if (customBlock != null)
			return customBlock == CustomBlock.NOTE_BLOCK; // TODO: Check if Custom Block is Interactable (new feature)

		if (GSitMaterials.contains(material)) {
			boolean interactingOnTop = this.blockFace == BlockFace.UP;
			boolean interactingOnBottom = this.blockFace == BlockFace.DOWN;

			if (MaterialTag.STAIRS.isTagged(material)) {
				if (_block.getBlockData() instanceof Bisected bisected) {
					if (interactingOnTop) {
						if (bisected.getHalf() == Half.BOTTOM)
							return true;
					}

					if (interactingOnBottom) {
						if (bisected.getHalf() == Half.TOP)
							return true;
					}
				}
			} else if (MaterialTag.SLABS.isTagged(material)) {
				if (_block.getBlockData() instanceof Slab slab) {
					if (interactingOnTop) {
						if (slab.getType() == Type.BOTTOM)
							return true;
					}

					if (interactingOnBottom) {
						if (slab.getType() == Type.TOP)
							return true;
					}
				}
			} else if (Material.SNOW == material) {
				if (_block.getBlockData() instanceof Snow snow) {
					if (interactingOnTop) {
						if (snow.getLayers() != snow.getMaximumLayers())
							return true;
					}
				}
			} else
				return true;
		} else {
			if (MaterialTag.INTERACTABLES.isTagged(material))
				return true;
		}

		if (getDecoration() != null) {
			DecorationConfig config = getDecoration().getConfig();
			if (config instanceof Interactable) {
				return true;
			}
		}

		return false;
	}

	public boolean isToolInteractable() {
		if (Nullables.isNullOrAir(tool))
			return true;

		if (new ItemBuilder(tool).is(ItemSetting.INTERACTABLE))
			return true;

		if (decoration != null && decoration.getConfig() != null)
			if (decoration.getConfig().shouldInteract(tool))
				return true;

		return false;
	}

	public boolean isSpecialTool() {
		boolean sneaking = player.isSneaking();
		if (Nullables.isNullOrAir(tool))
			return false;

		if (DyeStation.isPaintbrush(tool))
			return true;

		return false;
	}

	public boolean doPlayHitSound() {
		// Disables sound on multiblock wallthings when attempting to break from any other face than hanged face
		if (decoration.getConfig().isMultiBlockWallThing() && decoration.getItemFrame().getFacing().getOppositeFace() != blockFaceOverride)
			return false;

		return true;
	}
}

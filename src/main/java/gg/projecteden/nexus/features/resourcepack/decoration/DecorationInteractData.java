package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.nexus.features.clientside.models.ClientSideItemFrame;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Interactable;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent.InteractType;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
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
import org.bukkit.inventory.ItemStack;

import java.util.Set;

import static gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils.debug;

@Data
public class DecorationInteractData {
	public static final int MAX_RADIUS = 4; // Since model max size = 3x3x3 blocks, 4 should be enough
	private Player player;
	private Decoration decoration;
	private Block block;
	private BlockFace blockFace;
	private ItemStack tool;
	private BlockFace blockFaceOverride;

	public DecorationInteractData(Block block, BlockFace blockFace) {
		this(null, null, block, blockFace, null, null);
	}

	@Builder
	public DecorationInteractData(Player player, Decoration decoration, Block block, BlockFace blockFace, BlockFace blockFaceOverride, ItemStack tool) {
		this.player = player;
		this.decoration = decoration;
		this.block = block;
		this.blockFace = blockFace;
		this.blockFaceOverride = blockFaceOverride;
		this.tool = tool;

		if (this.decoration == null) {
			ItemFrame itemFrame = (ItemFrame) DecorationUtils.getItemFrame(block, MAX_RADIUS, blockFaceOverride, player, false);
			ItemStack item;
			if (itemFrame == null) {
				// Clientside Entities
				ClientSideItemFrame clientSideItemFrame = (ClientSideItemFrame) DecorationUtils.getItemFrame(block, MAX_RADIUS, blockFaceOverride, player, true);
				if (clientSideItemFrame == null)
					return;

				final DecorationConfig config = DecorationConfig.of(clientSideItemFrame.content());
				if (config != null) {
					Rotation rotation = clientSideItemFrame.getBukkitRotation();
					this.decoration = new Decoration(config, null, rotation);
				}
			} else {
				item = itemFrame.getItem();
				if (Nullables.isNullOrAir(item))
					return;

				final DecorationConfig config = DecorationConfig.of(item);
				if (config != null)
					this.decoration = new Decoration(config, itemFrame);
			}
		}
	}

	public boolean interact(InteractType type) {
		debug(player, "interacting...");
		return decoration.interact(player, block, type, getTool());
	}

	public boolean destroy(Player debugger) {
		debug(player, "destroying...");
		return decoration.destroy(player, getBlockFaceOverride(), debugger);
	}

	public boolean place() {
		debug(player, "placing...");
		boolean placed = decoration.getConfig().place(player, block, blockFace, tool);
		if (!placed) {
			debug(player, "failed to place decoration");
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

	public boolean playerCanEdit() {
		return PlayerUtils.canEdit(player, getLocation());
	}

	private static final Set<Material> GSitMaterials = new MaterialTag(Tag.STAIRS)
			.append(Tag.SLABS)
			.append(Tag.WOOL_CARPETS)
			.append(Material.MOSS_CARPET)
			.append(Material.SNOW)
			.getValues();

	private static final Set<Material> NOT_ACTUALLY_INTERACTABLE = Set.of(Material.BEEHIVE, Material.BEE_NEST);

	public boolean isInteractable() {
		Block _block = getBlock();
		Material material = _block.getType();

		if (GSitMaterials.contains(material)) {
			if (MaterialTag.STAIRS.isTagged(material)) {
				if (_block.getBlockData() instanceof Bisected bisected) {
					if (bisected.getHalf() == Half.BOTTOM)
						return true;
				}
			} else if (MaterialTag.SLABS.isTagged(material)) {
				if (_block.getBlockData() instanceof Slab slab) {
					if (slab.getType() == Type.BOTTOM)
						return true;
				}
			} else if (Material.SNOW == material) {
				if (_block.getBlockData() instanceof Snow snow) {
					if (snow.getLayers() != snow.getMaximumLayers())
						return true;
				}
			} else
				return true;
		} else {
			if (NOT_ACTUALLY_INTERACTABLE.contains(material))
				return false;

			if (material.isInteractable())
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
}

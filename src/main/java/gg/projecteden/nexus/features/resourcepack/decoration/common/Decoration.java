package gg.projecteden.nexus.features.resourcepack.decoration.common;

import de.tr7zw.nbtapi.NBTItem;
import gg.projecteden.nexus.features.commands.staff.WorldGuardEditCommand;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationDestroyEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationPlaceEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationSitEvent;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.trust.Trust.Type;
import gg.projecteden.nexus.models.trust.TrustService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Utils.ItemFrameRotation;
import gg.projecteden.nexus.utils.WorldGroup;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class Decoration {
	private static final String nbtOwnerKey = "DecorationOwner";
	protected String name;
	protected int modelData;
	protected @NonNull Material material = Material.PAPER;
	protected List<String> lore = Collections.singletonList("Decoration");

	protected List<Hitbox> hitboxes = Hitbox.NONE();
	protected DisabledRotation disabledRotation = DisabledRotation.NONE;
	protected List<DisabledPlacement> disabledPlacements = new ArrayList<>();

	public Decoration(String name, int modelData, @NotNull Material material, List<Hitbox> hitboxes) {
		this.name = name;
		this.modelData = modelData;
		this.material = material;
		this.hitboxes = hitboxes;

		if (this.isMultiBlock())
			this.disabledRotation = DisabledRotation.DEGREE_45;
	}

	public Decoration(String name, int modelData, List<Hitbox> hitboxes) {
		this(name, modelData, Material.PAPER, hitboxes);
	}

	public Decoration(String name, int modelData) {
		this(name, modelData, Hitbox.NONE());
	}

	public Decoration(String name, int modelData, Material material) {
		this(name, modelData, material, Hitbox.NONE());
	}

	public ItemStack getItem() {
		ItemBuilder decor = new ItemBuilder(material).customModelData(modelData).name(name).lore(lore);

		if (this instanceof Colorable colorable && colorable.isColorable())
			decor.dyeColor(colorable.getType().getColor());

		return decor.build();
	}

	public boolean isMultiBlock() {
		return this.getClass().getAnnotation(MultiBlock.class) != null;
	}

	private boolean isSeat() {
		return this instanceof Seat;
	}

	//

	public boolean place(Player player, Block block, BlockFace clickedFace, ItemStack item) {
		if (!isValidPlacement(clickedFace))
			return false;

		Location origin = block.getRelative(clickedFace).getLocation().clone();

		// TODO: maybe add a toggleable to this, allowing for furniture to be placed inside of other blocks-- wouldn't replace
		ItemFrameRotation frameRotation = findValidFrameRotation(origin, ItemFrameRotation.of(player));
		if (frameRotation == null)
			return false;
		//

		DecorationPlaceEvent placeEvent = new DecorationPlaceEvent(player, origin, this, item);
		if (!placeEvent.callEvent())
			return false;

		ItemStack _item = placeEvent.getItem().clone();
		_item.setAmount(1);
		if (!player.getGameMode().equals(GameMode.CREATIVE))
			placeEvent.getItem().subtract();

		NBTItem nbtItem = new NBTItem(_item);
		nbtItem.setString(nbtOwnerKey, player.getUniqueId().toString());

		ItemFrame itemFrame = (ItemFrame) block.getWorld().spawnEntity(origin, EntityType.ITEM_FRAME);
		itemFrame.setFacingDirection(clickedFace, true);
		itemFrame.setRotation(frameRotation.getRotation());
//		itemFrame.setVisible(false);
		itemFrame.setGlowing(false);
		itemFrame.setSilent(true);
		itemFrame.setItem(nbtItem.getItem(), false);

		Hitbox.place(getHitboxes(), origin, frameRotation.getBlockFace());
		return true;
	}

	public boolean destroy(@NonNull Player player, @NonNull ItemFrame itemFrame) {
		World world = player.getWorld();
		ItemStack item = itemFrame.getItem().clone();
		Location origin = itemFrame.getLocation().toBlockLocation().clone();

		NBTItem nbtItem = new NBTItem(item);
		String ownerUUID = nbtItem.getString(nbtOwnerKey);

		DecorationDestroyEvent destroyEvent = new DecorationDestroyEvent(player, origin, this, item, ownerUUID);
		if (!destroyEvent.callEvent())
			return false;

		if (this instanceof Seat seat) {
			if (seat.isOccupied(this, itemFrame)) {
				PlayerUtils.send(player, DecorationUtils.getPrefix() + "&cSeat is occupied");
				return false;
			}
		}

		if (!canEdit(player, ownerUUID, origin)) {
			PlayerUtils.send(player, DecorationUtils.getPrefix() + "&cThis decoration is locked.");
			return false;
		}

		itemFrame.remove();
		Hitbox.destroy(getHitboxes(), origin, ItemFrameRotation.of(itemFrame).getBlockFace());

		world.dropItemNaturally(origin, destroyEvent.getItem());
		return true;
	}

	private boolean canEdit(Player player, String ownerUUID, Location origin) {
		String playerUUID = player.getUniqueId().toString();
		Rank playerRank = Rank.of(player);

		if (playerUUID.equals(ownerUUID))
			return true;

		// TODO: integrate with trusts
		if (new TrustService().get(ownerUUID).trusts(Type.DECORATION, player))
			return true;

		if (playerRank.isStaff()) {
			if (playerRank.isSeniorStaff() || playerRank.equals(Rank.ARCHITECT) || player.isOp())
				return true;

			if (WorldGroup.STAFF.equals(WorldGroup.of(player)))
				return true;

			if (WorldGuardEditCommand.canWorldGuardEdit(player) && new WorldGuardUtils(player).getRegionsAt(origin).size() > 0)
				return true;
		}

		return false;
	}

	public boolean interact(Player player, ItemFrame itemFrame, Block block) {
		ItemStack item = itemFrame.getItem().clone();
		Location origin = itemFrame.getLocation().toBlockLocation().clone();

		DecorationInteractEvent interactEvent = new DecorationInteractEvent(player, origin, this, item);
		if (!interactEvent.callEvent())
			return false;

		if (this.isSeat()) {
			Seat seat = (Seat) this;
			DecorationSitEvent sitEvent = new DecorationSitEvent(player, origin, seat, item, itemFrame.getRotation(), block);

			if (sitEvent.callEvent())
				seat.trySit(player, block, sitEvent.getRotation(), this);
		}

		return true;
	}

	// validation

	private boolean isValidPlacement(BlockFace clickedFace) {
		for (DisabledPlacement disabledPlacement : getDisabledPlacements()) {
			if (disabledPlacement.getBlockFaces().contains(clickedFace))
				return false;
		}

		return true;
	}

	private @Nullable ItemFrameRotation findValidFrameRotation(Location origin, ItemFrameRotation frameRotation) {
		if (isValidLocation(origin, frameRotation))
			return frameRotation;

		BlockFace rotated = frameRotation.getBlockFace();
		for (int tries = 0; tries < DecorationUtils.getDirections().size(); tries++) {
			rotated = DecorationUtils.rotateClockwise(rotated);

			ItemFrameRotation newFrameRotation = ItemFrameRotation.from(rotated);
			if (isValidLocation(origin, newFrameRotation))
				return newFrameRotation;
		}

		return null;
	}

	private boolean isValidLocation(Location origin, ItemFrameRotation frameRotation) {
		if (!isValidRotation(frameRotation))
			return false;

		List<Hitbox> hitboxes = Hitbox.rotateHitboxes(this, frameRotation.getBlockFace());
		for (Hitbox hitbox : hitboxes) {
			if (!MaterialTag.ALL_AIR.isTagged(hitbox.getOffsetBlock(origin).getType()))
				return false;
		}

		return true;
	}

	public boolean isValidRotation(ItemFrameRotation frameRotation) {
		if (this.disabledRotation.equals(DisabledRotation.NONE))
			return true;

		if (!this.disabledRotation.contains(frameRotation))
			return true;

		return false;
	}

	public boolean isOwner(UUID uuid) {
		NBTItem nbtItem = new NBTItem(this.getItem());
		String ownerUUID = nbtItem.getString(nbtOwnerKey);

		return uuid.toString().equals(ownerUUID);
	}
}

package gg.projecteden.nexus.features.resourcepack.decoration.common;

import de.tr7zw.nbtapi.NBTItem;
import gg.projecteden.nexus.features.commands.staff.WorldGuardEditCommand;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationDestroyEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationSitEvent;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.trust.Trust.Type;
import gg.projecteden.nexus.models.trust.TrustService;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Utils.ItemFrameRotation;
import gg.projecteden.nexus.utils.WorldGroup;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.Data;
import lombok.NonNull;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Data
public class Decoration {
	@NonNull
	private final DecorationConfig config;
	private final ItemFrame itemFrame;

	public Location getOrigin() {
		if (!isValidFrame())
			return null;

		return itemFrame.getLocation().toBlockLocation().clone();
	}

	private boolean isValidFrame() {
		return itemFrame != null && itemFrame.isValid();
	}

	public UUID getOwner() {
		ItemStack item = getItem();

		NBTItem nbtItem = new NBTItem(item);
		if (!nbtItem.hasKey(DecorationConfig.NBT_OWNER_KEY))
			return null;

		return UUID.fromString(nbtItem.getString(DecorationConfig.NBT_OWNER_KEY));
	}

	public ItemStack getItem() {
		if (!isValidFrame())
			return null;

		return itemFrame.getItem();
	}

	public ItemFrameRotation getRotation() {
		if (!isValidFrame())
			return null;

		return ItemFrameRotation.of(itemFrame);
	}

	public boolean destroy(@NonNull Player player) {
		World world = player.getWorld();

		final Decoration decoration = new Decoration(config, itemFrame);
		DecorationDestroyEvent destroyEvent = new DecorationDestroyEvent(player, decoration);
		if (!destroyEvent.callEvent())
			return false;

		if (this instanceof Seat seat) {
			if (seat.isOccupied(config, itemFrame)) {
				PlayerUtils.send(player, DecorationUtils.getPrefix() + "&cSeat is occupied");
				return false;
			}
		}

		if (!canEdit(player, decoration.getOrigin())) {
			PlayerUtils.send(player, DecorationUtils.getPrefix() + "&cThis decoration is locked.");
			return false;
		}

		Hitbox.destroy(decoration);

		if (!player.getGameMode().equals(GameMode.CREATIVE))
			world.dropItemNaturally(decoration.getOrigin(), decoration.getItem());

		itemFrame.remove();

		return true;
	}

	private boolean canEdit(Player player, Location origin) {
		Rank playerRank = Rank.of(player);

		if (player.getUniqueId().equals(getOwner()))
			return true;

		// TODO: Use locks
		if (new TrustService().get(getOwner()).trusts(Type.DECORATIONS, player))
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

	public boolean interact(Player player, Block block) {
		final Decoration decoration = new Decoration(config, itemFrame);
		DecorationInteractEvent interactEvent = new DecorationInteractEvent(player, decoration);
		if (!interactEvent.callEvent())
			return false;

		if (config.isSeat()) {
			DecorationSitEvent sitEvent = new DecorationSitEvent(player, decoration, itemFrame.getRotation(), block);

			if (sitEvent.callEvent())
				sitEvent.getSeat().trySit(player, block, sitEvent.getRotation(), config);
		}

		return true;
	}

	public boolean isOwner(UUID uuid) {
		return uuid.equals(getOwner());
	}

}

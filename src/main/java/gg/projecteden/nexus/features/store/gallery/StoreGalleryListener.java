package gg.projecteden.nexus.features.store.gallery;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.commands.staff.WorldGuardEditCommand;
import gg.projecteden.nexus.features.store.gallery.annotations.Category.GalleryCategory;
import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.ItemBuilder.ModelId;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityMountEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

public class StoreGalleryListener implements Listener {

	public StoreGalleryListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(PlayerInteractEntityEvent event) {
		if (event.getHand() != EquipmentSlot.HAND)
			return;

		final Player player = event.getPlayer();
		if (WorldGuardEditCommand.canWorldGuardEdit(player))
			return;

		final Entity entity = event.getRightClicked();
		if (CitizensUtils.isNPC(entity)) {
			final NPC npc = CitizensUtils.getNPC(entity);
			final GalleryPackage galleryPackage = GalleryPackage.of(npc);
			if (galleryPackage == null)
				return;

			galleryPackage.onNpcInteract(player);
		} else {
			final GalleryPackage galleryPackage = getGalleryPackage(entity.getLocation());
			if (galleryPackage == null)
				return;

			event.setCancelled(true);

			if (entity instanceof ItemFrame itemFrame && List.of(BlockFace.NORTH, BlockFace.SOUTH).contains(itemFrame.getAttachedFace()))
				if (!isNullOrAir(itemFrame.getItem()) && ModelId.of(itemFrame.getItem()) == 1199)
					galleryPackage.onClickCart(player);
				else
					galleryPackage.onImageInteract(player);
			else
				galleryPackage.onEntityInteract(player, entity);
		}
	}

	@EventHandler
	public void on(PlayerInteractEvent event) {
		if (!ActionGroup.CLICK_BLOCK.applies(event))
			return;

		if (event.getHand() != EquipmentSlot.HAND)
			return;

		final Block block = event.getClickedBlock();
		if (isNullOrAir(block))
			return;

		if (block.getType() != Material.BLACK_CONCRETE)
			return;

		if (WorldGuardEditCommand.canWorldGuardEdit(event.getPlayer()))
			return;

		GalleryPackage galleryPackage = getGalleryPackage(block.getLocation());
		if (galleryPackage == null)
			return;

		event.setCancelled(true);

		galleryPackage.onImageInteract(event.getPlayer());
	}

	@EventHandler
	public void on(EntityMountEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		final WorldGuardUtils worldguard = new WorldGuardUtils(player.getWorld());
		if (!worldguard.isInRegion(player, "store_gallery"))
			return;

		event.setCancelled(true);
	}

	@Nullable
	private GalleryPackage getGalleryPackage(Location location) {
		if (!location.getWorld().equals(StoreGallery.getWorld()))
			return null;

		final WorldGuardUtils worldguard = new WorldGuardUtils(location);
		final Set<ProtectedRegion> regions = worldguard.getRegionsLikeAt("store_gallery__.*", location);

		GalleryCategory galleryCategory = null;
		GalleryPackage galleryPackage = null;

		for (ProtectedRegion region : regions) {
			try {
				final String[] split = region.getId().split("__");
				galleryCategory = GalleryCategory.valueOf(split[1].toUpperCase());
				galleryPackage = GalleryPackage.valueOf(split[2].toUpperCase());

				if (galleryPackage.getCategory() == galleryCategory)
					break;
			} catch (Exception ignore) {}
		}

		if (galleryCategory == null || galleryPackage == null)
			return null;

		return galleryPackage;
	}

}

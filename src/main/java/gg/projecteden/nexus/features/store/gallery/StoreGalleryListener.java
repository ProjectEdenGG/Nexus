package gg.projecteden.nexus.features.store.gallery;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.commands.staff.WorldGuardEditCommand;
import gg.projecteden.nexus.features.store.gallery.annotations.Category.GalleryCategory;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

import static gg.projecteden.nexus.utils.BlockUtils.isNullOrAir;

public class StoreGalleryListener implements Listener {

	public StoreGalleryListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(PlayerInteractEntityEvent event) {
		if (event.getHand() != EquipmentSlot.HAND)
			return;

		if (!(event.getRightClicked() instanceof ItemFrame itemFrame))
			return;

		if (!List.of(BlockFace.NORTH, BlockFace.SOUTH).contains(itemFrame.getAttachedFace()))
			return;

		if (WorldGuardEditCommand.canWorldGuardEdit(event.getPlayer()))
			return;

		final GalleryPackage galleryPackage = getGalleryPackage(itemFrame.getLocation());
		if (galleryPackage == null)
			return;

		event.setCancelled(true);

		galleryPackage.onImageInteract(event.getPlayer());
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
	public void on(NPCRightClickEvent event) {
		for (GalleryPackage galleryPackage : GalleryPackage.values())
			if (galleryPackage.getNpcId() == event.getNPC().getId()) {
				galleryPackage.onNpcInteract(event.getClicker());
				return;
			}
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

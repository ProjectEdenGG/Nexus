package gg.projecteden.nexus.features.store.gallery;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.framework.features.Depends;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

@Depends(ResourcePack.class)
public class StoreGallery extends Feature {

	@Override
	public void onStart() {
		new StoreGalleryListener();
		Tasks.wait(TickTime.SECOND.x(10), () -> {
			new StoreGalleryNPCs();
			GalleryPackage.onStart();
		});
	}

	@Override
	public void onStop() {
		GalleryPackage.onStop();
	}

	public static String getRegion() {
		return "store_gallery";
	}

	public static World getWorld() {
		return Bukkit.getWorld("server");
	}

	public static boolean isInStoreGallery(Player player) {
		return player.getWorld().equals(getWorld()) && new WorldGuardUtils(player).isInRegion(player, getRegion());
	}

	public static Location location(double x, double y, double z) {
		return new Location(getWorld(), x, y, z);
	}

}

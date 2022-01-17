package gg.projecteden.nexus.features.store.gallery;

import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.framework.features.Depends;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

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

	public static World getWorld() {
		return Bukkit.getWorld("server");
	}

	public static Location location(double x, double y, double z) {
		return new Location(getWorld(), x, y, z);
	}

}

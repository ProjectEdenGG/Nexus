package gg.projecteden.nexus.features.store.gallery;

import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.framework.features.Depends;
import gg.projecteden.nexus.framework.features.Feature;
import org.bukkit.Bukkit;
import org.bukkit.World;

@Depends(ResourcePack.class)
public class StoreGallery extends Feature {

	@Override
	public void onStart() {
		new StoreGalleryNPCs();
		new StoreGalleryListener();
	}

	@Override
	public void onStop() {
		for (GalleryPackage galleryPackage : GalleryPackage.values())
			galleryPackage.shutdown();
	}

	public static World getWorld() {
		return Bukkit.getWorld("server");
	}

}

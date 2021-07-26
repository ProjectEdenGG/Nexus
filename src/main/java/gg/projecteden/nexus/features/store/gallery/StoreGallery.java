package gg.projecteden.nexus.features.store.gallery;

import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.framework.features.Depends;
import gg.projecteden.nexus.framework.features.Feature;

@Depends(ResourcePack.class)
public class StoreGallery extends Feature {
	@Override
	public void onStart() {
		new NPCDisplays();
	}
}

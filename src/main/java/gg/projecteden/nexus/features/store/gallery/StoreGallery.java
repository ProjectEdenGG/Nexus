package gg.projecteden.nexus.features.store.gallery;

import gg.projecteden.nexus.framework.features.Feature;

public class StoreGallery extends Feature {
	@Override
	public void onStart() {
		new NPCDisplays();
	}
}

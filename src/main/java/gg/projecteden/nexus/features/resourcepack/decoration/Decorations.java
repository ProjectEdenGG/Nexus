package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.nexus.features.resourcepack.decoration.catalog.Catalog;
import gg.projecteden.nexus.features.resourcepack.decoration.store.DecorationStoreManager;
import lombok.Getter;

/*
	TODO:
		- Change creative paint brush to open menu on more than just be on RIGHT_CLICK_BLOCK
 */
public class Decorations {

	@Getter
	private static boolean isServerReloading = false;

	public Decorations() {
		try {
			DecorationType.initDecorations();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		new Catalog();
		new DecorationListener();
		new DecorationTypeListener();
		new TickableDecorations();
		new DecorationStoreManager();
	}

	public static void onStop() {
		isServerReloading = true;
		DecorationStoreManager.onStop();
	}
}

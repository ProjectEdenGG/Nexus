package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.nexus.features.resourcepack.decoration.catalog.Catalog;
import gg.projecteden.nexus.features.survival.decorationstore.DecorationStore;
import lombok.Getter;

public class Decorations {

	@Getter
	private static boolean isServerReloading = false;

	public Decorations() {
		try {
			DecorationType.initDecorations();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		new DecorationStore();
		new Catalog();
		new DecorationListener();
		new DecorationTypeListener();
		new TickableDecorations();
	}

	public static void onStop() {
		isServerReloading = true;
		DecorationStore.onStop();
	}
}

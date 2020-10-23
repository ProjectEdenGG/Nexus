package me.pugabyte.bncore.features.mcmmo;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.MaterialMapStore;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.features.Feature;

import java.lang.reflect.Field;
import java.util.HashSet;

public class McMMO extends Feature {

	@Override
	public void startup() {
		new McMMOListener();

		// Remove when updating to mcmmo 2.2
		addCrossbowToIronTools();
	}

	private void addCrossbowToIronTools() {
		try {
			MaterialMapStore materialMapStore = mcMMO.getMaterialMapStore();
			Field field = materialMapStore.getClass().getDeclaredField("ironTools");
			field.setAccessible(true);
			HashSet<String> ironTools = (HashSet<String>) field.get(materialMapStore);
			ironTools.add("crossbow");
		} catch (Exception ex) {
			BNCore.log("Could not add crossbow to iron tools");
			ex.printStackTrace();
		}
	}

}

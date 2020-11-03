package me.pugabyte.bncore.features.mcmmo;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.MaterialMapStore;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.features.Feature;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;

import java.lang.reflect.Field;
import java.util.HashSet;

public class McMMO extends Feature {

	@Override
	public void startup() {
		new McMMOListener();

		// Remove when updating to mcmmo 2.2
		Tasks.wait(Time.SECOND.x(5), this::addCrossbowToIronTools);
	}

	private void addCrossbowToIronTools() {
		try {
			MaterialMapStore materialMapStore = mcMMO.getMaterialMapStore();
			if (materialMapStore == null)
				throw new RuntimeException("materialMapStore is null");
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

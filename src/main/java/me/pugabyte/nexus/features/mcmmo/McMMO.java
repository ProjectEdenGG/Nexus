package me.pugabyte.nexus.features.mcmmo;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.MaterialMapStore;
import eden.utils.TimeUtils.Time;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.features.Feature;
import me.pugabyte.nexus.utils.Tasks;

import java.lang.reflect.Field;
import java.util.HashSet;

public class McMMO extends Feature {

	@Override
	public void onStart() {
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
			Nexus.log("Could not add crossbow to iron tools");
			ex.printStackTrace();
		}
	}

}

package gg.projecteden.nexus.features.mcmmo;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.MaterialMapStore;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.mcmmo.reset.McMMOResetProvider.ResetSkillType;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.Tasks;

import java.lang.reflect.Field;
import java.util.HashSet;

public class McMMO extends Feature {

	public static final int TIER_ONE = 100;
	public static final int TIER_TWO = 200;
	public static final int TIER_ONE_ALL = ResetSkillType.values().length * TIER_ONE;
	public static final int TIER_TWO_ALL = ResetSkillType.values().length * TIER_TWO;

	@Override
	public void onStart() {
		new McMMOListener();

		// Remove when updating to mcmmo 2.2
		Tasks.wait(TickTime.SECOND.x(5), this::addCrossbowToIronTools);
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

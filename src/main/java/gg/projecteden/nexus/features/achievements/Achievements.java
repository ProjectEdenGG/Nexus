package gg.projecteden.nexus.features.achievements;

import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;

public class Achievements extends Feature {

	// TODO:
	// - Test DB implementation, Mongo may not like Object
	// - Update economy listener
	// - Update event listeners (see events folder)
	// - Add more achievements

	@Override
	public void onStart() {
		Tasks.wait(TickTime.SECOND.x(5), () -> {
			for (World world : Bukkit.getWorlds())
				world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
		});

	/*
		subTypesOf(getClass().getPackageName(), Listener.class).forEach(listener -> {
			try {
				if (listener.getAnnotation(Disabled.class) == null)
					Nexus.registerListener(new ObjenesisStd().newInstance(listener));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	*/
	}

}

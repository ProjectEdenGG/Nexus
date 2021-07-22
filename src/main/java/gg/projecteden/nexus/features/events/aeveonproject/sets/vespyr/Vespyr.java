package gg.projecteden.nexus.features.events.aeveonproject.sets.vespyr;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.aeveonproject.sets.APRegions;
import gg.projecteden.nexus.features.events.aeveonproject.sets.APSet;
import gg.projecteden.nexus.features.events.annotations.Region;
import org.bukkit.event.Listener;

import java.util.Collections;
import java.util.List;

@Region("vespyr")
public class Vespyr implements Listener, APSet {
	public static boolean active = false;

	public Vespyr() {
		Nexus.registerListener(this);

		new Sounds();
		new Particles();
	}

	@Override
	public List<String> getUpdateRegions() {
		return Collections.singletonList(APRegions.vespyr_shipColor);
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public void setActive(boolean bool) {
		active = bool;
	}
}

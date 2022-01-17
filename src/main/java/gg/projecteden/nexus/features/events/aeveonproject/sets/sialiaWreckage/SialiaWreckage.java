package gg.projecteden.nexus.features.events.aeveonproject.sets.sialiaWreckage;

import gg.projecteden.nexus.features.events.aeveonproject.sets.APSet;
import org.bukkit.event.Listener;

import java.util.List;

//@Region("")
public class SialiaWreckage implements Listener, APSet {
	public static boolean active = false;

	@Override
	public List<String> getUpdateRegions() {
		return null;
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

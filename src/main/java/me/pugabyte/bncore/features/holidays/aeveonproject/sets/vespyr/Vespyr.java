package me.pugabyte.bncore.features.holidays.aeveonproject.sets.vespyr;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.aeveonproject.sets.APSet;
import me.pugabyte.bncore.features.holidays.annotations.Region;
import org.bukkit.event.Listener;

import java.util.List;

@Region("vespyr")
public class Vespyr implements Listener, APSet {
	public static boolean active = false;

	public Vespyr() {
		BNCore.registerListener(this);

		new Sounds();
		new Particles();
	}

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

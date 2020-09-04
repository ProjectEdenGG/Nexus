package me.pugabyte.bncore.features.holidays.aeveonproject.sets.sialiaWreckage;

import me.pugabyte.bncore.features.holidays.aeveonproject.sets.APSet;
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

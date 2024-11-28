package gg.projecteden.nexus.models.warps;

import gg.projecteden.nexus.models.warps.Warps.Warp;
import org.bukkit.Location;

import java.util.List;

public enum WarpType {
	AEVEON_PROJECT,
	ARENA,
	ATP,
	BEAR_FAIR20,
	BEAR_FAIR21,
	EASTER21,
	EASTER22,
	GILIHOUSE,
	HALLOWEEN22,
	HUB,
	LEGACY,
	MINIGAMES,
	NORMAL,
	PRIDE22,
	PUGMAS25,
	STAFF,
	STATUE_HUNT20,
	QUEST,
	WEEKLY_WAKKA,
	VULAN24,
	XRAY,
	;

	private Warps get() {
		return new WarpsService().get0();
	}

	public List<Warp> getAll() {
		return get().getAll(this);
	}

	public Warp get(String name) {
		return getAll().stream().filter(warp -> warp.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}

	public void add(String name, Location location) {
		get().add(new Warp(name, this, location));
	}

	public void delete(String name) {
		get().delete(this, name);
	}

}

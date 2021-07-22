package gg.projecteden.nexus.models.warps;

import gg.projecteden.nexus.models.MySQLService;

import java.util.List;

public class WarpService extends MySQLService {

	public Warp get(String name, WarpType type) {
		Warp warp = database.where("type = ? AND name = ?", type.name(), name).first(Warp.class);
		if (warp.getName() == null) return null;
		return warp;
	}

	public Warp getNormalWarp(String name) {
		Warp warp = database.where("type = ? AND name = ?", WarpType.NORMAL.name(), name).first(Warp.class);
		if (warp.getName() == null) return null;
		return warp;
	}

	public Warp getStaffWarp(String name) {
		Warp warp = database.where("type = ? AND name = ?", WarpType.STAFF.name(), name).first(Warp.class);
		if (warp.getName() == null) return null;
		return warp;
	}

	public List<Warp> getAllWarps() {
		return database.select("*").results(Warp.class);
	}

	public List<Warp> getWarpsByType(WarpType type) {
		return database.where("type = ?", type.name()).results(Warp.class);
	}

	public void delete(Warp warp) {
		database.table("warp").where("type = ? AND name = ?", warp.getType(), warp.getName()).delete();
	}

}

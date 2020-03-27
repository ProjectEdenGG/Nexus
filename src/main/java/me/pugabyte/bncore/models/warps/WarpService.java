package me.pugabyte.bncore.models.warps;

import me.pugabyte.bncore.models.MySQLService;

import java.util.List;

public class WarpService extends MySQLService {

	public Warp get(String name, WarpType type) {
		Warp warp = database.where("name = ? AND type = ?", name, type.name()).first(Warp.class);
		if (warp.getName() == null) return null;
		return warp;
	}

	public Warp getNormalWarp(String name) {
		Warp warp = database.where("name = ? AND type = ?", name, WarpType.NORMAL.name()).first(Warp.class);
		if (warp.getName() == null) return null;
		return warp;
	}

	public Warp getStaffWarp(String name) {
		Warp warp = database.where("name = ? AND type = ?", name, WarpType.STAFF.name()).first(Warp.class);
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
		database.table("staff_warp").where("name = ? AND type = ?", warp.getName(), warp.getType()).delete();
	}

}

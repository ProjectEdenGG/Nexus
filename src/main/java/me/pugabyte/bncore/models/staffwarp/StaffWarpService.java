package me.pugabyte.bncore.models.staffwarp;

import me.pugabyte.bncore.models.MySQLService;

import java.util.List;

public class StaffWarpService extends MySQLService {

	public StaffWarp get(String name) {
		StaffWarp warp = database.where("name = ?", name).first(StaffWarp.class);
		if (warp.getName() == null) return null;
		return warp;
	}

	public List<StaffWarp> getStaffWarps() {
		return database.select("*").results(StaffWarp.class);
	}

	public void delete(StaffWarp warp) {
		database.table("staff_warp").where("name = ?", warp.getName()).delete();
	}

}

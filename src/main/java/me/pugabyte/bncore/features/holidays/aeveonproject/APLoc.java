package me.pugabyte.bncore.features.holidays.aeveonproject;

import org.bukkit.Location;

public class APLoc extends Location {

	public APLoc(double x, double y, double z) {
		super(AeveonProject.WORLD, x, y, z);
	}

	public APLoc(double x, double y, double z, float yaw, float pitch) {
		super(AeveonProject.WORLD, x, y, z, yaw, pitch);
	}
}

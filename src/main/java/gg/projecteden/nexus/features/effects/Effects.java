package gg.projecteden.nexus.features.effects;

import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public abstract class Effects extends Feature {

	@Override
	public void onStart() {
		particles();
		sounds();
	}

	public void sounds() {
	}

	public void particles() {
	}

	public Location loc(double x, double y, double z) {
		return new Location(getWorld(), x, y, z);
	}

	public World getWorld() {
		return Bukkit.getWorld("server");
	}

	public boolean hasPlayersNearby(Location origin, double radius) {
		return OnlinePlayers.where().world(getWorld()).radius(origin, radius).get().size() > 0;
	}
}

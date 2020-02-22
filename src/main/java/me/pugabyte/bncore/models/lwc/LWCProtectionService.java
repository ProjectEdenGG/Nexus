package me.pugabyte.bncore.models.lwc;

import com.dieselpoint.norm.Database;
import me.pugabyte.bncore.framework.persistence.MySQLDatabase;
import me.pugabyte.bncore.framework.persistence.MySQLPersistence;
import me.pugabyte.bncore.models.MySQLService;
import org.bukkit.Location;

import java.util.List;
import java.util.UUID;

public class LWCProtectionService extends MySQLService {
	protected static Database database = MySQLPersistence.getConnection(MySQLDatabase.SMP_LWC);


	public LWCProtection get(int id) {
		return database.where("id = ?", id).first(LWCProtection.class);
	}

	public LWCProtection getByLocation(Location location) {
		return database.where("x, y, z, world = ?, ?, ?, ?")
				.args(location.getX(), location.getY(), location.getZ(), location.getWorld().getName())
				.first(LWCProtection.class);
	}

	public List<LWCProtection> getPlayerProtections(UUID uuid) {
		return database.where("owner = ?", uuid.toString()).results(LWCProtection.class);
	}

	public List<LWCProtection> getProtectionsInRange(Location location, int range) {
		int xpos = (int) location.getX() + range;
		int xneg = (int) location.getX() - range;
		int zpos = (int) location.getZ() + range;
		int zneg = (int) location.getZ() - range;
		return database.where("(x BETWEEN ? AND ?) AND (z BETWEEN ? AND ?) AND world = ?")
				.args(xneg, xpos, zneg, zpos, location.getWorld().getName())
				.results(LWCProtection.class);
	}
}

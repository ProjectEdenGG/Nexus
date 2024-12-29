package gg.projecteden.nexus.features.events.aeveonproject;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.features.events.aeveonproject.sets.APSet;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class APUtils {

	public static int getPlayersInAPWorld() {
		return (int) OnlinePlayers.getAll().stream().filter(APUtils::isInWorld).count();
	}

	public static Collection<Player> getPlayersInSet(APSet set) {
		try {
			return AeveonProject.worldguard().getPlayersInRegion(set.getRegion());
		} catch (Exception ignored) {
			return null;
		}
	}

	public static boolean isInSpace(Player player) {
		Set<ProtectedRegion> regions = AeveonProject.worldguard().getRegionsAt(player.getLocation());
		Set<ProtectedRegion> spaceRegions = regions.stream()
				.filter(region -> region.getId().contains("space") || region.getId().contains("night"))
				.collect(Collectors.toSet());

		return spaceRegions.size() > 0;
	}

	public static boolean isInRegion(Player player, String protectedRegion) {
		return isInRegion(player, AeveonProject.worldguard().getProtectedRegion(protectedRegion));
	}

	public static boolean isInRegion(Player player, ProtectedRegion protectedRegion) {
		return isInRegion(player.getLocation(), protectedRegion);
	}

	public static boolean isInRegion(Block block, String protectedRegion) {
		return isInRegion(block, AeveonProject.worldguard().getProtectedRegion(protectedRegion));
	}

	public static boolean isInRegion(Block block, ProtectedRegion protectedRegion) {
		return isInRegion(block.getLocation(), protectedRegion);
	}

	public static boolean isInRegion(Location location, String protectedRegion) {
		return isInRegion(location, AeveonProject.worldguard().getProtectedRegion(protectedRegion));
	}

	public static boolean isInRegion(Location location, ProtectedRegion protectedRegion) {
		Set<ProtectedRegion> regions = AeveonProject.worldguard().getRegionsAt(location);
		for (ProtectedRegion region : regions) {
			if (region.equals(protectedRegion))
				return true;
		}
		return false;
	}

	public static boolean isInWorld(Block block) {
		return isInWorld(block.getLocation());
	}

	public static boolean isInWorld(Player player) {
		return isInWorld(player.getLocation());
	}

	public static boolean isInWorld(Location location) {
		return location.getWorld().equals(AeveonProject.getWorld());
	}

	public static Location APLoc(double x, double y, double z) {
		return new Location(AeveonProject.getWorld(), x, y, z);
	}

	public static Location APLoc(double x, double y, double z, float yaw, float pitch) {
		return new Location(AeveonProject.getWorld(), x, y, z, yaw, pitch);
	}

	public static String getShipColorRegion(String updateRg) {
		return updateRg.replaceAll("_update", "");
	}

	public static void makeArmorStandLookAtPlayer(ArmorStand stand, Player player) {
		Location origin = stand.getEyeLocation(); //our original location (Point A)
		double initYaw = origin.getYaw();
		Vector tgt = player.getEyeLocation().toVector(); //our target location (Point B)
		origin.setDirection(tgt.subtract(origin.toVector())); //set the origin's direction to be the direction vector between point A and B.
		double yaw = origin.getYaw() - initYaw;
		double pitch = origin.getPitch();

		if (yaw < -180)
			yaw = yaw + 360;
		else if (yaw >= 180)
			yaw -= 360;

		if (pitch > 0)
			pitch = 0;
		else if (pitch < -15)
			pitch = -15;

		double x = Math.toRadians(pitch);
		double y = Math.toRadians(yaw);

		EulerAngle ea = new EulerAngle(x, y, 0);
		stand.setHeadPose(ea);
	}
}

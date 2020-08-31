package me.pugabyte.bncore.features.holidays.aeveonproject.sets.lobby;

import me.pugabyte.bncore.features.holidays.aeveonproject.sets.APSet;
import me.pugabyte.bncore.features.holidays.aeveonproject.sets.Regions;
import me.pugabyte.bncore.features.holidays.annotations.Region;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.features.holidays.aeveonproject.AeveonProject.APLoc;

@Region("lobby")
public class Lobby implements APSet {
	Location armorStandLoc = APLoc(-1765, 98, -1165);

	public Lobby() {
		Tasks.repeat(0, 2, () -> {
			Player nearestPlayer = (Player) getNearestEntityType(armorStandLoc, EntityType.PLAYER, 7);
			ArmorStand armorStand = (ArmorStand) getNearestEntityType(armorStandLoc, EntityType.ARMOR_STAND, 1);
			if (nearestPlayer != null && armorStand != null) {
				makeArmorStandLookAtPlayer(armorStand, nearestPlayer);
			}
		});
	}


	private void makeArmorStandLookAtPlayer(ArmorStand stand, Player player) {
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

	private Entity getNearestEntityType(Location location, EntityType filter, double radius) {
		List<Entity> entities = location.getNearbyEntities(radius, radius, radius).stream()
				.filter(_entity -> _entity.getType().equals(filter))
				.collect(Collectors.toList());

		double shortest = radius;
		Entity result = null;
		for (Entity entity : entities) {
			double distance = entity.getLocation().distance(location);
			if (distance < shortest) {
				shortest = distance;
				result = entity;
			}
		}

		return result;
	}

	@Override
	public List<String> getUpdateRegions() {
		return Collections.singletonList(Regions.lobby_shipColor);
	}
}

package gg.projecteden.nexus.features.minigames.utils;

import gg.projecteden.nexus.features.minigames.mechanics.Murder;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.matchdata.MurderMatchData;
import gg.projecteden.nexus.utils.Distance;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks.Countdown;
import lombok.Data;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class Gun {
	@NonNull
	private Minigamer minigamer;
	private String name;
	private Material material;
	private String[] lore;
	private int range = 200;
	private int cooldown;
	private double damage = 1000;
	private double lastShot;
	private double hitbox = 1;
	private boolean shouldDamageWithConsole;
	private Set<Material> passthroughMaterials = new HashSet<>() {{
		add(Material.AIR);
		add(Material.TRIPWIRE_HOOK);
		add(Material.TRIPWIRE);
		add(Material.STRING);
		addAll(MaterialTag.PLANTS.getValues());
	}};

	public void shoot() {
		if (!canShoot()) return;

		Location location = minigamer.getOnlinePlayer().getLocation();
		List<Block> los = minigamer.getOnlinePlayer().getLineOfSight(passthroughMaterials, range);
		double blockDistance = Distance.distance(los.get(los.size() - 1), location).getRealDistance();

		Location start = minigamer.getOnlinePlayer().getEyeLocation();
		Vector increase = start.getDirection();

		for (int counter = 0; counter < blockDistance - 1; counter++) {
			Location point = start.add(increase);
			for (Player _player : OnlinePlayers.where().world(minigamer.getOnlinePlayer().getWorld()).get())
				_player.spawnParticle(Particle.CRIT, point, 1, 0, 0, 0, 0.1);
		}

		minigamer.getOnlinePlayer().playSound(location, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 0.8F);

		Location observerPos = minigamer.getOnlinePlayer().getEyeLocation();
		Vector3D observerDir = new Vector3D(observerPos.getDirection());

		Vector3D observerStart = new Vector3D(observerPos);
		Vector3D observerEnd = observerStart.add(observerDir.multiply(range));

		for (Player target : minigamer.getOnlinePlayer().getWorld().getPlayers()) {
			Vector3D targetPos = new Vector3D(target.getLocation());
			double hitboxVal = hitbox / 2;
			Vector3D minimum = targetPos.add(-hitboxVal, 0, -hitboxVal);
			Vector3D maximum = targetPos.add(hitboxVal, 1.80, hitboxVal);

			boolean notTargetingSelf = target != minigamer.getOnlinePlayer();
			boolean sameMatch = minigamer.getMatch().getOnlinePlayers().contains(target);
			boolean inGunRange = Distance.distance(target, location).lt(blockDistance);
			if (notTargetingSelf && inGunRange && sameMatch)
				if (Vector3D.hasIntersection(observerStart, observerEnd, minimum, maximum)) {
					if (minigamer.getMatch().getMechanic() instanceof Murder) {
						MurderMatchData matchData = minigamer.getMatch().getMatchData();
						if (Minigamer.of(target).equals(matchData.getMurderer()))
							matchData.setHero(minigamer);
					}

					minigamer.getOnlinePlayer().playSound(location, Sound.ENTITY_SHULKER_BULLET_HIT, 1, 1);
					minigamer.getMatch().getMechanic().kill(Minigamer.of(target), minigamer);
				}
		}

		if (cooldown > 0)
			startCooldown();
	}

	private void startCooldown() {
		Player player = minigamer.getOnlinePlayer();
		minigamer.getMatch().getTasks().countdown(Countdown.builder()
				.duration(cooldown)
				.onStart(() -> player.setLevel(0))
				.onTick(ticks -> player.setExp((float) ticks / cooldown))
				.onComplete(() -> player.setExp(0)));
	}

	public boolean canShoot() {
		return minigamer.getOnlinePlayer().getExp() == 0;
	}

	public static class Vector3D {
		/**
		 * Represents the null (0, 0, 0) origin.
		 */
		public static final Vector3D ORIGIN = new Vector3D(0, 0, 0);

		// Use protected members, like Bukkit
		final double x;
		final double y;
		final double z;

		/**
		 * Construct an immutable 3D vector.
		 */
		private Vector3D(double x, double y, double z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		/**
		 * Construct an immutable floating point 3D vector from a location object.
		 *
		 * @param location - the location to clone.
		 */
		Vector3D(Location location) {
			this(location.toVector());
		}

		/**
		 * Construct an immutable floating point 3D vector from a mutable Bukkit vector.
		 *
		 * @param vector - the mutable real Bukkit vector to clone.
		 */
		Vector3D(Vector vector) {
			if (vector == null)
				throw new IllegalArgumentException("Vector cannot be NULL.");
			this.x = vector.getX();
			this.y = vector.getY();
			this.z = vector.getZ();
		}

		/**
		 * Convert this instance to an equivalent real 3D vector.
		 *
		 * @return Real 3D vector.
		 */
		public Vector toVector() {
			return new Vector(x, y, z);
		}

		/**
		 * Adds the current vector and a given position vector, producing a result vector.
		 *
		 * @param other - the other vector.
		 * @return The new result vector.
		 */
		Vector3D add(Vector3D other) {
			if (other == null)
				throw new IllegalArgumentException("other cannot be NULL");
			return new Vector3D(x + other.x, y + other.y, z + other.z);
		}

		/**
		 * Adds the current vector and a given vector together, producing a result vector.
		 *
		 * @param x X
		 * @param y Y
		 * @param z Z
		 * @return The new result vector.
		 */
		Vector3D add(double x, double y, double z) {
			return new Vector3D(this.x + x, this.y + y, this.z + z);
		}

		/**
		 * Substracts the current vector and a given vector, producing a result position.
		 *
		 * @param other - the other position.
		 * @return The new result position.
		 */
		Vector3D subtract(Vector3D other) {
			if (other == null)
				throw new IllegalArgumentException("other cannot be NULL");
			return new Vector3D(x - other.x, y - other.y, z - other.z);
		}

		/**
		 * Substracts the current vector and a given vector together, producing a result vector.
		 *
		 * @param x X
		 * @param y Y
		 * @param z Z
		 * @return The new result vector.
		 */
		public Vector3D subtract(double x, double y, double z) {
			return new Vector3D(this.x - x, this.y - y, this.z - z);
		}

		/**
		 * Multiply each dimension in the current vector by the given factor.
		 *
		 * @param factor - multiplier.
		 * @return The new result.
		 */
		public Vector3D multiply(int factor) {
			return new Vector3D(x * factor, y * factor, z * factor);
		}

		/**
		 * Multiply each dimension in the current vector by the given factor.
		 *
		 * @param factor - multiplier.
		 * @return The new result.
		 */
		Vector3D multiply(double factor) {
			return new Vector3D(x * factor, y * factor, z * factor);
		}

		/**
		 * Divide each dimension in the current vector by the given divisor.
		 *
		 * @param divisor - the divisor.
		 * @return The new result.
		 */
		public Vector3D divide(int divisor) {
			if (divisor == 0)
				throw new IllegalArgumentException("Cannot divide by null.");
			return new Vector3D(x / divisor, y / divisor, z / divisor);
		}

		/**
		 * Divide each dimension in the current vector by the given divisor.
		 *
		 * @param divisor - the divisor.
		 * @return The new result.
		 */
		public Vector3D divide(double divisor) {
			if (divisor == 0)
				throw new IllegalArgumentException("Cannot divide by null.");
			return new Vector3D(x / divisor, y / divisor, z / divisor);
		}

		/**
		 * Retrieve the absolute value of this vector.
		 *
		 * @return The new result.
		 */
		Vector3D abs() {
			return new Vector3D(Math.abs(x), Math.abs(y), Math.abs(z));
		}

		@Override
		public String toString() {
			return String.format("[x: %s, y: %s, z: %s]", x, y, z);
		}

		public static boolean hasIntersection(Vector3D p1, Vector3D p2, Vector3D min, Vector3D max) {
			final double epsilon = 0.0001f;

			Vector3D d = p2.subtract(p1).multiply(0.5);
			Vector3D e = max.subtract(min).multiply(0.5);
			Vector3D c = p1.add(d).subtract(min.add(max).multiply(0.5));
			Vector3D ad = d.abs();

			if (Math.abs(c.x) > e.x + ad.x)
				return false;
			if (Math.abs(c.y) > e.y + ad.y)
				return false;
			if (Math.abs(c.z) > e.z + ad.z)
				return false;

			if (Math.abs(d.y * c.z - d.z * c.y) > e.y * ad.z + e.z * ad.y + epsilon)
				return false;
			if (Math.abs(d.z * c.x - d.x * c.z) > e.z * ad.x + e.x * ad.z + epsilon)
				return false;
			if (Math.abs(d.x * c.y - d.y * c.x) > e.x * ad.y + e.y * ad.x + epsilon)
				return false;

			return true;
		}
	}

}

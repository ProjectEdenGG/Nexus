package me.pugabyte.bncore.features.oldminigames.quake;

import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

public abstract class Gun extends Weapon {

	Set<Material> passthroughMaterials = new HashSet<>();
	double damage;
	private double cooldown;
	private double range;
	private double lastShot;
	private double hitbox;
	private boolean shouldDamageWithConsole;

	static boolean hasIntersection(Vector3D p1, Vector3D p2, Vector3D min, Vector3D max) {
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

	public double getLastShot() {
		return lastShot;
	}

	public void setLastShot(double lastShot) {
		this.lastShot = lastShot;
	}

	double getHitbox() {
		return hitbox;
	}

	public void setHitbox(double hitbox) {
		this.hitbox = hitbox;
	}

	public abstract void shoot();

	double getRange() {
		return range;
	}

	public void setRange(double range) {
		this.range = range;
	}

	double getCooldown() {
		return cooldown;
	}

	void setCooldown(double cooldown) {
		this.cooldown = cooldown;
	}

	public double getDamage() {
		return damage;
	}

	public void setDamage(double damage) {
		this.damage = damage;
	}

	public boolean shouldDamageWithConsole() {
		return shouldDamageWithConsole;
	}

	public void setShouldDamageWithConsole(boolean shouldDamageWithConsole) {
		this.shouldDamageWithConsole = shouldDamageWithConsole;
	}

	public boolean canShoot() {
		return player.getPlayer().getTotalExperience() == 0;
	}

	Set<Material> getPassthroughMaterials() {
		return passthroughMaterials;
	}

	void setPassthroughMaterials(Set<Material> passthroughMaterials) {
		passthroughMaterials.add(Material.AIR);
		passthroughMaterials.add(Material.DOUBLE_PLANT);
		this.passthroughMaterials = passthroughMaterials;
	}

}
package me.pugabyte.bncore.features.oldminigames.quake;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class Vector3D {
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
	 * @param x
	 * @param y
	 * @param z
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
	 * @param x
	 * @param y
	 * @param z
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
}
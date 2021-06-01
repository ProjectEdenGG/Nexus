package me.pugabyte.nexus.features.fakenpc;

import com.mojang.authlib.GameProfile;
import lombok.NonNull;
import net.minecraft.server.v1_16_R3.EntityArmorStand;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import net.minecraft.server.v1_16_R3.PlayerInteractManager;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;

import java.util.UUID;

public class NMSUtils {

	public static MinecraftServer getServer() {
		return ((CraftServer) Bukkit.getServer()).getServer();
	}

	public static WorldServer getWorldServer(Location bukkitLocation) {
		return getWorldServer(bukkitLocation.getWorld());
	}

	public static WorldServer getWorldServer(World bukkitWorld) {
		return ((CraftWorld) bukkitWorld).getHandle();
	}

	public static EntityPlayer createEntityPlayer(UUID uuid, @NonNull Location location, String name) {
		if (uuid == null)
			uuid = UUID.randomUUID();

		WorldServer world = getWorldServer(location);
		GameProfile gameProfile = new GameProfile(uuid, name);
		EntityPlayer entityPlayer = new EntityPlayer(NMSUtils.getServer(), world, gameProfile, new PlayerInteractManager(world));
		setLocation(entityPlayer, location);
		return entityPlayer;
	}

	public static void setLocation(EntityPlayer entityPlayer, Location location) {
		entityPlayer.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
	}

	public static EntityArmorStand createHologram(net.minecraft.server.v1_16_R3.World world) {
		EntityArmorStand armorStand = new EntityArmorStand(EntityTypes.ARMOR_STAND, world);
		armorStand.setMarker(true);
		armorStand.setInvisible(true);
		armorStand.setBasePlate(true);
		armorStand.setSmall(true);
		armorStand.setCustomNameVisible(true);

		return armorStand;
	}
}

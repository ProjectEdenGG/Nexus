package gg.projecteden.nexus.features.fakenpc;

import com.mojang.authlib.GameProfile;
import lombok.NonNull;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_18_R1.CraftServer;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;

import java.util.UUID;

public class NMSUtils {

	public static MinecraftServer getServer() {
		return ((CraftServer) Bukkit.getServer()).getServer();
	}

	public static ServerLevel getWorldServer(Location bukkitLocation) {
		return getWorldServer(bukkitLocation.getWorld());
	}

	public static ServerLevel getWorldServer(World bukkitWorld) {
		return ((CraftWorld) bukkitWorld).getHandle();
	}

	public static ServerPlayer createEntityPlayer(UUID uuid, @NonNull Location location, String name) {
		if (uuid == null)
			uuid = UUID.randomUUID();

		ServerLevel world = getWorldServer(location);
		GameProfile gameProfile = new GameProfile(uuid, name);
		ServerPlayer entityPlayer = new ServerPlayer(NMSUtils.getServer(), world, gameProfile);
		setLocation(entityPlayer, location);
		return entityPlayer;
	}

	public static void setLocation(ServerPlayer entityPlayer, Location location) {
		entityPlayer.moveTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
	}

	public static ArmorStand createHologram(net.minecraft.world.level.Level world) {
		ArmorStand armorStand = new ArmorStand(EntityType.ARMOR_STAND, world);
		armorStand.setMarker(true);
		armorStand.setInvisible(true);
		armorStand.setNoBasePlate(true);
		armorStand.setSmall(true);
		armorStand.setCustomNameVisible(true);

		return armorStand;
	}
}

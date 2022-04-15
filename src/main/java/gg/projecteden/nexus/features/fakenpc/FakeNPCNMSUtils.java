package gg.projecteden.nexus.features.fakenpc;

import com.mojang.authlib.GameProfile;
import gg.projecteden.nexus.utils.NMSUtils;
import lombok.NonNull;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import org.bukkit.Location;

import java.util.UUID;

public class FakeNPCNMSUtils {
	public static EntityPlayer createEntityPlayer(UUID uuid, @NonNull Location location, String name) {
		if (uuid == null)
			uuid = UUID.randomUUID();

		WorldServer world = NMSUtils.getWorldServer(location);
		GameProfile gameProfile = new GameProfile(uuid, name);
		EntityPlayer entityPlayer = new EntityPlayer(NMSUtils.getServer(), world, gameProfile);
		setLocation(entityPlayer, location);
		return entityPlayer;
	}

	public static void setLocation(EntityPlayer entityPlayer, Location location) {
		entityPlayer.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
	}

}

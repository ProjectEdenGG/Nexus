package gg.projecteden.nexus.features.fakenpc;

import com.mojang.authlib.GameProfile;
import gg.projecteden.nexus.utils.NMSUtils;
import lombok.NonNull;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Location;

import java.util.UUID;

public class FakeNPCNMSUtils {
	public static ServerPlayer createEntityPlayer(UUID uuid, @NonNull Location location, String name) {
		if (uuid == null)
			uuid = UUID.randomUUID();

		ServerLevel world = NMSUtils.getWorldServer(location);
		GameProfile gameProfile = new GameProfile(uuid, name);
		ServerPlayer entityPlayer = new ServerPlayer(NMSUtils.getServer(), world, gameProfile, null);
		setLocation(entityPlayer, location);
		return entityPlayer;
	}

	public static void setLocation(Player entityPlayer, Location location) {
		entityPlayer.moveTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
	}

}

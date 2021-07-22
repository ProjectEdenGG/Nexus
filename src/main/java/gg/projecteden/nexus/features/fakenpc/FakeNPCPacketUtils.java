package gg.projecteden.nexus.features.fakenpc;

import gg.projecteden.nexus.features.fakenpc.FakeNPC.Hologram;
import gg.projecteden.nexus.utils.PacketUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Utils;
import lombok.NonNull;
import me.lexikiq.HasPlayer;
import net.minecraft.network.protocol.game.PacketPlayOutEntityHeadRotation;
import net.minecraft.network.protocol.game.PacketPlayOutNamedEntitySpawn;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.UUID;

import static gg.projecteden.nexus.utils.PacketUtils.sendPacket;

public class FakeNPCPacketUtils {

	// NPCs

	public static void spawnFakeNPC(HasPlayer hasPlayer, FakeNPC fakeNPC) {
		EntityPlayer entityPlayer = fakeNPC.getEntityPlayer();
		PacketPlayOutPlayerInfo playerInfoPacket = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.a, entityPlayer);
		PacketPlayOutNamedEntitySpawn spawnPacket = new PacketPlayOutNamedEntitySpawn(entityPlayer);
		PacketPlayOutEntityHeadRotation headRotationPacket =
				new PacketPlayOutEntityHeadRotation(entityPlayer, PacketUtils.encodeAngle(fakeNPC.getLocation().getYaw()));

		// untested
		DataWatcher dataWatcher = entityPlayer.getDataWatcher();
		dataWatcher.set(new DataWatcherObject<>(16, DataWatcherRegistry.a), (byte) 127);

		sendPacket(hasPlayer, playerInfoPacket, spawnPacket, headRotationPacket);
		spawnHologram(hasPlayer, fakeNPC);
	}

	public static void despawnFakeNPC(UUID uuid, FakeNPC fakeNPC) {
		OfflinePlayer offlinePlayer = PlayerUtils.getPlayer(uuid);
		if (offlinePlayer.getPlayer() != null && offlinePlayer.getPlayer().isOnline())
			despawnFakeNPC(offlinePlayer.getPlayer(), fakeNPC);
	}

	public static void despawnFakeNPC(HasPlayer hasPlayer, FakeNPC fakeNPC) {
		EntityPlayer entityPlayer = fakeNPC.getEntityPlayer();
		PacketUtils.entityDestroy(hasPlayer, entityPlayer);
	}

	// Holograms

	public static void updateHologram(@NonNull HasPlayer player, FakeNPC fakeNPC) {
		Hologram hologram = fakeNPC.getHologram();
		if (Utils.isNullOrEmpty(hologram.getLines()))
			return;

		if (!Utils.isNullOrEmpty(hologram.getArmorStandList()))
			despawnHologram(player, hologram);

		spawnHologram(player, fakeNPC, 0.3, hologram.getLines());
	}

	public static void spawnHologram(@NonNull HasPlayer player, FakeNPC fakeNPC) {
		spawnHologram(player, fakeNPC, 0.3, fakeNPC.getHologram().getLines());
	}

	public static void spawnHologram(@NonNull HasPlayer player, FakeNPC fakeNPC, double distance, List<String> lines) {
		int index = 0;
		for (String line : lines)
			spawnHologram(player, fakeNPC, distance, line, index++);

	}

	public static void spawnHologram(@NonNull HasPlayer player, FakeNPC fakeNPC, double distance, String customName, int index) {
		PacketUtils.entityNameFake(player, fakeNPC.getEntityPlayer().getBukkitEntity(), distance, customName, index);
	}

	public static void despawnHologram(@NonNull UUID uuid, Hologram hologram) {
		OfflinePlayer offlinePlayer = PlayerUtils.getPlayer(uuid);
		if (offlinePlayer.getPlayer() != null && offlinePlayer.getPlayer().isOnline())
			despawnHologram(offlinePlayer.getPlayer(), hologram);
	}

	public static void despawnHologram(@NonNull HasPlayer player, Hologram hologram) {
		hologram.getArmorStandList().forEach(entityArmorStand -> PacketUtils.entityDestroy(player, entityArmorStand));
	}
}

package gg.projecteden.nexus.features.fakenpc;

import gg.projecteden.nexus.features.fakenpc.FakeNPC.Hologram;
import gg.projecteden.nexus.utils.PacketUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.parchment.HasPlayer;
import lombok.NonNull;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket.Action;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.UUID;

import static gg.projecteden.nexus.utils.PacketUtils.sendPacket;
import static gg.projecteden.utils.Nullables.isNullOrEmpty;

public class FakeNPCPacketUtils {

	// NPCs

	public static void spawnFakeNPC(HasPlayer hasPlayer, FakeNPC fakeNPC) {
		ServerPlayer entityPlayer = fakeNPC.getEntityPlayer();
		ClientboundPlayerInfoPacket playerInfoPacket = new ClientboundPlayerInfoPacket(Action.ADD_PLAYER, entityPlayer);
		ClientboundAddPlayerPacket spawnPacket = new ClientboundAddPlayerPacket(entityPlayer);
		ClientboundRotateHeadPacket headRotationPacket =
				new ClientboundRotateHeadPacket(entityPlayer, PacketUtils.encodeAngle(fakeNPC.getLocation().getYaw()));

		// untested
		SynchedEntityData dataWatcher = entityPlayer.getEntityData();
		dataWatcher.set(EntityDataSerializers.BYTE.createAccessor(16), (byte) 127);

		sendPacket(hasPlayer, playerInfoPacket, spawnPacket, headRotationPacket);
		spawnHologram(hasPlayer, fakeNPC);
	}

	public static void despawnFakeNPC(UUID uuid, FakeNPC fakeNPC) {
		OfflinePlayer offlinePlayer = PlayerUtils.getPlayer(uuid);
		if (offlinePlayer.getPlayer() != null && offlinePlayer.getPlayer().isOnline())
			despawnFakeNPC(offlinePlayer.getPlayer(), fakeNPC);
	}

	public static void despawnFakeNPC(HasPlayer hasPlayer, FakeNPC fakeNPC) {
		ServerPlayer entityPlayer = fakeNPC.getEntityPlayer();
		PacketUtils.entityDestroy(hasPlayer, entityPlayer.getId());
	}

	// Holograms

	public static void updateHologram(@NonNull HasPlayer player, FakeNPC fakeNPC) {
		Hologram hologram = fakeNPC.getHologram();
		if (isNullOrEmpty(hologram.getLines()))
			return;

		if (!isNullOrEmpty(hologram.getArmorStandList()))
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
		hologram.getArmorStandList().forEach(entityArmorStand -> PacketUtils.entityDestroy(player, entityArmorStand.getId()));
	}
}

package gg.projecteden.nexus.features.fakenpc;

import gg.projecteden.nexus.features.fakenpc.FakeNPC.Hologram;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PacketUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.parchment.HasPlayer;
import io.papermc.paper.adventure.AdventureComponent;
import lombok.NonNull;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket.Action;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.UUID;

import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;
import static gg.projecteden.nexus.utils.PacketUtils.sendPacket;

public class FakeNPCPacketUtils {

	// NPCs

	public static void spawnFor(FakeNPC fakeNPC, HasPlayer hasPlayer) {
		ServerPlayer entityPlayer = fakeNPC.getEntityPlayer();
		ClientboundPlayerInfoPacket playerInfoPacket = new ClientboundPlayerInfoPacket(Action.ADD_PLAYER, entityPlayer);
		ClientboundAddPlayerPacket spawnPacket = new ClientboundAddPlayerPacket(entityPlayer);
		ClientboundRotateHeadPacket headRotationPacket = new ClientboundRotateHeadPacket(entityPlayer, PacketUtils.encodeAngle(fakeNPC.getLocation().getYaw()));

		SynchedEntityData synchedData = entityPlayer.getEntityData();
		synchedData.set(EntityDataSerializers.BYTE.createAccessor(17), (byte) 127);
		ClientboundSetEntityDataPacket metadataPacket = new ClientboundSetEntityDataPacket(entityPlayer.getId(), synchedData, true);

		sendPacket(hasPlayer, playerInfoPacket, spawnPacket, headRotationPacket, metadataPacket);
		spawnHologramFor(fakeNPC, hasPlayer);
	}

	public static void despawnFor(FakeNPC fakeNPC, UUID uuid) {
		OfflinePlayer offlinePlayer = PlayerUtils.getPlayer(uuid);
		if (offlinePlayer.getPlayer() != null && offlinePlayer.getPlayer().isOnline())
			despawnFor(fakeNPC, offlinePlayer.getPlayer());
	}

	public static void despawnFor(FakeNPC fakeNPC, HasPlayer hasPlayer) {
		ServerPlayer entityPlayer = fakeNPC.getEntityPlayer();
		PacketUtils.entityDestroy(hasPlayer, entityPlayer.getId());
		despawnHologramFor(fakeNPC.getHologram(), hasPlayer);
	}

	// Holograms

	public static void updateHologramFor(FakeNPC fakeNPC, @NonNull HasPlayer player) {
		Hologram hologram = fakeNPC.getHologram();
		if (isNullOrEmpty(hologram.getLines()))
			return;

		if (!isNullOrEmpty(hologram.getArmorStandList()))
			despawnHologramFor(hologram, player);

		spawnHologramFor(fakeNPC, player);
	}

	public static void spawnHologramFor(FakeNPC fakeNPC, @NonNull HasPlayer player) {
		int index = 0;
		Location location = fakeNPC.getEntityPlayer().getBukkitEntity().getLocation();
		List<ArmorStand> armorStands = fakeNPC.getHologram().getArmorStandList();
		List<String> lines = fakeNPC.getHologram().getLines();


		for (ArmorStand armorStand : armorStands) {
			spawnHologramFor(armorStand, player, location, lines.get(index), index++);
		}
	}

	private static void spawnHologramFor(ArmorStand armorStand, @NonNull HasPlayer player, Location loc, String line, int index) {
		double y = loc.getY() + 1.8 + (0.3 * index);

		armorStand.moveTo(loc.getX(), y, loc.getZ(), 0, 0);
		armorStand.setMarker(true);
		armorStand.setInvisible(true);
		armorStand.setNoBasePlate(true);
		armorStand.setSmall(true);
		if (line != null) {
			armorStand.setCustomName(new AdventureComponent(new JsonBuilder(line).build()));
			armorStand.setCustomNameVisible(true);
		}

		ClientboundAddEntityPacket spawnArmorStand = new ClientboundAddEntityPacket(armorStand, PacketUtils.getObjectId(armorStand));
		ClientboundSetEntityDataPacket rawMetadataPacket = new ClientboundSetEntityDataPacket(armorStand.getId(), armorStand.getEntityData(), true);
		ClientboundSetEquipmentPacket rawEquipmentPacket = new ClientboundSetEquipmentPacket(armorStand.getId(), PacketUtils.getEquipmentList());

		sendPacket(player, spawnArmorStand, rawMetadataPacket, rawEquipmentPacket);
	}

	public static void despawnHologramFor(Hologram hologram, @NonNull UUID uuid) {
		OfflinePlayer offlinePlayer = PlayerUtils.getPlayer(uuid);
		if (offlinePlayer.getPlayer() != null && offlinePlayer.getPlayer().isOnline())
			despawnHologramFor(hologram, offlinePlayer.getPlayer());
	}

	public static void despawnHologramFor(Hologram hologram, @NonNull HasPlayer player) {
		hologram.getArmorStandList().forEach(entityArmorStand -> PacketUtils.entityDestroy(player, entityArmorStand.getId()));
	}

	//
}

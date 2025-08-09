package gg.projecteden.nexus.features.fakenpc;

import com.mojang.authlib.GameProfile;
import gg.projecteden.nexus.features.fakenpc.FakeNPCUtils.SkinProperties;
import gg.projecteden.nexus.models.fakenpcs.npcs.FakeNPC;
import gg.projecteden.nexus.models.fakenpcs.npcs.FakeNPC.Hologram;
import gg.projecteden.nexus.models.fakenpcs.npcs.types.PlayerNPC;
import gg.projecteden.nexus.models.fakenpcs.users.FakeNPCUser;
import gg.projecteden.nexus.models.fakenpcs.users.FakeNPCUserService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.nms.NMSUtils;
import gg.projecteden.nexus.utils.nms.NMSUtils.Property;
import gg.projecteden.nexus.utils.nms.PacketUtils;
import gg.projecteden.parchment.HasPlayer;
import io.papermc.paper.adventure.AdventureComponent;
import lombok.NonNull;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket.Rot;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class FakeNPCPacketUtils {

	// NPCs

	public static void spawnFor(FakeNPC fakeNPC, HasPlayer hasPlayer) {
		if (fakeNPC instanceof PlayerNPC playerNPC) {
			ServerPlayer _serverPlayer = playerNPC.getEntityPlayer();
			if (playerNPC.isMirror())
				_serverPlayer = getNewSkin(playerNPC, SkinProperties.of(hasPlayer.getPlayer()));

			//
			final ServerPlayer serverPlayer = _serverPlayer;

			ClientboundPlayerInfoUpdatePacket playerInfoPacket = new ClientboundPlayerInfoUpdatePacket(Action.ADD_PLAYER, serverPlayer); // required

			ClientboundAddEntityPacket spawnPacket = new ClientboundAddEntityPacket(serverPlayer, 0, serverPlayer.blockPosition());
			ClientboundRotateHeadPacket headRotationPacket =
				new ClientboundRotateHeadPacket(serverPlayer, PacketUtils.encodeAngle(fakeNPC.getLocation().getYaw()));

			SynchedEntityData synchedData = serverPlayer.getEntityData();
			synchedData.set(EntityDataSerializers.BYTE.createAccessor(17), (byte) 127); // TODO: skin layers
			ClientboundSetEntityDataPacket metadataPacket = new ClientboundSetEntityDataPacket(serverPlayer.getId(), synchedData.packDirty());

			PacketUtils.sendPacket(hasPlayer, playerInfoPacket, spawnPacket, headRotationPacket, metadataPacket);

			if (fakeNPC.getHologram().isSpawned())
				spawnHologramFor(fakeNPC, hasPlayer);

			// Remove npc from tab
			Tasks.wait(2, () -> PacketUtils.sendPacket(hasPlayer, new ClientboundPlayerInfoRemovePacket(Collections.singletonList(serverPlayer.getUUID()))));
		}
	}

	private static ServerPlayer getNewSkin(PlayerNPC playerNPC, SkinProperties skinProperties) {
		ServerPlayer serverPlayer = NMSUtils.createServerPlayer(
			playerNPC.getEntityPlayer().getUUID(),
			playerNPC.getEntityPlayer().getBukkitEntity().getLocation(),
			playerNPC.getName());

		GameProfile profile = serverPlayer.getGameProfile();

		Property skinProperty = new Property("textures", skinProperties.getTexture(), skinProperties.getSignature());

		profile.getProperties().removeAll("textures"); // ensure client does not crash due to duplicate properties.
		profile.getProperties().put("textures", skinProperty.toNMS());

		return serverPlayer;
	}

	public static void despawnFor(FakeNPC fakeNPC, HasPlayer hasPlayer) {
		PacketUtils.entityDestroy(hasPlayer, fakeNPC.getEntity().getId());
	}

	// Holograms

	public static void updateHologram(FakeNPC fakeNPC) {
		Hologram hologram = fakeNPC.getHologram();
		if (hologram == null || gg.projecteden.api.common.utils.Nullables.isNullOrEmpty(hologram.getLines()))
			return;

		if (!gg.projecteden.api.common.utils.Nullables.isNullOrEmpty(hologram.getArmorStandList()))
			despawnHologram(fakeNPC);

		Tasks.wait(1, () -> spawnHologram(fakeNPC));
	}

	public static void spawnHologram(FakeNPC fakeNPC) {
		fakeNPC.getHologram().setSpawned(true);
		for (FakeNPCUser user : new FakeNPCUserService().getOnline())
			if (user.canSeeNPC(fakeNPC))
				spawnHologramFor(fakeNPC, user.getOnlinePlayer());
	}

	public static void despawnHologram(FakeNPC fakeNPC) {
		Hologram hologram = fakeNPC.getHologram();
		if (hologram == null)
			return;

		hologram.setSpawned(false);
		for (FakeNPCUser user : new FakeNPCUserService().getOnline())
			if (user.canSeeNPC(fakeNPC))
				despawnHologramFor(fakeNPC, user.getOnlinePlayer());
	}

	public static void updateHologramFor(FakeNPC fakeNPC, @NonNull HasPlayer player) {
		Hologram hologram = fakeNPC.getHologram();
		if (gg.projecteden.api.common.utils.Nullables.isNullOrEmpty(hologram.getLines()))
			return;

		if (!gg.projecteden.api.common.utils.Nullables.isNullOrEmpty(hologram.getArmorStandList()))
			despawnHologramFor(fakeNPC, player);

		spawnHologramFor(fakeNPC, player);
	}

	public static void spawnHologramFor(FakeNPC fakeNPC, @NonNull HasPlayer player) {
		int index = 0;
		Hologram hologram = fakeNPC.getHologram();
		List<ArmorStand> armorStands = hologram.getArmorStandList();
		List<String> lines = hologram.getLines();
		if (Nullables.isNullOrEmpty(lines))
			return;

		for (ArmorStand armorStand : armorStands) {
			spawnHologramFor(fakeNPC, armorStand, player, index);
			index++;
		}
	}

	private static void spawnHologramFor(FakeNPC fakeNPC, ArmorStand armorStand, @NonNull HasPlayer player, int index) {
		Hologram hologram = fakeNPC.getHologram();
		String line;
		if (index == 0) {
			if (!hologram.isNameVisible())
				return;
			line = hologram.getName();
		} else {
			line = hologram.getLines().get(index);
		}

		if (Nullables.isNullOrEmpty(line))
			return;

		Location npcLocation = fakeNPC.getBukkitEntity().getLocation();
		double y = npcLocation.getY() + 1.8 + (0.3 * index);

		armorStand.snapTo(npcLocation.getX(), y, npcLocation.getZ(), 0, 0);
		armorStand.setMarker(true);
		armorStand.setInvisible(true);
		armorStand.setNoBasePlate(true);
		armorStand.setSmall(true);

		armorStand.setCustomNameVisible(true);
		armorStand.setCustomName(new AdventureComponent(new JsonBuilder(line).build()));

		ServerLevel world = (ServerLevel) armorStand.level();
		ChunkMap.TrackedEntity entityTracker = world.getChunkSource().chunkMap.entityMap.get(armorStand.getId());


		ClientboundAddEntityPacket spawnArmorStand = new ClientboundAddEntityPacket(armorStand, 0, armorStand.blockPosition());
		ClientboundSetEntityDataPacket rawMetadataPacket = new ClientboundSetEntityDataPacket(armorStand.getId(), armorStand.getEntityData().packDirty());
		ClientboundSetEquipmentPacket rawEquipmentPacket = new ClientboundSetEquipmentPacket(armorStand.getId(), NMSUtils.getArmorEquipmentList());

		PacketUtils.sendPacket(player, spawnArmorStand, rawMetadataPacket, rawEquipmentPacket);
	}

	public static void despawnHologramFor(FakeNPC fakeNPC, @NonNull UUID uuid) {
		OfflinePlayer offlinePlayer = PlayerUtils.getPlayer(uuid);
		if (offlinePlayer.getPlayer() != null && offlinePlayer.getPlayer().isOnline())
			despawnHologramFor(fakeNPC, offlinePlayer.getPlayer());
	}

	public static void despawnHologramFor(FakeNPC fakeNPC, @NonNull HasPlayer player) {
		fakeNPC.getHologram().getArmorStandList().forEach(entityArmorStand -> PacketUtils.entityDestroy(player, entityArmorStand.getId()));
	}

	public static void lookAt(FakeNPC fakeNPC, @NonNull HasPlayer player) {
		net.minecraft.world.entity.Entity entity = fakeNPC.getEntity();
		Entity npcEntityBukkit = entity.getBukkitEntity();

		Location npcLocation = npcEntityBukkit.getLocation();
		if (npcEntityBukkit instanceof LivingEntity livingEntity)
			npcLocation = livingEntity.getEyeLocation();

		npcLocation.setYaw(0);
		npcLocation.setPitch(0);

		float entityYaw = npcLocation.getYaw();

		Location playerLocation = player.getPlayer().getEyeLocation();
		npcLocation.setDirection(playerLocation.toVector().subtract(npcLocation.toVector()));

		float yaw = npcLocation.getYaw() - entityYaw;
		float pitch = npcLocation.getPitch();

		if (yaw < -180)
			yaw += 360;
		else if (yaw >= 180)
			yaw -= 360;

		byte _yaw = PacketUtils.encodeAngle(yaw);
		byte _pitch = (byte) pitch;

		ClientboundRotateHeadPacket rotateHeadPacket = new ClientboundRotateHeadPacket(entity, _yaw);
		ClientboundMoveEntityPacket moveEntityPacket = new Rot(fakeNPC.getEntity().getId(), _yaw, _pitch, false);

		PacketUtils.sendPacket(player, rotateHeadPacket, moveEntityPacket);
	}
}

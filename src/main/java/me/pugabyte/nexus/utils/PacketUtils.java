package me.pugabyte.nexus.utils;

import com.comphenix.packetwrapper.WrapperPlayServerBlockChange;
import com.comphenix.packetwrapper.WrapperPlayServerEntityHeadRotation;
import com.comphenix.packetwrapper.WrapperPlayServerNamedEntitySpawn;
import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;
import com.comphenix.packetwrapper.WrapperPlayServerTileEntityData;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.mojang.datafixers.util.Pair;
import eden.interfaces.Named;
import lombok.NonNull;
import me.lexikiq.HasPlayer;
import me.pugabyte.nexus.Nexus;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_16_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftItemFrame;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PacketUtils {

	public static void copyTileEntityClient(HasPlayer recipient, Block origin, Location destination) {
		BlockPosition destinationPosition = toBlockPosition(destination);

		WrapperPlayServerBlockChange blockChange = new WrapperPlayServerBlockChange();
		blockChange.setLocation(destinationPosition);
		blockChange.setBlockData(WrappedBlockData.createData(Material.PLAYER_HEAD));

		WrapperPlayServerTileEntityData tileEntityData = new WrapperPlayServerTileEntityData();
		tileEntityData.setAction(4);
		tileEntityData.setLocation(destinationPosition);
		tileEntityData.setNbtData(NbtFactory.readBlockState(origin));

		Player player = recipient.getPlayer();
		blockChange.sendPacket(player);
		tileEntityData.sendPacket(player);
	}

	public static BlockPosition toBlockPosition(Location destination) {
		return new BlockPosition(destination.getBlockX(), destination.getBlockY(), destination.getBlockZ());
	}

	/*
	public void addNPCPacket(EntityPlayer npc, Player player) {
		PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
		connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc)); // "Adds the player data for the client to use when spawning a player" - https://wiki.vg/Protocol#Spawn_Player
		connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc)); // Spawns the NPC for the player client.
		connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256 / 360))); // Correct head rotation when spawned in player look direction.
	}
	 */

	public void npcPacket(Entity entity, HasPlayer recipient) {
		WrapperPlayServerPlayerInfo playerInfo = new WrapperPlayServerPlayerInfo();

		playerInfo.setAction(PlayerInfoAction.UPDATE_DISPLAY_NAME);
		//
		PlayerInfoData hi = new PlayerInfoData(
				WrappedGameProfile.fromHandle(playerInfo.getHandle()),
				0,
				NativeGameMode.SURVIVAL,
				WrappedChatComponent.fromText("hi")
		);

		PlayerInfoData playerInfoData = playerInfo.getData().get(0);
		WrappedGameProfile profile = playerInfoData.getProfile();
		WrappedGameProfile newProfile = profile.withName("hi");

		playerInfo.setData(Collections.singletonList(hi));
		//

		WrapperPlayServerNamedEntitySpawn entitySpawn = new WrapperPlayServerNamedEntitySpawn();
		entitySpawn.setEntityID(entity.getEntityId());

		WrapperPlayServerEntityHeadRotation headRotation = new WrapperPlayServerEntityHeadRotation();
		headRotation.setEntityID(entity.getEntityId());
		headRotation.setHeadYaw((byte) (entity.getLocation().getYaw() * 256 / 360));

		Player player = recipient.getPlayer();
		playerInfo.sendPacket(player);
		entitySpawn.sendPacket(player);
		headRotation.sendPacket(player);
	}

	public static void spawnItemFrame(@NonNull HasPlayer player, @NonNull Location location, BlockFace blockFace, ItemStack content, int rotation, boolean makeSound, boolean invisible) {
		if (content == null) content = new ItemStack(Material.AIR);
		if (blockFace == null) blockFace = BlockFace.NORTH;

		EnumDirection direction = CraftBlock.blockFaceToNotch(blockFace);
		EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
		EntityItemFrame itemFrame = new EntityItemFrame(EntityTypes.ITEM_FRAME, nmsPlayer.world);
		itemFrame.setLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ(), 0, 0);
		itemFrame.setItem(CraftItemStack.asNMSCopy(content), true, makeSound);
		itemFrame.setDirection(direction);
		itemFrame.setInvisible(invisible);
		itemFrame.setRotation(rotation);

		PacketPlayOutSpawnEntity rawSpawnPacket = new PacketPlayOutSpawnEntity(itemFrame, 71);

		PacketPlayOutEntityMetadata rawMetadataPacket = new PacketPlayOutEntityMetadata(
				itemFrame.getId(), itemFrame.getDataWatcher(), true);

		sendPackets(player, rawSpawnPacket, rawMetadataPacket);
	}

	public static void updateItemFrame(@NonNull HasPlayer player, @NonNull ItemFrame entity, ItemStack content, int rotation) {
		if (content == null) content = new ItemStack(Material.AIR);

		EntityItemFrame itemFrame = ((CraftItemFrame) entity).getHandle();

		DataWatcher dataWatcher = itemFrame.getDataWatcher();
		dataWatcher.set(DataWatcherRegistry.g.a(7), CraftItemStack.asNMSCopy(content));
		dataWatcher.set(DataWatcherRegistry.b.a(8), rotation);

		PacketPlayOutEntityMetadata rawMetadataPacket = new PacketPlayOutEntityMetadata(
				itemFrame.getId(), itemFrame.getDataWatcher(), true);

		sendPacket(player, rawMetadataPacket);
	}

	public static void spawnArmorStand(HasPlayer player, Location location, List<Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack>> equipment, boolean invisible) {
		if (equipment == null) equipment = getEquipmentList(null, null, null, null);

		EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
		EntityArmorStand armorStand = new EntityArmorStand(EntityTypes.ARMOR_STAND, nmsPlayer.world);
		armorStand.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		armorStand.setInvisible(invisible);

		PacketPlayOutSpawnEntity rawSpawnPacket = new PacketPlayOutSpawnEntity(armorStand, 78);
		PacketPlayOutEntityMetadata rawMetadataPacket = new PacketPlayOutEntityMetadata(armorStand.getId(), armorStand.getDataWatcher(), true);
		PacketPlayOutEntityEquipment rawEquipmentPacket = new PacketPlayOutEntityEquipment(armorStand.getId(), equipment);

		sendPackets(player, rawSpawnPacket, rawMetadataPacket, rawEquipmentPacket);
	}

	public static void updateArmorStandArmor(HasPlayer player, ArmorStand entity, List<Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack>> equipment) {
		if (equipment == null) equipment = getEquipmentList(null, null, null, null);
		EntityArmorStand armorStand = ((CraftArmorStand) entity).getHandle();

		PacketPlayOutEntityEquipment rawEquipmentPacket = new PacketPlayOutEntityEquipment(armorStand.getId(), equipment);

		sendPacket(player, rawEquipmentPacket);
	}


	public static List<Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack>> getEquipmentList() {
		return getEquipmentList(null, null, null, null);
	}

	public static List<Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack>> getEquipmentList(ItemStack head, ItemStack chest, ItemStack legs, ItemStack feet) {
		List<Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack>> equipmentList = new ArrayList<>();
		equipmentList.add(new Pair<>(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(head)));
		equipmentList.add(new Pair<>(EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(chest)));
		equipmentList.add(new Pair<>(EnumItemSlot.LEGS, CraftItemStack.asNMSCopy(legs)));
		equipmentList.add(new Pair<>(EnumItemSlot.FEET, CraftItemStack.asNMSCopy(feet)));
		return equipmentList;
	}


	private static void sendPackets(HasPlayer player, Object... packets) {
		for (Object packet : packets) {
			sendPacket(player, packet);
		}
	}

	private static void sendPacket(HasPlayer player, Object packet) {
		PacketContainer packetContainer = PacketContainer.fromPacket(packet);
		String name = player instanceof Named ? ((Named) player).getName() : player.getPlayer().getName();
		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(player.getPlayer(), packetContainer);
		} catch (InvocationTargetException e) {
			Nexus.log("Error trying to send " + packetContainer + " packet to " + name);
			e.printStackTrace();
		}
	}
}

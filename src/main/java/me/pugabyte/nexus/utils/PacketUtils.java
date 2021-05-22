package me.pugabyte.nexus.utils;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.mojang.datafixers.util.Pair;
import eden.interfaces.Named;
import lombok.NonNull;
import me.lexikiq.HasPlayer;
import me.pugabyte.nexus.Nexus;
import net.minecraft.server.v1_16_R3.*;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntity.PacketPlayOutEntityLook;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_16_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftItemFrame;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacketUtils {

	public static BlockPosition toBlockPosition(Location destination) {
		return new BlockPosition(destination.getBlockX(), destination.getBlockY(), destination.getBlockZ());
	}

	// Common

	public static void entityDestroy(@NonNull HasPlayer player, @NonNull Entity entity) {
		PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(entity.getId());
		sendPacket(player, destroyPacket);
	}

	public static void entityInvisible(@NonNull HasPlayer player, @NonNull org.bukkit.entity.Entity bukkitEntity, boolean invisible) {
		Entity entity = ((CraftEntity) bukkitEntity).getHandle();
		entity.setInvisible(invisible);
		PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(entity.getId(), entity.getDataWatcher(), true);
		sendPacket(player, metadataPacket);
	}

	public static void entityLook(@NonNull HasPlayer player, @NonNull org.bukkit.entity.Entity bukkitEntity, float yaw, float pitch) {
		Entity entity = ((CraftEntity) bukkitEntity).getHandle();
		PacketPlayOutEntityHeadRotation headRotationPacket = new PacketPlayOutEntityHeadRotation(entity, (byte) (yaw * 256 / 360));
		PacketPlayOutEntityLook lookPacket = new PacketPlayOutEntity.PacketPlayOutEntityLook(entity.getId(), (byte) (yaw * 256 / 360), (byte) (pitch * 256 / 360), true);
		sendPacket(player, headRotationPacket, lookPacket);
	}


	// can't get move to work correctly
//	public static void entityMove(@NonNull HasPlayer player, @NonNull org.bukkit.entity.Entity bukkitEntity, double x, double y, double z) {
//		Entity entity = ((CraftEntity) bukkitEntity).getHandle();
//		PacketPlayOutRelEntityMove movePacket = new PacketPlayOutRelEntityMove(entity.getId(), (short)(x * 4096), (short)(y * 4096), (short)(z * 4096), true);
//		sendPacket(player, movePacket);
//	}

	// can't get move to work correctly
//	public static void entityMoveLook(@NonNull HasPlayer player, @NonNull org.bukkit.entity.Entity bukkitEntity, double x, double y, double z, float yaw, float pitch){
//		Entity entity = ((CraftEntity) bukkitEntity).getHandle();
//		PacketPlayOutRelEntityMoveLook moveLookPacket = new PacketPlayOutRelEntityMoveLook(entity.getId(), (short)(x * 4096), (short)(y * 4096), (short)(z * 4096), (byte)(yaw * 256 / 360), (byte)(pitch * 256 / 360), true);
//		sendPacket(player, moveLookPacket);
//	}

	// NPC


	// TODO: if possible
	public static void updateNPCName(@NonNull HasPlayer player, org.bukkit.entity.NPC entity, String name) {
		EntityPlayer entityPlayer = ((CraftPlayer) entity).getHandle();
		entityPlayer.setCustomName(new ChatComponentText(name));
		PacketPlayOutEntityMetadata entityMetadataPacket = new PacketPlayOutEntityMetadata();

//		DataWatcher dataWatcher = entityPlayer.getDataWatcher();
//		dataWatcher.set(DataWatcherRegistry.d.a(), );

//		entityMetadataPacket
	}

	/*
	public void addNPCPacket(EntityPlayer npc, Player player) {
		PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
		connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc)); // "Adds the player data for the client to use when spawning a player" - https://wiki.vg/Protocol#Spawn_Player
		connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc)); // Spawns the NPC for the player client.
		connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256 / 360))); // Correct head rotation when spawned in player look direction.
	}
	 */

	// Item Frame
	public static EntityItemFrame spawnItemFrame(@NonNull HasPlayer player, @NonNull Location location, BlockFace blockFace, ItemStack content, int rotation, boolean makeSound, boolean invisible) {
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

		PacketPlayOutSpawnEntity rawSpawnPacket = new PacketPlayOutSpawnEntity(itemFrame, getObjectId(itemFrame));

		PacketPlayOutEntityMetadata rawMetadataPacket = new PacketPlayOutEntityMetadata(
				itemFrame.getId(), itemFrame.getDataWatcher(), true);

		sendPacket(player, rawSpawnPacket, rawMetadataPacket);
		return itemFrame;
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

	// Armor Stand
	public static void spawnArmorStand(HasPlayer player, Location location, List<Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack>> equipment, boolean invisible) {
		if (equipment == null) equipment = getEquipmentList(null, null, null, null);

		EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
		EntityArmorStand armorStand = new EntityArmorStand(EntityTypes.ARMOR_STAND, nmsPlayer.world);
		armorStand.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		armorStand.setInvisible(invisible);

		PacketPlayOutSpawnEntity rawSpawnPacket = new PacketPlayOutSpawnEntity(armorStand, getObjectId(armorStand));
		PacketPlayOutEntityMetadata rawMetadataPacket = new PacketPlayOutEntityMetadata(armorStand.getId(), armorStand.getDataWatcher(), true);
		PacketPlayOutEntityEquipment rawEquipmentPacket = new PacketPlayOutEntityEquipment(armorStand.getId(), equipment);

		sendPacket(player, rawSpawnPacket, rawMetadataPacket, rawEquipmentPacket);
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

	//

	private static void sendPacket(HasPlayer player, Object... packets) {
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

	// TODO 1.17: Update object and living ids
	public static Integer getObjectId(net.minecraft.server.v1_16_R3.Entity entity) {
		if (entity == null)
			return null;

		EntityTypes<?> type = entity.getEntityType();

		// Prioritize these ID's first
		Map<EntityTypes<?>, Integer> objectIds = new HashMap<>() {{
			put(EntityTypes.BOAT, 1);
			put(EntityTypes.ITEM, 2);
			put(EntityTypes.AREA_EFFECT_CLOUD, 3);
			put(EntityTypes.MINECART, 10);
			put(EntityTypes.CHEST_MINECART, 10);
			put(EntityTypes.COMMAND_BLOCK_MINECART, 10);
			put(EntityTypes.FURNACE_MINECART, 10);
			put(EntityTypes.HOPPER_MINECART, 10);
			put(EntityTypes.SPAWNER_MINECART, 10);
			put(EntityTypes.TNT_MINECART, 10);
			put(EntityTypes.TNT, 50);
			put(EntityTypes.END_CRYSTAL, 51);
			put(EntityTypes.ARROW, 60);
			put(EntityTypes.SNOWBALL, 61);
			put(EntityTypes.EGG, 62);
			put(EntityTypes.FIREBALL, 63);
			put(EntityTypes.SMALL_FIREBALL, 64);
			put(EntityTypes.ENDER_PEARL, 65);
			put(EntityTypes.WITHER_SKULL, 66);
			put(EntityTypes.SHULKER_BULLET, 67);
			put(EntityTypes.LLAMA_SPIT, 68);
			put(EntityTypes.FALLING_BLOCK, 70);
			put(EntityTypes.ITEM_FRAME, 71);
			put(EntityTypes.EYE_OF_ENDER, 72);
			put(EntityTypes.POTION, 73);
			put(EntityTypes.EXPERIENCE_BOTTLE, 75);
			put(EntityTypes.FIREWORK_ROCKET, 76);
			put(EntityTypes.LEASH_KNOT, 77);
			put(EntityTypes.ARMOR_STAND, 78);
			put(EntityTypes.EVOKER_FANGS, 79);
			put(EntityTypes.FISHING_BOBBER, 90);
			put(EntityTypes.SPECTRAL_ARROW, 91);
			put(EntityTypes.DRAGON_FIREBALL, 93);
			put(EntityTypes.TRIDENT, 94);
		}};

		if (objectIds.containsKey(type))
			return objectIds.get(type);

		Map<EntityTypes<?>, Integer> livingIds = new HashMap<>() {{
			put(EntityTypes.BAT, 3);
			put(EntityTypes.BEE, 4);
			put(EntityTypes.BLAZE, 5);
			put(EntityTypes.CAT, 7);
			put(EntityTypes.CAVE_SPIDER, 8);
			put(EntityTypes.CHICKEN, 9);
			put(EntityTypes.COD, 10);
			put(EntityTypes.COW, 11);
			put(EntityTypes.CREEPER, 12);
			put(EntityTypes.DOLPHIN, 13);
			put(EntityTypes.DONKEY, 14);
			put(EntityTypes.DROWNED, 16);
			put(EntityTypes.ELDER_GUARDIAN, 17);
			put(EntityTypes.ENDER_DRAGON, 19);
			put(EntityTypes.ENDERMAN, 20);
			put(EntityTypes.ENDERMITE, 21);
			put(EntityTypes.EVOKER, 22);
			put(EntityTypes.EXPERIENCE_ORB, 24);
			put(EntityTypes.FOX, 28);
			put(EntityTypes.GHAST, 29);
			put(EntityTypes.GIANT, 30);
			put(EntityTypes.GUARDIAN, 31);
			put(EntityTypes.HOGLIN, 32);
			put(EntityTypes.HORSE, 33);
			put(EntityTypes.HUSK, 34);
			put(EntityTypes.ILLUSIONER, 35);
			put(EntityTypes.LLAMA, 42);
			put(EntityTypes.MAGMA_CUBE, 44);
			put(EntityTypes.MULE, 52);
			put(EntityTypes.MOOSHROOM, 53);
			put(EntityTypes.OCELOT, 54);
			put(EntityTypes.PAINTING, 55);
			put(EntityTypes.PANDA, 56);
			put(EntityTypes.PARROT, 57);
			put(EntityTypes.PHANTOM, 58);
			put(EntityTypes.PIG, 59);
			put(EntityTypes.PIGLIN, 60);
			put(EntityTypes.PIGLIN_BRUTE, 61);
			put(EntityTypes.PILLAGER, 62);
			put(EntityTypes.POLAR_BEAR, 63);
			put(EntityTypes.PUFFERFISH, 65);
			put(EntityTypes.RABBIT, 66);
			put(EntityTypes.RAVAGER, 67);
			put(EntityTypes.SALMON, 68);
			put(EntityTypes.SHEEP, 69);
			put(EntityTypes.SHULKER, 70);
			put(EntityTypes.SILVERFISH, 72);
			put(EntityTypes.SKELETON, 73);
			put(EntityTypes.SKELETON_HORSE, 74);
			put(EntityTypes.SLIME, 75);
			put(EntityTypes.SNOW_GOLEM, 77);
			put(EntityTypes.SPIDER, 80);
			put(EntityTypes.SQUID, 81);
			put(EntityTypes.STRAY, 82);
			put(EntityTypes.STRIDER, 83);
			put(EntityTypes.TRADER_LLAMA, 89);
			put(EntityTypes.TROPICAL_FISH, 90);
			put(EntityTypes.TURTLE, 91);
			put(EntityTypes.VEX, 92);
			put(EntityTypes.VILLAGER, 93);
			put(EntityTypes.VINDICATOR, 94);
			put(EntityTypes.WANDERING_TRADER, 95);
			put(EntityTypes.WITCH, 96);
			put(EntityTypes.WITHER, 97);
			put(EntityTypes.WITHER_SKELETON, 98);
			put(EntityTypes.WOLF, 100);
			put(EntityTypes.ZOGLIN, 101);
			put(EntityTypes.ZOMBIE, 102);
			put(EntityTypes.ZOMBIE_HORSE, 103);
			put(EntityTypes.ZOMBIE_VILLAGER, 104);
			put(EntityTypes.ZOMBIFIED_PIGLIN, 105);
			put(EntityTypes.PLAYER, 106);
		}};

		if (livingIds.containsKey(type))
			return livingIds.get(type);

		return null;
	}

}

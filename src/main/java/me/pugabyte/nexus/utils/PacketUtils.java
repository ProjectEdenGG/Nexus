package me.pugabyte.nexus.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.mojang.authlib.GameProfile;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.mojang.datafixers.util.Pair;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import me.lexikiq.HasPlayer;
import me.pugabyte.nexus.Nexus;
import net.minecraft.server.v1_16_R3.*;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntity.PacketPlayOutEntityLook;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntity.PacketPlayOutRelEntityMove;
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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@UtilityClass
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

	/**
	 * Gets the slot int corresponding to an {@link EnumItemSlot}. Returns -1 for {@link EnumItemSlot#MAINHAND MAINHAND}.
	 * @param slot an item slot
	 * @return integer slot
	 */
	public int getSlotInt(EnumItemSlot slot) {
		return switch (slot) {
			case MAINHAND -> -1;
			case OFFHAND -> 45;
			case FEET -> 8;
			case LEGS -> 7;
			case CHEST -> 6;
			case HEAD -> 5;
		};
	}

	/**
	 * Gets the slot int corresponding to an {@link EnumWrappers.ItemSlot}. Returns -1 for {@link EnumWrappers.ItemSlot#MAINHAND MAINHAND}.
	 * @param slot an item slot
	 * @return integer slot
	 */
	public int getSlotInt(EnumWrappers.ItemSlot slot) {
		return switch (slot) {
			case MAINHAND -> -1;
			case OFFHAND -> 45;
			case FEET -> 8;
			case LEGS -> 7;
			case CHEST -> 6;
			case HEAD -> 5;
		};
	}

	/**
	 * Gets the {@link EnumItemSlot} corresponding to an {@link EnumWrappers.ItemSlot}.
	 * @param slot an item slot
	 * @return enum item slot
	 */
	public EnumItemSlot getEnumItemSlot(EnumWrappers.ItemSlot slot) {
		return EnumItemSlot.valueOf(slot.name());
	}

	/**
	 * Sends a fake packet for an armor piece or main/off-hand item for a player.
	 * <p>
	 * To avoid sending a packet to the item owner, remove them from <code>recipients</code>.
	 * @param owner player to "give" the item
	 * @param recipients packet recipients
	 * @param item item to "give"
	 * @param slot slot to "set"
	 */
	public void sendFakeItem(HasPlayer owner, Collection<? extends HasPlayer> recipients, ItemStack item, EnumItemSlot slot) {
		Player player = owner.getPlayer();

		// self packet avoids playing the armor equip sound effect
		PacketContainer selfPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SET_SLOT);
		selfPacket.getIntegers().write(0, 0); // inventory ID (0 = player)
		int slotInt = getSlotInt(slot);
		if (slotInt == -1)
			slotInt = owner.getPlayer().getInventory().getHeldItemSlot() + 36;
		selfPacket.getIntegers().write(1, slotInt);
		selfPacket.getItemModifier().write(0, item);

		// other packet is sent to all other players to show the armor piece
		List<Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack>> equipmentList = new ArrayList<>();
		equipmentList.add(new Pair<>(slot, CraftItemStack.asNMSCopy(item)));
		PacketPlayOutEntityEquipment rawPacket = new PacketPlayOutEntityEquipment(player.getEntityId(), equipmentList);
		PacketContainer otherPacket = PacketContainer.fromPacket(rawPacket);

		// send packets
		recipients.stream().filter(_player -> player.getWorld() == _player.getPlayer().getWorld()).forEach(_player -> {
			PacketContainer packet = _player.getPlayer().getUniqueId().equals(player.getUniqueId()) ? selfPacket : otherPacket;
			sendPacket(_player, packet);
		});
	}

	/**
	 * Sends fake packets for an armor piece or main/off-hand item for a player.
	 * @param owner player to "give" the item
	 * @param recipients packet recipients
	 * @param item item to "give"
	 * @param slot slot to "set"
	 */
	public void sendFakeItem(HasPlayer owner, Collection<? extends HasPlayer> recipients, ItemStack item, EnumWrappers.ItemSlot slot) {
		sendFakeItem(owner, recipients, item, getEnumItemSlot(slot));
	}


	// untested
	public static void entityRelativeMove(@NonNull HasPlayer player, @NonNull org.bukkit.entity.Entity bukkitEntity, Vector delta, boolean onGround) {
		Entity entity = ((CraftEntity) bukkitEntity).getHandle();
		PacketPlayOutRelEntityMove movePacket = new PacketPlayOutRelEntityMove(entity.getId(),
				encodePosition(delta.getX()), encodePosition(delta.getY()), encodePosition(delta.getZ()), onGround);

		sendPacket(player, movePacket);
	}

	public static void entityTeleport(@NonNull HasPlayer player, @NonNull org.bukkit.entity.Entity bukkitEntity, Location location, boolean onGround) {
		Entity entity = ((CraftEntity) bukkitEntity).getHandle();
		entity.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		entity.setOnGround(onGround);

		sendPacket(player, new PacketPlayOutEntityTeleport(entity));
		entityLook(player, bukkitEntity, location.getYaw(), location.getPitch());
	}

	// NPC


	// TODO: if possible
	public static void entityName(@NonNull HasPlayer player, org.bukkit.entity.NPC entity, String name) {
		EntityPlayer entityPlayer = ((CraftPlayer) entity).getHandle();
		GameProfile profile = new GameProfile(UUID.randomUUID(), name);
//		entityPlayer.setCustomName(new ChatComponentText(name));
		PacketPlayOutEntityMetadata entityMetadataPacket = new PacketPlayOutEntityMetadata();

//		DataWatcher dataWatcher = entityPlayer.getDataWatcher();
//		dataWatcher.set(DataWatcherRegistry. );

//		entityMetadataPacket
	}

	public static List<EntityArmorStand> entityNameFake(@NonNull HasPlayer player, org.bukkit.entity.Entity bukkitEntity, String... customNames) {
		return entityNameFake(player, bukkitEntity, 0.3, customNames);
	}

	public static List<EntityArmorStand> entityNameFake(@NonNull HasPlayer player, org.bukkit.entity.Entity bukkitEntity, double distance, String... customNames) {
		int index = 0;
		List<EntityArmorStand> armorStands = new ArrayList<>();
		for (String customName : customNames)
			armorStands.add(entityNameFake(player, bukkitEntity, customName, index++, distance));

		if (armorStands.isEmpty())
			armorStands = null;

		return armorStands;
	}

	public static EntityArmorStand entityNameFake(@NonNull HasPlayer player, org.bukkit.entity.Entity bukkitEntity, String customName) {
		return entityNameFake(player, bukkitEntity, customName, 0);
	}

	public static EntityArmorStand entityNameFake(@NonNull HasPlayer player, org.bukkit.entity.Entity bukkitEntity, String customName, int index) {
		return entityNameFake(player, bukkitEntity, customName, index, 0.3);
	}

	public static EntityArmorStand entityNameFake(@NonNull HasPlayer player, org.bukkit.entity.Entity bukkitEntity, String customName, int index, double distance) {
		EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
		EntityArmorStand armorStand = new EntityArmorStand(EntityTypes.ARMOR_STAND, nmsPlayer.world);
		Location loc = bukkitEntity.getLocation();
		double y = (loc.getY() + 1.8) + (distance * index);

		armorStand.setLocation(loc.getX(), y, loc.getZ(), 0, 0);
		armorStand.setMarker(true);
		armorStand.setInvisible(true);
		armorStand.setBasePlate(true);
		armorStand.setSmall(true);
		if (customName != null) {
			armorStand.setCustomName(new ChatComponentText(StringUtils.colorize(customName)));
			armorStand.setCustomNameVisible(true);
		}

		PacketPlayOutSpawnEntity spawnArmorStand = new PacketPlayOutSpawnEntity(armorStand, getObjectId(armorStand));
		PacketPlayOutEntityMetadata rawMetadataPacket = new PacketPlayOutEntityMetadata(armorStand.getId(), armorStand.getDataWatcher(), true);
		PacketPlayOutEntityEquipment rawEquipmentPacket = new PacketPlayOutEntityEquipment(armorStand.getId(), getEquipmentList());

		sendPacket(player, spawnArmorStand, rawMetadataPacket, rawEquipmentPacket);
		return armorStand;
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

	public static EntityArmorStand spawnArmorStand(HasPlayer player, Location location, boolean invisible) {
		return spawnArmorStand(player, location, null, invisible);
	}

	public static EntityArmorStand spawnArmorStand(HasPlayer player, Location location, boolean invisible, String customName) {
		return spawnArmorStand(player, location, null, invisible, customName);
	}

	public static EntityArmorStand spawnArmorStand(HasPlayer player, Location location, List<Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack>> equipment, boolean invisible) {
		return spawnArmorStand(player, location, equipment, invisible, null);
	}

	public static EntityArmorStand spawnArmorStand(HasPlayer player, Location location, List<Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack>> equipment, boolean invisible, String customName) {
		if (equipment == null) equipment = getEquipmentList(null, null, null, null);

		EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
		EntityArmorStand armorStand = new EntityArmorStand(EntityTypes.ARMOR_STAND, nmsPlayer.world);
		armorStand.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		armorStand.setInvisible(invisible);
		if (customName != null) {
			armorStand.setCustomName(new ChatComponentText(customName));
			armorStand.setCustomNameVisible(true);
		}

		PacketPlayOutSpawnEntity rawSpawnPacket = new PacketPlayOutSpawnEntity(armorStand, getObjectId(armorStand));
		PacketPlayOutEntityMetadata rawMetadataPacket = new PacketPlayOutEntityMetadata(armorStand.getId(), armorStand.getDataWatcher(), true);
		PacketPlayOutEntityEquipment rawEquipmentPacket = new PacketPlayOutEntityEquipment(armorStand.getId(), equipment);

		sendPacket(player, rawSpawnPacket, rawMetadataPacket, rawEquipmentPacket);
		return armorStand;
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

	public static void sendPacket(HasPlayer player, Collection<?> packets) {
		packets.forEach(packet -> sendPacket(player, packet));
	}

	public static void sendPacket(HasPlayer player, Object... packets) {
		for (Object packet : packets) {
			if (packet instanceof PacketContainer container)
				sendPacket(player, container);
			else
				sendPacket(player, PacketContainer.fromPacket(packet));
		}
	}

	public static void sendPacket(HasPlayer player, PacketContainer... packets) {
		for (PacketContainer packet : packets) {
			try {
				ProtocolLibrary.getProtocolManager().sendServerPacket(player.getPlayer(), packet);
			} catch (InvocationTargetException e) {
				Nexus.log("Error trying to send " + packet + " packet to " + player.getPlayer().getName());
				e.printStackTrace();
			}
		}
	}

	private static byte encodeAngle(float angle) {
		return (byte) (angle * 256f / 360f);
	}

	private static int encodeVelocity(double v) {
		return (int) (v * 8000D);
	}

	private static short encodePosition(double d) {
		return (short) (d * 4096D);
	}

	// TODO 1.17: Update object and living ids
	public static Integer getObjectId(Entity entity) {
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

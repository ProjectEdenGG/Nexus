package gg.projecteden.nexus.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.parchment.HasPlayer;
import lombok.NonNull;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket.Pos;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket.Rot;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_18_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_18_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftItemFrame;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/*
 TODO for 1.18
       - ClientboundAddEntityPacket seems to have been updated as this code is using the second
         constructor parameter to specify the entity ID despite the parameter (seemingly) not being
         used for that purpose
 */

public class PacketUtils {

	public static BlockPosition toBlockPosition(Location destination) {
		return new BlockPosition(destination.getBlockX(), destination.getBlockY(), destination.getBlockZ());
	}

	// Common

	public static void entityDestroy(@NonNull HasPlayer player, org.bukkit.entity.Entity entity) {
		entityDestroy(player, entity.getEntityId());
	}

	public static void entityDestroy(@NonNull HasPlayer player, int entityId) {
		ClientboundRemoveEntitiesPacket destroyPacket = new ClientboundRemoveEntitiesPacket(entityId);
		sendPacket(player, destroyPacket);
	}

	public static void entityInvisible(@NonNull HasPlayer player, @NonNull org.bukkit.entity.Entity bukkitEntity, boolean invisible) {
		Entity entity = ((CraftEntity) bukkitEntity).getHandle();
		entity.setInvisible(invisible);
		ClientboundSetEntityDataPacket metadataPacket = new ClientboundSetEntityDataPacket(entity.getId(), entity.getEntityData(), true);
		sendPacket(player, metadataPacket);
	}

	public static void entityLook(@NonNull HasPlayer player, @NonNull org.bukkit.entity.Entity bukkitEntity, float yaw, float pitch) {
		Entity entity = ((CraftEntity) bukkitEntity).getHandle();
		ClientboundRotateHeadPacket headRotationPacket = new ClientboundRotateHeadPacket(entity, (byte) (yaw * 256 / 360));
		Rot lookPacket = new ClientboundMoveEntityPacket.Rot(entity.getId(), (byte) (yaw * 256 / 360), (byte) (pitch * 256 / 360), true);
		sendPacket(player, headRotationPacket, lookPacket);
	}

	/**
	 * Gets the slot int corresponding to an {@link EquipmentSlot}. Returns -1 for {@link EquipmentSlot#MAINHAND}.
	 * @param slot an item slot
	 * @return integer slot
	 * @deprecated enum fields are now obfuscated, use {@link #getSlotInt(EnumWrappers.ItemSlot)}
	 */
	@Deprecated
	public static int getSlotInt(EquipmentSlot slot) {
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
	public static int getSlotInt(EnumWrappers.ItemSlot slot) {
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
	 * Gets the {@link EquipmentSlot} corresponding to an {@link EnumWrappers.ItemSlot}.
	 * @param slot an item slot
	 * @return enum item slot
	 */
	public static EquipmentSlot getEquipmentSlot(EnumWrappers.ItemSlot slot) {
		return switch (slot) {
			case MAINHAND -> EquipmentSlot.MAINHAND;
			case OFFHAND -> EquipmentSlot.OFFHAND;
			case FEET -> EquipmentSlot.FEET;
			case LEGS -> EquipmentSlot.LEGS;
			case CHEST -> EquipmentSlot.CHEST;
			case HEAD -> EquipmentSlot.HEAD;
		};
	}

	/**
	 * Gets the {@link EnumWrappers.ItemSlot} corresponding to an {@link org.bukkit.inventory.EquipmentSlot}.
	 * @param slot an item slot
	 * @return protocol item slot
	 */
	public static EnumWrappers.ItemSlot getItemSlot(org.bukkit.inventory.EquipmentSlot slot) {
		return switch (slot) {
			case HAND -> EnumWrappers.ItemSlot.MAINHAND;
			case OFF_HAND -> EnumWrappers.ItemSlot.OFFHAND;
			case FEET -> EnumWrappers.ItemSlot.FEET;
			case LEGS -> EnumWrappers.ItemSlot.LEGS;
			case CHEST -> EnumWrappers.ItemSlot.CHEST;
			case HEAD -> EnumWrappers.ItemSlot.HEAD;
		};
	}

	/**
	 * Sends a fake packet for an armor piece or main/off-hand item for a player.
	 * <p>
	 * To avoid sending a packet to the item owner, remove them from <code>recipients</code>.
	 * @param owner player to "give" the item
	 * @param recipients packet recipients
	 * @param item item to "give"
	 * @param slot slot to "set"
	 * @deprecated enum names are now obfuscated, use {@link #sendFakeItem(org.bukkit.entity.Entity, HasPlayer, ItemStack, EnumWrappers.ItemSlot)}
	 */
	@Deprecated
	public static void sendFakeItem(org.bukkit.entity.Entity owner, Collection<? extends HasPlayer> recipients, ItemStack item, EquipmentSlot slot) {
		// self packet avoids playing the armor equip sound effect
		PacketContainer selfPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SET_SLOT);
		selfPacket.getIntegers().write(0, 0); // inventory ID (0 = player)
		int slotInt = getSlotInt(slot);
		if (slotInt == -1 && owner instanceof org.bukkit.entity.HumanEntity player)
			slotInt = player.getInventory().getHeldItemSlot() + 36;
		selfPacket.getIntegers().write(2, slotInt);
		selfPacket.getItemModifier().write(0, item);

		// other packet is sent to all other players to show the armor piece
		List<Pair<EquipmentSlot, net.minecraft.world.item.ItemStack>> equipmentList = new ArrayList<>();
		equipmentList.add(new Pair<>(slot, CraftItemStack.asNMSCopy(item)));
		ClientboundSetEquipmentPacket rawPacket = new ClientboundSetEquipmentPacket(owner.getEntityId(), equipmentList);
		PacketContainer otherPacket = PacketContainer.fromPacket(rawPacket);

		// send packets
		recipients.stream().filter(player -> owner.getWorld().equals(player.getPlayer().getWorld())).forEach(player -> {
			PacketContainer packet = player.getPlayer().getUniqueId().equals(owner.getUniqueId()) ? selfPacket : otherPacket;
			sendPacket(player, packet);
		});
	}

	/**
	 * Sends a fake packet for an armor piece or main/off-hand item for a player.
	 * <p>
	 * To avoid sending a packet to the item owner, remove them from <code>recipients</code>.
	 * @param owner player to "give" the item
	 * @param recipient packet recipient
	 * @param item item to "give"
	 * @param slot slot to "set"
	 * @deprecated enum names are now obfuscated, use {@link #sendFakeItem(org.bukkit.entity.Entity, HasPlayer, ItemStack, EnumWrappers.ItemSlot)}
	 */
	@Deprecated
	public static void sendFakeItem(org.bukkit.entity.Entity owner, HasPlayer recipient, ItemStack item, EquipmentSlot slot) {
		sendFakeItem(owner, Collections.singletonList(recipient), item, slot);
	}

	/**
	 * Sends fake packets for an armor piece or main/off-hand item for a player.
	 * <p>
	 * To avoid sending a packet to the item owner, remove them from <code>recipients</code>.
	 * @param owner player to "give" the item
	 * @param recipients packet recipients
	 * @param item item to "give"
	 * @param slot slot to "set"
	 */
	public static void sendFakeItem(org.bukkit.entity.Entity owner, Collection<? extends HasPlayer> recipients, ItemStack item, EnumWrappers.ItemSlot slot) {
		sendFakeItem(owner, recipients, item, getEquipmentSlot(slot));
	}

	/**
	 * Sends a fake packet for an armor piece or main/off-hand item for a player.
	 * <p>
	 * To avoid sending a packet to the item owner, remove them from <code>recipients</code>.
	 * @param owner player to "give" the item
	 * @param recipient packet recipient
	 * @param item item to "give"
	 * @param slot slot to "set"
	 */
	public static void sendFakeItem(org.bukkit.entity.Entity owner, HasPlayer recipient, ItemStack item, EnumWrappers.ItemSlot slot) {
		sendFakeItem(owner, Collections.singletonList(recipient), item, slot);
	}

	/**
	 * Sends fake packets for an armor piece or main/off-hand item for a player.
	 * <p>
	 * To avoid sending a packet to the item owner, remove them from <code>recipients</code>.
	 * @param owner player to "give" the item
	 * @param recipients packet recipients
	 * @param item item to "give"
	 * @param slot slot to "set"
	 */
	public static void sendFakeItem(org.bukkit.entity.Entity owner, Collection<? extends HasPlayer> recipients, ItemStack item, org.bukkit.inventory.EquipmentSlot slot) {
		sendFakeItem(owner, recipients, item, getItemSlot(slot));
	}

	/**
	 * Sends a fake packet for an armor piece or main/off-hand item for a player.
	 * <p>
	 * To avoid sending a packet to the item owner, remove them from <code>recipients</code>.
	 * @param owner player to "give" the item
	 * @param recipient packet recipient
	 * @param item item to "give"
	 * @param slot slot to "set"
	 */
	public static void sendFakeItem(org.bukkit.entity.Entity owner, HasPlayer recipient, ItemStack item, org.bukkit.inventory.EquipmentSlot slot) {
		sendFakeItem(owner, Collections.singletonList(recipient), item, slot);
	}


	// untested
	public static void entityRelativeMove(@NonNull HasPlayer player, @NonNull org.bukkit.entity.Entity bukkitEntity, Vector delta, boolean onGround) {
		Entity entity = ((CraftEntity) bukkitEntity).getHandle();
		Pos movePacket = new Pos(entity.getId(),
				encodePosition(delta.getX()), encodePosition(delta.getY()), encodePosition(delta.getZ()), onGround);

		sendPacket(player, movePacket);
	}

	public static void entityTeleport(@NonNull HasPlayer player, @NonNull org.bukkit.entity.Entity bukkitEntity, Location location, boolean onGround) {
		Entity entity = ((CraftEntity) bukkitEntity).getHandle();
		entity.moveTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		entity.setOnGround(onGround);

		sendPacket(player, new ClientboundTeleportEntityPacket(entity));
		entityLook(player, bukkitEntity, location.getYaw(), location.getPitch());
	}

	// TODO: if possible
	public static void entityName(@NonNull HasPlayer player, org.bukkit.entity.NPC entity, String name) {
		ServerPlayer entityPlayer = ((CraftPlayer) entity).getHandle();
		GameProfile profile = new GameProfile(UUID.randomUUID(), name);
//		entityPlayer.setCustomName(new TextComponent(name));
//		ClientboundSetEntityDataPacket entityMetadataPacket = new ClientboundSetEntityDataPacket();

//		SynchedEntityData dataWatcher = entityPlayer.getDataWatcher();
//		dataWatcher.set(DataWatcherRegistry. );

//		entityMetadataPacket
	}

	public static List<ArmorStand> entityNameFake(@NonNull HasPlayer player, org.bukkit.entity.Entity bukkitEntity, String... customNames) {
		return entityNameFake(player, bukkitEntity, 0.3, customNames);
	}

	public static ArmorStand entityNameFake(@NonNull HasPlayer player, org.bukkit.entity.Entity bukkitEntity, double distance, String customName) {
		return entityNameFake(player, bukkitEntity, distance, customName, 0);
	}

	public static List<ArmorStand> entityNameFake(@NonNull HasPlayer player, org.bukkit.entity.Entity bukkitEntity, double distance, String... customNames) {
		int index = 0;
		List<ArmorStand> armorStands = new ArrayList<>();
		for (String customName : customNames)
			armorStands.add(entityNameFake(player, bukkitEntity, distance, customName, index++));

		if (armorStands.isEmpty())
			armorStands = null;

		return armorStands;
	}

	public static ArmorStand entityNameFake(@NonNull HasPlayer player, org.bukkit.entity.Entity bukkitEntity, String customName) {
		return entityNameFake(player, bukkitEntity, customName, 0);
	}

	public static ArmorStand entityNameFake(@NonNull HasPlayer player, org.bukkit.entity.Entity bukkitEntity, String customName, int index) {
		return entityNameFake(player, bukkitEntity, 0.3, customName, index);
	}

	public static ArmorStand entityNameFake(@NonNull HasPlayer player, org.bukkit.entity.Entity bukkitEntity, double distance, String customName, int index) {
		ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
		ArmorStand armorStand = new ArmorStand(EntityType.ARMOR_STAND, nmsPlayer.getLevel());
		Location loc = bukkitEntity.getLocation();
		double y = loc.getY() + (distance * index);
		if (bukkitEntity instanceof org.bukkit.entity.Player)
			y += 1.8;

		armorStand.moveTo(loc.getX(), y, loc.getZ(), 0, 0);
		armorStand.setMarker(true);
		armorStand.setInvisible(true);
		armorStand.setNoBasePlate(true);
		armorStand.setSmall(true);
		if (customName != null) {
			armorStand.setCustomName(new TextComponent(StringUtils.colorize(customName)));
			armorStand.setCustomNameVisible(true);
		}

		ClientboundAddEntityPacket spawnArmorStand = new ClientboundAddEntityPacket(armorStand, getObjectId(armorStand));
		ClientboundSetEntityDataPacket rawMetadataPacket = new ClientboundSetEntityDataPacket(armorStand.getId(), armorStand.getEntityData(), true);
		ClientboundSetEquipmentPacket rawEquipmentPacket = new ClientboundSetEquipmentPacket(armorStand.getId(), getEquipmentList());

		sendPacket(player, spawnArmorStand, rawMetadataPacket, rawEquipmentPacket);
		return armorStand;
	}


	/*
	public static void addNPCPacket(ServerPlayer npc, org.bukkit.entity.Player player) {
		PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
		connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc)); // "Adds the player data for the client to use when spawning a player" - https://wiki.vg/Protocol#Spawn_Player
		connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc)); // Spawns the NPC for the player client.
		connection.sendPacket(new ClientboundRotateHeadPacket(npc, (byte) (npc.yaw * 256 / 360))); // Correct head rotation when spawned in player look direction.
	}
	 */

	// Slime
	public static Slime spawnSlime(org.bukkit.entity.Player player, Location location, int size, boolean invisible, boolean glowing) {
		ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

		Slime slime = new Slime(EntityType.SLIME, nmsPlayer.getLevel());
		slime.moveTo(location.getBlockX(), location.getBlockY(), location.getBlockZ(), 0, 0);
		slime.setSize(size, true);
		slime.setInvisible(invisible);
		slime.getBukkitEntity().setGlowing(glowing);
		slime.setNoGravity(true);
		slime.setPersistenceRequired();

		ClientboundAddEntityPacket rawSpawnPacket = new ClientboundAddEntityPacket(slime, getObjectId(slime));
		ClientboundSetEntityDataPacket rawMetadataPacket = new ClientboundSetEntityDataPacket(slime.getId(), slime.getEntityData(), true);

		sendPacket(player, rawSpawnPacket, rawMetadataPacket);
		return slime;
	}

	// Falling Block
	// needs more testing, seemed to only spawn an iron ore block
	public static FallingBlockEntity spawnFallingBlock(@NonNull HasPlayer player, @NonNull Location location, Block block) {
		ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
		BlockState blockData = ((CraftBlockData) block.getBlockData()).getState();

		FallingBlockEntity fallingBlock = new FallingBlockEntity(nmsPlayer.getLevel(), location.getX(), location.getY(), location.getZ(), blockData);
		fallingBlock.setInvulnerable(true);
		fallingBlock.setNoGravity(true);
		fallingBlock.setInvisible(true);
		fallingBlock.getBukkitEntity().setGlowing(true);
		fallingBlock.getBukkitEntity().setVelocity(new Vector(0, 0, 0));

		ClientboundAddEntityPacket rawSpawnPacket = new ClientboundAddEntityPacket(fallingBlock, getObjectId(fallingBlock));
		ClientboundSetEntityDataPacket rawMetadataPacket = new ClientboundSetEntityDataPacket(fallingBlock.getId(), fallingBlock.getEntityData(), true);

		sendPacket(player, rawSpawnPacket, rawMetadataPacket);
		return fallingBlock;
	}

	// Item Frame
	public static ItemFrame spawnItemFrame(@NonNull HasPlayer player, @NonNull Location location, BlockFace blockFace, ItemStack content, int rotation, boolean makeSound, boolean invisible) {
		if (content == null) content = new ItemStack(Material.AIR);
		if (blockFace == null) blockFace = BlockFace.NORTH;

		Direction direction = CraftBlock.blockFaceToNotch(blockFace);
		ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
		ItemFrame itemFrame = new ItemFrame(EntityType.ITEM_FRAME, nmsPlayer.getLevel());
		itemFrame.moveTo(location.getBlockX(), location.getBlockY(), location.getBlockZ(), 0, 0);
		itemFrame.setItem(CraftItemStack.asNMSCopy(content), true, makeSound);
		itemFrame.setDirection(direction);
		itemFrame.setInvisible(invisible);
		itemFrame.setRotation(rotation);

//		ClientboundAddEntityPacket rawSpawnPacket = new ClientboundAddEntityPacket(itemFrame, getObjectId(itemFrame));
		ClientboundAddEntityPacket rawSpawnPacket = (ClientboundAddEntityPacket) itemFrame.getAddEntityPacket();
		ClientboundSetEntityDataPacket rawMetadataPacket = new ClientboundSetEntityDataPacket(itemFrame.getId(), itemFrame.getEntityData(), true);

		sendPacket(player, rawSpawnPacket, rawMetadataPacket);
		return itemFrame;
	}

	public static void updateItemFrame(@NonNull HasPlayer player, @NonNull org.bukkit.entity.ItemFrame entity, ItemStack content, int rotation) {
		if (content == null) content = new ItemStack(Material.AIR);

		ItemFrame itemFrame = ((CraftItemFrame) entity).getHandle();

		SynchedEntityData dataWatcher = itemFrame.getEntityData();
		dataWatcher.set(EntityDataSerializers.ITEM_STACK.createAccessor(7), CraftItemStack.asNMSCopy(content));
		dataWatcher.set(EntityDataSerializers.INT.createAccessor(8), rotation != -1 ? rotation : itemFrame.getRotation());

		ClientboundSetEntityDataPacket rawMetadataPacket = new ClientboundSetEntityDataPacket(
				itemFrame.getId(), itemFrame.getEntityData(), true);

		sendPacket(player, rawMetadataPacket);
	}

	// Armor Stand -- TODO: Needs to be turned into a builder

	public static ArmorStand spawnArmorStand(HasPlayer player, Location location, boolean invisible) {
		return spawnArmorStand(player, location, invisible, null);
	}

	public static ArmorStand spawnArmorStand(HasPlayer player, Location location, boolean invisible, String customName) {
		return spawnArmorStand(player, location, null, invisible, customName);
	}

	public static ArmorStand spawnArmorStand(HasPlayer player, Location location,
												   List<Pair<EquipmentSlot, net.minecraft.world.item.ItemStack>> equipment,
												   boolean invisible) {
		return spawnArmorStand(player, location, equipment, invisible, null);
	}

	public static ArmorStand spawnArmorStand(HasPlayer player, Location location,
												   List<Pair<EquipmentSlot, net.minecraft.world.item.ItemStack>> equipment,
												   boolean invisible, String customName) {
		if (equipment == null) equipment = getEquipmentList();

		ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
		ArmorStand armorStand = new ArmorStand(EntityType.ARMOR_STAND, nmsPlayer.getLevel());
		armorStand.moveTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		armorStand.setInvisible(invisible);
		if (customName != null) {
			armorStand.setCustomName(new TextComponent(customName));
			armorStand.setCustomNameVisible(true);
		}

		ClientboundAddEntityPacket rawSpawnPacket = new ClientboundAddEntityPacket(armorStand, getObjectId(armorStand));
		ClientboundSetEntityDataPacket rawMetadataPacket = new ClientboundSetEntityDataPacket(armorStand.getId(), armorStand.getEntityData(), true);
		ClientboundSetEquipmentPacket rawEquipmentPacket = new ClientboundSetEquipmentPacket(armorStand.getId(), equipment);

		sendPacket(player, rawSpawnPacket, rawMetadataPacket, rawEquipmentPacket);
		return armorStand;
	}

	public static ArmorStand spawnBeaconArmorStand(org.bukkit.entity.Player player, Location location) {
		ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

		ArmorStand armorStand = new ArmorStand(EntityType.ARMOR_STAND, nmsPlayer.getLevel());
		location = location.toCenterLocation();
		armorStand.moveTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		armorStand.setInvisible(true);
		armorStand.setNoBasePlate(true);
		armorStand.setSmall(true);
		armorStand.getBukkitEntity().setGlowing(true);

		ClientboundAddEntityPacket rawSpawnPacket = new ClientboundAddEntityPacket(armorStand, getObjectId(armorStand));
		ClientboundSetEntityDataPacket rawMetadataPacket = new ClientboundSetEntityDataPacket(armorStand.getId(), armorStand.getEntityData(), true);
		ClientboundSetEquipmentPacket rawEquipmentPacket = new ClientboundSetEquipmentPacket(armorStand.getId(), getEquipmentList());

		sendPacket(player, rawSpawnPacket, rawMetadataPacket, rawEquipmentPacket);

		return armorStand;
	}


	public static void updateArmorStandArmor(HasPlayer player, org.bukkit.entity.ArmorStand entity, List<Pair<EquipmentSlot, net.minecraft.world.item.ItemStack>> equipment) {
		if (equipment == null) equipment = getEquipmentList(null, null, null, null);
		ArmorStand armorStand = ((CraftArmorStand) entity).getHandle();

		ClientboundSetEquipmentPacket rawEquipmentPacket = new ClientboundSetEquipmentPacket(armorStand.getId(), equipment);

		sendPacket(player, rawEquipmentPacket);
	}


	public static List<Pair<EquipmentSlot, net.minecraft.world.item.ItemStack>> getEquipmentList() {
		return getEquipmentList(null, null, null, null);
	}

	public static List<Pair<EquipmentSlot, net.minecraft.world.item.ItemStack>> getEquipmentList(ItemStack head, ItemStack chest, ItemStack legs, ItemStack feet) {
		List<Pair<EquipmentSlot, net.minecraft.world.item.ItemStack>> equipmentList = new ArrayList<>();
		equipmentList.add(new Pair<>(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(head)));
		equipmentList.add(new Pair<>(EquipmentSlot.CHEST, CraftItemStack.asNMSCopy(chest)));
		equipmentList.add(new Pair<>(EquipmentSlot.LEGS, CraftItemStack.asNMSCopy(legs)));
		equipmentList.add(new Pair<>(EquipmentSlot.FEET, CraftItemStack.asNMSCopy(feet)));
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

	public static byte encodeAngle(float angle) {
		return (byte) (angle * 256f / 360f);
	}

	private static int encodeVelocity(double v) {
		return (int) (v * 8000D);
	}

	private static short encodePosition(double d) {
		return (short) (d * 4096D);
	}

	public static Integer getObjectId(org.bukkit.entity.EntityType entity) {
		if (entity == null)
			return null;
		return Bukkit.getUnsafe().entityID(entity);
	}

	public static Integer getObjectId(org.bukkit.entity.Entity entity) {
		if (entity == null)
			return null;
		return getObjectId(entity.getType());
	}

	public static Integer getObjectId(Entity entity) {
		if (entity == null)
			return null;
		return getObjectId(entity.getBukkitEntity());
	}
}

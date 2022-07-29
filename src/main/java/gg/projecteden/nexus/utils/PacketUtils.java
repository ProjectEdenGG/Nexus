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
import io.papermc.paper.adventure.AdventureComponent;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.core.Direction;
import net.minecraft.core.Rotations;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_19_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftItemFrame;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@UtilityClass
public class PacketUtils {

	@NotNull
	public net.minecraft.world.entity.EquipmentSlot toNMS(EquipmentSlot slot) {
		return net.minecraft.world.entity.EquipmentSlot.values()[slot.ordinal()];
	}

	@NotNull
	public net.minecraft.world.item.ItemStack toNMS(ItemStack item) {
		return CraftItemStack.asNMSCopy(item);
	}

	@NotNull
	public ServerLevel toNMS(World world) {
		return ((CraftWorld) world).getHandle().getLevel();
	}

	@NotNull
	public Rotations toNMS(EulerAngle angle) {
		final Function<Double, Float> toDegrees = value -> (float) Math.toDegrees(value);
		return Rotations.createWithoutValidityChecks(toDegrees.apply(angle.getX()), toDegrees.apply(angle.getY()), toDegrees.apply(angle.getZ()));
	}

	@NotNull
	public Direction toNMS(BlockFace blockFace) {
		return CraftBlock.blockFaceToNotch(blockFace);
	}

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
		ClientboundMoveEntityPacket.Rot lookPacket = new ClientboundMoveEntityPacket.Rot(entity.getId(), (byte) (yaw * 256 / 360), (byte) (pitch * 256 / 360), true);
		sendPacket(player, headRotationPacket, lookPacket);
	}

	/**
	 * Gets the slot int corresponding to an
	 * {@link net.minecraft.world.entity.EquipmentSlot EquipmentSlot}.
	 * Returns -1 for {@link net.minecraft.world.entity.EquipmentSlot#MAINHAND MAINHAND}.
	 *
	 * @param slot an item slot
	 * @return integer slot
	 */
	public int getSlotInt(net.minecraft.world.entity.EquipmentSlot slot) {
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
	 *
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
	 * Gets the {@link net.minecraft.world.entity.EquipmentSlot } corresponding to an {@link EnumWrappers.ItemSlot}.
	 *
	 * @param slot an item slot
	 * @return enum item slot
	 */
	public net.minecraft.world.entity.EquipmentSlot getEnumItemSlot(EnumWrappers.ItemSlot slot) {
		return switch (slot) {
			case MAINHAND -> net.minecraft.world.entity.EquipmentSlot.MAINHAND;
			case OFFHAND -> net.minecraft.world.entity.EquipmentSlot.OFFHAND;
			case FEET -> net.minecraft.world.entity.EquipmentSlot.FEET;
			case LEGS -> net.minecraft.world.entity.EquipmentSlot.LEGS;
			case CHEST -> net.minecraft.world.entity.EquipmentSlot.CHEST;
			case HEAD -> net.minecraft.world.entity.EquipmentSlot.HEAD;
		};
	}

	/**
	 * Gets the {@link EnumWrappers.ItemSlot} corresponding to an {@link EquipmentSlot}.
	 *
	 * @param slot an item slot
	 * @return protocol item slot
	 */
	public EnumWrappers.ItemSlot getItemSlot(EquipmentSlot slot) {
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
	 *
	 * @param owner player to "give" the item
	 * @param recipients packet recipients
	 * @param item item to "give"
	 * @param slot slot to "set"
	 */
	public void sendFakeItem(org.bukkit.entity.Entity owner, Collection<? extends HasPlayer> recipients, ItemStack item, net.minecraft.world.entity.EquipmentSlot slot) {
		// self packet avoids playing the armor equip sound effect
		PacketContainer selfPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SET_SLOT);
		selfPacket.getIntegers().write(0, 0); // inventory ID (0 = player)
		int slotInt = getSlotInt(slot);
		if (slotInt == -1 && owner instanceof HumanEntity player)
			slotInt = player.getInventory().getHeldItemSlot() + 36;
		selfPacket.getIntegers().write(2, slotInt);
		selfPacket.getItemModifier().write(0, item);

		// other packet is sent to all other players to show the armor piece
		List<Pair<net.minecraft.world.entity.EquipmentSlot, net.minecraft.world.item.ItemStack>> equipmentList = new ArrayList<>();
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
	 *
	 * @param owner player to "give" the item
	 * @param recipient packet recipient
	 * @param item item to "give"
	 * @param slot slot to "set"
	 */
	public void sendFakeItem(org.bukkit.entity.Entity owner, HasPlayer recipient, ItemStack item, net.minecraft.world.entity.EquipmentSlot slot) {
		sendFakeItem(owner, Collections.singletonList(recipient), item, slot);
	}

	/**
	 * Sends fake packets for an armor piece or main/off-hand item for a player.
	 * <p>
	 * To avoid sending a packet to the item owner, remove them from <code>recipients</code>.
	 *
	 * @param owner player to "give" the item
	 * @param recipients packet recipients
	 * @param item item to "give"
	 * @param slot slot to "set"
	 */
	public void sendFakeItem(org.bukkit.entity.Entity owner, Collection<? extends HasPlayer> recipients, ItemStack item, EnumWrappers.ItemSlot slot) {
		sendFakeItem(owner, recipients, item, getEnumItemSlot(slot));
	}

	/**
	 * Sends a fake packet for an armor piece or main/off-hand item for a player.
	 * <p>
	 * To avoid sending a packet to the item owner, remove them from <code>recipients</code>.
	 *
	 * @param owner player to "give" the item
	 * @param recipient packet recipient
	 * @param item item to "give"
	 * @param slot slot to "set"
	 */
	public void sendFakeItem(org.bukkit.entity.Entity owner, HasPlayer recipient, ItemStack item, EnumWrappers.ItemSlot slot) {
		sendFakeItem(owner, Collections.singletonList(recipient), item, slot);
	}

	/**
	 * Sends fake packets for an armor piece or main/off-hand item for a player.
	 * <p>
	 * To avoid sending a packet to the item owner, remove them from <code>recipients</code>.
	 *
	 * @param owner player to "give" the item
	 * @param recipients packet recipients
	 * @param item item to "give"
	 * @param slot slot to "set"
	 */
	public void sendFakeItem(org.bukkit.entity.Entity owner, Collection<? extends HasPlayer> recipients, ItemStack item, EquipmentSlot slot) {
		sendFakeItem(owner, recipients, item, getItemSlot(slot));
	}

	/**
	 * Sends a fake packet for an armor piece or main/off-hand item for a player.
	 * <p>
	 * To avoid sending a packet to the item owner, remove them from <code>recipients</code>.
	 *
	 * @param owner player to "give" the item
	 * @param recipient packet recipient
	 * @param item item to "give"
	 * @param slot slot to "set"
	 */
	public void sendFakeItem(org.bukkit.entity.Entity owner, HasPlayer recipient, ItemStack item, EquipmentSlot slot) {
		sendFakeItem(owner, Collections.singletonList(recipient), item, slot);
	}

	// untested
	public static void entityRelativeMove(@NonNull HasPlayer player, @NonNull org.bukkit.entity.Entity bukkitEntity, Vector delta, boolean onGround) {
		Entity entity = ((CraftEntity) bukkitEntity).getHandle();
		ClientboundMoveEntityPacket.Pos movePacket = new ClientboundMoveEntityPacket.Pos(entity.getId(),
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
//		entityPlayer.setCustomName(new ChatComponentText(name));
//		PacketPlayOutEntityMetadata entityMetadataPacket = new PacketPlayOutEntityMetadata();

//		DataWatcher dataWatcher = entityPlayer.getDataWatcher();
//		dataWatcher.set(DataWatcherRegistry. );

//		entityMetadataPacket
	}

	public static List<net.minecraft.world.entity.decoration.ArmorStand> entityNameFake(@NonNull HasPlayer player, org.bukkit.entity.Entity bukkitEntity, String... customNames) {
		return entityNameFake(player, bukkitEntity, 0.3, customNames);
	}

	public static net.minecraft.world.entity.decoration.ArmorStand entityNameFake(@NonNull HasPlayer player, org.bukkit.entity.Entity bukkitEntity, double distance, String customName) {
		return entityNameFake(player, bukkitEntity, distance, customName, 0);
	}

	public static List<net.minecraft.world.entity.decoration.ArmorStand> entityNameFake(@NonNull HasPlayer player, org.bukkit.entity.Entity bukkitEntity, double distance, String... customNames) {
		int index = 0;
		List<net.minecraft.world.entity.decoration.ArmorStand> armorStands = new ArrayList<>();
		for (String customName : customNames)
			armorStands.add(entityNameFake(player, bukkitEntity, distance, customName, index++));

		if (armorStands.isEmpty())
			armorStands = null;

		return armorStands;
	}

	public static net.minecraft.world.entity.decoration.ArmorStand entityNameFake(@NonNull HasPlayer player, org.bukkit.entity.Entity bukkitEntity, String customName) {
		return entityNameFake(player, bukkitEntity, customName, 0);
	}

	public static net.minecraft.world.entity.decoration.ArmorStand entityNameFake(@NonNull HasPlayer player, org.bukkit.entity.Entity bukkitEntity, String customName, int index) {
		return entityNameFake(player, bukkitEntity, 0.3, customName, index);
	}

	public static net.minecraft.world.entity.decoration.ArmorStand entityNameFake(@NonNull HasPlayer player, org.bukkit.entity.Entity bukkitEntity, double distance, String customName, int index) {
		ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
		net.minecraft.world.entity.decoration.ArmorStand armorStand = new net.minecraft.world.entity.decoration.ArmorStand(net.minecraft.world.entity.EntityType.ARMOR_STAND, nmsPlayer.getLevel());
		Location loc = bukkitEntity.getLocation();
		double y = loc.getY() + (distance * index);
		if (bukkitEntity instanceof Player)
			y += 1.8;

		armorStand.moveTo(loc.getX(), y, loc.getZ(), 0, 0);
		armorStand.setMarker(true);
		armorStand.setInvisible(true);
		armorStand.setNoBasePlate(true);
		armorStand.setSmall(true);
		if (customName != null) {
			armorStand.setCustomName(new AdventureComponent(new JsonBuilder(customName).build()));
			armorStand.setCustomNameVisible(true);
		}

		ClientboundAddEntityPacket spawnArmorStand = new ClientboundAddEntityPacket(armorStand, getObjectId(armorStand));
		ClientboundSetEntityDataPacket rawMetadataPacket = new ClientboundSetEntityDataPacket(armorStand.getId(), armorStand.getEntityData(), true);
		ClientboundSetEquipmentPacket rawEquipmentPacket = new ClientboundSetEquipmentPacket(armorStand.getId(), getEquipmentList());

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

	// Slime
	public static Slime spawnSlime(Player player, Location location, int size, boolean invisible, boolean glowing) {
		ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

		Slime slime = new Slime(net.minecraft.world.entity.EntityType.SLIME, nmsPlayer.getLevel());
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

	public static void updateItemFrame(@NonNull HasPlayer player, @NonNull ItemFrame entity, ItemStack content, int rotation) {
		if (content == null) content = new ItemStack(Material.AIR);

		net.minecraft.world.entity.decoration.ItemFrame itemFrame = ((CraftItemFrame) entity).getHandle();

		SynchedEntityData dataWatcher = itemFrame.getEntityData();
		dataWatcher.set(EntityDataSerializers.ITEM_STACK.createAccessor(7), CraftItemStack.asNMSCopy(content));
		dataWatcher.set(EntityDataSerializers.INT.createAccessor(8), rotation != -1 ? rotation : itemFrame.getRotation());

		ClientboundSetEntityDataPacket rawMetadataPacket = new ClientboundSetEntityDataPacket(
				itemFrame.getId(), itemFrame.getEntityData(), true);

		sendPacket(player, rawMetadataPacket);
	}

	// Armor Stand

	public static void updateArmorStandArmor(HasPlayer player, ArmorStand entity, List<Pair<net.minecraft.world.entity.EquipmentSlot, net.minecraft.world.item.ItemStack>> equipment) {
		if (equipment == null) equipment = getEquipmentList();
		net.minecraft.world.entity.decoration.ArmorStand armorStand = ((CraftArmorStand) entity).getHandle();

		ClientboundSetEquipmentPacket rawEquipmentPacket = new ClientboundSetEquipmentPacket(armorStand.getId(), equipment);

		sendPacket(player, rawEquipmentPacket);
	}

	public static List<Pair<net.minecraft.world.entity.EquipmentSlot, net.minecraft.world.item.ItemStack>> getEquipmentList() {
		return getEquipmentList(null, null, null, null);
	}

	public static List<Pair<net.minecraft.world.entity.EquipmentSlot, net.minecraft.world.item.ItemStack>> getEquipmentList(ItemStack head, ItemStack chest, ItemStack legs, ItemStack feet) {
		List<Pair<net.minecraft.world.entity.EquipmentSlot, net.minecraft.world.item.ItemStack>> equipmentList = new ArrayList<>();
		equipmentList.add(new Pair<>(net.minecraft.world.entity.EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(head)));
		equipmentList.add(new Pair<>(net.minecraft.world.entity.EquipmentSlot.CHEST, CraftItemStack.asNMSCopy(chest)));
		equipmentList.add(new Pair<>(net.minecraft.world.entity.EquipmentSlot.LEGS, CraftItemStack.asNMSCopy(legs)));
		equipmentList.add(new Pair<>(net.minecraft.world.entity.EquipmentSlot.FEET, CraftItemStack.asNMSCopy(feet)));
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

	public static Integer getObjectId(EntityType entity) {
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

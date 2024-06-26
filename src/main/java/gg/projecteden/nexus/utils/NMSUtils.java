package gg.projecteden.nexus.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlocks.SoundAction;
import gg.projecteden.parchment.HasLocation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.SneakyThrows;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Rotations;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.entity.ExperienceOrb.SpawnReason;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class NMSUtils {

	public static MinecraftServer getServer() {
		return ((CraftServer) Bukkit.getServer()).getServer();
	}

	public static ServerLevel toNMS(org.bukkit.World bukkitWorld) {
		return ((CraftWorld) bukkitWorld).getHandle();
	}

	public static BlockPos toNMS(HasLocation hasLocation) {
		final Location location = hasLocation.getLocation();
		return new BlockPos((int) location.getX(), (int) location.getY(), (int) location.getZ());
	}

	public static Location fromNMS(World world, BlockPos pos) {
		return new Location(world, pos.getX(), pos.getY(), pos.getZ());
	}

	public static Block toNMS(org.bukkit.block.Block block) {
		return ((CraftBlock) block).getNMS().getBlock();
	}

	public static BlockState toNMS(BlockData blockData) {
		return ((CraftBlockData) blockData).getState();
	}

	@NotNull
	public static Direction toNMS(BlockFace blockFace) {
		return CraftBlock.blockFaceToNotch(blockFace);
	}

	public static AABB toNMS(BoundingBox box) {
		return new AABB(box.getMinX(), box.getMinY(), box.getMinZ(), box.getMaxX(), box.getMaxY(), box.getMaxZ());
	}

	public static BoundingBox fromNMS(AABB box) {
		return new BoundingBox(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
	}

	public static EntityType<?> toNMS(org.bukkit.entity.EntityType type) {
		return net.minecraft.world.entity.EntityType.byString(type.getName()).get();
	}

	public static ItemStack toNMS(org.bukkit.inventory.ItemStack itemStack) {
		return CraftItemStack.asNMSCopy(itemStack);
	}

	@NotNull
	public static Rotations toNMS(EulerAngle angle) {
		final Function<Double, Float> toDegrees = value -> (float) Math.toDegrees(value);
		return Rotations.createWithoutValidityChecks(toDegrees.apply(angle.getX()), toDegrees.apply(angle.getY()), toDegrees.apply(angle.getZ()));
	}

	@NotNull
	public static net.minecraft.world.entity.EquipmentSlot toNMS(org.bukkit.inventory.EquipmentSlot slot) {
		return net.minecraft.world.entity.EquipmentSlot.values()[slot.ordinal()];
	}

	public static GameProfile getGameProfile(Player player) {
		ServerPlayer entityPlayer = ((CraftPlayer) player).getHandle();
		return entityPlayer.getBukkitEntity().getProfile();
	}

	public static DamageSources getDamageSources(Player player) {
		return getDamageSources(player.getWorld());
	}

	public static DamageSources getDamageSources(World world) {
		return toNMS(world).damageSources();
	}

	public static void hurtPlayer(Player player, DamageSource damageSource, float damage) {
		ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
		nmsPlayer.hurt(damageSource, damage);
	}
	
	@Data
	@AllArgsConstructor
	public static class Property {
		private String name;
		private String value;
		private String signature;

		public com.mojang.authlib.properties.Property toNMS() {
			return new com.mojang.authlib.properties.Property(name, value, signature);
		}

		@SneakyThrows
		public static Property fromNMS(com.mojang.authlib.properties.Property property) {
			var nameField = property.getClass().getDeclaredField("name");
			var valueField = property.getClass().getDeclaredField("value");
			var signatureField = property.getClass().getDeclaredField("signature");
			nameField.setAccessible(true);
			valueField.setAccessible(true);
			signatureField.setAccessible(true);
			return new Property((String) nameField.get(property), (String) valueField.get(property), (String) signatureField.get(property));
		}
	}

	@SneakyThrows
	public static Property getSkinProperty(Player player) {
		return Property.fromNMS(getGameProfile(player).getProperties().get("textures").iterator().next());
	}

	public static boolean setBlockDataAt(BlockData blockData, Location location, boolean doPhysics) {
		ServerLevel world = toNMS(location.getWorld());
		BlockPos blockPosition = toNMS(location);
		BlockState iBlockData = toNMS(blockData);

		return world.setBlock(blockPosition, iBlockData, doPhysics ? 3 : 2);
	}

	public static void applyPhysics(org.bukkit.block.Block block) {
		applyPhysics(block.getLocation());
	}

	public static void applyPhysics(Location location) {
		ServerLevel world = toNMS(location.getWorld());
		BlockPos position = toNMS(location);
		Block nmsBlock = toNMS(location.getBlock());

		world.updateNeighborsAt(position, nmsBlock);
	}

	public static ArmorStand createHologram(Level world) {
		ArmorStand armorStand = new ArmorStand(EntityType.ARMOR_STAND, world);
		armorStand.setMarker(true);
		armorStand.setInvisible(true);
		armorStand.setNoBasePlate(true);
		armorStand.setSmall(true);
		armorStand.setCustomNameVisible(true);

		return armorStand;
	}

	public static @Nullable Sound getSound(SoundAction soundAction, org.bukkit.block.Block block) {
		try {
			Block nmsBlock = toNMS(block);
			SoundType soundEffectType = nmsBlock.getSoundType(toNMS(block.getBlockData()));
			SoundEvent nmsSound = switch (soundAction) {
				case BREAK -> soundEffectType.getBreakSound();
				case STEP -> soundEffectType.getStepSound();
				case PLACE -> soundEffectType.getPlaceSound();
				case HIT -> soundEffectType.getHitSound();
				case FALL -> soundEffectType.getFallSound();
			};

			ResourceLocation nmsString = nmsSound.getLocation();
			String soundString = nmsString.getPath().replace(".", "_").toUpperCase();
			return Sound.valueOf(soundString);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	public static float getDestroySpeed(org.bukkit.block.Block block, org.bukkit.inventory.ItemStack itemStack) {
		try {
			ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
			Item item = nmsItemStack.getItem();
			return item.getDestroySpeed(nmsItemStack, NMSUtils.toNMS(block.getBlockData()));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return 1;
	}

	public static ServerPlayer createServerPlayer(UUID uuid, @NonNull Location location, String name) {
		if (uuid == null)
			uuid = UUID.randomUUID();

		ServerLevel world = NMSUtils.toNMS(location.getWorld());
		GameProfile gameProfile = new GameProfile(uuid, name);
		ServerPlayer serverPlayer = new ServerPlayer(NMSUtils.getServer(), world, gameProfile, ClientInformation.createDefault());

		teleport(serverPlayer, location);

		return serverPlayer;
	}

	public static void teleport(Entity entity, Location location) {
		entity.moveTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
	}

	public static void awardExperience(@NonNull Player player, Location location, int experience, @NonNull SpawnReason spawnReason) {
		Vec3 pos = Vec3.atCenterOf(toNMS(location));
		ServerLevel level = toNMS(player.getWorld());
		net.minecraft.world.entity.ExperienceOrb.award(level, pos, experience, spawnReason, (Entity) player);
	}

	public static List<Pair<EquipmentSlot, ItemStack>> getArmorEquipmentList() {
		return getArmorEquipmentList(null, null, null, null);
	}

	public static List<Pair<EquipmentSlot, ItemStack>> getHandEquipmentList() {
		return getHandEquipmentList(null, null);
	}

	public static List<Pair<EquipmentSlot, ItemStack>> getAllEquipmentList() {
		return getAllEquipmentList(null, null, null, null, null, null);
	}

	public static List<Pair<EquipmentSlot, ItemStack>> getArmorEquipmentList(
		org.bukkit.inventory.ItemStack head,
		org.bukkit.inventory.ItemStack chest,
		org.bukkit.inventory.ItemStack legs,
		org.bukkit.inventory.ItemStack feet) {

		List<Pair<EquipmentSlot, ItemStack>> equipmentList = new ArrayList<>();
		equipmentList.add(new Pair<>(EquipmentSlot.HEAD, toNMS(head)));
		equipmentList.add(new Pair<>(EquipmentSlot.CHEST, toNMS(chest)));
		equipmentList.add(new Pair<>(EquipmentSlot.LEGS, toNMS(legs)));
		equipmentList.add(new Pair<>(EquipmentSlot.FEET, toNMS(feet)));
		return equipmentList;
	}

	public static List<Pair<EquipmentSlot, ItemStack>> getHandEquipmentList(
		org.bukkit.inventory.ItemStack mainHand,
		org.bukkit.inventory.ItemStack offHand) {


		List<Pair<EquipmentSlot, ItemStack>> equipmentList = new ArrayList<>();
		equipmentList.add(new Pair<>(EquipmentSlot.MAINHAND, toNMS(mainHand)));
		equipmentList.add(new Pair<>(EquipmentSlot.OFFHAND, toNMS(offHand)));
		return equipmentList;
	}

	public static List<Pair<EquipmentSlot, ItemStack>> getAllEquipmentList(
		org.bukkit.inventory.ItemStack head,
		org.bukkit.inventory.ItemStack chest,
		org.bukkit.inventory.ItemStack legs,
		org.bukkit.inventory.ItemStack feet,
		org.bukkit.inventory.ItemStack mainHand,
		org.bukkit.inventory.ItemStack offHand) {

		List<Pair<EquipmentSlot, ItemStack>> both = new ArrayList<>();
		both.addAll(getArmorEquipmentList(head, chest, legs, feet));
		both.addAll(getHandEquipmentList(mainHand, offHand));

		return both;
	}

	public static void setStaticFinal(Field field, Object newValue) throws Exception {
		var unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
		unsafeField.setAccessible(true);
		final Unsafe unsafe = (Unsafe) unsafeField.get(null);
		var offset = unsafe.staticFieldOffset(field);
		unsafe.putObject(unsafe.staticFieldBase(field), offset, newValue);
	}

}

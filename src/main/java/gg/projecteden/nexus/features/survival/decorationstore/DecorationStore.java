package gg.projecteden.nexus.features.survival.decorationstore;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationListener;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.catalog.Catalog;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.survival.Survival;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.NMSUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PacketUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.entity.EquipmentSlot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Rotatable;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gg.projecteden.nexus.features.resourcepack.decoration.DecorationInteractData.MAX_RADIUS;

public class DecorationStore implements Listener {

	@Getter
	@Setter
	private static boolean active = true;
	private static final Map<Player, Pair<Entity, Location>> targetBuyablesMap = new HashMap<>();

	private static final List<EntityType> glowTypes = List.of(EntityType.ITEM_FRAME, EntityType.ARMOR_STAND, EntityType.PAINTING);
	private static final int REACH_DISTANCE = 5;


	public DecorationStore() {
		Nexus.registerListener(this);

		new Catalog();
		new DecorationListener();

		glowingBuyables();
	}

	// TODO: Swap schematics
	public static void refresh() {
		active = false;

		for (Player player : targetBuyablesMap.keySet()) {
			Pair<Entity, Location> buyablePair = targetBuyablesMap.remove(player);
			if (buyablePair != null)
				unglowEntity(player, buyablePair.getFirst());
		}

		targetBuyablesMap.clear();

		active = true;
	}

	public static @Nullable ItemStack getTargetedBuyable(Player player) {
		Pair<Entity, Location> buyablePair = targetBuyablesMap.getOrDefault(player, new Pair<>(null, null));
		if (buyablePair == null || buyablePair.getFirst() == null)
			return null;

		Entity entity = buyablePair.getFirst();
		if (entity == null)
			return null;

		if (entity instanceof Painting)
			return new ItemStack(Material.PAINTING);

		if (entity instanceof ItemFrame itemFrame)
			return itemFrame.getItem();

		if (entity instanceof ArmorStand armorStand) {
			ItemStack handItem = armorStand.getItem(org.bukkit.inventory.EquipmentSlot.HAND);
			if (Nullables.isNotNullOrAir(handItem) && handItem.getType().equals(Material.PLAYER_HEAD))
				return handItem;

			handItem = armorStand.getItem(org.bukkit.inventory.EquipmentSlot.OFF_HAND);
			if (Nullables.isNotNullOrAir(handItem) && handItem.getType().equals(Material.PLAYER_HEAD))
				return handItem;

			return new ItemStack(Material.ARMOR_STAND);
		}

		return null;
	}

	/*
		TODO:
			- glowing on multiblock paintings is taking light blocks into account
			- glow heads are a little glitchy when ontop of decoration
			- player wall skulls need to be properly setup
	 */
	private void glowingBuyables() {
		Tasks.repeat(0, TickTime.TICK.x(1), () -> {
			if (!active)
				return;

			List<Player> players = (List<Player>) Survival.worldguard().getPlayersInRegion(DecorationStoreUtils.storeRegion);
			for (Player player : players) {
				Block targetBlock = player.getTargetBlockExact(REACH_DISTANCE);
				Pair<Entity, Location> buyablePair = null;
				if (!Nullables.isNullOrAir(targetBlock)) {
					buyablePair = glowSkull(player, targetBlock);
				}

				if (buyablePair == null) {
					Entity targetEntity = getTargetEntity(player);
					if (targetEntity != null) {
						buyablePair = glowEntity(player, targetEntity);
					}
				}

				if (buyablePair != null)
					targetBuyablesMap.put(player, buyablePair);
			}

		});
	}

	private Pair<Entity, Location> glowSkull(Player player, Block block) {
		ItemStack blockItem = ItemUtils.getItem(block);

		Entity oldEntity = targetBuyablesMap.getOrDefault(player, new Pair<>(null, null)).getFirst();
		Location oldLocation = targetBuyablesMap.getOrDefault(player, new Pair<>(null, null)).getSecond();

		if (Nullables.isNullOrAir(block) || !MaterialTag.PLAYER_SKULLS.isTagged(block) || blockItem == null) {
			unglowEntity(player, oldEntity);
			return null;
		}

		Location newLocation = block.getLocation();
		if (newLocation.equals(oldLocation))
			return null;

		// New armorstand
		unglowEntity(player, oldEntity);

		Location standLoc = newLocation.clone().add(0.5, -1.4, 0.5);
		if (block.getType().equals(Material.PLAYER_HEAD)) {
			Rotatable rotatable = (Rotatable) block.getBlockData();
			float yaw = getYaw(rotatable.getRotation());
			standLoc.setYaw(yaw);
		} else if (block.getType().equals(Material.PLAYER_WALL_HEAD)) {
			// TODO
			return null;
		}

		blockItem = new ItemBuilder(blockItem).modelId(2).build();

		List<Pair<EquipmentSlot, net.minecraft.world.item.ItemStack>> equipment = NMSUtils.getHandEquipmentList(blockItem, null);

		return new Pair<>(PacketUtils.spawnArmorStand(player, standLoc, equipment).getBukkitEntity(), newLocation);
	}

	private static Pair<Entity, Location> glowEntity(Player player, Entity newEntity) {
		Entity oldEntity = targetBuyablesMap.getOrDefault(player, new Pair<>(null, null)).getFirst();

		boolean removeGlowing = newEntity == null;
		if (newEntity != null) {
			if (oldEntity != null && oldEntity.getUniqueId() != newEntity.getUniqueId())
				removeGlowing = true;

			if (!glowTypes.contains(newEntity.getType()))
				removeGlowing = true;
		}

		if (removeGlowing) {
			unglowEntity(player, oldEntity);
			return null;
		}

		PacketUtils.glow(player, newEntity, true);
		return new Pair<>(newEntity, newEntity.getLocation());
	}

	private static void unglowEntity(Player player, Entity oldEntity) {
		if (oldEntity == null)
			return;

		targetBuyablesMap.remove(player);
		if (oldEntity instanceof ArmorStand armorStand) {
			ItemStack item = armorStand.getItem(org.bukkit.inventory.EquipmentSlot.HAND);
			if (Nullables.isNotNullOrAir(item) && item.getType().equals(Material.PLAYER_HEAD)) {
				PacketUtils.entityDestroy(player, oldEntity);
				return;
			}
		}

		PacketUtils.glow(player, oldEntity, false);
	}

	private Entity getTargetEntity(Player player) {
		Entity targetEntity = player.getTargetEntity(REACH_DISTANCE, false);
		if (targetEntity != null)
			return targetEntity;

		targetEntity = PlayerUtils.getTargetItemFrame(player, 10, Map.of(BlockFace.UP, 1, BlockFace.DOWN, 1));
		if (targetEntity != null)
			return targetEntity;

		// Target Block
		Block block = player.getTargetBlockExact(REACH_DISTANCE);
		if (Nullables.isNotNullOrAir(block)) {
			ItemFrame itemFrame = DecorationUtils.getItemFrame(block, MAX_RADIUS, BlockFace.UP, player);
			if (itemFrame != null) {
				DecorationConfig config = DecorationConfig.of(itemFrame);
				if (config != null) {
					return itemFrame;
				}
			}
		}

		return null;
	}

	private float getYaw(BlockFace face) {
		int val = switch (face) {
			case SOUTH -> 0x0;
			case SOUTH_SOUTH_WEST -> 0x1;
			case SOUTH_WEST -> 0x2;
			case WEST_SOUTH_WEST -> 0x3;
			case WEST -> 0x4;
			case WEST_NORTH_WEST -> 0x5;
			case NORTH_WEST -> 0x6;
			case NORTH_NORTH_WEST -> 0x7;
			case NORTH -> 0x8;
			case NORTH_NORTH_EAST -> 0x9;
			case NORTH_EAST -> 0xA;
			case EAST_NORTH_EAST -> 0xB;
			case EAST -> 0xC;
			case EAST_SOUTH_EAST -> 0xD;
			case SOUTH_EAST -> 0xE;
			case SOUTH_SOUTH_EAST -> 0xF;
			default -> throw new IllegalArgumentException("Illegal rotation " + face);
		};

		return (float) (-180 + (22.5 * val));
	}

}

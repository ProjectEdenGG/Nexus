package gg.projecteden.nexus.features.survival.decorationstore;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.NMSUtils;
import gg.projecteden.nexus.utils.PacketUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.entity.EquipmentSlot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Rotatable;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

@Data
@RequiredArgsConstructor
public class TargetData {
	@NonNull UUID playerUUID;

	ItemStack targetItem;
	Entity currentEntity;
	Entity oldEntity;

	Location currentSkullLocation;

	public TargetData(Player player) {
		playerUUID = player.getUniqueId();
	}

	public void update(Entity newEntity) {
		oldEntity = currentEntity;
		currentEntity = newEntity;
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(playerUUID);
	}

	public void setupTargetHDB(@NonNull Block targetBlock, @NonNull ItemStack skullItem) {
		Location standLoc = targetBlock.getLocation().clone().add(0.5, -1.4, 0.5);
		switch (skullItem.getType()) {
			case PLAYER_HEAD -> standLoc.setYaw(getYaw(((Rotatable) targetBlock.getBlockData()).getRotation()));
			case PLAYER_WALL_HEAD -> {
				// TODO
				return;
			}
		}

		ItemStack standItem = new ItemBuilder(skullItem.clone()).modelId(2).build();
		List<Pair<EquipmentSlot, net.minecraft.world.item.ItemStack>> equipment = NMSUtils.getHandEquipmentList(standItem, null);

		ArmorStand armorStand = spawnArmorStand(standLoc, equipment);
		debug("target: skull " + armorStand.getType());
		update(armorStand);
		setCurrentSkullLocation(targetBlock.getLocation());

		targetItem = skullItem;
	}

	public void setupTargetEntity(@NonNull Entity entity) {
		debug("target: entity " + entity.getType());

		update(entity);

		if (entity instanceof Painting)
			targetItem = new ItemStack(Material.PAINTING);
		else if (entity instanceof ItemFrame itemFrame)
			targetItem = itemFrame.getItem();
	}

	public void glowCurrentEntity() {
		if (currentEntity == null)
			return;

		glowEntity(currentEntity, true);
	}

	public void unglowEntity(Entity entity) {
		debug("unglowEntity");
		if (entity == null) {
			debug(" entity == null");
			return;
		}
		debug(" unglowing entity");
		glowEntity(entity, false);

		if (entity instanceof ArmorStand) {
			PacketUtils.entityDestroy(getPlayer(), entity);
		}
	}

	private void glowEntity(Entity entity, boolean glowing) {
		PacketUtils.glow(getPlayer(), entity, glowing);

//		if (glowing)
//			GlowUtils.glow(entity).color(GlowColor.PURPLE).receivers(getPlayer()).run();
//		else
//			GlowUtils.unglow(entity).receivers(getPlayer()).run();
	}

	private static float getYaw(BlockFace face) {
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

	private ArmorStand spawnArmorStand(Location location, List<Pair<EquipmentSlot, net.minecraft.world.item.ItemStack>> equipment) {
		ArmorStand armorStand = (ArmorStand) PacketUtils
			.spawnArmorStand(getPlayer(), location, true, true, true, false, true, true)
			.getBukkitEntity();

		Tasks.wait(1, () -> PacketUtils.updateArmorStandArmor(getPlayer(), armorStand, equipment));

		return armorStand;
	}

	private void debug(String message) {
		DecorationStore.debug(getPlayer(), message);
	}
}

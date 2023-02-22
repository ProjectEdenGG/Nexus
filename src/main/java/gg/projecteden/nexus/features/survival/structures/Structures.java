package gg.projecteden.nexus.features.survival.structures;

import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

@NoArgsConstructor
public class Structures extends Feature implements Listener {
	private static final String region_buildadmin = "structures";

	List<Material> spawnEggTypes = new ArrayList<>() {{
		addAll(MaterialTag.SPAWN_EGGS.getValues());
		add(Material.END_CRYSTAL);
	}};

	List<Material> MUSHROOMS = List.of(Material.BROWN_MUSHROOM, Material.RED_MUSHROOM, Material.CRIMSON_FUNGUS, Material.WARPED_FUNGUS);

	@EventHandler
	public void onPlaceUnsafe(PlayerInteractEvent event) {
		ItemStack item = event.getItem();
		if (isNullOrAir(item)) return;
		Material type = item.getType();

		if (!MUSHROOMS.contains(type)) return;
		if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

		Block clickedBlock = event.getClickedBlock();
		if (isNullOrAir(clickedBlock)) return;
		if (!isInBuildRegion(event.getPlayer())) return;

		Block block = clickedBlock.getRelative(event.getBlockFace());
		if (!MaterialTag.ALL_AIR.isTagged(block)) return;

		block.setType(type, false);
		new SoundBuilder(Sound.BLOCK_GRASS_PLACE).location(block).volume(0.5).play();
	}

	@EventHandler
	public void onUseSpawnEgg(PlayerInteractEvent event) {
		ItemStack item = event.getItem();
		if (isNullOrAir(item)) return;
		if (!spawnEggTypes.contains(item.getType())) return;
		if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

		Block clickedBlock = event.getClickedBlock();
		if (isNullOrAir(clickedBlock)) return;
		if (clickedBlock.getType().equals(Material.SPAWNER)) return;
		if (!isInBuildRegion(event.getPlayer())) return;

		Block block = clickedBlock.getRelative(event.getBlockFace());
		boolean isAir = MaterialTag.ALL_AIR.isTagged(block);
		boolean isWater = block.getType().equals(Material.WATER);
		if (!isAir && !isWater) return;

		EntityType entityType = null;
		if (MaterialTag.SPAWN_EGGS.isTagged(item.getType())) {
			String spawnEggType = item.getType().name().toUpperCase().replaceAll("_SPAWN_EGG", "");

			try {
				entityType = EntityType.valueOf(spawnEggType);
			} catch (Exception ignored) {
				return;
			}
		} else if (item.getType().equals(Material.END_CRYSTAL)) {
			entityType = EntityType.ENDER_CRYSTAL;
		}

		if (entityType == null) return;

		// Place spawn sign instead
		event.setCancelled(true);

		block.setType(Material.OAK_SIGN);
		if (isWater) {
			Waterlogged waterlogged = (Waterlogged) block.getBlockData();
			waterlogged.setWaterlogged(true);
			block.setBlockData(waterlogged);
		}

		Sign sign = (Sign) block.getState();
		sign.setLine(0, "[spawn]");
		sign.setLine(1, entityType.name().toLowerCase());
		sign.update();
	}


	@AllArgsConstructor
	public enum SpecialBlockType {
		TINTED_GLASS(Material.TINTED_GLASS, Material.AIR),
		RED_GLASS(Material.RED_STAINED_GLASS, Material.BARRIER),
		NETHERITE_BLOCK(Material.NETHERITE_BLOCK, Material.TNT),

		;

		@Getter
		private final Material fromType;
		@Getter
		private final Material toType;

		public static @Nullable Material convert(Material fromType) {
			SpecialBlockType type = Arrays.stream(values())
				.filter(specialBlockType -> specialBlockType.getFromType().equals(fromType))
				.findFirst()
				.orElse(null);

			return type == null ? null : type.getToType();
		}
	}

	public static boolean isInBuildRegion(Player player) {
		World world = player.getWorld();
		if (!world.equals(Bukkit.getWorld("buildadmin")))
			return false;

		return new WorldGuardUtils(world).isInRegion(player, region_buildadmin);
	}
}

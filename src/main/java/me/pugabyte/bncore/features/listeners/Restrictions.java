package me.pugabyte.bncore.features.listeners;

import me.pugabyte.bncore.features.chat.Koda;
import me.pugabyte.bncore.models.nerd.Nerd;
import me.pugabyte.bncore.models.nerd.Rank;
import me.pugabyte.bncore.utils.MaterialTag;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent.Cause;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.pugabyte.bncore.utils.StringUtils.camelCase;
import static me.pugabyte.bncore.utils.StringUtils.colorize;

public class Restrictions implements Listener {
	private static final String PREFIX = Koda.getLocalFormat();

	@EventHandler
	public void onPortalEvent(PlayerPortalEvent event) {
		if (WorldGroup.get(event.getPlayer()).equals(WorldGroup.CREATIVE))
			event.setCancelled(true);
	}

	@EventHandler
	public void onInteractWithFire(PlayerInteractEvent event) {
		if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
			return;

		if (Utils.isNullOrAir(event.getItem()))
			return;

		Material itemType = event.getItem().getType();
		if (!(itemType.equals(Material.FLINT_AND_STEEL) || itemType.equals(Material.FIRE_CHARGE)))
			return;

		Player player = event.getPlayer();
		Material clickedMaterial = event.getClickedBlock().getType();
		if (WorldGroup.get(player).equals(WorldGroup.CREATIVE))
			if (clickedMaterial.equals(Material.TNT))
				event.setCancelled(true);

		if (!(clickedMaterial.equals(Material.OBSIDIAN) || clickedMaterial.equals(Material.NETHERRACK)))
			if (!player.hasPermission("use.fire")) {
				event.setCancelled(true);
				player.sendMessage(colorize(PREFIX + "Sorry, but you are not a high enough rank to light fire! Please create a &c/ticket &fto ask a staff member to light fire for you"));
			}
	}

	@EventHandler
	public void onPlaceLava(PlayerBucketEmptyEvent event) {
		Material material = event.getBucket();
		if (!material.equals(Material.LAVA_BUCKET))
			return;

		Player player = event.getPlayer();
		if (!player.hasPermission("use.fire")) {
			event.setCancelled(true);
			player.sendMessage(colorize(PREFIX + "Hey " + player.getName() + "! I noticed that you are trying to place lava. Unfortunately lava placing is disabled for Member and below due to grief and safety issues"));
			Tasks.wait(20, () -> player.sendMessage(colorize(PREFIX + "However, a staff member will be happy to place it for you. Please create a &c/ticket &fand a staff member will be with your shortly! :)")));
		}
	}

	@EventHandler
	public void onPlaceTNT(BlockPlaceEvent event) {
		Material material = event.getBlock().getType();
		if (!material.equals(Material.TNT))
			return;

		Player player = event.getPlayer();
		if (!player.hasPermission("use.fire")) {
			event.setCancelled(true);
			player.sendMessage(colorize(PREFIX + "Sorry, but you can't use TNT! You must be Member or above"));
		}
	}

	@EventHandler
	public void onCraftTNT(CraftItemEvent event) {
		if (!event.getRecipe().getResult().getType().equals(Material.TNT))
			return;

		Player player = (Player) event.getWhoClicked();
		if (!player.hasPermission("use.fire")) {
			event.setCancelled(true);
			player.sendMessage(colorize(PREFIX + "Sorry, but you can't use TNT! You must be Member or above"));
		}
	}

	@EventHandler
	public void onWitherRoseEffect(EntityPotionEffectEvent event) {
		if (event.getCause() == Cause.WITHER_ROSE)
			event.setCancelled(true);
	}

	@EventHandler
	public void onInteractHoldingEnderCrystal(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (player.hasPermission("use.fire"))
			return;

		if (!Utils.isNullOrAir(event.getItem()) && event.getItem().getType().equals(Material.END_CRYSTAL)) {
			event.setCancelled(true);
			player.sendMessage(colorize(PREFIX + "Sorry, but you can't use Ender Crystals! You must be Member or above"));
		}
	}

	@EventHandler
	public void onDamageEnderCrystal(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		if (damager instanceof Arrow)
			if (((Arrow) damager).getShooter() instanceof Player)
				damager = ((Player) ((Arrow) damager).getShooter()).getPlayer();

		if (!event.getEntity().getType().equals(EntityType.ENDER_CRYSTAL))
			return;

		if (!damager.hasPermission("use.fire")) {
			event.setCancelled(true);
			damager.sendMessage(colorize(PREFIX + "Sorry, but you can't use Ender Crystals! You must be Member or above"));
		}

	}

	@EventHandler
	public void onInteractHoldingSpawnEgg(PlayerInteractEvent event) {
		if (Utils.isNullOrAir(event.getItem()))
			return;

		if (!MaterialTag.SPAWN_EGGS.isTagged(event.getItem().getType()))
			return;

		if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
			return;

		if (!event.getClickedBlock().getType().equals(Material.SPAWNER))
			return;

		if (!event.getPlayer().hasPermission("group.seniorstaff"))
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlaceBed(BlockPlaceEvent event) {
		if (!MaterialTag.BEDS.isTagged(event.getBlock().getType()))
			return;

		Player player = event.getPlayer();
		String worldName = player.getWorld().getName().toLowerCase();
		if (!(worldName.endsWith("nether") || worldName.endsWith("the_end")))
			return;

		if (!player.hasPermission("group.staff")) {
			event.setCancelled(true);
			player.sendMessage(colorize(PREFIX + "Sorry, but you can't place beds here! They will go boom!"));
		}
	}

	// TODO This should be handled by WE but its broken
	@EventHandler
	public void onWorldEditCommand(PlayerCommandPreprocessEvent event) {
		if (!Arrays.asList(Rank.GUEST, Rank.MEMBER).contains(new Nerd(event.getPlayer()).getRank()))
			return;

		String command = event.getMessage().toLowerCase();

		if (!command.split(" ")[0].replace("worldedit:", "").startsWith("//"))
			return;

		List<Material> used = new ArrayList<>();
		disallowedInWorldEdit.forEach(material -> {
			if (command.contains(material.name().toLowerCase()))
				used.add(material);
		});

		if (!used.isEmpty()) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(colorize("&cYou cannot use the following materials with WorldEdit:"));
			used.forEach(material ->
					event.getPlayer().sendMessage(colorize("&7 - &c" + camelCase(material.name()))));
		}
	}

	private static final List<Material> disallowedInWorldEdit = Arrays.asList(
			Material.ACACIA_BUTTON,
			Material.ACACIA_PRESSURE_PLATE,
			Material.ACACIA_SAPLING,
			Material.ACACIA_SIGN,
			Material.ACACIA_WALL_SIGN,
			Material.ACACIA_DOOR,
			Material.ACTIVATOR_RAIL,
			Material.ALLIUM,
			Material.ANVIL,
			Material.AZURE_BLUET,
			Material.BEETROOTS,
			Material.BIRCH_BUTTON,
			Material.BIRCH_DOOR,
			Material.BIRCH_PRESSURE_PLATE,
			Material.BIRCH_SAPLING,
			Material.BIRCH_SIGN,
			Material.BIRCH_WALL_SIGN,
			Material.BLACK_BANNER,
			Material.BLACK_CARPET,
			Material.BLACK_CONCRETE_POWDER,
			Material.BLACK_WALL_BANNER,
			Material.BLUE_BANNER,
			Material.BLUE_CARPET,
			Material.BLUE_CONCRETE_POWDER,
			Material.BLUE_ORCHID,
			Material.BLUE_WALL_BANNER,
			Material.BROWN_BANNER,
			Material.BROWN_CARPET,
			Material.BROWN_CONCRETE_POWDER,
			Material.BROWN_MUSHROOM,
			Material.BROWN_WALL_BANNER,
			Material.CACTUS,
			Material.CAKE,
			Material.CARROTS,
			Material.CHEST,
			Material.CHIPPED_ANVIL,
			Material.CHORUS_FLOWER,
			Material.CHORUS_PLANT,
			Material.COCOA,
			Material.COMPARATOR,
			Material.CORNFLOWER,
			Material.CYAN_BANNER,
			Material.CYAN_CARPET,
			Material.CYAN_CONCRETE_POWDER,
			Material.CYAN_WALL_BANNER,
			Material.DAMAGED_ANVIL,
			Material.DANDELION,
			Material.DARK_OAK_BUTTON,
			Material.DARK_OAK_DOOR,
			Material.DARK_OAK_PRESSURE_PLATE,
			Material.DARK_OAK_SAPLING,
			Material.DARK_OAK_SIGN,
			Material.DARK_OAK_WALL_SIGN,
			Material.DEAD_BUSH,
			Material.DEAD_BUSH,
			Material.DETECTOR_RAIL,
			Material.DRAGON_EGG,
			Material.END_PORTAL,
			Material.END_PORTAL,
			Material.FERN,
			Material.FLOWER_POT,
			Material.GRASS,
			Material.GRAVEL,
			Material.GRAY_BANNER,
			Material.GRAY_CARPET,
			Material.GRAY_CONCRETE_POWDER,
			Material.GRAY_WALL_BANNER,
			Material.GREEN_BANNER,
			Material.GREEN_CARPET,
			Material.GREEN_CONCRETE_POWDER,
			Material.GREEN_WALL_BANNER,
			Material.HEAVY_WEIGHTED_PRESSURE_PLATE,
			Material.IRON_DOOR,
			Material.JUNGLE_BUTTON,
			Material.JUNGLE_DOOR,
			Material.JUNGLE_PRESSURE_PLATE,
			Material.JUNGLE_SAPLING,
			Material.JUNGLE_SIGN,
			Material.JUNGLE_WALL_SIGN,
			Material.KELP,
			Material.LADDER,
			Material.LARGE_FERN,
			Material.LEVER,
			Material.LIGHT_BLUE_BANNER,
			Material.LIGHT_BLUE_CARPET,
			Material.LIGHT_BLUE_CONCRETE_POWDER,
			Material.LIGHT_BLUE_WALL_BANNER,
			Material.LIGHT_GRAY_BANNER,
			Material.LIGHT_GRAY_CARPET,
			Material.LIGHT_GRAY_CONCRETE_POWDER,
			Material.LIGHT_GRAY_WALL_BANNER,
			Material.LIGHT_WEIGHTED_PRESSURE_PLATE,
			Material.LILAC,
			Material.LILY_OF_THE_VALLEY,
			Material.LILY_PAD,
			Material.LIME_BANNER,
			Material.LIME_CARPET,
			Material.LIME_CONCRETE_POWDER,
			Material.LIME_WALL_BANNER,
			Material.MAGENTA_BANNER,
			Material.MAGENTA_CARPET,
			Material.MAGENTA_CONCRETE_POWDER,
			Material.MAGENTA_WALL_BANNER,
			Material.MELON_STEM,
			Material.NETHER_PORTAL,
			Material.NETHER_PORTAL,
			Material.NETHER_WART,
			Material.OAK_BUTTON,
			Material.OAK_DOOR,
			Material.OAK_PRESSURE_PLATE,
			Material.OAK_SAPLING,
			Material.OAK_SIGN,
			Material.OAK_WALL_SIGN,
			Material.ORANGE_BANNER,
			Material.ORANGE_CARPET,
			Material.ORANGE_CONCRETE_POWDER,
			Material.ORANGE_TULIP,
			Material.ORANGE_WALL_BANNER,
			Material.OXEYE_DAISY,
			Material.PEONY,
			Material.PINK_BANNER,
			Material.PINK_CARPET,
			Material.PINK_CONCRETE_POWDER,
			Material.PINK_TULIP,
			Material.PINK_WALL_BANNER,
			Material.POPPY,
			Material.POTATOES,
			Material.POWERED_RAIL,
			Material.PUMPKIN_STEM,
			Material.PURPLE_BANNER,
			Material.PURPLE_CARPET,
			Material.PURPLE_CONCRETE_POWDER,
			Material.PURPLE_WALL_BANNER,
			Material.RAIL,
			Material.RED_BANNER,
			Material.RED_CARPET,
			Material.RED_CONCRETE_POWDER,
			Material.RED_MUSHROOM,
//			Material.RED_SAND,
			Material.RED_TULIP,
			Material.RED_WALL_BANNER,
			Material.REDSTONE,
			Material.REDSTONE_WIRE,
			Material.REDSTONE_TORCH,
			Material.REPEATER,
			Material.ROSE_BUSH,
//			Material.SAND,
			Material.SEA_PICKLE,
			Material.SEAGRASS,
			Material.SNOW,
			Material.SPRUCE_BUTTON,
			Material.SPRUCE_DOOR,
			Material.SPRUCE_PRESSURE_PLATE,
			Material.SPRUCE_SAPLING,
			Material.SPRUCE_SIGN,
			Material.SPRUCE_WALL_SIGN,
			Material.STONE_BUTTON,
			Material.STONE_PRESSURE_PLATE,
			Material.SUGAR_CANE,
			Material.SUNFLOWER,
			Material.TALL_GRASS,
			Material.TORCH,
			Material.TRAPPED_CHEST,
			Material.VINE,
			Material.WHEAT,
			Material.WHITE_BANNER,
			Material.WHITE_CARPET,
			Material.WHITE_CONCRETE_POWDER,
			Material.WHITE_TULIP,
			Material.WHITE_WALL_BANNER,
			Material.WITHER_ROSE,
			Material.YELLOW_BANNER,
			Material.YELLOW_CARPET,
			Material.YELLOW_CONCRETE_POWDER,
			Material.YELLOW_WALL_BANNER
	);

}

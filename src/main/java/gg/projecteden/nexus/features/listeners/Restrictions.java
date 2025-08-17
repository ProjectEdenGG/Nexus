package gg.projecteden.nexus.features.listeners;

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.features.chat.Censor;
import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.craftbukkit.entity.CraftFishHook;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent.Cause;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Restrictions implements Listener {
	private static final List<WorldGroup> ALLOWED_WORLD_GROUPS = Arrays.asList(WorldGroup.SURVIVAL, WorldGroup.CREATIVE, WorldGroup.SKYBLOCK);
	private static final List<WorldGroup> BLOCKED_WORLD_GROUPS = Arrays.asList(WorldGroup.LEGACY, WorldGroup.SERVER);
	private static final List<String> BLOCKED_WORLDS = Arrays.asList("safepvp", "events");

	public static boolean isPerkAllowedAt(HasUniqueId player, Location location) {
		if (Rank.of(player).isAdmin())
			return true;

		WorldGroup worldGroup = WorldGroup.of(location);
		if (!ALLOWED_WORLD_GROUPS.contains(worldGroup))
			return false;

		if (BLOCKED_WORLD_GROUPS.contains(worldGroup))
			return false;

		if (BLOCKED_WORLDS.contains(location.getWorld().getName()))
			return false;

		WorldGuardUtils worldGuardUtils = new WorldGuardUtils(location);
		if (!worldGuardUtils.getRegionsAt(location).isEmpty())
			return false;

		return true;
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (event.getCause() != TeleportCause.SPECTATE)
			return;

		final Player player = event.getPlayer();
		if (Rank.of(player).isStaff())
			return;

		event.setCancelled(true);
		player.setGameMode(GameMode.SPECTATOR);
	}

	private void spawnShoulderParrot(Location location, Parrot original) {
		location.getWorld().spawn(location, Parrot.class, parrot -> {
			parrot.setAI(original.hasAI());
			parrot.setAge(original.getAge());
			parrot.setBreed(original.canBreed());
			parrot.setOwner(original.getOwner());
			parrot.setTamed(original.isTamed());
			parrot.setVariant(original.getVariant());
			parrot.customName(original.customName());
			parrot.setCustomNameVisible(original.isCustomNameVisible());
			if (original.isAdult())
				parrot.setAdult();
			else
				parrot.setBaby();
		});
	}

	@EventHandler
	@SuppressWarnings("deprecation")
	public void onParrotTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();

		if (CitizensUtils.isNPC(player))
			return;

		boolean perkAllowed = Restrictions.isPerkAllowedAt(player, event.getTo());
		boolean sameWorldGroup = WorldGroup.of(player) == WorldGroup.of(event.getTo());
		if (perkAllowed && sameWorldGroup)
			return;

		if (player.getShoulderEntityLeft() instanceof Parrot leftParrot) {
			spawnShoulderParrot(event.getFrom(), leftParrot);
			player.setShoulderEntityLeft(null);
		}

		if (player.getShoulderEntityRight() instanceof Parrot rightParrot) {
			spawnShoulderParrot(event.getFrom(), rightParrot);
			player.setShoulderEntityRight(null);
		}
	}


	private static final CooldownService COOLDOWN_SERVICE = new CooldownService();

	@EventHandler(priority = EventPriority.LOWEST)
	public void onWorldChange(PlayerTeleportEvent event) {
		if (event.getFrom().getWorld().equals(event.getTo().getWorld()))
			return;

		if (Nerd.of(event.getPlayer()).getRank().isStaff())
			return;

		if (CooldownService.isNotOnCooldown(event.getPlayer(), "world-change", TickTime.SECOND))
			return;

		event.setCancelled(true);
		PlayerUtils.send(event.getPlayer(), StringUtils.getPrefix("Restrictions") + "&cYou cannot change worlds that fast");
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onWorldChangeGamemode(PlayerTeleportEvent event) {
		if (event.getFrom().getWorld().equals(event.getTo().getWorld()))
			return;

		if (Nerd.of(event.getPlayer()).getRank().isStaff())
			return;

		Tasks.wait(2, () -> {
			if (WorldGroup.of(event.getPlayer()) == WorldGroup.SURVIVAL)
				event.getPlayer().setGameMode(GameMode.SURVIVAL);
		});
	}

	@EventHandler
	public void onCommandMinecartSpawn(EntitySpawnEvent event) {
		if (event.getEntity() instanceof CommandMinecart) {
			event.setCancelled(true);
			Tasks.wait(1, () -> event.getEntity().remove());
		}
	}

	@EventHandler
	public void onCommandMinecartInteract(PlayerInteractEvent event) {
		if (Nullables.isNullOrAir(event.getItem()))
			return;

		if (event.getItem().getType() == Material.COMMAND_BLOCK_MINECART)
			event.setCancelled(true);
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		Player player = event.getPlayer();
		if (Rank.of(player).isStaff())
			return;

		String[] lines = event.getLines();
		boolean censored = false;

		for (int i = 0; i < lines.length; i++) {
			String line = StringUtils.stripColor(lines[i]);
			if (Censor.isCensored(player, line)) {
				event.setLine(i, "");
				censored = true;
			}
		}

		if (!censored)
			return;

		PlayerUtils.send(player, "&cInappropriate sign content");
		String location = "(" + StringUtils.xyzw(event.getBlock().getLocation()) + ")";
		String message = "&cSign content by " + Nickname.of(player) + " was censored: &e" + String.join(", ", lines) + " " + location;
		Broadcast.staff().prefix("Censor").message(message).send();
	}

	@EventHandler
	public void onAnvilRenameItem(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player player))
			return;

		Inventory inventory = event.getClickedInventory();
		if (inventory == null || inventory.getType() != InventoryType.ANVIL)
			return;

		if (event.getSlotType() != SlotType.RESULT)
			return;

		ItemStack item = event.getCurrentItem();

		if (Nullables.isNullOrAir(item))
			return;

		ItemMeta meta = item.getItemMeta();

		String input = meta.getDisplayName();
		if (!Censor.isCensored(player, input))
			return;

		meta.setDisplayName(null);
		item.setItemMeta(meta);

		PlayerUtils.send(player, "&cInappropriate item name");
		String message = "&cAnvil name by " + Nickname.of(player) + " was censored: &e" + input;
		Broadcast.staff().prefix("Censor").message(message).send();
	}

	@EventHandler
	public void onPortalEvent(PlayerPortalEvent event) {
		if (Arrays.asList(WorldGroup.SKYBLOCK, WorldGroup.CREATIVE).contains(WorldGroup.of(event.getPlayer())))
			event.setCancelled(true);
	}

	@EventHandler
	public void onEndPortalCreate(PortalCreateEvent event) {
		final WorldGroup worldGroup = WorldGroup.of(event.getWorld());
		if (worldGroup == WorldGroup.SURVIVAL)
			return;

		// Vanilla mechanic portals
		if (worldGroup == WorldGroup.MINIGAMES && !Minigames.getWorld().equals(event.getWorld()))
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onWitherRoseEffect(EntityPotionEffectEvent event) {
		if (event.getCause() == Cause.WITHER_ROSE)
			if (event.getEntity() instanceof Player)
				event.setCancelled(true);
	}

	@EventHandler
	public void onSkyBlockFallingCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		if (WorldGroup.of(player) != WorldGroup.SKYBLOCK)
			return;

		if (Vanish.isVanished(player))
			return;

		if (player.getLocation().getY() < -1000)
			return;

		if (player.getFallDistance() > 5 && !player.isFlying()) {
			event.setCancelled(true);
			PlayerUtils.send(player, "&cYou cannot run commands while falling (try moving onto a solid block)");
		}
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		event.getPlayer().closeInventory();
		if (event.getFrom().getWorld().getEnvironment() == Environment.THE_END || event.getTo().getWorld().getEnvironment() != Environment.THE_END)
			return;

		if (Rank.of(event.getPlayer()).gte(Rank.TRUSTED))
			return;

		AdvancementProgress progress = event.getPlayer().getAdvancementProgress(PlayerUtils.getAdvancement("story/follow_ender_eye"));
		if (!progress.isDone()) {
			event.setCancelled(true);
			PlayerUtils.send(event.getPlayer(), "&cYou must enter an end portal before you can enter The End!");
		}
	}

	@EventHandler
	public void onInteractHoldingSpawnEgg(PlayerInteractEvent event) {
		if (Nullables.isNullOrAir(event.getItem())) return;
		if (!MaterialTag.SPAWN_EGGS.isTagged(event.getItem().getType())) return;
		if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
		if (Nullables.isNullOrAir(event.getClickedBlock())) return;
		if (!event.getClickedBlock().getType().equals(Material.SPAWNER)) return;

		if (WorldGroup.STAFF.contains(event.getClickedBlock().getWorld()))
			return;

		if (!Rank.of(event.getPlayer()).isSeniorStaff())
			event.setCancelled(true);
	}

	// TODO This should be handled by WE but its broken
	@EventHandler
	public void onWorldEditCommand(PlayerCommandPreprocessEvent event) {
		if (event.getPlayer().hasPermission("worldedit.bypass.material"))
			return;

		if (!Arrays.asList(Rank.GUEST, Rank.MEMBER).contains(Rank.of(event.getPlayer())))
			return;

		String command = event.getMessage().toLowerCase();

		if (!command.split(" ")[0].replace("worldedit:", "").startsWith("//"))
			return;

		Set<Material> used = new HashSet<>();
		disallowedInWorldEdit.forEach(material -> {
			for (String arg : command.split(" "))
				for (String input : arg.split(","))
					if (input.equals(material.name().toLowerCase()))
						used.add(material);
		});

		if (!used.isEmpty()) {
			event.setCancelled(true);
			PlayerUtils.send(event.getPlayer(), "&cYou cannot use the following materials with WorldEdit:");
			used.forEach(material ->
				PlayerUtils.send(event.getPlayer(), "&7 - &c" + StringUtils.camelCase(material.name())));
		}
	}

	@EventHandler
	public void onNPCInvOpen(InventoryOpenEvent event) {
		Player player = (Player) event.getPlayer();
		NPC selectedNPC = CitizensUtils.getSelectedNPC(player);
		if (selectedNPC == null)
			return;

		net.citizensnpcs.api.trait.trait.Inventory inventory = selectedNPC.getTraitNullable(net.citizensnpcs.api.trait.trait.Inventory.class);
		if (inventory == null)
			return;

		InventoryHolder npcHolder = inventory.getInventoryView().getHolder();
		if (npcHolder == null)
			return;

		if (!npcHolder.equals(event.getInventory().getHolder()))
			return;

		World world = player.getWorld();
		if (selectedNPC.getStoredLocation().getWorld().equals(world))
			return;

		event.setCancelled(true);
		PlayerUtils.send(player, StringUtils.getPrefix("NPC") + "&cYou must be in the same world as your selected NPC (" + world.getName() + ")");
	}

	private static final Set<Material> disallowedInWorldEdit = Set.of(
		Material.SMALL_AMETHYST_BUD,
		Material.MEDIUM_AMETHYST_BUD,
		Material.LARGE_AMETHYST_BUD,
		Material.AMETHYST_CLUSTER,
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
		Material.BAMBOO,
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
		Material.BLAST_FURNACE,
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
		Material.CRIMSON_FUNGUS,
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
		Material.DETECTOR_RAIL,
		Material.DRAGON_EGG,
		Material.END_PORTAL,
		Material.FERN,
		Material.FLOWER_POT,
		Material.FURNACE,
		Material.SHORT_GRASS,
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
		Material.RED_SAND,
		Material.RED_TULIP,
		Material.RED_WALL_BANNER,
		Material.REDSTONE,
		Material.REDSTONE_WIRE,
		Material.REDSTONE_TORCH,
		Material.REPEATER,
		Material.ROSE_BUSH,
		Material.SAND,
		Material.SEA_PICKLE,
		Material.SEAGRASS,
		Material.SMOKER,
		Material.SNOW,
		Material.SOUL_TORCH,
		Material.SOUL_WALL_TORCH,
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
		Material.WARPED_FUNGUS,
		Material.WALL_TORCH,
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

	@EventHandler(priority = EventPriority.LOWEST)
	public void onVanillaAchievement(PlayerAdvancementCriterionGrantEvent event) {
		if (!WorldGroup.SURVIVAL.contains(event.getPlayer().getWorld()) || event.getPlayer().getGameMode() != GameMode.SURVIVAL)
			event.setCancelled(true);
	}

	static {
		var ignored = List.of(CraftFishHook.class);

		Tasks.repeat(0, TickTime.SECOND.x(5), () -> {
			for (var world : Bukkit.getWorlds())
				for (var projectile : world.getEntitiesByClass(Projectile.class)) {
					if (ignored.contains(projectile.getClass()))
						continue;
					if (projectile.getTicksLived() <= TickTime.MINUTE.x(3))
						continue;

					projectile.remove();
				}
		});
	}

}

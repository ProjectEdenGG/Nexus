package gg.projecteden.nexus.features.listeners;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent.SlotType;
import de.tr7zw.nbtapi.NBTTileEntity;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.commands.teleport.TeleportCommand;
import gg.projecteden.nexus.features.listeners.events.FakePlayerInteractEvent;
import gg.projecteden.nexus.features.listeners.events.PlayerDamageByPlayerEvent;
import gg.projecteden.nexus.features.listeners.events.WorldGroupChangedEvent;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.vanish.events.VanishToggleEvent;
import gg.projecteden.nexus.framework.commands.Commands;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.tip.Tip;
import gg.projecteden.nexus.models.tip.Tip.TipType;
import gg.projecteden.nexus.models.tip.TipService;
import gg.projecteden.nexus.utils.*;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup.SpawnType;
import me.libraryaddict.disguise.DisguiseAPI;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.metadata.MetadataValue;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static gg.projecteden.nexus.utils.ItemUtils.getTool;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

@SuppressWarnings("SwitchStatementWithTooFewBranches")
public class Misc implements Listener {

	static {
		for (World world : Bukkit.getWorlds()) {
			// Skip main world
			if (!world.equals(Bukkit.getWorlds().get(0)))
				world.setKeepSpawnInMemory(false);

			world.setGameRule(GameRule.COMMAND_BLOCK_OUTPUT, false);
			if (WorldGroup.of(world) == WorldGroup.SURVIVAL)
				world.setDifficulty(Difficulty.HARD);

			// disable TIME_SINCE_SLEEP (used to determine when to spawn phantoms) outside the survival worlds
			WorldGroup worldGroup = WorldGroup.of(world);
			((CraftWorld) world).getHandle().paperConfig().entities.behavior.tickTimeSinceSleep = worldGroup == WorldGroup.SURVIVAL || worldGroup == WorldGroup.SKYBLOCK;
		}
	}

	@EventHandler
	public void on(PlayerTeleportEvent event) {
		final WorldBorder border = event.getTo().getWorld().getWorldBorder();
		if (border.isInside(event.getTo()))
			return;

		if (Minigamer.of(event.getPlayer()).isPlaying())
			// Trust mechanics know what they are doing
			return;

		event.setCancelled(true);
		PlayerUtils.send(event.getPlayer(), Commands.getPrefix(TeleportCommand.class) + "&cYou cannot teleport outside of the border");
	}

	@EventHandler
	public void onLightEndPortal(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		final ItemStack item = event.getItem();
		final Block block = event.getClickedBlock();
		if (isNullOrAir(item) || isNullOrAir(block))
			return;

		if (item.getType() != Material.ENDER_EYE)
			return;

		if (block.getType() != Material.END_PORTAL_FRAME)
			return;

		if (WorldGroup.of(block) == WorldGroup.SURVIVAL)
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onPlaceOnLight(BlockPlaceEvent event) {
		if (event.getBlockReplacedState().getType() != Material.LIGHT)
			return;

		if (event.getBlock().getType() == Material.LIGHT)
			return;

		event.setCancelled(true);
		PlayerUtils.send(event.getPlayer(), "&c&lHey! &7There's a light block there.");
	}

	@EventHandler
	public void onLightInteract(PlayerInteractEvent event) {
		if (event.getAction() != Action.LEFT_CLICK_BLOCK)
			return;

		if (event.getPlayer().getGameMode() != GameMode.SURVIVAL)
			return;

		final Block block = event.getClickedBlock();
		if (isNullOrAir(block) || block.getType() != Material.LIGHT)
			return;

		final ItemStack item = event.getItem();
		if (isNullOrAir(item) || item.getType() != Material.LIGHT)
			return;

		if (!new BlockBreakEvent(block, event.getPlayer()).callEvent())
			return;

		block.setType(Material.AIR);
		block.getWorld().dropItemNaturally(block.getLocation().toCenterLocation(), new ItemStack(Material.LIGHT));
	}

	@EventHandler
	public void onVanishToggleEvent(VanishToggleEvent event) {
		Nexus.getOpenInv().setPlayerSilentChestStatus(event.getPlayer(), event.getUser().isVanished());
	}

	@EventHandler
	public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
		if (event.getRemover() instanceof Projectile)
			event.setCancelled(true);
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof ItemFrame)
			if (event.getDamager() instanceof Projectile)
				event.setCancelled(true);
	}

	@EventHandler
	public void onDamageArmorStand(EntityDamageByEntityEvent event) {
		if (!event.getEntityType().equals(EntityType.ARMOR_STAND))
			return;

		if (!event.getCause().equals(DamageCause.ENTITY_EXPLOSION))
			return;

		if (event.getDamager() instanceof Player)
			return;

		if (WorldGroup.of(event.getEntity()) != WorldGroup.SURVIVAL)
			return;

		event.setCancelled(true);
	}

	private static final Map<EquipmentSlot, MaterialTag> slots = Map.of(
		EquipmentSlot.HEAD, MaterialTag.ALL_HELMETS,
		EquipmentSlot.CHEST, new MaterialTag(MaterialTag.ALL_CHESTPLATES).append(Material.ELYTRA),
		EquipmentSlot.LEGS, MaterialTag.ALL_LEGGINGS,
		EquipmentSlot.FEET, MaterialTag.ALL_BOOTS
	);

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event instanceof FakePlayerInteractEvent)
			return;

		if (!ActionGroup.RIGHT_CLICK.applies(event))
			return;

		// Ignore McMMO blocks
		if (event.getClickedBlock() != null)
			if (List.of(Material.IRON_BLOCK, Material.GOLD_BLOCK).contains(event.getClickedBlock().getType()))
				return;

		if (event.getHand() != EquipmentSlot.HAND)
			return;

		final ItemStack item = event.getItem();
		final PlayerInventory inventory = event.getPlayer().getInventory();

		for (EquipmentSlot slot : slots.keySet()) {
			final MaterialTag tag = slots.get(slot);
			if (!tag.isTagged(item))
				continue;

			final ItemStack existing = ItemUtils.clone(inventory.getItem(slot));
			if (!isNullOrAir(existing) && existing.getItemMeta().hasEnchant(Enchant.BINDING_CURSE))
				continue;

			final PlayerArmorChangeEvent armorChangeEvent = new PlayerArmorChangeEvent(event.getPlayer(), SlotType.valueOf(slot.name()), existing, item);
			if (!armorChangeEvent.callEvent())
				continue;

			inventory.setItem(EquipmentSlot.HAND, existing);
			inventory.setItem(slot, ItemUtils.clone(item));
			event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1f, 1f);
			return;
		}
	}

	@EventHandler
	public void onCoralDeath(BlockFadeEvent event) {
		Block block = event.getBlock();
		if (MaterialTag.ALL_CORALS.isTagged(block.getType())) {
			WorldGroup worldGroup = WorldGroup.of(block.getWorld());
			if (WorldGroup.CREATIVE == worldGroup || WorldGroup.ADVENTURE == worldGroup || WorldGroup.MINIGAMES == worldGroup)
				event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityTarget(EntityTargetEvent event) {
		if (event.getTarget() == null)
			return;

		final EntityType attackerType = event.getEntity().getType();
		final EntityType targetType = event.getTarget().getType();

		if (attackerType != EntityType.AXOLOTL && attackerType != EntityType.FOX)
			return;

		if (targetType != EntityType.TROPICAL_FISH)
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onHorseLikeDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof AbstractHorse)
			if (event.getCause().equals(EntityDamageEvent.DamageCause.SUFFOCATION))
				event.setCancelled(true);
	}

	@EventHandler
	public void onWanderingTraderSpawn(EntitySpawnEvent event) {
		List<EntityType> types = Arrays.asList(EntityType.WANDERING_TRADER, EntityType.TRADER_LLAMA);
		List<WorldGroup> worlds = Arrays.asList(WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK);

		if (!types.contains(event.getEntity().getType()))
			return;

		if (worlds.contains(WorldGroup.of(event.getLocation().getWorld())))
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onVoidDamage(EntityDamageEvent event) {
		if (event.isCancelled())
			return;

		if (!(event.getEntity() instanceof Player player))
			return;

		if (event.getCause() != DamageCause.VOID)
			return;

		if (player.getWorld().getEnvironment() == Environment.THE_END)
			return;

		if (Minigamer.of(player).isPlaying())
			return;

		SpawnType spawnType = WorldGroup.of(player).getSpawnType();
		if (spawnType == null)
			return;

		spawnType.teleport(player);
	}

	@EventHandler
	public void onPlayerDamageByPlayer(PlayerDamageByPlayerEvent event) {
		if (event.getPlayer().getUniqueId().equals(event.getAttacker().getUniqueId()))
			event.getOriginalEvent().setCancelled(true);
	}

	@EventHandler
	public void onFireworkDamage(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Firework firework))
			return;

		if (!firework.hasMetadata(FireworkLauncher.METADATA_KEY_DAMAGE))
			return;

		for (MetadataValue value : firework.getMetadata(FireworkLauncher.METADATA_KEY_DAMAGE))
			if (!value.asBoolean())
				event.setCancelled(true);
	}

	@EventHandler
	public void onBreakEmptyShulkerBox(BlockBreakEvent event) {
		if (!MaterialTag.SHULKER_BOXES.isTagged(event.getBlock().getType()))
			return;

		if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
			return;

		NBTTileEntity tileEntityNBT = new NBTTileEntity(event.getBlock().getState());
		if (!tileEntityNBT.asNBTString().contains("Items:[")) {
			event.setCancelled(true);
			event.getBlock().setType(Material.AIR);
		}
	}

	@EventHandler
	public void on(WorldGroupChangedEvent event) {
		final Player player = event.getPlayer();

		switch (event.getNewWorldGroup()) {
			case MINIGAMES -> {
				if (DisguiseAPI.isDisguised(player))
					if (event.getOldWorldGroup() != WorldGroup.MINIGAMES)
						DisguiseAPI.undisguiseToAll(player);
			}
		}
	}

	@EventHandler
	public void resetPlayerTime(WorldGroupChangedEvent event) {
		Tasks.wait(10, event.getPlayer()::resetPlayerTime);
	}

	// ImageOnMap rotating frames on placement; rotate back one before placement to offset
	@EventHandler
	public void onMapHang(PlayerInteractEntityEvent event) {
		if (event.getHand() != EquipmentSlot.HAND)
			return;

		Entity entity = event.getRightClicked();
		if (!(entity instanceof ItemFrame itemFrame))
			return;

		ItemStack tool = getTool(event.getPlayer());
		if (tool == null)
			return;

		if (tool.getType() != Material.FILLED_MAP)
			return;

		int mapId = ((MapMeta) tool.getItemMeta()).getMapId();
		if (!Paths.get("plugins/ImageOnMap/images/map" + mapId + ".png").toFile().exists())
			return;

		if (!isNullOrAir(itemFrame.getItem()))
			return;

		itemFrame.setRotation(itemFrame.getRotation().rotateCounterClockwise());
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (WorldGroup.of(event.getPlayer()) != WorldGroup.SURVIVAL)
			return;

		if (!MaterialTag.CONCRETE_POWDERS.isTagged(event.getBlock().getType()))
			return;

		TipService tipService = new TipService();
		Tip tip = tipService.get(event.getPlayer());
		if (tip.show(TipType.CONCRETE))
			PlayerUtils.send(event.getPlayer(), "&3Did you know? &e- &3You can craft powdered concrete into concrete using a water bucket. &c/customrecipes");
	}

	@EventHandler
	public void on(EntityAddToWorldEvent event) {
		if (event.getEntity() instanceof EnderDragon dragon)
			if (dragon.getBossBar() != null)
				dragon.getBossBar().setColor(BarColor.PURPLE);
	}

	@EventHandler
	public void on(EntityDamageByEntityEvent event) {
		if (!event.getCause().equals(DamageCause.ENTITY_EXPLOSION)) return;
		if (!(event.getDamager() instanceof Creeper)) return;
		if (!(event.getEntity() instanceof Creeper creeper)) return;
		
		double health = creeper.getHealth() - event.getFinalDamage();
		if (health <= 0)
			event.setCancelled(true);

		creeper.ignite();
	}

	@EventHandler
	public void on(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		final Location location = player.getLocation();
		if (location.getWorld() != Bukkit.getWorlds().get(0))
			return;

		if (Rank.of(player).isAdmin())
			return;

		final WorldGuardUtils worldguard = new WorldGuardUtils(player);
		if (worldguard.getRegionsAt(location).size() != 0)
			return;

		SpawnType.HUB.teleport(player);
	}

}

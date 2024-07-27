package gg.projecteden.nexus.features.listeners;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.commands.staff.WorldGuardEditCommand;
import gg.projecteden.nexus.features.events.models.EventErrors;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerLeftRegionEvent;
import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.InventoryUtils.BlockInventoryType;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.TitleBuilder;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import gg.projecteden.nexus.utils.WorldGuardFlagUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.type.Farmland;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.MoistureChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static gg.projecteden.nexus.features.commands.staff.WorldGuardEditCommand.canWorldGuardEdit;
import static gg.projecteden.nexus.utils.EntityUtils.isHostile;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;
import static gg.projecteden.nexus.utils.WorldGuardFlagUtils.CustomFlags.*;

public class WorldGuardFlags implements Listener {

	@EventHandler
	public void onCandleModify(PlayerInteractEvent event) {
		final Block block = event.getClickedBlock();
		if (isNullOrAir(block))
			return;

		if (WorldGuardEditCommand.canWorldGuardEdit(event.getPlayer()))
			return;

		if (new WorldGuardUtils(block).getRegionsAt(block.getLocation()).isEmpty())
			return;

		boolean cancel = false;

		// if player clicks candle
		if (MaterialTag.CANDLES.isTagged(block.getType()))
			cancel = true;

			// if player right clicks
		else if (ActionGroup.RIGHT_CLICK.applies(event)) {
			// if player is holding candle
			if (event.getItem() != null && MaterialTag.CANDLES.isTagged(event.getItem().getType()))
				cancel = true;
		}

		if (cancel)
			event.setCancelled(true);
	}

	// temp fix for item frames making sound on cancelled hanging break with custom model items
	@EventHandler
	public void onEntityItemFrameDamage(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			if (event.getEntity() instanceof ItemFrame itemFrame) {
				ItemStack itemStack = itemFrame.getItem();
				if (isNullOrAir(itemStack))
					return;

				if (CustomModel.exists(itemStack))
					itemFrame.setSilent(false);
			}
		}
	}

	@EventHandler
	public void onItemFrameBreak(HangingBreakEvent event) {
		if (RemoveCause.ENTITY.equals(event.getCause()))
			return;

		if (WorldGuardFlagUtils.query(event.getEntity().getLocation(), HANGING_BREAK) == State.DENY) {
			event.setCancelled(true);

		} else if (event.getEntity() instanceof ItemFrame itemFrame) {
			ItemStack itemStack = itemFrame.getItem();
			if (isNullOrAir(itemStack))
				return;

			if (CustomModel.exists(itemStack)) {
				itemFrame.setSilent(true);
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBlockSpread(BlockSpreadEvent event) {
		if (event.getNewState().getType() == Material.BAMBOO)
			if (WorldGuardFlagUtils.query(event.getBlock().getLocation(), Flags.CROP_GROWTH) == State.DENY)
				event.setCancelled(true);
	}

	@EventHandler
	public void onBonemealUse(PlayerInteractEvent event) {
		if (!EquipmentSlot.HAND.equals(event.getHand())) return;
		if (!Action.RIGHT_CLICK_BLOCK.equals(event.getAction())) return;

		ItemStack item = event.getItem();
		if (isNullOrAir(item)) return;
		if (!item.getType().equals(Material.BONE_MEAL)) return;

		Block clicked = event.getClickedBlock();
		if (isNullOrAir(clicked)) return;
		if (!(clicked instanceof Ageable ageable)) return;

		int age = ageable.getAge();
		if (age == ageable.getMaximumAge()) return;

		if (canWorldGuardEdit(event.getPlayer())) return;
		if (WorldGuardFlagUtils.query(clicked.getLocation(), Flags.CROP_GROWTH) != State.DENY) return;

		ageable.setAge(++age);
		clicked.setBlockData(ageable);
	}

	@EventHandler
	public void onCreatureSpawnAllow(CreatureSpawnEvent event) {
		try {
			Set<com.sk89q.worldedit.world.entity.EntityType> entityTypeSet = WorldGuardFlagUtils.queryValue(event.getLocation(), ALLOW_SPAWN);
			List<EntityType> entityTypeList = new ArrayList<>();
			if (entityTypeSet == null) return;
			entityTypeSet.forEach(entityType -> {
				try {
					entityTypeList.add(EntityType.valueOf(entityType.getName().toUpperCase().replace("MINECRAFT:", "")));
				} catch (Exception ignore) {}
			});
			if (entityTypeList.isEmpty()) return;
			if (!entityTypeList.contains(event.getEntityType()))
				event.setCancelled(true);
		} catch (Exception ignore) {}
	}

	@EventHandler
	public void onItemFrameBreak(HangingBreakByEntityEvent event) {
		Entity remover = event.getRemover();
		if (remover instanceof Player)
			return;

		if (WorldGuardFlagUtils.query(event.getEntity().getLocation(), HANGING_BREAK) == State.DENY)
			event.setCancelled(true);
	}

	@EventHandler
	public void onGrassDecay(BlockFadeEvent event) {
		if (event.getBlock().getType() == Material.GRASS_BLOCK && event.getNewState().getType() == Material.DIRT)
			if (WorldGuardFlagUtils.query(event.getBlock().getLocation(), GRASS_DECAY) == State.DENY)
				event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (isHostile(event.getEntity()))
			if (WorldGuardFlagUtils.query(event.getLocation(), HOSTILE_SPAWN) == State.DENY)
				event.setCancelled(true);
	}

	@EventHandler
	public void onEntityTarget(EntityTargetEvent event) {
		if (event.getTarget() != null)
			if (WorldGuardFlagUtils.query(event.getTarget().getLocation(), MOB_AGGRESSION) == State.DENY)
				event.setCancelled(true);
	}

	@EventHandler
	public void onEntityTame(EntityTameEvent event) {
		if (WorldGuardFlagUtils.query(event.getEntity().getLocation(), TAMING) == State.DENY) {
			event.setCancelled(true);
			PlayerUtils.send(event.getOwner(), "&c&lHey! &7Sorry, but you can't tame that here.");
		}
	}

	static {
		Tasks.repeat(TickTime.SECOND, TickTime.SECOND, () ->
				Minigames.getActiveMinigamers().forEach(minigamer -> {
					if (minigamer.getOnlinePlayer().isInWater())
						if (WorldGuardFlagUtils.query(minigamer.getOnlinePlayer().getLocation(), MINIGAMES_WATER_DAMAGE) == State.ALLOW)
							minigamer.getOnlinePlayer().damage(1.25);
				}));
	}

	@EventHandler
	public void onInteractTrapDoor(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		if (isNullOrAir(block) || !(MaterialTag.TRAPDOORS.isTagged(block.getType())))
			return;

		if (WorldGuardFlagUtils.query(block, USE_TRAP_DOORS) == State.DENY) {
			if (canWorldGuardEdit(event.getPlayer()))
				return;
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockInventoryOpen(InventoryOpenEvent event) {
		if (event.getInventory().getLocation() == null)
			return;

		if (canWorldGuardEdit(event.getPlayer()))
			return;

		if (Arrays.stream(BlockInventoryType.values()).noneMatch(blockInventoryType -> blockInventoryType.getInventoryType() == event.getInventory().getType()))
			return;

		Set<String> blockInventoryTypes = WorldGuardFlagUtils.queryValue(event.getInventory().getLocation(), ALLOWED_BLOCK_INVENTORIES);
		if (blockInventoryTypes == null)
			return;

		if (blockInventoryTypes.stream().map(BlockInventoryType::valueOf).map(BlockInventoryType::getInventoryType).toList().contains(event.getInventory().getType()))
			return;

		event.setCancelled(true);
		PlayerUtils.send(event.getPlayer(), EventErrors.CANT_OPEN);
	}

	@EventHandler
	public void onInteractFenceGate(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		if (isNullOrAir(block) || !(MaterialTag.FENCE_GATES.isTagged(block.getType())))
			return;

		if (WorldGuardFlagUtils.query(block, USE_FENCE_GATES) == State.DENY) {
			if (canWorldGuardEdit(event.getPlayer()))
				return;
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onInteractNoteBlock(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		if (isNullOrAir(block) || !block.getType().equals(Material.NOTE_BLOCK))
			return;

		if (WorldGuardFlagUtils.query(block, USE_NOTE_BLOCKS) == State.DENY) {
			if (canWorldGuardEdit(event.getPlayer()))
				return;
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onSoilMoistureChange(MoistureChangeEvent event) {
		Block block = event.getBlock();
		if (!block.getType().equals(Material.FARMLAND))
			return;

		Farmland from = (Farmland) block.getBlockData();
		Farmland to = (Farmland) event.getNewState().getBlockData();
		if (from.getMoisture() <= to.getMoisture())
			return;

		if (WorldGuardFlagUtils.query(block.getLocation(), com.sk89q.worldguard.protection.flags.Flags.SOIL_DRY) == State.DENY)
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEnterRegion(PlayerEnteredRegionEvent event) {
		Player player = event.getPlayer();

		// Action Bar
		String greeting_actionbar = event.getRegion().getFlag(GREETING_ACTIONBAR.get());
		if (!Nullables.isNullOrEmpty(greeting_actionbar)) {
			Integer actionbar_ticks = event.getRegion().getFlag(ACTIONBAR_TICKS.get());
			if (actionbar_ticks == null)
				actionbar_ticks = 60;
			else if (actionbar_ticks < 1)
				actionbar_ticks = 1;

			ActionBarUtils.sendActionBar(player, greeting_actionbar, actionbar_ticks);
		}

		// Titles
		String greeting_title = event.getRegion().getFlag(GREETING_TITLE.get());
		String greeting_subtitle = event.getRegion().getFlag(GREETING_SUBTITLE.get());
		if (!(Nullables.isNullOrEmpty(greeting_title) && Nullables.isNullOrEmpty(greeting_subtitle))) {
			if (greeting_title == null)
				greeting_title = "";
			if (greeting_subtitle == null)
				greeting_subtitle = "";

			Integer title_ticks = event.getRegion().getFlag(TITLE_TICKS.get());
			if (title_ticks == null)
				title_ticks = 200;
			else if (title_ticks < 1)
				title_ticks = 1;

			Integer title_fade = event.getRegion().getFlag(TITLE_FADE.get());
			if (title_fade == null)
				title_fade = 20;
			else if (title_fade < 1)
				title_fade = 1;

			new TitleBuilder().players(player).title(greeting_title).subtitle(greeting_subtitle).fade(title_fade).stay(title_ticks).send();
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onExitRegion(PlayerLeftRegionEvent event) {
		Player player = event.getPlayer();

		World world = WorldGuardUtils.getWorld(event.getRegion());
		if (world != null && !world.equals(player.getWorld()))
			return;

		String farewell_actionbar = event.getRegion().getFlag(FAREWELL_ACTIONBAR.get());
		if (!Nullables.isNullOrEmpty(farewell_actionbar)) {

			Integer actionbar_ticks = event.getRegion().getFlag(ACTIONBAR_TICKS.get());
			if (actionbar_ticks == null)
				actionbar_ticks = 60;
			else if (actionbar_ticks < 1)
				actionbar_ticks = 1;

			ActionBarUtils.sendActionBar(player, farewell_actionbar, actionbar_ticks);
		}

		// Titles
		String farewell_title = event.getRegion().getFlag(FAREWELL_TITLE.get());
		String farewell_subtitle = event.getRegion().getFlag(FAREWELL_SUBTITLE.get());
		if (!(Nullables.isNullOrEmpty(farewell_title) && Nullables.isNullOrEmpty(farewell_subtitle))) {
			if (Nullables.isNullOrEmpty(farewell_title))
				farewell_title = "";
			if (Nullables.isNullOrEmpty(farewell_subtitle))
				farewell_subtitle = "";

			Integer title_ticks = event.getRegion().getFlag(TITLE_TICKS.get());
			if (title_ticks == null)
				title_ticks = 200;
			else if (title_ticks < 1)
				title_ticks = 1;

			Integer title_fade = event.getRegion().getFlag(TITLE_FADE.get());
			if (title_fade == null)
				title_fade = 20;
			else if (title_fade < 1)
				title_fade = 1;

			new TitleBuilder().players(player).title(farewell_title).subtitle(farewell_subtitle).fade(title_fade).stay(title_ticks).send();
		}
	}

	@EventHandler
	public void on(StructureGrowEvent event) {
		if (WorldGuardFlagUtils.query(event.getLocation(), SAPLING_GROWTH) == State.DENY) {
			if (event.isFromBonemeal() && canWorldGuardEdit(event.getPlayer()))
				return;
			event.setCancelled(true);
		}
	}

}

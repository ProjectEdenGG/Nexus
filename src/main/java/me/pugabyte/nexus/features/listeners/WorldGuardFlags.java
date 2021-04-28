package me.pugabyte.nexus.features.listeners;

import com.sk89q.worldguard.protection.flags.StateFlag.State;
import eden.utils.TimeUtils.Time;
import joptsimple.internal.Strings;
import me.pugabyte.nexus.features.commands.staff.WorldGuardEditCommand;
import me.pugabyte.nexus.features.minigames.Minigames;
import me.pugabyte.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import me.pugabyte.nexus.features.regionapi.events.player.PlayerLeftRegionEvent;
import me.pugabyte.nexus.utils.ActionBarUtils;
import me.pugabyte.nexus.utils.BlockUtils;
import me.pugabyte.nexus.utils.EntityUtils;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.TitleUtils;
import me.pugabyte.nexus.utils.WorldGuardFlagUtils;
import me.pugabyte.nexus.utils.WorldGuardUtils;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Farmland;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.MoistureChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static me.pugabyte.nexus.utils.WorldGuardFlagUtils.Flags.*;

public class WorldGuardFlags implements Listener {

	@EventHandler
	public void onItemFrameBreak(HangingBreakEvent event) {
		if (RemoveCause.ENTITY.equals(event.getCause()))
			return;

		if (WorldGuardFlagUtils.query(event.getEntity().getLocation(), HANGING_BREAK) == State.DENY)
			event.setCancelled(true);
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
				} catch (Exception ig) {
				}
			});
			if (entityTypeList.isEmpty()) return;
			if (!entityTypeList.contains(event.getEntityType()))
				event.setCancelled(true);
		} catch (Exception ig) {
		}
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
		if (event.getEntity() instanceof Monster || EntityUtils.getExtraHostileMobs().contains(event.getEntity().getType()))
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
		Tasks.repeat(Time.SECOND, Time.SECOND, () ->
				Minigames.getActiveMinigamers().forEach(minigamer -> {
					if (minigamer.getPlayer().isInWater())
						if (WorldGuardFlagUtils.query(minigamer.getPlayer().getLocation(), MINIGAMES_WATER_DAMAGE) == State.ALLOW)
							minigamer.getPlayer().damage(1.25);
				}));
	}

	@EventHandler
	public void onInteractTrapDoor(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		if (BlockUtils.isNullOrAir(block) || !(MaterialTag.TRAPDOORS.isTagged(block.getType())))
			return;

		if (WorldGuardFlagUtils.query(block, USE_TRAP_DOORS) == State.DENY) {
			if (event.getPlayer().hasPermission(WorldGuardEditCommand.getPermission()))
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
		String greeting_actionbar = (String) event.getRegion().getFlag(GREETING_ACTIONBAR.get());
		if (!Strings.isNullOrEmpty(greeting_actionbar)) {
			Integer actionbar_ticks = (Integer) event.getRegion().getFlag(ACTIONBAR_TICKS.get());
			if (actionbar_ticks == null)
				actionbar_ticks = 60;
			else if (actionbar_ticks < 1)
				actionbar_ticks = 1;

			ActionBarUtils.sendActionBar(player, greeting_actionbar, actionbar_ticks);
		}

		// Titles
		String greeting_title = (String) event.getRegion().getFlag(GREETING_TITLE.get());
		String greeting_subtitle = (String) event.getRegion().getFlag(GREETING_SUBTITLE.get());
		if (!(Strings.isNullOrEmpty(greeting_title) && Strings.isNullOrEmpty(greeting_subtitle))) {
			if (greeting_title == null)
				greeting_title = "";
			if (greeting_subtitle == null)
				greeting_subtitle = "";

			Integer title_ticks = (Integer) event.getRegion().getFlag(TITLE_TICKS.get());
			if (title_ticks == null)
				title_ticks = 200;
			else if (title_ticks < 1)
				title_ticks = 1;

			Integer title_fade = (Integer) event.getRegion().getFlag(TITLE_FADE.get());
			if (title_fade == null)
				title_fade = 20;
			else if (title_fade < 1)
				title_fade = 1;

			TitleUtils.sendTitle(player, greeting_title, greeting_subtitle, title_ticks, title_fade);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onExitRegion(PlayerLeftRegionEvent event) {
		Player player = event.getPlayer();

		World world = WorldGuardUtils.getWorld(event.getRegion());
		if (world != null && !world.equals(player.getWorld()))
			return;

		String farewell_actionbar = (String) event.getRegion().getFlag(FAREWELL_ACTIONBAR.get());
		if (!Strings.isNullOrEmpty(farewell_actionbar)) {

			Integer actionbar_ticks = (Integer) event.getRegion().getFlag(ACTIONBAR_TICKS.get());
			if (actionbar_ticks == null)
				actionbar_ticks = 60;
			else if (actionbar_ticks < 1)
				actionbar_ticks = 1;

			ActionBarUtils.sendActionBar(player, farewell_actionbar, actionbar_ticks);
		}

		// Titles
		String farewell_title = (String) event.getRegion().getFlag(FAREWELL_TITLE.get());
		String farewell_subtitle = (String) event.getRegion().getFlag(FAREWELL_SUBTITLE.get());
		if (!(Strings.isNullOrEmpty(farewell_title) && Strings.isNullOrEmpty(farewell_subtitle))) {
			if (Strings.isNullOrEmpty(farewell_title))
				farewell_title = "";
			if (Strings.isNullOrEmpty(farewell_subtitle))
				farewell_subtitle = "";

			Integer title_ticks = (Integer) event.getRegion().getFlag(TITLE_TICKS.get());
			if (title_ticks == null)
				title_ticks = 200;
			else if (title_ticks < 1)
				title_ticks = 1;

			Integer title_fade = (Integer) event.getRegion().getFlag(TITLE_FADE.get());
			if (title_fade == null)
				title_fade = 20;
			else if (title_fade < 1)
				title_fade = 1;

			TitleUtils.sendTitle(player, farewell_title, farewell_subtitle, title_ticks, title_fade);
		}
	}

}

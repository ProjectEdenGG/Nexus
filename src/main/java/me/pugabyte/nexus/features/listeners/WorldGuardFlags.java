package me.pugabyte.nexus.features.listeners;

import com.sk89q.worldguard.protection.flags.StateFlag.State;
import me.pugabyte.nexus.utils.Utils;
import me.pugabyte.nexus.utils.WorldGuardFlagUtils;
import me.pugabyte.nexus.utils.WorldGuardFlagUtils.Flags;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;

public class WorldGuardFlags implements Listener {

	@EventHandler
	public void onItemFrameBreak(HangingBreakEvent event) {
		if (WorldGuardFlagUtils.query(event.getEntity().getLocation(), Flags.HANGING_BREAK) == State.DENY)
			event.setCancelled(true);
	}

	@EventHandler
	public void onItemFrameBreak(HangingBreakByEntityEvent event) {
		if (WorldGuardFlagUtils.query(event.getEntity().getLocation(), Flags.HANGING_BREAK) == State.DENY)
			event.setCancelled(true);
	}

	@EventHandler
	public void onGrassDecay(BlockFadeEvent event) {
		if (event.getBlock().getType() == Material.GRASS_BLOCK && event.getNewState().getType() == Material.DIRT)
			if (WorldGuardFlagUtils.query(event.getBlock().getLocation(), Flags.GRASS_DECAY) == State.DENY)
				event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (event.getEntity() instanceof Monster || event.getEntity().getType() == EntityType.PHANTOM)
			if (WorldGuardFlagUtils.query(event.getLocation(), Flags.HOSTILE_SPAWN) == State.DENY)
				event.setCancelled(true);
	}

	@EventHandler
	public void onEntityTarget(EntityTargetEvent event) {
		if (event.getTarget() != null)
			if (WorldGuardFlagUtils.query(event.getTarget().getLocation(), Flags.MOB_AGGRESSION) == State.DENY)
				event.setCancelled(true);
	}

	@EventHandler
	public void onEntityTame(EntityTameEvent event) {
		if (WorldGuardFlagUtils.query(event.getEntity().getLocation(), Flags.TAMING) == State.DENY) {
			event.setCancelled(true);
			Utils.send((Player) event.getOwner(), "&c&lHey! &7Sorry, but you can't tame that here.");
		}
	}
}

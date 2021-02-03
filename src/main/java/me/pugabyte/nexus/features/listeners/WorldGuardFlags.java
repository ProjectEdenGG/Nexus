package me.pugabyte.nexus.features.listeners;

import com.sk89q.worldguard.protection.flags.StateFlag.State;
import me.pugabyte.nexus.features.commands.staff.WorldGuardEditCommand;
import me.pugabyte.nexus.features.minigames.Minigames;
import me.pugabyte.nexus.utils.BlockUtils;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Time;
import me.pugabyte.nexus.utils.WorldGuardFlagUtils;
import me.pugabyte.nexus.utils.WorldGuardFlagUtils.Flags;
import org.bukkit.Material;
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

public class WorldGuardFlags implements Listener {

	@EventHandler
	public void onItemFrameBreak(HangingBreakEvent event) {
		if (RemoveCause.ENTITY.equals(event.getCause()))
			return;

		if (WorldGuardFlagUtils.query(event.getEntity().getLocation(), Flags.HANGING_BREAK) == State.DENY)
			event.setCancelled(true);
	}

	@EventHandler
	public void onItemFrameBreak(HangingBreakByEntityEvent event) {
		Entity remover = event.getRemover();
		if (remover instanceof Player)
			return;

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
			PlayerUtils.send((Player) event.getOwner(), "&c&lHey! &7Sorry, but you can't tame that here.");
		}
	}

	static {
		Tasks.repeat(Time.SECOND, Time.SECOND, () ->
				Minigames.getActiveMinigamers().forEach(minigamer -> {
					if (minigamer.getPlayer().isInWater())
						if (WorldGuardFlagUtils.query(minigamer.getPlayer().getLocation(), Flags.MINIGAMES_WATER_DAMAGE) == State.ALLOW)
							minigamer.getPlayer().damage(1.25);
				}));
	}

	@EventHandler
	public void onInteractTrapDoor(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		if (BlockUtils.isNullOrAir(block) || !(MaterialTag.TRAPDOORS.isTagged(block.getType())))
			return;

		if (WorldGuardFlagUtils.query(block, Flags.USE_TRAP_DOORS) == State.DENY) {
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

}

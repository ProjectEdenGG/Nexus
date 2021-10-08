package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.godmode.Godmode;
import gg.projecteden.nexus.models.godmode.GodmodeService;
import gg.projecteden.nexus.models.pvp.PVP;
import gg.projecteden.nexus.models.pvp.PVPService;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGroup;
import gg.projecteden.utils.TimeUtils.TickTime;
import io.papermc.paper.event.player.PlayerBedFailEnterEvent;
import lombok.NoArgsConstructor;
import me.lexikiq.HasPlayer;
import me.lexikiq.event.player.PlayerUseRespawnAnchorEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static gg.projecteden.nexus.utils.PlayerUtils.isVanished;
import static gg.projecteden.nexus.utils.StringUtils.colorize;

@NoArgsConstructor
@Aliases({"spvp", "duel", "fight"})
@Description("Toggle PVP in the survival world")
public class PVPCommand extends CustomCommand implements Listener {
	public final PVPService service = new PVPService();
	private final Map<UUID, Location> bedLocations = new HashMap<>();
	public PVP pvp;

	static {
		Tasks.repeatAsync(5, TickTime.SECOND.x(2), () -> {
			PVPService service = new PVPService();
			for (Player player : OnlinePlayers.getAll()) {
				PVP pvp = service.get(player);
				if (pvp.isEnabled())
					player.sendActionBar(colorize("&cPVP is enabled"));
			}
		});
	}

	public PVPCommand(CommandEvent event) {
		super(event);
		if (isPlayer())
			pvp = service.get(player());
	}

	@Path("[enable]")
	void enable(Boolean enable) {
		if (enable == null)
			enable = !pvp.isEnabled();
		pvp.setEnabled(enable);
		service.save(pvp);

		send(PREFIX + (enable ? "&aEnabled" : "&cDisabled"));
	}

	@Path("keepInventory [enable]")
	@Description("Toggle keeping your inventory and experience when dying in PVP")
	void keepInventory(Boolean enable) {
		if (enable == null)
			enable = !pvp.isKeepInventory();

		pvp.setKeepInventory(enable);
		service.save(pvp);
		send(PREFIX + "Keep inventory on PVP death " + (pvp.isKeepInventory() ? "&aenabled" : "&cdisabled"));
	}

	/**
	 * Processes a player attacking another player. This cancels the event if the fighters aren't eligible to fight.
	 * <br>
	 * Criteria to permit the event: players must both have PVP enabled, be unvanished, be in survival, and not have godmode enabled.
	 * Event will return if the attacker is null, the victim is the attacker, or the attack is outside the survival world.
	 * @param event the originating damage event
	 * @param victim user who is getting attacked
	 * @param attacker user who is attacking
	 */
	public void processAttack(@NotNull Cancellable event, @NotNull PVP victim, @Nullable PVP attacker) {
		if (attacker == null)
			return;
		if (victim.equals(attacker))
			return;
		if (WorldGroup.of(victim) != WorldGroup.SURVIVAL) return;

		// Cancel if both players do not have pvp on
		if (!victim.isEnabled() || !attacker.isEnabled()) {
			event.setCancelled(true);
			return;
		}

		if (isVanished(victim.getOnlinePlayer()) || isVanished(attacker.getOnlinePlayer())) {
			event.setCancelled(true);
			return;
		}

		if (victim.getOnlinePlayer().getGameMode() != GameMode.SURVIVAL || attacker.getOnlinePlayer().getGameMode() != GameMode.SURVIVAL) {
			event.setCancelled(true);
			return;
		}

		GodmodeService godmodeService = new GodmodeService();
		Godmode victimGodmode = godmodeService.get(victim);
		Godmode attackerGodmode = godmodeService.get(attacker);

		if (victimGodmode.isActive() || attackerGodmode.isActive())
			event.setCancelled(true);
	}

	@Nullable
	public PVP getDamageCause(EntityDamageEvent event) {
		if (event == null) return null;
		if (event.getEntity() instanceof Player && ((Player) event.getEntity()).getKiller() != null)
			return service.get(((Player) event.getEntity()).getKiller());

		PVP attacker = null;
		if (event instanceof EntityDamageByEntityEvent entityEvent) {
			if (entityEvent.getDamager() instanceof Player player) {
				attacker = service.get(player);
			} else if (entityEvent.getDamager() instanceof Projectile projectile) {
				if (projectile.getShooter() instanceof Player shooter)
					attacker = service.get(shooter);
			} else if (entityEvent.getDamager() instanceof EnderCrystal crystal) {
				// find last user to damage the end crystal
				EntityDamageEvent crystalDamage = crystal.getLastDamageCause();
				if (crystalDamage == null) return null;
				if (!(crystalDamage instanceof EntityDamageByEntityEvent crystalDamageEvent)) return null;
				Entity damager = crystalDamageEvent.getDamager();
				if (damager instanceof Player)
					attacker = service.get(damager);
				// check if last damager was a projectile shot by a player
				else if (damager instanceof Projectile projectile) {
					if (projectile.getShooter() != null && projectile.getShooter() instanceof Player shooter)
						attacker = service.get(shooter);
				}
			} else if (entityEvent.getDamager() instanceof TNTPrimed tnt) {
				if (tnt.getSource() instanceof Player player)
					attacker = service.get(player);
			} else if (entityEvent.getDamager() instanceof Firework firework) {
				if (firework.getBoostedEntity() != null || firework.getSpawningEntity() == null) {
					event.setCancelled(true);
					return null;
				} else {
					Entity entity = Bukkit.getEntity(firework.getSpawningEntity());
					if (entity instanceof Player)
						attacker = service.get(entity);
				}
			}
		} else if (event instanceof EntityDamageByBlockEvent entityDamageByBlockEvent) {
			Location location = entityDamageByBlockEvent.getLocation();
			if (location == null)
				return null;
			if (event.getCause() != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)
				return null;
			if (!(event.getEntity() instanceof Player player))
				return null;

			for (Map.Entry<UUID, Location> entry : bedLocations.entrySet()) {
				UUID uuid = entry.getKey();
				Location location1 = entry.getValue();

				if (!LocationUtils.blockLocationsEqual(location, location1)) continue;
				if (uuid.equals(player.getUniqueId())) continue;
				attacker = service.get(uuid);
				break;
			}
		}
		return attacker;
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerPVP(EntityDamageByEntityEvent event) {
		if (WorldGroup.of(event.getEntity()) != WorldGroup.SURVIVAL) return;

		if (!(event.getEntity() instanceof Player player)) return;
		PVP victim = service.get(player);
		PVP attacker = getDamageCause(event);

		processAttack(event, victim, attacker);
	}

	private void saveBed(HasPlayer player, Block block) {
		UUID key = player.getPlayer().getUniqueId();
		Location value = block.getLocation();
		bedLocations.put(key, value);
		Tasks.waitAsync(1, () -> bedLocations.remove(key, value));
	}

	@EventHandler
	public void onAnchorExplode(PlayerUseRespawnAnchorEvent event) {
		if (event.getResult() != PlayerUseRespawnAnchorEvent.RespawnAnchorResult.EXPLODE)
			return;
		if (!WorldGroup.SURVIVAL.contains(event.getRespawnAnchor().getWorld())) {
			event.setCancelled(true);
			return;
		}
		saveBed(event.getPlayer(), event.getRespawnAnchor());
	}

	@EventHandler
	public void onBedFailure(PlayerBedFailEnterEvent event) {
		if (event.getFailReason() == PlayerBedFailEnterEvent.FailReason.NOT_POSSIBLE_HERE)
			saveBed(event, event.getBed());
	}

	@EventHandler
	public void onDamageByBlock(EntityDamageByBlockEvent event) {
		processAttack(event, service.get(event.getEntity()), getDamageCause(event));
	}

	@EventHandler
	public void onPotionSplash(PotionSplashEvent event) {
		if (!(event.getPotion().getShooter() instanceof Player attacker))
			return;

		for (LivingEntity affectedEntity : event.getAffectedEntities())
			if (affectedEntity instanceof Player victim)
				processAttack(event, service.get(victim), service.get(attacker));
	}

	@EventHandler
	public void onAreaEffectCloudApply(AreaEffectCloudApplyEvent event) {
		if (!(event.getEntity().getSource() instanceof Player attacker))
			return;

		for (LivingEntity affectedEntity : event.getAffectedEntities())
			if (affectedEntity instanceof Player victim)
				processAttack(event, service.get(victim), service.get(attacker));
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (WorldGroup.of(event.getEntity()) != WorldGroup.SURVIVAL) return;
		if (getDamageCause(event.getEntity().getLastDamageCause()) == null) return;
		PVP victim = service.get(event.getEntity());
		if (!victim.isEnabled()) return;
		// For some reason, spigots PlayerDeathEvent#setKeepInventory() method
		// duplicates the items, and md_5 does not see this as a bug
		// We must clear the drops as well to keep them from duping
		if (victim.isKeepInventory()) {
			event.setKeepInventory(true);
			event.getDrops().clear();
			event.setKeepLevel(true);
			event.setDroppedExp(0);
		} else
			event.setKeepInventory(false);
	}

}

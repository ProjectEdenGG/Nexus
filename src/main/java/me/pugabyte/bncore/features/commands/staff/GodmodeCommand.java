package me.pugabyte.bncore.features.commands.staff;

import com.earth2me.essentials.User;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.annotations.Redirects.Redirect;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.godmode.Godmode;
import me.pugabyte.bncore.models.godmode.GodmodeService;
import org.bukkit.GameMode;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import static me.pugabyte.bncore.utils.Utils.isVanished;

@Aliases("god")
// WorldEdit overriding our alias
@Redirect(from = "/god", to = "/godmode")
@NoArgsConstructor
@Permission("group.staff")
public class GodmodeCommand extends CustomCommand implements Listener {
	private final GodmodeService service = new GodmodeService();

	public GodmodeCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[enable] [player]")
	void run(Boolean enable, @Arg("self") Player player) {
		if (Godmode.getDisabledWorlds().contains(player.getWorld().getName()))
			error("Godmode disabled here");

		Godmode godmode = service.get(player);
		if (enable == null)
			enable = !godmode.isEnabled();

		godmode.setEnabled(enable);
		service.save(godmode);

		if (enable && player.getHealth() != 0) {
			player.setHealth(player.getMaxHealth());
			player.setFoodLevel(20);
		}

		send(player, PREFIX + (enable ? "&aEnabled" : "&cDisabled"));
		if (!isSelf(player))
			send(PREFIX + "Godmode " + (enable ? "&aenabled" : "&cdisabled") + " &3for &e" + player.getName());
	}

	@EventHandler
	public void onJoin(final PlayerJoinEvent event) {
		if (!event.getPlayer().hasPermission("group.staff")) return;
		User user = BNCore.getEssentials().getUser(event.getPlayer().getUniqueId());
		boolean enabled = user.isGodModeEnabledRaw();
		if (enabled) {
			Godmode godmode = new GodmodeService().get(event.getPlayer());
			godmode.setEnabled(true);
			user.setGodModeEnabled(false);
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEntityDamage(final EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			final Player player = (Player) event.getEntity();
			Godmode godmode = new GodmodeService().get(player);
			if (godmode.isEnabled()) {
				player.setFireTicks(0);
				player.setRemainingAir(player.getMaximumAir());
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEntityCombust(final EntityCombustEvent event) {
		if (event.getEntity() instanceof Player) {
			Godmode godmode = new GodmodeService().get((Player) event.getEntity());
			if (godmode.isEnabled())
				event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityCombustByEntity(final EntityCombustByEntityEvent event) {
		if (event.getCombuster() instanceof Arrow && event.getEntity() instanceof Player) {
			Arrow combuster = (Arrow) event.getCombuster();
			if (combuster.getShooter() instanceof Player) {
				Godmode godmode = new GodmodeService().get((Player) event.getEntity());
				if (godmode.isEnabled())
					event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onFoodLevelChange(final FoodLevelChangeEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Godmode godmode = new GodmodeService().get(player);
			if (godmode.isEnabled()) {
				player.setFoodLevel(20);
				player.setSaturation(10);
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPotionSplashEvent(final PotionSplashEvent event) {
		for (LivingEntity entity : event.getAffectedEntities())
			if (entity instanceof Player) {
				Player player = (Player) entity;
				Godmode godmode = new GodmodeService().get(player);
				if (godmode.isEnabled() || player.getGameMode() == GameMode.CREATIVE || isVanished(player))
					event.setIntensity(player, 0f);
			}
	}

}

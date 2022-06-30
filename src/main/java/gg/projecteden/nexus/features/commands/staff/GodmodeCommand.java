package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.godmode.Godmode;
import gg.projecteden.nexus.models.godmode.GodmodeService;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.GameMode;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PotionSplashEvent;

import static gg.projecteden.nexus.utils.PlayerUtils.isVanished;

@Aliases("god")
// WorldEdit overriding our alias
@Redirect(from = "/god", to = "/godmode")
@NoArgsConstructor
@Permission(Group.STAFF)
public class GodmodeCommand extends CustomCommand implements Listener {
	private final GodmodeService service = new GodmodeService();

	public GodmodeCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[enable] [player]")
	void run(Boolean enable, @Arg("self") Godmode godmode) {
		Player player = godmode.getOnlinePlayer();
		if (Godmode.getDisabledWorlds().contains(player.getWorld().getName()))
			error("Godmode disabled here");

		if (enable == null)
			enable = !godmode.isActive();

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

	private boolean hasGodmode(Player player) {
		return new GodmodeService().get(player).isActive();
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEntityDamage(final EntityDamageEvent event) {
		if (event.getEntity() instanceof final Player player)
			if (hasGodmode(player)) {
				player.setFireTicks(0);
				player.setRemainingAir(player.getMaximumAir());
				event.setCancelled(true);
			}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEntityCombust(final EntityCombustEvent event) {
		if (event.getEntity() instanceof Player player)
			if (hasGodmode(player))
				event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityCombustByEntity(final EntityCombustByEntityEvent event) {
		if (event.getCombuster() instanceof Arrow combuster && event.getEntity() instanceof Player player)
			if (combuster.getShooter() instanceof Player)
				if (hasGodmode(player))
					event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onFoodLevelChange(final FoodLevelChangeEvent event) {
		if (event.getEntity() instanceof Player player)
			if (hasGodmode(player)) {
				player.setFoodLevel(20);
				player.setSaturation(10);
				event.setCancelled(true);
			}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPotionSplashEvent(final PotionSplashEvent event) {
		for (LivingEntity entity : event.getAffectedEntities())
			if (entity instanceof Player player)
				if (hasGodmode(player) || player.getGameMode() == GameMode.CREATIVE || isVanished(player))
					event.setIntensity(player, 0f);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEntityTarget(final EntityTargetLivingEntityEvent event) {
		if (event.getEntity().getType() == EntityType.EXPERIENCE_ORB)
			return;

		if (event.getTarget() instanceof Player player)
			if (hasGodmode(player))
				event.setCancelled(true);
	}

}

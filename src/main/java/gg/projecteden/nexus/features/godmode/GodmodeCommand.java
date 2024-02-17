package gg.projecteden.nexus.features.godmode;

import gg.projecteden.nexus.features.commands.staff.CheatsCommand;
import gg.projecteden.nexus.features.godmode.events.GodmodeActivatedEvent;
import gg.projecteden.nexus.features.godmode.events.GodmodeDeactivatedEvent;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.godmode.Godmode;
import gg.projecteden.nexus.models.godmode.GodmodeService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PotionSplashEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static gg.projecteden.nexus.features.vanish.Vanish.isVanished;

@Aliases("god")
@Redirect(from = "/god", to = "/godmode") // WorldEdit overriding our alias
@NoArgsConstructor
@Permission(Group.STAFF)
public class GodmodeCommand extends CustomCommand implements Listener {
	private final GodmodeService service = new GodmodeService();

	public GodmodeCommand(@NonNull CommandEvent event) {
		super(event);
	}

	static {
		final Map<UUID, Boolean> lastKnown = new HashMap<>();

		Tasks.repeat(0, 1, () -> {
			for (Player player : OnlinePlayers.getAll()) {
				final Godmode godmode = Godmode.of(player);

				if (!lastKnown.containsKey(player.getUniqueId())) {
					if (godmode.isActive())
						new GodmodeActivatedEvent(godmode).callEvent();
				} else {
					if (godmode.isActive() != lastKnown.get(player.getUniqueId()))
						if (godmode.isActive())
							new GodmodeActivatedEvent(godmode).callEvent();
						else
							new GodmodeDeactivatedEvent(godmode).callEvent();
				}

				lastKnown.put(player.getUniqueId(), godmode.isActive());
			}
		});
	}

	@Path("[enable] [player]")
	@Description("Toggle god mode, preventing damage and mob targeting")
	void run(Boolean enable, @Arg(value = "self", permission = Group.SENIOR_STAFF) Godmode user) {
		Player player = user.getOnlinePlayer();
		if (Godmode.getDisabledWorlds().contains(WorldGroup.of(player)))
			throw new InvalidInputException("You cannot enable god mode in this world");

		if (enable == null)
			enable = !user.isActive();

		if (enable && !CheatsCommand.canEnableCheats(player))
			throw new InvalidInputException("You cannot enable cheats in this world");

		user.setEnabled(enable);
		service.save(user);

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

		if (!(event.getTarget() instanceof Player player))
			return;

		if (hasGodmode(player))
			event.setCancelled(true);

		else if (!Nerd.of(player).hasMovedAfterTeleport())
			event.setCancelled(true);
	}

	@EventHandler
	public void on(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		if (Nerd.of(player).hasMovedAfterTeleport())
			return;

		event.setCancelled(true);
	}

}

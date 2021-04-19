package me.pugabyte.nexus.features.commands.staff.moderator.justice.add;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.features.chat.Chat.StaticChannel;
import me.pugabyte.nexus.features.commands.staff.moderator.justice.misc._PunishmentCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.freeze.Freeze;
import me.pugabyte.nexus.models.freeze.FreezeService;
import me.pugabyte.nexus.models.punishments.PunishmentType;
import me.pugabyte.nexus.models.punishments.Punishments;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.List;

@NoArgsConstructor
@Permission("group.moderator")
public class FreezeCommand extends _PunishmentCommand implements Listener {

	public FreezeCommand(CommandEvent event) {
		super(event);
	}

	@Path("<players...>")
	void freeze(@Arg(type = Punishments.class) List<Punishments> players) {
		punish(players);
	}

	@Override
	protected PunishmentType getType() {
		return PunishmentType.FREEZE;
	}

	@Path("cleanup")
	void cleanup() {
		send(PREFIX + "Removed &e" + cleanup(world()) + " &3freeze stands.");
	}

	public static int cleanup(World world) {
		int count = 0;
		for (Entity entity : world.getEntitiesByClass(ArmorStand.class)) {
			if (entity.getCustomName() == null)
				continue;

			if (entity.getCustomName().contains("FreezeStand-")) {
				entity.remove();
				count++;
			}
		}
		return count;
	}

	public boolean isFrozen(OfflinePlayer player) {
		return get(player).isFrozen();
	}

	private Freeze get(OfflinePlayer player) {
		return new FreezeService().get(player);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		if (!isFrozen(event.getPlayer())) return;
		Player player = event.getPlayer();
		if (player.getVehicle() != null)
			player.getVehicle().remove();

		String message = "&e" + player.getName() + " &3has logged out while frozen.";
		Chat.broadcastIngame(PREFIX + message, StaticChannel.STAFF);
		Chat.broadcastDiscord(DISCORD_PREFIX + message, StaticChannel.STAFF);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (!isFrozen(event.getPlayer())) return;
		Tasks.wait(5, () -> {
			if (!event.getPlayer().isOnline())
				return;

			get(event.getPlayer()).mount();

			String message = "&e" + event.getPlayer().getName() + " &3has logged in while frozen.";
			Chat.broadcastIngame(PREFIX + message, StaticChannel.STAFF);
			Chat.broadcastDiscord(DISCORD_PREFIX + message, StaticChannel.STAFF);
		});
	}

	@EventHandler
	public void onExitVehicle(VehicleExitEvent event) {
		if (!(event.getExited() instanceof Player)) return;
		Player player = (Player) event.getExited();
		if (!isFrozen(player)) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onExitVehicle(VehicleEnterEvent event) {
		if (!(event.getEntered() instanceof Player)) return;
		Player player = (Player) event.getEntered();
		if (!isFrozen(player)) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onDismount(EntityDismountEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		Player player = (Player) event.getEntity();
		if (!isFrozen(player)) return;
		event.setCancelled(true);
		ArmorStand armorStand = (ArmorStand) event.getDismounted();
		Tasks.wait(1, () -> {
			if (!armorStand.isDead())
				armorStand.addPassenger(player);
		});
	}

	// Players can spam click to enter another vehicle which can cause an internal error when unfreezing.
	// I'm canceling the event but if they spam faster than the event throws it glitches out.
	// The player remains on the armor stand, but the server thinks they are in two separate vehicles.
	// May require packet listening to fix

	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		if (!isFrozen(event.getPlayer())) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		if (!isFrozen(event.getPlayer())) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onPickUp(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		if (!isFrozen((Player) event.getEntity())) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player)) return;
		if (!isFrozen((Player) event.getDamager())) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onTakeDamage(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		if (!isFrozen((Player) event.getEntity())) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onTarget(EntityTargetEvent event) {
		if (!(event.getTarget() instanceof Player)) return;
		if (!isFrozen((Player) event.getTarget())) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onSwapHands(PlayerSwapHandItemsEvent event) {
		if (!isFrozen(event.getPlayer())) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		if (!isFrozen(event.getPlayer())) return;
		if (PlayerUtils.isStaffGroup(event.getPlayer())) return;
		switch (event.getMessage().split(" ")[0]) {
			case "/rules":
			case "/ch":
			case "/chat":
			case "/channel":
			case "/r":
			case "/reply":
			case "/msg":
			case "/pm":
			case "/tell":
			case "/freeze":
			case "/unfreeze":
				return;
			default:
				event.setCancelled(true);
		}
	}

}

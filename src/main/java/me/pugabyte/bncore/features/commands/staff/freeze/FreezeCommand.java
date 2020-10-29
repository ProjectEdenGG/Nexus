package me.pugabyte.bncore.features.commands.staff.freeze;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.features.chat.Chat;
import me.pugabyte.bncore.features.chat.Chat.StaticChannel;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.freeze.Freeze;
import me.pugabyte.bncore.models.freeze.FreezeService;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.Location;
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
@Permission("group.staff")
public class FreezeCommand extends CustomCommand implements Listener {
	private final FreezeService service = new FreezeService();

	public FreezeCommand(CommandEvent event) {
		super(event);
	}

	public boolean isFrozen(Player player) {
		return ((Freeze) new FreezeService().get(player)).isFrozen();
	}

	@Path("cleanup")
	void prune() {
		send(PREFIX + "Removed &e" + cleanup(player().getWorld()) + " &3freeze stands.");
	}

	public static int cleanup(World world) {
		int i = 0;
		for (Entity entity : world.getEntitiesByClass(ArmorStand.class)) {
			if (entity.getCustomName() == null) continue;
			if (entity.getCustomName().contains("FreezeStand-")) {
				entity.remove();
				i++;
			}
		}
		return i;
	}

	@Path("<players...>")
	void freeze(@Arg(type = Player.class) List<Player> players) {
		for (Player player : players) {
			try {
				Freeze freeze = new FreezeService().get(player);
				if (freeze.isFrozen()) {
					if (player.getVehicle() != null && player.getVehicle() instanceof ArmorStand)
						runCommand("unfreeze " + player.getName());
					else
						freezePlayer(player);
					continue;
				}

				freeze.setFrozen(true);
				service.save(freeze);
				freezePlayer(player);

				Chat.broadcastIngame(PREFIX + "&e" + player().getName() + " &3has frozen &e" + player.getName(), StaticChannel.STAFF);
				Chat.broadcastDiscord("**[Freeze]** " + player().getName() + " has frozen " + player.getName(), StaticChannel.STAFF);
				send(player, "&cYou have been frozen! This likely means you are breaking a rule; please pay attention to staff in chat");
			} catch (Exception ex) {
				event.handleException(ex);
			}
		}
	}

	public static void freezePlayer(Player player) {
		if (player.getVehicle() != null)
			player.getVehicle().removePassenger(player);
		Location spawnLoc = player.getLocation().clone().subtract(0, 1, 0);
		ArmorStand armorStand = player.getWorld().spawn(spawnLoc, ArmorStand.class);
		armorStand.setInvulnerable(true);
		armorStand.setVisible(false);
		armorStand.setGravity(false);
		armorStand.setCustomName("FreezeStand-" + player.getUniqueId().toString());
		armorStand.setCustomNameVisible(false);
		armorStand.addPassenger(player);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		if (!isFrozen(event.getPlayer())) return;
		Player player = event.getPlayer();
		if (player.getVehicle() != null)
			player.getVehicle().remove();
		Chat.broadcastIngame(PREFIX + "&e" + player.getName() + " &3has logged out while frozen.", StaticChannel.STAFF);
		Chat.broadcastDiscord("**[Freeze]** " + player.getName() + " &3has logged out while frozen.", StaticChannel.STAFF);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (!isFrozen(event.getPlayer())) return;
		Tasks.wait(5, () -> freezePlayer(event.getPlayer()));
		Chat.broadcastIngame(PREFIX + "&e" + event.getPlayer().getName() + " &3has logged in while frozen.", StaticChannel.STAFF);
		Chat.broadcastDiscord("**[Freeze]** " + event.getPlayer().getName() + " has logged in while frozen.", StaticChannel.STAFF);
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

	//Players can spam click to enter another vehicle which can cause an internal error when unfreezing.
	//I'm canceling the event but if they spam faster than the event throws it glitches out.
	//The player remains on the armor stand, but the server thinks they are in two separate vehicles.
	//May require packet listening to fix

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
		if (event.getPlayer().hasPermission("group.staff")) return;
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

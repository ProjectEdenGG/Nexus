package me.pugabyte.bncore.features.commands.staff.freeze;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.features.discord.Discord;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.List;

@NoArgsConstructor
@Permission("group.staff")
public class FreezeCommand extends CustomCommand implements Listener {
	SettingService service = new SettingService();

	public FreezeCommand(CommandEvent event) {
		super(event);
	}

	public boolean isFrozen(Player player) {
		return new SettingService().get(player, "frozen").getBoolean();
	}

	@Path("cleanup")
	void prune() {
		int i = 0;
		for (Entity entity : player().getWorld().getEntitiesByClass(ArmorStand.class)) {
			if (entity.getCustomName().contains("FreezeStand-")) {
				entity.remove();
				i++;
			}
		}
		send(PREFIX + "Removed &e" + i + " &3freeze stands.");
	}

	@Path("<players...>")
	void freeze(@Arg(type = Player.class) List<Player> players) {
		for (Player player : players) {
			Setting setting = service.get(player, "frozen");
			if (setting.getBoolean()) error("That player is already frozen");
			setting.setBoolean(true);
			service.save(setting);
			freezePlayer(player);
			Utils.mod(PREFIX + "&e" + player().getName() + " &3has frozen &e" + player.getName());
			send(player, "&cYou have been frozen! This likely means you are breaking a rule; please pay attention to staff in chat");
		}
	}

	public void freezePlayer(Player player) {
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
		player.getVehicle().remove();
		Utils.mod(PREFIX + "&e" + player.getName() + " &3has logged out while frozen.");
		Discord.log(PREFIX + player.getName() + " has logged out while frozen.");
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (!isFrozen(event.getPlayer())) return;
		Tasks.wait(5, () -> freezePlayer(event.getPlayer()));
		Utils.mod(PREFIX + "&e" + event.getPlayer().getName() + " &3has logged in while frozen.");
		Discord.log(PREFIX + event.getPlayer().getName() + " has logged in while frozen.");
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
		//event.setCancelled(true); 1.15
		ArmorStand armorStand = (ArmorStand) event.getDismounted();
		armorStand.addPassenger(player);
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
	public void onPickUp(PlayerPickupItemEvent event) {
		if (!isFrozen(event.getPlayer())) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player)) return;
		Player player = (Player) event.getDamager();
		if (!isFrozen(player)) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onTakeDamage(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		Player player = (Player) event.getEntity();
		if (!isFrozen(player)) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onTarget(EntityTargetEvent event) {
		if (!(event.getTarget() instanceof Player)) return;
		Player player = (Player) event.getTarget();
		if (!isFrozen(player)) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		if (!isFrozen(event.getPlayer())) return;
		switch (event.getMessage()) {
			case "/rules":
			case "/ch":
			case "/msg":
			case "/pm":
			case "/freeze":
			case "/sk":
				return;
			default:
				event.setCancelled(true);
		}
	}

	@EventHandler
	public void onSwapHandS(PlayerSwapHandItemsEvent event) {
		if (!isFrozen(event.getPlayer())) return;
		event.setCancelled(true);
	}

}

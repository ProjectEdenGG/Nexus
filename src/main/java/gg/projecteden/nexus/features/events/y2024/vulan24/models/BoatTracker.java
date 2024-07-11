package gg.projecteden.nexus.features.events.y2024.vulan24.models;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2024.vulan24.VuLan24;
import gg.projecteden.nexus.models.vulan24.VuLan24User;
import gg.projecteden.nexus.models.vulan24.VuLan24UserService;
import gg.projecteden.nexus.utils.BoatType;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class BoatTracker implements Listener {

	private static final VuLan24UserService userService = new VuLan24UserService();

	public BoatTracker() {
		Nexus.registerListener(this);
	}

	private static void removeBoat(VuLan24User user, Boat boat) {
		boat.remove();
		user.setBoatUUID(null);
		userService.save(user);

		PlayerUtils.giveItem(user, new ItemStack(user.getBoatType().getBoatMaterial()));
	}

	private static void placeBoat(VuLan24User user, Location location) {
		Entity entity = location.getWorld().spawnEntity(location, EntityType.BOAT);
		Boat boat = (Boat) entity;

		boat.addPassenger(user.getPlayer());
		boat.setBoatType(user.getBoatType().getInternalBoatType());

		user.setBoatUUID(boat.getUniqueId());
		userService.save(user);
	}

	public static void selectBoat(Player player, BoatType boatType) {
		VuLan24User user = userService.get(player);

		ItemStack newBoat = new ItemStack(boatType.getBoatMaterial());
		ItemStack oldBoat = new ItemStack(user.getBoatType().getBoatMaterial());

		if (user.getBoatUUID() != null) {
			Entity entity = Bukkit.getEntity(user.getBoatUUID());
			if (entity != null)
				entity.remove();
		}

		user.setBoatType(boatType);
		userService.save(user);

		PlayerUtils.removeItem(player, oldBoat);
		PlayerUtils.giveItem(user, newBoat);
	}

	//

	@EventHandler
	public void onPlace(PlayerInteractEvent event) {
		if (event.isCancelled())
			return;

		Player player = event.getPlayer();
		if (!VuLan24.get().isAtEvent(player))
			return;

		Block clickedBlock = event.getClickedBlock();
		if (Nullables.isNullOrAir(clickedBlock))
			return;

		ItemStack tool = event.getItem();
		if (Nullables.isNullOrAir(tool))
			return;

		if (!MaterialTag.BOATS.isTagged(tool))
			return;

		event.setCancelled(true);
		VuLan24User user = userService.get(player);

		if (user.getBoatUUID() != null)
			return;

		if (user.getBoatType() != BoatType.from(tool.getType()))
			return;

		tool.subtract();
		placeBoat(user, clickedBlock.getLocation().add(0, 1, 0));
	}

	@EventHandler
	public void onExit(VehicleExitEvent event) {
		if (event.isCancelled())
			return;

		if (!(event.getExited() instanceof Player player))
			return;

		if (!(event.getVehicle() instanceof Boat boat))
			return;

		if (!VuLan24.get().isAtEvent(player))
			return;

		VuLan24User user = userService.get(player);
		UUID boatUUID = user.getBoatUUID();
		if (boatUUID != null) {
			if (!boat.getUniqueId().toString().equalsIgnoreCase(boatUUID.toString()))
				return;
		}

		removeBoat(user, boat);
	}

	@EventHandler
	public void onDamage(VehicleDamageEvent event) {
		if (!(event.getAttacker() instanceof Player player))
			return;

		if (!(event.getVehicle() instanceof Boat boat))
			return;

		if (!VuLan24.get().isAtEvent(player))
			return;

		event.setCancelled(true);
		VuLan24User user = userService.get(player);

		if (!boat.getUniqueId().toString().equalsIgnoreCase(user.getBoatUUID().toString()))
			return;

		removeBoat(user, boat);
	}


}

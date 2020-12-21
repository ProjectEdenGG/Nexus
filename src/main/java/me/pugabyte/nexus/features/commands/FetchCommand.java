package me.pugabyte.nexus.features.commands;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
public class FetchCommand extends CustomCommand implements Listener {
	public static boolean enabled = false;
	public static List<UUID> fetchers = new ArrayList<>();
	public static List<Arrow> arrows = new ArrayList<>();

	public FetchCommand(CommandEvent event) {
		super(event);
	}

	@Path("on")
	void on() {
		if (!enabled) error("Fetch is not currently enabled");
		if (fetchers.contains(player().getUniqueId())) error("You are already playing fetch");
		fetchers.add(player().getUniqueId());
		send(PREFIX + "You are now playing fetch");
	}

	@Path("off")
	void off() {
		if (!enabled) error("Fetch is not currently enabled");
		if (!fetchers.contains(player().getUniqueId())) error("You are not playing fetch");
		fetchers.remove(player().getUniqueId());
		send(PREFIX + "You are no longer playing fetch");
	}

	@Permission("group.staff")
	@Path("enable")
	void enable() {
		if (enabled) error("Fetch is already enabled");
		enabled = true;
		send(PREFIX + "Fetch is now enabled");
	}

	@Permission("group.staff")
	@Path("disable")
	void disable() {
		if (!enabled) error("Fetch is not enabled");
		enabled = false;
		for (UUID uuid : fetchers) {
			send(uuid, PREFIX + "Fetch has been disabled");
		}
		fetchers.clear();
		arrows.clear();
		send(PREFIX + "Fetch is now disabled");
	}

	@Path()
	void usage() {
		send(PREFIX + "&cCorrect usage: on/off" + (PlayerUtils.isStaff(player()) ? "/enable/disable" : ""));
	}

	@EventHandler
	public void onBoneToss(PlayerInteractEvent event) {
		if (!enabled) return;
		if (event.getAction() != Action.RIGHT_CLICK_AIR) return;
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getPlayer().getInventory().getItemInMainHand() == null) return;
		if (event.getPlayer().getInventory().getItemInMainHand().getType() != Material.BONE) return;
		if (!fetchers.contains(event.getPlayer().getUniqueId())) return;
		event.setCancelled(true);
		Arrow arrow = event.getPlayer().launchProjectile(Arrow.class);
		arrow.setVelocity(arrow.getVelocity().multiply(.75));
		arrows.add(arrow);
		event.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.BONE,
				event.getPlayer().getInventory().getItemInMainHand().getAmount() - 1));
	}

	@EventHandler
	public void onArrowHit(ProjectileHitEvent event) {
		if (!enabled) return;
		if (!(event.getEntity() instanceof Arrow)) return;
		Arrow arrow = (Arrow) event.getEntity();
		if (!arrows.contains(arrow)) return;
		arrow.getLocation().getWorld().dropItem(arrow.getLocation(), new ItemStack(Material.BONE));
		arrows.remove(arrow);
		arrow.remove();
	}
}

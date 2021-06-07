package me.pugabyte.nexus.features.justice.activate;

import eden.utils.TimeUtils.Timespan;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.chat.Chat.Broadcast;
import me.pugabyte.nexus.features.chat.Chat.StaticChannel;
import me.pugabyte.nexus.features.chat.commands.ChannelCommand;
import me.pugabyte.nexus.features.chat.commands.MessageCommand;
import me.pugabyte.nexus.features.chat.commands.ReplyCommand;
import me.pugabyte.nexus.features.commands.info.RulesCommand;
import me.pugabyte.nexus.features.justice.misc._PunishmentCommand;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.commands.models.events.CommandRunEvent;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.freeze.Freeze;
import me.pugabyte.nexus.models.freeze.FreezeService;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nerd.Rank;
import me.pugabyte.nexus.models.punishments.PunishmentType;
import me.pugabyte.nexus.models.punishments.Punishments;
import me.pugabyte.nexus.utils.JsonBuilder;
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
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

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

	@Async
	@Path("list [page]")
	void list(@Arg("1") int page) {
		List<Freeze> all = new FreezeService().getAll().stream()
				.filter(Freeze::isFrozen)
				.sorted(Comparator.comparing(freeze -> Nerd.of(freeze).getLastJoin()))
				.collect(Collectors.toList());

		Collections.reverse(all);

		BiFunction<Freeze, String, JsonBuilder> formatter = (freeze, index) ->
				json("&3" + index + " &e" + freeze.getNickname() + " &7- "
						+ Timespan.of(Nerd.of(freeze).getLastJoin()).format() + " ago");

		paginate(all, formatter, "/freeze list", page);
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
		Broadcast.all().channel(StaticChannel.STAFF).prefix("Freeze").message(message).send();
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Tasks.wait(5, () -> {
			if (!event.getPlayer().isOnline())
				return;

			if (!isFrozen(event.getPlayer())) {
				PotionEffect jumpEffect = event.getPlayer().getPotionEffect(PotionEffectType.JUMP);
				if (jumpEffect != null && jumpEffect.getAmplifier() >= 127)
					try {
						new FreezeService().get(event.getPlayer()).unfreeze();
					} catch (InvalidInputException ignore) {}
				return;
			}

			get(event.getPlayer()).mount();

			String message = "&e" + event.getPlayer().getName() + " &3has logged in while frozen.";
			Broadcast.all().channel(StaticChannel.STAFF).prefix("Freeze").message(message).send();
		});
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (!isFrozen(event.getPlayer())) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onExitVehicle(VehicleExitEvent event) {
		if (!(event.getExited() instanceof Player player)) return;
		if (!isFrozen(player)) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onExitVehicle(VehicleEnterEvent event) {
		if (!(event.getEntered() instanceof Player player)) return;
		if (!isFrozen(player)) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onDismount(EntityDismountEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
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
		if (!(event.getEntity() instanceof Player player)) return;
		if (!isFrozen(player)) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player player)) return;
		if (!isFrozen(player)) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onTakeDamage(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (!isFrozen(player)) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onTarget(EntityTargetEvent event) {
		if (!(event.getTarget() instanceof Player player)) return;
		if (!isFrozen(player)) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onSwapHands(PlayerSwapHandItemsEvent event) {
		if (!isFrozen(event.getPlayer())) return;
		event.setCancelled(true);
	}

	private static final List<Class<? extends CustomCommand>> commandWhitelist = Arrays.asList(
			ChannelCommand.class,
			MessageCommand.class,
			ReplyCommand.class,
			RulesCommand.class
	);

	@EventHandler
	public void onCommand(CommandRunEvent event) {
		if (!(event.getSender() instanceof Player player))
			return;

		if (!isFrozen(player))
			return;

		if (Rank.of(player).isStaff())
			return;

		Punishments.of(player).getActiveMute().ifPresent(mute -> {
			if (!commandWhitelist.contains(event.getCommand().getClass()))
				event.setCancelled(true);
		});
	}

}

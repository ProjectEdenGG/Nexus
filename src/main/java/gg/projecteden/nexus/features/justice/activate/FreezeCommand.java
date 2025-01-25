package gg.projecteden.nexus.features.justice.activate;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.features.chat.commands.MessageCommand;
import gg.projecteden.nexus.features.chat.commands.ReplyCommand;
import gg.projecteden.nexus.features.commands.info.RulesCommand;
import gg.projecteden.nexus.features.justice.misc._PunishmentCommand;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.commands.models.events.CommandRunEvent;
import gg.projecteden.nexus.models.freeze.Freeze;
import gg.projecteden.nexus.models.freeze.FreezeService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.punishments.PunishmentType;
import gg.projecteden.nexus.models.punishments.Punishments;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDismountEvent;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Permission(Group.MODERATOR)
public class FreezeCommand extends _PunishmentCommand implements Listener {

	static {
		FreezeService freezeService = new FreezeService();
		Tasks.repeatAsync(0, TimeUtils.TickTime.TICK.x(10), () -> {
			for (Freeze freeze : freezeService.getOnline())
				if (freeze.isFrozen() && !freeze.isInArea())
					Tasks.sync(() -> freeze.getPlayer().teleportAsync(freeze.getLocation()));
		});
	}

	public FreezeCommand(CommandEvent event) {
		super(event);
	}

	@Path("<players(s)>")
	@Description("Freeze a player or players")
	void freeze(@Arg(type = Punishments.class) List<Punishments> players) {
		punish(players);
	}

	@Override
	protected PunishmentType getType() {
		return PunishmentType.FREEZE;
	}

	@Path("cleanup")
	@Description("Delete left over freeze armor stands")
	void cleanup() {
		send(PREFIX + "Removed &e" + cleanup(world()) + " &3freeze stands.");
	}

	@Async
	@Path("list [page]")
	@Description("List frozen players")
	void list(@Arg("1") int page) {
		List<Freeze> all = new FreezeService().getAll().stream()
				.filter(Freeze::isFrozen)
				.sorted(Comparator.<Freeze, LocalDateTime>comparing(freeze -> Nerd.of(freeze).getLastJoin()).reversed())
				.collect(Collectors.toList());

		new Paginator<Freeze>()
			.values(all)
			.formatter((freeze, index) ->
				json(index + " &e" + freeze.getNickname() + " &7- " + Timespan.of(Nerd.of(freeze).getLastJoin()).format() + " ago")
			)
			.command("/freeze list")
			.page(page)
			.send();
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

		String message = "&e" + Nickname.of(player) + " &3has logged out while frozen.";
		Broadcast.staff().prefix("Freeze").message(message).send();
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Tasks.wait(5, () -> {
			if (!event.getPlayer().isOnline())
				return;

			if (!isFrozen(event.getPlayer())) {
				PotionEffect jumpEffect = event.getPlayer().getPotionEffect(PotionEffectType.JUMP_BOOST);
				if (jumpEffect != null && event.getPlayer().getWalkSpeed() == 0)
					if (get(event.getPlayer()).isInArea())
						get(event.getPlayer()).unmount();
				return;
			}

			get(event.getPlayer()).mount();
			get(event.getPlayer()).sendMessage(getPrefix() + "&3You are currently frozen!");

			String message = "&e" + Nickname.of(event.getPlayer()) + " &3has logged in while frozen.";
			Broadcast.staff().prefix("Freeze").message(message).send();
		});
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (event.getTo().getLocation().equals(get(event.getPlayer()).getLocation())) return;
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

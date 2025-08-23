package gg.projecteden.nexus.features.listeners;

import dev.morphia.query.Sort;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.warps.Warps;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.nerd.NBTPlayer;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.NerdService;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.punishments.Punishments;
import gg.projecteden.nexus.utils.Distance;
import gg.projecteden.nexus.utils.Name;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.SubWorldGroup;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NerdListener implements Listener {

	static {
		Tasks.repeatAsync(0, TickTime.MINUTE, () -> {
			for (Nerd recentJoin : new NerdService().getAllSortedByLimit(200, Sort.descending("lastJoin")))
				if (!recentJoin.isOnline() && recentJoin.getNerd().getLastQuit() != null && recentJoin.getLastQuit().isBefore(recentJoin.getLastJoin()))
					recentJoin.setLastQuit(LocalDateTime.now());
		});
	}

	private static final List<UUID> toSpawn = new ArrayList<>();

	@EventHandler
	public void on(AsyncPlayerPreLoginEvent event) {
		new NerdService().edit(event.getUniqueId(), nerd -> {
			nerd.setLastJoin(LocalDateTime.now());
			if (nerd.getName().length() <= 16 && !nerd.getName().equals(event.getName())) {
				Punishments.broadcast("&e" + nerd.getNickname() + " &chas changed their username from &e" + nerd.getName() + " &cto &e" + event.getName());
				nerd.setName(event.getName());
			}
			nerd.getPastNames().add(event.getName());
		});

		try {
			Nerd nerd = Nerd.of(event.getUniqueId());
			World world = new NBTPlayer(nerd).getWorld();
			if (world == null) return;

			if (SubWorldGroup.of(world) == SubWorldGroup.RESOURCE) {
				nerd = Nerd.of(event.getUniqueId());
				if (nerd.getLastQuit().isBefore(YearMonth.now().atDay(1).atStartOfDay()))
					toSpawn.add(event.getUniqueId());
			}
		} catch (InvalidInputException ignore) {}
	}

	@EventHandler
	public void on(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		final Runnable setLoginLocation = () -> new NerdService().edit(player, nerd -> nerd.setLoginLocation(player.getLocation()));

		setLoginLocation.run();

		if (toSpawn.contains(player.getUniqueId())) {
			Warps.survival(player).thenRun(setLoginLocation);
			Nexus.log("Teleporting resource world player " + Nickname.of(player) + " to spawn");
			toSpawn.remove(player.getUniqueId());
		}

		Tasks.wait(5, () -> {
			if (toSpawn.contains(player.getUniqueId())) {
				Warps.survival(player).thenRun(setLoginLocation);
				Nexus.log("Teleporting resource world player " + Nickname.of(player) + " to spawn [2]");
				toSpawn.remove(player.getUniqueId());
			}
		});
	}

	@EventHandler
	public void on(PlayerTeleportEvent event) {
		boolean nearbyTeleport = event.getFrom().getWorld().equals(event.getTo().getWorld()) && Distance.distance(event.getFrom(), event.getTo()).lte(128);

		if (!nearbyTeleport)
			new NerdService().edit(event.getPlayer(), nerd -> nerd.setTeleportLocation(event.getTo()));
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		new NerdService().edit(event.getPlayer(), nerd -> {
			nerd.setLastQuit(LocalDateTime.now());
			nerd.getPastNames().add(Name.of(event.getPlayer()));
			nerd.setLocation(event.getPlayer().getLocation());
		});
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		new NerdService().edit(event.getPlayer(), nerd -> nerd.setLastDeath(nerd.getWorldGroup(), LocalDateTime.now()));
	}

}

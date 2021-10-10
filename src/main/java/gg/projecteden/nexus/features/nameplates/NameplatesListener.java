package gg.projecteden.nexus.features.nameplates;

import de.myzelyam.api.vanish.PlayerVanishStateChangeEvent;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.nameplates.protocol.NameplateManager;
import gg.projecteden.nexus.models.afk.events.AFKEvent;
import gg.projecteden.nexus.utils.LuckPermsUtils.GroupChange.PlayerRankChangeEvent;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.Objects;

public class NameplatesListener implements Listener {

	public NameplatesListener() {
		Nameplates.debug("===== NameplatesListener()");
		Nexus.registerListener(this);
	}

	private static Nameplates nameplates() {
		return Nameplates.get();
	}

	private static NameplateManager manager() {
		return nameplates().getNameplateManager();
	}

	static {
		Tasks.repeatAsync(TickTime.SECOND.x(5), TickTime.SECOND.x(5), () -> manager().spawnAll());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void on(PlayerJoinEvent event) {
		Nameplates.debug("on PlayerJoinEvent(" + event.getPlayer().getName() + ")");
		Player player = event.getPlayer();
		if (nameplates().isManageTeams())
			nameplates().getTeam().addEntry(player.getName());

		Tasks.waitAsync(10, () -> manager().respawn(player));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void on(PlayerQuitEvent event) {
		Nameplates.debug("on PlayerQuitEvent(" + event.getPlayer().getName() + ")");
		manager().removeManagerOf(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(PlayerKickEvent event) {
		Nameplates.debug("on PlayerKickEvent(" + event.getPlayer().getName() + ")");
		manager().removeManagerOf(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void on(PlayerTeleportEvent event) {
		Nameplates.debug("on PlayerTeleportEvent(" + event.getPlayer().getName() + ")");
		if (!event.getFrom().getWorld().equals(event.getTo().getWorld()))
			manager().destroyViewable(event.getPlayer());

		manager().respawn(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void on(PlayerGameModeChangeEvent event) {
		Nameplates.debug("on PlayerGameModeChangeEvent(" + event.getPlayer().getName() + ")");

		if (event.getPlayer().getGameMode() == GameMode.SPECTATOR)
			manager().destroyViewable(event.getPlayer());

		manager().respawn(event.getPlayer());
	}

	@EventHandler
	public void on(PlayerVanishStateChangeEvent event) {
		final Player player = Bukkit.getPlayer(event.getUUID());
		if (player == null || !player.isOnline())
			return;

		Nameplates.debug("on PlayerVanishStateChangeEvent(" + Objects.requireNonNull(Bukkit.getPlayer(event.getUUID())).getName() + ")");
		manager().respawn(Bukkit.getPlayer(event.getUUID()));
	}

	@EventHandler
	public void on(AFKEvent event) {
		Nameplates.debug("on AFKEvent(" + event.getUser().getOnlinePlayer().getName() + ")");
		manager().update(event.getUser().getOnlinePlayer());
	}

	@EventHandler
	public void on(PlayerRankChangeEvent event) {
		final Player player = Bukkit.getPlayer(event.getUuid());
		if (player == null || !player.isOnline())
			return;

		Nameplates.debug("on PlayerRankChangeEvent(" + player.getName() + ")");
		manager().update(player);
	}

	@EventHandler
	public void on(PlayerToggleSneakEvent event) {
		Nameplates.debug("on PlayerToggleSneakEvent(" + event.getPlayer().getName() + ", sneaking=" + event.isSneaking() + ")");
		if (event.isSneaking())
			manager().destroy(event.getPlayer());
		else {
			event.getPlayer().setSneaking(false);
			manager().spawn(event.getPlayer());
		}
	}

}

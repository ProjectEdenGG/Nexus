package gg.projecteden.nexus.features.nameplates;

import de.myzelyam.api.vanish.PlayerVanishStateChangeEvent;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.nameplates.protocol.NameplateManager;
import gg.projecteden.nexus.models.afk.events.AFKEvent;
import gg.projecteden.nexus.utils.LuckPermsUtils.GroupChange.PlayerRankChangeEvent;
import gg.projecteden.nexus.utils.Tasks;
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

import java.util.Objects;

public class NameplatesListener implements Listener {

	public NameplatesListener() {
		System.out.println("===== NameplatesListener()");
		Nexus.registerListener(this);
	}

	private Nameplates nameplates() {
		return Nameplates.get();
	}

	private NameplateManager manager() {
		return nameplates().getNameplateManager();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void on(PlayerJoinEvent event) {
		System.out.println("on PlayerJoinEvent(" + event.getPlayer().getName() + ")");
		Player player = event.getPlayer();
		if (nameplates().isManageTeams())
			nameplates().getTeam().addEntry(player.getName());

		Tasks.waitAsync(10, () -> manager().respawn(player));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void on(PlayerQuitEvent event) {
		System.out.println("on PlayerQuitEvent(" + event.getPlayer().getName() + ")");
		manager().removeManagerOf(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(PlayerKickEvent event) {
		System.out.println("on PlayerKickEvent(" + event.getPlayer().getName() + ")");
		manager().removeManagerOf(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void on(PlayerTeleportEvent event) {
		System.out.println("on PlayerTeleportEvent(" + event.getPlayer().getName() + ")");
		manager().respawn(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void on(PlayerGameModeChangeEvent event) {
		System.out.println("on PlayerGameModeChangeEvent(" + event.getPlayer().getName() + ")");

		if (event.getPlayer().getGameMode() == GameMode.SPECTATOR)
			manager().destroyViewable(event.getPlayer());

		manager().respawn(event.getPlayer());
	}

	@EventHandler
	public void on(PlayerVanishStateChangeEvent event) {
		System.out.println("on PlayerVanishStateChangeEvent(" + Objects.requireNonNull(Bukkit.getPlayer(event.getUUID())).getName() + ")");
		manager().respawn(Bukkit.getPlayer(event.getUUID()));
	}

	@EventHandler
	public void on(AFKEvent event) {
		System.out.println("on AFKEvent(" + event.getUser().getOnlinePlayer().getName() + ")");
		manager().update(event.getUser().getOnlinePlayer());
	}

	@EventHandler
	public void on(PlayerRankChangeEvent event) {
		final Player player = Bukkit.getPlayer(event.getUuid());
		if (player == null || !player.isOnline())
			return;

		System.out.println("on PlayerRankChangeEvent(" + player.getName() + ")");
		manager().update(player);
	}

}

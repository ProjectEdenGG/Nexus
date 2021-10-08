package gg.projecteden.nexus.features.nameplates;

import de.myzelyam.api.vanish.PlayerVanishStateChangeEvent;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.nameplates.protocol.FakeEntityManager;
import gg.projecteden.nexus.models.afk.events.AFKEvent;
import gg.projecteden.nexus.utils.LuckPermsUtils.GroupChange.PlayerRankChangeEvent;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class NameplatesListener implements Listener {

	public NameplatesListener() {
		System.out.println("===== NameplatesListener()");
		Nexus.registerListener(this);
	}

	private Nameplates nameplates() {
		return Nameplates.get();
	}

	private FakeEntityManager fakeEntityManager() {
		return nameplates().getFakeEntityManager();
	}

	public void respawn(Player player) {
		fakeEntityManager().removeFakeEntityAroundPlayer(player);
		Tasks.waitAsync(10, () -> fakeEntityManager().spawnFakeEntityAroundPlayer(player));
	}

	public void update(Player player) {
		fakeEntityManager().updateFakeEntityAroundPlayer(player);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void on(PlayerJoinEvent event) {
		System.out.println("on PlayerJoinEvent(" + event.getPlayer().getName() + ")");
		Player player = event.getPlayer();
		if (nameplates().isManageTeams())
			nameplates().getTeam().addEntry(player.getName());

		Tasks.waitAsync(10, () -> fakeEntityManager().updateFakeEntityAroundPlayer(player));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void on(PlayerQuitEvent event) {
		System.out.println("on PlayerQuitEvent(" + event.getPlayer().getName() + ")");
		Player player = event.getPlayer();
		fakeEntityManager().removeFakeEntityAroundPlayer(player);
		fakeEntityManager().removeManagerOf(player);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(PlayerKickEvent event) {
		System.out.println("on PlayerKickEvent(" + event.getPlayer().getName() + ")");
		Player player = event.getPlayer();
		fakeEntityManager().removeFakeEntityAroundPlayer(player);
		fakeEntityManager().removeManagerOf(player);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void on(PlayerTeleportEvent event) {
		System.out.println("on PlayerTeleportEvent(" + event.getPlayer().getName() + ")");
		respawn(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void on(PlayerGameModeChangeEvent event) {
		System.out.println("on PlayerGameModeChangeEvent(" + event.getPlayer().getName() + ")");
		respawn(event.getPlayer());
	}

	@EventHandler
	public void on(PlayerVanishStateChangeEvent event) {
		System.out.println("on PlayerVanishStateChangeEvent(" + Bukkit.getPlayer(event.getUUID()).getName() + ")");
		respawn(Bukkit.getPlayer(event.getUUID()));
	}

	@EventHandler
	public void on(AFKEvent event) {
		System.out.println("on AFKEvent(" + event.getUser().getPlayer().getName() + ")");
		update(event.getUser().getPlayer());
	}

	@EventHandler
	public void on(PlayerRankChangeEvent event) {
		System.out.println("on PlayerRankChangeEvent(" + Bukkit.getPlayer(event.getUuid()).getName() + ")");
		update(Bukkit.getPlayer(event.getUuid()));
	}
}

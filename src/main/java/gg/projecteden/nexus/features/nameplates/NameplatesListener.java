package gg.projecteden.nexus.features.nameplates;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.resourcepack.models.events.ResourcePackUpdateCompleteEvent;
import gg.projecteden.nexus.features.resourcepack.models.events.ResourcePackUpdateStartEvent;
import gg.projecteden.nexus.framework.features.Features;
import gg.projecteden.nexus.hooks.vanish.VanishHook.VanishStateChangeEvent;
import gg.projecteden.nexus.models.afk.events.AFKEvent;
import gg.projecteden.nexus.utils.LuckPermsUtils.GroupChange.PlayerRankChangeEvent;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import me.libraryaddict.disguise.events.DisguiseEvent;
import me.libraryaddict.disguise.events.UndisguiseEvent;
import net.citizensnpcs.api.event.NPCSpawnEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NameplatesListener implements Listener {

	public NameplatesListener() {
//		Nexus.registerListener(this);
	}

	private static Nameplates nameplates() {
		return Nameplates.get();
	}

	private static NameplateManager manager() {
		return nameplates().getNameplateManager();
	}

	private static final List<Integer> taskIds = new ArrayList<>();

	@EventHandler
	public void on(ResourcePackUpdateStartEvent event) {
		Iterator<Integer> iterator = taskIds.iterator();
		while (iterator.hasNext()) {
			int taskId = iterator.next();
			Tasks.cancel(taskId);
			iterator.remove();
		}
	}

	@EventHandler
	public void on(ResourcePackUpdateCompleteEvent event) {
		taskIds.add(Tasks.repeatAsync(TickTime.SECOND.x(5), TickTime.SECOND.x(5), () -> manager().spawnAll()));
		taskIds.add(Tasks.repeat(0, 10, () -> OnlinePlayers.getAll().forEach(player -> Nameplates.get().updateTeamOf(player))));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void on(PlayerJoinEvent event) {
		Nameplates.debug("on PlayerJoinEvent(" + event.getPlayer().getName() + ")");
		Player player = event.getPlayer();

		Nameplates.get().updateTeamOf(player);
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

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void on(VanishStateChangeEvent event) {
		final Player player = Bukkit.getPlayer(event.getUuid());
		if (player == null || !player.isOnline())
			return;

		Nameplates.debug("on VanishStateChangeEvent(" + player.getName() + ")");
		Nameplates.get().updateTeamOf(player);
		manager().respawn(player);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void on(EntityPotionEffectEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		if (!event.getModifiedType().equals(PotionEffectType.INVISIBILITY))
			return;

		Nameplates.debug("on EntityPotionEffectEvent(" + player.getName() + ", type=" + event.getModifiedType() + ", action=" + event.getAction() + ")");

		switch (event.getAction()) {
			case ADDED -> manager().destroy(player);
			case REMOVED -> manager().spawn(player);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void on(AFKEvent event) {
		Nameplates.debug("on AFKEvent(" + event.getUser().getOnlinePlayer().getName() + ")");
		manager().update(event.getUser().getOnlinePlayer());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void on(PlayerRankChangeEvent event) {
		final Player player = Bukkit.getPlayer(event.getUuid());
		if (player == null || !player.isOnline())
			return;

		Nameplates.debug("on PlayerRankChangeEvent(" + player.getName() + ")");
		manager().update(player);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void on(PlayerToggleSneakEvent event) {
		Nameplates.debug("on PlayerToggleSneakEvent(" + event.getPlayer().getName() + ", sneaking=" + event.isSneaking() + ")");
		if (event.isSneaking())
			manager().destroy(event.getPlayer());
		else
			Tasks.wait(1, () -> manager().spawn(event.getPlayer()));
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void on(DisguiseEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		Nameplates.debug("on DisguiseEvent(" + player.getName() + ")");
		manager().destroy(player);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void on(UndisguiseEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		Nameplates.debug("on UndisguiseEvent(" + player.getName() + ")");
		manager().spawn(player);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void on(PlayerDeathEvent event) {
		Nameplates.debug("on PlayerDeathEvent(" + event.getEntity().getName() + ")");
		manager().destroy(event.getEntity());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void on(PlayerRespawnEvent event) {
		Nameplates.debug("on PlayerRespawnEvent(" + event.getPlayer().getName() + ")");
		manager().spawn(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void on(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		Nameplates.debug("on EntityDamageEvent(" + player.getName() + ")");
		manager().update(player);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void on(EntityRegainHealthEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		Nameplates.debug("on EntityRegainHealthEvent(" + player.getName() + ")");
		manager().update(player);
	}

	@EventHandler
	public void on(NPCSpawnEvent event) {
		Features.get(Nameplates.class).updateTeamOf(event.getNPC());
	}

}

package gg.projecteden.nexus.features.equipment.stattrack;

import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class CommonListeners implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onShootBow(EntityShootBowEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		ItemStack weapon = event.getBow();
		if (weapon == null)
			return;

		UUID statTrackId = StatTrack.getStatTrackId(weapon);
		if (statTrackId == null)
			return;

		Entity projectile = event.getProjectile();

		projectile.getPersistentDataContainer().set(StatTrack.TRACKING_KEY, PersistentDataType.STRING, statTrackId.toString());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onRodCast(PlayerFishEvent event) {
		if (event.getState() != PlayerFishEvent.State.FISHING)
			return;

		Player player = event.getPlayer();
		ItemStack rod = player.getInventory().getItem(event.getHand());

		UUID statTrackId = StatTrack.getStatTrackId(rod);
		if (statTrackId == null)
			return;

		FishHook hook = event.getHook();
		hook.getPersistentDataContainer().set(
			StatTrack.TRACKING_KEY,
			PersistentDataType.STRING,
			statTrackId.toString()
		);
	}

}

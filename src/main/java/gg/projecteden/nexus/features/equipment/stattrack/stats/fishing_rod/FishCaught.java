package gg.projecteden.nexus.features.equipment.stattrack.stats.fishing_rod;

import gg.projecteden.nexus.features.equipment.stattrack.StatTrack;
import gg.projecteden.nexus.features.equipment.stattrack.StatTrackStatistic;
import gg.projecteden.nexus.features.equipment.stattrack.stats.annotations.DisplayName;
import gg.projecteden.nexus.features.equipment.stattrack.stats.annotations.Id;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.Material;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

@Id("fish_caught")
@DisplayName("Fish Caught")
public class FishCaught extends StatTrackStatistic {
	@Override
	public MaterialTag getApplicableTools() {
		return new MaterialTag(Material.FISHING_ROD);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onItemCaught(PlayerFishEvent event) {
		if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH)
			return;

		if (!(event.getCaught() instanceof Item caughtItem))
			return;

		UUID statTrackId = getStatTrackId(event.getHook());
		if (statTrackId == null)
			return;

		ItemStack caught = caughtItem.getItemStack();

		if (MaterialTag.RAW_FISH.isTagged(caught))
			track(statTrackId, caught.getAmount());
	}

	private UUID getStatTrackId(FishHook hook) {
		String storedId = hook.getPersistentDataContainer().get(StatTrack.TRACKING_KEY, PersistentDataType.STRING);

		if (storedId == null)
			return null;

		try { return UUID.fromString(storedId); }
		catch (IllegalArgumentException ignored) { return null; }
	}

}

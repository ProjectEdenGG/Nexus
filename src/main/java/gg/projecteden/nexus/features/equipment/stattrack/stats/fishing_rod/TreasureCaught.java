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

@Id("treasure_caught")
@DisplayName("Treasure Caught")
public class TreasureCaught extends StatTrackStatistic {
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

		if (!MaterialTag.RAW_FISH.isTagged(caught))
			return;

		if (isTrash(caught))
			return;

		track(statTrackId, caught.getAmount());
	}

	private UUID getStatTrackId(FishHook hook) {
		String storedId = hook.getPersistentDataContainer().get(StatTrack.TRACKING_KEY, PersistentDataType.STRING);

		if (storedId == null)
			return null;

		try { return UUID.fromString(storedId); }
		catch (IllegalArgumentException ignored) { return null; }
	}

	private boolean isTrash(ItemStack item) {
		return switch (item.getType()) {
			case LILY_PAD,
			     BOWL,
			     LEATHER,
			     LEATHER_BOOTS,
			     ROTTEN_FLESH,
			     STICK,
			     STRING,
			     POTION,
			     BONE,
			     INK_SAC,
			     TRIPWIRE_HOOK -> true;
			case BOW,
				 FISHING_ROD -> item.getEnchantments().isEmpty();
			default -> false;
		};
	}

}

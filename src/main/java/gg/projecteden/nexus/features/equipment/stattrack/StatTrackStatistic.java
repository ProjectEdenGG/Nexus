package gg.projecteden.nexus.features.equipment.stattrack;

import gg.projecteden.nexus.features.equipment.stattrack.stats.annotations.DisplayName;
import gg.projecteden.nexus.features.equipment.stattrack.stats.annotations.Id;
import gg.projecteden.nexus.models.stattrack.StatTrackItem;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public abstract class StatTrackStatistic implements Listener {

	public abstract MaterialTag getApplicableTools();

	public String getId() {
		if (getClass().isAnnotationPresent(Id.class))
			return getClass().getAnnotation(Id.class).value();
		return StringUtils.camelCaseWithUnderscores(getClass().getSimpleName()).toLowerCase();
	}

	public String getDisplayName() {
		if (getClass().isAnnotationPresent(DisplayName.class))
			return getClass().getAnnotation(DisplayName.class).value();
		return getClass().getSimpleName().replaceAll("(?<=[a-z])(?=[A-Z])", " "); // BlocksBroken -> Blocks Broken
	}

	public void track(ItemStack tool) {
		this.track(tool, 1);
	}

	public void track(ItemStack tool, double amount) {
		if (!canTrack(tool)) return;
		UUID uuid = StatTrack.getStatTrackId(tool);
		if (uuid == null) return;

		track(uuid, amount);
	}

	public void track(UUID uuid, double amount) {
		StatTrackItem item = StatTrack.SERVICE.get(uuid);
		double value = item.getValues().getOrDefault(getId(), 0.0) + amount;
		item.getValues().put(getId(), value);
		StatTrack.SERVICE.save(item);
	}

	public boolean canTrack(ItemStack item) {
		return getApplicableTools().isTagged(item);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof StatTrackStatistic o)) return false;
		return o.getId().equalsIgnoreCase(this.getId());
	}
}

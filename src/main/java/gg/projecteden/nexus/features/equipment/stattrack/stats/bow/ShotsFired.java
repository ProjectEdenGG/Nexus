package gg.projecteden.nexus.features.equipment.stattrack.stats.bow;

import gg.projecteden.nexus.features.equipment.stattrack.StatTrack;
import gg.projecteden.nexus.features.equipment.stattrack.StatTrackStatistic;
import gg.projecteden.nexus.features.equipment.stattrack.stats.annotations.DisplayName;
import gg.projecteden.nexus.features.equipment.stattrack.stats.annotations.Id;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Id("shots_fired")
@DisplayName("Shots Fired")
public class ShotsFired extends StatTrackStatistic {

	@Override
	public MaterialTag getApplicableTools() {
		return new MaterialTag(Material.BOW, Material.CROSSBOW);
	}

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

		if (weapon.getEnchantments().containsKey(Enchantment.MULTISHOT))
			track(weapon, 3);
		else
			track(weapon);
	}

}

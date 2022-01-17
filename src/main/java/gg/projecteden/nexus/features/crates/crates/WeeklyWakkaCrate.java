package gg.projecteden.nexus.features.crates.crates;

import gg.projecteden.nexus.features.crates.models.Crate;
import gg.projecteden.nexus.features.crates.models.CrateType;
import gg.projecteden.nexus.features.crates.models.events.CrateSpawnItemEvent;
import lombok.NoArgsConstructor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class WeeklyWakkaCrate extends Crate {

	@Override
	public CrateType getCrateType() {
		return CrateType.WEEKLY_WAKKA;
	}

	@Override
	public List<String> getCrateHologramLines() {
		return new ArrayList<>() {{
			add("&3&l--=[+]=--");
			add("&3[+] &e&lWeekly Wakka Crate &3[+]");
			add("&3Find &eWakka's NPC &3around");
			add("&3spawn to claim a Key");
			add("&3&l--=[+]=--");
		}};
	}

	@Override
	public Location getHologramLocation() {
		Location loc = super.getHologramLocation();
		return loc.add(0, .3, 0);
	}

	Color brown = Color.fromRGB(82, 53, 5);
	int time = 0;
	Color returnColor = brown;

	@Override
	public Color[] getBandColors() {
		if (time++ == 500) {
			if (returnColor == brown) returnColor = Color.WHITE;
			else returnColor = brown;
			time = 0;
		}
		return new Color[]{returnColor, returnColor};
	}

	@EventHandler
	public void onSpawnItem(CrateSpawnItemEvent event) {
		if (event.getCrateType() != getCrateType()) return;
		this.time = 0;
	}

	@Override
	public void playFinalSound(Location location) {
		location.getWorld().playSound(location, Sound.ENTITY_COW_AMBIENT, SoundCategory.MASTER, 1f, .1f);
	}
}

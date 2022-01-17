package gg.projecteden.nexus.features.crates.crates;

import gg.projecteden.nexus.features.crates.models.Crate;
import gg.projecteden.nexus.features.crates.models.CrateLoot;
import gg.projecteden.nexus.features.crates.models.CrateType;
import gg.projecteden.nexus.features.crates.models.events.CrateSpawnItemEvent;
import org.bukkit.Color;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

public class Pugmas21Crate extends Crate {
	@Override
	public CrateType getCrateType() {
		return CrateType.PUGMAS_21;
	}

	@Override
	public List<String> getCrateHologramLines() {
		return new ArrayList<>() {{
			add("&3&l--=[+]=--");
			add("&3[+] &a&lPugmas 21 Crate &3[+]");
			add("&3&l--=[+]=--");
		}};
	}

	@Override
	public Color[] getBandColors() {
		return new Color[]{Color.RED, Color.LIME};
	}

	@EventHandler
	public void onItemSpawn(CrateSpawnItemEvent event) {
		CrateLoot loot = event.getCrateLoot();
		if (getCrateType().equals(event.getCrateType()) && loot.getTitle().equalsIgnoreCase("Gift Cards!")) {
			broadcastLoot(player, loot);
		}
	}
}

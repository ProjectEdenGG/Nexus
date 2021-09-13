package gg.projecteden.nexus.features.crates.crates;

import gg.projecteden.nexus.features.crates.models.Crate;
import gg.projecteden.nexus.features.crates.models.CrateLoot;
import gg.projecteden.nexus.features.crates.models.CrateType;
import gg.projecteden.nexus.features.crates.models.events.CrateSpawnItemEvent;
import lombok.NoArgsConstructor;
import org.bukkit.Color;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class VoteCrate extends Crate {

	@Override
	public CrateType getCrateType() {
		return CrateType.VOTE;
	}

	@Override
	public List<String> getCrateHologramLines() {
		return new ArrayList<>() {{
			add("&3&l--=[+]=--");
			add("&3[+] &e&lVote Crate &3[+]");
			add("&3&l--=[+]=--");
		}};
	}

	@Override
	public Color[] getBandColors() {
		return new Color[]{Color.WHITE, Color.WHITE};
	}

	@EventHandler
	public void onItemSpawn(CrateSpawnItemEvent event) {
		CrateLoot loot = event.getCrateLoot();
		if (getCrateType().equals(event.getCrateType()) && loot.getItems().contains(CrateType.MYSTERY.getKey())) {
			broadcastLoot(player, loot);
		}
	}
}

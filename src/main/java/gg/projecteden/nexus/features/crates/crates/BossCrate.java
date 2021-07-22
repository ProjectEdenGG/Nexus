package gg.projecteden.nexus.features.crates.crates;

import gg.projecteden.nexus.features.crates.models.Crate;
import gg.projecteden.nexus.features.crates.models.CrateType;
import gg.projecteden.nexus.models.nerd.Rank;

import java.util.ArrayList;
import java.util.List;

public class BossCrate extends Crate {

	@Override
	public CrateType getCrateType() {
		return CrateType.BOSS;
	}

	@Override
	public List<String> getCrateHologramLines() {
		return new ArrayList<>() {{
			add("&f&l--=[+]=--");
			add("&f&k[+] " + Rank.OWNER.getChatColor() + "&lBoss Crate &f&k[+]");
			add("&f&l--=[+]=--");
			add("&cComing Soon...");
		}};
	}
}

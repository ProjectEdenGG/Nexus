package me.pugabyte.nexus.features.crates.crates;

import me.pugabyte.nexus.features.crates.models.Crate;
import me.pugabyte.nexus.features.crates.models.CrateType;
import me.pugabyte.nexus.models.nerd.Rank;

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

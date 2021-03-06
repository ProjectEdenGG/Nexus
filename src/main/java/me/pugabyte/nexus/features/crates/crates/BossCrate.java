package me.pugabyte.nexus.features.crates.crates;

import me.pugabyte.nexus.features.crates.models.Crate;
import me.pugabyte.nexus.features.crates.models.CrateType;

import java.util.ArrayList;
import java.util.List;

public class BossCrate extends Crate {

	@Override
	public CrateType getCrateType() {
		return CrateType.BOSS;
	}

	@Override
	public List<String> getCrateHologramLines() {
		return new ArrayList<String>() {{
			add("&8&l--&k=&f&l[+]=&k-&f&l-");
			add("&f&k[+] &8&lBoss Crate &f&k[+]");
			add("&f&l-&k-=&f&l[+&k]&f&l=--");
			add("&cComing Soon...");
		}};
	}
}

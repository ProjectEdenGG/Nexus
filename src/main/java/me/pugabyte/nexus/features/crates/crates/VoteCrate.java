package me.pugabyte.nexus.features.crates.crates;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.crates.models.Crate;
import me.pugabyte.nexus.features.crates.models.CrateType;
import org.bukkit.Color;

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
		return new ArrayList<String>() {{
			add("&3&l--=[+]=--");
			add("&3[+] &e&lVote Crate &3[+]");
			add("&3&l--=[+]=--");
		}};
	}

	@Override
	public Color[] getBandColors() {
		return new Color[]{Color.WHITE, Color.WHITE};
	}
}

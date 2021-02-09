package me.pugabyte.nexus.features.crates.crates;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.crates.models.Crate;
import me.pugabyte.nexus.features.crates.models.CrateType;
import me.pugabyte.nexus.utils.ColorType;
import org.bukkit.Color;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class MysteryCrate extends Crate {

	@Override
	public CrateType getCrateType() {
		return CrateType.MYSTERY;
	}

	@Override
	public List<String> getCrateHologramLines() {
		return new ArrayList<String>() {{
			add("&3&l--=[+]=--");
			add("&3[+] &e&lMystery Crate &3[+]");
			add("&3|                      |");
			add("&3Use a &eCrate Key &3to claim rewards");
			add("&3&l--=[+]=--");
		}};
	}

	@Override
	public Color[] getBandColors() {
		return new Color[]{ColorType.CYAN.getColor(), Color.YELLOW};
	}
}

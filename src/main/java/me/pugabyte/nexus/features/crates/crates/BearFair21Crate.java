package me.pugabyte.nexus.features.crates.crates;

import me.pugabyte.nexus.features.crates.models.Crate;
import me.pugabyte.nexus.features.crates.models.CrateType;
import me.pugabyte.nexus.utils.ColorType;
import org.bukkit.Color;

import java.util.ArrayList;
import java.util.List;

public class BearFair21Crate extends Crate {
	@Override
	public CrateType getCrateType() {
		return CrateType.BEAR_FAIR_21;
	}

	@Override
	public List<String> getCrateHologramLines() {
		return new ArrayList<>() {{
			add("&f&l--=[+]=--");
			add("&f[+] &lBear Fair 21 Crate &f[+]");
			add("&f&l--=[+]=--");
		}};
	}

	@Override
	public Color[] getBandColors() {
		return new Color[]{ColorType.YELLOW.getBukkitColor(), ColorType.CYAN.getBukkitColor()};
	}
}

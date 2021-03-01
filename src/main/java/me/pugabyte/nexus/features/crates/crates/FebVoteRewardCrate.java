package me.pugabyte.nexus.features.crates.crates;

import com.destroystokyo.paper.ParticleBuilder;
import me.pugabyte.nexus.features.crates.models.Crate;
import me.pugabyte.nexus.features.crates.models.CrateType;
import org.bukkit.Location;
import org.bukkit.Particle;

import java.util.ArrayList;
import java.util.List;

public class FebVoteRewardCrate extends Crate {
	@Override
	public CrateType getCrateType() {
		return CrateType.FEBRUARY_VOTE_REWARD;
	}

	@Override
	public List<String> getCrateHologramLines() {
		return new ArrayList<String>() {{
			add("&c&l--=[+]=--");
			add("&c[+] &6&lFeb. Vote Goal Crate &c[+]");
			add("&c&l--=[+]=--");
		}};
	}

	@Override
	public Particle getParticleType() {
		return Particle.FLAME;
	}

	@Override
	public void playFinalParticle(Location location) {
		for (int i = 0; i < 5; i++)
			new ParticleBuilder(Particle.LAVA)
					.count(25)
					.location(location)
					.spawn();
	}
}

package gg.projecteden.nexus.features.wither.fights;

import gg.projecteden.nexus.features.wither.WitherChallenge;
import gg.projecteden.nexus.features.wither.models.WitherFight;
import gg.projecteden.nexus.utils.RandomUtils;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Wither;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@NoArgsConstructor
public class EasyFight extends WitherFight {

	@Override
	public WitherChallenge.Difficulty getDifficulty() {
		return WitherChallenge.Difficulty.EASY;
	}

	@Override
	public void spawnWither(Location location) {
		this.wither = location.getWorld().spawn(location, Wither.class, SpawnReason.NATURAL);
	}

	@Override
	public boolean shouldGiveStar() {
		return RandomUtils.chanceOf(12.5);
	}

	@Override
	public List<ItemStack> getAlternateDrops() {
		return null;
	}
}

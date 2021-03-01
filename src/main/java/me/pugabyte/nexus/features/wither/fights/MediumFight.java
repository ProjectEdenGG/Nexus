package me.pugabyte.nexus.features.wither.fights;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.wither.WitherChallenge;
import me.pugabyte.nexus.features.wither.models.WitherFight;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@NoArgsConstructor
public class MediumFight extends WitherFight {
	@Override
	public WitherChallenge.Difficulty getDifficulty() {
		return WitherChallenge.Difficulty.MEDIUM;
	}

	@Override
	public void spawnWither(Location location) {
	}

	@Override
	public boolean shouldGiveStar() {
		return false;
	}

	@Override
	public List<ItemStack> getAlternateDrops() {
		return null;
	}
}

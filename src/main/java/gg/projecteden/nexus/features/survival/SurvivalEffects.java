package gg.projecteden.nexus.features.survival;

import gg.projecteden.nexus.features.effects.Effects;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class SurvivalEffects extends Effects {

	@Override
	public World getWorld() {
		return Bukkit.getWorld("survival");
	}

	// TODO
}

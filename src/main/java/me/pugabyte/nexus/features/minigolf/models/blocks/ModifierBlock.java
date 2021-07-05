package me.pugabyte.nexus.features.minigolf.models.blocks;

import me.pugabyte.nexus.features.minigolf.models.GolfBall;
import org.bukkit.Material;

import java.util.Set;

public abstract class ModifierBlock {

	public abstract void handle(GolfBall golfball);

	public abstract Set<Material> getMaterials();

}

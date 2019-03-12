package me.pugabyte.bncore.features.oldminigames.quake;

import au.com.mineauz.minigames.MinigamePlayer;
import org.bukkit.Material;

public abstract class Weapon {
	protected MinigamePlayer player;
	private String name;
	private Material material;
	private String[] lore;

	public abstract Weapon clone();

	Material getMaterial() {
		return material;
	}

	void setMaterial(Material material) {
		this.material = material;
	}

	String getName() {
		return name;
	}

	void setName(String name) {
		this.name = name;
	}

	public MinigamePlayer getPlayer() {
		return player;
	}

	public void setPlayer(MinigamePlayer player) {
		this.player = player;
	}

	String[] getLore() {
		return lore;
	}

	void setLore(String[] lore) {
		this.lore = lore;
	}

}
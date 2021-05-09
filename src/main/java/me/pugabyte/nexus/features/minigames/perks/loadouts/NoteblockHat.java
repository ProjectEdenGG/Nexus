package me.pugabyte.nexus.features.minigames.perks.loadouts;

import me.pugabyte.nexus.features.minigames.models.perks.common.LoadoutPerk;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class NoteblockHat extends LoadoutPerk {
	@Override
	public String getName() {
		return "Note Block";
	}

	@Override
	public @NotNull String getDescription() {
		return "Sing a nice tune with this note block hat";
	}

	@Override
	public int getPrice() {
		return 10;
	}

	@Override
	public Material getMaterial() {
		return Material.NOTE_BLOCK;
	}
}

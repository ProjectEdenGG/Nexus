package me.pugabyte.nexus.features.minigames.perks.loadouts;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.events.y2021.pride21.Flags;
import me.pugabyte.nexus.features.minigames.models.perks.common.HatPerk;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class FlagHat implements HatPerk {
	private final @Getter Flags flag;

	@Override
	public @NotNull String getName() {
		return getFlag().toString();
	}

	@Override
	public int getPrice() {
		return 10;
	}

	@Override
	public @NotNull String getDescription() {
		return "Show off your " + getFlag() + " pride with this flag";
	}

	@Override
	public @NotNull ItemStack getItem() {
		return getFlag().getFlag();
	}
}

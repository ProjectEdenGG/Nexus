package gg.projecteden.nexus.features.minigames.perks.loadouts;

import gg.projecteden.nexus.features.events.y2021.pride21.Flags;
import gg.projecteden.nexus.features.minigames.models.perks.PerkCategory;
import gg.projecteden.nexus.features.minigames.models.perks.common.HatPerk;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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

	@Override
	public @NotNull PerkCategory getPerkCategory() {
		return PerkCategory.PRIDE_FLAG_HAT;
	}
}

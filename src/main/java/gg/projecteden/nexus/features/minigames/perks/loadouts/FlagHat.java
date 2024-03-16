package gg.projecteden.nexus.features.minigames.perks.loadouts;

import gg.projecteden.nexus.features.minigames.models.perks.PerkCategory;
import gg.projecteden.nexus.features.minigames.models.perks.common.HatPerk;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Flag.PrideFlagType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class FlagHat implements HatPerk {
	private final @Getter PrideFlagType flagType;

	@Override
	public @NotNull String getName() {
		return getFlagType().toString();
	}

	@Override
	public int getPrice() {
		return 10;
	}

	@Override
	public @NotNull List<String> getDescription() {
		return Collections.singletonList("Show off your " + getFlagType() + " pride with this flag");
	}

	@Override
	public @NotNull ItemStack getItem() {
		return getFlagType().getFlagItem();
	}

	@Override
	public @NotNull PerkCategory getPerkCategory() {
		return PerkCategory.PRIDE_FLAG_HAT;
	}
}

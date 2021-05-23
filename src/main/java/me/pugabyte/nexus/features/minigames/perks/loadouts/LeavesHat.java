package me.pugabyte.nexus.features.minigames.perks.loadouts;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.minigames.models.perks.common.HatMaterialPerk;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import static eden.utils.StringUtils.camelCase;

@Getter
@RequiredArgsConstructor
public class LeavesHat implements HatMaterialPerk {
	private final Material material;

	@Override
	public @NotNull String getName() {
		return camelCase(getMaterial());
	}

	@Override
	public @NotNull String getDescription() {
		return "Wear the leaves of a real tree";
	}

	@Override
	public int getPrice() {
		return 5;
	}
}

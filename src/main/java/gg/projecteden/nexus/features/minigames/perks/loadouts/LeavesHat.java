package gg.projecteden.nexus.features.minigames.perks.loadouts;

import gg.projecteden.nexus.features.minigames.models.perks.common.HatMaterialPerk;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import static gg.projecteden.utils.StringUtils.camelCase;

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

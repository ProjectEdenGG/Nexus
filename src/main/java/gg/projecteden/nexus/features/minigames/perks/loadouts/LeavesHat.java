package gg.projecteden.nexus.features.minigames.perks.loadouts;

import gg.projecteden.nexus.features.minigames.models.perks.common.HatMaterialPerk;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import static gg.projecteden.nexus.utils.StringUtils.camelCase;

@Getter
@RequiredArgsConstructor
public class LeavesHat implements HatMaterialPerk {
	private final Material material;

	@Override
	public @NotNull String getName() {
		return camelCase(getMaterial());
	}

	@Override
	public @NotNull List<String> getDescription() {
		return Collections.singletonList("Wear the leaves of a real tree");
	}

	@Override
	public int getPrice() {
		return 5;
	}
}

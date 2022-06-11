package gg.projecteden.nexus.features.minigames.perks.loadouts.teamed;

import gg.projecteden.nexus.features.minigames.models.perks.common.TeamHatMaterialPerk;
import gg.projecteden.nexus.utils.ColorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public class TeamHatMaterialImpl implements TeamHatMaterialPerk {
	private final @NotNull String name;
	private final int price;
	private final @NotNull List<String> description;
	private final @NotNull Function<ColorType, Material> colorMaterial;

	public TeamHatMaterialImpl(@NotNull String name, int price, @NotNull String description, @NotNull Function<ColorType, Material> colorMaterial) {
		this(name, price, Collections.singletonList(description), colorMaterial);
	}

	@Override
	public @NotNull Material getColorMaterial(ColorType color) {
		return colorMaterial.apply(color);
	}
}

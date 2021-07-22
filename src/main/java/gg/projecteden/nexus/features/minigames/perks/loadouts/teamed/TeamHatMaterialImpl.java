package gg.projecteden.nexus.features.minigames.perks.loadouts.teamed;

import gg.projecteden.nexus.features.minigames.models.perks.common.TeamHatMaterialPerk;
import gg.projecteden.nexus.utils.ColorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public class TeamHatMaterialImpl implements TeamHatMaterialPerk {
	private final String name;
	private final int price;
	private final String description;
	private final Function<ColorType, Material> colorMaterial;

	@Override
	public Material getColorMaterial(ColorType color) {
		return colorMaterial.apply(color);
	}
}

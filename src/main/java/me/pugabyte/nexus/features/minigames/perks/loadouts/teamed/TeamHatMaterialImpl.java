package me.pugabyte.nexus.features.minigames.perks.loadouts.teamed;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.minigames.models.perks.common.TeamHatMaterialPerk;
import me.pugabyte.nexus.utils.ColorType;
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

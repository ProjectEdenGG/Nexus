package gg.projecteden.nexus.features.resourcepack.models;

import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.AllArgsConstructor;
import org.bukkit.Color;

// ordinal == shader color - file must be in same order as texture

@AllArgsConstructor
public enum CustomArmorType {
	WITHER("armor/wither"),
	WARDEN("armor/warden"),
	BERSERKER("armor/berserker"),
	BROWN_BERSERK("armor/brown_berserk"),
	COPPER("armor/copper"),
	DAMASCUS("armor/damascus"),
	DRUID("armor/druid"),
	HELLFIRE("armor/hellfire"),
	JARL("armor/jarl"),
	MYTHRIL("armor/mythril"),
	TANK("armor/tank"),
	THOR("armor/thor"),
	WIZARD("armor/wizard"),
	WOLF("armor/wolf"),
	FISHING("armor/fishing"),
	;

	private final String id;

	public String getId(PlayerUtils.ArmorSlot slot) {
		return id + "/" + slot.name().toLowerCase();
	}

	public Color getShaderDyeColor() {
		return Color.fromRGB(ordinal() + 1);
	}

}

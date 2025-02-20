package gg.projecteden.nexus.features.resourcepack.models;

import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.AllArgsConstructor;
import org.bukkit.Color;

// ordinal == shader color - file must be in same order as texture

@AllArgsConstructor
public enum CustomArmorType {
	WITHER("skins/armor/wither"),
	WARDEN("skins/armor/warden"),
	BERSERKER("skins/armor/berserker"),
	BROWN_BERSERK("skins/armor/brown_berserk"),
	COPPER("skins/armor/copper"),
	COBALT("skins/armor/cobalt"),
	DRUID("skins/armor/druid"),
	HELLFIRE("skins/armor/hellfire"),
	JARL("skins/armor/jarl"),
	MYTHRIL("skins/armor/mythril"),
	TANK("skins/armor/tank"),
	THOR("skins/armor/thor"),
	WIZARD("skins/armor/wizard"),
	WOLF("skins/armor/wolf"),
	FISHING("skins/armor/fishing"),
	;

	private final String id;

	public String getId(PlayerUtils.ArmorSlot slot) {
		return id + "/" + slot.name().toLowerCase();
	}

	public Color getShaderDyeColor() {
		return Color.fromRGB(ordinal() + 1);
	}

}

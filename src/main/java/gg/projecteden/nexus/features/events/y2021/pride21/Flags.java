package gg.projecteden.nexus.features.events.y2021.pride21;

import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.inventory.ItemStack;

import static gg.projecteden.nexus.features.resourcepack.models.CustomMaterial.PRIDE_BUNTING_BASE;
import static gg.projecteden.nexus.features.resourcepack.models.CustomMaterial.PRIDE_FLAG_BASE;

public enum Flags {
	ACE,
	AGENDER,
	ARO,
	BI,
	DEMI,
	DEMIBOY,
	DEMIGIRL,
	DEMIROMANTIC,
	GAY,
	GENDERFLUID,
	GENDERFLUX,
	GENDERQUEER,
	GRAY_ACE,
	GRAY_ARO,
	INTERSEX,
	LESBIAN,
	NONBINARY,
	PAN,
	POLYAM,
	POLYSEX,
	TRANS,
	TRANSFEM,
	TRANSMASC,
	QUEER;

	@Override
	public String toString() {
		return StringUtils.camelCase(this);
	}

	public ItemStack getFlag() {
		return new ItemBuilder(PRIDE_FLAG_BASE).modelId(PRIDE_FLAG_BASE.getModelId() + ordinal()).name("&e" + this +" Flag").build();
	}

	public ItemStack getBunting() {
		return new ItemBuilder(PRIDE_BUNTING_BASE).modelId(PRIDE_BUNTING_BASE.getModelId() + ordinal()).name("&e" + this +" Bunting").build();
	}
}

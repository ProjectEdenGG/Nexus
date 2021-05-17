package me.pugabyte.nexus.features.events.y2021.pride21;

import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

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
		return new ItemBuilder(Material.WHITE_BANNER).customModelData(101 + ordinal()).name("&e" + this +" Flag").build();
	}

	public ItemStack getBunting() {
		return new ItemBuilder(Material.WHITE_BANNER).customModelData(201 + ordinal()).name("&e" + this +" Bunting").build();
	}
}

package me.pugabyte.nexus.features.minigames.models.sabotage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.interfaces.ColoredAndNamed;
import me.pugabyte.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static eden.utils.StringUtils.camelCase;

@RequiredArgsConstructor
public enum SabotageColor implements ColoredAndNamed {
	// TODO: more colors
	BLACK("40038", 0x2c3236),
	BLUE("40040", 0x0d2193),
	BROWN("40030", 0x503315),
	CYAN("40031", 0x28b199),
	GREEN("40041", 0x0d5920),
	LIME("40032", 0x38a728),
	ORANGE("40033", 0xec7c0e),
	PINK("40034", 0xea52b7),
	PURPLE("40035", 0x4b2284),
	RED("40042", 0xc31111),
	WHITE("40036", 0xdddddd),
	YELLOW("40039", 0xf2f257)
	;

	@Override
	public String toString() {
		return camelCase(name());
	}

	@Override
	public @NotNull String getName() {
		return toString();
	}

	private @NotNull final String headID;

	private @NotNull @Getter final Color color;

	SabotageColor(String headID, int r, int g, int b) {
		this(headID, new Color(r, g, b));
	}

	SabotageColor(String headID, int hex) {
		this(headID, new Color(hex));
	}

	public ItemStack getHead() {
		return Nexus.getHeadAPI().getItemHead(headID);
	}

	public @NotNull ItemStack getChest() {
		return new ItemBuilder(Material.LEATHER_CHESTPLATE).armorColor(this).build();
	}

	public @NotNull ItemStack getLegs() {
		return new ItemBuilder(Material.LEATHER_LEGGINGS).armorColor(this).build();
	}

	public @NotNull ItemStack getBoots() {
		return new ItemBuilder(Material.LEATHER_BOOTS).armorColor(this).build();
	}
}

package gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.interfaces.Colored;
import gg.projecteden.nexus.framework.interfaces.IsColoredAndNamed;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public enum SabotageColor implements IsColoredAndNamed {
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
		return StringUtils.camelCase(name());
	}

	@Override
	public @NotNull String getName() {
		return toString();
	}

	private @NotNull final String headID;
	private @NotNull @Getter @Accessors(fluent = true) final Colored colored;

	SabotageColor(String headID, int r, int g, int b) {
		this(headID, Colored.of(r, g, b));
	}

	SabotageColor(String headID, int hex) {
		this(headID, Colored.of(hex));
	}

	public ItemStack getHead() {
		return Nexus.getHeadAPI().getItemHead(headID);
	}

	public @NotNull ItemStack getChest() {
		return new ItemBuilder(Material.LEATHER_CHESTPLATE).dyeColor(colored).build();
	}

	public @NotNull ItemStack getLegs() {
		return new ItemBuilder(Material.LEATHER_LEGGINGS).dyeColor(colored).build();
	}

	public @NotNull ItemStack getBoots() {
		return new ItemBuilder(Material.LEATHER_BOOTS).dyeColor(colored).build();
	}
}

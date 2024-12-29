package gg.projecteden.nexus.features.minigames.perks.loadouts;

import gg.projecteden.nexus.features.minigames.models.perks.common.HatMaterialPerk;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.LanguageUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Getter;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

@Getter
public class HatMaterialImpl implements HatMaterialPerk {
	private final Material material;
	private final String name;
	private final int price;
	private final List<String> description;
	private final boolean customName;

	public HatMaterialImpl(Material material, String name, int price, String description) {
		this(material, name, price, Collections.singletonList(description));
	}

	public HatMaterialImpl(Material material, String name, int price, List<String> description) {
		this.material = material;
		this.name = name;
		this.price = price;
		this.description = description;
		customName = true;
	}

	public HatMaterialImpl(Material material, int price, String description) {
		this(material, price, Collections.singletonList(description));
	}

	public HatMaterialImpl(Material material, int price, List<String> description) {
		this.price = price;
		this.description = description;
		this.material = material;
		String name;
		try {
			name = LanguageUtils.translate(material);
		} catch (NullPointerException e) { // ensure compatibility with tests (catches NPE from Bukkit.getServer())
			name = StringUtils.camelCase(material.name());
		}
		this.name = name;
		customName = false;
	}

	@Override
	public @NotNull ItemStack getItem() {
		ItemBuilder builder = new ItemBuilder(getMaterial());
		if (customName)
			builder.name(new JsonBuilder(name).decorate(false, TextDecoration.ITALIC));
		return builder.build();
	}
}

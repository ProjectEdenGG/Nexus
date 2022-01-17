package gg.projecteden.nexus.features.minigames.perks.loadouts;

import gg.projecteden.nexus.features.minigames.models.perks.common.HatPerk;
import gg.projecteden.nexus.utils.AdventureUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import static gg.projecteden.nexus.utils.StringUtils.camelCase;

@Getter
@RequiredArgsConstructor
public class HatImpl implements HatPerk {
	private final ItemStack item;
	private final String name;
	private final int price;
	private final String description;

	public HatImpl(ItemStack item, int price, String description) {
		this.price = price;
		this.description = description;
		this.item = item;

		String name = null;
		try {
			if (item.hasItemMeta()) {
				ItemMeta meta = item.getItemMeta();
				if (meta.hasDisplayName())
					name = AdventureUtils.asPlainText(meta.displayName());
				else if (meta.hasLocalizedName())
					name = meta.getLocalizedName();
			}
			if (name == null)
				name = item.getI18NDisplayName();
		} catch (NullPointerException e) { // ensure compatibility with tests (catches NPE from Bukkit.getServer())
			name = camelCase(item.getType().name());
		}
		this.name = name;
	}

	@Override
	public @NotNull ItemStack getItem() {
		return item.clone();
	}
}

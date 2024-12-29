package gg.projecteden.nexus.features.minigames.menus;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.minigames.menus.perks.BuyPerksMenu;
import gg.projecteden.nexus.features.minigames.menus.perks.CategoryMenu;
import gg.projecteden.nexus.features.minigames.menus.perks.PlayerPerksMenu;
import gg.projecteden.nexus.models.perkowner.PerkOwner;
import gg.projecteden.nexus.models.perkowner.PerkOwnerService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Material;

import java.text.DecimalFormat;

@Rows(3)
@Title("Minigame Collectibles")
public class PerkMenu extends InventoryProvider {
	private static final DecimalFormat FORMATTER = new DecimalFormat("#,###");

	@Override
	public void init() {
		ClickableItem yourPerks = ClickableItem.of(new ItemBuilder(Material.PLAYER_HEAD).skullOwner(viewer).name("&6&lYour Perks").build(), e -> new CategoryMenu<>(PlayerPerksMenu.class).open(viewer));
		ClickableItem buyPerks = ClickableItem.of(new ItemBuilder(Material.EMERALD).name("&a&lBuy Perks").build(), e -> new CategoryMenu<>(BuyPerksMenu.class).open(viewer));
		contents.set(SlotPos.of(1, 3), yourPerks);
		contents.set(SlotPos.of(1, 5), buyPerks);

		PerkOwner perkOwner = new PerkOwnerService().get(viewer);
		contents.set(0, 8, ClickableItem.empty(new ItemBuilder(Material.EMERALD).name("&2&lBalance")
			.lore("&f" + FORMATTER.format(perkOwner.getTokens()) + StringUtils.plural(" token", perkOwner.getTokens()))
			.build()));
	}
}

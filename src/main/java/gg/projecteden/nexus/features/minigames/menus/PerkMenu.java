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
import org.bukkit.Material;

import java.text.DecimalFormat;

import static gg.projecteden.nexus.utils.StringUtils.plural;

@Rows(3)
@Title("Minigame Collectibles")
public class PerkMenu extends InventoryProvider {
	private static final DecimalFormat FORMATTER = new DecimalFormat("#,###");

	@Override
	public void init() {
		ClickableItem yourPerks = ClickableItem.of(new ItemBuilder(Material.PLAYER_HEAD).skullOwner(player).name("&6&lYour Perks").build(), e -> new CategoryMenu<>(PlayerPerksMenu.class).open(player));
		ClickableItem buyPerks = ClickableItem.of(new ItemBuilder(Material.EMERALD).name("&a&lBuy Perks").build(), e -> new CategoryMenu<>(BuyPerksMenu.class).open(player));
		contents.set(SlotPos.of(1, 3), yourPerks);
		contents.set(SlotPos.of(1, 5), buyPerks);

		PerkOwner perkOwner = new PerkOwnerService().get(player);
		contents.set(0, 8, ClickableItem.empty(new ItemBuilder(Material.EMERALD).name("&2&lBalance")
			.lore("&f" + FORMATTER.format(perkOwner.getTokens()) + plural(" token", perkOwner.getTokens()))
			.build()));
	}
}

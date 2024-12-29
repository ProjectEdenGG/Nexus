package gg.projecteden.nexus.features.minigames.menus.perks;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.minigames.models.perks.Perk;
import gg.projecteden.nexus.features.minigames.models.perks.PerkCategory;
import gg.projecteden.nexus.features.minigames.models.perks.PerkType;
import gg.projecteden.nexus.models.perkowner.PerkOwner;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Title("Purchase Collectibles")
public class BuyPerksMenu extends CommonPerksMenu {
	private static final DecimalFormat FORMATTER = new DecimalFormat("#,###");

	public BuyPerksMenu(PerkCategory category) {
		super(category);
	}

	@Override
	protected int getRows(Integer page) {
		return Math.max(3, MenuUtils.calculateRows(PerkType.getByCategory(category).size(), 1));
	}

	@Override
	public void init() {
		addBackItem($ -> new CategoryMenu<>(getClass()).open(viewer));

		PerkOwner perkOwner = service.get(viewer);

		contents.set(0, 8, ClickableItem.empty(new ItemBuilder(Material.EMERALD).name("&2&lBalance")
				.lore("&f"+FORMATTER.format(perkOwner.getTokens()) + StringUtils.plural(" token", perkOwner.getTokens()))
				.build()));

		// get perks and sort them
		List<PerkSortWrapper> perkSortWrappers = new ArrayList<>();
		PerkType.getByCategory(category).forEach(perkType -> perkSortWrappers.add(PerkSortWrapper.of(perkOwner, perkType)));
		perkSortWrappers.sort(Comparator.comparing(PerkSortWrapper::isOwned).thenComparing(PerkSortWrapper::getPrice).thenComparing(PerkSortWrapper::getName));
		List<PerkType> perks = perkSortWrappers.stream().map(PerkSortWrapper::getPerkType).collect(Collectors.toList());

		// create the items
		paginator().items(new ArrayList<ClickableItem>() {{
			perks.forEach(perkType -> {
				Perk perk = perkType.getPerk();
				boolean userOwned = perkOwner.getPurchasedPerks().containsKey(perkType);

				List<String> lore = getLore(viewer, perk);
				lore.add(1, userOwned ? "&cPurchased" : ("&aPurchase for &e" + perk.getPrice() + "&a " + StringUtils.plural("token", perk.getPrice())));
				if (lore.size() > 2)
					lore.add(2, "");

				ItemBuilder item = getItem(perk, lore);
				add(ClickableItem.of(item, e -> buyItem(viewer, perkType, contents)));
			});
		}}).build();
	}

	protected void buyItem(Player player, PerkType perkType, InventoryContents contents) {
		Perk perk = perkType.getPerk();
		PerkOwner perkOwner = service.get(player);
		if (perkOwner.getPurchasedPerks().containsKey(perkType))
			error(player, "You already own that item");
		else if (perkOwner.purchase(perkType)) {
			send(player, "You purchased the &e"+perk.getName()+"&3 collectible for &e"+perk.getPrice()+ StringUtils.plural(" token", perk.getPrice()));
			open(player, contents.pagination().getPage());
		} else
			error(player, "You don't have enough tokens to purchase that");
	}

	protected static void error(Player player, String message) {
		send(player, "&c"+message);
		new SoundBuilder(Sound.ENTITY_VILLAGER_NO).receiver(player).category(SoundCategory.VOICE).volume(0.8).play();
	}
}

package gg.projecteden.nexus.features.minigames.menus.perks;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.models.perks.Perk;
import gg.projecteden.nexus.features.minigames.models.perks.PerkCategory;
import gg.projecteden.nexus.features.minigames.models.perks.PerkType;
import gg.projecteden.nexus.models.perkowner.PerkOwner;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerPerksMenu extends CommonPerksMenu implements InventoryProvider {
	public PlayerPerksMenu(PerkCategory category) {
		super(category);
	}

	@Override
	public void open(Player player, int page) {
		PerkOwner perkOwner = service.get(player);
		SmartInventory.builder()
				.provider(this)
				.title("Your Collectibles")
				.rows(Math.max(3, getRows(perkOwner.getPurchasedPerkTypesByCategory(category).size(), 1)))
				.build()
				.open(player, page);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, $ -> new CategoryMenu<>(getClass()).open(player));

		PerkOwner perkOwner = service.get(player);

		// get perks and sort them
		List<PerkSortWrapper> perkSortWrappers = new ArrayList<>();
		perkOwner.getPurchasedPerkTypesByCategory(category).forEach(perkType -> perkSortWrappers.add(new PerkSortWrapper(true, perkType)));
		perkSortWrappers.sort(Comparator.comparing(PerkSortWrapper::getPrice).thenComparing(PerkSortWrapper::getName));
		List<PerkType> perks = perkSortWrappers.stream().map(PerkSortWrapper::getPerkType).collect(Collectors.toList());

		List<ClickableItem> clickableItems = new ArrayList<>();
		perks.forEach(perkType -> {
			boolean enabled = perkOwner.getPurchasedPerks().get(perkType);
			Perk perk = perkType.getPerk();

			List<String> lore = getLore(player, perk);
			lore.add(1, enabled ? "&aEnabled" : "&cDisabled");
			// insert whitespace
			if (lore.size() > 2)
				lore.add(2, "");

			ItemStack item = getItem(perk, lore);
			if (enabled)
				addGlowing(item);

			clickableItems.add(ClickableItem.of(item, e -> toggleBoolean(player, perkType, contents)));
		});
		paginator(player, contents, clickableItems);
	}

	protected void toggleBoolean(Player player, PerkType perkType, InventoryContents contents) {
		PerkOwner perkOwner = service.get(player);
		perkOwner.toggle(perkType);
		open(player, contents.pagination().getPage());
	}
}

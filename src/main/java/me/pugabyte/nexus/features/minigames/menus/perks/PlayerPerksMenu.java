package me.pugabyte.nexus.features.minigames.menus.perks;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.nexus.features.minigames.menus.PerkMenu;
import me.pugabyte.nexus.features.minigames.models.perks.Perk;
import me.pugabyte.nexus.features.minigames.models.perks.PerkOwner;
import me.pugabyte.nexus.features.minigames.models.perks.PerkType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class PlayerPerksMenu extends CommonPerksMenu implements InventoryProvider {
	@Override
	public void open(Player viewer, int page) {
		PerkOwner perkOwner = service.get(viewer);
		SmartInventory.builder()
				.provider(this)
				.title("Your Collectibles")
				.size(Math.max(3, getRows(perkOwner.getPurchasedPerks().size(), 2)), 9)
				.build()
				.open(viewer, page);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, $ -> new PerkMenu().open(player));

		PerkOwner perkOwner = service.get(player);
		List<ClickableItem> clickableItems = new ArrayList<>();
		perkOwner.getPurchasedPerks().forEach((perkType, enabled) -> {
			Perk perk = perkType.getPerk();
			List<String> lore = getLore(perk);
			lore.add(1, enabled ? "&aEnabled" : "&cDisabled"); // TODO: glowing
			// insert whitespace
			if (lore.size() > 2)
				lore.add(2, "");

			ItemStack item = getItem(perk, lore);
			clickableItems.add(ClickableItem.from(item, e -> toggleBoolean(player, perkType)));
		});
		addPagination(player, contents, clickableItems);
	}

	protected void toggleBoolean(Player player, PerkType perkType) {
		PerkOwner perkOwner = service.get(player);
		Map<PerkType, Boolean> perkTypes = perkOwner.getPurchasedPerks();
		boolean setTo = !perkTypes.get(perkType);
		// disable other perk types if this is being enabled and this is part of an exclusive perk category
		if (setTo && perkType.getPerk().getCategory().isExclusive())
			(new HashSet<>(perkTypes.keySet())).stream().filter(otherType -> otherType.getPerk().getCategory() == perkType.getPerk().getCategory()).forEach(otherType -> perkTypes.put(otherType, false));

		perkTypes.put(perkType, setTo);
		service.save(perkOwner);
		open(player); // TODO: can i get the page?
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {
	}
}

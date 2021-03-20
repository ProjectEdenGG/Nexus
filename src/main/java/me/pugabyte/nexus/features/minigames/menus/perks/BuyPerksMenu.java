package me.pugabyte.nexus.features.minigames.menus.perks;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.minigames.menus.PerkMenu;
import me.pugabyte.nexus.features.minigames.models.perks.Perk;
import me.pugabyte.nexus.features.minigames.models.perks.PerkOwner;
import me.pugabyte.nexus.features.minigames.models.perks.PerkType;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.SoundUtils;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

public class BuyPerksMenu extends CommonPerksMenu implements InventoryProvider {
	@Override
	public void open(Player viewer, int page) {
		PerkOwner perkOwner = service.get(viewer);
		SmartInventory.builder()
				.provider(this)
				.title("Purchase Collectibles")
				.size(getRows(PerkType.values().length, 2), 9)
				.build()
				.open(viewer, page);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, $ -> new PerkMenu().open(player));

		PerkOwner perkOwner = service.get(player);

		contents.set(0, 8, ClickableItem.empty(new ItemBuilder(Material.EMERALD).name("&2&lBalance")
				.lore("&f"+perkOwner.getTokens()) // TODO: properly format item lore
				.build()));

		LinkedHashSet<PerkType> playerPerks = sortPerks(perkOwner.getPurchasedPerks().keySet());
		LinkedHashSet<PerkType> unownedPerks = sortPerks(Arrays.stream(PerkType.values()).filter(perkType -> !playerPerks.contains(perkType)).collect(Collectors.toSet()));
		LinkedHashSet<PerkType> allPerks = new LinkedHashSet<>(unownedPerks);
		allPerks.addAll(playerPerks);

		List<ClickableItem> clickableItems = new ArrayList<>();
		allPerks.forEach(perkType -> {
			Perk perk = perkType.getPerk();
			boolean userOwned = playerPerks.contains(perkType);

			List<String> lore = getLore(perk);
			lore.add(1, userOwned ? "&cPurchased" : ("&aPurchase for &e" + perk.getPrice() + "&a " + StringUtils.plural("point", perk.getPrice())));
			if (lore.size() > 2)
				lore.add(2, "");

			ItemStack item = getItem(perk, lore);
			clickableItems.add(ClickableItem.from(item, e -> buyItem(player, perkType)));
			Nexus.severe("a");
		});
		addPagination(player, contents, clickableItems);
	}

	protected void buyItem(Player player, PerkType perkType) {
		Perk perk = perkType.getPerk();
		PerkOwner perkOwner = service.get(player);
		if (perkOwner.getPurchasedPerks().containsKey(perkType)) {
			send(player, "&cYou already own that item");
			SoundUtils.playSound(player, Sound.ENTITY_VILLAGER_NO, SoundCategory.VOICE, 0.8f, 1.0f);
		}
		else if (perkOwner.getTokens() >= perk.getPrice()) {
			perkOwner.setTokens(perkOwner.getTokens() - perk.getPrice());
			perkOwner.getPurchasedPerks().put(perkType, false);
			service.save(perkOwner);
			send(player, "You purchased the &e"+perk.getName()+"&3 collectible for &e"+perk.getPrice()+StringUtils.plural(" token", perk.getPrice()));
			open(player);
		} else {
			send(player, "&cYou don't have enough tokens to purchase that");
			SoundUtils.playSound(player, Sound.ENTITY_VILLAGER_NO, SoundCategory.VOICE, 0.8f, 1.0f);
		}
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {
	}
}

package me.pugabyte.bncore.features.votes.vps;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.votes.vps.VPSMenu.VPSPage;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class VPSProvider extends MenuUtils implements InventoryProvider {
	private VPSPage page;

	public VPSProvider(VPSPage page) {
		this.page = page;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		int balance = 0; // TODO

		addCloseItem(contents);
		contents.set(0, 8, ClickableItem.empty(new ItemBuilder(Material.BOOK).name("&3You have &e" + balance + " &3vote points").build()));

		VPSMenu menu = VPS.getMenu(player);
		int index = menu.indexOf(page) + 1;
		if (!menu.isFirst(page))
			contents.set(5, 0, ClickableItem.from(new ItemBuilder(Material.PAPER).amount(index - 1).name("&6<-").build(), e ->
					VPS.open(player, menu, index - 1)));
		if (!menu.isLast(page))
			contents.set(5, 8, ClickableItem.from(new ItemBuilder(Material.PAPER).amount(index + 1).name("&6->").build(), e ->
					VPS.open(player, menu, index + 1)));

		page.getItems().forEach((slot, item) -> {
			ItemStack display = item.getDisplay().clone();
			if (item.getPrice() > 0)
				// TODO if can afford
				ItemBuilder.addLore(display, "", "&6Price: &e" + item.getPrice());

			contents.set(slot, ClickableItem.from(display, e -> {
				// TODO if (!canAfford)

				// TODO take points

				if (item.getMoney() > 0)
					Utils.runConsoleCommand("eco give " + player.getName() + " " + item.getMoney());
				if (item.getConsoleCommand() != null && item.getConsoleCommand().length() > 0)
					Utils.runConsoleCommand(item.getConsoleCommand().replaceAll("\\[player]", player.getName()));
				if (item.getCommand() != null && item.getCommand().length() > 0)
					Utils.runCommand(player, item.getCommand().replaceAll("\\[player]", player.getName()));
				if (item.getItems() != null && item.getItems().size() > 0)
					Utils.giveItems(player, item.getItems());

				if (item.isClose())
					player.closeInventory();
			}));
		});
	}

	@Override
	public void update(Player player, InventoryContents contents) {}

}

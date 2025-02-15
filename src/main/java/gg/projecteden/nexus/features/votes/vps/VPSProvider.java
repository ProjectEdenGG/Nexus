package gg.projecteden.nexus.features.votes.vps;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.features.votes.vps.VPSMenu.VPSPage;
import gg.projecteden.nexus.features.votes.vps.VPSMenu.VPSPage.VPSSlot;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.models.voter.Voter;
import gg.projecteden.nexus.models.voter.VoterService;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.IOUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Title("&3Vote Point Store")
public class VPSProvider extends InventoryProvider {
	private final VPSMenu menu;
	private final VPSPage page;
	private final int index;

	public VPSProvider(VPSMenu menu, VPSPage page) {
		this.menu = menu;
		this.page = page;
		this.index = menu.indexOf(page) + 1;
	}

	@Override
	protected int getRows(Integer page) {
		return menu.getPage(page == null ? 1 : page).getRows();
	}

	@Override
	public void init() {
		VoterService service = new VoterService();
		Voter voter = service.get(viewer);

		addCloseItem();
		addPagination(contents, viewer);
		contents.set(0, 8, ClickableItem.empty(new ItemBuilder(Material.BOOK).name("&3You have &e" + voter.getPoints() + " &3vote points").build()));

		page.getItems().forEach((slot, item) -> {
			ItemBuilder display = new ItemBuilder(item.getDisplay());
			if (item.getPrice() > 0)
				if (voter.getPoints() >= item.getPrice())
					display.lore("", "&6Price: &e" + item.getPrice());
				else
					display.lore("", "&6Price: &c" + item.getPrice());

			contents.set(slot, ClickableItem.of(display, e -> {
				if (voter.getPoints() < item.getPrice()) {
					PlayerUtils.send(viewer, VPS.PREFIX + "&cYou do not have enough vote points! &3Use &c/vote &3to vote!");
					return;
				}

				if (item.getOnPurchase() != null)
					if (!item.getOnPurchase().test(viewer, item))
						return;

				if (item.getMoney() > 0)
					new BankerService().deposit(viewer, item.getMoney(), ShopGroup.of(viewer), TransactionCause.VOTE_POINT_STORE);
				if (item.getConsoleCommand() != null && item.getConsoleCommand().length() > 0)
					PlayerUtils.runCommandAsConsole(item.getConsoleCommand().replaceAll("\\[player]", viewer.getName()));
				if (item.getCommand() != null && item.getCommand().length() > 0)
					PlayerUtils.runCommand(viewer, item.getCommand().replaceAll("\\[player]", viewer.getName()));
				if (item.getItems() != null && item.getItems().size() > 0)
					PlayerUtils.giveItems(viewer, item.getItems());

				if (item.getPrice() > 0) {
					voter.takePoints(item.getPrice());
					service.save(voter);
					PlayerUtils.send(viewer, VPS.PREFIX + "You spent &e" + item.getPrice() + StringUtils.plural(" &3point", item.getPrice())
						+ " on &e" + StringUtils.stripColor(item.getName()) + "&3. &e" + voter.getPoints() + " &3points remaining.");
				}

				log(viewer, item);

				if (item.isClose())
					viewer.closeInventory();
				else
					VPS.open(viewer, menu, index);
			}));
		});
	}

	public void addPagination(InventoryContents contents, Player player) {
		if (!menu.isFirst(page)) {
			ItemStack back = new ItemBuilder(ItemModelType.GUI_ARROW_LEFT).dyeColor(ColorType.CYAN).itemFlags(ItemFlag.HIDE_DYE).name("&fPrevious Page").build();
			contents.set(5, 0, ClickableItem.of(back, e -> VPS.open(player, menu, index - 1)));
		}
		if (!menu.isLast(page)) {
			ItemStack forward = new ItemBuilder(ItemModelType.GUI_ARROW_RIGHT).dyeColor(ColorType.CYAN).itemFlags(ItemFlag.HIDE_DYE).name("&fNext Page").build();
			contents.set(5, 8, ClickableItem.of(forward, e -> VPS.open(player, menu, index + 1)));
		}
	}

	public void log(Player player, VPSSlot vpsSlot) {
		List<String> columns = new ArrayList<>(Arrays.asList(
				DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()),
				player.getUniqueId().toString(),
				player.getName(),
				vpsSlot.getName(),
				String.valueOf(vpsSlot.getPrice())
		));

		IOUtils.csvAppend("vps", String.join(",", columns));
	}

}

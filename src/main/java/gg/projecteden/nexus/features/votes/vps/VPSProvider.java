package gg.projecteden.nexus.features.votes.vps;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.votes.vps.VPSMenu.VPSPage;
import gg.projecteden.nexus.features.votes.vps.VPSMenu.VPSPage.VPSSlot;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.models.voter.Voter;
import gg.projecteden.nexus.models.voter.VoterService;
import gg.projecteden.nexus.utils.IOUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static gg.projecteden.nexus.features.votes.vps.VPS.PREFIX;
import static gg.projecteden.nexus.utils.StringUtils.plural;
import static gg.projecteden.nexus.utils.StringUtils.stripColor;

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
	public void open(Player player, int page) {
		VPSPage vpsPage = menu.getPage(page);
		SmartInventory.builder()
			.provider(this)
			.title(ChatColor.DARK_AQUA + "Vote Point Store")
			.rows(vpsPage.getRows())
			.build()
			.open(player, page);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		VoterService service = new VoterService();
		Voter voter = service.get(player);

		addCloseItem(contents);
		addPagination(contents, player);
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
					PlayerUtils.send(player, PREFIX + "&cYou do not have enough vote points! &3Use &c/vote &3to vote!");
					return;
				}

				if (item.getOnPurchase() != null)
					if (!item.getOnPurchase().test(player, item))
						return;

				if (item.getMoney() > 0)
					new BankerService().deposit(player, item.getMoney(), ShopGroup.of(player), TransactionCause.VOTE_POINT_STORE);
				if (item.getConsoleCommand() != null && item.getConsoleCommand().length() > 0)
					PlayerUtils.runCommandAsConsole(item.getConsoleCommand().replaceAll("\\[player]", player.getName()));
				if (item.getCommand() != null && item.getCommand().length() > 0)
					PlayerUtils.runCommand(player, item.getCommand().replaceAll("\\[player]", player.getName()));
				if (item.getItems() != null && item.getItems().size() > 0)
					PlayerUtils.giveItems(player, item.getItems());

				if (item.getPrice() > 0) {
					voter.takePoints(item.getPrice());
					service.save(voter);
					PlayerUtils.send(player, PREFIX + "You spent &e" + item.getPrice() + plural(" &3point", item.getPrice())
							+ " on &e" + stripColor(item.getName()) + "&3. &e" + voter.getPoints() + " &3points remaining.");
				}

				log(player, item);

				if (item.isClose())
					player.closeInventory();
				else
					VPS.open(player, menu, index);
			}));
		});
	}

	public void addPagination(InventoryContents contents, Player player) {
		if (!menu.isFirst(page)) {
			ItemStack back = new ItemBuilder(Material.PAPER).amount(index - 1).name("&6<-").build();
			contents.set(5, 0, ClickableItem.of(back, e -> VPS.open(player, menu, index - 1)));
		}
		if (!menu.isLast(page)) {
			ItemStack forward = new ItemBuilder(Material.PAPER).amount(index + 1).name("&6->").build();
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

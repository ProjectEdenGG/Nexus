package gg.projecteden.nexus.features.resourcepack;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.store.StoreCommand;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.costume.Costume;
import gg.projecteden.nexus.models.costume.CostumeUser;
import gg.projecteden.nexus.models.costume.CostumeUserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static gg.projecteden.nexus.features.resourcepack.CustomModel.ICON;
import static gg.projecteden.nexus.models.costume.Costume.EXCLUSIVE;
import static gg.projecteden.nexus.models.costume.Costume.ROOT_FOLDER;

@Aliases("costumes")
@Permission("group.admin") // TODO Remove
public class CostumeCommand extends CustomCommand {
	private final CostumeUserService service = new CostumeUserService();

	public CostumeCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void menu() {
		new CostumeInventoryMenu().open(player());
	}

	@Path("store")
	void store() {
		new CostumeStoreMenu().open(player());
	}

	@Path("vouchers [player]")
	void vouchers(@Arg("self") CostumeUser user) {
		send(PREFIX + (isSelf(user) ? "Your" : user.getNickname() + "'s") + " vouchers: &e" + user.getVouchers());
		send(json(PREFIX + "Spend them in &c/costumes store").command("/costumes store"));
	}

	@Permission("group.admin")
	@Path("vouchers add <amount> [player]")
	void vouchers_add(int amount, @Arg("self") CostumeUser user) {
		user.setVouchers(user.getVouchers() + amount);
		service.save(user);
		send(PREFIX + "Gave &e" + amount + " &3vouchers to &e" + user.getNickname() + "&3. New balance: &e" + user.getVouchers());
	}

	@Permission("group.admin")
	@Path("vouchers remove <amount> [player]")
	void vouchers_remove(int amount, @Arg("self") CostumeUser user) {
		user.setVouchers(user.getVouchers() - amount);
		service.save(user);
		send(PREFIX + "Removed &e" + amount + " &3vouchers from &e" + user.getNickname() + "&3. New balance: &e" + user.getVouchers());
	}

	@Path("list [page]")
	@Permission("group.admin")
	void list(@Arg("1") int page) {
		paginate(Costume.values(), (costume, index) -> json("&3" + index + " &e" + costume.getId()), "/costume list", page);
	}

	@AllArgsConstructor
	public abstract static class CostumeMenu extends MenuUtils implements InventoryProvider {
		protected final CostumeUserService service = new CostumeUserService();
		protected final CostumeMenu previousMenu;
		protected final CustomModelFolder folder;

		public CostumeMenu() {
			this(null, ROOT_FOLDER);
		}

		@Override
		public void open(Player player, int page) {
			SmartInventory.builder()
				.provider(this)
				.size(6, 9)
				.title("Costumes")
				.build()
				.open(player, page);
		}

		@Override
		public void init(Player player, InventoryContents contents) {
			if (previousMenu == null)
				addCloseItem(contents);
			else
				addBackItem(contents, e -> previousMenu.open(player));
			final CostumeUser user = service.get(player);
			final ItemBuilder info = new ItemBuilder(Material.BOOK)
				.name("&6&lVouchers: " + (user.getVouchers() == 0 ? "&c" : "&e") + user.getVouchers())
				.lore("", "&eBuy vouchers on the &c/store", "&eClick for a link");

			contents.set(0, 8, ClickableItem.from(info.build(), e -> {
				player.closeInventory();
				user.sendMessage("&e" + StoreCommand.URL + "/category/costumes");
			}));

			List<ClickableItem> items = new ArrayList<>();

			for (CustomModelFolder subfolder : folder.getFolders()) {
				if (subfolder.getPath().contains(EXCLUSIVE))
					return;

				CustomModel firstModel = subfolder.getIcon();
				ItemStack item = new ItemStack(Material.BARRIER);
				if (firstModel != null)
					item = firstModel.getDisplayItem();

				ItemBuilder builder = new ItemBuilder(item).name(subfolder.getDisplayPath()).glow();
				items.add(ClickableItem.from(formatFolder(builder).build(), e -> newMenu(this, subfolder).open(player)));
			}

			// TODO Use CostumeType order
			if (folder.equals(ROOT_FOLDER))
				Collections.reverse(items);

			if (!items.isEmpty()) {
				while (items.size() % 9 != 0)
					items.add(ClickableItem.NONE);

				for (int i = 0; i < 9; i++)
					items.add(ClickableItem.NONE);
			}

			for (Costume costume : Costume.values()) {
				if (costume.getModel().getFileName().equals(ICON))
					continue;

				if (costume.getModel().getMaterial() != Material.STONE_BUTTON)
					continue;

				if (folder.equals(costume.getModel().getFolder()))
					items.add(formatCostume(user, costume));
			}

			addPagination(player, contents, items);
		}

		protected abstract CostumeMenu newMenu(CostumeMenu previousMenu, CustomModelFolder subfolder);

		abstract protected ItemBuilder formatFolder(ItemBuilder builder);

		abstract protected ClickableItem formatCostume(CostumeUser user, Costume costume);
	}

	@NoArgsConstructor
	public static class CostumeStoreMenu extends CostumeMenu {

		public CostumeStoreMenu(CostumeMenu previousMenu, CustomModelFolder folder) {
			super(previousMenu, folder);
		}

		@Override
		protected CostumeMenu newMenu(CostumeMenu previousMenu, CustomModelFolder subfolder) {
			return new CostumeStoreMenu(previousMenu, subfolder);
		}

		protected ItemBuilder formatFolder(ItemBuilder builder) {
			return builder; // TODO Show # available costumes
		}

		protected ClickableItem formatCostume(CostumeUser user, Costume costume) {
			final ItemBuilder builder = new ItemBuilder(costume.getModel().getDisplayItem());
			// TODO show price like VPS

			return ClickableItem.from(builder.build(), e -> {
				if (user.getVouchers() > 0) {
					user.setVouchers(user.getVouchers() - 1);
					user.getOwnedCostumes().add(costume);
					service.save(user);
				}
			});
		}
	}

	@NoArgsConstructor
	public static class CostumeInventoryMenu extends CostumeMenu {

		public CostumeInventoryMenu(CostumeMenu previousMenu, CustomModelFolder folder) {
			super(previousMenu, folder);
		}

		@Override
		protected CostumeMenu newMenu(CostumeMenu previousMenu, CustomModelFolder subfolder) {
			return new CostumeInventoryMenu(previousMenu, subfolder);
		}

		protected ItemBuilder formatFolder(ItemBuilder builder) {
			return builder; // TODO Show # owned costumes
		}

		protected ClickableItem formatCostume(CostumeUser user, Costume costume) {
			final ItemBuilder builder = new ItemBuilder(costume.getModel().getDisplayItem());
			if (user.getActiveCostume().equals(costume))
				builder.glow();

			return ClickableItem.from(builder.build(), e -> {
				user.setActiveCostume(costume);
				service.save(user);
			});
		}
	}



}

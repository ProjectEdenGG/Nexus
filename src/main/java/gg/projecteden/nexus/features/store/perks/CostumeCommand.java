package gg.projecteden.nexus.features.store.perks;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.features.resourcepack.models.events.ResourcePackUpdateCompleteEvent;
import gg.projecteden.nexus.features.resourcepack.models.events.ResourcePackUpdateStartEvent;
import gg.projecteden.nexus.features.resourcepack.models.files.CustomModelFolder;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.costume.Costume;
import gg.projecteden.nexus.models.costume.CostumeUser;
import gg.projecteden.nexus.models.costume.CostumeUserService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static gg.projecteden.nexus.features.resourcepack.models.CustomModel.ICON;
import static gg.projecteden.nexus.models.costume.Costume.EXCLUSIVE;

@NoArgsConstructor
@Aliases("costumes")
public class CostumeCommand extends CustomCommand implements Listener {
	private final CostumeUserService service = new CostumeUserService();

	public CostumeCommand(@NonNull CommandEvent event) {
		super(event);
	}

	private static int taskId;

	@EventHandler
	public void on(ResourcePackUpdateStartEvent event) {
		Tasks.cancel(taskId);
	}

	@EventHandler
	public void on(ResourcePackUpdateCompleteEvent event) {
		Costume.loadAll();

		final CostumeUserService service = new CostumeUserService();
		taskId = Tasks.repeat(TickTime.TICK, TickTime.TICK, () -> {
			for (Player player : OnlinePlayers.getAll())
				service.get(player).sendCostumePacket();
		});
	}

	@Path
	void menu() {
		new CostumeInventoryMenu().open(player());
	}

	@Path("off [player]")
	void off(@Arg(value = "self", permission = "group.staff") CostumeUser user) {
		user.setActiveCostume(null);
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
	@Path("reload")
	void reload() {
		Costume.loadAll();
		send(PREFIX + "Loaded " + Costume.values().size() + " costumes");
	}

	@Permission("group.admin")
	@Path("vouchers add <amount> [player]")
	void vouchers_add(int amount, @Arg("self") CostumeUser user) {
		user.addVouchers(amount);
		service.save(user);
		send(PREFIX + "Gave &e" + amount + " &3vouchers to &e" + user.getNickname() + "&3. New balance: &e" + user.getVouchers());
	}

	@Permission("group.admin")
	@Path("vouchers remove <amount> [player]")
	void vouchers_remove(int amount, @Arg("self") CostumeUser user) {
		user.takeVouchers(amount);
		service.save(user);
		send(PREFIX + "Removed &e" + amount + " &3vouchers from &e" + user.getNickname() + "&3. New balance: &e" + user.getVouchers());
	}

	@Path("list [page]")
	@Permission("group.admin")
	void list(@Arg("1") int page) {
		paginate(Costume.values(), (costume, index) -> json(index + " &e" + costume.getId()), "/costume list", page);
	}

	@Path("top [page]")
	@Permission("group.admin")
	void top(@Arg("1") int page) {
		Map<Costume, Integer> counts = new HashMap<>() {{
			for (CostumeUser user : service.getAll())
				if (user.getRank() != Rank.ADMIN)
					for (String costume : user.getOwnedCostumes())
						put(Costume.of(costume), getOrDefault(costume, 0) + 1);
		}};

		final BiFunction<Costume, String, JsonBuilder> formatter = (costume, index) ->
			json(index + " &e" + costume.getId() + " &7- " + counts.get(costume));

		paginate(Utils.sortByValueReverse(counts).keySet(), formatter, "/costume top", page);
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		final CostumeUserService service = new CostumeUserService();
		final CostumeUser user = service.get(event.getPlayer());
		if (user.getActiveCostume() == null)
			Tasks.wait(1, user::sendResetPackets);
	}

	@AllArgsConstructor
	public abstract static class CostumeMenu extends MenuUtils implements InventoryProvider {
		protected final CostumeUserService service = new CostumeUserService();
		protected final CostumeMenu previousMenu;
		protected final CustomModelFolder folder;

		public CostumeMenu() {
			this(null, Costume.getRootFolder());
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

			init(user, contents);

			List<ClickableItem> items = new ArrayList<>();

			for (CustomModelFolder subfolder : folder.getFolders()) {
				if (subfolder.getPath().contains(EXCLUSIVE))
					continue;

				CustomModel firstModel = subfolder.getIcon(model -> {
					if (this instanceof CostumeStoreMenu)
						return true;
					return user.owns(model);
				});
				ItemStack item = new ItemStack(Material.BARRIER);
				if (firstModel != null)
					item = firstModel.getDisplayItem();

				final String displayName = StringUtils.camelCase(StringUtils.listLast(subfolder.getDisplayPath(), "/"));
				ItemBuilder builder = new ItemBuilder(item).name(displayName).glow();
				final int available = getAvailableCostumes(player, subfolder);
				if (available == 0)
					continue;

				builder.lore("", "&3Available Costumes: &e" + available);
				items.add(ClickableItem.from(builder.build(), e -> newMenu(this, subfolder).open(player)));
			}

			if (!items.isEmpty()) {
				while (items.size() % 9 != 0)
					items.add(ClickableItem.NONE);

				for (int i = 0; i < 9; i++)
					items.add(ClickableItem.NONE);
			}

			for (Costume costume : Costume.values()) {
				if (costume.getModel().getFileName().equals(ICON))
					continue;

				// legacy GG hat
				if (costume.getModel().getMaterial() == Material.CYAN_STAINED_GLASS_PANE)
					continue;

				if (!isAvailableCostume(user, costume))
					continue;

				if (folder.equals(costume.getModel().getFolder()))
					items.add(formatCostume(user, costume, contents));
			}

			paginator(player, contents, items);
		}

		protected abstract CostumeMenu newMenu(CostumeMenu previousMenu, CustomModelFolder subfolder);

		protected void init(CostumeUser user, InventoryContents contents) {}

		protected int getAvailableCostumes(Player player, CustomModelFolder folder) {
			final CostumeUserService service = new CostumeUserService();
			final CostumeUser user = service.get(player);
			int available = 0;
			for (Costume costume : Costume.values()) {
				if (!isAvailableCostume(user, costume))
					continue;

				if (!(costume.getModel().getFolder().getPath() + "/").contains(folder.getPath()))
					continue;

				++available;
			}

			return available;
		}

		abstract protected boolean isAvailableCostume(CostumeUser user, Costume costume);

		abstract protected ClickableItem formatCostume(CostumeUser user, Costume costume, InventoryContents contents);
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

		@Override
		protected void init(CostumeUser user, InventoryContents contents) {
			final ItemBuilder info = new ItemBuilder(Material.BOOK)
				.name("&6&lVouchers: " + (user.getVouchers() == 0 ? "&c" : "&e") + user.getVouchers())
				.lore("", "&eBuy vouchers on the &c/store", "&eClick for a link"); // TODO Mention /store gallery

			contents.set(0, 8, ClickableItem.from(info.build(), e -> {
				user.getOnlinePlayer().closeInventory();
				user.sendMessage("&e" + Costume.STORE_URL);
			}));
		}

		@Override
		protected boolean isAvailableCostume(CostumeUser user, Costume costume) {
			return !user.getOwnedCostumes().contains(costume.getId());
		}

		protected ClickableItem formatCostume(CostumeUser user, Costume costume, InventoryContents contents) {
			final ItemBuilder builder = new ItemBuilder(costume.getModel().getDisplayItem());
			builder.lore("", "&3Cost: " + (user.getVouchers() <= 0 ? "&c" : "&e") + "1");

			return ClickableItem.from(builder.build(), e -> {
				if (user.getVouchers() > 0) {
					ConfirmationMenu.builder()
						.onConfirm(e2 -> {
							user.takeVouchers(1);
							user.getOwnedCostumes().add(costume.getId());
							service.save(user);
						})
						.onFinally(e2 -> open(user.getOnlinePlayer(), contents.pagination().getPage()))
						.open(user.getOnlinePlayer());
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

		@Override
		protected void init(CostumeUser user, InventoryContents contents) {
			final ItemBuilder info = new ItemBuilder(Material.BOOK)
				.name("&6&lVouchers: " + (user.getVouchers() == 0 ? "&c" : "&e") + user.getVouchers())
				.lore("", "&eClick to view the costume store");

			contents.set(0, 8, ClickableItem.from(info.build(), e ->
				new CostumeStoreMenu(this, Costume.getRootFolder()).open(user.getOnlinePlayer())));

			final Costume costume = Costume.of(user.getActiveCostume());
			if (costume != null) {
				final ItemBuilder builder = new ItemBuilder(costume.getModel().getDisplayItem())
					.lore("", "&a&lActive", "&cClick to deactivate")
					.glow();

				contents.set(0, 4, ClickableItem.from(builder.build(), e -> {
					user.setActiveCostume(null);
					service.save(user);
					open(user.getOnlinePlayer(), contents.pagination().getPage());
				}));
			}
		}

		@Override
		protected boolean isAvailableCostume(CostumeUser user, Costume costume) {
			return user.getOwnedCostumes().contains(costume.getId());
		}

		protected ClickableItem formatCostume(CostumeUser user, Costume costume, InventoryContents contents) {
			final ItemBuilder builder = new ItemBuilder(costume.getModel().getDisplayItem());
			if (costume.getId().equals(user.getActiveCostume()))
				builder.lore("", "&a&lActive").glow();

			return ClickableItem.from(builder.build(), e -> {
				user.setActiveCostume(costume.getId().equals(user.getActiveCostume()) ? null : costume);
				service.save(user);
				open(user.getOnlinePlayer(), contents.pagination().getPage());
			});
		}
	}

}

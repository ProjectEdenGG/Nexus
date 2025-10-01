package gg.projecteden.nexus.features.store.perks.visuals;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelInstance;
import gg.projecteden.nexus.features.resourcepack.models.events.ResourcePackUpdateCompleteEvent;
import gg.projecteden.nexus.features.resourcepack.models.events.ResourcePackUpdateStartEvent;
import gg.projecteden.nexus.features.resourcepack.models.files.ItemModelFolder;
import gg.projecteden.nexus.features.store.gallery.StoreGallery;
import gg.projecteden.nexus.features.workbenches.dyestation.DyeStation;
import gg.projecteden.nexus.features.workbenches.dyestation.DyeStationMenu;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.WikiConfig;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.costume.Costume;
import gg.projecteden.nexus.models.costume.Costume.CostumeType;
import gg.projecteden.nexus.models.costume.CostumeUser;
import gg.projecteden.nexus.models.costume.CostumeUserService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor
@Aliases("costumes")
@WikiConfig(rank = "Store", feature = "Visuals")
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
		service.getCache().values().forEach(user -> user.getCachedItems().clear());
		taskId = Tasks.repeat(TickTime.TICK, TickTime.TICK, () -> {
			for (Player player : OnlinePlayers.getAll())
				service.get(player).sendCostumePacket();
			for (Player player : OnlinePlayers.where().world(StoreGallery.getWorld()).get())
				service.get(player).sendDisplayCostumePacket();
		});
	}

	@Path
	@Description("Open the costumes menu")
	void menu() {
		new CostumeInventoryMenu().open(player());
	}

	@Path("off [player]")
	@Description("Disable your costumes")
	void off(@Arg(value = "self", permission = Group.STAFF) CostumeUser user) {
		for (CostumeType type : CostumeType.values())
			user.setActiveCostume(type, null);
		service.save(user);
	}
	
	@Path("useArmorSkinHelmetCostume [state]")
	void useArmorSkinHelmetCostume(Boolean state) {
		var user = service.get(player());

		if (state == null)
			state = !user.isUseArmorSkinHelmetCostume();

		user.setUseArmorSkinHelmetCostume(state);
		service.save(user);
		user.sendResetPacket(CostumeType.HAT);
		send(PREFIX + "You are " + (state ? "&anow" : "&cno longer") + " using &3armor skin helmet costumes");
	}

	@Path("store")
	@Description("Open the costume store")
	void store() {
		new CostumeStoreMenu().open(player());
	}

	@Path("dye <type> <color>")
	@Description("Dye a costume")
	void dye(CostumeType type, ChatColor color) {
		final CostumeUser user = service.get(player());
		final Costume costume = user.getActiveCostume(type);
		if (costume == null)
			error("You do not have an active costume");
		if (!costume.isDyeable())
			error("That costume is not dyeable");

		user.dye(costume, ColorType.toBukkitColor(color));
		service.save(user);
		send(PREFIX + "Set costume color to " + color + arg(2));
	}

	@Path("reload")
	@Permission(Group.ADMIN)
	@Description("Reload costumes from the resource pack")
	void reload() {
		Costume.loadAll();
		service.getCache().values().forEach(user -> user.getCachedItems().clear());
		send(PREFIX + "Loaded " + Costume.values().size() + " costumes");
	}

	@Path("list")
	@Permission(Group.ADMIN)
	@Description("List all Costumes by their Id")
	void list() {
		send(PREFIX + "Loaded Costumes: ");

		Costume.values().stream().map(Costume::getId).sorted().toList().forEach(id -> {
			send(" &7- &e" + id);
		});
	}

	@Path("vouchers [player]")
	@Description("View how many costume vouchers you have")
	void vouchers(@Arg(value = "self", permission = Group.STAFF) CostumeUser user) {
		send(PREFIX + (isSelf(user) ? "Your" : user.getNickname() + "'s") + " vouchers: &e" + user.getVouchers());
		send(json(PREFIX + "Spend them in &c/costumes store").command("/costumes store"));
	}

	@Path("vouchers add <amount> [player]")
	@Permission(Group.ADMIN)
	@Description("Modify a player's vouchers")
	void vouchers_add(int amount, @Arg("self") CostumeUser user) {
		user.addVouchers(amount);
		service.save(user);
		send(PREFIX + "Gave &e" + amount + " &3vouchers to &e" + user.getNickname() + "&3. New balance: &e" + user.getVouchers());
	}

	@Path("vouchers remove <amount> [player]")
	@Permission(Group.ADMIN)
	@Description("Modify a player's vouchers")
	void vouchers_remove(int amount, @Arg("self") CostumeUser user) {
		user.takeVouchers(amount);
		service.save(user);
		send(PREFIX + "Removed &e" + amount + " &3vouchers from &e" + user.getNickname() + "&3. New balance: &e" + user.getVouchers());
	}

	@Path("tempvouchers add <amount> [player]")
	@Permission(Group.ADMIN)
	@Description("Modify a player's temporary vouchers")
	void tempvouchers_add(int amount, @Arg("self") CostumeUser user) {
		user.addTemporaryVouchers(amount);
		service.save(user);
		send(PREFIX + "Gave &e" + amount + " &3temporary vouchers to &e" + user.getNickname() + "&3. New balance: &e" + user.getTemporaryVouchers());
	}

	@Permission(Group.ADMIN)
	@Path("tempvouchers remove <amount> [player]")
	@Description("Modify a player's temporary vouchers")
	void tempvouchers_remove(int amount, @Arg("self") CostumeUser user) {
		user.takeTemporaryVouchers(amount);
		service.save(user);
		send(PREFIX + "Removed &e" + amount + " &3temporary vouchers from &e" + user.getNickname() + "&3. New balance: &e" + user.getTemporaryVouchers());
	}

	@Path("top [page]")
	@Permission(Group.ADMIN)
	@Description("View the most popular costumes")
	void top(@Arg("1") int page) {
		Map<Costume, Integer> counts = new HashMap<>() {{
			for (CostumeUser user : service.getAll())
				if (user.getRank() != Rank.ADMIN)
					for (String costume : user.getOwnedCostumes())
						if (Costume.of(costume) != null)
							put(Costume.of(costume), getOrDefault(costume, 0) + 1);
		}};

		new Paginator<Costume>()
			.values(Utils.sortByValueReverse(counts).keySet())
			.formatter((costume, index) -> json(index + " &e" + costume.getId() + " &7- " + counts.get(costume)))
			.command("/costume top")
			.page(page)
			.send();
	}

	@TabCompleterFor(Costume.class)
	List<String> tabCompleteCostume(String filter) {
		return Costume.values().stream()
			.map(Costume::getId)
			.filter(id -> id.toLowerCase().startsWith(filter.toLowerCase()))
			.collect(Collectors.toList());
	}

	@ConverterFor(Costume.class)
	Costume convertToCostume(String value) {
		final Costume costume = Costume.of(value);
		if (costume == null)
			throw new InvalidInputException("Costume &e" + value + " &cnot found");
		return costume;
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		final CostumeUserService service = new CostumeUserService();
		final CostumeUser user = service.get(event.getPlayer());
		for (CostumeType type : CostumeType.values())
			if (!user.hasActiveCostume(type))
				Tasks.wait(1, () -> user.sendResetPacket(type));
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		final CostumeUserService service = new CostumeUserService();
		final CostumeUser user = service.get(event.getPlayer());
		user.getCachedItems().clear();
	}

	@Title("Costumes")
	@AllArgsConstructor
	public abstract static class CostumeMenu extends InventoryProvider {
		protected final CostumeUserService service = new CostumeUserService();
		protected final CostumeMenu previousMenu;
		protected final ItemModelFolder folder;

		public CostumeMenu() {
			this(null, Costume.getRootFolder());
		}

		@Override
		public void init() {
			if (previousMenu == null)
				addCloseItem();
			else
				addBackItem(e -> previousMenu.open(viewer));
			final CostumeUser user = service.get(viewer);

			init(user, contents);

			List<ClickableItem> items = new ArrayList<>();

			for (ItemModelFolder subfolder : folder.getFolders()) {
				if (subfolder.getPath().contains(Costume.EXCLUSIVE))
					continue;
				if (subfolder.getPath().contains(Costume.ARMOR))
					continue;
				if (subfolder.getPath().contains(Costume.BACK))
					continue;

				ItemModelInstance firstModel = subfolder.getIcon(model -> isAvailableCostume(user, Costume.of(model)));
				ItemStack item = new ItemStack(Material.BARRIER);
				if (firstModel != null)
					item = firstModel.getDisplayItem();

				final String displayName = StringUtils.camelCase(StringUtils.listLast(subfolder.getDisplayPath(), "/"));
				ItemBuilder builder = new ItemBuilder(item).name(displayName).glow();
				final int available = getAvailableCostumes(viewer, subfolder);
				if (available == 0)
					continue;

				builder.lore("", "&3Available Costumes: &e" + available);
				items.add(ClickableItem.of(builder.build(), e -> newMenu(this, subfolder).open(viewer)));
			}

			if (!items.isEmpty()) {
				while (items.size() % 9 != 0)
					items.add(ClickableItem.NONE);

				for (int i = 0; i < 9; i++)
					items.add(ClickableItem.NONE);
			}

			for (Costume costume : Costume.values()) {
				if (costume.getModel().getFileName().equals(ItemModelInstance.ICON))
					continue;

				if (!isAvailableCostume(user, costume))
					continue;

				if (folder.equals(costume.getModel().getFolder()))
					items.add(formatCostume(user, costume, contents));
			}

			paginate(items);
		}

		abstract protected CostumeMenu newMenu(CostumeMenu previousMenu, ItemModelFolder subfolder);

		protected void init(CostumeUser user, InventoryContents contents) {}

		protected int getAvailableCostumes(Player player, ItemModelFolder folder) {
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

		protected boolean isAvailableCostume(CostumeUser user, Costume costume) {
			return true;
		}

		abstract protected ClickableItem formatCostume(CostumeUser user, Costume costume, InventoryContents contents);
	}

	@NoArgsConstructor
	public static class CostumeStoreMenu extends CostumeMenu {

		public CostumeStoreMenu(CostumeMenu previousMenu, ItemModelFolder folder) {
			super(previousMenu, folder);
		}

		@Override
		protected CostumeMenu newMenu(CostumeMenu previousMenu, ItemModelFolder subfolder) {
			return new CostumeStoreMenu(previousMenu, subfolder);
		}

		@Override
		protected void init(CostumeUser user, InventoryContents contents) {
			final ItemBuilder info = new ItemBuilder(Material.BOOK)
				.name("&6&lVouchers: " + (user.getVouchers() == 0 ? "&c" : "&e") + user.getVouchers());

			if (user.hasTemporaryVouchers())
				info.lore("&6&lTemporary Vouchers: &e" + user.getTemporaryVouchers());

			info.lore("", "&eBuy vouchers on the &c/store", "&eClick for a link", "", "&ePreview costumes in &c/store gallery");

			contents.set(0, 8, ClickableItem.of(info.build(), e -> {
				user.getOnlinePlayer().closeInventory();
				user.sendMessage("&e" + Costume.STORE_URL_VISUALS);
			}));
		}

		@Override
		protected boolean isAvailableCostume(CostumeUser user, Costume costume) {
			return !user.owns(costume);
		}

		protected ClickableItem formatCostume(CostumeUser user, Costume costume, InventoryContents contents) {
			final ItemBuilder builder = new ItemBuilder(user.getCostumeDisplayItem(costume));
			builder.lore("", "&3Cost: " + (user.getVouchers() <= 0 ? "&c" : "&e") + "1");

			return ClickableItem.of(builder.build(), e -> {
				if (user.hasAnyVouchers()) {
					ConfirmationMenu.builder()
						.onConfirm(e2 -> {
							if (user.hasTemporaryVouchers()) {
								user.takeTemporaryVouchers(1);
								user.getTemporarilyOwnedCostumes().add(costume.getId());
							} else if (user.hasVouchers()) {
								user.takeVouchers(1);
								user.getOwnedCostumes().add(costume.getId());
							} else {
								throw new InvalidInputException("You do not have enough vouchers");
							}
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

		public CostumeInventoryMenu(CostumeMenu previousMenu, ItemModelFolder folder) {
			super(previousMenu, folder);
		}

		@Override
		protected CostumeMenu newMenu(CostumeMenu previousMenu, ItemModelFolder subfolder) {
			return new CostumeInventoryMenu(previousMenu, subfolder);
		}

		@Override
		protected void init(CostumeUser user, InventoryContents contents) {
			final ItemBuilder info = new ItemBuilder(Material.BOOK)
				.name("&6&lVouchers: " + (user.getVouchers() == 0 ? "&c" : "&e") + user.getVouchers())
				.lore("", "&eClick to view the costume store");

			contents.set(0, 8, ClickableItem.of(info.build(), e ->
				new CostumeStoreMenu(this, Costume.getRootFolder()).open(user.getOnlinePlayer())));

			for (CostumeType type : CostumeType.values()) {
				final Costume costume = user.getActiveCostume(type);
				if (costume != null) {
					final ItemBuilder builder = new ItemBuilder(user.getCostumeDisplayItem(costume))
						.lore("", "&a&lActive", "&cClick to deactivate")
						.glow();

					contents.set(0, type.getMenuHeaderSlot(), ClickableItem.of(builder.build(), e -> {
						user.setActiveCostume(type, null);
						service.save(user);
						open(user.getOnlinePlayer(), contents.pagination().getPage());
					}));

					if (MaterialTag.DYEABLE.isTagged(costume.getItem().getType())) {
						contents.set(0, type.getMenuHeaderSlot() + 1, ClickableItem.of(DyeStation.getWorkbench().build(), e ->
							new DyeStationMenu().openCostume(user, costume, data -> {
								user.dye(costume, data.getColor());
								service.save(user);
								open(user.getOnlinePlayer());
							})));
					}
				}
			}
		}

		@Override
		protected boolean isAvailableCostume(CostumeUser user, Costume costume) {
			return Rank.of(user).isAdmin() || user.owns(costume);
		}

		protected ClickableItem formatCostume(CostumeUser user, Costume costume, InventoryContents contents) {
			final ItemBuilder builder = new ItemBuilder(user.getCostumeDisplayItem(costume));
			if (user.hasCostumeActivated(costume))
				builder.lore("", "&a&lActive").glow();

			return ClickableItem.of(builder.build(), e -> {
				user.setActiveCostume(costume.getType(), user.hasCostumeActivated(costume) ? null : costume);
				service.save(user);
				open(user.getOnlinePlayer(), contents.pagination().getPage());
			});
		}
	}

}

package gg.projecteden.nexus.features.store;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.store.BuycraftUtils.CouponCreator;
import gg.projecteden.nexus.features.store.annotations.Category.StoreCategory;
import gg.projecteden.nexus.features.store.gallery.StoreGalleryNPCs;
import gg.projecteden.nexus.features.store.gallery.StoreGalleryNPCs.DisplaySet;
import gg.projecteden.nexus.features.store.perks.visuals.NPCListener;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromHelp;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.extraplots.ExtraPlotUserService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.store.Contributor;
import gg.projecteden.nexus.models.store.Contributor.Purchase;
import gg.projecteden.nexus.models.store.ContributorService;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.LuckPermsUtils.GroupChange.PlayerRankChangeEvent;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import net.buycraft.plugin.data.Coupon;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Aliases({"donate", "buy"})
@Redirect(from = "/seeGlobalDeliverable", to = "/donothing")
public class StoreCommand extends CustomCommand implements Listener {
	public static final String URL = "https://store." + Nexus.DOMAIN;
	public static final String PREFIX = StringUtils.getPrefix("Store");
	private static final String PLUS = "&3[+] &e";

	private final ContributorService service = new ContributorService();
	private Contributor contributor;

	public StoreCommand(CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			contributor = service.get(player());
	}

	static {
		Utils.tryRegisterListener(new NPCListener());
	}

	@Path
	@Description("Get a link to the server store")
	void store() {
		line();
		send("&eEnjoying the server and want to support us?");
		line();
		send(json().next("&3Visit our store: &e" + URL));
		send(json().next("&3View interactable perks in-game at the ").group().next("&e/store gallery").command("/store gallery").hover("&3Click to teleport"));
		line();
		send(json(PLUS + "Terms and Conditions").hover(PLUS + "Click here before you purchase for anything").command("/store tac"));
	}

	@Path("packages [player]")
	@Description("View which packages you own")
	void packages(@Arg("self") Contributor player) {
		new StoreProvider(null, null, player).open(player());
	}

	@Async
	@Path("contributors recent [page]")
	@Description("View recent contributors")
	void contributors_recent(@Arg("1") int page) {
		new Paginator<Purchase>()
			.values(service.getRecent())
			.formatter((purchase, index) -> json(index + " " + Nerd.of(purchase.getPurchaserUuid()).getColoredName() + " &7- " + StringUtils.prettyMoney(purchase.getRealPrice())))
			.command("/store contributors recent")
			.page(page)
			.send();
	}

	@Path("credit [player]")
	@Description("View your store credit")
	void credit(@Arg(value = "self", permission = Group.STAFF) Contributor contributor) {
		send(PREFIX + (isSelf(contributor) ? "Your" : contributor.getNickname() + "'s") + " store credit: " + contributor.getCreditFormatted() + " USD");
		if (isSelf(contributor)) {
			line();
			send("&3Redeem with &c/store credit redeem <amount>");
			send("&3View available coupons with &c/store coupons list");
		}
	}

	@Async
	@Path("credit redeem <amount> [player]")
	@Description("Generate a coupon code with store credit")
	void credit_redeem(double amount, @Arg(value = "self", permission = Group.STAFF) Contributor contributor) {
		if (!contributor.hasCredit(amount))
			error("You do not have enough credit");

		contributor.takeCredit(amount);
		service.save(contributor);

		String code = new CouponCreator(contributor, amount).create();
		List<String> lore = new ArrayList<>(List.of("&fClick to copy", "&fRedeem at " + StoreCommand.URL));
		if (contributor.hasNickname())
			lore.add("&fMake sure to use your real username, not nickname");

		send(json(PREFIX + "Created store coupon &e" + code + "&3. Click to copy").copy(code).hover(lore));
	}

	@Permission(Group.ADMIN)
	@Path("credit set <player> <amount>")
	@Description("Modify a player's store credit")
	void set(Contributor contributor, double amount) {
		contributor.setCredit(amount);
		service.save(contributor);
		send(PREFIX + "Set &e" + Nickname.of(contributor) + "'s &3balance to &e" + contributor.getCreditFormatted());
	}

	@Permission(Group.ADMIN)
	@Path("credit give <player> <amount>")
	@Description("Modify a player's store credit")
	void give(Contributor contributor, double amount) {
		contributor.giveCredit(amount);
		service.save(contributor);
		send(PREFIX + "Added &e" + StringUtils.prettyMoney(amount) + " &3to &e" + Nickname.of(contributor) + "'s &3balance. New balance: &e" + contributor.getCreditFormatted());
	}

	@Permission(Group.ADMIN)
	@Path("credit take <player> <amount>")
	@Description("Modify a player's store credit")
	void take(Contributor contributor, double amount) {
		contributor.takeCredit(amount);
		service.save(contributor);
		send(PREFIX + "Removed &e" + StringUtils.prettyMoney(amount) + " &3from &e" + Nickname.of(contributor) + "'s &3balance. New balance: &e" + contributor.getCreditFormatted());
	}

	@Async
	@SneakyThrows
	@Path("coupons create <player> <amount>")
	@Permission(Group.ADMIN)
	@Description("Create a coupon for a player")
	void coupon_create(Contributor contributor, double amount) {
		String code = new CouponCreator(contributor, amount).create();
		send(json(PREFIX + "Created coupon &e" + code).copy(code).hover("&fClick to copy"));
	}

	@Async
	@SneakyThrows
	@Path("coupons list <player>")
	@Description("List your available coupon codes")
	void coupon_list(@Arg(value = "self", permission = Group.STAFF) Contributor contributor) {
		final List<Coupon> coupons = Nexus.getBuycraft().getApiClient().getAllCoupons().execute().body().getData().stream()
			.filter(coupon -> coupon.getUsername().equals(contributor.getName()))
			.filter(coupon -> coupon.getExpire().getLimit() > 0)
			.toList();

		if (coupons.isEmpty())
			error("No coupons found" + (isSelf(contributor) ? ". Create one with /store credit redeem <amount>" : " for " + contributor.getNickname()));

		send(PREFIX + "Available coupons (&eClick &3to copy)");

		line();
		for (Coupon coupon : coupons)
			send(json(" &e" + coupon.getCode() + " &7- $" + coupon.getDiscount().getValue()).copy(coupon.getCode()).hover("&fClick to copy"));

		line();
		send("&3Redeem at &e" + StoreCommand.URL);
	}

	@AllArgsConstructor
	private static class StoreProvider extends InventoryProvider {
		private final StoreProvider previousMenu;
		private final StoreCategory category;
		private final Contributor contributor;

		@Override
		public String getTitle() {
			return "Store" + (category == null ? "" : " - " + StringUtils.camelCase(category));
		}

		@Override
		public void init() {
			if (previousMenu == null)
				addCloseItem();
			else
				addBackItem(e -> previousMenu.open(viewer));

			ItemBuilder info = new ItemBuilder(Material.BOOK).name("&eVisit Store").lore("&f" + URL);
			contents.set(0, 8, ClickableItem.of(info.build(), e -> {
				viewer.closeInventory();
				PlayerUtils.send(viewer, new JsonBuilder(PREFIX + "Click me to open the &estore").url(URL));
			}));

			List<ClickableItem> items = new ArrayList<>();

			if (category == null)
				for (StoreCategory category : StoreCategory.values()) {
					ItemBuilder item = category.getDisplayItem();

					int owned = 0;
					int count = category.getPackages().size();
					for (Package storePackage : category.getPackages())
						if (storePackage.has(contributor))
							++owned;

					item.lore("", "&fOwned: " + (owned == 0 ? "&c0" : owned == count ? "&a" + owned : "&e" + owned));

					items.add(ClickableItem.of(item.glow(owned == count), e -> new StoreProvider(this, category, contributor).open(viewer)));
				}
			else
				for (Package storePackage : category.getPackages()) {
					ItemBuilder item = storePackage.getDisplayItem(viewer.getUniqueId());
					boolean has = storePackage.has(contributor);
					int count = storePackage.count(contributor);

					item.lore("", "&fOwned: " + (has ? "&aYes" + (count == 1 ? "" : " &f(" + count + ")") : "&cNo"));

					items.add(ClickableItem.empty(item.glow(has)));
				}

			paginate(items);
		}

	}

	@Path("tac")
	@HideFromHelp
	@HideFromWiki
	@TabCompleteIgnore
	void tac() {
		line();
		send("&3Before you donate on the server, here are some things you must know before you do so");
		send(PLUS + "There are no refunds");
		send(PLUS + "If you are under the age of eighteen, be sure to have a parent or guardians permission");
		send(PLUS + "None of the money that is donated goes to a Staff member personally. The money is for improving the server only");
		send(PLUS + "Just because you donate does not mean you can not be banned");
	}

	@Path("apply <package> [player]")
	@Permission(Group.ADMIN)
	@Description("Apply a package to a player")
	void apply(Package packageType, @Arg("self") UUID uuid) {
		packageType.apply(uuid);
		send(PREFIX + "Applied package " + camelCase(packageType) + " to " + Nickname.of(uuid));
	}

	@Path("(expire|remove) <package> [player]")
	@Permission(Group.ADMIN)
	@Description("Expire a package on a player")
	void expire(Package packageType, @Arg("self") UUID uuid) {
		packageType.expire(uuid);
		send(PREFIX + "Removed package " + camelCase(packageType) + " from " + Nickname.of(uuid));
	}

	@Path("gallery")
	@Description("Teleport to the store gallery")
	void gallery() {
		WarpType.STAFF.get("store").teleportAsync(player());
	}

	@Path("gallery displays updateSkins")
	@Permission(Group.ADMIN)
	@Description("Update the storey gallery NPC skins")
	void gallery_displays_updateSkins() {
		StoreGalleryNPCs.updateSkins();
		send(PREFIX + "Updated skins");
	}

	@Path("gallery displays getIds")
	@Permission(Group.ADMIN)
	@Description("Get the NPC ids of the display NPCs")
	void gallery_displays_getIds() {
		for (DisplaySet display : StoreGalleryNPCs.getDisplays()) {
			send(display.getId() + ":");
			send(" 1: " + display.getDisplay1().getId());
			send(" 2: " + display.getDisplay2().getId());
			send(" 3: " + display.getDisplay3().getId());
		}
	}

	@Path("broadcasts <on/off>")
	@Description("Toggle your purchase broadcasts")
	void broadcasts(Boolean enabled) {
		if (enabled == null)
			enabled = contributor.isBroadcasts();

		contributor.setBroadcasts(enabled);
		service.save(contributor);
		send(PREFIX + (enabled ? "&aEnabled" : "&cDisabled") + " &3your purchase broadcasts");
	}

	@EventHandler
	public void onPlayerRankChange(PlayerRankChangeEvent event) {
		new ExtraPlotUserService().get(event.getUuid()).update();
	}

}

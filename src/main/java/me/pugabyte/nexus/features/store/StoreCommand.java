package me.pugabyte.nexus.features.store;

import com.google.common.collect.ImmutableList;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.features.store.annotations.Category.StoreCategory;
import me.pugabyte.nexus.features.store.perks.NPCListener;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.contributor.Contributor;
import me.pugabyte.nexus.models.contributor.Contributor.Purchase;
import me.pugabyte.nexus.models.contributor.ContributorService;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.Name;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Utils;
import net.buycraft.plugin.data.Coupon;
import net.buycraft.plugin.data.Coupon.Discount;
import net.buycraft.plugin.data.Coupon.Effective;
import net.buycraft.plugin.data.Coupon.Expire;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.BiFunction;

import static eden.utils.StringUtils.prettyMoney;

@Aliases({"donate", "buy"})
public class StoreCommand extends CustomCommand {
	public static final String URL = "https://store.projecteden.gg";
	private static final String PLUS = "&3[+] &e";

	private final ContributorService service = new ContributorService();

	public StoreCommand(CommandEvent event) {
		super(event);
	}

	static {
		Utils.tryRegisterListener(new NPCListener());
	}

	@Path
	void store() {
		line();
		send("&eEnjoying the server and want to support us?");
		line();
		send(json().next("&3Visit our store: &e" + URL));
		line();
		send(json(PLUS + "Terms and Conditions").hover(PLUS + "Click here before you purchase for anything").command("/store tac"));
	}

	@Path("packages [player]")
	void packages(@Arg("self") OfflinePlayer player) {
		new StoreProvider(player).open(player());
	}

	@Async
	@Path("contributors recent [page]")
	void contributors_recent(@Arg("1") int page) {
		BiFunction<Purchase, String, JsonBuilder> formatter = (purchase, index) ->
			json("&3" + index + " " + Nerd.of(purchase.getPurchaserUuid()).getColoredName() + " &7- " +
				StringUtils.prettyMoney(purchase.getRealPrice()));
		paginate(service.getRecent(), formatter, "/store contributors recent", page);
	}

	@Path("credit [player]")
	void credit(@Arg(value = "self", permission = "group.staff") Contributor contributor) {
		send(PREFIX + (isSelf(contributor) ? "Your" : contributor.getNickname() + "'s") + " store credit: " + contributor.getCreditFormatted());
		// TODO Info on how to convert to coupons
	}

	@Permission("group.admin")
	@Path("credit set <player> <amount>")
	void set(Contributor contributor, double amount) {
		contributor.setCredit(amount);
		service.save(contributor);
		send(PREFIX + "Set &e" + Nickname.of(contributor) + "'s &3balance to &e" + contributor.getCreditFormatted());
	}

	@Permission("group.admin")
	@Path("credit give <player> <amount>")
	void give(Contributor contributor, double amount) {
		contributor.giveCredit(amount);
		service.save(contributor);
		send(PREFIX + "Added &e" + prettyMoney(amount) + " &3to &e" + Nickname.of(contributor) + "'s &3balance. New balance: &e" + contributor.getCreditFormatted());
	}

	@Permission("group.admin")
	@Path("credit take <player> <amount>")
	void take(Contributor contributor, double amount) {
		contributor.takeCredit(amount);
		service.save(contributor);
		send(PREFIX + "Removed &e" + prettyMoney(amount) + " &3from &e" + Nickname.of(contributor) + "'s &3balance. New balance: &e" + contributor.getCreditFormatted());
	}

	@AllArgsConstructor
	private static class StoreProvider extends MenuUtils implements InventoryProvider {
		private final StoreProvider previousMenu;
		private final StoreCategory category;
		private final OfflinePlayer player;

		public StoreProvider(OfflinePlayer player) {
			this.previousMenu = null;
			this.category = null;
			this.player = player;
		}

		@Override
		public void open(Player player, int page) {
			SmartInventory.builder()
					.provider(this)
					.title("Store" + (category == null ? "" : " - " + StringUtils.camelCase(category)))
					.size(6, 9)
					.build()
					.open(player, page);
		}

		@Override
		public void init(Player viewer, InventoryContents contents) {
			if (previousMenu == null)
				addCloseItem(contents);
			else
				addBackItem(contents, e -> previousMenu.open(viewer));

			ItemBuilder info = new ItemBuilder(Material.BOOK).name("&eVisit Store").lore("&f" + URL);
			contents.set(0, 8, ClickableItem.from(info.build(), e -> {
				viewer.closeInventory();
				PlayerUtils.send(player, new JsonBuilder(StringUtils.getPrefix("Store") + "Click me to open the &estore").url(URL));
			}));

			List<ClickableItem> items = new ArrayList<>();

			if (category == null)
				for (StoreCategory category : StoreCategory.values()) {
					ItemBuilder item = category.getDisplayItem();

					int owned = 0;
					int count = category.getPackages().size();
					for (Package storePackage : category.getPackages())
						if (storePackage.has(player))
							++owned;

					item.lore("", "&fOwned: " + (owned == 0 ? "&c0" : owned == count ? "&a" + owned : "&e" + owned));

					if (owned == count)
						item.glow();

					items.add(ClickableItem.from(item.build(), e -> new StoreProvider(this, category, player).open(viewer)));
				}
			else
				for (Package storePackage : category.getPackages()) {
					ItemBuilder item = storePackage.getDisplayItem();
					boolean has = storePackage.has(player);
					int count = storePackage.count(player);

					item.lore("", "&fOwned: " + (has ? "&aYes" + (count == 1 ? "" : " &f(" + count + ")") : "&cNo"));
					if (has)
						item.glow();

					items.add(ClickableItem.empty(item.build()));
				}

			addPagination(viewer, contents, items);
		}

	}

	@TabCompleteIgnore
	@Path("tac")
	void tac() {
		line();
		send("&3Before you donate on the server, here are some things you must know before you do so");
		send(PLUS + "There are no refunds");
		send(PLUS + "If you are under the age of eighteen, be sure to have a parent or guardians permission");
		send(PLUS + "None of the money that is donated goes to a Staff member personally. The money is for improving the server only");
		send(PLUS + "Just because you donate does not mean you can not be banned");
	}

	public static String generateCouponCode() {
		StringBuilder code = new StringBuilder();
		for (int i = 1; i < 15; i++)
			if (i % 5 == 0)
				code.append("-");
			else
				code.append(RandomUtils.randomAlphanumeric());
		return code.toString();
	}

	@Async
	@SneakyThrows
	@Path("createCoupon <player> <amount>")
	@Permission("group.admin")
	void createCoupon(OfflinePlayer offlinePlayer, double amount) {
		Coupon coupon = Coupon.builder()
				.code(generateCouponCode())
				.effective(new Effective("cart", ImmutableList.of(), ImmutableList.of()))
				.basketType("both")
				.discount(new Discount("amount", BigDecimal.ZERO, BigDecimal.valueOf(amount)))
				.discountMethod(2)
				.username(Name.of(offlinePlayer))
				.redeemUnlimited(false)
				.redeemUnlimited(1)
				.minimum(BigDecimal.ZERO)
				.startDate(new Date())
				.expireNever(true)
				.expire(new Expire("timestamp", 0, new Date(System.currentTimeMillis() + 1L)))
				.build();

		Nexus.getBuycraft().getApiClient().createCoupon(coupon).execute();

		send(json(PREFIX + "Created coupon &e" + coupon.getCode()).insert(coupon.getCode()));
	}

	@Path("apply <package> [player]")
	@Permission("group.admin")
	void apply(Package packageType, @Arg("self") OfflinePlayer player) {
		packageType.apply(player);
		send(PREFIX + "Applied package " + camelCase(packageType) + " to " + Nickname.of(player));
	}

	@Path("(expire|remove) <package> [player]")
	@Permission("group.admin")
	void expire(Package packageType, @Arg("self") OfflinePlayer player) {
		packageType.expire(player);
		send(PREFIX + "Removed package " + camelCase(packageType) + " from " + Nickname.of(player));
	}

}

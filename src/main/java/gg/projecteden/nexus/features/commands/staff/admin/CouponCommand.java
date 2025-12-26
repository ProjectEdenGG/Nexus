package gg.projecteden.nexus.features.commands.staff.admin;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.player.UserManager;
import com.mongodb.lang.Nullable;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.mcmmo.reset.McMMOResetProvider;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.models.costume.CostumeUserService;
import gg.projecteden.nexus.models.coupon.CouponService;
import gg.projecteden.nexus.models.coupon.Coupons;
import gg.projecteden.nexus.models.coupon.Coupons.Coupon;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.models.voter.VoterService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@NoArgsConstructor
@HideFromWiki // TODO Document after recode
@Aliases("coupons")
@Permission(Group.ADMIN)
public class CouponCommand extends CustomCommand implements Listener {
	private final CouponService service = new CouponService();
	private final Coupons coupons = service.get0();

	@Getter
	@AllArgsConstructor
	public enum CouponEvent {
		ECO(false) {
			@Override
			void use(PlayerInteractEvent event, @NotNull ItemStack item) {
				int amount = extractValue(item);
				new BankerService().deposit(event.getPlayer(), amount, ShopGroup.of(event.getPlayer()), TransactionCause.COUPON);
				item.subtract();
				PlayerUtils.send(event.getPlayer(), "&e$" + amount + " &3has been added to your account");
			}
		},
		VPS(false) {
			@Override
			void use(PlayerInteractEvent event, @NotNull ItemStack item) {
				int amount = extractValue(item);
				new VoterService().edit(event.getPlayer(), voter -> voter.givePoints(amount));
				PlayerUtils.send(event.getPlayer(), "&3You have been given &e" + amount + "&3 vote points");
				item.subtract();
			}
		},
		MCMMO(false) {
			@Override
			void use(PlayerInteractEvent event, @NotNull ItemStack item) {
				new McMMOLevelCouponProvider(extractValue(item)).open(event.getPlayer());
			}
		},
		EVENT_TOKENS(false) {
			@Override
			void use(PlayerInteractEvent event, @NotNull ItemStack item) {
				new EventUserService().edit(event.getPlayer(), user -> user.giveTokens(extractValue(item)));
				item.subtract();
			}
		},
		PAINTING(false) {
			@Override
			void use(PlayerInteractEvent event, @NotNull ItemStack item) {
				// TODO
				PlayerUtils.send(event.getPlayer(), "Make a /ticket or post in #questions to claim this coupon.");
			}
		},
		SONG(false) {
			@Override
			void use(PlayerInteractEvent event, @NotNull ItemStack item) {
				PlayerUtils.runCommand(event.getPlayer(), "jukebox store");
			}
		},
		EXPERIENCE_75(true) {
			@Override
			void use(PlayerInteractEvent event, @NotNull ItemStack item) {
				event.getPlayer().giveExpLevels(75);
			}
		},
		COSTUME(true) {
			@Override
			void use(PlayerInteractEvent event, @NotNull ItemStack item) {
				new CostumeUserService().edit(event.getPlayer(), user -> user.addVouchers(1));
				PlayerUtils.runCommand(event.getPlayer(), "costumes store");
			}
		},
		COSTUMES_5(true) {
			@Override
			void use(PlayerInteractEvent event, @NotNull ItemStack item) {
				new CostumeUserService().edit(event.getPlayer(), user -> user.addVouchers(5));
				PlayerUtils.runCommand(event.getPlayer(), "costumes store");
			}
		};

		private final boolean autoremove;

		public void removeItem(Player player) {
			Coupon coupon = getCoupon();
			if (coupon == null) {
				Nexus.log("Tried to remove coupon item " + name().toLowerCase() + " from " + player.getName() + " which does not exist");
				return;
			}

			player.getInventory().removeItem(coupon.getItem());
		}

		public Coupons getCoupons() {
			final CouponService service = new CouponService();
			return service.get0();
		}

		public Coupon getCoupon() {
			return getCoupons().of(name());
		}

		public void handle(PlayerInteractEvent event) {
			use(event, Objects.requireNonNull(event.getItem()));
			if (autoremove) {
				getCoupon().use();
				new CouponService().save(getCoupons());
				removeItem(event.getPlayer());
			}
		}

		abstract void use(PlayerInteractEvent event, @NotNull ItemStack item);

		public static CouponEvent of(String id) {
			try {
				return valueOf(id.toUpperCase());
			} catch (IllegalArgumentException ex) {
				return null;
			}
		}

		public int extractValue(@Nullable ItemStack item) {
			if (item == null)
				return 0;
			List<String> lore = item.getLore();
			return Integer.parseInt(lore.get(0).split(StringUtils.colorize(": &e"))[1]);
		}

	}

	public CouponCommand(CommandEvent event) {
		super(event);
	}

	@Path("list [page]")
	void list(@Arg("1") int page) {
		if (coupons.getCoupons().isEmpty())
			error("No coupons have been created");

		send(PREFIX + "Created coupons:");

		BiFunction<Coupon, String, JsonBuilder> json = (coupon, index) -> json()
				.next(" &e" + coupon.getId() + " &7- " + coupon.getUses())
				.command("/coupons get " + coupon.getId())
				.hover(coupon.getItem());

		new Paginator<Coupon>()
			.values(coupons.getCoupons())
			.formatter(json)
			.command("/coupon list")
			.page(page)
			.send();
	}

	@Path("save <id>")
	void save(@Arg(tabCompleter = Coupon.class, regex = "^[\\w]+$") String id) {
		id = id.toLowerCase();
		if (coupons.of(id) != null)
			error("Coupon &e" + id + " &calready exists, use /coupon update <id>");

		Coupon coupon = new Coupon(id, getToolRequired());
		coupons.getCoupons().add(coupon);
		service.save(coupons);
		send(json(PREFIX + "Coupon &e" + id + " &3created").hover("Add your coupon logic to the code", "Click to copy the coupon's ID").copy(id));
	}

	@Path("update <id>")
	void update(Coupon coupon) {
		coupons.getCoupons().remove(coupon);
		save(coupon.getId());
	}

	@Path("delete <id>")
	void delete(Coupon coupon) {
		coupons.getCoupons().remove(coupon);
		service.save(coupons);
		send(json(PREFIX + "Coupon &e" + coupon.getId() + " &3deleted"));
	}

	@Path("get <id>")
	void get(Coupon coupon) {
		giveItem(coupon.getItem());
		send(PREFIX + "Giving coupon &e" + coupon.getId() + " &3(" + coupon.getUses() + " uses)");
	}

	@Path("get <id> <amount>")
	void generic(Coupon coupon, Integer amount) {
		ItemStack couponItem = getGenericCoupon(coupon.getId(), amount);
		giveItem(couponItem);
	}

	public static ItemStack getGenericCoupon(String id, Integer amount) {
		CouponService service = new CouponService();
		Coupons coupons = service.get0();

		Coupon coupon = coupons.of(id);
		ItemStack itemStack = coupon.getItem().clone();
		ItemMeta meta = itemStack.getItemMeta();
		List<String> lore = meta.getLore();
		if (lore == null)
			lore = new ArrayList<>();

		lore.set(0, StringUtils.colorize("&3Amount: &e" + amount));
		meta.setLore(lore);
		itemStack.setItemMeta(meta);

		return itemStack;
	}

	@ConverterFor(Coupon.class)
	Coupon convertToCoupon(String value) {
		Coupon coupon = coupons.of(value);
		if (coupon == null)
			error("Coupon from ID &e" + value + " &cnot found");
		return coupon;
	}

	@TabCompleterFor(Coupon.class)
	List<String> tabCompleteCoupon(String filter) {
		return coupons.getCoupons().stream()
				.map(Coupon::getId)
				.filter(id -> id.startsWith(filter.toLowerCase()))
				.collect(Collectors.toList());
	}

	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		if (!ActionGroup.CLICK.applies(event)) return;
		if (Nullables.isNullOrAir(event.getItem())) return;

		final CouponService service = new CouponService();
		final Coupons coupons = service.get0();

		Coupon coupon = coupons.of(event.getItem());
		if (coupon == null) return;

		CouponEvent couponEvent = CouponEvent.of(coupon.getId());
		if (couponEvent != null)
			couponEvent.handle(event);
	}

	@Title("Select McMMO Skill")
	@AllArgsConstructor
	public static class McMMOLevelCouponProvider extends InventoryProvider {
		private static final int MAX_LEVEL = 200;
		private int levels;

		@Override
		public void init() {
			ItemStack coupon = viewer.getInventory().getItemInMainHand();
			McMMOPlayer mcmmoPlayer = UserManager.getPlayer(viewer);
			for (McMMOResetProvider.ResetSkillType skill : McMMOResetProvider.ResetSkillType.values()) {
				ItemStack item = new ItemBuilder(skill.getMaterial())
					.name("&e" + StringUtils.camelCase(skill.name()))
					.lore("&3Level: &e" + mcmmoPlayer.getSkillLevel(PrimarySkillType.valueOf(skill.name())))
					.build();

				contents.set(skill.getRow(), skill.getColumn(), ClickableItem.of(item, e -> {
					int mcMMOLevel = mcmmoPlayer.getSkillLevel(PrimarySkillType.valueOf(skill.name()));
					if (mcMMOLevel >= MAX_LEVEL)
						return;

					levels = Math.min(MAX_LEVEL - mcMMOLevel, levels);
					PlayerUtils.runCommandAsConsole("addlevels " + viewer.getName() + " " + skill.name().toLowerCase() + " " + levels);
					coupon.subtract();
					viewer.closeInventory();
				}));
			}

		}
	}

}

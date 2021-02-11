package me.pugabyte.nexus.features.commands.staff;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.coupon.CouponService;
import me.pugabyte.nexus.models.coupon.Coupons;
import me.pugabyte.nexus.models.coupon.Coupons.Coupon;
import me.pugabyte.nexus.models.eventuser.EventUser;
import me.pugabyte.nexus.models.eventuser.EventUserService;
import me.pugabyte.nexus.models.vote.Voter;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Utils.ActionGroup;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static me.pugabyte.nexus.utils.ItemUtils.giveItem;

@NoArgsConstructor
@Aliases("coupons")
@Permission("group.admin")
public class CouponCommand extends CustomCommand implements Listener {
	private final CouponService service = new CouponService();
	private final Coupons coupons = service.get(Nexus.getUUID0());

	@Getter
	@AllArgsConstructor
	public enum CouponEvent {
		ECO(false) {
			@Override
			void use(PlayerInteractEvent event) {
				int amount = extractValue(event.getItem());
				PlayerUtils.runCommandAsConsole("eco give " + event.getPlayer().getName() + " " + amount);
				ItemStack item = event.getItem();
				item.setAmount(item.getAmount() - 1);
			}
		},
		VPS(false) {
			@Override
			void use(PlayerInteractEvent event) {
				int amount = extractValue(event.getItem());
				new Voter(event.getPlayer()).givePoints(amount);
				PlayerUtils.send(event.getPlayer(), "&3You have been given &e" + amount + "&3 vote points");
				ItemStack item = event.getItem();
				item.setAmount(item.getAmount() - 1);
			}
		},
		MCMMO(false) {
			@Override
			void use(PlayerInteractEvent event) {
				// TODO: McMMO Menu
				PlayerUtils.send(event.getPlayer(), "&3Coming soon! &eContact an admin to redeem for now");
			}
		},
		PUGMAS20_ADVENT_PAINTING(false) {
			@Override
			void use(PlayerInteractEvent event) {
				PlayerUtils.send(event.getPlayer(), Pugmas20.PREFIX + "This coupon will be claimable at the end of the month");
			}
		},
		PUGMAS20_ADVENT_SONG(false) {
			@Override
			void use(PlayerInteractEvent event) {
				PlayerUtils.send(event.getPlayer(), Pugmas20.PREFIX + "This coupon will be claimable at the end of the month");
			}
		},
		PUGMAS20_ADVENT_5000(true) {
			@Override
			void use(PlayerInteractEvent event) {
				PlayerUtils.runCommandAsConsole("eco give " + event.getPlayer().getName() + " 5000");
			}
		},
		PUGMAS20_ITEM_PAINTING(false) {
			@Override
			void use(PlayerInteractEvent event) {
				PlayerUtils.send(event.getPlayer(), Pugmas20.PREFIX + "This coupon will be claimable at the end of the month");
			}
		},
		PUGMAS20_ITEM_SONG(false) {
			@Override
			void use(PlayerInteractEvent event) {
				PlayerUtils.send(event.getPlayer(), Pugmas20.PREFIX + "This coupon will be claimable at the end of the month");
			}
		},
		PUGMAS20_100_EVENT_TOKENS(true) {
			@Override
			void use(PlayerInteractEvent event) {
				EventUserService eventUserService = new EventUserService();
				EventUser user = eventUserService.get(event.getPlayer());
				user.giveTokens(100);
				eventUserService.save(user);
			}
		},
		EXPERIENCE_75(true) {
			@Override
			void use(PlayerInteractEvent event) {
				event.getPlayer().giveExpLevels(75);
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
			return service.get(Nexus.getUUID0());
		}

		public Coupon getCoupon() {
			return getCoupons().of(name());
		}

		public void handle(PlayerInteractEvent event) {
			use(event);
			if (autoremove) {
				getCoupon().use();
				new CouponService().save(getCoupons());
				removeItem(event.getPlayer());
			}
		}

		abstract void use(PlayerInteractEvent event);

		public static CouponEvent of(String id) {
			try {
				return valueOf(id.toUpperCase());
			} catch (IllegalArgumentException ex) {
				return null;
			}
		}

		public int extractValue(ItemStack item) {
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

		BiFunction<Coupon, Integer, JsonBuilder> json = (coupon, index) -> json()
				.next(" &e" + coupon.getId() + " &7- " + coupon.getUses())
				.command("/coupons get " + coupon.getId())
				.hover(coupon.getItem());

		paginate(coupons.getCoupons(), json, "/coupon list", page);
	}

	@Path("save <id>")
	void save(@Arg(tabCompleter = Coupon.class, regex = "^[a-zA-Z0-9_]+$") String id) {
		id = id.toLowerCase();
		if (coupons.of(id) != null)
			error("Coupon &e" + id + " &calready exists, use /coupon update <id>");

		Coupon coupon = new Coupon(id, getToolRequired());
		coupons.getCoupons().add(coupon);
		service.save(coupons);
		send(json(PREFIX + "Coupon &e" + id + " &3created").hover("Add your coupon logic to the code\nClick to copy the coupon's ID").copy(id));
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
		giveItem(player(), coupon.getItem());
		send(PREFIX + "Giving coupon &e" + coupon.getId() + " &3(" + coupon.getUses() + " uses)");
	}

	@Path("get (eco|vps|mcmmo) <amount>")
	void generic(Integer amount) {
		String type = arg(2);
		Coupon coupon = coupons.of(type);
		ItemStack itemStack = coupon.getItem().clone();
		ItemMeta meta = itemStack.getItemMeta();
		List<String> lore = meta.getLore();
		lore.set(0, StringUtils.colorize("&3Amount: &e" + amount));
		meta.setLore(lore);
		itemStack.setItemMeta(meta);
		player().getInventory().addItem(itemStack);
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
		if (ItemUtils.isNullOrAir(event.getItem())) return;

		final CouponService service = new CouponService();
		final Coupons coupons = service.get(Nexus.getUUID0());

		Coupon coupon = coupons.of(event.getItem());
		if (coupon == null) return;

		CouponEvent couponEvent = CouponEvent.of(coupon.getId());
		if (couponEvent != null)
			couponEvent.handle(event);
	}

}

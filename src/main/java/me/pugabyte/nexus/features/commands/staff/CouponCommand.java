package me.pugabyte.nexus.features.commands.staff;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.Nexus;
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
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.Utils;
import me.pugabyte.nexus.utils.Utils.ActionGroup;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static me.pugabyte.nexus.utils.ItemUtils.giveItem;

@NoArgsConstructor
@Aliases("coupons")
@Permission("group.admin")
public class CouponCommand extends CustomCommand implements Listener {
	private final CouponService service = new CouponService();
	private final Coupons coupons = service.get(Nexus.getUUID0());

	private static final Map<String, Consumer<PlayerInteractEvent>> map = new HashMap<String, Consumer<PlayerInteractEvent>>() {{
		put("pugmas20_advent_painting", event -> Utils.send(event.getPlayer(), "pugmas20_advent_painting"));
		put("pugmas20_advent_song", event -> Utils.send(event.getPlayer(), "pugmas20_advent_song"));
		put("pugmas20_item_painting", event -> Utils.send(event.getPlayer(), "pugmas20_item_painting"));
		put("pugmas20_item_song", event -> Utils.send(event.getPlayer(), "pugmas20_item_song"));
	}};

	public CouponCommand(CommandEvent event) {
		super(event);
	}

	@Path("[page]")
	void list(@Arg("1") int page) {
		if (coupons.getCoupons().isEmpty())
			error("No coupons have been created");

		send(PREFIX + "Created coupons:");

		Function<Coupon, JsonBuilder> json = coupon -> json()
				.next(" &e" + coupon.getId() + " &7- " + coupon.getUses())
				.command("/coupons get " + coupon.getId())
				.hover(coupon.getItem());

		paginate(coupons.getCoupons(), json, "/coupon list", page);
	}

	@Path("save <id>")
	void save(@Arg(tabCompleter = Coupon.class, regex = "^[a-zA-Z0-9_]+$") String id) {
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

		if (map.containsKey(coupon.getId()))
			map.get(coupon.getId()).accept(event);
	}


}

package me.pugabyte.nexus.features.store;

import com.google.common.collect.ImmutableList;
import lombok.SneakyThrows;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.store.perks.NPCListener;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.Utils;
import net.buycraft.plugin.data.Coupon;
import net.buycraft.plugin.data.Coupon.Discount;
import net.buycraft.plugin.data.Coupon.Effective;
import net.buycraft.plugin.data.Coupon.Expire;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;
import java.util.Date;

@Aliases({"donate", "buy"})
public class StoreCommand extends CustomCommand {
	private static final String PLUS = "&3[+] &e";

	public StoreCommand(CommandEvent event) {
		super(event);
	}

	static {
		Utils.tryRegisterListener(new NPCListener());
	}

	@Path
	void donate() {
		line();
		send("&3Enjoying the server? &3Share the love by &edonating&3! We are always extremely grateful for donations, " +
				"and they come with some cool &erewards&3!");
		line();
		send(json().next("&3Visit our store: &ehttps://store.projecteden.gg"));
		line();
		send(json(PLUS + "Terms and Conditions").hover(PLUS + "Click here before you donate for anything.").command("/donate tac"));
	}

	@TabCompleteIgnore
	@Path("tac")
	void tac() {
		line();
		send("&3Before you donate on the server, here are some things you must know before you do so.");
		send(PLUS + "There are no refunds.");
		send(PLUS + "If you are under the age of eighteen, be sure to have a parent or guardians permission.");
		send(PLUS + "None of the money that is donated goes to a Staff member personally. The money is for improving " +
				"the server only.");
		send(PLUS + "Just because you donate does not mean you can not be banned.");
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
				.username(offlinePlayer.getName())
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
		send(PREFIX + "Applied package " + camelCase(packageType) + " to " + player.getName());
	}

	@Path("(expire|remove) <package> [player]")
	@Permission("group.admin")
	void expire(Package packageType, @Arg("self") OfflinePlayer player) {
		packageType.expire(player);
		send(PREFIX + "Removed package " + camelCase(packageType) + " from " + player.getName());
	}

}

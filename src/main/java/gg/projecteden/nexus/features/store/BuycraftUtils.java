package gg.projecteden.nexus.features.store;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.models.contributor.Contributor;
import gg.projecteden.nexus.utils.HttpUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SerializationUtils.Json;
import okhttp3.Headers;

import java.time.LocalDate;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

public class BuycraftUtils {

	public static String getSecret() {
		return Nexus.getBuycraft().getConfiguration().getServerKey();
	}

	public static Headers headers() {
		return Headers.of("X-Tebex-Secret", getSecret());
	}

	public static String url(String path) {
		return "https://plugin.tebex.io/" + path;
	}

	private static void post(String path, Object object) {
		HttpUtils.post(url(path), headers(), Json.of(object));
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

	@SuppressWarnings({"unused", "FieldCanBeLocal"})
	public static class CouponCreator {
		private final String username;
		private final double discount_amount;
		private final String code = generateCouponCode();
		private final String basket_type = "both";
		private final int discount_application_method = 2;
		private final double discount_percentage = 0;
		private final String discount_type = "value";
		private final String effective_on = "cart";
		private final boolean expire_never = true;
		private final double minimum = 0;
		private final boolean redeem_unlimited = false;
		private final String start_date = LocalDate.now().format(ISO_LOCAL_DATE);

		public CouponCreator(Contributor user, double amount) {
			this.username = user.getName();
			this.discount_amount = amount;
		}

		public String create() {
			post("coupons", this);
			return code;
		}

	}

}

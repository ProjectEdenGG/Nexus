package me.pugabyte.nexus.models.contributor;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.LocalDateTimeConverter;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.store.Package;
import me.pugabyte.nexus.framework.exceptions.preconfigured.NegativeBalanceException;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.lang.reflect.Modifier;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Entity("contributor")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocalDateTimeConverter.class})
public class Contributor implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<Purchase> purchases = new ArrayList<>();
	private double credit;

	public void add(Purchase purchase) {
		purchases.removeIf(_purchase -> purchase.getId().equals(_purchase.getId()));
		purchases.add(purchase);
	}

	public double getSum() {
		return new HashMap<String, Double>() {{
			for (Purchase purchase : purchases)
				put(purchase.getTransactionId(), purchase.getPrice());
		}}.values().stream()
				.mapToDouble(Double::valueOf)
				.sum();
	}

	public String getSumFormatted() {
		return NumberFormat.getCurrencyInstance().format(getSum());
	}

	public void giveCredit(double credit) {
		setCredit(this.credit + credit);
	}

	public void takeCredit(double credit) {
		setCredit(this.credit - credit);
	}

	public void setCredit(double credit) {
		if (credit < 0)
			throw new NegativeBalanceException();

		this.credit = credit;
	}

	public String getCreditFormatted() {
		return NumberFormat.getCurrencyInstance().format(credit);
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Purchase implements PlayerOwnedObject {
		private UUID id;
		private String name;
		private UUID uuid;
		private String transactionId;
		private double price;
		private String currency;
		private LocalDateTime timestamp;
		private String email;
		private String ip;
		private String packageId;
		private double packagePrice;
		private String packageExpiry;
		private String packageName;
		private String purchaserName;
		private UUID purchaserUuid;

		public OfflinePlayer getPurchaserOfflinePlayer() {
			return Bukkit.getOfflinePlayer(purchaserUuid);
		}

		public double getRealPrice() {
			if (Package.CUSTOM_DONATION.getId().equals(packageId))
				return price;
			else
				return packagePrice;
		}

		private static Converter<String, String> caseConverter = CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE);

		public String toDiscordString() {
			final StringBuilder message = new StringBuilder("Purchase caught; processing...\n```");

			Arrays.asList(getClass().getDeclaredFields()).forEach(field -> {
				if (Modifier.isStatic(field.getModifiers())) return;
				try {
					String name = StringUtils.camelCase(caseConverter.convert(field.getName()));
					String value = field.get(this).toString();
					message.append(name).append(": ").append(value).append("\n");
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			});

			message.append("```");

			return message.toString();
		}
	}

}

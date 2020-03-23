package me.pugabyte.bncore.models.purchase;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import lombok.Builder;
import lombok.Data;
import me.pugabyte.bncore.utils.StringUtils;

import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.util.Arrays;

@Data
@Builder
public class Purchase {
	private String name;
	private String uuid;
	private String transactionId;
	private double price;
	private String currency;
	private LocalDateTime timestamp;
	private String email;
	private String ip;
	private int packageId;
	private double packagePrice;
	private String packageExpiry;
	private String packageName;
	private String purchaserName;
	private String purchaserUuid;

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

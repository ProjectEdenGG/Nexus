package me.pugabyte.nexus.models.balanceconverter;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

@Data
@Builder
@Entity("balance_converter")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class BalanceConverter extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<String, Double> balances = new HashMap<>();
	private double result;

	public void findBalances() {
		balances.clear();

		File ess = Paths.get("plugins/Essentials/userdata/" + getUuid().toString() + ".yml").toFile();

		YamlConfiguration essConfig = YamlConfiguration.loadConfiguration(ess);

		balances.put("essentials", essConfig.getDouble("money", 0));

		File mwm = Paths.get("plugins/MultiWorldMoney/players/" + getUuid().toString() + ".yml").toFile();
		if (!mwm.exists())
			return;

		YamlConfiguration mwmConfig = YamlConfiguration.loadConfiguration(mwm);
		ConfigurationSection mwmBalances = mwmConfig.getConfigurationSection("balances");
		if (mwmBalances == null)
			return;

		Set<String> keys = mwmBalances.getKeys(false);
		keys.forEach(world -> {
			if (world.contains("skyblock")) return;
			double balance = mwmBalances.getDouble(world);
			if (balance > 0)
				balances.put(world, balance);
		});
	}

	@ToString.Include
	public double sum() {
		return reduce(BigDecimal::add);
	}

	@ToString.Include
	public double max() {
		return reduce(BigDecimal::max);
	}

	public double reduce(BinaryOperator<BigDecimal> operator) {
		return stream().reduce(BigDecimal.ZERO, operator).doubleValue();
	}

	@NotNull
	public Stream<BigDecimal> stream() {
		return balances.values().stream().map(BigDecimal::valueOf);
	}


}

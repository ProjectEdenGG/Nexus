package gg.projecteden.nexus.features.customenchants;

import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

import static gg.projecteden.nexus.utils.StringUtils.stripColor;

public class OldCEConverter {

	static {
		Tasks.repeat(100, 100, () -> {
			for (Player player : PlayerUtils.getOnlinePlayers()) {
				for (ItemStack item : player.getInventory()) {
					convertItem(item);
				}
			}
		});
	}

	public static void convertItem(ItemStack item) {
		if (ItemUtils.isNullOrAir(item)) return;
		if (item.getItemMeta() == null || item.getItemMeta().getLore() == null || item.getItemMeta().getLore().isEmpty()) return;
		for (String line : new ArrayList<>(item.getItemMeta().getLore())) {
			String ogLine = line;
			for (ConversionEnchant enchant : ConversionEnchant.values()) {
				if (enchant.getEnchant() == null) continue;
				if (item.getItemMeta().hasEnchant(enchant.getEnchant())) return;;
				line = stripColor(line);
				if (line.matches(String.format("(?i)^%s.*", enchant.getCEName()))) {
					int level = 1;
					if (line.matches(String.format("(?i)%s ((I?X|IV|V?I{0,3})|\\d)", enchant.getCEName()))) {
						try {
							level = StringUtils.fromRoman(line.replace(String.format("(?i)%s", enchant.getCEName()), ""));
						} catch (Exception ignore) { } // invalid roman numeral parsing
					}
					item.getItemMeta().getLore().remove(ogLine);
					item.addUnsafeEnchantment(enchant.getEnchant(), level);
					CustomEnchants.update(item);
				}
			}
		}
	}

	/**
	 * When adding new Nexus Custom Enchants that replace old CE enchants,
	 * add an enum value here with with respected data:
	 *
	 * Enum name: Old CE name, respecting underscores, case insensitive
	 * CustomEnchant: The enchant value from the {@link Enchant} class
	 */
	@Getter
	@AllArgsConstructor
	public enum ConversionEnchant {
		GLOWING(Enchant.GLOWING),
		AUTOREPAIR(Enchant.AUTOREPAIR);

		CustomEnchant enchant;

		public String getCEName() {
			return name().toLowerCase().replace("_", " ");
		}

	}


}

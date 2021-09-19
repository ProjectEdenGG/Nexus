package gg.projecteden.nexus.features.customenchants;

import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

import static gg.projecteden.nexus.utils.StringUtils.stripColor;

public class OldCEConverter {

	static void load() {
		Tasks.sync(new AtomicReference<Runnable>() {{
			set(() -> {
				final Iterator<Player> iterator = OnlinePlayers.getAll().iterator();

				int wait = 0;
				Tasks.wait(TickTime.SECOND.x(++wait), () -> {
					if (!iterator.hasNext()) {
						Tasks.sync(get());
						return;
					}

					final Player player = iterator.next();
					if (!player.isOnline())
						return;

					for (ItemStack item : player.getInventory())
						convertItem(item);
				});
			});
		}}.get());
	}

	public static void convertItem(ItemStack item) {
		if (ItemUtils.isNullOrAir(item))
			return;
		if (item.getItemMeta() == null)
			return;
		if (item.getItemMeta().getLore() == null)
			return;
		if (item.getItemMeta().getLore().isEmpty())
			return;

		for (String line : new ArrayList<>(item.getItemMeta().getLore())) {
			String ogLine = line;
			for (ConversionEnchant enchant : ConversionEnchant.values()) {
				if (enchant.getEnchant() == null)
					continue;
				if (item.getItemMeta().hasEnchant(enchant.getEnchant()))
					continue;

				line = stripColor(line);

				if (!line.matches(String.format("(?i)^%s.*", enchant.getCEName())))
					continue;

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
		AUTOREPAIR(Enchant.AUTOREPAIR),
		THUNDERINGBLOW(Enchant.THUNDERINGBLOW);

		CustomEnchant enchant;

		public String getCEName() {
			return name().toLowerCase().replace("_", " ");
		}

	}


}

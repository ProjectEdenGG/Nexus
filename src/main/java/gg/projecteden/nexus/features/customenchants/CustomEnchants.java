package gg.projecteden.nexus.features.customenchants;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.customenchants.models.CustomEnchant;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NoArgsConstructor;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class CustomEnchants extends Feature implements Listener {
	private static final Map<Class<? extends CustomEnchant>, Enchantment> enchants = new HashMap<>();

	public static Enchantment get(Class<? extends CustomEnchant> clazz) {
		return enchants.computeIfAbsent(clazz, $ -> CustomEnchantsRegistration.register(Nexus.singletonOf(clazz)));
	}

	public static Collection<Enchantment> getEnchants() {
		return enchants.values();
	}

	static Map<Class<? extends CustomEnchant>, Enchantment> getEnchantsMap() {
		return enchants;
	}

	public static Enchantment get(Key key) {
		return enchants.values().stream().filter(enchant -> enchant.getKey().getKey().equals(key.value())).findFirst().orElse(null);
	}

	@Override
	public void onStart() {
		CustomEnchantsRegistration.freeze();
	}

	@NotNull
	public static NamespacedKey getKey(Class<? extends CustomEnchant> enchant) {
		return CustomEnchantsRegistration.getKey(getId(enchant));
	}

	@NotNull
	public static String getId(Class<? extends CustomEnchant> enchant) {
		return StringUtils.camelToSnake(enchant.getSimpleName()).toLowerCase().replace("_enchant", "");
	}

}

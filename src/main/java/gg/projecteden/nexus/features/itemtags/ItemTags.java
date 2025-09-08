package gg.projecteden.nexus.features.itemtags;

import gg.projecteden.nexus.features.customenchants.CustomEnchants;
import gg.projecteden.nexus.framework.features.Depends;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.IOUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Depends(CustomEnchants.class)
public class ItemTags extends Feature {
	private static final Map<Enchantment, List<Level>> enchantsConfigMap = new HashMap<>();
	private static final Map<String, Integer> armorConfigMap = new HashMap<>();
	private static final Map<String, Integer> toolConfigMap = new HashMap<>();

	@Getter
	private static final String fileName = "itemtags.yml";
	private static YamlConfiguration config;

	@Data
	@AllArgsConstructor
	private static class Level {
		String name;
		int value;
		boolean uncraftable;
	}

	@Override
	public void onStart() {
		reloadConfig();
		new ItemTagsListener();
	}

	public static void reloadConfig() {
		config = IOUtils.getNexusConfig(fileName);
		loadConfigMaps();
	}

	public static int getEnchantVal(Enchantment enchant, int lvl, RarityArgs args) {
		List<Level> levels = enchantsConfigMap.get(enchant);
		if (levels == null || levels.isEmpty())
			return 0;

		int enchantLvl = 0;
		for (Level level : levels) {
			try {
				enchantLvl = Integer.parseInt(level.getName());
				if (level.isUncraftable() && !args.isAboveVanillaEnchants())
					args.setAboveVanillaEnchants(true);

				if (enchantLvl == lvl)
					return level.getValue();

			} catch (Exception ignored) {

			}
		}

		// "above"
		if (lvl > enchantLvl) {
			if (!args.isAboveVanillaEnchants())
				args.setAboveVanillaEnchants(true);

			return levels.getLast().getValue();
		}

		return 0;
	}

	public static Integer getArmorMaterialVal(Material material) {
		String type = parseMaterial(material.name());
		if (!armorConfigMap.containsKey(type))
			return null;

		return armorConfigMap.get(type);
	}

	public static Integer getToolMaterialVal(Material material) {
		String type = parseMaterial(material.name());
		if (!toolConfigMap.containsKey(type))
			return null;

		return toolConfigMap.get(type);
	}

	private static void loadConfigMaps() {
		ConfigurationSection enchantments = config.getConfigurationSection("ItemTags.Enchantments");
		if (enchantments != null) {
			for (String key : enchantments.getKeys(false)) {
				Enchantment enchant = parseEnchantment(key);
				if (enchant == null)
					continue;

				List<Level> levels = new ArrayList<>();
				ConfigurationSection section = config.getConfigurationSection(enchantments.getCurrentPath() + "." + key);
				if (section != null) {
					boolean uncraftable = section.getBoolean("uncraftable", false);
					for (String sectionKey : section.getKeys(false)) {
						int value = section.getInt(sectionKey);
						levels.add(new Level(sectionKey, value, uncraftable));
					}
				}

				enchantsConfigMap.put(enchant, levels);
			}
		}

		ConfigurationSection armorMaterials = config.getConfigurationSection("ItemTags.Material.Armor");
		if (armorMaterials != null) {
			for (String key : armorMaterials.getKeys(false)) {
				String material = parseMaterial(key);
				int value = armorMaterials.getInt(key);
				armorConfigMap.put(material, value);
			}
		}

		ConfigurationSection toolMaterials = config.getConfigurationSection("ItemTags.Material.Tool");
		if (toolMaterials != null) {
			for (String key : toolMaterials.getKeys(false)) {
				String material = parseMaterial(key);
				int value = toolMaterials.getInt(key);
				toolConfigMap.put(material, value);
			}
		}
	}

	private static Enchantment parseEnchantment(String value) {
		return Enchant.values().stream()
			.filter(enchantment -> enchantment.getKey().getKey().equalsIgnoreCase(value))
			.findFirst()
			.orElse(null);
	}

	public static String parseMaterial(String materialName) {
		String[] strings = materialName.toLowerCase().split("_");
		String ingot = strings[0];

		// Special cases
		if (ingot.equalsIgnoreCase("golden"))
			ingot = "gold";
		else if (ingot.equalsIgnoreCase("turtle"))
			ingot = "turtle_shell";
		else if (ingot.equalsIgnoreCase("wooden"))
			ingot = "wood";
		//

		return StringUtils.camelCase(ingot).replace(" ", "_");
	}

	public static void debug(Player player, JsonBuilder json) {
		if (player != null && player.isOnline())
			json.send(player);
	}

	public static void debug(Player player, String message) {
		if (player != null && player.isOnline())
			player.sendMessage(StringUtils.colorize(message));
	}

}


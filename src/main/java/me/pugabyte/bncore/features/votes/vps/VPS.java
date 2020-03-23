package me.pugabyte.bncore.features.votes.vps;

import fr.minuskube.inv.SmartInventory;
import lombok.SneakyThrows;
import me.pugabyte.bncore.features.votes.vps.VPSMenu.VPSItem;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class VPS {
	private static YamlConfiguration config = getFile();
	private static Map<String, Map<Integer, VPSMenu>> menus = new HashMap<>();

	public VPS() {
		Tasks.async(VPS::read);
	}

	private static void read() {
		config.getRoot().getKeys(false).forEach(group -> {
			Map<Integer, VPSMenu> pages = menus.computeIfAbsent(group, $ -> new HashMap<>());
			ConfigurationSection groupSection = config.getConfigurationSection(group);

			groupSection.getKeys(false).stream().map(Integer::valueOf).forEach(page -> {
				VPSMenu menu = pages.computeIfAbsent(page, $ -> new VPSMenu());
				ConfigurationSection pageSection = groupSection.getConfigurationSection(page.toString());

				if (pageSection.contains("rows"))
					menu.setRows(pageSection.getInt("rows"));

				groupSection.getKeys(false).stream().filter(Utils::isInt).map(Integer::valueOf).forEach(slot -> {
					ConfigurationSection slotSection = groupSection.getConfigurationSection(slot.toString());
					VPSItem item = VPSItem.builder()
							.name(slotSection.getString("name"))
							.display((ItemStack) ConfigurationSerialization.deserializeObject((Map<String, ?>) slotSection.getConfigurationSection("display")))
							.price(slotSection.getInt("price"))
							.takePoints(slotSection.getBoolean("takePoints"))
							.close(slotSection.getBoolean("close"))
							.build();
					menu.addItem(slot, item);

				});
			});
		});
	}

	@SneakyThrows
	private static YamlConfiguration getFile() {
		File file = new File("plugins/BNCore/vps.yml");
		if (!file.exists()) file.createNewFile();
		return YamlConfiguration.loadConfiguration(file);
	}

	public static void open(Player player, String type, int page) {
		VPSMenu menu = getMenu(type, page);
		SmartInventory.builder()
				.provider(new VPSProvider(menu))
				.size(menu.getRows(), 9)
				.title("&3Vote Point Store")
				.build()
				.open(player);
	}

	public static VPSMenu getMenu(String type, int page) {
		if (!menus.containsKey(type))
			throw new InvalidInputException("Unknown menu");
		if (!menus.get(type).containsKey(page))
			throw new InvalidInputException("Invalid page");

		return menus.get(type).get(page);
	}

}

package me.pugabyte.bncore.features.votes.mysterychest;

import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.menus.rewardchests.RewardChestLoot;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class MysteryChest {
	private final SettingService service = new SettingService();
	private final OfflinePlayer player;
	@Getter
	private static String file = "plugins/BNCore/mysteryChestLoot.yml";

	public MysteryChest(OfflinePlayer player) {
		this.player = player;
	}

	public int give(int amount) {
		Setting setting = getSetting();
		setting.setInt(setting.getInt() + amount);
		service.save(setting);
		return setting.getInt();
	}

	public int take(int amount) {
		Setting setting = getSetting();
		setting.setInt(setting.getInt() - amount);
		service.save(setting);
		return setting.getInt();
	}

	public Setting getSetting() {
		return service.get(player, "mysteryChest");
	}

	public static FileConfiguration getConfig() {
		File file = new File(getFile());
		if (!file.exists()) {
			try {
				if (!file.createNewFile())
					BNCore.warn("File " + file.getName() + " already exists");
			} catch (IOException ex) {
				BNCore.severe("An error occurred while trying to create a configuration file: " + ex.getMessage());
			}
		}
		return YamlConfiguration.loadConfiguration(file);
	}

	public static Set<String> getConfigSections() {
		return getConfig().getConfigurationSection("").getKeys(false);
	}

	public static int getNextId() {
		int id = 0;
		Set<String> sections = getConfigSections();
		if (sections.size() == 0) return id;
		for (String section : sections) {
			try {
				int savedId = Integer.parseInt(section);
				if (savedId > id) id = savedId + 1;
			} catch (Exception ex) {
				BNCore.warn("An error occured while trying to save a Mystery Chest to file");
				ex.printStackTrace();
			}
		}
		return id;
	}

	public static RewardChestLoot[] getAllRewards() {
		RewardChestLoot[] loot = new RewardChestLoot[getConfigSections().size()];
		for (int i = 0; i < getConfigSections().size(); i++) {
			loot[i] = (RewardChestLoot) getConfig().get((String) getConfigSections().toArray()[i]);
		}
		return loot;
	}

}

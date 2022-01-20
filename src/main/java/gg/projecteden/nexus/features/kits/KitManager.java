package gg.projecteden.nexus.features.kits;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.IOUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static gg.projecteden.utils.Utils.typesAnnotatedWith;

public class KitManager {

	@Getter
	private static final String fileName = "kits.yml";
	@Getter
	private static YamlConfiguration config;

	public KitManager() {
		registerSerializables();
	}

	static {
		reloadConfig();
	}

	private void registerSerializables() {
		typesAnnotatedWith(SerializableAs.class, this.getClass().getPackageName()).forEach(clazz -> {
			String alias = clazz.getAnnotation(SerializableAs.class).value();
			ConfigurationSerialization.registerClass((Class<? extends ConfigurationSerializable>) clazz, alias);
		});
	}

	public static void reloadConfig() {
		config = IOUtils.getNexusConfig(fileName);
	}

	@SneakyThrows
	public static void saveConfig() {
		config.save(IOUtils.getPluginFile(fileName));
	}

	public static Set<String> getConfigSections() {
		if (config == null)
			reloadConfig();
		return config.getConfigurationSection("").getKeys(false);
	}

	public static Kit get(int id) {
		Kit kit = (Kit) getConfig().get(id + "");
		kit.setId(id);
		return kit;
	}

	public static Kit getByName(String name) {
		return Arrays.stream(getAllKits()).filter(kit -> kit.getName().toLowerCase().contains(name.toLowerCase())).findFirst().get();
	}

	public static int getNextId() {
		int id = 0;
		Set<String> sections = getConfigSections();
		if (sections.size() == 0) return id;
		for (String section : sections) {
			try {
				int savedId = Integer.parseInt(section);
				if (savedId >= id) id = savedId + 1;
			} catch (Exception ex) {
				Nexus.warn("An error occurred while trying to save a Kit to file");
				ex.printStackTrace();
			}
		}
		return id;
	}

	public static Kit[] getAllKits() {
		List<Kit> kits = new ArrayList<>();
		for (String section : getConfigSections()) {
			Kit kit = (Kit) getConfig().get(section);
			kit.setId(Integer.parseInt(section));
			kits.add(kit);
		}
		return kits.toArray(Kit[]::new);
	}

}

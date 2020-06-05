package me.pugabyte.bncore.features.menus.rewardchests;

import fr.minuskube.inv.SmartInventory;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.reflections.Reflections;

public class RewardChest {

	public RewardChest() {
		registerSerializables();
	}

	public static SmartInventory getInv(RewardChestLoot... loot) {
		return SmartInventory.builder()
				.size(3, 9)
				.title("Reward Chest")
				.provider(new RewardChestProvider(loot))
				.closeable(false)
				.build();
	}

	private void registerSerializables() {
		new Reflections(this.getClass().getPackage().getName()).getTypesAnnotatedWith(SerializableAs.class).forEach(clazz -> {
			String alias = clazz.getAnnotation(SerializableAs.class).value();
			ConfigurationSerialization.registerClass((Class<? extends ConfigurationSerializable>) clazz, alias);
		});
	}

}

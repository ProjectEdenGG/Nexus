package me.pugabyte.bncore.features.votes.mysterychest;

import fr.minuskube.inv.SmartInventory;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import org.bukkit.OfflinePlayer;

public class MysteryChest {

	SettingService service = new SettingService();

	public static SmartInventory INV = SmartInventory.builder()
			.size(3, 9)
			.title("Mystery Chest")
			.provider(new MysteryChestProvider())
			.closeable(false)
			.build();

	public MysteryChest(OfflinePlayer player, int amount) {
		givePlayer(player, amount);
	}

	public void givePlayer(OfflinePlayer player, int amount) {
		Setting setting = service.get(player, "mysteryChest");
		int chests = 0;
		try {
			chests = Integer.parseInt(setting.getValue());
		} catch (Exception ignore) {
		}
		chests += amount;
		setting.setValue("" + chests);
		service.save(setting);
	}

}

package me.pugabyte.bncore.features.votes.mysterychest;

import fr.minuskube.inv.SmartInventory;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import org.bukkit.OfflinePlayer;

public class MysteryChest {
	private final SettingService service = new SettingService();
	private final OfflinePlayer player;

	public static SmartInventory INV = SmartInventory.builder()
			.size(3, 9)
			.title("Mystery Chest")
			.provider(new MysteryChestProvider())
			.closeable(false)
			.build();

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

}

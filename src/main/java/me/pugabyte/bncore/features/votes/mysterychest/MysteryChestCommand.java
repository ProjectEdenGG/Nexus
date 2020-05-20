package me.pugabyte.bncore.features.votes.mysterychest;

import fr.minuskube.inv.SmartInventory;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.OfflinePlayer;

public class MysteryChestCommand extends CustomCommand {

	SettingService service = new SettingService();

	public MysteryChestCommand(CommandEvent event) {
		super(event);
	}

	public static SmartInventory INV = SmartInventory.builder()
			.size(3, 9)
			.title("Mystery Chest")
			.provider(new MysteryChestProvider())
			.closeable(false)
			.build();

	@Path()
	void use() {
		Setting setting = service.get(player(), "mysteryChest");
		int chests = 0;
		try {
			chests = Integer.parseInt(setting.getValue());
		} catch (Exception ignore) {
		}
		if (chests > 0)
			error("You do not have any mystery chest to open");
		if (!WorldGroup.get(player()).equals(WorldGroup.SURVIVAL))
			error("You must be in the survival world to run this command.");
		MysteryChestProvider.time = 0;
		MysteryChestProvider.speed = 4;
		MysteryChestProvider.lootIndex = Utils.randomInt(0, MysteryChestLoot.values().length - 1);
		INV.open(player());
	}

	@Path("give <player> [amount]")
	@Permission("group.admin")
	void give(OfflinePlayer player, @Arg("1") int amount) {
		Setting setting = service.get(player, "mysteryChest");
		int chests = 0;
		try {
			chests = Integer.parseInt(setting.getValue());
		} catch (Exception ignore) {
		}
		chests += amount;
		setting.setValue("" + chests);
		service.save(setting);
		send(PREFIX + "&e" + player.getName() + " &3now has &e" + chests + "&3 Mystery Chests");
	}

	@Path("test")
	@Permission("group.admin")
	void test() {
		Setting setting = service.get(player(), "mysteryChest");
		setting.setValue("1");
		service.save(setting);
		use();
	}

}

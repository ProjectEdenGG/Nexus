package me.pugabyte.bncore.features.votes.mysterychest;

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

import java.util.Arrays;

public class MysteryChestCommand extends CustomCommand {

	SettingService service = new SettingService();

	public MysteryChestCommand(CommandEvent event) {
		super(event);
	}

	@Path()
	void use() {
		Setting setting = service.get(player(), "mysteryChest");
		if (isNullOrEmpty(setting.getValue()) || !isInt(setting.getValue()))
			error("You do not have any mystery chest to open");
		int chests = Integer.parseInt(setting.getValue());
		if (chests <= 0)
			error("You do not have any mystery chest to open");
		if (!WorldGroup.get(player()).equals(WorldGroup.SURVIVAL))
			error("You must be in the survival world to run this command.");
		MysteryChestProvider.time = 0;
		MysteryChestProvider.speed = 4;
		MysteryChestProvider.lootIndex = Utils.randomInt(0, MysteryChestLoot.values().length - 1);
		MysteryChest.INV.open(player());
	}

	@Path("give <player> [amount]")
	@Permission("group.admin")
	void give(OfflinePlayer player, @Arg("1") int amount) {
		new MysteryChest(player, amount);
		Setting setting = service.get(player, "mysteryChest");
		send(PREFIX + "&e" + player.getName() + " &3now has &e" + setting.getValue() + "&3 Mystery Chests");
	}

	@Path("test")
	@Permission("group.admin")
	void test() {
		new MysteryChest(player());
		use();
	}

	@Path("2")
	@Permission("group.admin")
	void two() {
		Utils.giveItems(player(), Arrays.asList(MysteryChestLoot.TWO.getLoot()));
	}

}

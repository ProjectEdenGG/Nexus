package me.pugabyte.bncore.features.votes.mysterychest;

import fr.minuskube.inv.SmartInventory;
import me.pugabyte.bncore.features.menus.rewardchests.RewardChest;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.OfflinePlayer;

import java.util.Arrays;

public class MysteryChestCommand extends CustomCommand {
	private final SettingService service = new SettingService();

	static {
		new RewardChest();
	}

	public MysteryChestCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void use() {
		int chests = service.get(player(), "mysteryChest").getInt();
		if (chests <= 0)
			error("You do not have any mystery chest to open");
		if (!WorldGroup.get(player()).equals(WorldGroup.SURVIVAL))
			error("You must be in the survival world to run this command.");

		RewardChest.getInv(MysteryChestLootEnum.getAllLoot()).open(player());
		new MysteryChest(player()).take(1);
	}

	@Path("give <player> [amount]")
	@Permission("group.admin")
	void give(OfflinePlayer player, @Arg("1") int amount) {
		send(PREFIX + "&e" + player.getName() + " &3now has &e" + new MysteryChest(player).give(amount) + "&3 Mystery Chests");
	}

	@Path("take <player> [amount]")
	@Permission("group.admin")
	void take(OfflinePlayer player, @Arg("1") int amount) {
		send(PREFIX + "&e" + player.getName() + " &3now has &e" + new MysteryChest(player).take(amount) + "&3 Mystery Chests");
	}

	@Path("edit")
	@Permission("group.staff")
	void edit() {
		SmartInventory.builder()
				.title("Mystery Chest Rewards")
				.provider(new MysteryChestEditProvider(null))
				.size(6, 9)
				.build().open(player());
	}

	@Path("test")
	@Permission("group.admin")
	void test() {
		new MysteryChest(player()).give(1);
		use();
	}

	@Path("items <index>")
	@Permission("group.admin")
	void two(int index) {
		Utils.giveItems(player(), Arrays.asList(MysteryChestLootEnum.values()[index - 1].getLoot().getItems()));
	}

}

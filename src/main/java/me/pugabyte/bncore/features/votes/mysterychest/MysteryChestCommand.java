package me.pugabyte.bncore.features.votes.mysterychest;

import me.pugabyte.bncore.features.menus.rewardchests.RewardChest;
import me.pugabyte.bncore.features.menus.rewardchests.RewardChestType;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.OfflinePlayer;

public class MysteryChestCommand extends CustomCommand {

	static {
		new RewardChest();
	}

	public MysteryChestCommand(CommandEvent event) {
		super(event);
	}

	@Path()
	void info() {
		line(2);
		send("&eHey there!");
		send("&3This special chest here is what we call a &eMystery Chest&3. We have a few different types of chests you can open" +
				" and earn special rewards that can help you out on the server or cool cosmetics.");

		send("&3To open a chest, you will need a &eReward Key&3. You can earn these keys by &evoting&3,&e being the top voter&3,&e participating in events&3,&e and more!");
		line();
		send("&3The types of keys we have right now are:");
		for (RewardChestType type : RewardChestType.values()) {
			if (type == RewardChestType.ALL) continue;
			send("&e - " + StringUtils.camelCase(type.name()));
		}
		line();
		send("&3If you have a key, simply &eclick &3the &eMystery Chest &3with a &eReward Key &3to open it up and earn your rewards!");
	}

	@Path("give <player> <type> [amount]")
	@Permission("group.admin")
	void give(OfflinePlayer player, RewardChestType type, @Arg("1") int amount) {
		send(PREFIX + "&e" + player.getName() + " &3now has &e" + new MysteryChest(player).give(amount, type) + "&3 Mystery Chests");
	}

	@Path("take <player> <type> [amount]")
	@Permission("group.admin")
	void take(OfflinePlayer player, RewardChestType type, @Arg("1") int amount) {
		send(PREFIX + "&e" + player.getName() + " &3now has &e" + new MysteryChest(player).take(amount, type) + "&3 Mystery Chests");
	}

	@Path("edit [type]")
	@Permission("group.admin")
	void edit(@Arg() RewardChestType type) {
		if (type == null)
			type = RewardChestType.MYSTERY;
		MysteryChest.getInv(null, type).open(player(), 0);
	}

	@Path("test [type]")
	@Permission("group.admin")
	void test(@Arg() RewardChestType type) {
		if (type == null)
			type = RewardChestType.ALL;
		RewardChest.getInv(MysteryChest.getAllActiveRewardsByType(type)).open(player());
	}

}

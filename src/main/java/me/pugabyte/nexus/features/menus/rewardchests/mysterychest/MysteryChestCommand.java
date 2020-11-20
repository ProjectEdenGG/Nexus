package me.pugabyte.nexus.features.menus.rewardchests.mysterychest;

import me.pugabyte.nexus.features.menus.rewardchests.RewardChest;
import me.pugabyte.nexus.features.menus.rewardchests.RewardChestType;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.OfflinePlayer;

import java.util.Map;

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
		send(PREFIX + "&e" + player.getName() + " &3now has &e" + new MysteryChest(player).give(amount, type) + "&3 Mystery Chest Keys");
	}

	@Path("take <player> <type> [amount]")
	@Permission("group.admin")
	void take(OfflinePlayer player, RewardChestType type, @Arg("1") int amount) {
		send(PREFIX + "&e" + player.getName() + " &3now has &e" + new MysteryChest(player).take(amount, type) + "&3 Mystery Chest Keys");
	}

	@Path("count <player> [type]")
	@Permission("group.admin")
	void count(OfflinePlayer player, RewardChestType type) {
		Map<RewardChestType, Integer> amounts = new MysteryChest(player).getMysteryChestPlayer().getAmounts();
		if (type != null)
			send(PREFIX + "&e" + player.getName() + " &3has &e" + amounts.getOrDefault(type, 0) + "&3 Mystery Chest Keys for type " + camelCase(type));
		else
			for (RewardChestType value : RewardChestType.values())
				send(PREFIX + "&e" + player.getName() + " &3has &e" + amounts.getOrDefault(value, 0) + "&3 Mystery Chest Keys for type " + camelCase(value));
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

//	@Async
//	@Path("giveMonthlyRewards")
//	@Permission("group.admin")
//	void giveMonthlyRewards() {
//		send("Generating data...");
//		String data = "";
//		Map<String, Integer> map = new HashMap<>();
//		for (Nerd nerd : new NerdService().getNerdsLastJoinedAfter(LocalDateTime.now().minusMonths(3)))
//			map.put(nerd.getName(), map.getOrDefault(nerd.getName(), 0) + 1);
//		EndOfMonth.TopVoterData topVoters = new EndOfMonth.TopVoterData(Month.JUNE);
//		for (TopVoter voter : topVoters.getVotersWith(1))
//			map.put(Utils.getPlayer(voter.getUuid()).getName(), map.getOrDefault(Utils.getPlayer(voter.getUuid()).getName(), 0) + 1);
//		for (TopVoter voter : topVoters.getVotersWith(12))
//			map.put(Utils.getPlayer(voter.getUuid()).getName(), map.getOrDefault(Utils.getPlayer(voter.getUuid()).getName(), 0) + 1);
//		for (TopVoter voter : topVoters.getEco15kWinners())
//			map.put(Utils.getPlayer(voter.getUuid()).getName(), map.getOrDefault(Utils.getPlayer(voter.getUuid()).getName(), 0) + 1);
//		for (TopVoter voter : topVoters.getEco20kWinners())
//			map.put(Utils.getPlayer(voter.getUuid()).getName(), map.getOrDefault(Utils.getPlayer(voter.getUuid()).getName(), 0) + 1);
//		for (TopVoter voter : topVoters.getEco30kWinners())
//			map.put(Utils.getPlayer(voter.getUuid()).getName(), map.getOrDefault(Utils.getPlayer(voter.getUuid()).getName(), 0) + 1);
//		for (TopVoter voter : topVoters.getNpcOrHoloWinners())
//			map.put(Utils.getPlayer(voter.getUuid()).getName(), map.getOrDefault(Utils.getPlayer(voter.getUuid()).getName(), 0) + 1);
//		for (String string : map.keySet()) {
//			data += string + ": " + map.get(string) + ", ";
//		}
//		send(new JsonBuilder("Click here to view data").url(StringUtils.paste(data)));
//		for (String string : map.keySet()) {
//			if (string == null || string.equalsIgnoreCase("null"))
//				continue;
//			new MysteryChest(Utils.getPlayer(string)).give(map.get(string), RewardChestType.MYSTERY);
//			send("&3Gave &e" + string + " " + map.get(string) + " Mystery Chests");
//		}
//	}


}

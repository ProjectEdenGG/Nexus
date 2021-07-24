package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromHelp;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.setting.Setting;
import gg.projecteden.nexus.models.setting.SettingService;
import gg.projecteden.nexus.models.vote.TopVoter;
import gg.projecteden.nexus.models.vote.VoteService;
import gg.projecteden.nexus.utils.PlayerUtils;
import org.bukkit.inventory.ItemStack;

import java.time.Month;

@Description("Claims a Head Database head if you have a valid coupon")
public class ClaimHeadCommand extends CustomCommand {
	private static final SettingService service = new SettingService();
	private static final String SETTING_PATH = "head-db-coupons";

	public ClaimHeadCommand(CommandEvent event) {
		super(event);
	}

	@Path("<ID>")
	void run(String headID) {
		Setting setting = service.get(offlinePlayer(), SETTING_PATH);
		if (setting.getInt() <= 0)
			error("You do not have any Head Database coupons");
		ItemStack item = Nexus.getHeadAPI().getItemHead(headID);
		if (item == null)
			error("That head could not be found");
		PlayerUtils.dropExcessItems(player(), PlayerUtils.giveItemsAndGetExcess(player(), item));
		send(PREFIX + "You have claimed the head " + item.getItemMeta().getDisplayName());
		setting.setInt(setting.getInt()-1);
		service.save(setting);
	}

	@Path("allot")
	@Permission("group.admin")
	@TabCompleteIgnore
	@HideFromHelp
	void allot() {
		int voters = 0;
		for (TopVoter voter : new VoteService().getTopVoters(Month.MAY)) {
			if (voter.getCount() < 40) continue;

			Setting setting = service.get(voter.getUuid(), SETTING_PATH);
			setting.setInt(setting.getInt() + 1);
			service.save(setting);
			voters += 1;
		}
		send(PREFIX + "Coupons given to " + voters + " users");
	}

	@Path("allot clear")
	@Permission("group.admin")
	@TabCompleteIgnore
	@HideFromHelp
	void allot_clear() {
		for (TopVoter voter : new VoteService().getTopVoters(Month.MAY)) {
			Setting setting = service.get(voter.getUuid(), SETTING_PATH);
			setting.setInt(0);
			service.save(setting);
		}
		send(PREFIX + "Coupons cleared");
	}
}

package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.voter.Voter;
import gg.projecteden.nexus.models.voter.VoterService;
import org.bukkit.inventory.ItemStack;

public class ClaimHeadCommand extends CustomCommand {
	private final VoterService service = new VoterService();
	private Voter voter;

	public ClaimHeadCommand(CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			voter = service.get(player());
	}

	@NoLiterals
	@Description("Claims a Head Database head if you have a valid coupon")
	void run(String headId) {
		if (voter.getHeadCoupons() <= 0)
			error("You do not have any Head Database coupons");

		ItemStack item = Nexus.getHeadAPI().getItemHead(headId);
		if (item == null)
			error("That head could not be found");

		giveItem(item);
		send(PREFIX + "You have claimed the head " + item.getItemMeta().getDisplayName());
		voter.setHeadCoupons(voter.getHeadCoupons() - 1);
		service.save(voter);
	}

}

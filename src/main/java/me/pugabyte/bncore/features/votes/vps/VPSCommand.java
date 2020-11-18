package me.pugabyte.bncore.features.votes.vps;

import lombok.NonNull;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.nerd.Nerd;
import me.pugabyte.bncore.models.vote.VoteService;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.ItemUtils;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.Material;

public class VPSCommand extends CustomCommand {

	public VPSCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[page]")
	void run(@Arg("1") int page) {
		VPS.open(player(), page);
	}

	@Path("buy head <player>")
	void buyHead(Nerd nerd) {
		if (WorldGroup.get(player()) != WorldGroup.CREATIVE)
			error("You must be in Creative to buy heads");

		int price = 6;
		if (nerd.getRank().isStaff())
			price = 9;

		new VoteService().takePoints(player().getUniqueId().toString(), price);
		ItemUtils.giveItem(player(), new ItemBuilder(Material.PLAYER_HEAD).skullOwner(nerd.getOfflinePlayer()).build());
		send(PREFIX + "Purchased &e" + nerd.getName() + "'s head &3for &e" + price + " vote points");
	}

	@Path("buy plot")
	void buyPlot() {
		if (BNCore.getPerms().playerHas("creative", player(), "plots.plot.6"))
			error("You have already purchased the maximum amount of plots");

		new VoteService().takePoints(player().getUniqueId().toString(), 150);
		runCommandAsConsole("permhelper plots add " + player().getName() + " 1");
		send(PREFIX + "Purchased &e1 creative plot &3for &e150 vote points");
	}

}

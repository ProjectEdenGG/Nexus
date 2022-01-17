package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromHelp;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.hours.Hours;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.models.inviterewards.InviteRewards;
import gg.projecteden.nexus.models.inviterewards.InviteRewardsService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.voter.VoterService;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import static gg.projecteden.nexus.utils.StringUtils.colorize;

@Aliases("invited")
@Permission("invite.rewards")
@Description("Receive a reward for inviting a player to the server")
public class InviteRewardsCommand extends CustomCommand {

	InviteRewardsCommand(CommandEvent event) {
		super(event);
	}

	@Path("<username>")
	void send(Player invited) {
		Player inviter = player();
		if (inviter.equals(invited))
			error(colorize("You cannot invite yourself!"));

		if (Nerd.of(inviter).getFirstJoin().isAfter(Nerd.of(invited).getFirstJoin()))
			error("You joined after &e" + invited.getName() + "&c, so you can't have invited them!");

		if (hasBeenInvitedBefore(invited))
			error("&e" + invited.getName() + "&c has already confirmed being invited by someone else");

		if (!invited.hasPermission("invite.rewards.confirm"))
			error("The person you are inviting must be a &fMember &cor above.");

		if (getMinutesPlayed(invited) < 60)
			error("&e" + invited.getName() + "&c has to play for an hour before you can do that.");

		sendInviteConfirmation(inviter, invited);
	}

	@HideFromHelp
	@TabCompleteIgnore
	@Path("confirm <inviter>")
	void confirm(Player inviter) {
		Player invited = player();
		if (Nerd.of(inviter).getFirstJoin().isAfter(Nerd.of(invited).getFirstJoin()))
			error("&e" + inviter.getName() + " &cjoined after you, so you can't have been invited by them!");

		if (hasBeenInvitedBefore(invited))
			error("&cYou have already confirmed being invited by someone else");

		if (getMinutesPlayed(invited) < 60)
			error("You have to play for an hour before you can do that.");

		send(inviter, PREFIX + "&e" + invited.getName() + "&3 has confirmed your invite; thank you for " +
				"helping Project Eden grow! You earned &e15 vote points");
		send(invited, PREFIX + "You have confirmed &e" + inviter.getName() + "'s &3invite. Thank you " +
				"for flying Project Eden!");
		reward(inviter);
		saveInvitation(inviter, invited);
	}

	@HideFromHelp
	@TabCompleteIgnore
	@Path("deny <inviter>")
	void deny(Player inviter) {
		Player invited = player();
		send(inviter, PREFIX + "&e" + invited.getName() + "&3 has denied your invite confirmation.");
		send(invited, PREFIX + "You have denied &e" + inviter.getName() + "&3's invite.");
	}

	private static boolean hasBeenInvitedBefore(Player invited) {
		return new InviteRewardsService().hasBeenInvited(invited.getUniqueId());
	}

	private void sendInviteConfirmation(Player inviter, Player invited) {
		// Invited player
		send(invited, "");
		send(invited, json("  &3Did &e" + Nickname.of(inviter) + " &3invite you to Project Eden?"));

		send(invited, json()
				.next("  &3Click one  ||").color(NamedTextColor.DARK_AQUA)
				.next("  &a&lYes  ").command("/invited confirm " + inviter.getName())
				.group()
				.next("&3||")
				.group()
				.next("  &c&lNo  ").command("/invited deny " + inviter.getName()));
		send(invited, "");

		// Inviter
		send(inviter, PREFIX + "Invite confirmation sent to &e" + invited.getName());
	}

	static void saveInvitation(Player inviter, Player invitee) {
		InviteRewards inviteRewards = new InviteRewardsService().get(inviter);
		inviteRewards.getInvited().add(invitee.getUniqueId());
		new InviteRewardsService().save(inviteRewards);
	}

	private void reward(Player inviter) {
		new VoterService().edit(inviter, voter -> voter.givePoints(15));
	}

	private long getMinutesPlayed(Player player) {
		Hours hours = new HoursService().get(player.getUniqueId());
		return hours.getTotal() / 60;
	}

}

package me.pugabyte.nexus.features.commands;

import eden.models.hours.Hours;
import eden.models.hours.HoursService;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.HideFromHelp;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.models.vote.Voter;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import java.util.List;

import static me.pugabyte.nexus.utils.StringUtils.colorize;

@Aliases("invited")
@Permission("invite.rewards")
public class InviteRewardsCommand extends CustomCommand {

	InviteRewardsCommand(CommandEvent event) {
		super(event);
	}

	@Path("<username>")
	void send(Player invited) {
		Player inviter = player();
		if (inviter.equals(invited))
			error(colorize("You cannot invite yourself!"));

		if (inviter.getFirstPlayed() >= invited.getFirstPlayed())
			error("You joined after &e" + invited.getName() + "&3, so you can't have invited them!");

		if (hasBeenInvitedBefore(invited))
			error("&e" + invited.getName() + "&3 has already confirmed being invited by someone else");

		if (!invited.hasPermission("invite.rewards.confirm"))
			error("The person you are inviting must be a &fMember &3or above.");

		if (getMinutesPlayed(invited) < 60)
			error("&e" + invited.getName() + "&3 has to play for an hour before you can do that.");

		sendInviteConfirmation(inviter, invited);
	}

	@HideFromHelp
	@TabCompleteIgnore
	@Path("confirm <inviter>")
	void confirm(Player inviter) {
		Player invited = player();
		if (inviter.getFirstPlayed() >= invited.getFirstPlayed())
			error("&e" + inviter.getName() + " &3joined after you, so you can't have been invited by them!");

		if (!hasBeenInvitedBefore(invited))
			error("&3You have already confirmed being invited by someone else");

		if (getMinutesPlayed(invited) < 60)
			error("You have to play for an hour before you can do that.");

		send(inviter, "&e" + invited.getName() + "&3 has confirmed your invite; thank you for " +
				"helping Bear Nation grow! You earned &e15 vote points");
		send(invited, "You have confirmed &e" + inviter.getName() + "'s &3invite. Thank you " +
				"for flying Bear Nation!");
		reward(inviter);
		saveInvitation(invited, inviter);
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
		Configuration config = Nexus.getInstance().getConfig();
		try {
			for (String _inviter : config.getConfigurationSection("inviterewards.invited").getKeys(false)) {
				for (String _invited : config.getStringList("inviterewards.invited." + _inviter)) {
					if (_invited.contains(invited.getUniqueId().toString())) {
						return true;
					}
				}
			}
		} catch (NullPointerException ex) {
			return false;
		}
		return false;
	}

	private void sendInviteConfirmation(Player inviter, Player invited) {
		// Inviter
		send(inviter, "Invite confirmation sent to &e" + invited.getName());

		// Invited player
		send(invited, "");
		send(invited, json("  &3Did &e" + Nickname.of(inviter) + " &3invite you to Bear Nation?"));

		send(invited, json()
				.next("  &3Click one  ||").color(NamedTextColor.DARK_AQUA)
				.next("  &a&lYes  ").command("/invited confirm " + inviter.getName())
				.group()
				.next("&3||")
				.group()
				.next("  &c&lNo  ").command("/invited deny " + inviter.getName()));
	}

	static void saveInvitation(Player invitee, Player inviter) {
		String UUIDer = inviter.getUniqueId().toString();
		String UUIDed = invitee.getUniqueId().toString();

		Configuration config = Nexus.getInstance().getConfig();

		List<String> invited = config.getStringList("inviterewards.invited." + UUIDer);
		invited.add(UUIDed);
		config.set("inviterewards.invited." + UUIDer, invited);

		try {
			Nexus.getInstance().saveConfig();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void reward(Player inviter) {
		new Voter(inviter).givePoints(15);
	}

	private long getMinutesPlayed(Player player) {
		Hours hours = new HoursService().get(player.getUniqueId());
		return hours.getTotal() / 60;
	}

}

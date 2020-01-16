package me.pugabyte.bncore.features.inviterewards;

import ch.njol.skript.util.Timespan;
import ch.njol.skript.variables.Variables;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import static me.pugabyte.bncore.utils.Utils.colorize;

@Aliases("invited")
@Permission("invite.rewards")
public class InviteRewardsCommand extends CustomCommand {

	InviteRewardsCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void help() {
		reply(PREFIX + "Correct usage: &c/invited <username>");
	}

	@Path("<invited>")
	void send(@Arg Player invited) {
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

	@Path("confirm <inviter>")
	void confirm(@Arg Player inviter) {
		Player invited = player();
		if (inviter.getFirstPlayed() >= invited.getFirstPlayed())
			error("&e" + inviter.getName() + " &3joined after you, so you can't have been invited by them!");

		if (!hasBeenInvitedBefore(invited))
			error("&3You have already confirmed being invited by someone else");

		if (getMinutesPlayed(invited) < 60)
			error("You have to play for an hour before you can do that.");

		inviter.sendMessage(colorize("&e" + invited.getName() + "&3 has confirmed your invite; thank you for " +
				"helping Bear Nation grow! You earned &e15 vote points"));
		invited.sendMessage(colorize("You have confirmed &e" + inviter.getName() + "'s &3invite. Thank you " +
				"for flying Bear Nation!"));
		reward(inviter);
		InviteRewards.saveInvitation(invited, inviter);
	}

	@Path("deny <inviter>")
	void deny(@Arg Player inviter) {
		Player invited = player();
		send(inviter, PREFIX + "&e" + invited.getName() + "&3 has denied your invite confirmation.");
		send(invited, PREFIX + "You have denied &e" + inviter.getName() + "&3's invite.");
	}

	private static boolean hasBeenInvitedBefore(Player invited) {
		Configuration config = BNCore.getInstance().getConfig();
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
		inviter.sendMessage(colorize("Invite confirmation sent to &e" + invited.getName()));

		// Invited player
		invited.sendMessage("");
		invited.spigot().sendMessage(new ComponentBuilder("")
				.append("  Did ").color(ChatColor.DARK_AQUA)
				.append(inviter.getName()).color(ChatColor.YELLOW)
				.append(" invite you to Bear Nation?").color(ChatColor.DARK_AQUA)
				.create());

		invited.spigot().sendMessage(new ComponentBuilder("")
				.append("  Click one  ||").color(ChatColor.DARK_AQUA)
				.append("  Yes  ").color(ChatColor.GREEN).bold(true)
				.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ("/invited confirm " + inviter.getName())))
				.append("||").color(ChatColor.DARK_AQUA).bold(false)
				.append("  No  ").color(ChatColor.RED).bold(true)
				.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ("/invited deny " + inviter.getName())))
				.create());
	}

	private void reward(Player inviter) {
		InviteRewards.getPlayerPoints().getAPI().give(inviter.getUniqueId(), 15);
	}

	private long getMinutesPlayed(Player player) {
		Timespan hours = (Timespan) Variables.getVariable("hours::" + player.getUniqueId().toString(), null, false);
		return hours.getTicks_i() / 20 / 60;
	}

}

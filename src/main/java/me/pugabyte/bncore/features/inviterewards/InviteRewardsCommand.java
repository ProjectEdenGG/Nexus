package me.pugabyte.bncore.features.inviterewards;

import ch.njol.skript.util.Timespan;
import ch.njol.skript.variables.Variables;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.models.exceptions.InvalidInputException;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import static me.pugabyte.bncore.BNCore.colorize;

public class InviteRewardsCommand implements CommandExecutor {
	final static String PREFIX = BNCore.getPrefix("InviteRewards");

	InviteRewardsCommand() {
		BNCore.registerCommand("invited", this);
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

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			if (sender instanceof Player) {
				Player player = (Player) sender;

				if (args.length == 0) {
					throw new InvalidInputException("Correct usage: &c/invited <username>");
				}

				switch (args[0]) {
					case "deny":
						if (player.hasPermission("inviterewards.confirm")) {
							denyInvite(args[1], player);
							return true;
						} else {
							throw new InvalidInputException("You must be &fMember &3to do that.");
						}
					case "confirm":
						if (player.hasPermission("inviterewards.confirm")) {
							confirmInvite(args[1], player);
							return true;
						} else {
							throw new InvalidInputException("You must be &fMember &3to do that.");
						}
					default:
						if (player.hasPermission("inviterewards.send")) {
							invite(player, args[0]);
							return true;
						} else {
							throw new InvalidInputException("You must be &f&lMember+ &3to do that.");
						}
				}
			} else {
				throw new InvalidInputException("You must be a player to run that command");
			}
		} catch (InvalidInputException ex) {
			sender.sendMessage(PREFIX + colorize(ex.getMessage()));
		}
		return true;

	}

	private void invite(Player inviter, String invitedString) {
		Player invited = findPlayer(invitedString);

		if (invited != null) {
			if (inviter.equals(invited)) {
				inviter.sendMessage(colorize("You cannot invite yourself!"));
				return;
			}

			if (inviter.getFirstPlayed() < invited.getFirstPlayed()) { // Inviter has to have joined before the person they invited
				if (!hasBeenInvitedBefore(invited)) {
					if (invited.hasPermission("inviterewards.confirm")) { // Invited player has to be Member or above
						long minutes = getMinutesPlayed(invited);

						// Invited player has to play for at least an hour
						if (minutes >= 60) {
							sendInviteConfirmation(inviter, invited);
						} else {
							inviter.sendMessage(colorize("&e" + invited.getName() + "&3 has to play for an hour before you can do that."));
						}
					} else {
						inviter.sendMessage(colorize("The person you are inviting must be a &fMember &3or above."));
					}
				} else {
					inviter.sendMessage(colorize("&e" + invited.getName() + "&3 has already confirmed being invited by someone else"));
				}
			} else {
				inviter.sendMessage(colorize("You joined after &e" + invited.getName() + "&3, so you can't have invited them!"));
			}
		} else {
			inviter.sendMessage(colorize("Player not found"));
		}
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

	private void confirmInvite(String inviterString, Player invited) {
		Player inviter = Bukkit.getPlayer(inviterString);
		if (inviter != null) {
			if (inviter.getFirstPlayed() < invited.getFirstPlayed()) {
				if (!hasBeenInvitedBefore(invited)) {
					long minutes = getMinutesPlayed(invited);

					// Invited player has to play for at least an hour
					if (minutes >= 60) {
						inviter.sendMessage(colorize("&e" + invited.getName() + "&3 has confirmed your invite; thank you for helping Bear Nation grow! You earned &e15 vote points"));
						invited.sendMessage(colorize("You have confirmed &e" + inviter.getName() + "'s &3invite. Thank you for flying Bear Nation!"));
						reward(inviter);
						InviteRewards.saveInvitation(invited, inviter);
					} else {
						invited.sendMessage(colorize("You have to play for an hour before you can do that."));
					}
				} else {
					invited.sendMessage(colorize("&3You have already confirmed being invited by someone else"));
				}
			} else {
				invited.sendMessage(colorize("&e" + inviter.getName() + " &3joined after you, so you can't have been invited by them!"));
			}
		} else {
			invited.sendMessage(colorize("Player not found"));
		}
	}

	private void denyInvite(String inviterString, Player invited) {
		Player inviter = Bukkit.getPlayer(inviterString);

		inviter.sendMessage(colorize("&e" + invited.getName() + "&3 has denied your invite confirmation."));
		invited.sendMessage(colorize("You have denied &e" + inviter.getName() + "'s &3invite."));
	}

	private Player findPlayer(String arg) {
		Player inviter = null;
		for (Player _player : Bukkit.getServer().getOnlinePlayers()) {
			if (_player.getName().toLowerCase().startsWith(arg.toLowerCase())) {
				inviter = _player;
			}
		}

		return inviter;
	}

	private long getMinutesPlayed(Player player) {
		Timespan hours = (Timespan) Variables.getVariable("hours::" + player.getUniqueId().toString(), null, false);
		return hours.getTicks_i() / 20 / 60;
	}

}

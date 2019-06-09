package me.pugabyte.bncore.features.chat.alerts;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.models.alerts.Alerts;
import me.pugabyte.bncore.models.alerts.AlertsService;
import me.pugabyte.bncore.models.exceptions.MustBeIngameException;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Optional;

public class AlertsCommand implements CommandExecutor {
	private final String PREFIX = BNCore.getPrefix("Alerts");
	AlertsService alertsService = new AlertsService();

	AlertsCommand() {
		BNCore.registerCommand("alerts", this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			if (args.length == 0) {
				if (sender instanceof Player) {
					helpMenu(sender);
				} else {
					sender.sendMessage("You must a player to view the help menu.");
				}
				return true;
			}

			if (!(sender instanceof Player)) {
				throw new MustBeIngameException();
			}

			if (args[0] == null || args[0].length() == 0) {
				helpMenu(sender);
			}

			Player player = (Player) sender;
			Alerts alerts = alertsService.get(player);

			switch (args[0]) {
				case "list":
				case "edit":
					if (alerts.getHighlights().size() != 0) {
						sender.sendMessage(PREFIX + "§eYour alerts list:");
						for (Alerts.Highlight highlight : alerts.getHighlights()) {

							ComponentBuilder builder = new ComponentBuilder(" ");

							if (highlight.isPartialMatching()) {
								builder.append("✔")
										.color(ChatColor.GREEN)
										.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/alerts partialmatch false " + highlight.getHighlight()))
										.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to turn off partial matching").color(ChatColor.RED).create()));
							} else {
								builder.append("✕ ")
										.color(ChatColor.RED)
										.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/alerts partialmatch true " + highlight.getHighlight()))
										.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to turn on partial matching").color(ChatColor.GREEN).create()));
							}

							builder.append(" " + highlight.getHighlight() + " ").color(ChatColor.DARK_AQUA);

							sender.spigot().sendMessage(builder.create());
						}
					} else {
						sender.sendMessage(PREFIX + "You don't have any alerts! Add some with §c/alerts add <word or phrase>");
					}
					return true;
				case "partialmatch":
				case "partialmatching":
					if (args.length >= 3) {
						String highlight = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
						if (args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")) {
							Optional<Alerts.Highlight> match = alerts.get(highlight);
							if (match.isPresent()) {
								boolean partialMatching = Boolean.parseBoolean(args[1]);
								alerts.delete(highlight);
								alerts.add(highlight, partialMatching);
								alertsService.save(alerts);
								sender.sendMessage("");
								sender.sendMessage(PREFIX + "Partial matching for alert " + ChatColor.YELLOW + highlight + ChatColor.DARK_AQUA + " " + (partialMatching ? "enabled" : "disabled"));
								sender.sendMessage("");
								Bukkit.dispatchCommand(sender, "alerts edit");
							} else {
								sender.sendMessage(PREFIX + "I could not find that alert in your alerts list");
							}
						} else {
							sender.sendMessage(PREFIX + "Correct usage: /alerts " + args[0].toLowerCase() + " <true|false> <highlight>");
						}
					}
					return true;
				case "add":
					if (args.length > 1) {
						String[] arguments = Arrays.copyOfRange(args, 1, args.length);
						String highlight = String.join(" ", arguments);
						if (highlight.toLowerCase().equals(player.getName().toLowerCase())) {
							sender.sendMessage(PREFIX + "Your name is automatically included in your alerts list.");
						} else {
							if (alerts.add(highlight)) {
								alertsService.save(alerts);
								sender.sendMessage(PREFIX + "Added §e" + highlight + " §3to your alerts list.");
							} else {
								sender.sendMessage(PREFIX + "You already had §e" + highlight + " §3in your alerts list.");
							}
						}
					} else {
						sender.sendMessage(PREFIX + "Enter a word or phrase to set as an alert.");
						sender.sendMessage(PREFIX + "Example: §c/alerts add hockey");
					}
					return true;
				case "delete":
					if (args.length > 1) {
						String[] arguments = Arrays.copyOfRange(args, 1, args.length);
						String highlight = String.join(" ", arguments);
						if (alerts.delete(highlight)) {
							alertsService.save(alerts);
							sender.sendMessage(PREFIX + "Removed §e" + highlight + " §3from your alerts list.");
						} else {
							sender.sendMessage(PREFIX + "You did not have §e" + highlight + " §3in your alerts list.");
						}
					} else {
						sender.sendMessage(PREFIX + "Enter a word or phrase to delete.");
						sender.sendMessage(PREFIX + "Example: §c/alerts delete hockey");
					}
					return true;
				case "clear":
					alerts.clear();
					alertsService.save(alerts);
					sender.sendMessage(PREFIX + "Cleared your alerts.");
					return true;
				case "mute":
					alerts.setMuted(true);
					sender.sendMessage(PREFIX + "Alerts muted.");
					return true;
				case "unmute":
					alerts.setMuted(false);
					sender.sendMessage(PREFIX + "Alerts unmuted.");
					return true;
				case "toggle":
					alerts.setMuted(!alerts.isMuted());
					sender.sendMessage(PREFIX + "Alerts " + (alerts.isMuted() ? "" : "un") + "muted");
					return true;
				case "sound":
					alerts.playSound();
					sender.sendMessage(PREFIX + "Test sound sent.");
					return true;
				default:
					helpMenu(sender);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return true;
	}

	private void helpMenu(CommandSender sender) {
		Player player = (Player) sender;
		TextComponent message = new TextComponent(PREFIX
				+ "Receive a §e'ping' noise §3whenever a word or phrase in your §c/alerts list §3is said in chat. " +
				"Make sure you have your 'Players' sound on!");
		message.setColor(ChatColor.DARK_AQUA);
		TextComponent edit = new TextComponent("You can edit your alerts with the following commands:");
		edit.setColor(ChatColor.DARK_AQUA);
		TextComponent commandList = new TextComponent(" /alerts list");
		commandList.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/alerts list"));
		commandList.setColor(ChatColor.RED);
		TextComponent commandAdd = new TextComponent(" /alerts add <word or phrase>");
		commandAdd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/alerts add "));
		commandAdd.setColor(ChatColor.RED);
		TextComponent commandDelete = new TextComponent(" /alerts delete <word or phrase>");
		commandDelete.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/alerts delete "));
		commandDelete.setColor(ChatColor.RED);
		TextComponent commandClear = new TextComponent(" /alerts clear");
		commandClear.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/alerts clear"));
		commandClear.setColor(ChatColor.RED);
		TextComponent commandMute = new TextComponent(" /alerts <mute|unmute|toggle>");
		commandMute.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/alerts <mute|unmute|toggle>"));
		commandMute.setColor(ChatColor.RED);

		sender.sendMessage("");
		player.spigot().sendMessage(message);
		sender.sendMessage("");
		player.spigot().sendMessage(edit);
		player.spigot().sendMessage(commandList);
		player.spigot().sendMessage(commandAdd);
		player.spigot().sendMessage(commandDelete);
		player.spigot().sendMessage(commandClear);
		player.spigot().sendMessage(commandMute);
	}

}
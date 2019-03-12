package me.pugabyte.bncore.features.chat.alerts;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.chat.alerts.models.AlertsPlayer;
import me.pugabyte.bncore.features.chat.alerts.models.Highlight;
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

import static me.pugabyte.bncore.features.chat.Chat.alerts;

public class AlertsCommand implements CommandExecutor {
	private final String PREFIX = BNCore.getPrefix("Alerts");

	AlertsCommand() {
		BNCore.registerCommand("alerts", this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			if (sender instanceof Player) {
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
			} else {
				sender.sendMessage("You must a player to view the help menu.");
			}
		} else {
			if (args[0] != null && args[0].length() != 0) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					AlertsPlayer alertsPlayer = alerts.getPlayer(player);
					if (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("edit")) {
						if (alertsPlayer.getHighlights().size() != 0) {
							sender.sendMessage(PREFIX + "§eYour alerts list:");
							for (Highlight highlight : alertsPlayer.getHighlights()) {

								ComponentBuilder builder = new ComponentBuilder(" ");

								if (highlight.isPartialMatching()) {
									builder.append("✔")
											.color(ChatColor.GREEN)
											.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/alerts partialmatch false " + highlight.get()))
											.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to turn off partial matching").color(ChatColor.RED).create()));
								} else {
									builder.append("✕ ")
											.color(ChatColor.RED)
											.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/alerts partialmatch true " + highlight.get()))
											.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to turn on partial matching").color(ChatColor.GREEN).create()));
								}

								builder.append(" " + highlight.get() + " ").color(ChatColor.DARK_AQUA);

								sender.spigot().sendMessage(builder.create());
							}
							return true;
						} else {
							sender.sendMessage(PREFIX + "You don't have any alerts! Add some with §c/alerts add <word or phrase>");
							return true;
						}
					} else if (args[0].equalsIgnoreCase("partialmatch") || args[0].equalsIgnoreCase("partialmatching")) {
						if (args.length >= 3) {
							String highlight = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
							if (args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")) {
								Optional<Highlight> match = alertsPlayer.get(highlight);
								if (match.isPresent()) {
									boolean partialMatching = Boolean.parseBoolean(args[1]);
									alertsPlayer.delete(highlight);
									alertsPlayer.add(highlight, partialMatching);
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
					} else if (args[0].equalsIgnoreCase("add")) {
						if (args.length > 1) {
							String[] arguments = Arrays.copyOfRange(args, 1, args.length);
							String highlight = String.join(" ", arguments);
							if (highlight.toLowerCase().equals(player.getName().toLowerCase())) {
								sender.sendMessage(PREFIX + "Your name is automatically included in your alerts list.");
							} else {
								if (alertsPlayer.add(highlight)) {
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
					} else if (args[0].equalsIgnoreCase("delete")) {
						if (args.length > 1) {
							String[] arguments = Arrays.copyOfRange(args, 1, args.length);
							String highlight = String.join(" ", arguments);
							if (alertsPlayer.delete(highlight)) {
								sender.sendMessage(PREFIX + "Removed §e" + highlight + " §3from your alerts list.");
							} else {
								sender.sendMessage(PREFIX + "You did not have §e" + highlight + " §3in your alerts list.");
							}
						} else {
							sender.sendMessage(PREFIX + "Enter a word or phrase to delete.");
							sender.sendMessage(PREFIX + "Example: §c/alerts delete hockey");
						}
						return true;
					} else if (args[0].equalsIgnoreCase("clear")) {
						alertsPlayer.clear();
						sender.sendMessage(PREFIX + "Cleared your alerts.");
						return true;
					} else if (args[0].equalsIgnoreCase("mute")) {
						alertsPlayer.setMuted(true);
						sender.sendMessage(PREFIX + "Alerts muted.");
						return true;
					} else if (args[0].equalsIgnoreCase("unmute")) {
						alertsPlayer.setMuted(false);
						sender.sendMessage(PREFIX + "Alerts unmuted.");
					} else if (args[0].equalsIgnoreCase("toggle")) {
						alertsPlayer.setMuted(!alertsPlayer.isMuted());
						sender.sendMessage(PREFIX + "Alerts " + (alertsPlayer.isMuted() ? "" : "un") + "muted");
					} else if (args[0].equalsIgnoreCase("sound")) {
						alerts.playSound(player);
						sender.sendMessage(PREFIX + "Test sound sent.");
					}
				}
			}
		}
		return true;
	}
}
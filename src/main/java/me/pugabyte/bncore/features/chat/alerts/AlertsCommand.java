package me.pugabyte.bncore.features.chat.alerts;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.alerts.Alerts;
import me.pugabyte.bncore.models.alerts.AlertsService;
import me.pugabyte.bncore.utils.Tasks;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;

import static me.pugabyte.bncore.utils.Utils.colorize;

public class AlertsCommand extends CustomCommand {
	private AlertsService service = new AlertsService();
	private Alerts alerts;

	public AlertsCommand(CommandEvent event) {
		super(event);
		if (sender() instanceof Player)
			alerts = service.get(player());
	}

	@Path
	void main() {
		TextComponent message = new TextComponent(PREFIX + colorize("Receive a &e'ping' noise &3whenever a " +
				"word or phrase in your &c/alerts list &3is said in chat. Make sure you have your 'Players' sound on!"));
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

		player().sendMessage("");
		player().spigot().sendMessage(message);
		player().sendMessage("");
		player().spigot().sendMessage(edit);
		player().spigot().sendMessage(commandList);
		player().spigot().sendMessage(commandAdd);
		player().spigot().sendMessage(commandDelete);
		player().spigot().sendMessage(commandClear);
		player().spigot().sendMessage(commandMute);
	}

	@Path("(list|edit)")
	void list() {
		if (alerts.getHighlights().size() == 0)
			error("&3You don't have any alerts! Add some with &c/alerts add <word or phrase>");

		send(PREFIX + "&eYour alerts list:");
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

			player().spigot().sendMessage(builder.create());
		}
	}

	@Path("add <highlight...>")
	void add(@Arg String highlight) {
		if (highlight.equalsIgnoreCase(player().getName()))
			error("Your name is automatically included in your alerts list");

		if (!alerts.add(highlight))
			error("You already had &e" + highlight + " &3in your alerts list");

		service.save(alerts);
		send(PREFIX + "Added &e" + highlight + " &3to your alerts list");
	}

	@Path("delete <highlight...>")
	void delete(@Arg String highlight) {
		if (!alerts.delete(highlight))
			error("You did not have &e" + highlight + " &3in your alerts list");

		service.save(alerts);
		send(PREFIX + "Removed &e" + highlight + " &3from your alerts list");
	}

	@Path("(partialmatch|partialmatching) <truse|false> [highlight...]")
	void partialMatching(@Arg boolean partialMatching, @Arg String highlight) {
		Optional<Alerts.Highlight> match = alerts.get(highlight);
		if (!match.isPresent())
			error("I could not find that alert in your alerts list");

		alerts.delete(highlight);
		alerts.add(highlight, partialMatching);
		service.save(alerts);
		line();
		send(PREFIX + "Partial matching for alert " + ChatColor.YELLOW + highlight + ChatColor.DARK_AQUA + " "
				+ (partialMatching ? "enabled" : "disabled"));
		line();
		Tasks.wait(2, () -> Bukkit.dispatchCommand(player(), "alerts edit"));
	}

	@Path("clear")
	void clear() {
		alerts.clear();
		service.save(alerts);
		send(PREFIX + "Cleared your alerts");
	}

	@Path("mute")
	void mute() {
		alerts.setMuted(true);
		send(PREFIX + "Alerts muted");
	}

	@Path("unmute")
	void unmute() {
		alerts.setMuted(false);
		send(PREFIX + "Alerts unmuted");
	}

	@Path("toggle")
	void toggle() {
		alerts.setMuted(!alerts.isMuted());
		send(PREFIX + "Alerts " + (alerts.isMuted() ? "" : "un") + "muted");
	}

	@Path("sound")
	void sound() {
		alerts.playSound();
		send(PREFIX + "Test sound sent");
	}

}

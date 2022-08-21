package gg.projecteden.nexus.features.chat.alerts;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.alerts.Alerts;
import gg.projecteden.nexus.models.alerts.AlertsService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.Optional;

public class AlertsCommand extends CustomCommand {
	private final AlertsService service = new AlertsService();
	private Alerts alerts;

	public AlertsCommand(CommandEvent event) {
		super(event);
		if (sender() instanceof Player)
			alerts = service.get(player());
	}

	@Path
	void main() {
		new JsonBuilder()
				.next("&3Receive a &e'ping' noise &3whenever a word or phrase in your &c/alerts list &3is said in chat. ")
				.next("&3Make sure you have your 'Players' sound on!")
				.line()
				.next("&3You can edit your alerts with the following commands:")
				.newline()
				.next("&c /alerts list").suggest("/alerts list ")
				.newline()
				.next("&c /alerts add <word or phrase>").suggest("/alerts add ")
				.newline()
				.next("&c /alerts delete <word or phrase>").suggest("/alerts delete ")
				.newline()
				.next("&c /alerts clear").suggest("/alerts clear")
				.send(player());
	}

	@Path("(list|edit)")
	void list() {
		if (alerts.getHighlights().size() == 0)
			error("&3You don't have any alerts! Add some with &c/alerts add <word or phrase>");

		send(PREFIX + "&eYour alerts list:");
		for (Alerts.Highlight highlight : alerts.getHighlights()) {

			JsonBuilder builder = new JsonBuilder();

			send("&e- &3" + highlight.getHighlight());

			if (highlight.isPartialMatching()) {
				builder.next("    &7[&a✔ Partially Matching&7]")
					.command("/alerts partialmatch " + highlight.getHighlight())
					.hover("&eClick to turn off partial matching")
					.group();
			} else {
				builder.next("    &7[&c✕ Partially Matching&7]")
					.command("/alerts partialmatch " + highlight.getHighlight())
					.hover("&eClick to turn on partial matching")
					.group();
			}

			if (highlight.isNegated()) {
				builder.next(" &7[&a✔ Negated&7]")
					.command("/alerts negate " + highlight.getHighlight())
					.hover("&eClick to turn off negation")
					.group();
			} else {
				builder.next(" &7[&c✕ Negated&7]")
					.command("/alerts negate " + highlight.getHighlight())
					.hover("&eClick to turn on negation")
					.group();
			}

			builder.send(player());
		}
	}

	@Path("add <highlight...>")
	void add(String highlight) {
		if (highlight.equalsIgnoreCase(name()))
			error("Your name is automatically included in your alerts list");
		if (highlight.equalsIgnoreCase(nickname()))
			error("Your nickname is automatically included in your alerts list");

		if (!alerts.add(highlight))
			error("You already had &e" + highlight + " &3in your alerts list");

		service.save(alerts);
		send(PREFIX + "Added &e" + highlight + " &3to your alerts list");
	}

	@Path("delete <highlight...>")
	void delete(String highlight) {
		if (!alerts.delete(highlight))
			error("You do not have &e" + highlight + " &cin your alerts list");

		service.save(alerts);
		send(PREFIX + "Removed &e" + highlight + " &3from your alerts list");
	}

	@Path("(partialmatch|partialmatching) <highlight...>")
	void partialMatching(String highlight) {
		Optional<Alerts.Highlight> match = alerts.get(highlight);
		if (!match.isPresent())
			error("You do not have &e" + highlight + " &cin your alerts list");

		match.get().setPartialMatching(!match.get().isPartialMatching());
		service.save(alerts);
		line();
		send(PREFIX + "Partial matching for alert &e" + highlight
			+ " &3" + (match.get().isPartialMatching() ? "enabled" : "disabled"));
		line();
		Tasks.wait(2, () -> PlayerUtils.runCommand(player(), "alerts edit"));
	}

	@Path("negate <highlight...>")
	void negated(String highlight) {
		Optional<Alerts.Highlight> match = alerts.get(highlight);
		if (!match.isPresent())
			error("You do not have &e" + highlight + " &cin your alerts list");

		match.get().setNegated(!match.get().isNegated());
		service.save(alerts);
		line();
		send(PREFIX + "Negation for alert &e" + highlight
			+ " &3" + (match.get().isNegated() ? "enabled" : "disabled"));
		line();
		Tasks.wait(2, () -> PlayerUtils.runCommand(player(), "alerts edit"));
	}

	@Path("clear")
	void clear() {
		alerts.clear();
		service.save(alerts);
		send(PREFIX + "Cleared your alerts");
	}

	@Path("sound")
	void sound() {
		alerts.playSound();
		send(PREFIX + "Test sound sent");
	}

}

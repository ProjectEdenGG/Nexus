package gg.projecteden.nexus.features.chat.alerts;

import gg.projecteden.nexus.framework.commandsv2.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commandsv2.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Vararg;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.Confirm;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.HideFromHelp;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.TabCompleteIgnore;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.HideFromWiki;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.alerts.Alerts;
import gg.projecteden.nexus.models.alerts.Alerts.Highlight;
import gg.projecteden.nexus.models.alerts.AlertsService;
import gg.projecteden.nexus.utils.JsonBuilder;
import org.bukkit.entity.Player;

import java.util.List;

public class AlertsCommand extends CustomCommand {
	private final AlertsService service = new AlertsService();
	private Alerts alerts;

	public AlertsCommand(CommandEvent event) {
		super(event);
		if (sender() instanceof Player)
			alerts = service.get(player());
	}

	@Override
	@NoLiterals
	@Description("Help menu")
	public void help() {
		new JsonBuilder(PREFIX)
				.next("&3Receive a &e'ping' noise &3whenever a word or phrase in your &c/alerts list &3is said in chat. ")
				.next("&3Make sure you have your 'Players' sound on!")
				.send(player());

		super.help();
	}

	@Description("List and edit your existing alerts")
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

	@Description("Add a new alert")
	void add(@Vararg String highlight) {
		if (highlight.equalsIgnoreCase(name()))
			error("Your name is automatically included in your alerts list");
		if (highlight.equalsIgnoreCase(nickname()))
			error("Your nickname is automatically included in your alerts list");

		if (!alerts.add(highlight))
			error("You already had &e" + highlight + " &3in your alerts list");

		service.save(alerts);
		send(PREFIX + "Added &e" + highlight + " &3to your alerts list");
	}

	@Description("Delete an alert")
	void delete(@Vararg Highlight highlight) {
		alerts.delete(highlight);
		service.save(alerts);

		send(PREFIX + "Removed &e" + highlight.getHighlight() + " &3from your alerts list");
	}

	@HideFromWiki
	@HideFromHelp
	@TabCompleteIgnore
	void partialMatching(@Vararg Highlight highlight) {
		highlight.setPartialMatching(!highlight.isPartialMatching());
		service.save(alerts);

		line();
		send(PREFIX + "Partial matching for alert &e%s %s".formatted(highlight.getHighlight(), highlight.isPartialMatching() ? "&aenabled" : "&cdisabled"));
		line();
		list();
	}

	@HideFromWiki
	@HideFromHelp
	@TabCompleteIgnore
	void negate(@Vararg Highlight highlight) {
		highlight.setNegated(!highlight.isNegated());
		service.save(alerts);

		line();
		send(PREFIX + "Negation for alert &e%s %s".formatted(highlight.getHighlight(), highlight.isNegated() ? "&aenabled" : "&cdisabled"));
		line();
		list();
	}

	@Confirm
	@Description("Clear your alerts list")
	void clear() {
		alerts.clear();
		service.save(alerts);
		send(PREFIX + "Cleared your alerts");
	}

	@Description("Play a test alerts sound")
	void sound() {
		alerts.playSound();
		send(PREFIX + "Test sound sent");
	}

	@TabCompleterFor(Highlight.class)
	List<String> tabCompleteHighlight(String filter) {
		return alerts.getHighlights().stream()
			.map(Highlight::getHighlight)
			.filter(highlight -> highlight.toLowerCase().startsWith(filter.toLowerCase()))
			.toList();
	}

	@ConverterFor(Highlight.class)
	Highlight convertToHighlight(String value) {
		return alerts.get(value).orElseThrow(() -> new InvalidInputException("You do not have &e" + value + " &cin your alerts list"));
	}

}

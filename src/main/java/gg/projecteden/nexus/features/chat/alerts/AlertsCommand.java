package gg.projecteden.nexus.features.chat.alerts;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Confirm;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromHelp;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.alerts.Alerts;
import gg.projecteden.nexus.models.alerts.Alerts.Highlight;
import gg.projecteden.nexus.models.alerts.AlertsService;
import gg.projecteden.nexus.models.chat.PublicChannel;
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

	@Path
	@Override
	@Description("Help menu")
	public void help() {
		new JsonBuilder(PREFIX)
			.next("&3Receive a &e'ping' noise &3whenever a word or phrase in your &c/alerts list &3is said in chat. ")
			.next("&3Make sure you have your 'Players' sound on!")
			.send(player());

		super.help();
	}

	@Path("(list|edit)")
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

	@Path("add <highlight...>")
	@Description("Add a new alert")
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
	@Description("Delete an alert")
	void delete(Highlight highlight) {
		alerts.delete(highlight);
		service.save(alerts);

		send(PREFIX + "Removed &e" + highlight.getHighlight() + " &3from your alerts list");
	}

	@HideFromWiki
	@HideFromHelp
	@TabCompleteIgnore
	@Path("(partialmatch|partialmatching) <highlight...>")
	void partialMatching(Highlight highlight) {
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
	@Path("negate <highlight...>")
	void negated(Highlight highlight) {
		highlight.setNegated(!highlight.isNegated());
		service.save(alerts);

		line();
		send(PREFIX + "Negation for alert &e%s %s".formatted(highlight.getHighlight(), highlight.isNegated() ? "&aenabled" : "&cdisabled"));
		line();
		list();
	}

	@Confirm
	@Path("clear")
	@Description("Clear your alerts list")
	void clear() {
		alerts.clear();
		service.save(alerts);
		send(PREFIX + "Cleared your alerts");
	}

	@Path("channel <channel> [state]")
	@Description("Toggle always alerting for messages in a channel")
	void channel(PublicChannel channel, Boolean state) {
		if (state == null)
			state = !alerts.getChannels().contains(channel);

		if (state) {
			alerts.getChannels().add(channel);
			send(PREFIX + "Now always alerting for messages in &e" + channel.getName());
		} else {
			alerts.getChannels().remove(channel);
			send(PREFIX + "No longer always alerting for messages in &e" + channel.getName());
		}

		service.save(alerts);
	}

	@Path("test")
	@Description("Play a test alerts sound")
	void test() {
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

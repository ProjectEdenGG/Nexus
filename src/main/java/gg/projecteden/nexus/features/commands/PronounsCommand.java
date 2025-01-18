package gg.projecteden.nexus.features.commands;

import gg.projecteden.api.mongodb.models.nerd.Nerd.Pronoun;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.nerd.Nerd;
import lombok.NonNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PronounsCommand extends CustomCommand {

	public PronounsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[user]")
	@Description("View a player's pronouns")
	void list(@Arg("self") Nerd nerd) {
		Set<Pronoun> pronouns = nerd.getPronouns();
		if (pronouns.isEmpty())
			error((isSelf(nerd) ? "You have" : nerd.getNickname() + " has") + " no saved pronouns");

		send(PREFIX + (isSelf(nerd) ? "Your" : nerd.getNickname() + "'s") + " pronouns are " +
			pronouns.stream().map(pronoun -> "&e" + pronoun + "&3").collect(Collectors.joining(", ")));
	}

	@Path("add <pronoun> [user]")
	@Description("Add a pronoun to your pronouns")
	void add(Pronoun pronoun, @Arg(value = "self", permission = Group.STAFF) Nerd nerd) {
		String user = isSelf(nerd) ? "your" : nerd.getNickname() + "'s";
		nerd.addPronoun(pronoun, !isPlayer() ? "Console" : nickname());
		send(PREFIX + "Added &e" + pronoun + "&3 to " + user + " pronouns");
	}

	@Path("remove <pronoun> [user]")
	@Description("Remove a pronoun from your pronouns")
	void remove(Pronoun pronoun, @Arg(value = "self", permission = Group.STAFF) Nerd nerd) {
		String user = isSelf(nerd) ? "your" : nerd.getNickname() + "'s";
		nerd.removePronoun(pronoun, !isPlayer() ? "Console" : nickname());
		send(PREFIX + "Removed &e" + pronoun + "&3 from " + user + " pronouns");
	}

	@ConverterFor(Pronoun.class)
	Pronoun convertToPronoun(String value) {
		try {
			return convertToEnum(value.replaceAll("/", "_"), Pronoun.class);
		} catch (InvalidInputException ex) {
			throw new InvalidInputException("Pronoun &e" + value + " &cnot whitelisted");
		}
	}

	@TabCompleterFor(Pronoun.class)
	List<String> tabCompletePronoun(String filter) {
		return tabCompleteEnum(filter, Pronoun.class, Pronoun::toString);
	}

}

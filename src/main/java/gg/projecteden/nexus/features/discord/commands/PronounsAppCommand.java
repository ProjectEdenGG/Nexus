package gg.projecteden.nexus.features.discord.commands;

import gg.projecteden.api.discord.appcommands.AppCommandEvent;
import gg.projecteden.api.discord.appcommands.AppCommandRegistry;
import gg.projecteden.api.discord.appcommands.annotations.Choices;
import gg.projecteden.api.discord.appcommands.annotations.Command;
import gg.projecteden.api.discord.appcommands.annotations.Desc;
import gg.projecteden.api.mongodb.models.nerd.Nerd.Pronoun;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.features.discord.commands.common.NexusAppCommand;
import gg.projecteden.nexus.features.discord.commands.common.annotations.Verify;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import org.jetbrains.annotations.NotNull;

@Verify
@Command("Manage your pronouns")
public class PronounsAppCommand extends NexusAppCommand {

	public PronounsAppCommand(AppCommandEvent event) {
		super(event);
	}

	@NotNull
	private String format(Pronoun pronoun) {
		return Discord.discordize(pronoun.toString()).replaceAll("[*`@]", "");
	}

	@Command("Add a pronoun")
	void add(@Desc("Pronoun") @Choices(Pronoun.class) Pronoun pronoun) {
		nerd().addPronoun(pronoun);
		replyEphemeral(String.format("Added **%s** to your pronouns", format(pronoun)));
	}

	@Command("Remove a pronoun")
	void remove(@Desc("Pronoun") @Choices(Pronoun.class) Pronoun pronoun) {
		nerd().removePronoun(pronoun);
		replyEphemeral(String.format("Remove **%s** from your pronouns", format(pronoun)));
	}

	static {
		AppCommandRegistry.supplyChoices(Pronoun.class, () -> AppCommandRegistry.loadEnumChoices(Pronoun.class, Pronoun::toString));
		AppCommandRegistry.registerConverter(Pronoun.class, argument -> {
			try {
				return AppCommandRegistry.convertToEnum(Pronoun.class, argument.getInput().replaceAll("/", "_"));
			} catch (InvalidInputException ex) {
				throw new InvalidInputException("Pronoun &e" + argument.getInput() + " &cnot whitelisted");
			}
		});
	}

}

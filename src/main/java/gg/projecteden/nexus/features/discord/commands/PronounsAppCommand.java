package gg.projecteden.nexus.features.discord.commands;

import gg.projecteden.discord.appcommands.AppCommandEvent;
import gg.projecteden.discord.appcommands.AppCommandRegistry;
import gg.projecteden.discord.appcommands.annotations.Choices;
import gg.projecteden.discord.appcommands.annotations.Command;
import gg.projecteden.discord.appcommands.annotations.Desc;
import gg.projecteden.models.nerd.Nerd.Pronoun;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.HandledBy;
import gg.projecteden.nexus.features.discord.appcommands.NexusAppCommand;
import gg.projecteden.nexus.features.discord.appcommands.annotations.Verify;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import org.jetbrains.annotations.NotNull;

import static gg.projecteden.nexus.features.discord.Discord.discordize;

@Verify
@HandledBy(Bot.KODA)
public class PronounsAppCommand extends NexusAppCommand {

	public PronounsAppCommand(AppCommandEvent event) {
		super(event);
	}

	@NotNull
	private String format(Pronoun pronoun) {
		return discordize(pronoun.toString()).replaceAll("[*`@]", "");
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

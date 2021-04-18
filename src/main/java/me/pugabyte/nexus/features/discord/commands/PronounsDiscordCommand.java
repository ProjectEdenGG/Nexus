package me.pugabyte.nexus.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.pugabyte.nexus.features.discord.Bot;
import me.pugabyte.nexus.features.discord.HandledBy;

@HandledBy(Bot.KODA)
public class PronounsDiscordCommand extends Command {
	public PronounsDiscordCommand() {
		this.name = "pronouns";
		this.aliases = new String[]{"pronoun"};
	}

	protected void execute(CommandEvent event) {
		/* disabled until pride month */
		/*
		Tasks.async(() -> {
			DiscordUser user = new DiscordUserService().checkVerified(event.getAuthor().getId());
			String pronoun = PronounsCommand.getPronoun(event.getArgs());
			Nerd nerd = Nerd.of(user);
			String output;
			if (nerd.getPronouns().contains(pronoun)) {
				nerd.removePronoun(pronoun);
				output = "Removed **%s** from your pronouns";
			} else {
				nerd.addPronoun(pronoun);
				output = "Added **%s** to your pronouns";
			}
			event.reply(String.format(output, Discord.discordize(pronoun).replaceAll("[*`@]", "")));
		});
		*/
	}
}

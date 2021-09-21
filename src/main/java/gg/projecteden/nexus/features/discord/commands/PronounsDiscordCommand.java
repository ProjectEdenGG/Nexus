package gg.projecteden.nexus.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import gg.projecteden.exceptions.EdenException;
import gg.projecteden.models.nerd.Nerd.Pronoun;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.HandledBy;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.Tasks;

import java.util.Set;
import java.util.function.Function;

import static gg.projecteden.nexus.features.discord.Discord.discordize;
import static gg.projecteden.nexus.utils.StringUtils.stripColor;

@HandledBy(Bot.KODA)
public class PronounsDiscordCommand extends Command {

	public PronounsDiscordCommand() {
		this.name = "pronouns";
		this.aliases = new String[]{"pronoun"};
	}

	private static final String USAGE = "Proper usage: `/pronouns <add|remove> [pronouns]`";

	protected void execute(CommandEvent event) {
		Tasks.async(() -> {
			try {
				DiscordUser user = new DiscordUserService().checkVerified(event.getAuthor().getId());
				String[] args = event.getArgs().toLowerCase().split(" ");
				if (!(args.length == 1 || args.length == 2))
					throw new InvalidInputException(USAGE);

				Nerd nerd = Nerd.of(user);
				Pronoun pronoun;

				Function<String, Pronoun> parse = input -> {
					Pronoun parsed = Pronoun.of(args[0]);
					if (parsed == null)
						throw new InvalidInputException("Pronoun `" + input + "` not whitelisted");

					return parsed;
				};

				boolean add;
				if (args.length == 1) {
					pronoun = parse.apply(args[0]);
					add = !nerd.getPronouns().contains(pronoun);
				} else {
					pronoun = parse.apply(args[1]);
					if (Set.of("add", "enable", "set").contains(args[0]))
						add = true;
					else if (Set.of("remove", "disable").contains(args[0]))
						add = false;
					else
						throw new InvalidInputException(USAGE);
				}

				String output;

				if (add) {
					nerd.addPronoun(pronoun);
					output = "Added **%s** to your pronouns";
				} else {
					nerd.removePronoun(pronoun);
					output = "Removed **%s** from your pronouns";
				}

				event.reply(String.format(output, discordize(pronoun.toString()).replaceAll("[*`@]", "")));
			} catch (Exception ex) {
				event.reply(stripColor(ex.getMessage()));
				if (!(ex instanceof EdenException))
					ex.printStackTrace();
			}
		});
	}
}

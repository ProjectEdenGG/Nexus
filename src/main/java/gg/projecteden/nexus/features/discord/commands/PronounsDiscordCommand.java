package gg.projecteden.nexus.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.features.discord.HandledBy;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.Tasks;

import java.util.Set;

@HandledBy(Bot.KODA)
public class PronounsDiscordCommand extends Command {
	public PronounsDiscordCommand() {
		this.name = "pronouns";
		this.aliases = new String[]{"pronoun"};
	}

	private static final String usage = "Proper usage: `/pronouns [add|remove] [pronouns]`";

	protected void execute(CommandEvent event) {
		Tasks.async(() -> {
			DiscordUser user = new DiscordUserService().checkVerified(event.getAuthor().getId());
			String[] args = event.getArgs().toLowerCase().split(" ");
			if (!(args.length == 1 || args.length == 2)) {
				event.reply(usage);
				return;
			}
			Nerd nerd = Nerd.of(user);
			String pronoun;
			boolean add;
			if (args.length == 1) {
				pronoun = args[0];
				add = !nerd.getPronouns().contains(pronoun);
			} else {
				pronoun = args[1];
				if (Set.of("add", "enable", "set").contains(args[0]))
					add = true;
				else if (Set.of("remove", "disable").contains(args[0]))
					add = false;
				else {
					event.reply(usage);
					return;
				}
			}

			String output;
			if (add) {
				nerd.addPronoun(pronoun);
				output = "Added **%s** to your pronouns";
			} else {
				nerd.removePronoun(pronoun);
				output = "Removed **%s** from your pronouns";
			}
			event.reply(String.format(output, Discord.discordize(pronoun).replaceAll("[*`@]", "")));
		});
	}
}

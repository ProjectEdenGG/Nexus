package gg.projecteden.nexus.features.store.perks.chat;

import gg.projecteden.nexus.features.chat.Chat.StaticChannel;
import gg.projecteden.nexus.features.chat.Emotes;
import gg.projecteden.nexus.features.chat.commands.EmotesCommand;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.HideFromWiki;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.WikiConfig;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.chat.PublicChannel;
import gg.projecteden.nexus.models.emote.EmoteService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.NerdService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.StringUtils.Gradient;
import gg.projecteden.nexus.utils.StringUtils.Rainbow;
import net.md_5.bungee.api.ChatColor;

import java.util.List;
import java.util.regex.Matcher;

import static gg.projecteden.nexus.utils.Nullables.isNullOrEmpty;
import static gg.projecteden.nexus.utils.StringUtils.decolorize;
import static gg.projecteden.nexus.utils.StringUtils.stripColor;
import static gg.projecteden.nexus.utils.StringUtils.stripFormat;

@WikiConfig(rank = "Store", feature = "Chat")
public class PrefixCommand extends CustomCommand {
	public static final String PERMISSION = "set.my.prefix";
	private final NerdService service = new NerdService();
	private Nerd nerd;

	public PrefixCommand(CommandEvent event) {
		super(event);
		if (isPlayer())
			nerd = Nerd.of(player());
	}

	@HideFromWiki
	@Path("checkmark")
	@Permission("donated")
	void checkmark() {
		error("Moved to /badge supporter");
	}

	@Path("reset [player]")
	@Permission("set.my.prefix")
	@Description("Reset your prefix")
	void reset(@Optional("self") Nerd nerd) {
		nerd.setPrefix(null);
		service.save(nerd);
		send(PREFIX + "Reset " + (isSelf(nerd) ? "your" : nerd.getNickname() + "'s") + " prefix");
	}

	@NoLiterals
	@Path("<prefix...>")
	@Permission("set.my.prefix")
	@Description("Set your prefix")
	void prefix(String input) {
		input = validate(input);
		nerd.setPrefix(input);
		service.save(nerd);
		send(PREFIX + "Your prefix has been set to &8&l[&f" + input + "&8&l]");
	}

	@Path("gradient <colors> <prefix...>")
	@Permission("set.my.prefix")
	@Description("Set your prefix with a color gradient")
	void gradient(@ErasureType(ChatColor.class) List<ChatColor> colors, String input) {
		prefix(Gradient.of(colors).apply(input));
	}

	@Path("rainbow <prefix...>")
	@Permission("set.my.prefix")
	@Description("Set your prefix with a rainbow gradient")
	void rainbow(String input) {
		prefix(Rainbow.apply(input));
	}

	@Path("copy [player]")
	@Description("Print your prefix to chat for copying")
	void copy(@Optional("self") Nerd nerd) {
		String prefix = nerd.getPrefix();

		if (isNullOrEmpty(prefix))
			prefix = Rank.of(player()).getPrefix();

		if (isNullOrEmpty(prefix))
			error((isSelf(nerd) ? "You do" : nerd.getNickname() + " does") + " not have a prefix");

		String original = prefix;

		while (true) {
			Matcher matcher = StringUtils.getHexColorizedPattern().matcher(prefix);
			if (!matcher.find()) break;

			String group = matcher.group();
			prefix = prefix.replace(group, group.replaceAll(StringUtils.getColorChar() + "x", "&#").replaceAll(StringUtils.getColorChar(), ""));
		}

		send(json(PREFIX + "Click here to copy " + (isSelf(nerd) ? "your" : nerd.getNickname() + "'s") + " current prefix: &f" + original).copy(decolorize(prefix)).hover("&7Click to copy"));
	}

	@Path("test <prefix...>")
	@Description("Preview a prefix in chat")
	void test_prefix(String input) {
		input = validate(input);
		final PublicChannel channel = StaticChannel.GLOBAL.getChannel();
		final ChatColor channelColor = channel.getColor();
		send("&6&l[Example] %s[%s] &8&l[&f%s&8&l] %s %s> %sHello!".formatted(channelColor, channel.getNickname(),
			input, nerd().getColoredName(), channelColor, channel.getMessageColor()));
	}

	@Path("test gradient <colors> <prefix...>")
	@Description("Preview a prefix with a color gradient in chat")
	void test_gradient(@ErasureType(ChatColor.class) List<ChatColor> colors, String input) {
		test_prefix(Gradient.of(colors).apply(input));
	}

	@Path("test rainbow <prefix...>")
	@Description("Preview a prefix with a rainbow gradient in chat")
	void test_rainbow(String input) {
		test_prefix(Rainbow.apply(input));
	}

	private String validate(String input) {
		if (player().hasPermission(EmotesCommand.PERMISSION))
			input = Emotes.process(new EmoteService().get(player()), input);

		final int length = stripColor(input).length();
		if (length > 10)
			error("Your prefix cannot be more than 10 characters (was " + length + ")");

		return stripFormat(input);
	}

}

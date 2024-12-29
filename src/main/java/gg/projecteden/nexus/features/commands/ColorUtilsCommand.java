package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.StringUtils.Gradient;
import gg.projecteden.nexus.utils.StringUtils.Rainbow;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;

import java.util.List;

public class ColorUtilsCommand extends CustomCommand {

	public ColorUtilsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("getHex <color>")
	@Description("Get the hex code of a default color")
	void getHex(ChatColor color) {
		String hex = StringUtils.toHex(color);
		send(json("&" + hex + hex).copy(hex).hover("Click to copy"));
	}

	@Path("getRankHex <rank>")
	@Description("Get the hex code of a rank")
	void getHex(Rank rank) {
		getHex(rank.getChatColor());
	}

	@Path("runSpigotHexCommand <commandNoSlash...>")
	@HideFromWiki
	@Permission(Group.ADMIN)
	void runHexCommand(String commandNoSlash) {
		runCommand(StringUtils.decolorize(commandNoSlash));
	}

	@Path("gradient <colors> <input...> [--decolorize]")
	@Description("Color gradient your input")
	void gradient(@Arg(type = ChatColor.class) List<ChatColor> colors, String input, @Switch boolean decolorize) {
		final String gradient = Gradient.of(colors).apply(input);
		String message = decolorize ? StringUtils.decolorize(gradient) : StringUtils.colorize(gradient);
		send(json(message).hover("Shift+Click to insert").insert(message));
	}

	@Path("rainbow <input...> [--decolorize]")
	@Description("Rainbow-ize your input")
	void rainbow(String input, @Switch boolean decolorize) {
		final String rainbow = Rainbow.apply(input);
		String message = decolorize ? StringUtils.decolorize(rainbow) : StringUtils.colorize(rainbow);
		send(json(message).hover("Shift+Click to insert").insert(message));
	}

}

package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.HideFromWiki;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Switch;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.StringUtils.Gradient;
import gg.projecteden.nexus.utils.StringUtils.Rainbow;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;

import java.util.List;

import static gg.projecteden.nexus.utils.StringUtils.colorize;
import static gg.projecteden.nexus.utils.StringUtils.decolorize;
import static gg.projecteden.nexus.utils.StringUtils.toHex;

public class ColorUtilsCommand extends CustomCommand {

	public ColorUtilsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("getHex <color>")
	@Description("Get the hex code of a default color")
	void getHex(ChatColor color) {
		String hex = toHex(color);
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
		runCommand(decolorize(commandNoSlash));
	}

	@Path("gradient <colors> <input...> [--decolorize]")
	@Description("Color gradient your input")
	void gradient(@ErasureType(ChatColor.class) List<ChatColor> colors, String input, @Switch boolean decolorize) {
		final String gradient = Gradient.of(colors).apply(input);
		String message = decolorize ? decolorize(gradient) : colorize(gradient);
		send(json(message).hover("Shift+Click to insert").insert(message));
	}

	@Path("rainbow <input...> [--decolorize]")
	@Description("Rainbow-ize your input")
	void rainbow(String input, @Switch boolean decolorize) {
		final String rainbow = Rainbow.apply(input);
		String message = decolorize ? decolorize(rainbow) : colorize(rainbow);
		send(json(message).hover("Shift+Click to insert").insert(message));
	}

}

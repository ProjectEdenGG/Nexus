package me.pugabyte.nexus.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Description;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nerd.Rank;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.StringUtils.Gradient;
import me.pugabyte.nexus.utils.StringUtils.Rainbow;
import net.md_5.bungee.api.ChatColor;

import static me.pugabyte.nexus.utils.StringUtils.colorize;
import static me.pugabyte.nexus.utils.StringUtils.decolorize;

public class ColorUtilsCommand extends CustomCommand {

	public ColorUtilsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("getHex <color>")
	void getHex(ChatColor color) {
		String hex = "#" + Integer.toHexString(color.getColor().getRGB()).substring(2);
		send(json("&" + hex + hex).copy(hex).hover("Click to copy"));
	}

	@Path("getRankHex <color>")
	void getHex(Rank rank) {
		String hex = rank.getColor().toString();
		send(json("&" + hex + hex).copy(hex).hover("Click to copy"));
	}

	@Path("getSpigotHex <input...>")
	void getSpigotHex(String input) {
		send(json("Click to copy").copy(decolorize(colorize(input))));
	}

	@Path("runSpigotHexCommand <commandNoSlash...>")
	void runHexCommand(String commandNoSlash) {
		runCommand(decolorize(colorize(commandNoSlash)));
	}

	@Path("setNpcName withPrefix <player>")
	void setNpcNameWithFormat(Nerd nerd) {
		runCommand("npc rename " + decolorize(colorize("&8&l[" + nerd.getRank().withColor() + "&8&l] " + nerd.getRank().getColor() + nerd.getName())));
	}

	@Path("setNpcName withColor <player>")
	void setNpcNameWithColor(Nerd nerd) {
		runCommand("npc rename " + decolorize(colorize(nerd.getRank().getColor().toString())) + nerd.getName());
	}

	@Description("Get the last color used in a string (including formatting)")
	@Path("getLastColor <message...>")
	void getLastColor(String message) {
		send(StringUtils.getLastColor(message) + "Last color");
	}

	@Path("gradient <color1> <color2> <input>")
	void gradient(ChatColor color1, ChatColor color2, String input) {
		send(Gradient.of(color1, color2).apply(input));
	}

	@Path("rainbow <input>")
	void rainbow(String input) {
		send(Rainbow.apply(input));
	}

}

package me.pugabyte.bncore.features.minigames.commands;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.utils.CitizensUtils;
import me.pugabyte.bncore.utils.Utils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Permission("minigames.manage")
public class PodiumsCommand extends CustomCommand {

	public PodiumsCommand(CommandEvent event) {
		super(event);
	}

	@Path("help")
	void help() {
		send("&c/podiums (left|right|middle) <player> <title...>");
		send("&c/podiums (tp|s) <position>");
		send("&c/podiums getId[s] [position]");
		send("&c/podiums setId <position> <id>");
	}

	@Path("<position> <player> <title...>")
	void update(@Arg Position position, @Arg OfflinePlayer player, @Arg String title) {
		CitizensUtils.updateNameAndSkin(getNpc(position), "&l" + player.getName());
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hd setline podium_" + position + " 1 " + title);
		send(PREFIX + "Podium updated");
	}

	@Path("(getId|getIds) [position]")
	void getId(@Arg Position position) {
		if (position == null) {
			send(PREFIX + "Podium IDs:");
			Arrays.asList(Position.values()).forEach(_position -> send("&3" + Utils.camelCase(_position.name()) +
					": &e" + getPodiumId(_position)));
		} else
			send(PREFIX + Utils.camelCase(position.name()) + ": &e" + getPodiumId(position));
	}

	@Path("setId <position> <id>")
	void setId(@Arg Position position, @Arg int id) {
		setPodiumId(position, id);
		send(PREFIX + Utils.camelCase(position.name()) + " podium ID updated to " + id);
	}

	@Path("tp <position>")
	void tp(@Arg Position position) {
		player().teleport(getNpc(position).getEntity());
	}

	@Path("(s|summon) <position>")
	void tphere(@Arg Position position) {
		runCommand("blockcenter");
		getNpc(position).getEntity().teleport(player().getLocation());
	}

	private NPC getNpc(int podiumId) {
		return CitizensAPI.getNPCRegistry().getById(podiumId);
	}

	private NPC getNpc(Position position) {
		return CitizensAPI.getNPCRegistry().getById(getPodiumId(position));
	}

	private int getPodiumId(Position position) {
		return BNCore.getInstance().getConfig().getInt("minigames.podiums." + position.name().toLowerCase());
	}

	private void setPodiumId(Position position, int id) {
		BNCore.getInstance().getConfig().set("minigames.podiums." + position.name().toLowerCase(), id);
		BNCore.getInstance().saveConfig();
	}

	static {
		Arrays.asList(Position.values()).forEach(position ->
				BNCore.getInstance().addConfigDefault("minigames.podiums." + position.name().toLowerCase(), 0));
	}

	public enum Position {
		LEFT,
		RIGHT,
		MIDDLE;

		public static Position get(String position) {
			if (position != null)
				switch (position.toLowerCase()) {
					case "l": case "left": return LEFT;
					case "r": case "right": return RIGHT;
					case "m": case "middle": return MIDDLE;
				}
			throw new InvalidInputException("Invalid podium position");
		}
	}

	@ConverterFor(Position.class)
	Position convertToPosition(String value) {
		return Position.get(value);
	}

	@TabCompleterFor(Position.class)
	public List<String> tabCompletePosition(String filter) {
		List<String> completions = Arrays.stream(Position.values()).map(position -> position.name().toLowerCase()).collect(Collectors.toList());
		new ArrayList<>(completions).forEach(position -> completions.add(String.valueOf(position.charAt(0))));
		return completions;
	}

}

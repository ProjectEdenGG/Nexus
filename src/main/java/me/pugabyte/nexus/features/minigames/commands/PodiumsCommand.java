package me.pugabyte.nexus.features.minigames.commands;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.utils.CitizensUtils;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Permission("minigames.manage")
public class PodiumsCommand extends CustomCommand {

	public PodiumsCommand(CommandEvent event) {
		super(event);
	}

	static {
		Arrays.asList(Position.values()).forEach(position ->
				Nexus.getInstance().addConfigDefault("minigames.podiums." + position.name().toLowerCase(), 0));
	}

	@Path("<position> <player> <title...>")
	void update(Position position, OfflinePlayer player, String title) {
		CitizensUtils.updateName(getNpc(position), "&l" + Nickname.of(player));
		CitizensUtils.updateSkin(getNpc(position), player.getName());
		PlayerUtils.runCommandAsConsole("hd setline podium_" + position + " 1 " + title);
		send(PREFIX + "Podium updated");
	}

	@Path("(getId|getIds) [position]")
	void getId(Position position) {
		if (position == null) {
			send(PREFIX + "Podium IDs:");
			Arrays.asList(Position.values()).forEach(_position -> send("&3" + StringUtils.camelCase(_position.name()) +
					": &e" + getPodiumId(_position)));
		} else
			send(PREFIX + StringUtils.camelCase(position.name()) + ": &e" + getPodiumId(position));
	}

	@Path("setId <position> <id>")
	void setId(Position position, int id) {
		setPodiumId(position, id);
		send(PREFIX + StringUtils.camelCase(position.name()) + " podium ID updated to " + id);
	}

	@Path("tp <position>")
	void tp(Position position) {
		player().teleport(getNpc(position).getEntity(), TeleportCause.COMMAND);
	}

	@Path("(s|summon) <position>")
	void tphere(Position position) {
		runCommand("blockcenter");
		getNpc(position).getEntity().teleport(location());
	}

	private static NPC getNpc(int podiumId) {
		return CitizensAPI.getNPCRegistry().getById(podiumId);
	}

	public static NPC getNpc(Position position) {
		return CitizensAPI.getNPCRegistry().getById(getPodiumId(position));
	}

	private static int getPodiumId(Position position) {
		return Nexus.getInstance().getConfig().getInt("minigames.podiums." + position.name().toLowerCase());
	}

	private static void setPodiumId(Position position, int id) {
		Nexus.getInstance().getConfig().set("minigames.podiums." + position.name().toLowerCase(), id);
		Nexus.getInstance().saveConfig();
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

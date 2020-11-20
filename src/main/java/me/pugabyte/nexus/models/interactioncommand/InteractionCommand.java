package me.pugabyte.nexus.models.interactioncommand;

import com.dieselpoint.norm.serialize.DbSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.framework.persistence.serializer.mysql.LocationSerializer;
import me.pugabyte.nexus.models.nerd.Nerd;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerInteractEvent;

import javax.persistence.Id;
import javax.persistence.Table;

import static me.pugabyte.nexus.utils.StringUtils.right;
import static me.pugabyte.nexus.utils.Utils.runCommand;
import static me.pugabyte.nexus.utils.Utils.runCommandAsConsole;
import static me.pugabyte.nexus.utils.Utils.runCommandAsOp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "interaction_command")
public class InteractionCommand {
	@Id
	@DbSerializer(LocationSerializer.class)
	private Location location;
	private int index;
	private String command;

	public String getTrimmedCommand() {
		if (isOp() || isConsole())
			return right(command, command.length() - 2);
		else if (isNormal())
			return right(command, command.length() - 1);
		else
			return command;
	}

	public boolean isOp() {
		return command.startsWith("/^");
	}

	public boolean isConsole() {
		return command.startsWith("/#");
	}

	public boolean isNormal() {
		return !isOp() && !isConsole() && command.startsWith("/");
	}

	public void run(PlayerInteractEvent event) {
		if (!event.getPlayer().isOnline())
			return;

		String command = parse(event);
		if (isOp())
			runCommandAsOp(event.getPlayer(), command);
		else if (isConsole())
			runCommandAsConsole(command);
		else if (isNormal())
			runCommand(event.getPlayer(), command);
		else
			new Nerd(event.getPlayer()).send(command);
	}

	private String parse(PlayerInteractEvent event) {
		String command = getTrimmedCommand();
		command = command.replaceAll("\\[player]", event.getPlayer().getName());
		return command;
	}
}

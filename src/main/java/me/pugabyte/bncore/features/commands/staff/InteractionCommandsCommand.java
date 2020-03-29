package me.pugabyte.bncore.features.commands.staff;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.interactioncommand.InteractionCommand;
import me.pugabyte.bncore.models.interactioncommand.InteractionCommandService;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

import static me.pugabyte.bncore.utils.StringUtils.right;
import static me.pugabyte.bncore.utils.Utils.runConsoleCommand;

@NoArgsConstructor
@Permission("group.staff")
@Aliases({"cmds", "cmdsign"})
public class InteractionCommandsCommand extends CustomCommand implements Listener {
	InteractionCommandService service = new InteractionCommandService();
	Block target;
	InteractionCommand command;

	public InteractionCommandsCommand(@NonNull CommandEvent event) {
		super(event);
		target = player().getTargetBlock(null, 20);
		command = service.get(target.getLocation());
	}

	@Path("<command...>")
	void set(String command) {
		service.save(new InteractionCommand(target.getLocation(), command));
		send(PREFIX + "Set command to " + command);
	}

	@Path("(delete|remove|clear)")
	void delete() {
		if (command == null)
			error("There is no command present at that location");
		service.delete(command);
	}

	@Path("read")
	void read() {
		send(command.getCommand());
	}

	@Path("clearCache")
	void clearCache() {
		service.clearCache();
		send("Cache cleared");
	}

//	@Path("copy")
//	void copy() {
//
//	}
//
//	@Path("paste")
//	void paste() {
//
//	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (event.getClickedBlock() == null) return;
		if (event.getHand() == EquipmentSlot.OFF_HAND) return;
		InteractionCommand command = new InteractionCommandService().get(event.getClickedBlock().getLocation());
		if (command == null) return;

		run(event, command.getCommand());
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		InteractionCommandService service = new InteractionCommandService();
		InteractionCommand command = service.get(event.getBlock().getLocation());
		if (command == null) return;

		service.delete(event.getBlock().getLocation());
		send(event.getPlayer(), PREFIX + "Cleared");
	}

	public void run(PlayerInteractEvent event, String interactionCommand) {
		String command = parse(event, interactionCommand);
		if (command.startsWith("/^"))
			runCommandAsOp(event.getPlayer(), trim(command, 2));
		else if (command.startsWith("/#"))
			runConsoleCommand(trim(command, 2));
		else if (command.startsWith("/"))
			runCommand(event.getPlayer(), trim(command, 1));
		else
			send(event.getPlayer(), command);
	}

	@NotNull
	public String trim(String command, int i) {
		return right(command, command.length() - i);
	}

	private String parse(PlayerInteractEvent event, String toRun) {
		toRun = toRun.replaceAll("\\[player]", event.getPlayer().getName());
		return toRun;
	}
}

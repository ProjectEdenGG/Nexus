package gg.projecteden.nexus.features.commands.staff.moderator;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.interactioncommand.InteractionCommandConfig;
import gg.projecteden.nexus.models.interactioncommand.InteractionCommandConfig.InteractionCommand;
import gg.projecteden.nexus.models.interactioncommand.InteractionCommandConfigService;
import gg.projecteden.nexus.utils.MaterialTag;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

@NoArgsConstructor
@Permission(Group.MODERATOR)
@Aliases({"cmds", "cmdsign"})
public class InteractionCommandsCommand extends CustomCommand implements Listener {
	private final InteractionCommandConfigService service = new InteractionCommandConfigService();
	private final InteractionCommandConfig config = service.get0();
	private Location location;
	private InteractionCommand interactionCommand;

	public InteractionCommandsCommand(@NonNull CommandEvent event) {
		super(event);
		Block target = getTargetBlock();
		if (target != null) {
			location = target.getLocation();
			interactionCommand = config.get(target.getLocation());
		}
	}

	private void save() {
		service.save(config);
	}

	@Path("<index> <command...>")
	@Description("Set a command at the provided index")
	void set(int index, String command) {
		if (index < 1)
			error("Index cannot be less than 1");
		if (location == null)
			getTargetBlockRequired();

		if (interactionCommand == null) {
			interactionCommand = new InteractionCommand(location);
			config.add(interactionCommand);
		}
		interactionCommand.getCommands().put(index, command);
		save();
		send(PREFIX + "Set command at index &e" + index + " &3to &e" + command);
	}

	@Path("(delete|remove|clear) [index]")
	@Description("Delete the command at the provided index")
	void delete(Integer index) {
		if (interactionCommand == null || interactionCommand.getCommands().isEmpty())
			error("There are no commands present at that location");
		if (location == null)
			getTargetBlockRequired();

		if (index != null) {
			String command = interactionCommand.getCommands().get(index);
			if (command == null)
				error("There is no command present at that index");
			interactionCommand.getCommands().remove(index);
			save();
			send(PREFIX + "Deleted command &e" + command + " &3at index " + index);
		} else {
			config.delete(location);
			save();
			send(PREFIX + "Deleted &e" + interactionCommand.getCommands().size() + " &3commands at that location");
		}
	}

	@Path("read")
	@Description("View the commands configured on the target block")
	void read() {
		if (interactionCommand == null || interactionCommand.getCommands().isEmpty())
			error("There are no commands present at that location");
		line();
		send(PREFIX + "Commands:");
		interactionCommand.getCommands().forEach((index, command) ->
			send(json()
				.next("&e" + index + " &7" + command)
				.group()
				.next(" &3[&eEdit&3]")
				.hover("Shift click to edit")
				.insert("/cmds " + index + " " + command)
				.group()
			)
		);
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
		if (event.getAction() != Action.PHYSICAL && MaterialTag.PRESSURE_PLATES.isTagged(event.getClickedBlock().getType())) return;
		if (event.getAction() == Action.PHYSICAL && Vanish.isVanished(event.getPlayer())) return;

		InteractionCommand interactionCommand = new InteractionCommandConfigService().get0().get(event.getClickedBlock().getLocation());
		if (interactionCommand == null || interactionCommand.getCommands().isEmpty())
			return;

		if (!new CooldownService().check(event.getPlayer(), "interactioncommand", TickTime.TICK.x(5)))
			return;

		interactionCommand.run(event);
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if (new InteractionCommandConfigService().get0().delete(event.getBlock().getLocation()))
			send(event.getPlayer(), PREFIX + "Cleared");
	}
}

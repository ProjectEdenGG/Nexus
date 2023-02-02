package gg.projecteden.nexus.features.bigdoors;

import gg.projecteden.nexus.features.bigdoors.BigDoorManager.NamedBigDoor;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.bigdoor.BigDoorConfig;
import gg.projecteden.nexus.models.bigdoor.BigDoorConfigService;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;
import nl.pim16aap2.bigDoors.Door;

import java.util.List;
import java.util.stream.Collectors;

@Permission(Group.ADMIN)
public class BigDoorManagerCommand extends CustomCommand {
	private static final BigDoorConfigService configService = new BigDoorConfigService();
	private BigDoorConfig config;

	public BigDoorManagerCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("info <id>")
	void info(int doorId) {
		Door door = BigDoorManager.getDoor(doorId);
		if (door == null)
			error("Unknown BigDoor Id " + doorId);

		send("Owner: " + door.getPlayerName());
		send("UID: " + door.getDoorUID());
		send("Name: " + door.getName());
		send("Type: " + door.getType());
		send("Engine: " + StringUtils.getLocationString(door.getEngine()));
	}

	@Path("toggleAllDoors")
	void fix() {
		for (Door door : BigDoorManager.getDoors()) {
			if (door == null || door.getDoorUID() == 0)
				return;

			config = configService.get(door.getName());
			if (config.getDoorId() == 0)
				return;

			if (BigDoorManager.isDoorBusy(config))
				return;

			runCommandAsConsole("bigdoors:toggledoor " + config.getDoorId());
			send("Toggling door: " + config.getDoorId());
		}
	}

	@Path("toggleDoor <id>")
	void open(int doorId) {
		Door door = BigDoorManager.getDoor(doorId);
		if (door == null)
			error("Unknown BigDoor Id " + doorId);

		config = configService.get(door.getName());
		if (BigDoorManager.isDoorBusy(config))
			return;

		BigDoorManager.toggleDoor(door);
	}

	@Path("create <doorId>")
	void create(int doorId) {
		Door door = BigDoorManager.getDoor(doorId);
		if (door == null)
			error("Unknown BigDoor Id " + doorId);

		String doorName = door.getName();
		config = configService.get(doorName);

		config.setDoorId(doorId);

		configService.save(config);

		send("Created door \"" + doorName + "\" with id " + doorId);
	}

	@Path("delete <doorName>")
	void create(NamedBigDoor door) {
		config = configService.get(door.getName());
		configService.delete(config);
		send("Door \"" + door.getName() + "\" deleted from the database");
	}

	@Path("setToggleRegion <doorName> <regionId>")
	void setToggleRegion(NamedBigDoor door, String regionId) {
		config = configService.get(door.getName());

		config.setToggleRegion(regionId);

		configService.save(config);

		send("Set door \"" + door.getName() + "\" toggle region to " + regionId);
	}

//	@Path("setTimeAction <doorName> <quadrant> <action>")
//	void setTimeAction(NamedBigDoor door, TimeQuadrant quadrant, DoorAction action){
//		config = configService.get(door.getName());
//
//		config.getTimeState().put(quadrant, action);
//
//		configService.save(config);
//
//		send("Set door \"" + door.getName() + "\" time quadrant of " + quadrant + " to apply action " + action);
//	}

	@ConverterFor(NamedBigDoor.class)
	NamedBigDoor convertToNamedBigDoor(String value) {
		Door door = BigDoorManager.getDoor(value);
		if (door == null)
			error("BigDoor named &e" + value + " &cnot found");
		return new NamedBigDoor(door.getName());
	}

	@TabCompleterFor(NamedBigDoor.class)
	List<String> tabCompleteNamedBigDoor(String filter) {
		return BigDoorManager.getDoors().stream()
			.map(Door::getName)
			.filter(doorName -> doorName.toLowerCase().startsWith(filter.toLowerCase()))
			.collect(Collectors.toList());
	}
}

package gg.projecteden.nexus.features.bigdoormanager;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.bigdoor.BigDoorConfig;
import gg.projecteden.nexus.models.bigdoor.BigDoorConfigService;
import lombok.NonNull;

public class BigDoorManagerCommand extends CustomCommand {
	private static final BigDoorConfigService configService = new BigDoorConfigService();
	private BigDoorConfig config;

	public BigDoorManagerCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("create <doorName> <doorId>")
	void create(String doorName, int doorId) {
		config = configService.get(doorName);

		config.setDoorId(doorId);

		configService.save(config);

		send("Created door \"" + doorName + "\" with id " + doorId);
	}

	@Path("delete <doorName>")
	void create(String doorName) {
		config = configService.get(doorName);
		configService.delete(config);
		send("Door \"" + doorName + "\" deleted from the database");
	}

	@Path("setToggleRegion <doorName> <regionId>")
	void setToggleRegion(String doorName, String regionId) {
		config = configService.get(doorName);

		config.setToggleRegion(regionId);

		configService.save(config);

		send("Set door \"" + doorName + "\" toggle region to " + regionId);
	}

//	@Path("setTimeAction <doorName> <quadrant> <action>")
//	void setTimeAction(String doorName, TimeQuadrant quadrant, DoorAction action){
//		config = configService.get(doorName);
//
//		config.getTimeState().put(quadrant, action);
//
//		configService.save(config);
//
//		send("Set door \"" + doorName + "\" time quadrant of " + quadrant + " to apply action " + action);
//	}


}

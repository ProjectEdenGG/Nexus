package gg.projecteden.nexus.features.events.y2021.pugmas21;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;

public class Pugmas21Command extends CustomCommand {

	public Pugmas21Command(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("train armorStand <model>")
	@Permission("group.admin")
	void train(int model) {
		Train.armorStand(model, location());
	}

	@Path("train start")
	void train(
		@Arg(".3") @Switch double speed,
		@Arg("60") @Switch int seconds,
		@Arg("false") @Switch boolean keep,
		@Arg("4") @Switch double smokeBack,
		@Arg("5.3") @Switch double smokeUp
	) {
		Train.builder()
			.location(location())
			.direction(player().getFacing())
			.speed(speed)
			.seconds(seconds)
			.smokeBack(smokeBack)
			.smokeUp(smokeUp)
			.build()
			.start();
	}

	@Path("npcs interact <npc>")
	void npcs_interact(Pugmas21InteractableNPC npc) {
		npc.interact(player());
	}

}

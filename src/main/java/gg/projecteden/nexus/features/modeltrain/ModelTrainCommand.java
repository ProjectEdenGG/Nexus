package gg.projecteden.nexus.features.modeltrain;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;

@Permission(Group.ADMIN)
public class ModelTrainCommand extends CustomCommand {
	private static ModelTrain modelTrain;

	public ModelTrainCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("stop")
	@Description("Stop the model train")
	void stop() {
		modelTrain.stop();
	}

	@Path("spawn")
	@Description("Spawn a model train at your current location")
	void spawn() {
		modelTrain = new ModelTrain(location());
	}


}

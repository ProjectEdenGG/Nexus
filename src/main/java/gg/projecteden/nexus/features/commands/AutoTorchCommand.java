package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.autotorch.AutoTorchService;
import gg.projecteden.nexus.models.autotorch.AutoTorchUser;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
@Permission("nexus.autotorch")
@Description("")
public class AutoTorchCommand extends CustomCommand {
	public static final String PERMISSION = "nexus.autotorch";

	private final AutoTorchService service = new AutoTorchService();
	private AutoTorchUser autoTorch;

	public AutoTorchCommand(@NonNull CommandEvent event) {
		super(event);
		autoTorch = service.get(player());
	}

	@Path("<on|off>")
	@Description("Toggle automatically placing torches when it gets too dark")
	void toggle(Boolean state) {
		if (state == null)
			state = !autoTorch.isEnabled();

		autoTorch.setEnabled(state);
		service.save(autoTorch);
		send(PREFIX + (state ? "&aEnabled" : "&cDisabled"));
	}

	@Path("lightlevel [level]")
	@Description("Adjust the light level at which torches are placed")
	void lightlevel(@Arg(min = 0, max = 15) int level) {
		autoTorch.setLightLevel(level);
		service.save(autoTorch);
		send(PREFIX + "Torches will now be automatically placed at your feet at light level &e"+level+"&3 or lower");
	}

}

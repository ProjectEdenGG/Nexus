package me.pugabyte.nexus.features.commands;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.autotorch.AutoTorchService;
import me.pugabyte.nexus.models.autotorch.AutoTorchUser;

@NoArgsConstructor
@Permission("automaticinventory.autotorch")
public class AutoTorchCommand extends CustomCommand {
	public static final String PERMISSION = "nexus.autotorch";

	private final AutoTorchService service = new AutoTorchService();
	private AutoTorchUser autoTorch;

	public AutoTorchCommand(@NonNull CommandEvent event) {
		super(event);
		autoTorch = service.get(player());
	}

	@Path("<on|off>")
	void toggle(Boolean enable) {
		autoTorch.setEnabled(enable);
		service.save(autoTorch);
		send(PREFIX + (enable ? "&aEnabled" : "&cDisabled"));
	}

	@Path("lightlevel [level]")
	void lightlevel(@Arg(min = 0, max = 15) int level) {
		autoTorch.setLightLevel(level);
		service.save(autoTorch);
		send(PREFIX + "Torches will now be automatically placed at your feet at light level &e"+level+"&3 or lower");
	}

}

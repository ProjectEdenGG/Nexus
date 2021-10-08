package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import org.bukkit.potion.PotionEffectType;

@Aliases("nv")
@Permission("group.staff")
public class NightVisionCommand extends CustomCommand {
	private static final PotionEffectType effectType = PotionEffectType.NIGHT_VISION;

	public NightVisionCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void on() {
		player().addPotionEffect(new PotionEffectBuilder(effectType).maxDuration().build());
	}

	@Path("off")
	void off() {
		player().removePotionEffect(effectType);
	}
}

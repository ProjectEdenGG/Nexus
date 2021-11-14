package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import org.bukkit.potion.PotionEffectType;

@Aliases("nv")
@Permission("nexus.nightvision")
public class NightVisionCommand extends CustomCommand {
	private static final PotionEffectType EFFECT_TYPE = PotionEffectType.NIGHT_VISION;

	public NightVisionCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void on() {
		player().addPotionEffect(new PotionEffectBuilder(EFFECT_TYPE).maxDuration().build());
	}

	@Path("off")
	void off() {
		player().removePotionEffect(EFFECT_TYPE);
	}
}

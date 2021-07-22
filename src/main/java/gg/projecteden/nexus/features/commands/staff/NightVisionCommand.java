package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Aliases("nv")
@Permission("group.staff")
public class NightVisionCommand extends CustomCommand {

	public NightVisionCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void on() {
		player().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 9999999, 1, false, false));
	}

	@Path("off")
	void off() {
		player().removePotionEffect(PotionEffectType.NIGHT_VISION);
	}
}

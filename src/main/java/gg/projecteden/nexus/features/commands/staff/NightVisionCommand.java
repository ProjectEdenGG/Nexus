package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import org.bukkit.potion.PotionEffectType;

@Aliases("nv")
@Permission(Group.STAFF)
public class NightVisionCommand extends CustomCommand {
	private static final PotionEffectType EFFECT_TYPE = PotionEffectType.NIGHT_VISION;

	public NightVisionCommand(CommandEvent event) {
		super(event);
	}

	@Path("[state]")
	@Description("Toggle night vision")
	void on(Boolean state) {
		if (state == null)
			state = !player().hasPotionEffect(EFFECT_TYPE);

		if (state)
			player().addPotionEffect(new PotionEffectBuilder(EFFECT_TYPE).maxDuration().build());
		else
			player().removePotionEffect(EFFECT_TYPE);
	}

}

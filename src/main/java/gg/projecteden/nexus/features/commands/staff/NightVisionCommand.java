package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.NerdService;
import gg.projecteden.nexus.models.vanish.VanishUser;
import gg.projecteden.nexus.models.vanish.VanishUser.Setting;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.potion.PotionEffectType;

@Aliases("nv")
@Permission(Group.STAFF)
public class NightVisionCommand extends CustomCommand {
	private static final NerdService nerdService = new NerdService();
	private static final PotionEffectType EFFECT_TYPE = PotionEffectType.NIGHT_VISION;
	private static final PotionEffectBuilder NIGHT_VISION = new PotionEffectBuilder(EFFECT_TYPE).infinite();

	public NightVisionCommand(CommandEvent event) {
		super(event);
	}

	static {
		Tasks.repeat(0, TickTime.SECOND.x(2), () ->
			OnlinePlayers.where(player -> Nerd.of(player).isNightVision())
				.forEach(player -> player.addPotionEffect(NIGHT_VISION.build())));
	}

	@NoLiterals
	@Description("Toggle night vision")
	void on(@Optional Boolean state) {
		if (state == null)
			state = !player().hasPotionEffect(EFFECT_TYPE);

		if (state) {
			player().addPotionEffect(NIGHT_VISION.build());
			nerdService.edit(player(), nerd -> nerd.setNightVision(true));
		} else {
			VanishUser user = Vanish.get(player());
			if (!user.isVanished() || !user.getSetting(Setting.NIGHT_VISION))
				player().removePotionEffect(EFFECT_TYPE);

			nerdService.edit(player(), nerd -> nerd.setNightVision(false));
		}
	}

}

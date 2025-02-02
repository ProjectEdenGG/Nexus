package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.NerdService;
import gg.projecteden.nexus.models.vanish.VanishUser;
import gg.projecteden.nexus.models.vanish.VanishUser.Setting;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.potion.PotionEffectType;

@Aliases("nv")
@Permission(Group.STAFF)
@NoArgsConstructor
public class NightVisionCommand extends CustomCommand implements Listener {
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

	@Path("[state]")
	@Description("Toggle night vision")
	void on(Boolean state) {
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

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Tasks.wait(5, () -> {
			if (Nerd.of(event.getPlayer()).isNightVision()) {
				event.getPlayer().removePotionEffect(EFFECT_TYPE);
				Tasks.wait(1, () -> event.getPlayer().addPotionEffect(NIGHT_VISION.build()));
			}
		});

	}

}

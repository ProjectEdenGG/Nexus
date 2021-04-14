package me.pugabyte.nexus.features.particles;

import com.mewin.worldguardregionapi.events.RegionLeftEvent;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.particles.effects.WingsEffect;
import me.pugabyte.nexus.features.particles.menu.ParticleMenu;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.particle.ParticleOwner;
import me.pugabyte.nexus.models.particle.ParticleService;
import me.pugabyte.nexus.models.particle.ParticleSetting;
import me.pugabyte.nexus.models.particle.ParticleType;
import me.pugabyte.nexus.utils.ColorType;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.TimeUtils.Time;
import org.bukkit.Color;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import java.util.Map;

@NoArgsConstructor
@Permission("wings.use")
public class WingsCommand extends CustomCommand implements Listener {

	ParticleService service = new ParticleService();

	public WingsCommand(CommandEvent event) {
		super(event);
	}

	@Path()
	void openMenu() {
		ParticleMenu.openSettingEditor(player(), ParticleType.WINGS);
	}

	@Path("stop")
	void stop() {
		ParticleOwner owner = service.get(player());
		owner.cancelTasks(ParticleType.WINGS);
	}

	@Path("preview <effect>")
	void effect(WingsEffect.WingStyle wingStyle) {
		if (!player().hasPermission("wings.style." + (wingStyle.ordinal() + 1)))
			error("You do not have permission to use that wing style");
		ParticleOwner owner = service.get(player());
		owner.cancelTasks(ParticleType.WINGS);

		Map<ParticleSetting, Object> wingSettings = owner.getSettings(ParticleType.WINGS);
		WingsEffect.WingStyle cur_Style = (WingsEffect.WingStyle) wingSettings.get(ParticleSetting.WINGS_STYLE);
		Color cur_Color1 = (Color) wingSettings.get(ParticleSetting.WINGS_COLOR_ONE);
		Color cur_Color2 = (Color) wingSettings.get(ParticleSetting.WINGS_COLOR_TWO);
		Color cur_Color3 = (Color) wingSettings.get(ParticleSetting.WINGS_COLOR_THREE);
		Boolean cur_Rainbow1 = (Boolean) wingSettings.get(ParticleSetting.WINGS_RAINBOW_ONE);
		Boolean cur_Rainbow2 = (Boolean) wingSettings.get(ParticleSetting.WINGS_RAINBOW_TWO);
		Boolean cur_Rainbow3 = (Boolean) wingSettings.get(ParticleSetting.WINGS_RAINBOW_THREE);

		// Default Preview Settings
		wingSettings.put(ParticleSetting.WINGS_STYLE, wingStyle);
		wingSettings.put(ParticleSetting.WINGS_COLOR_ONE, Color.YELLOW);
		wingSettings.put(ParticleSetting.WINGS_COLOR_TWO, Color.BLACK);
		wingSettings.put(ParticleSetting.WINGS_COLOR_THREE, ColorType.CYAN.getColor());
		wingSettings.put(ParticleSetting.WINGS_RAINBOW_ONE, false);
		wingSettings.put(ParticleSetting.WINGS_RAINBOW_TWO, false);
		wingSettings.put(ParticleSetting.WINGS_RAINBOW_THREE, false);

		Tasks.wait(5, () -> ParticleType.WINGS.run(player()));
		Tasks.wait(Time.SECOND.x(15), () -> {
			owner.cancelTasks(ParticleType.WINGS);
			wingSettings.put(ParticleSetting.WINGS_STYLE, cur_Style);
			wingSettings.put(ParticleSetting.WINGS_COLOR_ONE, cur_Color1);
			wingSettings.put(ParticleSetting.WINGS_COLOR_TWO, cur_Color2);
			wingSettings.put(ParticleSetting.WINGS_COLOR_THREE, cur_Color3);
			wingSettings.put(ParticleSetting.WINGS_RAINBOW_ONE, cur_Rainbow1);
			wingSettings.put(ParticleSetting.WINGS_RAINBOW_TWO, cur_Rainbow2);
			wingSettings.put(ParticleSetting.WINGS_RAINBOW_THREE, cur_Rainbow3);
		});
	}

	@EventHandler
	public void onLeave(RegionLeftEvent event) {
		if (!event.getRegion().getId().equalsIgnoreCase("exchange")) return;
		ParticleOwner owner = new ParticleService().get(event.getPlayer());
		owner.cancelTasks();
	}

	@EventHandler
	public void onLeave(PlayerChangedWorldEvent event) {
		if (!event.getFrom().getName().equalsIgnoreCase("bearfair")) return;
		ParticleOwner owner = new ParticleService().get(event.getPlayer());
		owner.cancelTasks();
	}

}

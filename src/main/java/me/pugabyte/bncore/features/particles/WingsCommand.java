package me.pugabyte.bncore.features.particles;

import com.mewin.worldguardregionapi.events.RegionLeftEvent;
import lombok.NoArgsConstructor;
import me.pugabyte.bncore.features.particles.effects.WingsEffect;
import me.pugabyte.bncore.features.particles.menu.ParticleMenu;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.particle.ParticleOwner;
import me.pugabyte.bncore.models.particle.ParticleService;
import me.pugabyte.bncore.models.particle.ParticleSetting;
import me.pugabyte.bncore.models.particle.ParticleType;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

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
	void effect(WingsEffect.WingStyle style) {
		if (!player().hasPermission("wings.style." + (style.ordinal() + 1)))
			error("You do not have permission to use that wing style");
		ParticleOwner owner = service.get(player());
		owner.cancelTasks(ParticleType.WINGS);
		owner.getSettings(ParticleType.WINGS).put(ParticleSetting.WINGS_STYLE, style);
		service.save(owner);
		Tasks.wait(5, () -> ParticleType.WINGS.run(player()));
		Tasks.wait(Time.SECOND.x(15), () -> owner.cancelTasks(ParticleType.WINGS));
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

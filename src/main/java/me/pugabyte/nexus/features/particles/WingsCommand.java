package me.pugabyte.nexus.features.particles;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.particles.effects.WingsEffect.WingStyle;
import me.pugabyte.nexus.features.particles.providers.EffectSettingProvider;
import me.pugabyte.nexus.features.regionapi.events.player.PlayerLeftRegionEvent;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.particle.ParticleOwner;
import me.pugabyte.nexus.models.particle.ParticleService;
import me.pugabyte.nexus.models.particle.ParticleType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import static me.pugabyte.nexus.features.particles.WingsCommand.PERMISSION;

@NoArgsConstructor
@Permission(PERMISSION)
public class WingsCommand extends CustomCommand implements Listener {
	public static final String PERMISSION = "wings.use";

	ParticleService service = new ParticleService();

	public WingsCommand(CommandEvent event) {
		super(event);
	}

	@Path()
	void openMenu() {
		new EffectSettingProvider(ParticleType.WINGS).open(player());
	}

	@Path("stop")
	void stop() {
		ParticleOwner owner = service.get(player());
		owner.cancel(ParticleType.WINGS);
	}

	@Path("preview <style>")
	void effect(WingStyle style) {
		if (!style.canBeUsedBy(player()))
			error("You do not have permission to use that wing style");

		style.preview(player());
	}

	@EventHandler
	public void onLeave(PlayerLeftRegionEvent event) {
		if (!event.getRegion().getId().equalsIgnoreCase("exchange")) return;
		ParticleOwner owner = new ParticleService().get(event.getPlayer());
		owner.cancel();
	}

	@EventHandler
	public void onLeave(PlayerChangedWorldEvent event) {
		if (!event.getFrom().getName().equalsIgnoreCase("bearfair")) return;
		ParticleOwner owner = new ParticleService().get(event.getPlayer());
		owner.cancel();
	}

}

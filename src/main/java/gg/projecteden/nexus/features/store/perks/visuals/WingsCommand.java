package gg.projecteden.nexus.features.store.perks.visuals;

import gg.projecteden.nexus.features.particles.effects.WingsEffect.WingStyle;
import gg.projecteden.nexus.features.particles.providers.EffectSettingProvider;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerLeftRegionEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.WikiConfig;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.particle.ParticleOwner;
import gg.projecteden.nexus.models.particle.ParticleService;
import gg.projecteden.nexus.models.particle.ParticleType;
import lombok.NoArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import static gg.projecteden.nexus.features.store.perks.visuals.WingsCommand.PERMISSION;

@NoArgsConstructor
@Permission(PERMISSION)
@WikiConfig(rank = "Store", feature = "Visuals")
public class WingsCommand extends CustomCommand implements Listener {
	public static final String PERMISSION = "wings.use";

	ParticleService service = new ParticleService();

	public WingsCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Open the wings menu")
	void openMenu() {
		new EffectSettingProvider(ParticleType.WINGS).open(player());
	}

	@Path("stop")
	@Description("Turn off your wings")
	void stop() {
		ParticleOwner owner = service.get(player());
		owner.cancel(ParticleType.WINGS);
	}

	@Path("preview <style>")
	@Description("Preview a wing style")
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

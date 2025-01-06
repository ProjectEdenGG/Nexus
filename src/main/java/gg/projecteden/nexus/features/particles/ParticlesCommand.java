package gg.projecteden.nexus.features.particles;

import gg.projecteden.nexus.features.minigames.models.events.matches.MatchJoinEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchQuitEvent;
import gg.projecteden.nexus.features.particles.effects.DotEffect;
import gg.projecteden.nexus.features.particles.effects.LineEffect;
import gg.projecteden.nexus.features.particles.providers.ParticleMenuProvider;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.particle.ParticleOwner;
import gg.projecteden.nexus.models.particle.ParticleService;
import gg.projecteden.nexus.models.particle.ParticleType;
import gg.projecteden.nexus.utils.LocationUtils;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@NoArgsConstructor
public class ParticlesCommand extends CustomCommand implements Listener {
	ParticleService service = new ParticleService();
	ParticleOwner particleOwner;

	public ParticlesCommand(@NonNull CommandEvent event) {
		super(event);
		particleOwner = service.get(player());
	}

	@Path
	@Description("Open the particles menu")
	void menu() {
		new ParticleMenuProvider().open(player());
	}

	@Path("<effectType>")
	@Description("Start a particle effect")
	@Permission(Group.ADMIN)
	void run(ParticleType particleType) {
		particleOwner.start(particleType);
	}

	@Path("stop all")
	@Description("Stop all particle effects")
	void stop_all() {
		particleOwner.cancel();
	}

	@Path("stop <effectType>")
	@Description("Stop a particle effect")
	void stop(ParticleType particleType) {
		particleOwner.cancel(particleType);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Particles.startParticles(event.getPlayer());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Particles.stopParticles(event.getPlayer());
	}

	@EventHandler
	public void onMatchJoin(MatchJoinEvent event) {
		Particles.stopParticles(event.getMinigamer().getPlayer());
	}

	@EventHandler
	public void onMatchQuit(MatchQuitEvent event) {
		Particles.startParticles(event.getMinigamer().getPlayer());
	}

	@Path("line [distance] [density]")
	@Permission(Group.ADMIN)
	@Description("Draw a particle line")
	void line(@Arg("10") int distance, @Arg("0.1") double density) {
		LineEffect.builder().owner(new ParticleService().get(player())).entity(player()).distance(distance).density(density).rainbow(true).start();
	}

	@Path("dot")
	@Permission(Group.ADMIN)
	@Description("Draw a particle dot")
	void dot() {
		Location loc = LocationUtils.getCenteredLocation(location()).add(0, 1, 0);
		DotEffect.builder().player(player()).location(loc).ticks(10 * 20).rainbow(true).start();
	}
}

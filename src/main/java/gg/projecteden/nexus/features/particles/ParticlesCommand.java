package gg.projecteden.nexus.features.particles;

import gg.projecteden.nexus.features.minigames.models.events.matches.MatchJoinEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MinigamerQuitEvent;
import gg.projecteden.nexus.features.particles.effects.DotEffect;
import gg.projecteden.nexus.features.particles.effects.LineEffect;
import gg.projecteden.nexus.features.particles.providers.ParticleMenuProvider;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.particle.ParticleOwner;
import gg.projecteden.nexus.models.particle.ParticleService;
import gg.projecteden.nexus.models.particle.ParticleType;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static gg.projecteden.nexus.features.particles.Particles.startParticles;
import static gg.projecteden.nexus.features.particles.Particles.stopParticles;
import static gg.projecteden.nexus.utils.LocationUtils.getCenteredLocation;

@NoArgsConstructor
public class ParticlesCommand extends CustomCommand implements Listener {
	ParticleService service = new ParticleService();
	ParticleOwner particleOwner;

	public ParticlesCommand(@NonNull CommandEvent event) {
		super(event);
		particleOwner = service.get(player());
	}

	@Path
	void menu() {
		new ParticleMenuProvider().open(player());
	}

	@Path("<effectType>")
	@Permission(Group.ADMIN)
	void run(ParticleType particleType) {
		particleOwner.start(particleType);
	}

	@Path("stop <effectType>")
	void stop(ParticleType particleType) {
		particleOwner.cancel(particleType);
	}

	@Path("stopall")
	void stopAll() {
		particleOwner.cancel();
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		startParticles(event.getPlayer());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		stopParticles(event.getPlayer());
	}

	@EventHandler
	public void onMatchJoin(MatchJoinEvent event) {
		stopParticles(event.getMinigamer().getPlayer());
	}

	@EventHandler
	public void onMatchQuit(MinigamerQuitEvent event) {
		startParticles(event.getMinigamer().getPlayer());
	}

	@Path("line [distance] [density]")
	@Permission(Group.ADMIN)
	void line(@Arg("10") int distance, @Arg("0.1") double density) {
		LineEffect.builder().player(player()).distance(distance).density(density).rainbow(true).start();
	}

	@Path("dot")
	@Permission(Group.ADMIN)
	void dot() {
		Location loc = getCenteredLocation(location()).add(0, 1, 0);
		DotEffect.builder().player(player()).location(loc).ticks(10 * 20).rainbow(true).start();
	}
}

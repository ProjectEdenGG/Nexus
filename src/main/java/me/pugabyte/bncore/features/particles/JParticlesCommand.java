package me.pugabyte.bncore.features.particles;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchJoinEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchQuitEvent;
import me.pugabyte.bncore.features.particles.effects.DotEffect;
import me.pugabyte.bncore.features.particles.effects.LineEffect;
import me.pugabyte.bncore.features.particles.menu.ParticleMenu;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.models.particle.ParticleOwner;
import me.pugabyte.bncore.models.particle.ParticleService;
import me.pugabyte.bncore.models.particle.ParticleType;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

import static me.pugabyte.bncore.features.particles.Particles.startParticles;
import static me.pugabyte.bncore.features.particles.Particles.stopParticles;

@NoArgsConstructor
public class JParticlesCommand extends CustomCommand implements Listener {
	ParticleService service = new ParticleService();
	ParticleOwner particleOwner;

	public JParticlesCommand(@NonNull CommandEvent event) {
		super(event);
		particleOwner = service.get(player());
	}

	@Path()
	void menu() {
		ParticleMenu.openMain(player());
	}

	@Path("<effectType>")
	@Permission("group.admin")
	void run(ParticleType particleType) {
		particleType.run(player());
	}

	@Path("stop <effectType>")
	void stop(ParticleType particleType) {
		particleOwner.cancelTasks(particleType);
	}

	@Path("stopall")
	void stopAll() {
		particleOwner.cancelTasks();
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
	public void onMatchQuit(MatchQuitEvent event) {
		startParticles(event.getMinigamer().getPlayer());
	}

	@Path("line [distance] [density]")
	@Permission("group.admin")
	void line(@Arg("10") int distance, @Arg("0.1") double density) {
		LineEffect.builder().player(player()).distance(distance).density(density).rainbow(true).start();
	}

	@Path("dot")
	@Permission("group.admin")
	void dot() {
		Location loc = Utils.getCenteredLocation(player().getLocation()).add(0, 1, 0);
		DotEffect.builder().player(player()).location(loc).ticks(10 * 20).rainbow(true).start();
	}

	@ConverterFor(ParticleType.class)
	ParticleType convertToEffectType(String value) {
		try {
			return ParticleType.valueOf(value.toUpperCase());
		} catch (IllegalArgumentException ignore) {
			throw new InvalidInputException("EffectType from " + value + " not found");
		}
	}

	@TabCompleterFor(ParticleType.class)
	List<String> tabCompleteEffectType(String filter) {
		return tabCompleteEnum(ParticleType.class, filter);
	}
}

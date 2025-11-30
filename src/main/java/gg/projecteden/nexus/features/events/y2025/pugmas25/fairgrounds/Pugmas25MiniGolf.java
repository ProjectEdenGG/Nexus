package gg.projecteden.nexus.features.events.y2025.pugmas25.fairgrounds;

import com.destroystokyo.paper.ParticleBuilder;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.minigolf.MiniGolf;
import gg.projecteden.nexus.features.minigolf.MiniGolfUtils;
import gg.projecteden.nexus.features.minigolf.models.GolfBallParticle;
import gg.projecteden.nexus.features.minigolf.models.GolfBallStyle;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerLeftRegionEvent;
import gg.projecteden.nexus.models.minigolf.MiniGolfUserService;
import gg.projecteden.nexus.models.pugmas25.Pugmas25UserService;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static gg.projecteden.api.common.utils.Nullables.isNotNullOrEmpty;
import static gg.projecteden.nexus.utils.Nullables.isNotNullOrAir;

public class Pugmas25MiniGolf implements Listener {
	public static final Location MINIGOLF_ANIMATION_LOCATION = Pugmas25.get().location(-712, 67, -2883);

	public Pugmas25MiniGolf() {
		Nexus.registerListener(this);
		styleAndParticleDisplays();
	}

	private void styleAndParticleDisplays() {
		// Styles
		var styleItemEntity = Bukkit.getEntity(UUID.fromString("936a258c-4849-406a-8959-db398f778ded"));

		if (!(styleItemEntity instanceof Item styleItem))
			return;

		styleItem.setWillAge(false);
		styleItem.setUnlimitedLifetime(true);
		styleItem.setCanPlayerPickup(false);
		styleItem.setCanMobPickup(false);

		List<ItemStack> golfBalls = Arrays.stream(GolfBallStyle.values())
			.filter(Objects::nonNull)
			.map(style -> (MiniGolfUtils.GOLF_BALL.clone().model(style.getModel()).build()))
			.toList();

		if (isNotNullOrEmpty(golfBalls)) {
			AtomicInteger index = new AtomicInteger();
			Tasks.repeat(0, TickTime.SECOND.x(5), () -> {
				ItemStack golfBall = golfBalls.get(index.get());
				if (isNotNullOrAir(golfBall))
					styleItem.setItemStack(golfBall);

				index.getAndIncrement();
				if (index.get() >= golfBalls.size())
					index.set(0);
			});
		}

		// Particles
		Location particleLoc = new Location(Pugmas25.get().getWorld(), -710, 80, -2863).toCenterLocation();
		List<Particle> particles = Arrays.stream(GolfBallParticle.values())
			.map(GolfBallParticle::getParticle)
			.filter(Objects::nonNull)
			.toList();

		if (!Nullables.isNullOrEmpty(particles)) {
			AtomicInteger index = new AtomicInteger(0);
			Tasks.repeat(0, TickTime.SECOND.x(5), () -> {
				Particle particle = particles.get(index.get());
				if (particle != null) {
					ParticleBuilder particleBuilder = new ParticleBuilder(particle)
						.location(particleLoc)
						.count(50)
						.extra(0.01)
						.offset(0.2, 0.2, 0.2);

					if (particle.equals(Particle.DUST))
						particleBuilder.color(Color.RED);

					particleBuilder.spawn();
					Tasks.wait(TickTime.SECOND, particleBuilder::spawn);
					Tasks.wait(TickTime.SECOND.x(2), particleBuilder::spawn);
					Tasks.wait(TickTime.SECOND.x(3), particleBuilder::spawn);
					Tasks.wait(TickTime.SECOND.x(4), particleBuilder::spawn);
				}

				index.getAndIncrement();
				if (index.get() >= particles.size())
					index.set(0);
			});
		}
	}

	@EventHandler
	public void on(PlayerEnteredRegionEvent event) {
		var player = event.getPlayer();
		var regions = getMinigolfRegions(player);

		if (regions.isEmpty())
			return;

		if (!new Pugmas25UserService().get(player).isStartedMiniGolf())
			return;

		MiniGolf.join(new MiniGolfUserService().get(player));
	}

	@EventHandler
	public void on(PlayerLeftRegionEvent event) {
		var player = event.getPlayer();
		var regions = getMinigolfRegions(player);

		if (!regions.isEmpty())
			return;

		MiniGolf.quit(new MiniGolfUserService().get(player));
	}

	private static @NotNull Set<ProtectedRegion> getMinigolfRegions(Player player) {
		var worldguard = Pugmas25.get().worldguard();
		return worldguard.getRegionsLikeAt("pugmas25_minigolf_course.*", player.getLocation());
	}

}

package gg.projecteden.nexus.models.forcefield;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Transient;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.particles.effects.PolygonEffect;
import gg.projecteden.nexus.features.particles.effects.PolygonEffect.PolygonEffectBuilder;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.utils.Tasks;
import lombok.*;
import org.bukkit.Color;
import org.bukkit.Particle;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Entity(value = "force_field_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class})
public class ForceFieldUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	double radius = 3.0;
	boolean enabled;
	boolean showParticles = true;
	boolean movePlayers = true;
	boolean moveProjectiles;
	int particleTaskId = -1;
	Set<UUID> ignored = new HashSet<>();
	@Transient
	PolygonEffectBuilder effectBuilder;

	public boolean isEnabled() {
		return enabled && (movePlayers || moveProjectiles);
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;

		if (enabled) {
			setupParticles();

			this.particleTaskId = this.effectBuilder.start().getTaskId();
		} else {
			if (particleTaskId != -1) {
				Tasks.cancel(particleTaskId);
				this.particleTaskId = -1;
			}
		}
	}

	public void setRadius(double radius) {
		this.radius = radius;
		if (isActive()) {
			this.effectBuilder.radius(radius);
			refreshEffect();
		}
	}

	public boolean isActive() {
		return particleTaskId != -1;
	}

	public void setupParticles() {
		this.effectBuilder = PolygonEffect.builder()
			.polygon(PolygonEffect.Polygon.TRIANGLE)
			.updateLoc(true)
			.ticks(-1)
			.clientSide(true)
			.player(getOnlinePlayer())
			.radius(this.radius)
			.whole(false)
			.particle(Particle.DUST)
			.color(Color.RED)
			.rotateSpeed(0.7 * Math.round(radius));
	}

	public void refreshEffect() {
		Tasks.cancel(this.particleTaskId);
		if (this.effectBuilder == null)
			setupParticles();

		this.particleTaskId = this.effectBuilder.start().getTaskId();
	}
}

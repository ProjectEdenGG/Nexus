package gg.projecteden.nexus.models.forcefield;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Transient;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.particles.effects.PolygonEffect;
import gg.projecteden.nexus.features.particles.effects.PolygonEffect.PolygonEffectBuilder;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.nexus.utils.Tasks;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Color;
import org.bukkit.Particle;

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
	boolean moveEntities;
	boolean moveItems;
	boolean moveProjectiles;
	int particleTaskId = -1;
	@Transient
	PolygonEffectBuilder effectBuilder;

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;

		if (enabled) {
			this.effectBuilder = PolygonEffect.builder()
				.polygon(PolygonEffect.Polygon.TRIANGLE)
				.updateLoc(true)
				.ticks(-1)
				.clientSide(true)
				.player(getOnlinePlayer())
				.radius(this.radius)
				.whole(false)
				.particle(Particle.REDSTONE)
				.color(Color.RED)
				.rotateSpeed(0.7 * Math.round(radius));

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
		this.effectBuilder.radius(radius);
		refreshEffect();
	}

	public void refreshEffect() {
		Tasks.cancel(this.particleTaskId);
		this.particleTaskId = this.effectBuilder.start().getTaskId();
	}
}

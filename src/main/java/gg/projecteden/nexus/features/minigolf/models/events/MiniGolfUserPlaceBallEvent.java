package gg.projecteden.nexus.features.minigolf.models.events;

import gg.projecteden.nexus.models.minigolf.GolfBall;
import gg.projecteden.nexus.models.minigolf.MiniGolfUser;
import lombok.Getter;
import org.bukkit.Material;

import java.util.Set;

public class MiniGolfUserPlaceBallEvent extends MiniGolfUserEvent {
	@Getter
	private final Set<Material> allowedMaterials;
	@Getter
	private final GolfBall golfBall;

	public MiniGolfUserPlaceBallEvent(MiniGolfUser user, GolfBall golfBall, Set<Material> allowedMaterials) {
		super(user);
		this.golfBall = golfBall;
		this.allowedMaterials = allowedMaterials;
	}

	public boolean canPlaceBall(Material material) {
		return allowedMaterials.contains(material);
	}

	public void addAllowedMaterial(Material material) {
		allowedMaterials.add(material);
	}

	public void removeAllowedMaterial(Material material) {
		allowedMaterials.remove(material);
	}
}

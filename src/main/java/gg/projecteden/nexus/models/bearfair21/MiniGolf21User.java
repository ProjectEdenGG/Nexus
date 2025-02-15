package gg.projecteden.nexus.models.bearfair21;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.BearFair21MiniGolf;
import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.models.BearFair21MiniGolfColor;
import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.models.BearFair21MiniGolfHole;
import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.models.BearFair21MiniGolfParticle;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.utils.GlowUtils.GlowColor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Entity(value = "bearfair21_minigolf_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MiniGolf21User implements PlayerOwnedObject {
	@Id
	@NonNull
	@EqualsAndHashCode.Include
	private UUID uuid;
	private BearFair21MiniGolfColor miniGolfColor = BearFair21MiniGolfColor.WHITE;
	private BearFair21MiniGolfParticle miniGolfParticle = null;
	private boolean playing = false;
	private boolean rainbow = false;
	private boolean debug = false;
	//
	private Set<BearFair21MiniGolfHole> holeInOne = new HashSet<>();
	private Set<BearFair21MiniGolfHole> completed = new HashSet<>();
	private Map<BearFair21MiniGolfHole, Integer> score = new ConcurrentHashMap<>();
	private BearFair21MiniGolfHole currentHole = null;
	private int currentStrokes = 0;
	//
	private transient Snowball snowball = null;
	private transient Location ballLocation = null;

	public void incStrokes() {
		this.currentStrokes += 1;
	}

	public void removeBall() {
		if (snowball != null) {
			debug("removing ball from user");
			snowball.remove();
			snowball = null;
			ballLocation = null;
		}
	}

	public Color getColor() {
		return this.miniGolfColor.getColorType().getBukkitColor();
	}

	public List<Color> getFireworkColor() {
		if (miniGolfColor.equals(BearFair21MiniGolfColor.RAINBOW)) {
			List<Color> rainbow = new ArrayList<>();
			for (BearFair21MiniGolfColor color : BearFair21MiniGolfColor.values())
				rainbow.add(color.getColorType().getBukkitColor());
			return rainbow;
		}

		return Collections.singletonList(this.miniGolfColor.getColorType().getBukkitColor());
	}

	public ChatColor getChatColor() {
		return miniGolfColor.getColorType().getChatColor();
	}

	public GlowColor getGlowColor() {
		return this.miniGolfColor.getColorType().getGlowColor();
	}

	public void debug(boolean bool, String debug) {
		if (bool)
			debug(debug);
	}

	public void debug(String debug) {
		if (this.isDebug())
			sendMessage(debug);
	}

	public ItemStack getGolfBall() {
		return BearFair21MiniGolf.getGolfBall().model(miniGolfColor.getModel()).build();
	}

}

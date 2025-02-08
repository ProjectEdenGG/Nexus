package gg.projecteden.nexus.models.bearfair21;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.MiniGolf;
import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.models.MiniGolfColor;
import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.models.MiniGolfHole;
import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.models.MiniGolfParticle;
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
	private MiniGolfColor miniGolfColor = MiniGolfColor.WHITE;
	private MiniGolfParticle miniGolfParticle = null;
	private boolean playing = false;
	private boolean rainbow = false;
	private boolean debug = false;
	//
	private Set<MiniGolfHole> holeInOne = new HashSet<>();
	private Set<MiniGolfHole> completed = new HashSet<>();
	private Map<MiniGolfHole, Integer> score = new ConcurrentHashMap<>();
	private MiniGolfHole currentHole = null;
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
		if (miniGolfColor.equals(MiniGolfColor.RAINBOW)) {
			List<Color> rainbow = new ArrayList<>();
			for (MiniGolfColor color : MiniGolfColor.values())
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
		return MiniGolf.getGolfBall().model(miniGolfColor.getModel()).build();
	}

}

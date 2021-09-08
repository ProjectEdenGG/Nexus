package gg.projecteden.nexus.models.ambience;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.PostLoad;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.ambience.effects.birds.BirdSound;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.nexus.models.ambience.AmbienceConfig.Ambience.AmbienceType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static gg.projecteden.nexus.utils.ItemUtils.isNullOrAir;
import static gg.projecteden.nexus.utils.RandomUtils.randomInt;
import static gg.projecteden.nexus.utils.StringUtils.getShortLocationString;
import static gg.projecteden.utils.StringUtils.camelCase;

@Data
@Entity(value = "ambience_config", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class AmbienceConfig implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;

	private List<Ambience> ambiences = new ArrayList<>();
	private transient Map<Location, Ambience> ambienceMap = new HashMap<>();

	@PostLoad
	void postLoad() {
		for (Ambience ambience : ambiences)
			ambienceMap.put(ambience.getLocation(), ambience);
	}

	public List<Ambience> get(AmbienceType type) {
		return ambiences.stream().filter(ambience -> ambience.getType() == type).toList();
	}

	public Ambience get(Location location) {
		return ambienceMap.get(location.toBlockLocation());
	}

	public void add(Ambience ambience) {
		this.ambiences.add(ambience);
		ambienceMap.put(ambience.getLocation(), ambience);
	}

	public boolean delete(Location location) {
		Ambience ambience = get(location);
		if (ambience == null)
			return false;

		return delete(ambience);
	}

	public boolean delete(Ambience ambience) {
		if (!this.ambiences.contains(ambience))
			return false;

		this.ambiences.remove(ambience);
		ambienceMap.remove(ambience.getLocation());
		return true;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Ambience {
		private Location location;
		private AmbienceType type;

		public boolean validate() {
			if (!type.getType().validate(this)) {
				Nexus.warn("[Ambience] " + camelCase(type) + " at " + getShortLocationString(location) + " invalid, removing");
				config().delete(this);
				return false;
			}

			return true;
		}

		static AmbienceConfig config() {
			return new AmbienceConfigService().get0();
		}

		public void play() {
			type.play(location);
		}

		@Getter
		@AllArgsConstructor
		public enum AmbienceType {
			METAL_WINDCHIMES(AmbienceLocationType.ITEM_FRAME, Material.AMETHYST_SHARD, Set.of(1, 2, 3)) {
				@Override
				void play(Location location) {
					new SoundBuilder("minecraft:custom.ambient.windchimes.metal_" + randomInt(1, 5))
						.category(SoundCategory.AMBIENT)
						.location(location)
						.volume(1.5)
						.pitch(RandomUtils.randomDouble(0.1, 2.0))
						.play();
				}
			},
			BIRDHOUSE(AmbienceLocationType.ITEM_FRAME, Material.OAK_WOOD, Set.of(1, 2, 3, 4, 5, 6, 7, 8, 9)) {
				@Override
				void play(Location location) {
					Tasks.wait(TickTime.SECOND.x(randomInt(0, 45)), () -> BirdSound.randomBirdhouse().play(location));
				}
			},
			;

			private final AmbienceLocationType type;
			private final Material material;
			private final Set<Integer> customModelDatas;

			abstract void play(Location location);

			public boolean equals(ItemStack itemStack) {
				if (itemStack.getType() != material)
					return false;
				if (!customModelDatas.contains(new ItemBuilder(itemStack).customModelData()))
					return false;

				return true;
			}

			private enum AmbienceLocationType {
				ITEM_FRAME {
					public boolean validate(Ambience ambience) {
						final Location location = ambience.getLocation();
						for (ItemFrame itemFrame : location.getNearbyEntitiesByType(ItemFrame.class, 1, 1, 1)) {
							if (!itemFrame.getLocation().toBlockLocation().equals(location.toBlockLocation()))
								continue;

							if (isNullOrAir(itemFrame.getItem()))
								continue;

							if (!ambience.getType().equals(itemFrame.getItem()))
								continue;

							return true;
						}

						return false;
					}
				},
				BLOCK {
					public boolean validate(Ambience ambience) {
						return ambience.getLocation().getBlock().getType() == ambience.getType().getMaterial();
					}
				},
				;

				abstract public boolean validate(Ambience ambience);
			}
		}

	}

}

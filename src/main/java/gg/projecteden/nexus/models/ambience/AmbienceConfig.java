package gg.projecteden.nexus.models.ambience;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.PostLoad;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.ambience.effects.birds.BirdSound;
import gg.projecteden.nexus.features.clientside.models.ClientSideItemFrame;
import gg.projecteden.nexus.features.clientside.models.IClientSideEntity.ClientSideEntityType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.craftable.BirdHouse;
import gg.projecteden.nexus.features.resourcepack.decoration.types.craftable.WindChime;
import gg.projecteden.nexus.features.resourcepack.models.CustomSound;
import gg.projecteden.nexus.features.survival.Survival;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.ambience.AmbienceConfig.Ambience.AmbienceType;
import gg.projecteden.nexus.models.clientside.ClientSideConfig;
import gg.projecteden.nexus.utils.ItemBuilder.Model;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.parchment.HasLocation;
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
	public static class Ambience implements HasLocation {
		private Location location;
		private AmbienceType type;

		public boolean validate() {
			if (!type.getType().validate(this)) {
				Nexus.warn("[Ambience] " + StringUtils.camelCase(type) + " at " + StringUtils.xyzw(location) + " invalid, removing");
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
			METAL_WINDCHIMES(AmbienceLocationType.ITEM_FRAME, Material.PAPER, WindChime.ids()) {
				@Override
				public void play(Location location) {
					new SoundBuilder(CustomSound.AMBIENT_WINDCHIMES_METAL)
						.category(SoundCategory.AMBIENT)
						.location(location)
						.volume(1.5)
						.pitch(RandomUtils.randomDouble(0.1, 2.0))
						.singleton("windchimes")
						.play();
				}
			},
			BIRDHOUSE(AmbienceLocationType.ITEM_FRAME, Material.PAPER, BirdHouse.ids()) {
				@Override
				public void play(Location location) {
					Tasks.wait(TickTime.SECOND.x(RandomUtils.randomInt(0, 45)), () -> BirdSound.randomBirdhouse().play(location));
				}
			},
			;

			private final AmbienceLocationType type;
			private final Material material;
			private final Set<String> models;

			abstract public void play(Location location);

			public boolean matches(ItemStack itemStack) {
				if (itemStack.getType() != material)
					return false;

				if (!models.contains(Model.of(itemStack)))
					return false;

				return true;
			}

			private enum AmbienceLocationType {
				ITEM_FRAME {
					public boolean validate(Ambience ambience) {
						final Location location = ambience.getLocation();
						for (ItemFrame itemFrame : location.getNearbyEntitiesByType(ItemFrame.class, 1, 1, 1)) {
							if (isValid(ambience, location, itemFrame.getItem(), itemFrame.getLocation()))
								return true;
						}

						if (Survival.isAtSpawn(location)) {
							for (var entity : ClientSideConfig.getEntities(location)) {
								if (entity.getType() == ClientSideEntityType.ITEM_FRAME) {
									ClientSideItemFrame clientSideItemFrame = (ClientSideItemFrame) entity;
									ItemStack itemStack = clientSideItemFrame.content();
									if (isValid(ambience, location, itemStack, clientSideItemFrame.location()))
										return true;
								}
							}
						}

						return false;
					}

					private boolean isValid(Ambience ambience, Location location, ItemStack frameItem, Location frameLocation) {
						if (!frameLocation.toBlockLocation().equals(location.toBlockLocation()))
							return false;

						if (Nullables.isNullOrAir(frameItem))
							return false;

						if (!ambience.getType().matches(frameItem))
							return false;

						return true;
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

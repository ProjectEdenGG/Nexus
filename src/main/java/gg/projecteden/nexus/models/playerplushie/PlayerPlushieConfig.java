package gg.projecteden.nexus.models.playerplushie;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.PostLoad;
import gg.projecteden.api.common.utils.Utils;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.features.resourcepack.models.Saturn;
import gg.projecteden.nexus.features.resourcepack.playerplushies.Pose;
import gg.projecteden.nexus.features.resourcepack.playerplushies.Pose.Animated;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.resourcepack.LocalResourcePackUser;
import gg.projecteden.nexus.models.resourcepack.LocalResourcePackUserService;
import gg.projecteden.nexus.models.skincache.SkinCache;
import gg.projecteden.nexus.utils.ImageUtils;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import kotlin.Pair;
import lombok.*;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Data
@Entity(value = "player_plushie_config", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class PlayerPlushieConfig implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;

	// LinkedHashSet does not preserve order in database (deterministically out of order??)
	private List<UUID> owners = new ArrayList<>();
	public static final Map<Integer, Pair<Pose, UUID>> ALL_MODELS = new ConcurrentHashMap<>();

	@PostLoad
	void addDefaultOwners() {
		DEFAULT_OWNERS.forEach(admin -> {
			if (!owners.contains(admin.getUuid()))
				owners.add(admin.getUuid());
		});
	}

	private static List<Dev> DEFAULT_OWNERS = List.of(
		Dev.GRIFFIN,
		Dev.WAKKA,
		Dev.BLAST,
		Dev.LEXI,
		Dev.ARBY,
		Dev.FILID,
		Dev.KODA,
		Dev.POWER
	);

	public void addOwner(UUID uuid) {
		if (owners.contains(uuid))
			return;

		owners.add(uuid);
		Tasks.async(() -> {
			Saturn.deploy(true, false);

			final String instructions;
			final LocalResourcePackUser rpUser = new LocalResourcePackUserService().get(uuid);
			if (rpUser.hasTitan())
				instructions = "in your &eOptions menu ";
			else if (rpUser.isEnabled())
				instructions = ""; // no help for u
			else
				instructions = "by &erelogging &3or running &c/rp &etwice ";

			Nerd.of(uuid).sendMessage("%sPlease update your texture pack %s&3in order to see your Player Plushies".formatted(StringUtils.getPrefix("PlayerPlushies"), instructions));
			ResourcePack.read(); // reload resource pack cache
		});
	}

	public boolean isOwner(HasUniqueId player) {
		return isOwner(player.getUniqueId());
	}

	public boolean isOwner(UUID uuid) {
		return owners.contains(uuid);
	}

	public static PlayerPlushieConfig get() {
		return new PlayerPlushieConfigService().get0();
	}

	public Set<UUID> getOwners() {
		return new LinkedHashSet<>(owners);
	}

	public static int random() {
		if (ALL_MODELS.isEmpty())
			generate();

		AtomicInteger modelId = new AtomicInteger(1);
		Utils.attempt(100, () -> {
			modelId.set(RandomUtils.randomElement(ALL_MODELS.keySet()));
			return ALL_MODELS.get(modelId.get()).getFirst() != null;
		});

		return modelId.get();
	}

	public static final Material MATERIAL = Material.LAPIS_LAZULI;
	private static final String SUBDIRECTORY = "projecteden/decoration/plushies/player";
	private static final String TEXTURES_SUBDIRECTORY = SUBDIRECTORY + "/players";
	private static final String MODELS_DIRECTORY = "assets/minecraft/models/" + SUBDIRECTORY;
	private static final String TEXTURES_DIRECTORY = "assets/minecraft/textures/" + TEXTURES_SUBDIRECTORY;

	private static final Map<String, Object> GLOBAL_VARIABLES = Map.of(
		"SUBDIRECTORY", SUBDIRECTORY,
		"TEXTURES_SUBDIRECTORY", TEXTURES_SUBDIRECTORY
	);

	private static String process(String templateString, Map<String, Object> variables) {
		final AtomicReference<String> template = new AtomicReference<>(templateString);

		BiConsumer<String, Object> consumer = (id, value) ->
			template.set(template.get().replaceAll("<" + id + ">", "" + value));

		variables.forEach(consumer);
		GLOBAL_VARIABLES.forEach(consumer);

		return template.get();
	}

	public static final String ITEM_TEMPLATE = """
		{
			"parent": "<SUBDIRECTORY>/<POSE>/template_<SKIN_TYPE>",
			"textures": {
				"0": "<TEXTURES_SUBDIRECTORY>/<UUID>"
			}
		}
	""";

	public static final String ANIMATED_ITEM_TEMPLATE = """
		{
			"parent": "<SUBDIRECTORY>/<POSE>/template_<SKIN_TYPE>",
			"textures": {
				"0": "<TEXTURES_SUBDIRECTORY>/<UUID>",
				"1": "<TEXTURES_SUBDIRECTORY>/<POSE>/<UUID>"
			}
		}
	""";

	public static final String ANIMATION_TEMPLATE = """
		{
			"animation": {
				"frametime": <FRAME_TIME>,
				"frames": <FRAMES>
			}
		}
		""";

	public static final String MATERIAL_TEMPLATE = """
		{
			"parent": "minecraft:item/generated",
			"textures": {
				"layer0": "minecraft:item/%s"
			},
			"overrides": [
				<OVERRIDES>
			]
		}
	""".formatted(MATERIAL.name().toLowerCase());

	public static final String PREDICATE_TEMPLATE = """
		{"predicate": {"custom_model_data": <ID>}, "model": "<SUBDIRECTORY>/<POSE>/<UUID>"}
	""";

	public static final String MISSING_TEXTURE_PREDICATE_TEMPLATE = """
		{"predicate": {"custom_model_data": <ID>}, "model": "minecraft:item/barrier"}
	""";

	@SneakyThrows
	public static Map<String, Object> generate() {
		ALL_MODELS.clear();
		return new HashMap<>() {{
			final var users = PlayerPlushieConfig.get().getOwners();

			for (Pose pose : Pose.values()) {
				int index = pose.getStartingIndex();
				for (UUID uuid : users) {
					try {
						++index;

						final String poseName = pose.name().toLowerCase();

						put(TEXTURES_DIRECTORY + "/%s.png".formatted(uuid), SkinCache.of(uuid).retrieveImage());
						put(MODELS_DIRECTORY + "/%s/%s.json".formatted(poseName, uuid), process(pose.isAnimated() ? ANIMATED_ITEM_TEMPLATE : ITEM_TEMPLATE, Map.of(
							"POSE", poseName,
							"SKIN_TYPE", SkinCache.of(uuid).getModel().name().toLowerCase(),
							"UUID", uuid)
						));

						if (pose.isAnimated()) {
							final Animated config = pose.getAnimationConfig();
							final String animatedTextureFile = "%s/%s/%s.png".formatted(TEXTURES_DIRECTORY, poseName, uuid);
							put(animatedTextureFile, generateAnimationTexture(uuid, config.frameCount()));
							put(animatedTextureFile + ".mcmeta", process(ANIMATION_TEMPLATE, Map.of(
								"FRAME_TIME", config.frameTime(),
								"FRAMES", Arrays.stream(config.frames()).mapToObj(String::valueOf).toList())
							));
						}

						ALL_MODELS.put(index, new Pair<>(pose.canBeGeneratedFor(uuid) ? pose : null, uuid));
					} catch (Exception ex) {
						ex.printStackTrace();
					}

				}
			}

			final String overrides = process(MATERIAL_TEMPLATE, Map.of("OVERRIDES", Utils.sortByKey(ALL_MODELS).entrySet().stream()
				.map(entry -> {
					if (entry.getValue().getFirst() == null)
						return process(MISSING_TEXTURE_PREDICATE_TEMPLATE, Map.of("ID", entry.getKey()));

					return process(PREDICATE_TEMPLATE, Map.of(
						"ID", entry.getKey(),
						"POSE", entry.getValue().getFirst().name().toLowerCase(),
						"UUID", entry.getValue().getSecond().toString()
					));
				})
				.collect(Collectors.joining(","))));

			put("%s/%s.json".formatted(CustomModel.getVanillaSubdirectory(), MATERIAL.name().toLowerCase()), overrides);
		}};
	}

	@NotNull
	private static BufferedImage generateAnimationTexture(UUID uuid, int frames) {
		final BufferedImage skin = SkinCache.of(uuid).retrieveImage();
		final BufferedImage texture = ImageUtils.newImage(frames * 64, frames * frames * 64);
		final Graphics graphics = texture.getGraphics();
		for (int frame = 0; frame < frames; frame++)
			graphics.drawImage(skin, frame * 64, frame * ((frames + 1) * 64), null);
		graphics.dispose();
		return texture;
	}

}

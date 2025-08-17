package gg.projecteden.nexus.models.playerplushie;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.PostLoad;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.PlayerPlushie;
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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

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

	private List<UUID> owners = new ArrayList<>();

	public static final Map<Pose, List<UUID>> GENERATED = new ConcurrentHashMap<>();

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
		Dev.POWER,
		Dev.BRI
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
				instructions = ""; // no help for u (local = true)
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

	public static PlayerPlushie random() {
		return random(RandomUtils.randomElement(GENERATED.keySet()));
	}

	public static PlayerPlushie random(Pose pose) {
		var uuid = RandomUtils.randomElement(GENERATED.get(pose));
		return pose.asDecoration(uuid);
	}

	public static final Material MATERIAL = Material.LAPIS_LAZULI;
	private static final String ITEMS_DIRECTORY = "assets/minecraft/items/decoration/plushies/player";
	private static final String MODELS_DIRECTORY = "assets/minecraft/models/projecteden/decoration/plushies/player";
	private static final String TEXTURES_DIRECTORY = "assets/minecraft/textures/projecteden/decoration/plushies/player/players";

	private static final Map<String, Object> GLOBAL_VARIABLES = Map.of(
		"SUBDIRECTORY", "projecteden/decoration/plushies/player",
		"TEXTURES_SUBDIRECTORY", "projecteden/decoration/plushies/player/players"
	);

	private static String process(String templateString, Map<String, Object> variables) {
		final AtomicReference<String> template = new AtomicReference<>(templateString);

		BiConsumer<String, Object> consumer = (id, value) ->
			template.set(template.get().replaceAll("<" + id + ">", "" + value));

		variables.forEach(consumer);
		GLOBAL_VARIABLES.forEach(consumer);

		return template.get();
	}

	public static final String MODEL_TEMPLATE = """
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

	public static final String ANIMATION_META_TEMPLATE = """
	{
		"animation": {
			"frametime": <FRAME_TIME>,
			"frames": <FRAMES>
		}
	}
	""";

	public static final String ITEM_TEMPLATE = """
	{
		"model": {
			"type": "minecraft:model",
			"model": "<SUBDIRECTORY>/<POSE>/<UUID>",
			"tints": []
		},
		"oversized_in_gui": true
	}
	""";

	@SneakyThrows
	public static Map<String, Object> generate() {
		return new HashMap<>() {{
			final var users = PlayerPlushieConfig.get().getOwners();

			for (UUID uuid : users) {
				put(TEXTURES_DIRECTORY + "/%s.png".formatted(uuid), SkinCache.of(uuid).retrieveImage());

				for (Pose pose : Pose.values()) {
					try {
						if (!pose.canBeGeneratedFor(uuid))
							continue;

						GENERATED.computeIfAbsent(pose, $ -> new ArrayList<>()).add(uuid);

						var poseName = pose.name().toLowerCase();
						var itemTemplate = pose.isAnimated() ? ANIMATED_ITEM_TEMPLATE : MODEL_TEMPLATE;

						put(MODELS_DIRECTORY + "/%s/%s.json".formatted(poseName, uuid), process(itemTemplate, Map.of(
							"POSE", poseName,
							"SKIN_TYPE", SkinCache.of(uuid).getModel().name().toLowerCase(),
							"UUID", uuid)
						));

						put(ITEMS_DIRECTORY + "/%s/%s.json".formatted(poseName, uuid), process(ITEM_TEMPLATE, Map.of(
							"POSE", poseName,
							"UUID", uuid
						)));

						if (pose.isAnimated()) {
							final Animated config = pose.getAnimationConfig();
							final String animatedTextureFile = "%s/%s/%s.png".formatted(TEXTURES_DIRECTORY, poseName, uuid);
							put(animatedTextureFile, generateAnimationTexture(uuid, config.frameCount()));
							put(animatedTextureFile + ".mcmeta", process(ANIMATION_META_TEMPLATE, Map.of(
								"FRAME_TIME", config.frameTime(),
								"FRAMES", Arrays.stream(config.frames()).mapToObj(String::valueOf).toList())
							));
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
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

package gg.projecteden.nexus.features.resourcepack;

import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.decoration.Decorations;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.features.resourcepack.models.events.ResourcePackUpdateCompleteEvent;
import gg.projecteden.nexus.features.resourcepack.models.events.ResourcePackUpdateStartEvent;
import gg.projecteden.nexus.features.resourcepack.models.files.CustomModelFolder;
import gg.projecteden.nexus.features.resourcepack.models.files.FontFile;
import gg.projecteden.nexus.features.resourcepack.models.files.ResourcePackOverriddenMaterial;
import gg.projecteden.nexus.features.resourcepack.models.files.SoundsFile;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.resourcepack.LocalResourcePackUser;
import gg.projecteden.nexus.models.resourcepack.LocalResourcePackUserService;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.HttpUtils;
import gg.projecteden.nexus.utils.IOUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.Model;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.parchment.OptionalPlayerLike;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor
public class ResourcePack extends Feature implements Listener {
	public static final String ENV_SUFFIX = Nexus.getEnv() == Env.PROD ? "" : "-" + Nexus.getEnv();
	public static final String FILE_NAME = "ResourcePack%s.zip".formatted(ENV_SUFFIX);
	public static final String URL = "https://cdn.%s/%s".formatted(Nexus.DOMAIN, FILE_NAME);
	@Getter
	static final String fileRegex = "[\\w-]+";
	@Getter
	public static String hash = Utils.createSha1(URL);
	@Getter
	public static File file = IOUtils.getPluginFile(FILE_NAME);
	@Getter
	static final URI fileUri = URI.create("jar:" + file.toURI());
	@Getter
	private static FileSystem zipFile;

	@Getter
	private static List<ResourcePackOverriddenMaterial> modelGroups;
	@Getter
	private static List<CustomModelFolder> folders;
	@Getter
	private static ConcurrentHashMap<String, CustomModel> models;
	@Getter
	private static CustomModelFolder rootFolder;
	@Getter
	private static SoundsFile soundsFile;
	@Getter
	private static FontFile fontFile;

	@Getter
	private static boolean reloading;

	@Override
	public void onStart() {
		new ResourcePackListener();
		new Decorations();
		read();
	}

	@Override
	@SneakyThrows
	public void onStop() {
		closeZip();
		Bukkit.getMessenger().unregisterIncomingPluginChannel(Nexus.getInstance());
		Bukkit.getMessenger().unregisterOutgoingPluginChannel(Nexus.getInstance());

		if (modelGroups != null)
			modelGroups.clear();
		if (folders != null)
			folders.clear();
		if (models != null)
			models.clear();
		rootFolder = null;

		Decorations.onStop();
	}

	public static void read() {
		Tasks.async(() -> {
			try {
				new ResourcePackUpdateStartEvent().callEvent();
				reloading = true;

				HttpUtils.saveFile(URL, FILE_NAME);
				openZip();

				setup();
				readAllFiles();
				CustomModelMenu.load();

				reloading = false;
				new ResourcePackUpdateCompleteEvent().callEvent();
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				closeZip();
			}
		});
	}

	private static void setup() {
		modelGroups = new ArrayList<>();
		folders = new ArrayList<>();
		models = new ConcurrentHashMap<>();
		rootFolder = new CustomModelFolder("/");
	}

	static void readAllFiles() {
		try {
			try {
				soundsFile = SoundsFile.of(zipFile.getPath(SoundsFile.getPath()));
			} catch (Exception ex) {
				Nexus.warn("Failed to load resource pack sounds file");
				ex.printStackTrace();
			}

			try {
				fontFile = FontFile.of(zipFile.getPath(FontFile.getPath()));
			} catch (Exception ex) {
				Nexus.warn("Failed to load resource pack font file");
				ex.printStackTrace();
			}

			for (Path root : zipFile.getRootDirectories()) {
				try (var walker = Files.walk(root)) {
					walker.forEach(path -> {
						try {
							final String uri = path.toUri().toString();

							try {
								if (uri.contains(CustomModel.getVanillaSubdirectory()))
									ResourcePackOverriddenMaterial.addCustomModelMaterial(path);
							} catch (Exception ex) {
								ex.printStackTrace();
							}

							if (soundsFile != null) {
								try {
									if (uri.contains(".ogg"))
										SoundsFile.addAudioFile(path);
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							}

						} catch (Exception ex) {
							ex.printStackTrace();
						}
					});
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@SneakyThrows
	public static void openZip() {
		try {
			FileSystem existing = FileSystems.getFileSystem(fileUri);
			if (existing != null && existing.isOpen())
				existing.close();
		} catch (FileSystemNotFoundException ignore) {}

		zipFile = FileSystems.newFileSystem(fileUri, Collections.emptyMap());
	}

	@SneakyThrows
	public static void closeZip() {
		if (zipFile != null && zipFile.isOpen())
			zipFile.close();
	}

	public static boolean isCustomItem(@Nullable ItemStack item) {
		return item != null && !MaterialTag.ALL_AIR.isTagged(item.getType()) && Model.of(item) != null;
	}

	public static boolean isEnabledFor(Player player) {
		return player.getResourcePackStatus() == Status.SUCCESSFULLY_LOADED || new LocalResourcePackUserService().get(player).isEnabled();
	}

	public static boolean isEnabledFor(OptionalPlayerLike player) {
		if (!player.isOnline() || player.getPlayer() == null)
			return false;

		return isEnabledFor(player.getPlayer());
	}

	private static final String LINE = "&8&m                                                      ";

	private static final TextComponent TEXT = new JsonBuilder()
		.next(LINE)
		.newline().next("&#f5a138Hey nerd!")
		.newline()
		.newline().next("&7To begin your Project Eden journey, you must")
		.newline().next("&7first accept our server's resource pack!")
		.newline()
		.newline().next("&7The pack adds custom items and images to")
		.newline().next("&7enhance your experience on the server.")
		.newline()
		.newline().next("&#3080ffAre you ready?")
		.newline().next(LINE)
		.build();

	public static void send(Player player) {
		final LocalResourcePackUser packUser = new LocalResourcePackUserService().get(player);
		if (packUser.hasTitan())
			return;

		player.setResourcePack(URL, hash, !packUser.isEnabled(), TEXT);
	}

	@Data
	@Accessors(fluent = true)
	public static class ResourcePackNumber {
		private Player player;
		private int number;
		private Color color;

		public ResourcePackNumber(int number) {
			this.number = number;
		}

		public static ResourcePackNumber of(int number) {
			return new ResourcePackNumber(number);
		}

		public static ResourcePackNumber of(int number, Color color) {
			return new ResourcePackNumber(number).color(color);
		}

		public static ResourcePackNumber of(int number, ColorType color) {
			return new ResourcePackNumber(number).color(color);
		}

		public ResourcePackNumber color(Color color) {
			this.color = color;
			return this;
		}

		public ResourcePackNumber color(ColorType colorType) {
			this.color = colorType.getBukkitColor();
			return this;
		}

		public ResourcePackNumber color(String hex) {
			this.color = ColorType.hexToBukkit(hex);
			return this;
		}

		private static final CustomMaterial BASE_MODEL = CustomMaterial.GUI_NUMBER;

		public ItemBuilder get() {
			return new ItemBuilder(BASE_MODEL)
				.customModelData(number)
				.dyeColor(color)
				.itemFlags(ItemFlag.HIDE_ATTRIBUTES);
		}
	}

	@Getter
	@AllArgsConstructor
	public enum RainbowBlockOrder {
		RED(ColorType.RED),
		ORANGE(ColorType.ORANGE),
		YELLOW(ColorType.YELLOW),
		LIME(ColorType.LIGHT_GREEN),
		GREEN(ColorType.GREEN),
		CYAN(ColorType.CYAN),
		LIGHT_BLUE(ColorType.LIGHT_BLUE),
		BLUE(ColorType.BLUE),
		PURPLE(ColorType.PURPLE),
		MAGENTA(ColorType.MAGENTA),
		PINK(ColorType.PINK),
		BROWN(ColorType.BROWN),
		BLACK(ColorType.BLACK),
		GRAY(ColorType.GRAY),
		LIGHT_GRAY(ColorType.LIGHT_GRAY),
		WHITE(ColorType.WHITE),
		;

		private final ColorType colorType;

		public static @NotNull RainbowBlockOrder of(ColorType colorType) {
			for (RainbowBlockOrder color : values())
				if (color.getColorType() == colorType)
					return color;

			throw new InvalidInputException("Unsupported color type");
		}

	}

}

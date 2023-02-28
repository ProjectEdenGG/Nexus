package gg.projecteden.nexus.features.resourcepack.models;

import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.commands.staff.admin.BashCommand;
import gg.projecteden.nexus.features.survival.MobNets;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.playerplushie.PlayerPlushieConfig;
import gg.projecteden.nexus.models.resourcepack.LocalResourcePackUser;
import gg.projecteden.nexus.models.resourcepack.LocalResourcePackUserService;
import gg.projecteden.nexus.utils.IOUtils;
import gg.projecteden.nexus.utils.ImageUtils;
import gg.projecteden.nexus.utils.Utils;
import lombok.SneakyThrows;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static gg.projecteden.nexus.features.resourcepack.ResourcePack.URL;
import static gg.projecteden.nexus.features.resourcepack.ResourcePack.hash;
import static gg.projecteden.nexus.utils.StringUtils.listLast;

public class Saturn {
	public static final String DIRECTORY = "/home/minecraft/git/Saturn" + (Nexus.getEnv() == Env.PROD ? "" : "-" + Nexus.getEnv()) + "/";
	public static final Path PATH = Path.of(DIRECTORY);

	private static final Supplier<String> HASH_SUPPLIER = () -> BashCommand.tryExecute("git rev-parse HEAD", PATH.toFile());

	@SneakyThrows
	private static void execute(String command) {
		command = command.replaceAll("//", "/");
		Nexus.debug("Executing %s at %s".formatted(command, PATH.toUri().toString().replaceFirst("file://", "")));

		final Process process = new ProcessBuilder(command.split(" "))
				.directory(PATH.toFile())
				.inheritIO()
				.start();

		process.waitFor();
		Nexus.debug("  Finished execution");
	}

	@SneakyThrows
	public static void deploy(boolean force, boolean silent) {
		Supplier<String> prefix = () -> "[Saturn Deploy] [" + DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()) + "] ";
		Nexus.log(prefix.get() + "Deploying Saturn...");

		Nexus.log(prefix.get() + "  Pulling");
		pull(force);

		Nexus.log(prefix.get() + "  Generating");
		generate();

		Nexus.log(prefix.get() + "  Committing");
		commitAndPush();

		Nexus.log(prefix.get() + "  Squashing");
		squash();

		// Temporary
//		Thread.sleep(20000);
		//

		Nexus.log(prefix.get() + "  Versioning");
		version();

		Nexus.log(prefix.get() + "  Hashing");
		updateHash();

		if (!silent) {
			Nexus.log(prefix.get() + "  Notifying");
			notifyTitanUsers();
		}

		Nexus.log(prefix.get() + "Deployed Saturn");
	}

	private static void pull(boolean force) {
		final String hashBefore = HASH_SUPPLIER.get();

		execute("git reset --hard origin/main");
		execute("git pull");

		final String hashAfter = HASH_SUPPLIER.get();

		final boolean foundUpdate = !Objects.equals(hashBefore, hashAfter);

		if (!force && !foundUpdate)
			throw new InvalidInputException("No Saturn updates found");
	}

	@SneakyThrows
	private static void squash() {
		execute("packsquash packsquash.toml");
	}

	private static void generate() {
		write(PlayerPlushieConfig.generate());
		write(MobNets.generate());
	}

	private static void commitAndPush() {
		if (Nexus.getEnv() == Env.PROD)
			execute("./commit.sh");
	}

	private static void write(Map<String, Object> files) {
		for (Entry<String, Object> entry : files.entrySet()) {
			final Consumer<String> writer = directory -> {
				try {
					final String path = directory + "/" + entry.getKey().replaceAll("//", "/").replaceAll("//", "/");
					execute("mkdir -p " + path.replace(listLast(path, "/"), ""));

					if (entry.getValue() instanceof String content)
						Files.write(Paths.get(path), content.getBytes());
					else if (entry.getValue() instanceof BufferedImage image)
						ImageUtils.write(image, Paths.get(path).toFile());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			};

			writer.accept(DIRECTORY);
		}
	}

	private static void version() {
		IOUtils.fileWrite(DIRECTORY + "/version" + (Nexus.getEnv() == Env.PROD ? "" : "-" + Nexus.getEnv()), (writer, outputs) -> outputs.add(HASH_SUPPLIER.get()));
	}

	private static void updateHash() {
		hash = Utils.createSha1(URL);

		if (hash == null)
			throw new InvalidInputException("Resource pack hash is null");
	}

	private static void notifyTitanUsers() {
		new LocalResourcePackUserService().getOnline().stream()
			.filter(LocalResourcePackUser::hasTitan)
			.forEach(user -> user.getOnlinePlayer().sendPluginMessage(Nexus.getInstance(), "titan:clientbound", "saturn-update".getBytes()));
	}

}

package gg.projecteden.nexus.features.resourcepack.models;

import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.commands.staff.admin.BashCommand;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.features.survival.MobNets;
import gg.projecteden.nexus.features.titan.ClientMessage;
import gg.projecteden.nexus.features.titan.clientbound.SaturnUpdate;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.models.playerplushie.PlayerPlushieConfig;
import gg.projecteden.nexus.models.resourcepack.LocalResourcePackUser;
import gg.projecteden.nexus.models.resourcepack.LocalResourcePackUserService;
import gg.projecteden.nexus.utils.IOUtils;
import gg.projecteden.nexus.utils.ImageUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import lombok.SneakyThrows;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Saturn {
	public static final String DIRECTORY = "/home/minecraft/git/Saturn" + (Nexus.getEnv() == Env.PROD ? "" : "-" + Nexus.getEnv()) + "/";
	public static final Path PATH = Path.of(DIRECTORY);

	private static final Supplier<String> HASH_SUPPLIER = () -> BashCommand.tryExecute("git rev-parse HEAD", PATH.toFile());

	@SneakyThrows
	private static void execute(String command) {
		command = command.replaceAll("//", "/");
		Nexus.debug("Executing %s at %s".formatted(command, PATH.toUri().toString().replaceFirst("file://", "")));

		final ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "))
			.directory(PATH.toFile())
			.inheritIO();

		processBuilder.environment().put("PACKSQUASH_LOG", "packsquash=info");

		final Process process = processBuilder.start();

		process.waitFor();
		Nexus.debug("  Finished execution");

		final int exitCode = process.exitValue();
		if (exitCode != 0) {
			throw new InvalidInputException("Command &e\"" + command + "\" &cexited with code " + exitCode);
		}
	}

	@SneakyThrows
	public static void deploy(boolean force, boolean silent) {
		Nexus.log("[Saturn] Deploying Saturn...");

		Nexus.log("[Saturn]   Pulling");
		pull(force);

		Nexus.log("[Saturn]   Generating");
		generate();

		Nexus.log("[Saturn]   Committing");
		commitAndPush();

		Nexus.log("[Saturn]   Filtering");
		filter();

		Nexus.log("[Saturn]   Squashing");
		squash();

		Nexus.log("[Saturn]   Versioning");
		version();

		Nexus.log("[Saturn]   Hashing");
		updateHash();

		if (!silent) {
			Nexus.log("[Saturn]   Notifying");
			notifyTitanUsers();
		}

		Nexus.log("[Saturn]   Deployed");
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

	private static void generate() {
		write(PlayerPlushieConfig.generate());
		write(MobNets.generate());
	}

	private static void commitAndPush() {
		if (Nexus.getEnv() == Env.PROD)
			execute("./commit.sh");
	}

	private static void filter() {
		execute("rm -rf assets/minecraft/textures/gui/title/background");
		execute("rm -rf archive");
	}

	@SneakyThrows
	private static void squash() {
		execute("packsquash packsquash.toml");
	}

	private static void write(Map<String, Object> files) {
		for (Entry<String, Object> entry : files.entrySet()) {
			final Consumer<String> writer = directory -> {
				try {
					final String path = directory + "/" + entry.getKey().replaceAll("//", "/").replaceAll("//", "/");
					execute("mkdir -p " + path.replace(StringUtils.listLast(path, "/"), ""));

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

	public static void updateHash() {
		ResourcePack.hash = Utils.createSha1(ResourcePack.URL);

		if (ResourcePack.hash == null)
			throw new InvalidInputException("Resource pack hash is null");
	}

	private static void notifyTitanUsers() {
		ClientMessage.builder()
			.message(new SaturnUpdate())
			.players(new LocalResourcePackUserService().getOnline().stream()
				.filter(LocalResourcePackUser::hasTitan)
				.map(PlayerOwnedObject::getOnlinePlayer)
				.collect(Collectors.toList()))
			.send();
	}

}

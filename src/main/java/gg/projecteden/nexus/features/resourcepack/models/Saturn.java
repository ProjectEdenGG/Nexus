package gg.projecteden.nexus.features.resourcepack.models;

import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.commands.staff.admin.BashCommand;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.playerplushie.PlayerPlushieConfig;
import gg.projecteden.nexus.models.resourcepack.LocalResourcePackUser;
import gg.projecteden.nexus.models.resourcepack.LocalResourcePackUserService;
import gg.projecteden.nexus.utils.Utils;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Supplier;

import static gg.projecteden.nexus.features.resourcepack.ResourcePack.FILE_NAME;
import static gg.projecteden.nexus.features.resourcepack.ResourcePack.URL;
import static gg.projecteden.nexus.features.resourcepack.ResourcePack.hash;
import static gg.projecteden.nexus.utils.Nullables.isNullOrEmpty;
import static gg.projecteden.nexus.utils.StringUtils.listLast;
import static gg.projecteden.nexus.utils.StringUtils.stripColor;

public class Saturn {
	public static final String DIRECTORY = "/home/minecraft/git/Saturn" + (Nexus.getEnv() == Env.PROD ? "" : "-" + Nexus.getEnv()) + "/";
	public static final String DEPLOY_DIRECTORY = DIRECTORY + "deploy";

	public static final Path PATH = Path.of(DIRECTORY);
	public static final Path DEPLOY_PATH = Path.of(DEPLOY_DIRECTORY);

	public static final List<String> INCLUDED = List.of("assets", "pack.mcmeta", "pack.png");

	private static void execute(String command, Path path) {
		command = command.replaceAll("//", "/");
		Nexus.log("Executing %s at %s".formatted(command, path.toUri().toString().replaceFirst("file://", "")));
		final String output = BashCommand.tryExecute(command, path.toFile());
		if (!isNullOrEmpty(output))
			Nexus.log(stripColor(output));
	}

	public static void deploy() {
		Nexus.log("Deploying Saturn...");

		pull();

		setup();

		copy();
		compute();
		minify();

		zip();

		cdn();

		teardown();

		updateHash();

		notifyTitanUsers();

		Nexus.log("Deployed Saturn");
	}

	private static void pull() {
		final Supplier<String> hash = () -> BashCommand.tryExecute("git rev-parse HEAD", PATH.toFile());

		final String hashBefore = hash.get();

		execute("git reset --hard origin/main");
		execute("git pull");

		final String hashAfter = hash.get();

		final boolean foundUpdate = !Objects.equals(hashBefore, hashAfter);

		if (!foundUpdate)
			throw new InvalidInputException("No Saturn updates found");
	}

	private static void setup() {
		teardown();
		execute("mkdir -p " + DEPLOY_DIRECTORY);
	}

	@SneakyThrows
	private static void copy() {
		execute("cp -r %s deploy".formatted(String.join(" ", INCLUDED)));
	}

	private static void execute(String command) {
		execute(command, PATH);
	}

	@SneakyThrows
	private static void minify() {
		Files.walk(DEPLOY_PATH).forEach(path -> {
			try {
				if (path.toUri().toString().endsWith(".json") || path.toUri().toString().endsWith(".meta")) {
					final Map<?, ?> map = Utils.getGson().fromJson(String.join("", Files.readAllLines(path)), Map.class);
					if (map.containsKey("textures") && map.containsKey("elements"))
						map.remove("groups");

					Files.write(path, Utils.getGson().toJson(map).getBytes());
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}

	private static void compute() {
		write(PlayerPlushieConfig.generate());
		// TODO
		//   Numbers?
	}

	private static void write(Map<String, String> files) {
		for (Entry<String, String> entry : files.entrySet()) {
			try {
				final String path = DEPLOY_DIRECTORY + "/" + entry.getKey().replaceAll("//", "/");
				final String content = entry.getValue();

				execute("mkdir -p " + path.replace(listLast(path, "/"), ""));
				Files.write(Paths.get(path), content.getBytes());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private static void zip() {
		execute("zip -rq %s .".formatted(FILE_NAME), DEPLOY_PATH);
	}

	private static void cdn() {
		execute("sudo /home/minecraft/git/deploy-saturn.sh " + (Nexus.getEnv() == Env.PROD ? "" : Nexus.getEnv()));
	}

	private static void teardown() {
		execute("rm -r " + DEPLOY_DIRECTORY);
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

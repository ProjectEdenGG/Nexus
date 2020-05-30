package me.pugabyte.bncore.features.trust;

import lombok.NonNull;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.trust.providers.TrustPlayerProvider;
import me.pugabyte.bncore.features.trust.providers.TrustProvider;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Description;
import me.pugabyte.bncore.framework.commands.models.annotations.HideFromHelp;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.home.Home;
import me.pugabyte.bncore.models.home.HomeOwner;
import me.pugabyte.bncore.models.home.HomeService;
import me.pugabyte.bncore.models.trust.Trust;
import me.pugabyte.bncore.models.trust.Trust.Type;
import me.pugabyte.bncore.models.trust.TrustService;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Aliases({"trusts", "allow"})
public class TrustCommand extends CustomCommand {
	Trust trust;
	TrustService service = new TrustService();

	public TrustCommand(@NonNull CommandEvent event) {
		super(event);
		trust = service.get(player());
	}

	@Description("Open the trust menu")
	@Path
	void run() {
		TrustProvider.open(player());
	}

	@HideFromHelp
	@Path("edit")
	void edit() {
		TrustProvider.open(player());
	}

	@Description("Open the trust menu for the specified player")
	@Path("<player>")
	void menu(OfflinePlayer player) {
		TrustPlayerProvider.open(player(), player);
	}

	@Description("Allow specified player(s) to a specific lock")
	@Path("lock <players...>")
	void lock(@Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		runCommand("cmodify " + names(players, " "));
	}

	@Description("Allow specified player(s) to a specific home")
	@Path("home <home> <players...>")
	void home(Home home, @Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		players.forEach(home::allow);
		new HomeService().save(home.getOwner());
		send(PREFIX + "Trusted &e" + names(players, "&3, &e") + " &3to home &e" + home.getName());
	}

	@Description("Allow specified player(s) to all locks")
	@Path("locks <players...>")
	void locks(@Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		process(players, Type.LOCKS);
	}

	@Description("Allow specified player(s) to all homes")
	@Path("homes <players...>")
	void homes(@Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		process(players, Type.HOMES);
	}

	@Description("Allow specified player(s) to everything")
	@Path("all <players...>")
	void all(@Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		process(players, Type.values());
	}

	@Permission("group.staff")
	@Path("admin locks <owner> <players...>")
	void locks(OfflinePlayer owner, @Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		trust = service.get(owner);
		send(PREFIX + "Modifying trusts of &e" + owner.getName());
		process(players, Type.LOCKS);
	}

	@Permission("group.staff")
	@Path("admin homes <owner> <players...>")
	void homes(OfflinePlayer owner, @Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		trust = service.get(owner);
		send(PREFIX + "Modifying trusts of &e" + owner.getName());
		process(players, Type.HOMES);
	}

	@Permission("group.staff")
	@Path("admin all <owner> <players...>")
	void all(OfflinePlayer owner, @Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		trust = service.get(owner);
		send(PREFIX + "Modifying trusts of &e" + owner.getName());
		process(players, Type.values());
	}

	private void process(List<OfflinePlayer> players, Trust.Type... types) {
		for (Type type : types)
			players.forEach(player -> trust.get(type).add(player.getUniqueId()));
		service.save(trust);
		String typeNames = Arrays.stream(types).map(Type::camelCase).collect(Collectors.joining("&3, &e"));
		send(PREFIX + "Trusted &e" + names(players, "&3, &e") + " &3to &e" + typeNames);
	}

	@NotNull
	private String names(List<OfflinePlayer> players, String separator) {
		return players.stream().map(OfflinePlayer::getName).collect(Collectors.joining(separator));
	}

	@Permission("migrate")
	@Path("migrate")
	void migrate() {
		HomeService homeService = new HomeService();
		TrustService trustService = new TrustService();

		migrationUuids.stream().map(UUID::fromString).forEach(uuid -> {
			HomeOwner homeOwner = homeService.get(uuid);
			Trust trust = trustService.get(uuid);
			trust.getHomes().addAll(homeOwner.getFullAccessList());
			trustService.save(trust);
		});

		BNCore.log("Migrated " + migrationUuids + " access lists");
	}

	private List<String> migrationUuids = Arrays.asList(
			"827b71ba-159d-4595-8bd7-f56608f9b795",
			"83765c2d-e10f-4913-b73b-f17a696c83f7",
			"894df72e-09ec-4f01-87ec-987ee4f59422",
			"88f9f7f6-7703-49bf-ad83-a4dec7e8022c",
			"8ab3da48-4ea8-4502-90f1-d412d057d240",
			"8bed99ff-835a-4471-814f-7eac8225b692",
			"916bbe68-09ed-4fe4-9532-fde20d357517",
			"916d3b36-9285-48b5-ae64-29b618bd790d",
			"9595d8c8-78a3-4163-b67e-799fc348a97f",
			"94306fe9-2d6d-4e01-8472-58f13d141fec",
			"96c28db2-b0e6-43ed-8c9d-28ab16ab32e0",
			"9843c7b3-dd4d-4b3c-9916-8234bb2e00c5",
			"9d390b0a-8083-4f71-a7d5-bef0ca0dd5d1",
			"a4274d94-10f2-4663-af3b-a842c7ec729c",
			"b043bad0-ce46-40d7-9774-4ab22e895353",
			"b0a4215d-4e1b-4a1b-8c26-8cf397794f5a",
			"b351fedc-6049-4be9-8cd7-aa08655d6080",
			"b4cf0745-6192-439b-896e-62a349eae31d",
			"b83bae78-83d6-43a0-9316-014a0a702ab2",
			"be973c5a-8d5b-4400-9114-401e9bf05159",
			"bfdcad23-ed46-4a7c-8796-c05475743e3d",
			"c0ca2d50-e517-40cd-be3c-dc3e886aeb35",
			"c12bc8fa-3c33-49e5-bb81-710100c6c36c",
			"c29168e4-0d67-4d80-b314-101b3637f9b4",
			"c32a246c-f8b4-4ac9-b036-c205e065e981",
			"c90d21e1-b8cf-414d-a6ff-c4dc86ffaaf9",
			"d1729990-0ad4-4db8-8a95-779128e9fa1a",
			"d997621b-84f6-457f-bfb9-c7401bf393f4",
			"dfa6bb32-a1a3-4c6b-9a97-d54e77b09c46",
			"e0d2e92f-5627-4109-aff1-86e86ea94a16",
			"e0232f4c-d9c5-46ee-a6f5-47141ab73518",
			"e14b8e66-8999-4c1e-994d-c292adb29094",
			"e6a41f28-7f50-4d35-8081-c909b8e5c64c",
			"e8aa8290-56ad-4bb7-8b3b-c7a27b7fdc6e",
			"e832284c-9681-49a1-be17-68ed388f1030",
			"ebf12c57-c2d9-4c37-a814-d9112b6d7744",
			"f3690c28-5802-41dc-aed0-d6ef137247f5",
			"f7978552-3991-4d52-9ed8-082fd2ebaacb",
			"fba2e04d-e8da-4c72-803a-5fb06afbe75c",
			"fc33745a-d3a1-47f7-8c73-26b199b54678",
			"0383cc62-834d-4882-abfb-b333d989e052",
			"050424fb-3e75-4a76-beb5-6917976d0672",
			"08e56296-52ac-412b-809a-c88237b6fd2d",
			"0a2221e4-000c-4818-82ea-cd43df07f0d4",
			"0ac76956-57d2-4ca5-a329-77ef4f1f76c7",
			"0b93554d-e7e2-4731-b5ac-f248648479a0",
			"0dedec56-8649-419d-b96a-9f99d2bdbb30",
			"0baaebf5-cfb0-431a-b625-465c64e694f1",
			"0e3cef4a-4554-4c96-9f41-975fc2ee058c",
			"19ba20ad-b535-4340-8910-59532909091d",
			"1b2cd278-f30f-4895-ad24-0554456315ca",
			"19be99b4-b3bd-4d2d-aa5e-7b1c2cd33489",
			"1e764db7-1a09-498a-9701-4759f6c6ea4b",
			"23acf4d0-f4db-4397-a5c4-809fa133a476",
			"294df887-8cf7-4ddc-9af3-a1a660417639",
			"2c71e62b-e11d-451f-bad8-b87b2dc63ddf",
			"33bff644-37b1-4ecd-949f-b46c010b14ae",
			"3dfb3e31-e7fb-44b0-8e78-7b23c74eb714",
			"417f3ed4-b87f-4966-b020-a738539b9055",
			"42f101fb-9724-4a64-8aee-4d6711f670ad",
			"4dc7928f-6210-4904-9cb6-fc615c4dccfb",
			"50dc9d8a-c593-4e21-b3fa-04f3343a6e3d",
			"55eac238-ba76-410f-9140-a78f879c0954",
			"592074f3-bb3e-4c5e-a1f4-4ce33764fb00",
			"5c3ddda9-51e1-4f9a-8233-e08cf0b26f11",
			"5d8debcc-a6f5-4ee6-ba31-353b3f88a917",
			"5fa2c854-4e85-4513-9ba7-26bd0dae1665",
			"620292fa-617a-4c6e-9271-3be019581bc1",
			"6469ee3e-5f50-42c2-ace0-a6537e294ace",
			"67fa2c3b-149c-4a2c-bfe6-cf7eaa45db63",
			"690b10e2-2d14-4829-9799-cad897884d33",
			"6810543f-0127-488f-be66-582f0f2d3aa8",
			"6afade73-6866-4fd9-baf6-f1dab00975d5",
			"6b4ae1ac-7618-4ab1-b476-07a7cd9fef32",
			"6c555ed6-f3b7-4afe-b634-74dde6789c9d",
			"71626033-d1ee-4268-9e4f-79b13b34232c",
			"70b644de-31f1-4889-8e5e-04963967a8dd",
			"6ef6c9db-a432-435b-9bc4-174a2b42309d",
			"73c4c7e6-cce0-42e1-b060-df733074fda2",
			"75f944f5-6972-43f5-abcf-17cdac2c605a",
			"76ef16fd-dfc3-470d-b150-967314c70c43",
			"77a12e7f-63b2-481e-9e6f-bdb62ee2e1e5",
			"77849598-3ef5-431b-a77c-1d925c304d7b",
			"7ac802d1-b902-4fbd-bd0b-253439852c94",
			"7ad8f35c-3cb2-45c1-8088-537fc32115c6",
			"7bcaffcb-8fc1-4864-8370-88d6570cc043",
			"7c10332d-0109-45eb-a0d8-5ec12f8840a1",
			"7c6893ed-83a4-4ba9-b989-6a178f39f92d",
			"7cfe8f22-0ed5-450f-952c-2459ff79ede6"
	);

}

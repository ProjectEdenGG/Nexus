package me.pugabyte.nexus.features.store.perks.autosort.commands;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.features.store.perks.autosort.AutoSort;
import me.pugabyte.nexus.features.store.perks.autosort.AutoSortFeature;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.preconfigured.NoPermissionException;
import me.pugabyte.nexus.models.autosort.AutoSortUser;
import me.pugabyte.nexus.models.autosort.AutoSortUserService;
import me.pugabyte.nexus.utils.LuckPermsUtils.GroupChange;
import me.pugabyte.nexus.utils.LuckPermsUtils.PermissionChange;
import org.bukkit.event.Listener;

import java.util.List;

@NoArgsConstructor
public class AutoSortCommand extends CustomCommand implements Listener {
	private final AutoSortUserService service = new AutoSortUserService();
	private AutoSortUser user;

	public AutoSortCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			user = service.get(player());
	}

	@Path("[feature] [enable]")
	void toggle(AutoSortFeature feature, Boolean enable) {
		if (feature != AutoSortFeature.AUTOTOOL)
			if (!player().hasPermission(AutoSort.PERMISSION))
				throw new NoPermissionException("Purchase at https://store.projecteden.gg");

		if (enable == null)
			enable = !user.hasFeatureEnabled(feature);

		if (enable)
			if (!user.getDisabledFeatures().contains(feature))
				error(camelCase(feature) + " is already enabled");
			else
				user.getDisabledFeatures().remove(feature);
		else
			if (user.getDisabledFeatures().contains(feature))
				error(camelCase(feature) + " is already disabled");
			else
				user.getDisabledFeatures().add(feature);

		service.save(user);
		String nameFixed = camelCase(feature.name().replaceFirst("AUTO", "AUTO_")).replaceFirst("Auto ", "Auto");
		send(PREFIX + nameFixed + " " + (enable ? "&aenabled" : "&cdisabled"));
	}

	@Path("convert")
	void convert() {
		List<String> autoSortUsers = List.of(
				"e664d005-6e49-4b00-a4ec-8a9ad693c543", "86d7e0e2-c95e-4f22-8f99-a6e83b398307", "77966ca3-ac85-44b2-bcb0-b7c5f9342e86",
				"2c71e62b-e11d-451f-bad8-b87b2dc63ddf", "827b71ba-159d-4595-8bd7-f56608f9b795", "b4cf0745-6192-439b-896e-62a349eae31d",
				"5ced9758-8268-46f8-9e12-54731dc7ba0f", "2f2b06a3-033f-4d53-b7c1-c1faffe5d314", "b299e2d2-4011-4968-a55c-638d9c37664b",
				"976dfbed-aefe-48d3-80a0-70f6614e779f", "ff6cfe09-eb11-4717-ba97-895f031f8418", "1524830c-6eaf-4469-9cb5-6e9312c9aa59",
				"6653ab1a-d6b9-4da8-9116-01f5bcd41d98", "22b0cc70-2573-4107-a5ae-e2acbe01d0cd", "9595d8c8-78a3-4163-b67e-799fc348a97f",
				"64233257-5a78-4f60-95d1-11cac8336c8b", "42f101fb-9724-4a64-8aee-4d6711f670ad", "da4b7877-2292-4f30-8180-843ea44a1531",
				"b5167a04-5900-41a6-aae5-1f42f92d7594", "77da09f0-8d1f-4e36-a542-3894b4916c31", "56cb00fd-4738-47bc-be08-cb7c4f9a5a94",
				"690b10e2-2d14-4829-9799-cad897884d33", "476aa4ee-5246-44d4-a328-f404961b7b8d", "e9e07315-d32c-4df7-bd05-acfe51108234",
				"499a1e70-d91a-4b55-bc5e-eb02824d838c", "b83bae78-83d6-43a0-9316-014a0a702ab2", "d1de9ca8-78f6-4aae-87a1-8c112f675f12",
				"92426267-5944-437b-a402-d8e9464ed44b", "7668257c-8e5f-4c14-87cb-84bd5e1c2853", "d997621b-84f6-457f-bfb9-c7401bf393f4",
				"27c0dcae-9643-4bdd-bb3d-34216d14761c", "562f342c-fed5-467f-b067-f62b78f9375a", "e4b78cfb-4339-4eb2-b580-bd7520972b8d",
				"1d70383f-21ba-4b8b-a0b4-6c327fbdade1", "67fa2c3b-149c-4a2c-bfe6-cf7eaa45db63", "0a2221e4-000c-4818-82ea-cd43df07f0d4",
				"acb8a46c-ff15-4127-812c-0a7d3485fdd7", "9d7e5f73-7a57-41ba-b528-d9a0786beb38", "a69f1433-8be4-4b32-a9eb-c5a0b02fbe5c",
				"5fa2c854-4e85-4513-9ba7-26bd0dae1665", "75d63edb-84cc-4d4a-b761-4f81c91b2b7a", "31926843-c131-4962-a608-ebb785cf6f8d",
				"91b9edf7-c351-4437-89d4-d11f3137b1f0", "a9b986c2-9a0c-4a9d-870c-2d8f91ce320c", "9f9cf214-8ff3-46bb-970a-1d1daca7d994",
				"f6fe588e-e044-45ea-8c51-c8d9ed691500", "b677762d-2549-4fa3-b5e0-287a1f5c3674", "32fc75e3-a278-43c4-99a7-90af03846dad",
				"604125c1-1a40-4b37-80d9-e530d4c3e09b", "5c3ddda9-51e1-4f9a-8233-e08cf0b26f11"
		);

		List<String> permissions = List.of(
				"automaticinventory.autocraft",
				"automaticinventory.autotrash",
				"automaticinventory.depositall",
				"automaticinventory.quickdeposit",
				"automaticinventory.refillstacks",
				"automaticinventory.sortchests",
				"automaticinventory.sortinventory"
		);

		for (String uuid : autoSortUsers) {
			for (String permission : permissions)
				PermissionChange.unset().uuid(uuid).permission(permission).run();

			GroupChange.remove().uuid(uuid).group("store.autosort").run();

			PermissionChange.set().uuid(uuid).permission("store.autosort").run();
		}
	}

}

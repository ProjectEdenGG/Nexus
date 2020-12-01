package me.pugabyte.nexus.features.commands.staff.admin;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.serializetest.SerializeTest;
import me.pugabyte.nexus.models.serializetest.SerializeTestService;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.SerializationUtils.JSON;
import me.pugabyte.nexus.utils.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SerializeCommand extends CustomCommand {
	private final SerializeTestService service = new SerializeTestService();
	private SerializeTest test;

	public SerializeCommand(@NonNull CommandEvent event) {
		super(event);
		test = service.get(player());
	}

	@Path("item toJson")
	void itemStackToJson() {
		String serialized = JSON.serializeItemStack(getToolRequired());
		send(json(serialized).copy(serialized).hover("Click to copy"));
	}

	@Path("item fromJson <json|paste>")
	void itemStackFromJson(String input) {
		if (!input.startsWith("{"))
			input = StringUtils.getPaste(input);

		ItemUtils.giveItem(player(), JSON.deserializeItemStack(input));
	}

	@Path("item database")
	void itemStackDatabase() {
		test.setItemStack(getToolRequired());
		reload();
		ItemUtils.giveItem(player(), test.getItemStack());
	}

	@Path("inventory database")
	void inventoryDatabase() {
		test.setItemStacks(Arrays.asList(player().getInventory().getContents()));
		reload();
		ItemUtils.giveItems(player(), test.getItemStacks());
	}

	@Path("hashmap initialized database")
	void hashmapInitializedPutDatabase() {
		Map<String, String> initializedMap = test.getInitializedMap();
		initializedMap.put("1", "1");
		initializedMap.put("2", "2");
		reload();
		send("Data: " + test.getInitializedMap());

		test.getInitializedMap().clear();
		reload();
		send("Cleared: " + test.getInitializedMap());

		test.setInitializedMap(null);
		reload();
		send("Nulled: " + test.getInitializedMap());
	}

	@Path("hashmap uninitialized database")
	void hashmapUninitializedPutDatabase() {
		Map<String, String> map = new HashMap<>();
		map.put("1", "1");
		map.put("2", "2");
		test.setUninitializedMap(map);
		reload();
		send("Data: " + test.getUninitializedMap());

		test.getUninitializedMap().clear();
		reload();
		send("Cleared: " + test.getUninitializedMap());

		test.setUninitializedMap(null);
		reload();
		send("Nulled: " + test.getUninitializedMap());
	}

	private void reload() {
		service.saveSync(test);
		service.clearCache();
		test = service.get(player());
	}

}

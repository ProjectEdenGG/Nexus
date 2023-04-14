package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.models.serializetest.SerializeTest;
import gg.projecteden.nexus.models.serializetest.SerializeTestService;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SerializationUtils.Json;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;
import static gg.projecteden.nexus.utils.SerializationUtils.Json.serialize;

@Permission(Group.ADMIN)
public class SerializeCommand extends CustomCommand {
	private final SerializeTestService service = new SerializeTestService();
	private SerializeTest test;

	public SerializeCommand(@NonNull CommandEvent event) {
		super(event);
		test = service.get(player());
	}

	private void reload() {
		service.saveSync(test);
		service.clearCache();
		test = service.get(player());
	}

	@Description("Serialize an item to JSON")
	void item_toJson() {
		String serialized = Json.toString(serialize(getToolRequired()));
		send(json(serialized).copy(serialized).hover("Click to copy"));
	}

	@Description("Deserialize an item from JSON or a paste")
	void item_fromJson(String json) {
		if (!json.startsWith("{"))
			json = StringUtils.getPaste(json);

		PlayerUtils.giveItem(player(), Json.deserializeItemStack(json));
	}

	@Description("Test database item serialization and deserialization")
	void item_database() {
		test.setItemStack(getToolRequired());
		reload();
		PlayerUtils.giveItem(player(), test.getItemStack());
	}

	@Description("Test database inventory serialization and deserialization")
	void inventory_database() {
		Block targetBlock = getTargetBlock();
		if (targetBlock.getType() != Material.CHEST)
			error("You must be looking at a chest");

		List<ItemStack> items = new ArrayList<>(Arrays.asList(inventory().getContents()));
		items.removeIf(Nullables::isNullOrAir);
		test.setItemStacks(items);
		reload();

		Chest state = (Chest) targetBlock.getState();
		state.getInventory().clear();
		for (ItemStack itemStack : test.getItemStacks())
			if (!isNullOrAir(itemStack))
				state.getInventory().addItem(itemStack);
	}

	@Description("Test database serialization and deserialization of an initialized map")
	void hashmap_initialized_database() {
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

	@Description("Test database serialization and deserialization of an uninitialized map")
	void hashmap_uninitialized_database() {
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

}

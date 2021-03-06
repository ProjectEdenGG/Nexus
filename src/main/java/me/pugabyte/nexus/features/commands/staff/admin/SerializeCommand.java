package me.pugabyte.nexus.features.commands.staff.admin;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.serializetest.SerializeTest;
import me.pugabyte.nexus.models.serializetest.SerializeTestService;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.SerializationUtils.JSON;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.pugabyte.nexus.utils.ItemUtils.isNullOrAir;
import static me.pugabyte.nexus.utils.SerializationUtils.JSON.serializeItemStack;

@Permission("group.admin")
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

	@Path("item toJson")
	void itemStackToJson() {
		String serialized = JSON.toString(serializeItemStack(getToolRequired()));
		send(json(serialized).copy(serialized).hover("Click to copy"));
	}

	@Path("item fromJson <json|paste>")
	void itemStackFromJson(String input) {
		if (!input.startsWith("{"))
			input = StringUtils.getPaste(input);

		PlayerUtils.giveItem(player(), JSON.deserializeItemStack(input));
	}

	@Path("item database")
	void itemStackDatabase() {
		test.setItemStack(getToolRequired());
		reload();
		PlayerUtils.giveItem(player(), test.getItemStack());
	}

	@Path("inventory database")
	void inventoryDatabase() {
		Block targetBlock = getTargetBlock();
		if (targetBlock.getType() != Material.CHEST)
			error("You must be looking at a chest");

		List<ItemStack> items = new ArrayList<>(Arrays.asList(inventory().getContents()));
		items.removeIf(ItemUtils::isNullOrAir);
		test.setItemStacks(items);
		reload();

		Chest state = (Chest) targetBlock.getState();
		state.getInventory().clear();
		for (ItemStack itemStack : test.getItemStacks())
			if (!isNullOrAir(itemStack))
				state.getInventory().addItem(itemStack);
	}

	@Path("hashmap initialized database")
	void hashmapInitializedDatabase() {
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
	void hashmapUninitializedDatabase() {
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

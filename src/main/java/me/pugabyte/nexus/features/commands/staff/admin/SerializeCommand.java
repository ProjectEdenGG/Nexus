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

public class SerializeCommand extends CustomCommand {
	private final SerializeTestService service = new SerializeTestService();
	private SerializeTest test;

	public SerializeCommand(@NonNull CommandEvent event) {
		super(event);
		test = service.get(player());
	}

	@Path("itemStack toJson")
	void itemStackToJson() {
		String serialized = JSON.serializeItemStack(getToolRequired());
		send(json(serialized).copy(serialized).hover("Click to copy"));
	}

	@Path("itemStack fromJson <json|paste>")
	void itemStackFromJson(String input) {
		if (!input.startsWith("{"))
			input = StringUtils.getPaste(input);

		ItemUtils.giveItem(player(), JSON.deserializeItemStack(input));
	}

	@Path("itemStack database")
	void itemStackDatabase() {
		test.setItemStack(getToolRequired());
		service.saveSync(test);
		service.clearCache();
		test = service.get(player());
		ItemUtils.giveItem(player(), test.getItemStack());
	}

	@Path("inventory database")
	void inventoryDatabase() {
		test.setItemStacks(Arrays.asList(player().getInventory().getContents()));
		service.saveSync(test);
		service.clearCache();
		test = service.get(player());
		ItemUtils.giveItems(player(), test.getItemStacks());
	}

}

package me.pugabyte.nexus.models.pugmas20;

import com.google.common.collect.Sets;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.events.models.QuestStage;
import me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20;
import me.pugabyte.nexus.features.events.y2020.pugmas20.models.QuestNPC;
import me.pugabyte.nexus.features.events.y2020.pugmas20.quests.OrnamentVendor.Ornament;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.ItemStackConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20.isAtPugmas;
import static me.pugabyte.nexus.utils.ItemUtils.isNullOrAir;
import static me.pugabyte.nexus.utils.StringUtils.colorize;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Entity("pugmas20_user")
@Converters({UUIDConverter.class, ItemStackConverter.class})
public class Pugmas20User implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;

	private boolean warped = false;
	private boolean muteTrain = false;

	// Advent
	@Embedded
	private Set<Integer> foundDays = new HashSet<>();
	@Embedded
	private Set<Integer> locatedDays = new HashSet<>();

	// Active Quest NPCs
	@Embedded
	private Set<Integer> nextStepNPCs = Sets.newHashSet(
			QuestNPC.NOUGAT.getId(), QuestNPC.QA_ELF.getId(), QuestNPC.HAZELNUT.getId(), QuestNPC.JADE.getId(), QuestNPC.TICKET_MASTER.getId());

	// Quest - Gift Giver
	private QuestStage giftGiverStage = QuestStage.NOT_STARTED;

	// Quest - Light The Tree
	private QuestStage lightTreeStage = QuestStage.NOT_STARTED;
	private int torchesLit = 0;
	private boolean lightingTorches = false;
	private int torchTimerTaskId = -1;

	// Quest - Toy Testing
	private QuestStage toyTestingStage = QuestStage.NOT_STARTED;
	private boolean masterMind = false;
	private boolean connectFour = false;
	private boolean ticTacToe = false;
	private boolean battleship = false;

	// Quest - Ornament Vendor
	private QuestStage ornamentVendorStage = QuestStage.NOT_STARTED;
	private Map<Ornament, Integer> ornamentTradeCount = new HashMap<>();
	@Getter
	private static final transient int maxOrnamentCount = 6;

	// Quest - The Mines
	private QuestStage minesStage = QuestStage.INELIGIBLE;

	@Embedded
	private List<ItemStack> inventory = new ArrayList<>();

	public void storeInventory() {
		if (!isOnline()) return;

		PlayerInventory playerInventory = getPlayer().getInventory();
		for (ItemStack item : playerInventory.getContents()) {
			if (isNullOrAir(item) || Utils.isNullOrEmpty(item.getLore()))
				continue;

			if (item.getLore().get(0).contains(colorize(Pugmas20.getQuestLore()))) {
				playerInventory.removeItem(item);
				inventory.add(item);
			}
		}
	}

	public void applyInventory() {
		if (!isOnline()) return;
		if (!isAtPugmas(getPlayer())) return;
		if (this.inventory.isEmpty()) return;

		ArrayList<ItemStack> inventory = new ArrayList<>(this.inventory);
		this.inventory.clear();
		this.inventory.addAll(PlayerUtils.giveItemsGetExcess(getPlayer(), inventory));

		if (this.inventory.isEmpty())
			send(Pugmas20.PREFIX + "Inventory applied");
		else
			send(new JsonBuilder(Pugmas20.PREFIX + "Could not give all event items, clear up some inventory space and click here or re-enter the world")
					.hover("Click to collect the rest of your event items")
					.command("/pugmas20 inventory apply"));
	}

	public void resetLightTheTree() {
		torchesLit = 0;
		lightingTorches = false;
		Tasks.cancel(torchTimerTaskId);
		torchTimerTaskId = -1;
	}

	public boolean canTradeOrnament(Ornament ornament) {
		return ornamentTradesLeft(ornament) > 0;
	}

	public int ornamentTradesLeft(Ornament ornament) {
		return maxOrnamentCount - ornamentTradeCount.getOrDefault(ornament, 0);
	}
}

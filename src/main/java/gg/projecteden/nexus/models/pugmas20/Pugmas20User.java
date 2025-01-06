package gg.projecteden.nexus.models.pugmas20;

import com.google.common.collect.Sets;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.events.models.QuestStage;
import gg.projecteden.nexus.features.events.y2020.pugmas20.Pugmas20;
import gg.projecteden.nexus.features.events.y2020.pugmas20.models.QuestNPC;
import gg.projecteden.nexus.features.events.y2020.pugmas20.quests.OrnamentVendor.Ornament;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.ItemStackConverter;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Entity(value = "pugmas20_user", noClassnameStored = true)
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
	private Map<Ornament, Integer> ornamentTradeCount = new ConcurrentHashMap<>();
	@Getter
	private static final transient int maxOrnamentCount = 6;

	// Quest - The Mines
	private QuestStage minesStage = QuestStage.INELIGIBLE;

	@Embedded
	private List<ItemStack> inventory = new ArrayList<>();

	public void storeInventory() {
		if (!isOnline()) return;

		PlayerInventory playerInventory = getOnlinePlayer().getInventory();
		for (ItemStack item : playerInventory.getContents()) {
			if (gg.projecteden.nexus.utils.Nullables.isNullOrAir(item) || Nullables.isNullOrEmpty(item.getLore()))
				continue;

			if (item.getLore().get(0).contains(StringUtils.colorize(Pugmas20.getQuestLore()))) {
				playerInventory.removeItem(item);
				inventory.add(item);
			}
		}
	}

	public void applyInventory() {
		if (!isOnline()) return;
		if (!Pugmas20.isAtPugmas(getOnlinePlayer())) return;
		if (this.inventory.isEmpty()) return;

		ArrayList<ItemStack> inventory = new ArrayList<>(this.inventory);
		this.inventory.clear();
		this.inventory.addAll(PlayerUtils.giveItemsAndGetExcess(getOnlinePlayer(), inventory));

		if (this.inventory.isEmpty())
			sendMessage(Pugmas20.PREFIX + "Inventory applied");
		else
			sendMessage(new JsonBuilder(Pugmas20.PREFIX + "Could not give all event items, clear up some inventory space and click here or re-enter the world")
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

package me.pugabyte.nexus.features.events.y2021.bearfair21.islands;

import eden.utils.TimeUtils.Time;
import eden.utils.TimeUtils.Timespan;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.events.annotations.Region;
import me.pugabyte.nexus.features.events.models.BearFairIsland.NPCClass;
import me.pugabyte.nexus.features.events.models.QuestStage;
import me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21;
import me.pugabyte.nexus.features.events.y2021.bearfair21.islands.PugmasIsland.PugmasNPCs;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.BearFair21TalkingNPC;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.clientside.ClientsideContentManager;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.npcs.BearFair21NPC;
import me.pugabyte.nexus.models.bearfair21.BearFair21User;
import me.pugabyte.nexus.models.bearfair21.BearFair21UserService;
import me.pugabyte.nexus.models.bearfair21.ClientsideContent;
import me.pugabyte.nexus.models.bearfair21.ClientsideContent.Content;
import me.pugabyte.nexus.models.bearfair21.ClientsideContentService;
import me.pugabyte.nexus.utils.ActionBarUtils;
import me.pugabyte.nexus.utils.BlockUtils;
import me.pugabyte.nexus.utils.LocationUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Tasks.Countdown;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21.isAtBearFair;

// TODO BF21: Quest + Dialog
@Region("pugmas")
@NPCClass(PugmasNPCs.class)
public class PugmasIsland implements Listener, BearFair21Island {
	private static final ClientsideContentService contentService = new ClientsideContentService();
	private static final ClientsideContent contentList = contentService.get0();

	private static final BearFair21UserService userService = new BearFair21UserService();
	private static final List<Location> contentIndex = Arrays.asList(
			loc(-45, 143, -325),
			loc(-49, 139, -300),
			loc(-76, 138, -305),
			loc(-101, 139, -290),
			loc(-111, 142, -321),
			loc(-104, 154, -350),
			loc(-72, 157, -364),
			loc(-78, 152, -346),
			loc(-79, 134, -322),
			loc(-59, 145, -343),
			loc(-40, 153, -353),
			loc(-59, 166, -374),
			loc(-78, 174, -384),
			loc(-106, 174, -381),
			loc(-67, 139, -308)
	);

	public PugmasIsland() {
		Nexus.registerListener(this);
	}

	public enum PugmasNPCs implements BearFair21TalkingNPC {
		VILLAGER_1(BearFair21NPC.PUGMAS_VILLAGER_1) {
			@Override
			public List<String> getScript(BearFair21User user) {
				return getScaredVillager();
			}
		},
		VILLAGER_2(BearFair21NPC.PUGMAS_VILLAGER_2) {
			@Override
			public List<String> getScript(BearFair21User user) {
				return getScaredVillager();
			}
		},
		VILLAGER_3(BearFair21NPC.PUGMAS_VILLAGER_3) {
			@Override
			public List<String> getScript(BearFair21User user) {
				return getScaredVillager();
			}
		},
		VILLAGER_4(BearFair21NPC.PUGMAS_VILLAGER_4) {
			@Override
			public List<String> getScript(BearFair21User user) {
				return getScaredVillager();
			}
		},
		VILLAGER_5(BearFair21NPC.PUGMAS_VILLAGER_5) {
			@Override
			public List<String> getScript(BearFair21User user) {
				return getScaredVillager();
			}
		},
		VILLAGER_6(BearFair21NPC.PUGMAS_VILLAGER_6) {
			@Override
			public List<String> getScript(BearFair21User user) {
				return getScaredVillager();
			}
		},
		VILLAGER_7(BearFair21NPC.PUGMAS_VILLAGER_7) {
			@Override
			public List<String> getScript(BearFair21User user) {
				return getScaredVillager();
			}
		},
		VILLAGER_8(BearFair21NPC.PUGMAS_VILLAGER_8) {
			@Override
			public List<String> getScript(BearFair21User user) {
				return getScaredVillager();
			}
		},
		VILLAGER_9(BearFair21NPC.PUGMAS_VILLAGER_9) {
			@Override
			public List<String> getScript(BearFair21User user) {
				return getScaredVillager();
			}
		},
		VILLAGER_10(BearFair21NPC.PUGMAS_VILLAGER_10) {
			@Override
			public List<String> getScript(BearFair21User user) {
				return getScaredVillager();
			}
		},
		VILLAGER_11(BearFair21NPC.PUGMAS_VILLAGER_11) {
			@Override
			public List<String> getScript(BearFair21User user) {
				return getScaredVillager();
			}
		},
		VILLAGER_12(BearFair21NPC.PUGMAS_VILLAGER_12) {
			@Override
			public List<String> getScript(BearFair21User user) {
				return getScaredVillager();
			}
		},
		VILLAGER_13(BearFair21NPC.PUGMAS_VILLAGER_13) {
			@Override
			public List<String> getScript(BearFair21User user) {
				return getScaredVillager();
			}
		},
		VILLAGER_14(BearFair21NPC.PUGMAS_VILLAGER_14) {
			@Override
			public List<String> getScript(BearFair21User user) {
				return getScaredVillager();
			}
		},
		MAYOR(BearFair21NPC.PUGMAS_MAYOR) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();

				switch (user.getQuestStage_Pugmas()) {
					case NOT_STARTED -> {
						script.add("TODO - Go find Grinch");

						user.setQuestStage_Pugmas(QuestStage.STARTED);
						userService.save(user);
						return script;
					}
					case STARTED -> {
						script.add("TODO - Reminder");
						return script;
					}
				}

				script.add("TODO - Hello");
				return script;
			}
		},
		GRINCH(BearFair21NPC.GRINCH) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();

				switch (user.getQuestStage_Pugmas()) {
					case STARTED -> {
						script.add("TODO - Start challenge");

						user.setQuestStage_Pugmas(QuestStage.STEP_ONE);
						userService.save(user);

						Tasks.wait(20, () -> startChallenge(user));
						return script;
					}

					case STEP_ONE -> {
						script.add("TODO - Reminder");
						return script;
					}
				}

				script.add("TODO - What do YOU want");
				return script;
			}
		},
		;

		private final BearFair21NPC npc;
		private final List<String> script;

		@Override
		public List<String> getScript(BearFair21User user) {
			return this.script;
		}

		@Override
		public String getName() {
			return this.npc.getName();
		}

		@Override
		public int getNpcId() {
			return this.npc.getId();
		}

		PugmasNPCs(BearFair21NPC npc) {
			this.npc = npc;
			this.script = new ArrayList<>();
		}
	}

	private static Location loc(int x, int y, int z) {
		return new Location(BearFair21.getWorld(), x, y, z);
	}

	private static List<String> getScaredVillager() {
		List<String> script = new ArrayList<>();
		script.add("TODO - Go see mayor");
		return script;
	}

	private static void startChallenge(BearFair21User user) {
		if (user.getActiveTaskId() != -1) {
			user.sendMessage("Error: You have an active task running");
			return;
		}

		Countdown countdown = Countdown.builder()
				.duration(Time.MINUTE.x(3))
				.onSecond(i -> ActionBarUtils.sendActionBar(user.getPlayer(), "&3Time Left: &e" + Timespan.of(i).format()))
				.onComplete(() -> endChallenge(user, false))
				.start();

		user.setActiveTaskId(countdown.getTaskId());
		user.setPresentNdx(1);
		userService.save(user);
		showNext(user);
	}

	private static void endChallenge(BearFair21User user, boolean completed) {
		user.cancelActiveTask();
		user.setQuestStage_Pugmas(QuestStage.COMPLETE);
		userService.save(user);

		user.sendMessage("End of challenge, completed: " + completed);
	}

	private void clickedPresent(BearFair21User user, Content content) {
		if (content != null)
			ClientsideContentManager.sendRemoveItemFrames(user.getOnlinePlayer(), Collections.singletonList(content));

		int userNdx = user.getPresentNdx() + 1;
		user.setPresentNdx(userNdx);
		userService.save(user);

		if (userNdx > contentIndex.size()) {
			endChallenge(user, true);
			return;
		}

		showNext(user);
	}

	private static void showNext(BearFair21User user) {
		Content next = contentList.from(contentIndex.get(user.getPresentNdx() - 1));
		if (next != null) {
			ClientsideContentManager.sendSpawnItemFrames(user.getPlayer(), Collections.singletonList(next), true);
			// TODO BF21: Show a beacon or glowing invisible slime or something
		}
	}

	@EventHandler
	public void onClickPresent(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if (!EquipmentSlot.HAND.equals(event.getHand())) return;

		Player player = event.getPlayer();
		if (!isAtBearFair(player)) return;

		Block block = event.getClickedBlock();
		if (BlockUtils.isNullOrAir(block)) return;

		BearFair21User user = userService.get(event.getPlayer());
		if (user.getQuestStage_Pugmas() != QuestStage.STEP_ONE) return;

		event.setCancelled(true);

		Location location = block.getRelative(event.getBlockFace()).getLocation();
		Content content = contentList.from(location);
		if (content == null)
			return;

		Location contentLoc = content.getLocation().toBlockLocation();
		int locNdx = getLocationIndex(contentLoc) + 1;
		int userNdx = user.getPresentNdx();
		if (locNdx == userNdx)
			clickedPresent(user, content);
	}

	private int getLocationIndex(Location location) {
		contentIndex.indexOf(location);

		int ndx = 0;
		for (Location _location : contentIndex) {
			if (LocationUtils.isFuzzyEqual(location, _location))
				return ndx;
			++ndx;
		}

		return -1;
	}
}

package gg.projecteden.nexus.features.quests.tasks;

import gg.projecteden.nexus.features.quests.interactable.instructions.Dialog;
import gg.projecteden.nexus.features.quests.interactable.instructions.DialogInstance;
import gg.projecteden.nexus.features.quests.tasks.EnterRegionQuestTask.EnterRegionQuestTaskStep;
import gg.projecteden.nexus.features.quests.tasks.common.QuestTask;
import gg.projecteden.nexus.features.quests.tasks.common.QuestTaskStep;
import gg.projecteden.nexus.models.quests.QuestTaskStepProgress;
import gg.projecteden.nexus.models.quests.Quester;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static gg.projecteden.api.common.utils.Nullables.isNotNullOrEmpty;

@Data
public class EnterRegionQuestTask extends QuestTask<EnterRegionQuestTask, EnterRegionQuestTaskStep> {

	public EnterRegionQuestTask(List<EnterRegionQuestTaskStep> steps) {
		super(steps);
	}

	@Data
	public static class EnterRegionQuestTaskStep extends QuestTaskStep<EnterRegionQuestTask, EnterRegionQuestTaskStep> {
		private World world;
		private String regex;
		private Dialog complete;

		@Override
		public DialogInstance interact(Quester quester, QuestTaskStepProgress stepProgress) {
			if (dialog != null && stepProgress.isFirstInteraction())
				return dialog.send(quester);
			else if (reminder != null)
				return reminder.send(quester);
			else
				return null;
		}

		@Override
		public boolean shouldAdvance(Quester quester, QuestTaskStepProgress stepProgress) {
			if (isNotNullOrEmpty(regex)) {
				WorldGuardUtils worldguard = new WorldGuardUtils(world);
				if (!stepProgress.isFirstInteraction() && worldguard.isInRegionLikeAt(regex, quester.getOnlinePlayer().getLocation()))
					return true;
			}

			return false;
		}

	}

	public static EnterRegionTaskBuilder builder() {
		return new EnterRegionTaskBuilder();
	}

	@NoArgsConstructor
	public static class EnterRegionTaskBuilder extends TaskBuilder<EnterRegionQuestTask, EnterRegionTaskBuilder, EnterRegionQuestTaskStep> {

		@Override
		public EnterRegionQuestTaskStep nextStep() {
			return new EnterRegionQuestTaskStep();
		}

		@NotNull
		public EnterRegionQuestTask newInstance() {
			return new EnterRegionQuestTask(steps);
		}

		public EnterRegionTaskBuilder enterRegion(String world, String regex) {
			return enterRegion(Bukkit.getWorld(world), regex);
		}

		public EnterRegionTaskBuilder enterRegion(World world, String regex) {
			currentStep.world = world;
			currentStep.regex = regex;
			return this;
		}

	}

}

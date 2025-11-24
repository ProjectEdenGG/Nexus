package gg.projecteden.nexus.features.quests.tasks;

import gg.projecteden.api.common.utils.RegexUtils;
import gg.projecteden.nexus.features.quests.interactable.instructions.Dialog;
import gg.projecteden.nexus.features.quests.interactable.instructions.DialogInstance;
import gg.projecteden.nexus.features.quests.tasks.EnteringRegionQuestTask.EnteringRegionQuestTaskStep;
import gg.projecteden.nexus.features.quests.tasks.common.QuestTask;
import gg.projecteden.nexus.features.quests.tasks.common.QuestTaskStep;
import gg.projecteden.nexus.models.quests.QuestTaskStepProgress;
import gg.projecteden.nexus.models.quests.Quester;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Pattern;

@Data
public class EnteringRegionQuestTask extends QuestTask<EnteringRegionQuestTask, EnteringRegionQuestTaskStep> {

	public EnteringRegionQuestTask(List<EnteringRegionQuestTaskStep> steps) {
		super(steps);
	}

	@Data
	public static class EnteringRegionQuestTaskStep extends QuestTaskStep<EnteringRegionQuestTask, EnteringRegionQuestTaskStep> {
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
			return false;
		}

		public boolean isMatchingRegion(World world, String region) {
			Pattern pattern = RegexUtils.ignoreCasePattern(regex);
			return this.world.equals(world) && pattern.matcher(region).matches();
		}

	}

	public static EnteringRegionTaskBuilder builder() {
		return new EnteringRegionTaskBuilder();
	}

	@NoArgsConstructor
	public static class EnteringRegionTaskBuilder extends TaskBuilder<EnteringRegionQuestTask, EnteringRegionTaskBuilder, EnteringRegionQuestTaskStep> {

		@Override
		public EnteringRegionQuestTaskStep nextStep() {
			return new EnteringRegionQuestTaskStep();
		}

		@NotNull
		public EnteringRegionQuestTask newInstance() {
			return new EnteringRegionQuestTask(steps);
		}

		public EnteringRegionTaskBuilder enterRegion(String world, String regex) {
			return enterRegion(Bukkit.getWorld(world), regex);
		}

		public EnteringRegionTaskBuilder enterRegion(World world, String regex) {
			currentStep.world = world;
			currentStep.regex = regex;
			return this;
		}

	}

}

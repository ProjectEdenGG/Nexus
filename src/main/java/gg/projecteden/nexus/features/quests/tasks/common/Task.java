package gg.projecteden.nexus.features.quests.tasks.common;

import gg.projecteden.nexus.features.quests.interactable.Interactable;
import gg.projecteden.nexus.features.quests.interactable.instructions.Dialog;
import gg.projecteden.nexus.features.quests.interactable.instructions.DialogInstance;
import gg.projecteden.nexus.features.quests.tasks.QuestReward;
import gg.projecteden.nexus.features.quests.tasks.common.Task.TaskStep;
import gg.projecteden.nexus.features.quests.users.QuestStepProgress;
import gg.projecteden.nexus.features.quests.users.Quester;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Data
@NoArgsConstructor
public abstract class Task<
	TaskType extends Task<TaskType, TaskStepType>,
	TaskStepType extends TaskStep<TaskType, TaskStepType>
> {
	protected List<TaskStepType> steps;

	public Task(List<TaskStepType> steps) {
		this.steps = steps;
	}

	@Data
	public static abstract class TaskStep<
		TaskType extends Task<TaskType, TaskStepType>,
		TaskStepType extends TaskStep<TaskType, TaskStepType>
	> {
		protected Interactable interactable;
		protected Dialog dialog;
		protected Dialog reminder;

		abstract public DialogInstance interact(Quester quester, QuestStepProgress stepProgress);

		abstract public boolean shouldAdvance(Quester quester, QuestStepProgress stepProgress);
	}

	public static abstract class TaskBuilder<
		TaskType extends Task<TaskType, TaskStepType>,
		TaskBuilderType extends TaskBuilder<TaskType, TaskBuilderType, TaskStepType>,
		TaskStepType extends TaskStep<TaskType, TaskStepType>
	> {
		protected List<TaskStepType> steps = new ArrayList<>();
		protected TaskStepType currentStep = nextStep();

		abstract public TaskStepType nextStep();

		abstract public TaskType newInstance();

		public TaskType build() {
			then();
			return newInstance();
		}

		public TaskBuilderType talkTo(Interactable interactable) {
			currentStep.interactable = interactable;
			return (TaskBuilderType) this;
		}

		public TaskBuilderType instructions(Function<Dialog, Dialog> instructions) {
			currentStep.dialog = instructions.apply(Dialog.from(currentStep.interactable));
			return (TaskBuilderType) this;
		}

		public TaskBuilderType reminder(Function<Dialog, Dialog> reminder) {
			currentStep.reminder = reminder.apply(Dialog.from(currentStep.interactable));
			return (TaskBuilderType) this;
		}

		public TaskBuilderType reward(QuestReward reward, int amount) {
			// TODO
			return (TaskBuilderType) this;
		}

		public TaskBuilderType then() {
			steps.add(currentStep);
			currentStep = nextStep();
			return (TaskBuilderType) this;
		}
	}

}

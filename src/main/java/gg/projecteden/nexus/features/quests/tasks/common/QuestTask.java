package gg.projecteden.nexus.features.quests.tasks.common;

import gg.projecteden.nexus.features.quests.QuestReward;
import gg.projecteden.nexus.features.quests.interactable.Interactable;
import gg.projecteden.nexus.features.quests.interactable.instructions.Dialog;
import gg.projecteden.nexus.models.quests.Quester;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Data
@NoArgsConstructor
public abstract class QuestTask<
	TaskType extends QuestTask<TaskType, TaskStepType>,
	TaskStepType extends QuestTaskStep<TaskType, TaskStepType>
> {
	protected List<TaskStepType> steps;
	protected List<Consumer<Quester>> rewards = new ArrayList<>();

	public QuestTask(List<TaskStepType> steps) {
		this.steps = steps;
	}

	public static abstract class TaskBuilder<
		TaskType extends QuestTask<TaskType, TaskStepType>,
		TaskBuilderType extends TaskBuilder<TaskType, TaskBuilderType, TaskStepType>,
		TaskStepType extends QuestTaskStep<TaskType, TaskStepType>
	> {
		protected List<TaskStepType> steps = new ArrayList<>();
		protected TaskStepType currentStep = nextStep();
		protected List<Consumer<Quester>> rewards = new ArrayList<>();

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

		public TaskBuilderType dialog(Function<Dialog, Dialog> instructions) {
			currentStep.dialog = instructions.apply(Dialog.from(currentStep.interactable));
			return (TaskBuilderType) this;
		}

		public TaskBuilderType reminder(Function<Dialog, Dialog> reminder) {
			currentStep.reminder = reminder.apply(Dialog.from(currentStep.interactable));
			return (TaskBuilderType) this;
		}

		public TaskBuilderType reward(QuestReward reward) {
			rewards.add(reward::apply);
			return (TaskBuilderType) this;
		}

		public TaskBuilderType reward(QuestReward reward, int amount) {
			rewards.add(quester -> reward.apply(quester, amount));
			return (TaskBuilderType) this;
		}

		public TaskBuilderType then() {
			steps.add(currentStep);
			currentStep = nextStep();
			return (TaskBuilderType) this;
		}
	}

}

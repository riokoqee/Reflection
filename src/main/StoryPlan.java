package main;

import java.util.ArrayList;

final class StoryPlan {

    private StoryPlan() {
    }

    static ArrayList<PlanTask> getTasks(int stage) {
        ArrayList<PlanTask> tasks = new ArrayList<>();
        addTask(tasks, stage, "Заправить кровать",
                StoryManager.STAGE_MAKE_BED, StoryManager.STAGE_MAKE_TEA);
        addTask(tasks, stage, "Убрать посуду на кухне",
                StoryManager.STAGE_MAKE_TEA, StoryManager.STAGE_WASH_FACE);
        addTask(tasks, stage, "Умыться в ванной",
                StoryManager.STAGE_WASH_FACE, StoryManager.STAGE_REST_IN_HALL);
        addTask(tasks, stage, "Включить телевизор и отдохнуть на диване",
                StoryManager.STAGE_REST_IN_HALL, StoryManager.STAGE_SHADOW_FIRST);
        addTask(tasks, stage, "Поговорить с Тенью у зеркала",
                StoryManager.STAGE_SHADOW_FIRST, StoryManager.STAGE_CHILD);
        addTask(tasks, stage, "Найти Ребёнка на качелях",
                StoryManager.STAGE_CHILD, StoryManager.STAGE_FOREST_SHADOW);
        addTask(tasks, stage, "Идти глубже к Тени",
                StoryManager.STAGE_FOREST_SHADOW, StoryManager.STAGE_FRIEND);
        addTask(tasks, stage, "Поговорить с Другом",
                StoryManager.STAGE_FRIEND, StoryManager.STAGE_ELDER);
        addTask(tasks, stage, "Зайти к Старику в библиотеку",
                StoryManager.STAGE_ELDER, StoryManager.STAGE_WARRIOR);
        addTask(tasks, stage, "Подняться к Воину",
                StoryManager.STAGE_WARRIOR, StoryManager.STAGE_DONE);
        return tasks;
    }

    private static void addTask(ArrayList<PlanTask> tasks, int stage, String text,
                                int visibleStage, int completedStage) {
        if (stage >= visibleStage) {
            tasks.add(new PlanTask(text, getCompletedText(visibleStage), stage >= completedStage));
        }
    }

    private static String getCompletedText(int visibleStage) {
        switch (visibleStage) {
            case StoryManager.STAGE_MAKE_BED:
                return "Кровать заправлена. В комнате стало тише.";
            case StoryManager.STAGE_MAKE_TEA:
                return "Посуда убрана. Вода была холоднее обычного.";
            case StoryManager.STAGE_WASH_FACE:
                return "Лицо умыто. В зеркале всё нормально. Наверное.";
            case StoryManager.STAGE_REST_IN_HALL:
                return "Телевизор шумит. Дома не стало спокойнее.";
            case StoryManager.STAGE_SHADOW_FIRST:
                return "Тень знает слишком много. Дальше ведёт лес.";
            case StoryManager.STAGE_CHILD:
                return "Качели скрипят даже после разговора.";
            case StoryManager.STAGE_FOREST_SHADOW:
                return "В лесу стало понятно: сомнение умеет говорить.";
            case StoryManager.STAGE_FRIEND:
                return "Друг услышал больше, чем было сказано.";
            case StoryManager.STAGE_ELDER:
                return "Старик оставил вопрос вместо ответа.";
            case StoryManager.STAGE_WARRIOR:
                return "Вершина достигнута. Осталось посмотреть внутрь.";
            default:
                return "";
        }
    }
}

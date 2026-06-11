package main;

import java.util.ArrayList;

final class StoryMemory {

    private StoryMemory() {
    }

    static ArrayList<MemoryEntry> getEntries(StoryManager story) {
        ArrayList<MemoryEntry> memories = new ArrayList<>();

        add(memories, "memory_bed", "Тихая кровать", "Квартира",
                "Утро началось с простого порядка. В тишине комнаты стало легче услышать себя.",
                story.getStage() >= StoryManager.STAGE_MAKE_TEA);
        add(memories, "memory_phone", "Сообщение от мамы", "Квартира",
                "Телефон напомнил, что связь не всегда требует правильных слов. Иногда достаточно ответить честно.",
                story.phoneEventDone);
        add(memories, "memory_photo", "Старое фото", "Квартира",
                "На снимке осталось время, где улыбки казались проще. Воспоминание болит, но уже не режет.",
                story.photoEventDone);
        add(memories, "memory_mirror", "Первый взгляд", "Квартира",
                "Отражение смотрело спокойнее, чем было внутри. Это был первый разговор без слов.",
                story.mirrorEventDone || story.getStage() >= StoryManager.STAGE_SHADOW_FIRST);
        add(memories, "memory_shadow", "Шорох у зеркала", "Квартира",
                "Тень появилась не как враг, а как часть, которую слишком долго оставляли в темноте.",
                story.getStage() >= StoryManager.STAGE_CHILD);
        add(memories, "memory_lantern", "Потухший фонарь", "Лес Сомнений",
                "Даже слабый свет может указать путь, если не требовать от него быть солнцем.",
                story.lostLanternEventDone);
        add(memories, "memory_bird", "Раненая птица", "Лес Сомнений",
                "Помощь оказалась не контролем, а осторожностью. Не всё хрупкое нужно держать крепче.",
                story.woundedBirdEventDone);
        add(memories, "memory_child", "Качели", "Лес Сомнений",
                "Ребёнок на качелях знал то, что взрослые части пытаются забыть.",
                story.getStage() >= StoryManager.STAGE_FOREST_SHADOW);
        add(memories, "memory_friend", "Друг на площади", "Деревня Связей",
                "Друг услышал больше, чем было сказано. Иногда близость начинается там, где заканчивается защита.",
                story.getStage() >= StoryManager.STAGE_ELDER);
        add(memories, "memory_letter", "Старое письмо", "Деревня Связей",
                "В чужих словах оказалось слишком знакомое чувство. Оно не исчезает, даже если спрятать бумагу.",
                story.oldLetterEventDone);
        add(memories, "memory_help_request", "Просьба о помощи", "Деревня Связей",
                "Чужая просьба проверяла не силу, а готовность заметить вес, который несёт кто-то рядом.",
                story.helpRequestEventDone);
        add(memories, "memory_library", "Тихая библиотека", "Библиотека",
                "Старик не дал готовый ответ. Он оставил вопрос, рядом с которым стало невозможно притворяться.",
                story.getStage() >= StoryManager.STAGE_WARRIOR);
        add(memories, "memory_fork", "Развилка", "Гора Целей",
                "Обе дороги вели вверх, но каждая спрашивала о разном: о спешке или терпении.",
                story.forkEventDone);
        add(memories, "memory_traveler", "Путник", "Гора Целей",
                "Поддержка не всегда звучит громко. Иногда это просто несколько шагов рядом.",
                story.travelerEventDone);
        add(memories, "memory_summit", "Вершина", "Гора Целей",
                "На вершине стало ясно: весь путь проходил внутри, а каждый голос был частью одного отражения.",
                story.getStage() >= StoryManager.STAGE_DONE);

        return memories;
    }

    static String getUnlockTitle(String promptId) {
        switch (promptId) {
            case StoryManager.OPTIONAL_PHONE:
                return "Сообщение от мамы";
            case StoryManager.OPTIONAL_PHOTO:
                return "Старое фото";
            case StoryManager.OPTIONAL_MIRROR:
                return "Первый взгляд";
            case StoryManager.OPTIONAL_LOST_LANTERN:
                return "Потухший фонарь";
            case StoryManager.OPTIONAL_WOUNDED_BIRD:
                return "Раненая птица";
            case StoryManager.OPTIONAL_OLD_LETTER:
                return "Старое письмо";
            case StoryManager.OPTIONAL_HELP_REQUEST:
                return "Просьба о помощи";
            case StoryManager.OPTIONAL_FORK:
                return "Развилка";
            case StoryManager.OPTIONAL_TRAVELER:
                return "Путник";
            default:
                return "";
        }
    }

    private static void add(ArrayList<MemoryEntry> memories, String id, String title, String location,
                            String text, boolean unlocked) {
        memories.add(new MemoryEntry(id, title, location, text, unlocked));
    }
}

package main;

public class StoryManager {

    public static final String SHADOW_APARTMENT = "shadow_apartment";
    public static final String CHILD = "child";
    public static final String SHADOW_FOREST = "shadow_forest";
    public static final String FRIEND = "friend";
    public static final String ELDER = "elder";
    public static final String WARRIOR = "warrior";

    private static final int STAGE_SHADOW_FIRST = 0;
    private static final int STAGE_SHADOW_SECOND = 1;
    private static final int STAGE_CHILD = 2;
    private static final int STAGE_FOREST_SHADOW = 3;
    private static final int STAGE_FRIEND = 4;
    private static final int STAGE_ELDER = 5;
    private static final int STAGE_WARRIOR = 6;
    private static final int STAGE_DONE = 7;

    private final GamePanel gp;
    private StoryPrompt activePrompt;
    private String messageSpeaker = "";
    private String messageText = "";
    private int pendingMap = -1;
    private int pendingCol;
    private int pendingRow;
    private boolean pendingResult = false;

    public int growth;
    public int calm;
    public int empathy;
    public int confidence;
    public int selectedChoice = 0;
    private int stage = STAGE_SHADOW_FIRST;

    public StoryManager(GamePanel gp) {
        this.gp = gp;
        resetMetrics();
    }

    public void startNewGame() {
        resetMetrics();
        stage = STAGE_SHADOW_FIRST;
        clearDialogue();
        gp.currentMap = MapId.APARTMENT;
        gp.hasLantern = false;
        gp.player.setPosition(14, 14);
        gp.player.direction = "down";
        gp.aSetter.setObject();
        gp.aSetter.setNPC();
        gp.gameState = gp.playState;
        gp.ui.commandNum = 0;
    }

    public void loadState(int stage, int growth, int calm, int empathy, int confidence) {
        this.stage = stage;
        this.growth = growth;
        this.calm = calm;
        this.empathy = empathy;
        this.confidence = confidence;
        clearDialogue();
    }

    public int getStage() {
        return stage;
    }

    public void interact(String role) {
        if (stage == STAGE_SHADOW_FIRST && SHADOW_APARTMENT.equals(role)) {
            openShadowFirst();
        }
        else if (stage == STAGE_SHADOW_SECOND && SHADOW_APARTMENT.equals(role)) {
            openShadowSecond();
        }
        else if (stage == STAGE_CHILD && CHILD.equals(role)) {
            openChild();
        }
        else if (stage == STAGE_FOREST_SHADOW && SHADOW_FOREST.equals(role)) {
            openForestShadow();
        }
        else if (stage == STAGE_FRIEND && FRIEND.equals(role)) {
            openFriend();
        }
        else if (stage == STAGE_ELDER && ELDER.equals(role)) {
            openElder();
        }
        else if (stage == STAGE_WARRIOR && WARRIOR.equals(role)) {
            openWarrior();
        }
        else {
            showCurrentHint();
        }
    }

    public void showCurrentHint() {
        switch (stage) {
            case STAGE_SHADOW_FIRST:
            case STAGE_SHADOW_SECOND:
                openMessage("Мысль", "Зеркало будто ждёт, когда ты посмотришь прямо в него.");
                break;
            case STAGE_CHILD:
                openMessage("Лес Сомнений", "На качелях сидит ребёнок. Кажется, он узнал тебя раньше, чем ты его.");
                break;
            case STAGE_FOREST_SHADOW:
                openMessage("Лес Сомнений", "Глубже между деревьями стоит Тень. Разговор ещё не закончен.");
                break;
            case STAGE_FRIEND:
                openMessage("Деревня Связей", "Друг ждёт на лавочке. Этот разговор нельзя обойти стороной.");
                break;
            case STAGE_ELDER:
                openMessage("Деревня Связей", "В библиотеке ждёт Старик. Он спрашивает не из любопытства.");
                break;
            case STAGE_WARRIOR:
                openMessage("Гора Целей", "У костра стоит Воин. Дальше тропа ведёт только вверх.");
                break;
            default:
                openMessage("Внутренний мир", "Путь уже пройден. Осталось только посмотреть на результат.");
                break;
        }
    }

    public boolean hasChoices() {
        return activePrompt != null;
    }

    public StoryPrompt getActivePrompt() {
        return activePrompt;
    }

    public String getMessageSpeaker() {
        return messageSpeaker;
    }

    public String getMessageText() {
        return messageText;
    }

    public void moveChoice(int amount) {
        if (activePrompt == null || activePrompt.choices.length == 0) {
            return;
        }
        selectedChoice += amount;
        if (selectedChoice < 0) {
            selectedChoice = activePrompt.choices.length - 1;
        }
        if (selectedChoice >= activePrompt.choices.length) {
            selectedChoice = 0;
        }
        gp.playCursorSE();
    }

    public void chooseSelected() {
        if (activePrompt == null) {
            continueDialogue();
            return;
        }

        StoryPrompt prompt = activePrompt;
        Choice choice = prompt.choices[selectedChoice];
        growth += choice.growth;
        calm += choice.calm;
        empathy += choice.empathy;
        confidence += choice.confidence;
        clampMetrics();

        if ("shadow_first".equals(prompt.id)) {
            stage = STAGE_SHADOW_SECOND;
            openShadowSecond();
        }
        else if ("shadow_second".equals(prompt.id)) {
            stage = STAGE_CHILD;
            finishChoice("Тень", "Дверь квартиры растворяется и становится порталом. За ней шумит Лес Сомнений.", MapId.FOREST_DOUBTS, 23, 43, false);
        }
        else if ("child".equals(prompt.id)) {
            stage = STAGE_FOREST_SHADOW;
            finishChoice("Ребёнок", "Качели тихо скрипят. Тропа дальше стала видимой.", -1, 0, 0, false);
        }
        else if ("forest_shadow".equals(prompt.id)) {
            stage = STAGE_FRIEND;
            finishChoice("Тень", "Лес расступается. Впереди появляются огни Деревни Связей.", MapId.VILLAGE, 23, 15, false);
        }
        else if ("friend".equals(prompt.id)) {
            stage = STAGE_ELDER;
            finishChoice("Друг", "На площади становится тише. В библиотеке всё ещё горит свет.", -1, 0, 0, false);
        }
        else if ("elder".equals(prompt.id)) {
            stage = STAGE_WARRIOR;
            finishChoice("Старик", "Мост за библиотекой ведёт к Горе Целей.", MapId.MOUNTAIN, 35, 31, false);
        }
        else if ("warrior".equals(prompt.id)) {
            stage = STAGE_DONE;
            finishChoice(
                    "Вершина",
                    "Все пятеро стоят кругом.\n\nСтарик: Ты всё ещё не понял?\nТень: Мы никогда не были снаружи.\nРебёнок: Мы - это ты.\nДруг: Всё, что ты видел... это твоя голова.\nВоин: Ты прошёл по своему собственному разуму.\n\nЭто был твой внутренний мир. Каждый выбор, который ты сделал, похож на выбор, который ты делаешь в реальной жизни.",
                    -1, 0, 0, true
            );
        }
    }

    public void continueDialogue() {
        if (activePrompt != null) {
            return;
        }

        boolean shouldShowResult = pendingResult;
        boolean openedNewLocation = pendingMap != -1;

        if (pendingMap != -1) {
            gp.currentMap = pendingMap;
            gp.player.setPosition(pendingCol, pendingRow);
            gp.player.direction = "down";
            pendingMap = -1;
        }

        clearDialogue();

        if (shouldShowResult) {
            gp.gameState = gp.resultState;
        }
        else {
            gp.gameState = gp.playState;
        }

        gp.saveLoad.save();
        if (openedNewLocation && !shouldShowResult) {
            gp.ui.showCheckpoint(getLocationTitle());
        }
    }

    public String getLocationTitle() {
        switch (gp.currentMap) {
            case MapId.APARTMENT: return "Квартира";
            case MapId.FOREST_DOUBTS: return "Лес Сомнений";
            case MapId.VILLAGE: return "Деревня Связей";
            case MapId.MOUNTAIN: return "Гора Целей";
            default: return "Внутренний мир";
        }
    }

    public String getObjective() {
        switch (stage) {
            case STAGE_SHADOW_FIRST:
            case STAGE_SHADOW_SECOND:
                return "Поговори с Тенью у зеркала";
            case STAGE_CHILD:
                return "Найди Ребёнка на качелях";
            case STAGE_FOREST_SHADOW:
                return "Иди глубже к Тени";
            case STAGE_FRIEND:
                return "Поговори с Другом";
            case STAGE_ELDER:
                return "Зайди к Старику в библиотеку";
            case STAGE_WARRIOR:
                return "Поднимись к Воину";
            default:
                return "Посмотри результат";
        }
    }

    public String getProfileTitle() {
        int average = (growth + calm + empathy + confidence) / 4;
        if (average >= 75) {
            return "Целостный путь";
        }
        if (growth >= calm && growth >= empathy && growth >= confidence) {
            return "Путь роста";
        }
        if (calm >= growth && calm >= empathy && calm >= confidence) {
            return "Путь покоя";
        }
        if (empathy >= growth && empathy >= calm && empathy >= confidence) {
            return "Путь связи";
        }
        return "Путь уверенности";
    }

    public String getProfileText() {
        String title = getProfileTitle();
        if ("Целостный путь".equals(title)) {
            return "Ты часто выбирал честность без жесткости. Внутренние части не исчезли, но начали говорить друг с другом.";
        }
        if ("Путь роста".equals(title)) {
            return "Ты чаще выбирал движение вперёд. Риск в том, чтобы не превращать рост в бегство от усталости.";
        }
        if ("Путь покоя".equals(title)) {
            return "Ты искал тишину и устойчивость. Важно не путать покой с отказом от трудных разговоров.";
        }
        if ("Путь связи".equals(title)) {
            return "Ты замечал других и свои забытые части. Твоя опора появляется через контакт, а не через изоляцию.";
        }
        return "Ты выбирал собранность и силу. Следующий шаг - оставить место не только контролю, но и доверию.";
    }

    public String getRecommendation() {
        int min = Math.min(Math.min(growth, calm), Math.min(empathy, confidence));
        if (min == growth) {
            return "Попробуй чаще спрашивать себя: какой маленький шаг я могу сделать сегодня?";
        }
        if (min == calm) {
            return "Замедляйся перед важными решениями: сначала дыхание, потом ответ.";
        }
        if (min == empathy) {
            return "Возвращайся к людям постепенно: одно честное сообщение лучше долгого молчания.";
        }
        return "Отмечай свои завершённые действия. Уверенность растёт от доказательств, а не от давления.";
    }

    private void openShadowFirst() {
        openPrompt(
                "shadow_first",
                "Тень",
                "Ты наконец-то меня заметил... Я уже давно здесь. Просто ты всегда отворачивался.",
                new Choice[]{
                        choice("Кто ты такой?", 0, 0, 0, 0),
                        choice("Это сон?", 0, 0, 0, 0),
                        choice("Я просто устал...", 0, 0, 0, 0),
                        choice("Оставь меня в покое.", 0, 0, 0, 0)
                }
        );
    }

    private void openShadowSecond() {
        openPrompt(
                "shadow_second",
                "Тень",
                "Я - то, от чего ты убегаешь. Хочешь пойти со мной?",
                new Choice[]{
                        choice("Пойдём.", 15, 8, 0, 0),
                        choice("Расскажи сначала, кто ты.", 0, 0, 12, 10),
                        choice("Я не хочу никуда.", -15, 10, 0, 0),
                        choice("Это всё не по-настоящему.", 0, -10, 0, 8)
                }
        );
    }

    private void openChild() {
        if (empathy >= 40) {
            openPrompt(
                    "child",
                    "Ребёнок",
                    "Я знал, что ты придёшь... Ты меня совсем забыл?",
                    new Choice[]{
                            choice("Прости, я был занят.", 0, -8, 18, 0),
                            choice("Я не бросал тебя.", 15, 0, 0, 10),
                            choice("Давай посидим вместе.", 10, 15, 22, 0)
                    }
            );
        }
        else {
            openPrompt(
                    "child",
                    "Ребёнок",
                    "Ты всегда так говоришь... Ты меня совсем забыл?",
                    new Choice[]{
                            choice("Прости, я был занят.", 0, -12, 8, 0),
                            choice("Кто ты? Я тебя не знаю.", 0, 10, -20, 0),
                            choice("Я не бросал тебя.", 5, 0, -10, 0)
                    }
            );
        }
    }

    private void openForestShadow() {
        openPrompt(
                "forest_shadow",
                "Тень",
                "Видишь? Даже он в тебе разочарован. Сколько ещё будешь прятаться?",
                new Choice[]{
                        choice("Я не прячусь.", 0, -8, 0, 12),
                        choice("Что мне делать?", 18, 0, 12, 0),
                        choice("Я принимаю тебя.", 20, 25, 15, 0),
                        choice("Просто исчезни.", 0, -25, -15, 18)
                }
        );
    }

    private void openFriend() {
        if (empathy >= 55) {
            openPrompt(
                    "friend",
                    "Друг",
                    "Ты наконец-то здесь... Я правда рад тебя видеть. Ты выглядишь так, будто не спал сто лет.",
                    new Choice[]{
                            choice("Всё нормально.", 0, 8, -10, 0),
                            choice("Честно? Мне хреново.", 0, 12, 22, 0),
                            choice("Прости, что пропал.", 15, 0, 25, 0)
                    }
            );
        }
        else {
            openPrompt(
                    "friend",
                    "Друг",
                    "Ты всегда так... отстранённо. Давно не виделись.",
                    new Choice[]{
                            choice("Всё нормально.", 0, 10, -15, 0),
                            choice("Честно? Мне хреново.", 0, 8, 15, 0),
                            choice("Расскажи лучше про себя.", 0, 0, 18, -8),
                            choice("Прости, что пропал.", 12, 0, 10, 0)
                    }
            );
        }
    }

    private void openElder() {
        openPrompt(
                "elder",
                "Старик",
                "Многие приходят сюда... но мало кто остаётся. Что ты ищешь в этом месте?",
                new Choice[]{
                        choice("Силу.", 0, 0, -10, 25),
                        choice("Покой.", 8, 25, 0, 0),
                        choice("Ответы.", 18, 0, 12, 0),
                        choice("Я просто иду дальше.", 10, 10, 10, 10)
                }
        );
    }

    private void openWarrior() {
        if (growth + confidence >= 130) {
            openPrompt(
                    "warrior",
                    "Воин",
                    "Ты дошёл. Немногие доходят так далеко.",
                    new Choice[]{
                            choice("Я готов.", 20, 0, 0, 25),
                            choice("Я устал.", -10, 18, 0, 0),
                            choice("Я возьму всех с собой.", 18, 0, 22, 0)
                    }
            );
        }
        else {
            openPrompt(
                    "warrior",
                    "Воин",
                    "Ты дошёл... но выглядишь так, будто хочешь сдаться на полпути.",
                    new Choice[]{
                            choice("Я готов.", 15, 0, 0, 18),
                            choice("Я устал.", -15, 25, 0, 0),
                            choice("Я уже изменился.", 20, 20, 20, 20)
                    }
            );
        }
    }

    private void openPrompt(String id, String speaker, String text, Choice[] choices) {
        activePrompt = new StoryPrompt(id, speaker, text, choices);
        messageSpeaker = "";
        messageText = "";
        selectedChoice = 0;
        gp.gameState = gp.dialogueState;
    }

    private void openMessage(String speaker, String text) {
        activePrompt = null;
        messageSpeaker = speaker;
        messageText = text;
        gp.gameState = gp.dialogueState;
    }

    private void finishChoice(String speaker, String text, int map, int col, int row, boolean result) {
        activePrompt = null;
        messageSpeaker = speaker;
        messageText = text;
        pendingMap = map;
        pendingCol = col;
        pendingRow = row;
        pendingResult = result;
        gp.gameState = gp.dialogueState;
    }

    private Choice choice(String text, int growth, int calm, int empathy, int confidence) {
        return new Choice(text, growth, calm, empathy, confidence);
    }

    private void resetMetrics() {
        growth = 35;
        calm = 35;
        empathy = 35;
        confidence = 35;
    }

    private void clampMetrics() {
        growth = clamp(growth);
        calm = clamp(calm);
        empathy = clamp(empathy);
        confidence = clamp(confidence);
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(100, value));
    }

    private void clearDialogue() {
        activePrompt = null;
        messageSpeaker = "";
        messageText = "";
        pendingMap = -1;
        pendingResult = false;
        selectedChoice = 0;
    }

    public static class StoryPrompt {
        public final String id;
        public final String speaker;
        public final String text;
        public final Choice[] choices;

        StoryPrompt(String id, String speaker, String text, Choice[] choices) {
            this.id = id;
            this.speaker = speaker;
            this.text = text;
            this.choices = choices;
        }
    }

    public static class Choice {
        public final String text;
        public final int growth;
        public final int calm;
        public final int empathy;
        public final int confidence;

        Choice(String text, int growth, int calm, int empathy, int confidence) {
            this.text = text;
            this.growth = growth;
            this.calm = calm;
            this.empathy = empathy;
            this.confidence = confidence;
        }
    }
}

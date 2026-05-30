package main;

public class StoryManager {

    public static final String SHADOW_APARTMENT = "shadow_apartment";
    public static final String CHILD = "child";
    public static final String SHADOW_FOREST = "shadow_forest";
    public static final String FRIEND = "friend";
    public static final String ELDER = "elder";
    public static final String WARRIOR = "warrior";
    public static final String TRAVELER = "traveler";

    private static final String OPTIONAL_PHONE = "optional_phone";
    private static final String OPTIONAL_PHOTO = "optional_photo";
    private static final String OPTIONAL_MIRROR = "optional_mirror";
    private static final String OPTIONAL_LOST_LANTERN = "optional_lost_lantern";
    private static final String OPTIONAL_WOUNDED_BIRD = "optional_wounded_bird";
    private static final String OPTIONAL_OLD_LETTER = "optional_old_letter";
    private static final String OPTIONAL_HELP_REQUEST = "optional_help_request";
    private static final String OPTIONAL_FORK = "optional_fork";
    private static final String OPTIONAL_TRAVELER = "optional_traveler";

    private static final int STAGE_MAKE_BED = 0;
    private static final int STAGE_MAKE_TEA = 1;
    private static final int STAGE_WASH_FACE = 2;
    private static final int STAGE_REST_IN_HALL = 3;
    private static final int STAGE_SHADOW_FIRST = 4;
    private static final int STAGE_SHADOW_SECOND = 5;
    private static final int STAGE_CHILD = 6;
    private static final int STAGE_FOREST_SHADOW = 7;
    private static final int STAGE_FRIEND = 8;
    private static final int STAGE_ELDER = 9;
    private static final int STAGE_WARRIOR = 10;
    private static final int STAGE_DONE = 11;

    private final GamePanel gp;
    private StoryPrompt activePrompt;
    private String messageSpeaker = "";
    private String messageText = "";
    private boolean phoneResultOpen = false;
    private String phoneResultPlayerText = "";
    private String phoneResultMomText = "";
    private int pendingMap = -1;
    private int pendingCol;
    private int pendingRow;
    private boolean pendingResult = false;
    private int dialogueLockCounter = 0;
    private int dialogueLockTotalFrames = 0;
    private boolean apartmentShadowConversationStarted = false;

    public int growth;
    public int calm;
    public int empathy;
    public int confidence;
    public int responsibility;
    public int avoidance;
    public int selfWorth;
    public int selectedChoice = 0;
    public boolean phoneEventDone = false;
    public boolean photoEventDone = false;
    public boolean mirrorEventDone = false;
    public boolean lostLanternEventDone = false;
    public boolean woundedBirdEventDone = false;
    public boolean oldLetterEventDone = false;
    public boolean helpRequestEventDone = false;
    public boolean forkEventDone = false;
    public boolean travelerEventDone = false;
    private int stage = STAGE_MAKE_BED;

    public StoryManager(GamePanel gp) {
        this.gp = gp;
        resetMetrics();
    }

    public void startNewGame() {
        resetMetrics();
        resetOptionalEvents();
        stage = STAGE_MAKE_BED;
        apartmentShadowConversationStarted = false;
        clearDialogue();
        gp.currentMap = MapId.APARTMENT;
        gp.hasLantern = false;
        gp.bedroomLampOn = false;
        gp.tvOn = false;
        gp.player.setPosition(16, 12);
        gp.player.direction = "down";
        gp.aSetter.setObject();
        gp.aSetter.setNPC();
        gp.gameState = gp.playState;
        gp.ui.commandNum = 0;
    }

    public void update() {
        if (dialogueLockCounter > 0) {
            dialogueLockCounter--;
        }
    }

    public void loadState(int stage, int growth, int calm, int empathy, int confidence) {
        loadState(stage, growth, calm, empathy, confidence, 35, 35, 35);
    }

    public void loadState(int stage, int growth, int calm, int empathy, int confidence,
                          int responsibility, int avoidance, int selfWorth) {
        this.stage = stage;
        this.growth = growth;
        this.calm = calm;
        this.empathy = empathy;
        this.confidence = confidence;
        if (responsibility == 0 && avoidance == 0 && selfWorth == 0) {
            responsibility = 35;
            avoidance = 35;
            selfWorth = 35;
        }
        this.responsibility = responsibility;
        this.avoidance = avoidance;
        this.selfWorth = selfWorth;
        clampMetrics();
        apartmentShadowConversationStarted = stage > STAGE_SHADOW_FIRST;
        clearDialogue();
    }

    public int getStage() {
        return stage;
    }

    public boolean shouldShowApartmentShadow() {
        return stage >= STAGE_SHADOW_FIRST;
    }

    public boolean shouldPlayApartmentWhispers() {
        return shouldShowApartmentShadow() && !apartmentShadowConversationStarted;
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
        else if (TRAVELER.equals(role)) {
            openTravelerEvent();
        }
    }

    public void interactObject(String objectName) {
        if (objectName == null) {
            return;
        }

        if (matchesObject(objectName, "Bedroom Lamp", "Dresser")) {
            toggleBedroomLamp();
        }
        else if (matchesObject(objectName, "TV")) {
            toggleTV();
        }
        else if (matchesObject(objectName, "Phone Message")) {
            openPhoneEvent();
        }
        else if (matchesObject(objectName, "Old Photo")) {
            openPhotoEvent();
        }
        else if (matchesObject(objectName, "Mirror") && !shouldShowApartmentShadow()) {
            openMirrorEvent();
        }
        else if (matchesObject(objectName, "Lost Lantern")) {
            openLostLanternEvent();
        }
        else if (matchesObject(objectName, "Wounded Bird")) {
            openWoundedBirdEvent();
        }
        else if (matchesObject(objectName, "Old Letter")) {
            openOldLetterEvent();
        }
        else if (matchesObject(objectName, "Help Request")) {
            openHelpRequestEvent();
        }
        else if (matchesObject(objectName, "Mountain Fork")) {
            openForkEvent();
        }
        else if (matchesObject(objectName, "Traveler Pack")) {
            openTravelerEvent();
        }
        else if (stage == STAGE_MAKE_BED && matchesObject(objectName, "Bed")) {
            stage = STAGE_MAKE_TEA;
            openTimedMessage("Квартира",
                    "Кровать приведена в порядок. В квартире стало чуть спокойнее. Теперь можно пройти на кухню.",
                    gp.playSEAndGetDurationFrames(Sound.BED_INTERACT));
        }
        else if (stage == STAGE_MAKE_TEA && matchesObject(objectName,
                "Kitchen Stove", "Kitchen Counter Left", "Kitchen Counter Right", "Kitchen Wall Sink")) {
            stage = STAGE_WASH_FACE;
            openTimedMessage("Кухня",
                    "Чайник тихо щелкнул. Тепло от кружки держит руки на месте. Осталось умыться.",
                    gp.playSEAndGetDurationFrames(Sound.KETTLE));
        }
        else if (stage == STAGE_WASH_FACE && matchesObject(objectName, "Bathroom Mirror")) {
            stage = STAGE_REST_IN_HALL;
            openTimedMessage("Ванная",
                    "Холодная вода возвращает ощущение утра. В зале стало необычно тихо.",
                    gp.playSEAndGetDurationFrames(Sound.WATER_SINK));
        }
        else if (stage == STAGE_REST_IN_HALL && matchesObject(objectName, "Sofa")) {
            stage = STAGE_SHADOW_FIRST;
            gp.aSetter.setNPC();
            int sofaFrames = gp.playSEAndGetDurationFrames(Sound.SOFA_SIT);
            int shadowFrames = gp.playSEAndGetDurationFrames(Sound.SHADOW_WHOOSH);
            int lockFrames = Math.max(sofaFrames, shadowFrames);
            gp.player.startSittingOnSofa(lockFrames);
            openTimedMessage("Зал",
                    "Тишина затянулась слишком надолго. Из спальни донесся едва слышный шорох у зеркала.",
                    lockFrames);
        }
        else if (matchesObject(objectName, "Sofa")) {
            gp.playSE(Sound.SOFA_SIT);
            gp.player.sitOnSofaUntilMoved();
        }
        else if (matchesObject(objectName, "Door")) {
            interactApartmentDoor();
        }
        else if (matchesObject(objectName, "Mirror") && shouldShowApartmentShadow()) {
            interact(SHADOW_APARTMENT);
        }
    }

    private void toggleBedroomLamp() {
        gp.bedroomLampOn = !gp.bedroomLampOn;
        gp.playSE(Sound.MENU_CONFIRM);
    }

    private void toggleTV() {
        gp.tvOn = !gp.tvOn;
        gp.playSE(Sound.MENU_CONFIRM);
    }

    private void openPhoneEvent() {
        if (phoneEventDone) {
            openPhoneResult("", "Переписка осталась открытой. Последнее сообщение уже отправлено.");
            return;
        }
        openPrompt(
                OPTIONAL_PHONE,
                "Мама",
                "Мама: Доброе утро. Ты проснулся?\nМама: Вчера ты звучал очень устало.\nМама: Напиши хотя бы пару слов, хорошо?",
                new Choice[]{
                        optionalChoice("Написать: \"Я не очень, но я здесь\".", 0, 0, 14, 0, 12, -8, 8,
                                "Сообщение отправлено. Ответ мамы приходит почти сразу: \"Спасибо, что написал. Я рядом\"."),
                        optionalChoice("Написать: \"Всё нормально, позже отвечу\".", 0, 6, 0, 0, 2, 6, 0,
                                "Сообщение отправлено коротко. Телефон темнеет, оставляя чувство незаконченного разговора."),
                        optionalChoice("Закрыть чат без ответа.", 0, 0, -12, 4, -8, 14, -8,
                                "Экран гаснет. На месте сообщения остаётся тихое напряжение.")
                }
        );
    }

    private void openPhotoEvent() {
        if (photoEventDone) {
            openMessage("Старое фото", "Фотография лежит на месте. Её уже невозможно увидеть впервые.");
            return;
        }
        openPrompt(
                OPTIONAL_PHOTO,
                "Старое фото",
                "На снимке люди улыбаются так, будто тогда всё было проще.",
                new Choice[]{
                        optionalChoice("Рассмотреть внимательнее.", 0, 0, 12, 0, 4, -4, 10,
                                "Ты задерживаешь взгляд. Воспоминание болит, но не ранит так сильно."),
                        optionalChoice("Аккуратно убрать обратно.", 0, 10, 0, 0, 8, -2, 2,
                                "Фотография возвращается на место. Не всё нужно трогать прямо сейчас."),
                        optionalChoice("Скомкать край.", 0, -10, -10, 10, -6, 10, -8,
                                "Бумага хрустит. Злость проходит быстро, а след остаётся.")
                }
        );
    }

    private void openMirrorEvent() {
        if (mirrorEventDone) {
            openMessage("Зеркало", "Отражение больше не кажется случайным. Оно запомнило твой первый взгляд.");
            return;
        }
        openPrompt(
                OPTIONAL_MIRROR,
                "Зеркало",
                "В отражении ты выглядишь спокойнее, чем чувствуешь себя внутри.",
                new Choice[]{
                        optionalChoice("Посмотреть себе в глаза.", 0, 0, 0, 12, 6, -8, 14,
                                "Сначала хочется отвернуться. Потом взгляд становится ровнее."),
                        optionalChoice("Отойти от зеркала.", 0, 6, 0, -8, -4, 10, -8,
                                "Ты делаешь шаг назад. Отражение остаётся там, где было."),
                        optionalChoice("Ударить по отражению ладонью.", 0, -12, 0, 12, -6, 8, -6,
                                "Стекло звенит, но не трескается. Будто оно ждало не силы, а ответа.")
                }
        );
    }

    private void openLostLanternEvent() {
        if (lostLanternEventDone) {
            openMessage("Потухший фонарь", "Фонарь лежит в траве тихо, без прежнего вопроса.");
            return;
        }
        openPrompt(
                OPTIONAL_LOST_LANTERN,
                "Потухший фонарь",
                "На обочине тропы лежит старый фонарь. Его свет почти погас.",
                new Choice[]{
                        optionalChoice("Поднять и поставить на камень.", 10, 0, 0, 0, 12, -8, 0,
                                "Слабый огонёк выпрямляется. Даже маленький свет может указать путь."),
                        optionalChoice("Оставить как есть.", 0, 4, 0, 0, 0, 4, 0,
                                "Ты проходишь мимо. Не каждый найденный предмет должен стать твоей ношей."),
                        optionalChoice("Затушить остаток света.", 0, -12, 0, 8, -8, 12, -4,
                                "Лес становится тише. На секунду кажется, что он смотрит в ответ.")
                }
        );
    }

    private void openWoundedBirdEvent() {
        if (woundedBirdEventDone) {
            openMessage("Раненая птица", "В траве остались только примятые листья.");
            return;
        }
        openPrompt(
                OPTIONAL_WOUNDED_BIRD,
                "Раненая птица",
                "Маленькая птица бьётся крылом у корней. Она не может взлететь.",
                new Choice[]{
                        optionalChoice("Укрыть её под ветками.", 0, 4, 16, 0, 12, -10, 6,
                                "Птица перестаёт метаться. Тропа будто становится мягче под ногами."),
                        optionalChoice("Взять с собой.", 4, -4, -8, 10, 2, 4, 0,
                                "Ты поднимаешь её, но она пугается твоих рук. Помощь не всегда про контроль."),
                        optionalChoice("Пройти мимо.", 0, 0, -14, 4, -8, 14, -4,
                                "Шорох за спиной быстро тонет в лесу. Но ты слышишь его ещё несколько шагов.")
                }
        );
    }

    private void openOldLetterEvent() {
        if (oldLetterEventDone) {
            openMessage("Старое письмо", "Письмо больше не спорит с тобой. Выбор уже сделан.");
            return;
        }
        openPrompt(
                OPTIONAL_OLD_LETTER,
                "Старое письмо",
                "На лавке лежит письмо без адресата. Чернила местами расплылись от дождя.",
                new Choice[]{
                        optionalChoice("Прочитать до конца.", 0, 0, 14, 0, 6, -4, 8,
                                "В чужих словах находится что-то слишком знакомое."),
                        optionalChoice("Спрятать под лавку.", 0, 10, 0, 0, -2, 8, 0,
                                "Письмо исчезает из виду. Иногда покой похож на закрытую дверь."),
                        optionalChoice("Сжечь у фонаря.", 0, -8, -12, 10, -6, 10, -4,
                                "Пепел поднимается легко. Слова исчезают, но смысл нет.")
                }
        );
    }

    private void openHelpRequestEvent() {
        if (helpRequestEventDone) {
            openMessage("Просьба о помощи", "Корзина уже разобрана. На площади стало свободнее.");
            return;
        }
        openPrompt(
                OPTIONAL_HELP_REQUEST,
                "Просьба о помощи",
                "У двери стоит корзина с запиской: \"Помоги донести до площади, если есть силы\".",
                new Choice[]{
                        optionalChoice("Донести корзину.", 0, 0, 16, 0, 16, -10, 4,
                                "Путь короткий, но плечи запоминают вес чужой просьбы."),
                        optionalChoice("Оставить на месте.", 0, 4, 0, 0, -2, 6, 0,
                                "Ты отходишь. Просьба остаётся тихой, без упрёка."),
                        optionalChoice("Оттолкнуть ногой.", 0, -6, -14, 10, -10, 12, -6,
                                "Корзина скребёт по камню. Площадь на мгновение замирает.")
                }
        );
    }

    private void openForkEvent() {
        if (forkEventDone) {
            openMessage("Развилка", "Знак уже не кажется выбором. Ты знаешь, какую дорогу заметил первым.");
            return;
        }
        openPrompt(
                OPTIONAL_FORK,
                "Развилка",
                "Две стрелки смотрят в разные стороны. Одна тропа крутая и короткая, другая длинная и ровная.",
                new Choice[]{
                        optionalChoice("Выбрать короткий подъём.", 12, 0, 0, 12, 4, 2, 4,
                                "Камни скользят под ногами, но вершина кажется ближе."),
                        optionalChoice("Пойти длинной тропой.", 10, 14, 0, 0, 10, -4, 4,
                                "Дорога делает петлю. В этом обходе меньше спешки и больше воздуха.")
                }
        );
    }

    private void openTravelerEvent() {
        if (travelerEventDone) {
            openMessage("Путник", "Путник кивает тебе издалека. На этот раз он справится сам.");
            return;
        }
        openPrompt(
                OPTIONAL_TRAVELER,
                "Путник",
                "У тропы сидит человек с тяжёлым рюкзаком. Он не просит, но идти ему трудно.",
                new Choice[]{
                        optionalChoice("Помочь подняться.", 0, 4, 14, 0, 14, -10, 6,
                                "Он встаёт не сразу. Зато потом делает первый шаг сам."),
                        optionalChoice("Пойти рядом до поворота.", 12, 0, 14, 0, 12, -8, 8,
                                "Вы идёте молча. Иногда поддержка не нуждается в словах."),
                        optionalChoice("Оставить позади.", 0, 0, 0, 12, -6, 12, 0,
                                "Ты ускоряешь шаг. Вершина ближе, но воздух становится холоднее.")
                }
        );
    }

    public void showCurrentHint() {
        switch (stage) {
            case STAGE_MAKE_BED:
                openMessage("Мысль", "Сначала заправь кровать в спальне. Дом должен проснуться раньше, чем день.");
                break;
            case STAGE_MAKE_TEA:
                openMessage("Мысль", "На кухне можно сделать чай. Это обычное дело, но оно держит утро в руках.");
                break;
            case STAGE_WASH_FACE:
                openMessage("Мысль", "Зайди в ванную и умойся. После этого можно будет спокойно выдохнуть.");
                break;
            case STAGE_REST_IN_HALL:
                openMessage("Мысль", "Присядь в зале на диван. Несколько секунд тишины не должны пугать.");
                break;
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

    private void interactApartmentDoor() {
        if (stage < STAGE_SHADOW_FIRST) {
            gp.playSE(Sound.DOOR_CLOSE);
            openMessage("Дверь", "Двор никуда не денется. Сначала нужно закончить утренние дела дома.");
        }
        else if (stage == STAGE_SHADOW_FIRST || stage == STAGE_SHADOW_SECOND) {
            gp.playSE(Sound.DOOR_CLOSE);
            openMessage("Дверь", "Ручка холодная. Кажется, сначала надо вернуться к зеркалу.");
        }
        else {
            gp.playSE(Sound.DOOR_OPEN);
            finishChoice("Дверь", "Ты выходишь во двор. За ним уже шумит Лес Сомнений.", MapId.FOREST_DOUBTS, 23, 43, false);
        }
    }

    private boolean matchesObject(String objectName, String... names) {
        for (String name : names) {
            if (name.equals(objectName)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasChoices() {
        return activePrompt != null;
    }

    public StoryPrompt getActivePrompt() {
        return activePrompt;
    }

    public boolean isPhonePrompt(StoryPrompt prompt) {
        return prompt != null && OPTIONAL_PHONE.equals(prompt.id);
    }

    public boolean isPhoneResultOpen() {
        return phoneResultOpen;
    }

    public String getPhoneResultPlayerText() {
        return phoneResultPlayerText;
    }

    public String getPhoneResultMomText() {
        return phoneResultMomText;
    }

    public String[] getPhoneIntroMessages() {
        return new String[]{
                "Мама: Доброе утро. Ты проснулся?",
                "Мама: Вчера ты звучал очень устало.",
                "Мама: Напиши хотя бы пару слов, хорошо?"
        };
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
        responsibility += choice.responsibility;
        avoidance += choice.avoidance;
        selfWorth += choice.selfWorth;
        clampMetrics();

        if (isOptionalPrompt(prompt.id)) {
            finishOptionalPrompt(prompt.id, choice);
        }
        else if ("shadow_first".equals(prompt.id)) {
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

    private boolean isOptionalPrompt(String id) {
        return OPTIONAL_PHONE.equals(id) ||
                OPTIONAL_PHOTO.equals(id) ||
                OPTIONAL_MIRROR.equals(id) ||
                OPTIONAL_LOST_LANTERN.equals(id) ||
                OPTIONAL_WOUNDED_BIRD.equals(id) ||
                OPTIONAL_OLD_LETTER.equals(id) ||
                OPTIONAL_HELP_REQUEST.equals(id) ||
                OPTIONAL_FORK.equals(id) ||
                OPTIONAL_TRAVELER.equals(id);
    }

    private void finishOptionalPrompt(String id, Choice choice) {
        if (OPTIONAL_PHONE.equals(id)) {
            phoneEventDone = true;
            openPhoneResult(selectedChoice);
            return;
        }
        else if (OPTIONAL_PHOTO.equals(id)) {
            photoEventDone = true;
        }
        else if (OPTIONAL_MIRROR.equals(id)) {
            mirrorEventDone = true;
        }
        else if (OPTIONAL_LOST_LANTERN.equals(id)) {
            lostLanternEventDone = true;
        }
        else if (OPTIONAL_WOUNDED_BIRD.equals(id)) {
            woundedBirdEventDone = true;
        }
        else if (OPTIONAL_OLD_LETTER.equals(id)) {
            oldLetterEventDone = true;
        }
        else if (OPTIONAL_HELP_REQUEST.equals(id)) {
            helpRequestEventDone = true;
        }
        else if (OPTIONAL_FORK.equals(id)) {
            forkEventDone = true;
        }
        else if (OPTIONAL_TRAVELER.equals(id)) {
            travelerEventDone = true;
        }

        activePrompt = null;
        clearPhoneResult();
        messageSpeaker = "Внутренний отклик";
        messageText = choice.resultText;
        dialogueLockCounter = 0;
        dialogueLockTotalFrames = 0;
        pendingMap = -1;
        pendingResult = false;
        selectedChoice = 0;
        gp.gameState = gp.dialogueState;
    }

    private void openPhoneResult(int replyIndex) {
        String playerText = "";
        String momText;

        if (replyIndex == 0) {
            playerText = "Я не очень, но я здесь.";
            momText = "Спасибо, что написал. Я рядом.";
        }
        else if (replyIndex == 1) {
            playerText = "Всё нормально, позже отвечу.";
            momText = "Хорошо. Только не пропадай совсем, ладно?";
        }
        else {
            momText = "Я не буду давить. Просто знай, что я рядом.";
        }

        openPhoneResult(playerText, momText);
    }

    private void openPhoneResult(String playerText, String momText) {
        activePrompt = null;
        phoneResultOpen = true;
        phoneResultPlayerText = playerText == null ? "" : playerText;
        phoneResultMomText = momText == null ? "" : momText;
        messageSpeaker = "";
        messageText = "";
        dialogueLockCounter = 0;
        dialogueLockTotalFrames = 0;
        pendingMap = -1;
        pendingResult = false;
        selectedChoice = 0;
        gp.gameState = gp.dialogueState;
    }

    public void continueDialogue() {
        if (!canContinueDialogue()) {
            return;
        }

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

    public boolean canContinueDialogue() {
        return dialogueLockCounter <= 0;
    }

    public boolean isDialogueLocked() {
        return dialogueLockCounter > 0;
    }

    public float getDialogueLockProgress() {
        if (dialogueLockTotalFrames <= 0) {
            return 1f;
        }
        float progress = 1f - dialogueLockCounter / (float) dialogueLockTotalFrames;
        return Math.max(0f, Math.min(1f, progress));
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
            case STAGE_MAKE_BED:
                return "Заправь кровать в спальне";
            case STAGE_MAKE_TEA:
                return "Завари чай на кухне";
            case STAGE_WASH_FACE:
                return "Умойся в ванной";
            case STAGE_REST_IN_HALL:
                return "Присядь в зале";
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
        if (avoidance >= 72 && responsibility <= 45) {
            return "Избегающий тип";
        }
        if (selfWorth <= 28 && calm <= 48) {
            return "Тревожный тип";
        }
        if (responsibility >= 72 && calm >= 62) {
            return "Перфекционист";
        }
        if (empathy >= 70 && responsibility >= 58) {
            return "Заботливый тип";
        }
        if (growth >= 70 && selfWorth >= 55) {
            return "Исследователь";
        }
        if (confidence >= 70 && responsibility >= 48) {
            return "Лидер";
        }
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
        if ("Заботливый тип".equals(title)) {
            return "Ты часто замечал тех, кому трудно, и не проходил мимо. Важно помнить: забота сильнее, когда в ней остаётся место для себя.";
        }
        if ("Исследователь".equals(title)) {
            return "Ты выбирал движение и готовность смотреть на новое. Твоя сила - любопытство, но ему нужен якорь, чтобы не стать бегством.";
        }
        if ("Лидер".equals(title)) {
            return "Ты склонен брать направление на себя и действовать, когда другие сомневаются. Следующий шаг - слышать не только цель, но и людей рядом.";
        }
        if ("Избегающий тип".equals(title)) {
            return "Ты часто выбирал дистанцию вместо прямого ответа. Это защищает от боли, но постепенно делает мир уже.";
        }
        if ("Тревожный тип".equals(title)) {
            return "Ты много прислушивался к угрозам и сомнениям. Осторожность помогает выжить, но ей нельзя отдавать весь голос.";
        }
        if ("Перфекционист".equals(title)) {
            return "Ты выбирал порядок, контроль и завершённость. Это даёт опору, пока не превращает каждый шаг в экзамен.";
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
        String title = getProfileTitle();
        if ("Избегающий тип".equals(title)) {
            return "Выбери один маленький разговор, который давно откладываешь, и начни его без требования решить всё сразу.";
        }
        if ("Тревожный тип".equals(title)) {
            return "Перед выбором отделяй факт от страха: что точно происходит, а что только звучит как угроза?";
        }
        if ("Перфекционист".equals(title)) {
            return "Оставляй одно дело в состоянии \"достаточно хорошо\". Это тренирует доверие к себе, а не отказ от качества.";
        }
        if ("Заботливый тип".equals(title)) {
            return "Помогая другим, заранее называй границу: сколько сил и времени ты действительно можешь дать.";
        }
        if ("Исследователь".equals(title)) {
            return "Записывай, зачем ты идёшь вперёд. Так рост остаётся выбором, а не автоматическим побегом.";
        }
        if ("Лидер".equals(title)) {
            return "Перед сильным решением задай один вопрос тому, кто идёт рядом. Это не снижает уверенность, а уточняет её.";
        }
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
        apartmentShadowConversationStarted = true;
        gp.stopWhispers();
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
        clearPhoneResult();
        messageSpeaker = "";
        messageText = "";
        dialogueLockCounter = 0;
        dialogueLockTotalFrames = 0;
        selectedChoice = 0;
        gp.gameState = gp.dialogueState;
    }

    private void openMessage(String speaker, String text) {
        activePrompt = null;
        clearPhoneResult();
        messageSpeaker = speaker;
        messageText = text;
        dialogueLockCounter = 0;
        dialogueLockTotalFrames = 0;
        gp.gameState = gp.dialogueState;
    }

    private void openTimedMessage(String speaker, String text, int lockFrames) {
        activePrompt = null;
        clearPhoneResult();
        messageSpeaker = speaker;
        messageText = text;
        dialogueLockCounter = Math.max(0, lockFrames);
        dialogueLockTotalFrames = dialogueLockCounter;
        gp.gameState = gp.dialogueState;
    }

    private void finishChoice(String speaker, String text, int map, int col, int row, boolean result) {
        activePrompt = null;
        clearPhoneResult();
        messageSpeaker = speaker;
        messageText = text;
        dialogueLockCounter = 0;
        dialogueLockTotalFrames = 0;
        pendingMap = map;
        pendingCol = col;
        pendingRow = row;
        pendingResult = result;
        gp.gameState = gp.dialogueState;
    }

    private Choice choice(String text, int growth, int calm, int empathy, int confidence) {
        return new Choice(text, growth, calm, empathy, confidence);
    }

    private Choice optionalChoice(String text, int growth, int calm, int empathy, int confidence,
                                  int responsibility, int avoidance, int selfWorth, String resultText) {
        return new Choice(text, growth, calm, empathy, confidence, responsibility, avoidance, selfWorth, resultText);
    }

    private void resetMetrics() {
        growth = 35;
        calm = 35;
        empathy = 35;
        confidence = 35;
        responsibility = 35;
        avoidance = 35;
        selfWorth = 35;
    }

    private void resetOptionalEvents() {
        phoneEventDone = false;
        photoEventDone = false;
        mirrorEventDone = false;
        lostLanternEventDone = false;
        woundedBirdEventDone = false;
        oldLetterEventDone = false;
        helpRequestEventDone = false;
        forkEventDone = false;
        travelerEventDone = false;
    }

    private void clampMetrics() {
        growth = clamp(growth);
        calm = clamp(calm);
        empathy = clamp(empathy);
        confidence = clamp(confidence);
        responsibility = clamp(responsibility);
        avoidance = clamp(avoidance);
        selfWorth = clamp(selfWorth);
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(100, value));
    }

    private void clearDialogue() {
        activePrompt = null;
        messageSpeaker = "";
        messageText = "";
        clearPhoneResult();
        dialogueLockCounter = 0;
        dialogueLockTotalFrames = 0;
        pendingMap = -1;
        pendingResult = false;
        selectedChoice = 0;
    }

    private void clearPhoneResult() {
        phoneResultOpen = false;
        phoneResultPlayerText = "";
        phoneResultMomText = "";
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
        public final int responsibility;
        public final int avoidance;
        public final int selfWorth;
        public final String resultText;

        Choice(String text, int growth, int calm, int empathy, int confidence) {
            this(text, growth, calm, empathy, confidence, 0, 0, 0, "");
        }

        Choice(String text, int growth, int calm, int empathy, int confidence,
               int responsibility, int avoidance, int selfWorth, String resultText) {
            this.text = text;
            this.growth = growth;
            this.calm = calm;
            this.empathy = empathy;
            this.confidence = confidence;
            this.responsibility = responsibility;
            this.avoidance = avoidance;
            this.selfWorth = selfWorth;
            this.resultText = resultText;
        }
    }
}

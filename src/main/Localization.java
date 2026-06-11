package main;

import java.util.HashMap;
import java.util.Map;

public final class Localization {

    private static final Map<String, String> ENGLISH = new HashMap<>();

    static {
        put("путь через страх, память и выбор", "a path through fear, memory, and choice");
        put("ПРОДОЛЖИТЬ", "CONTINUE");
        put("НОВАЯ ИГРА", "NEW GAME");
        put("НАСТРОЙКИ", "SETTINGS");
        put("ВЫЙТИ", "EXIT");
        put("Выберите слот для новой игры", "Choose a slot for a new game");
        put("Выберите сохранение", "Choose a save");
        put("СЛОТ", "SLOT");
        put("ПЕРЕЗАПИСАТЬ", "OVERWRITE");
        put("ЗАГРУЗИТЬ", "LOAD");
        put("ПУСТОЙ", "EMPTY");
        put("НАЗАД", "BACK");
        put("ПАУЗА", "PAUSE");
        put("СОХРАНИТЬ", "SAVE");
        put("В ГЛАВНОЕ МЕНЮ", "MAIN MENU");
        put("Esc - вернуться    Enter - выбрать", "Esc - back    Enter - select");
        put("Графика", "Graphics");
        put("Звук", "Sound");
        put("Чат", "Chat");
        put("Язык", "Language");
        put("Полный экран", "Fullscreen");
        put("Яркость", "Brightness");
        put("Показ FPS", "Show FPS");
        put("Музыка", "Music");
        put("Звуки", "Sounds");
        put("Эффекты", "Effects");
        put("Окружение", "Ambience");
        put("Шаги", "Footsteps");
        put("Интерфейс", "Interface");
        put("Шепоты", "Whispers");
        put("Размер текста", "Text size");
        put("Скорость текста", "Text speed");
        put("Высокий контраст", "High contrast");
        put("Q / Tab - вкладки, стрелки - значение, E / Enter - выбрать",
                "Q / Tab - tabs, arrows - value, E / Enter - select");
        put("ВКЛ", "ON");
        put("ВЫКЛ", "OFF");
        put("Без лимита", "Unlimited");
        put("Малый", "Small");
        put("Обычный", "Normal");
        put("Крупный", "Large");
        put("Медленно", "Slow");
        put("Обычно", "Normal");
        put("Быстро", "Fast");
        put("Мгновенно", "Instant");
        put("Русский", "Russian");
        put("Английский", "English");

        put("Квартира", "Apartment");
        put("Спальня", "Bedroom");
        put("Кухня", "Kitchen");
        put("Ванная", "Bathroom");
        put("Зал", "Living Room");
        put("Коридор", "Corridor");
        put("Комод", "Dresser");
        put("Старое фото", "Old Photo");
        put("Зеркало", "Mirror");
        put("Потухший фонарь", "Dead Lantern");
        put("Раненая птица", "Wounded Bird");
        put("Старое письмо", "Old Letter");
        put("Просьба о помощи", "A Request for Help");
        put("Развилка", "Fork in the Path");
        put("Путник", "Traveler");
        put("Мысль", "Thought");
        put("Дверь", "Door");
        put("Тень", "Shadow");
        put("Ребёнок", "Child");
        put("Друг", "Friend");
        put("Старик", "Elder");
        put("Воин", "Warrior");
        put("Вершина", "Summit");
        put("Внутренний отклик", "Inner Response");
        put("Мама", "Mom");
        put("Лес Сомнений", "Forest of Doubt");
        put("Деревня Связей", "Village of Bonds");
        put("Гора Целей", "Mountain of Goals");
        put("Библиотека", "Library");
        put("Внутренний мир", "Inner World");

        put("Рост", "Growth");
        put("Покой", "Calm");
        put("Эмпатия", "Empathy");
        put("Увер.", "Conf.");
        put("Уверенность", "Confidence");
        put("Контрольная точка", "Checkpoint");
        put("Сохранение...", "Saving...");
        put("Записка", "Note");
        put("План на утро", "Morning Plan");
        put("Новая запись", "New Entry");
        put("Быстрые подсказки", "Quick Tips");
        put("WASD / стрелки", "WASD / arrows");
        put("ходьба", "walk");
        put("бег", "run");
        put("взаимодействовать", "interact");
        put("список задач", "task list");
        put("Воспоминания", "Memories");
        put("задачи и воспоминания", "tasks and memories");
        put("Они появятся после важных предметов и разговоров.",
                "They will appear after important objects and conversations.");
        put("Тихая кровать", "Quiet Bed");
        put("Сообщение от мамы", "Message from Mom");
        put("Первый взгляд", "First Look");
        put("Шорох у зеркала", "Rustle by the Mirror");
        put("Потухший фонарь", "Dead Lantern");
        put("Качели", "The Swing");
        put("Друг на площади", "Friend in the Square");
        put("Тихая библиотека", "Quiet Library");
        put("Вершина", "Summit");
        put("Утро началось с простого порядка. В тишине комнаты стало легче услышать себя.",
                "The morning began with simple order. In the room's quiet, it became easier to hear yourself.");
        put("Телефон напомнил, что связь не всегда требует правильных слов. Иногда достаточно ответить честно.",
                "The phone reminded you that connection does not always need perfect words. Sometimes an honest answer is enough.");
        put("На снимке осталось время, где улыбки казались проще. Воспоминание болит, но уже не режет.",
                "The photo kept a time where smiles seemed simpler. The memory hurts, but no longer cuts.");
        put("Отражение смотрело спокойнее, чем было внутри. Это был первый разговор без слов.",
                "The reflection looked calmer than it felt inside. It was the first conversation without words.");
        put("Тень появилась не как враг, а как часть, которую слишком долго оставляли в темноте.",
                "The Shadow appeared not as an enemy, but as a part left in the dark for too long.");
        put("Даже слабый свет может указать путь, если не требовать от него быть солнцем.",
                "Even a weak light can show the way if you do not demand that it become the sun.");
        put("Помощь оказалась не контролем, а осторожностью. Не всё хрупкое нужно держать крепче.",
                "Help turned out not to be control, but care. Not every fragile thing needs to be held tighter.");
        put("Ребёнок на качелях знал то, что взрослые части пытаются забыть.",
                "The Child on the swing knew what the older parts try to forget.");
        put("Друг услышал больше, чем было сказано. Иногда близость начинается там, где заканчивается защита.",
                "The Friend heard more than was said. Sometimes closeness begins where defense ends.");
        put("В чужих словах оказалось слишком знакомое чувство. Оно не исчезает, даже если спрятать бумагу.",
                "Someone else's words held a feeling that was too familiar. It does not vanish even if the paper is hidden.");
        put("Чужая просьба проверяла не силу, а готовность заметить вес, который несёт кто-то рядом.",
                "Someone else's request tested not strength, but the willingness to notice the weight carried nearby.");
        put("Старик не дал готовый ответ. Он оставил вопрос, рядом с которым стало невозможно притворяться.",
                "The Elder did not give a ready answer. He left a question beside which pretending became impossible.");
        put("Обе дороги вели вверх, но каждая спрашивала о разном: о спешке или терпении.",
                "Both roads led upward, but each asked about something different: haste or patience.");
        put("Поддержка не всегда звучит громко. Иногда это просто несколько шагов рядом.",
                "Support is not always loud. Sometimes it is just a few steps beside someone.");
        put("На вершине стало ясно: весь путь проходил внутри, а каждый голос был частью одного отражения.",
                "At the summit it became clear: the whole path was inside, and every voice was part of one reflection.");
        put("Подсказка исчезнет сама", "This hint will fade on its own");
        put("РЕЗУЛЬТАТ", "RESULT");
        put("Рекомендация", "Recommendation");
        put("ПРОЙТИ ЕЩЁ РАЗ", "PLAY AGAIN");
        put("сообщение от мамы", "message from mom");
        put("чат закрыт без ответа", "chat closed without an answer");
        put("Игра сохранена", "Game saved");
        put("Сохранение не найдено", "Save not found");
        put("Этот слот пуст", "This slot is empty");

        put("Во время игры отвечай на вопросы искренне и честно. Reflection реагирует не на правильность, а на твой выбор.",
                "During the game, answer sincerely and honestly. Reflection does not react to being right, but to your choice.");

        put("Заправь кровать в спальне", "Make the bed in the bedroom");
        put("Убери посуду в раковине на кухне", "Clear the dishes in the kitchen sink");
        put("Умойся в ванной", "Wash your face in the bathroom");
        put("Включи телевизор в зале и отдохни на диване",
                "Turn on the TV in the living room and rest on the sofa");
        put("Поговори с Тенью у зеркала", "Talk to the Shadow by the mirror");
        put("Найди Ребёнка на качелях", "Find the Child on the swing");
        put("Иди глубже к Тени", "Go deeper toward the Shadow");
        put("Поговори с Другом", "Talk to the Friend");
        put("Зайди к Старику в библиотеку", "Visit the Elder in the library");
        put("Поднимись к Воину", "Climb to the Warrior");
        put("Посмотри результат", "View the result");
        put("Заправить кровать", "Make the bed");
        put("Убрать посуду на кухне", "Clear the dishes in the kitchen");
        put("Умыться в ванной", "Wash your face in the bathroom");
        put("Включить телевизор и отдохнуть на диване", "Turn on the TV and rest on the sofa");
        put("Поговорить с Тенью у зеркала", "Talk to the Shadow by the mirror");
        put("Найти Ребёнка на качелях", "Find the Child on the swing");
        put("Идти глубже к Тени", "Go deeper toward the Shadow");
        put("Поговорить с Другом", "Talk to the Friend");
        put("Зайти к Старику в библиотеку", "Visit the Elder in the library");
        put("Подняться к Воину", "Climb to the Warrior");
        put("Кровать заправлена. В комнате стало тише.", "The bed is made. The room feels quieter.");
        put("Посуда убрана. Вода была холоднее обычного.", "The dishes are cleared. The water was colder than usual.");
        put("Лицо умыто. В зеркале всё нормально. Наверное.",
                "Your face is washed. Everything looks normal in the mirror. Probably.");
        put("Телевизор шумит. Дома не стало спокойнее.",
                "The TV is making noise. Home does not feel calmer.");
        put("Тень знает слишком много. Дальше ведёт лес.",
                "The Shadow knows too much. The forest lies ahead.");
        put("Качели скрипят даже после разговора.", "The swing keeps creaking after the conversation.");
        put("В лесу стало понятно: сомнение умеет говорить.",
                "In the forest it became clear: doubt knows how to speak.");
        put("Друг услышал больше, чем было сказано.", "The Friend heard more than was said.");
        put("Старик оставил вопрос вместо ответа.", "The Elder left a question instead of an answer.");
        put("Вершина достигнута. Осталось посмотреть внутрь.",
                "The summit is reached. Now there is only looking inward.");

        put("Кровать приведена в порядок. В квартире стало чуть спокойнее. Теперь можно пройти на кухню и убрать посуду.",
                "The bed is in order. The apartment feels a little calmer. Now you can go to the kitchen and clear the dishes.");
        put("Тарелки больше не громоздятся в раковине. Вода смывает липкий шум утра, и кухня наконец выглядит живой.",
                "The plates no longer pile up in the sink. Water rinses away the sticky noise of the morning, and the kitchen finally feels alive.");
        put("Холодная вода возвращает ощущение утра. В зале стало необычно тихо.",
                "Cold water brings back the feeling of morning. The living room has become unusually quiet.");
        put("Сначала стоит включить телевизор. Пусть в комнате появится хоть какой-то обычный шум.",
                "You should turn on the TV first. Let some ordinary noise enter the room.");
        put("Тишина затянулась слишком надолго. Из спальни донесся едва слышный шорох у зеркала.",
                "The silence has lasted too long. From the bedroom comes a barely audible rustle near the mirror.");
        put("Ящик выдвигается с тихим щелчком. Внутри лежит телефон, и экран сразу загорается сообщением от мамы.",
                "The drawer slides open with a soft click. A phone lies inside, and the screen immediately lights up with a message from Mom.");
        put("Переписка осталась открытой. Последнее сообщение уже отправлено.",
                "The chat is still open. The last message has already been sent.");

        put("Мама: Доброе утро. Ты уже не спишь?\nМама: Вчера голос был совсем уставшим.\nМама: Напиши хотя бы пару слов, хорошо?",
                "Mom: Good morning. Are you awake?\nMom: Your voice sounded so tired yesterday.\nMom: Write at least a couple of words, okay?");
        put("Мама: Доброе утро. Ты уже не спишь?", "Mom: Good morning. Are you awake?");
        put("Мама: Вчера голос был совсем уставшим.", "Mom: Your voice sounded so tired yesterday.");
        put("Мама: Напиши хотя бы пару слов, хорошо?", "Mom: Write at least a couple of words, okay?");
        put("Написать: \"Я не очень, но я здесь\".", "Write: \"I am not great, but I am here.\"");
        put("Написать: \"Всё нормально, позже отвечу\".", "Write: \"Everything is fine, I will answer later.\"");
        put("Закрыть чат без ответа.", "Close the chat without answering.");
        put("Сообщение отправлено. Ответ мамы приходит почти сразу: \"Спасибо, что написал. Я рядом\".",
                "Message sent. Mom replies almost immediately: \"Thank you for writing. I am here.\"");
        put("Сообщение отправлено коротко. Телефон темнеет, оставляя чувство незаконченного разговора.",
                "The message is sent briefly. The phone dims, leaving the feeling of an unfinished conversation.");
        put("Экран гаснет. На месте сообщения остаётся тихое напряжение.",
                "The screen goes dark. A quiet tension remains where the message was.");
        put("Я не очень, но я здесь.", "I am not great, but I am here.");
        put("Спасибо, что написал. Я рядом.", "Thank you for writing. I am here.");
        put("Всё нормально, позже отвечу.", "Everything is fine, I will answer later.");
        put("Хорошо. Только не пропадай совсем, ладно?", "Okay. Just do not disappear completely, alright?");
        put("Я не буду давить. Просто знай, что я рядом.", "I will not push. Just know that I am here.");

        put("Фотография лежит на месте. Её уже невозможно увидеть впервые.",
                "The photo is still there. You can no longer see it for the first time.");
        put("На снимке люди улыбаются так, будто тогда всё было проще.",
                "In the picture, people smile as if everything was simpler then.");
        put("Рассмотреть внимательнее.", "Look closer.");
        put("Аккуратно убрать обратно.", "Carefully put it back.");
        put("Скомкать край.", "Crumple the edge.");
        put("Ты задерживаешь взгляд. Воспоминание болит, но не ранит так сильно.",
                "You hold your gaze. The memory hurts, but it does not wound as sharply.");
        put("Фотография возвращается на место. Не всё нужно трогать прямо сейчас.",
                "The photo returns to its place. Not everything has to be touched right now.");
        put("Бумага хрустит. Злость проходит быстро, а след остаётся.",
                "The paper crackles. Anger passes quickly, but the mark remains.");
        put("Отражение больше не кажется случайным. Оно запомнило твой первый взгляд.",
                "The reflection no longer feels accidental. It remembered your first look.");
        put("В отражении ты выглядишь спокойнее, чем чувствуешь себя внутри.",
                "In the reflection, you look calmer than you feel inside.");
        put("Посмотреть себе в глаза.", "Look yourself in the eyes.");
        put("Отойти от зеркала.", "Step away from the mirror.");
        put("Ударить по отражению ладонью.", "Strike the reflection with your palm.");
        put("Сначала хочется отвернуться. Потом взгляд становится ровнее.",
                "At first you want to look away. Then your gaze steadies.");
        put("Ты делаешь шаг назад. Отражение остаётся там, где было.",
                "You take a step back. The reflection stays where it was.");
        put("Стекло звенит, но не трескается. Будто оно ждало не силы, а ответа.",
                "The glass rings, but does not crack. As if it was waiting not for force, but for an answer.");

        put("Фонарь лежит в траве тихо, без прежнего вопроса.",
                "The lantern lies quietly in the grass, without its former question.");
        put("На обочине тропы лежит старый фонарь. Его свет почти погас.",
                "An old lantern lies by the path. Its light is almost gone.");
        put("Поднять и поставить на камень.", "Pick it up and place it on a stone.");
        put("Оставить как есть.", "Leave it as it is.");
        put("Затушить остаток света.", "Put out the last of the light.");
        put("Слабый огонёк выпрямляется. Даже маленький свет может указать путь.",
                "The weak flame steadies. Even a small light can point the way.");
        put("Ты проходишь мимо. Не каждый найденный предмет должен стать твоей ношей.",
                "You walk past. Not every found thing has to become your burden.");
        put("Лес становится тише. На секунду кажется, что он смотрит в ответ.",
                "The forest grows quieter. For a second, it feels like it is looking back.");
        put("В траве остались только примятые листья.", "Only flattened leaves remain in the grass.");
        put("Маленькая птица бьётся крылом у корней. Она не может взлететь.",
                "A small bird beats its wing near the roots. It cannot fly.");
        put("Укрыть её под ветками.", "Cover it with branches.");
        put("Взять с собой.", "Take it with you.");
        put("Пройти мимо.", "Walk past.");
        put("Птица перестаёт метаться. Тропа будто становится мягче под ногами.",
                "The bird stops thrashing. The path seems softer under your feet.");
        put("Ты поднимаешь её, но она пугается твоих рук. Помощь не всегда про контроль.",
                "You pick it up, but it is frightened by your hands. Help is not always about control.");
        put("Шорох за спиной быстро тонет в лесу. Но ты слышишь его ещё несколько шагов.",
                "The rustle behind you quickly sinks into the forest. But you hear it for several more steps.");

        put("Письмо больше не спорит с тобой. Выбор уже сделан.",
                "The letter no longer argues with you. The choice has already been made.");
        put("На лавке лежит письмо без адресата. Чернила местами расплылись от дождя.",
                "A letter without an addressee lies on the bench. Rain has blurred some of the ink.");
        put("Прочитать до конца.", "Read it to the end.");
        put("Спрятать под лавку.", "Hide it under the bench.");
        put("Сжечь у фонаря.", "Burn it by the lantern.");
        put("В чужих словах находится что-то слишком знакомое.",
                "Something too familiar is hidden in someone else's words.");
        put("Письмо исчезает из виду. Иногда покой похож на закрытую дверь.",
                "The letter disappears from view. Sometimes calm looks like a closed door.");
        put("Пепел поднимается легко. Слова исчезают, но смысл нет.",
                "Ash rises easily. The words vanish, but the meaning does not.");
        put("Корзина уже разобрана. На площади стало свободнее.",
                "The basket has already been dealt with. The square feels more open.");
        put("У двери стоит корзина с запиской: \"Помоги донести до площади, если есть силы\".",
                "A basket with a note stands by the door: \"Help carry this to the square if you have the strength.\"");
        put("Донести корзину.", "Carry the basket.");
        put("Оставить на месте.", "Leave it there.");
        put("Оттолкнуть ногой.", "Push it away with your foot.");
        put("Путь короткий, но плечи запоминают вес чужой просьбы.",
                "The path is short, but your shoulders remember the weight of someone else's request.");
        put("Ты отходишь. Просьба остаётся тихой, без упрёка.",
                "You step away. The request stays quiet, without reproach.");
        put("Корзина скребёт по камню. Площадь на мгновение замирает.",
                "The basket scrapes against stone. The square freezes for a moment.");
        put("Знак уже не кажется выбором. Одна дорога первой бросилась в глаза.",
                "The sign no longer feels like a choice. One path caught your eye first.");
        put("Две стрелки смотрят в разные стороны. Одна тропа крутая и короткая, другая длинная и ровная.",
                "Two arrows point in different directions. One path is steep and short, the other long and even.");
        put("Выбрать короткий подъём.", "Choose the short climb.");
        put("Пойти длинной тропой.", "Take the long trail.");
        put("Камни скользят под ногами, но вершина кажется ближе.",
                "Stones slide underfoot, but the summit seems closer.");
        put("Дорога делает петлю. В этом обходе меньше спешки и больше воздуха.",
                "The road loops around. This detour has less hurry and more air.");
        put("Путник кивает тебе издалека. На этот раз он справится сам.",
                "The traveler nods to you from afar. This time they will manage alone.");
        put("У тропы сидит человек с тяжёлым рюкзаком. Он не просит, но идти ему трудно.",
                "A person with a heavy backpack sits by the trail. They do not ask, but walking is hard for them.");
        put("Помочь подняться.", "Help them stand.");
        put("Пойти рядом до поворота.", "Walk beside them until the turn.");
        put("Оставить позади.", "Leave them behind.");
        put("Он встаёт не сразу. Зато потом делает первый шаг сам.",
                "They do not stand right away. But then they take the first step on their own.");
        put("Вы идёте молча. Иногда поддержка не нуждается в словах.",
                "You walk in silence. Sometimes support does not need words.");
        put("Ты ускоряешь шаг. Вершина ближе, но воздух становится холоднее.",
                "You quicken your pace. The summit is closer, but the air grows colder.");

        put("Сначала заправь кровать в спальне. Дом должен проснуться раньше, чем день.",
                "First make the bed in the bedroom. The home should wake before the day does.");
        put("На кухне в раковине осталась грязная посуда. Убери её, пока день не стал тяжелее.",
                "There are dirty dishes left in the kitchen sink. Clear them before the day grows heavier.");
        put("Зайди в ванную и умойся. После этого можно будет спокойно выдохнуть.",
                "Go to the bathroom and wash your face. After that, you can breathe out calmly.");
        put("Включи телевизор и присядь на диван. Обычный шум комнаты поможет выдохнуть.",
                "Turn on the TV and sit on the sofa. Ordinary room noise will help you breathe out.");
        put("Зеркало будто ждёт, когда ты посмотришь прямо в него.",
                "The mirror seems to be waiting for you to look straight into it.");
        put("На качелях сидит ребёнок. Кажется, он узнал тебя раньше, чем ты его.",
                "A child is sitting on the swing. It seems they recognized you before you recognized them.");
        put("Глубже между деревьями стоит Тень. Разговор ещё не закончен.",
                "Deeper between the trees stands the Shadow. The conversation is not over.");
        put("Друг ждёт на лавочке. Этот разговор нельзя обойти стороной.",
                "The Friend waits on a bench. This conversation cannot be avoided.");
        put("В библиотеке ждёт Старик. Он спрашивает не из любопытства.",
                "The Elder waits in the library. He does not ask out of curiosity.");
        put("У костра стоит Воин. Дальше тропа ведёт только вверх.",
                "The Warrior stands by the fire. From here, the trail only goes upward.");
        put("Путь уже пройден. Осталось только посмотреть на результат.",
                "The path is already complete. Only the result remains to be seen.");
        put("Двор никуда не денется. Сначала нужно закончить утренние дела дома.",
                "The yard is not going anywhere. First you need to finish the morning chores at home.");
        put("Ручка холодная. Кажется, сначала надо вернуться к зеркалу.",
                "The handle is cold. It feels like you should return to the mirror first.");
        put("Ты выходишь во двор. За ним уже шумит Лес Сомнений.",
                "You step into the yard. Beyond it, the Forest of Doubt is already rustling.");
        put("Дверь квартиры растворяется и становится порталом. За ней шумит Лес Сомнений.",
                "The apartment door dissolves and becomes a portal. Behind it, the Forest of Doubt is rustling.");
        put("Качели тихо скрипят. Тропа дальше стала видимой.",
                "The swing creaks softly. The path ahead becomes visible.");
        put("Лес расступается. Впереди появляются огни Деревни Связей.",
                "The forest parts. The lights of the Village of Bonds appear ahead.");
        put("На площади становится тише. В библиотеке всё ещё горит свет.",
                "The square grows quieter. A light is still burning in the library.");
        put("Мост за библиотекой ведёт к Горе Целей.", "The bridge behind the library leads to the Mountain of Goals.");
        put("Все пятеро стоят кругом.\n\nСтарик: Понимание уже рядом?\nТень: Мы никогда не были снаружи.\nРебёнок: Мы - это ты.\nДруг: Всё, что было видно... это твоя голова.\nВоин: Этот путь проходил внутри собственного разума.\n\nЭто был твой внутренний мир. Каждый выбор здесь похож на выбор, который ты делаешь в реальной жизни.",
                "All five stand in a circle.\n\nElder: Is understanding close now?\nShadow: We were never outside.\nChild: We are you.\nFriend: Everything that was visible... was inside your head.\nWarrior: This path passed through your own mind.\n\nThis was your inner world. Every choice here resembles a choice you make in real life.");

        put("Ты наконец-то смотришь на меня... Я уже давно здесь. Просто взгляд всегда уходил в сторону.",
                "You are finally looking at me... I have been here for a long time. Your gaze just kept turning away.");
        put("Что ты такое?", "What are you?");
        put("Это сон?", "Is this a dream?");
        put("Просто нет сил...", "I just have no strength...");
        put("Оставь меня в покое.", "Leave me alone.");
        put("Я - то, от чего ты убегаешь. Хочешь пойти со мной?",
                "I am what you keep running from. Do you want to come with me?");
        put("Пойдём.", "Let's go.");
        put("Расскажи сначала, кто ты.", "First tell me who you are.");
        put("Я не хочу никуда.", "I do not want to go anywhere.");
        put("Это всё не по-настоящему.", "None of this is real.");
        put("Я знал, что ты придёшь... Ты меня совсем забыл?",
                "I knew you would come... Did you forget me completely?");
        put("Ты всегда так говоришь... Ты меня совсем забыл?",
                "You always say that... Did you forget me completely?");
        put("Прости, я был занят.", "Sorry, I was busy.");
        put("Я не бросал тебя.", "I did not abandon you.");
        put("Давай посидим вместе.", "Let's sit together.");
        put("Кто ты? Я тебя не знаю.", "Who are you? I do not know you.");
        put("Видишь? Даже он в тебе разочарован. Сколько ещё будешь прятаться?",
                "See? Even he is disappointed in you. How much longer will you hide?");
        put("Я не прячусь.", "I am not hiding.");
        put("Что мне делать?", "What should I do?");
        put("Я принимаю тебя.", "I accept you.");
        put("Просто исчезни.", "Just disappear.");
        put("Ты наконец-то здесь... Я правда рад тебя видеть. Ты выглядишь так, будто не спал сто лет.",
                "You are finally here... I am really glad to see you. You look like you have not slept in a hundred years.");
        put("Ты всегда так... отстранённо. Давно не виделись.",
                "You are always so... distant. It has been a long time.");
        put("Всё нормально.", "Everything is fine.");
        put("Честно? Мне хреново.", "Honestly? I feel awful.");
        put("Прости, что пропал.", "Sorry I disappeared.");
        put("Расскажи лучше про себя.", "Tell me about yourself instead.");
        put("Многие приходят сюда... но мало кто остаётся. Что ты ищешь в этом месте?",
                "Many come here... but few stay. What are you looking for in this place?");
        put("Силу.", "Strength.");
        put("Покой.", "Calm.");
        put("Ответы.", "Answers.");
        put("Я просто иду дальше.", "I am just moving on.");
        put("Путь пройден. Немногие доходят так далеко.",
                "The path is complete. Few make it this far.");
        put("Путь почти пройден... но внутри будто хочется сдаться на полпути.",
                "The path is almost complete... but inside, it feels like giving up halfway.");
        put("Можно идти дальше.", "I can keep going.");
        put("Нужен отдых.", "I need rest.");
        put("Я возьму всех с собой.", "I will take everyone with me.");
        put("Во мне уже что-то изменилось.", "Something in me has already changed.");

        put("Избегающий тип", "Avoidant Type");
        put("Тревожный тип", "Anxious Type");
        put("Перфекционист", "Perfectionist");
        put("Заботливый тип", "Caring Type");
        put("Исследователь", "Explorer");
        put("Лидер", "Leader");
        put("Целостный путь", "Integrated Path");
        put("Путь роста", "Path of Growth");
        put("Путь покоя", "Path of Calm");
        put("Путь связи", "Path of Connection");
        put("Путь уверенности", "Path of Confidence");
        put("Ты часто выбирал честность без жесткости. Внутренние части не исчезли, но начали говорить друг с другом.",
                "You often chose honesty without harshness. The inner parts did not disappear, but they began speaking to each other.");
        put("Ты часто замечал тех, кому трудно, и не проходил мимо. Важно помнить: забота сильнее, когда в ней остаётся место для себя.",
                "You often noticed those who were struggling and did not walk past. Remember: care is stronger when it leaves room for yourself.");
        put("Ты выбирал движение и готовность смотреть на новое. Твоя сила - любопытство, но ему нужен якорь, чтобы не стать бегством.",
                "You chose movement and a willingness to look at the new. Your strength is curiosity, but it needs an anchor so it does not become escape.");
        put("Ты склонен брать направление на себя и действовать, когда другие сомневаются. Следующий шаг - слышать не только цель, но и людей рядом.",
                "You tend to take direction into your own hands and act when others doubt. The next step is to hear not only the goal, but also the people beside you.");
        put("Ты часто выбирал дистанцию вместо прямого ответа. Это защищает от боли, но постепенно делает мир уже.",
                "You often chose distance instead of a direct answer. It protects from pain, but gradually makes the world smaller.");
        put("Ты много прислушивался к угрозам и сомнениям. Осторожность помогает выжить, но ей нельзя отдавать весь голос.",
                "You listened closely to threats and doubts. Caution helps you survive, but it should not have the whole voice.");
        put("Ты выбирал порядок, контроль и завершённость. Это даёт опору, пока не превращает каждый шаг в экзамен.",
                "You chose order, control, and completion. It gives support until every step becomes an exam.");
        put("Ты чаще выбирал движение вперёд. Риск в том, чтобы не превращать рост в бегство от усталости.",
                "You more often chose forward movement. The risk is turning growth into escape from exhaustion.");
        put("Ты искал тишину и устойчивость. Важно не путать покой с отказом от трудных разговоров.",
                "You looked for quiet and stability. It is important not to confuse calm with refusing difficult conversations.");
        put("Ты замечал других и свои забытые части. Твоя опора появляется через контакт, а не через изоляцию.",
                "You noticed others and your forgotten parts. Your support appears through contact, not isolation.");
        put("Ты выбирал собранность и силу. Следующий шаг - оставить место не только контролю, но и доверию.",
                "You chose composure and strength. The next step is to leave room not only for control, but also for trust.");
        put("Выбери один маленький разговор, который давно откладываешь, и начни его без требования решить всё сразу.",
                "Choose one small conversation you have been postponing and start it without demanding that everything be solved at once.");
        put("Перед выбором отделяй факт от страха: что точно происходит, а что только звучит как угроза?",
                "Before choosing, separate fact from fear: what is definitely happening, and what only sounds like a threat?");
        put("Оставляй одно дело в состоянии \"достаточно хорошо\". Это тренирует доверие к себе, а не отказ от качества.",
                "Leave one task at \"good enough.\" This trains trust in yourself, not a refusal of quality.");
        put("Помогая другим, заранее называй границу: сколько сил и времени ты действительно можешь дать.",
                "When helping others, name the boundary in advance: how much strength and time you can truly give.");
        put("Записывай, зачем ты идёшь вперёд. Так рост остаётся выбором, а не автоматическим побегом.",
                "Write down why you are moving forward. That way growth remains a choice, not an automatic escape.");
        put("Перед сильным решением задай один вопрос тому, кто идёт рядом. Это не снижает уверенность, а уточняет её.",
                "Before a strong decision, ask one question of the person beside you. It does not reduce confidence, it clarifies it.");
        put("Попробуй чаще спрашивать себя: какой маленький шаг я могу сделать сегодня?",
                "Try asking yourself more often: what small step can I take today?");
        put("Замедляйся перед важными решениями: сначала дыхание, потом ответ.",
                "Slow down before important decisions: breath first, answer second.");
        put("Возвращайся к людям постепенно: одно честное сообщение лучше долгого молчания.",
                "Return to people gradually: one honest message is better than a long silence.");
        put("Отмечай свои завершённые действия. Уверенность растёт от доказательств, а не от давления.",
                "Notice the actions you complete. Confidence grows from evidence, not pressure.");
    }

    private Localization() {
    }

    public static String translate(String text, int languageMode) {
        if (text == null || languageMode != GamePanel.LANGUAGE_EN) {
            return text;
        }
        String translated = ENGLISH.get(text);
        return translated == null ? text : translated;
    }

    private static void put(String ru, String en) {
        ENGLISH.put(ru, en);
    }
}

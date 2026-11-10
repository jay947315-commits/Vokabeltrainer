package de.vocapro.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private data class Word(val en: String, val de: String, val sentence: String)
private data class Tense(val name: String, val pattern: String, val use: String, val example: String, val translation: String)
private data class Question(val prompt: String, val options: List<String>, val answer: String, val explanation: String, val category: String)

private val words = listOf(
    Word("achieve","erreichen","You can achieve your goals."), Word("although","obwohl","Although it was late, we continued."),
    Word("environment","Umwelt","We should protect the environment."), Word("improve","verbessern","Practice helps you improve."),
    Word("knowledge","Wissen","Knowledge gives you confidence."), Word("journey","Reise","The journey was exciting."),
    Word("opportunity","Gelegenheit","This is a great opportunity."), Word("reliable","zuverlässig","She is a reliable friend."),
    Word("solution","Lösung","We found a solution."), Word("challenge","Herausforderung","Learning a language is a challenge."),
    Word("to suggest","vorschlagen","I suggest taking a break."), Word("to avoid","vermeiden","Try to avoid common mistakes."),
    Word("necessary","notwendig","Sleep is necessary for learning."), Word("thought","Gedanke","That is an interesting thought."),
    Word("throughout","während / überall","It rained throughout the night."), Word("instead","stattdessen","Let's walk instead."),
    Word("careful","vorsichtig","Be careful when you cross the road."), Word("probably","wahrscheinlich","She will probably arrive soon."),
    Word("experience","Erfahrung","Experience makes you better."), Word("to remember","sich erinnern","Remember the new words."),
    Word("about","über","Tell me about your day."),
    Word("above","oberhalb","The picture is above the sofa."),
    Word("across","über","We walked across the street."),
    Word("action","Handlung","Every action has a result."),
    Word("actually","tatsächlich","I actually enjoyed the film."),
    Word("advice","Rat","Thank you for your advice."),
    Word("afraid","ängstlich","She is afraid of spiders."),
    Word("after","nach","Let's meet after school."),
    Word("again","wieder","Please say that again."),
    Word("age","Alter","What is your age?"),
    Word("agree","zustimmen","I agree with you."),
    Word("almost","fast","We are almost there."),
    Word("already","schon","I have already eaten."),
    Word("always","immer","He always arrives early."),
    Word("answer","Antwort","Do you know the answer?"),
    Word("appear","erscheinen","A rainbow appeared."),
    Word("apple","Apfel","I eat an apple every day."),
    Word("around","herum","We walked around the park."),
    Word("arrive","ankommen","The bus will arrive soon."),
    Word("ask","fragen","Can I ask a question?"),
    Word("beautiful","schön","What a beautiful day!"),
    Word("because","weil","I stayed home because it rained."),
    Word("become","werden","She wants to become a doctor."),
    Word("begin","beginnen","The lesson begins at nine."),
    Word("believe","glauben","I believe you."),
    Word("between","zwischen","The shop is between the bank and the cafe."),
    Word("break","Pause","Let's take a short break."),
    Word("bring","bringen","Please bring your book."),
    Word("build","bauen","They build houses."),
    Word("busy","beschäftigt","I am busy today."),
    Word("buy","kaufen","We need to buy some milk."),
    Word("call","anrufen","Call me when you arrive."),
    Word("change","ändern","Things can change quickly."),
    Word("choose","auswählen","Choose your favourite colour."),
    Word("city","Stadt","Berlin is a large city."),
    Word("clear","klar","Is the instruction clear?"),
    Word("close","schließen","Please close the door."),
    Word("come","kommen","Come here, please."),
    Word("common","häufig","This is a common mistake."),
    Word("country","Land","Which country are you from?"),
    Word("create","erschaffen","We can create something new."),
    Word("different","verschieden","We have different ideas."),
    Word("difficult","schwierig","This exercise is difficult."),
    Word("early","früh","I get up early."),
    Word("easy","einfach","The first question is easy."),
    Word("enough","genug","We have enough time."),
    Word("every","jeder","I practise every day."),
    Word("family","Familie","My family is very kind."),
    Word("feel","fühlen","How do you feel today?"),
    Word("find","finden","I cannot find my keys."),
    Word("finish","beenden","Finish your work, please."),
    Word("follow","folgen","Follow the instructions."),
    Word("friend","Freund","My friend lives nearby."),
    Word("get","bekommen","I get up at seven."),
    Word("give","geben","Give me a minute."),
    Word("good","gut","You did a good job."),
    Word("happen","passieren","What happened yesterday?"),
    Word("happy","glücklich","I am happy to see you."),
    Word("help","helfen","Can you help me?"),
    Word("important","wichtig","Sleep is important."),
    Word("keep","weitermachen","Keep learning every day."),
    Word("learn","lernen","I learn English."),
    Word("leave","verlassen","We leave at eight."),
    Word("life","Leben","Life is full of surprises."),
    Word("like","mögen","I like learning languages."),
    Word("listen","zuhören","Listen carefully."),
    Word("little","wenig","I need a little help."),
    Word("look","schauen","Look at this picture."),
    Word("make","machen","Let's make a plan."),
    Word("mean","bedeuten","What does this word mean?"),
    Word("meet","treffen","Nice to meet you."),
    Word("money","Geld","I saved some money."),
    Word("much","viel","Thank you very much."),
    Word("need","brauchen","I need more practice."),
    Word("never","nie","I never give up."),
    Word("often","oft","We often play football."),
    Word("people","Menschen","Many people speak English."),
    Word("place","Ort","This is a nice place."),
    Word("question","Frage","Ask me a question."),
    Word("read","lesen","I read every evening."),
    Word("right","richtig","Your answer is right."),
    Word("same","gleich","We have the same idea."),
    Word("say","sagen","What did you say?"),
    Word("school","Schule","My school is nearby."),
    Word("small","klein","The room is small."),
    Word("speak","sprechen","Can you speak slowly?"),
    Word("start","beginnen","Let's start now."),
    Word("still","immer noch","I am still learning."),
    Word("thing","Sache","One thing is important."),
    Word("think","denken","Think before you answer."),
    Word("understand","verstehen","I understand the rule."),
    Word("usually","gewöhnlich","I usually walk to school."),
    Word("want","wollen","I want to improve my English."),
    Word("way","Weg","There is another way."),
    Word("work","arbeiten","Practice makes the work easier."),
    Word("write","schreiben","Write the new word down.")
)


private val level78Words = listOf(
    Word("to achieve", "erreichen", "You can achieve more than you think."),
    Word("although", "obwohl", "Although it was raining, we went outside."),
    Word("to argue", "streiten / argumentieren", "They often argue about football."),
    Word("behaviour", "Verhalten", "Good behaviour is important in class."),
    Word("to borrow", "ausleihen", "Can I borrow your dictionary?"),
    Word("to complain", "sich beschweren", "He complained about the noise."),
    Word("confident", "selbstbewusst", "She feels confident before the test."),
    Word("consequence", "Folge", "Every decision has a consequence."),
    Word("to convince", "überzeugen", "I tried to convince my friend."),
    Word("dangerous", "gefährlich", "It is dangerous to swim here."),
    Word("development", "Entwicklung", "The development took several years."),
    Word("to discover", "entdecken", "They discovered an old castle."),
    Word("environment", "Umwelt", "We should protect the environment."),
    Word("to expect", "erwarten", "I didn't expect this result."),
    Word("experience", "Erfahrung / erleben", "It was an unforgettable experience."),
    Word("to fail", "scheitern / durchfallen", "Don't be afraid to fail."),
    Word("freedom", "Freiheit", "Everyone deserves freedom."),
    Word("to improve", "verbessern", "Practice helps you improve."),
    Word("independent", "unabhängig", "She is becoming more independent."),
    Word("to influence", "beeinflussen", "Friends can influence our choices."),
    Word("knowledge", "Wissen", "Knowledge grows when you stay curious."),
    Word("to manage", "bewältigen / schaffen", "He managed to solve the problem."),
    Word("nevertheless", "dennoch", "It was difficult; nevertheless, we continued."),
    Word("opportunity", "Gelegenheit", "This is a great opportunity."),
    Word("ordinary", "gewöhnlich", "It was an ordinary school day."),
    Word("to participate", "teilnehmen", "Everyone can participate in the project."),
    Word("to persuade", "überreden", "She persuaded me to join the team."),
    Word("probably", "wahrscheinlich", "They will probably arrive soon."),
    Word("to realise", "erkennen / merken", "I realised my mistake."),
    Word("reliable", "zuverlässig", "He is a reliable teammate."),
    Word("responsibility", "Verantwortung", "Taking care of a pet is a responsibility."),
    Word("to solve", "lösen", "Can you solve this riddle?"),
    Word("solution", "Lösung", "We found a clever solution."),
    Word("to suggest", "vorschlagen", "I suggest taking a short break."),
    Word("throughout", "während / überall", "It rained throughout the night."),
    Word("to avoid", "vermeiden", "Try to avoid making the same mistake."),
    Word("valuable", "wertvoll", "Your time is valuable."),
    Word("various", "verschiedene", "We discussed various ideas."),
    Word("to wonder", "sich fragen", "I wonder what will happen next.")
)

private val tenses = listOf(
    Tense("Simple Present","I play / he plays","Gewohnheiten, Fakten und regelmäßige Handlungen","She plays tennis every week.","Sie spielt jede Woche Tennis."),
    Tense("Present Continuous","am/is/are + verb-ing","Was gerade jetzt oder vorübergehend passiert","I am reading a book now.","Ich lese gerade ein Buch."),
    Tense("Present Perfect","have/has + past participle","Vergangenheit mit Ergebnis oder Bezug zur Gegenwart","We have finished our homework.","Wir haben unsere Hausaufgaben beendet."),
    Tense("Present Perfect Continuous","have/has been + verb-ing","Begann früher und dauert an oder wirkt nach","She has been studying for two hours.","Sie lernt seit zwei Stunden."),
    Tense("Simple Past","verb-ed / 2. Form","Abgeschlossene Handlung in der Vergangenheit","They visited London last year.","Sie besuchten letztes Jahr London."),
    Tense("Past Continuous","was/were + verb-ing","Handlung war zu einem Zeitpunkt in der Vergangenheit im Verlauf","I was sleeping at ten.","Ich schlief um zehn Uhr gerade."),
    Tense("Past Perfect","had + past participle","Etwas geschah vor einem anderen Ereignis in der Vergangenheit","He had left before I arrived.","Er war gegangen, bevor ich ankam."),
    Tense("Past Perfect Continuous","had been + verb-ing","Dauer einer Handlung bis zu einem Zeitpunkt in der Vergangenheit","They had been waiting for an hour.","Sie hatten eine Stunde gewartet."),
    Tense("Will Future","will + infinitive","Spontane Entscheidungen, Versprechen und Vorhersagen","I think it will rain.","Ich glaube, es wird regnen."),
    Tense("Going-to Future","am/is/are going to + infinitive","Pläne und Absichten; Vorhersage mit Anzeichen","We are going to travel soon.","Wir werden bald verreisen."),
    Tense("Future Continuous","will be + verb-ing","Handlung, die zu einem Zeitpunkt in der Zukunft gerade läuft","At six, I will be working.","Um sechs werde ich arbeiten."),
    Tense("Future Perfect","will have + past participle","Bis zu einem Zeitpunkt in der Zukunft abgeschlossen","By Friday, I will have finished.","Bis Freitag werde ich fertig sein."),
    Tense("Future Perfect Continuous","will have been + verb-ing","Wie lange etwas bis zu einem zukünftigen Zeitpunkt andauert","In June, I will have been living here for a year.","Im Juni werde ich seit einem Jahr hier wohnen.")
)

private val grammarQuestions = listOf(
    Question("She ___ to school every day.", listOf("go","goes","is going"), "goes", "Simple Present: Bei he/she/it bekommt das Verb meistens -s. Signal: every day.", "Simple Present"),
    Question("Look! The baby ___.", listOf("cries","is crying","has cried"), "is crying", "Present Continuous: am/is/are + -ing für etwas, das gerade passiert.", "Present Continuous"),
    Question("I ___ already ___ my homework.", listOf("have / finished","am / finishing","did / finish"), "have / finished", "Present Perfect: have/has + 3. Verbform. Das Ergebnis ist jetzt wichtig.", "Present Perfect"),
    Question("He ___ for three hours.", listOf("studies","has been studying","studied"), "has been studying", "Present Perfect Continuous: has/have been + -ing; betont die Dauer bis jetzt.", "Present Perfect Continuous"),
    Question("We ___ a film yesterday.", listOf("watch","watched","have watched"), "watched", "Simple Past: abgeschlossene Vergangenheit; yesterday ist ein typisches Signalwort.", "Simple Past"),
    Question("At 8 pm, I ___ dinner.", listOf("cooked","was cooking","have cooked"), "was cooking", "Past Continuous: was/were + -ing; Handlung war gerade im Gange.", "Past Continuous"),
    Question("The train ___ before we arrived.", listOf("left","had left","has left"), "had left", "Past Perfect: had + 3. Verbform; das passierte zuerst.", "Past Perfect"),
    Question("She was tired because she ___ all day.", listOf("worked","had been working","has worked"), "had been working", "Past Perfect Continuous: had been + -ing; Dauer vor einem Zeitpunkt in der Vergangenheit.", "Past Perfect Continuous"),
    Question("I think it ___ sunny tomorrow.", listOf("is","will be","was"), "will be", "Will-Future: will + Grundform für Vorhersagen und spontane Entscheidungen.", "Will Future"),
    Question("They ___ visit their aunt this weekend.", listOf("are going to","will have","are"), "are going to", "Going-to Future: am/is/are going to + Grundform für Pläne.", "Going-to Future"),
    Question("This time tomorrow, we ___ to Spain.", listOf("will be flying","fly","have flown"), "will be flying", "Future Continuous: will be + -ing; eine Handlung läuft dann gerade.", "Future Continuous"),
    Question("By 6 pm, I ___ the report.", listOf("will finish","will have finished","am finishing"), "will have finished", "Future Perfect: will have + 3. Verbform; bis dahin fertig.", "Future Perfect"),
    Question("By July, he ___ here for ten years.", listOf("will live","will have been living","lived"), "will have been living", "Future Perfect Continuous: will have been + -ing; betont die Dauer bis in die Zukunft.", "Future Perfect Continuous")
)

private fun todayKey(): String = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
private class ProgressStore(context: Context) {
    private val prefs = context.getSharedPreferences("vocapro_progress", Context.MODE_PRIVATE)
    fun int(key: String, default: Int = 0) = prefs.getInt(key, default)
    fun string(key: String, default: String = "") = prefs.getString(key, default) ?: default
    fun setInt(key: String, value: Int) { prefs.edit().putInt(key, value).apply() }
    fun setString(key: String, value: String) { prefs.edit().putString(key, value).apply() }
    fun recordStudy(): Int {
        val today = todayKey()
        val last = string("last_study")
        var streak = int("streak")
        if (last != today) {
            val yesterday = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date(System.currentTimeMillis() - 86400000L))
            streak = if (last == yesterday) streak + 1 else 1
            setInt("streak", streak)
            setString("last_study", today)
        }
        return streak
    }
}

/**
 * Lightweight offline spaced-repetition scheduler.
 * Correct answers return after 1, 3, 7... days; mistakes return after 15 minutes.
 */
private class AdaptiveLearning(context: Context) {
    private val prefs = context.getSharedPreferences("vocapro_adaptive", Context.MODE_PRIVATE)
    private fun key(topic: String, suffix: String) = "${topic.lowercase(Locale.ROOT).hashCode()}_$suffix"
    fun attempts(topic: String) = prefs.getInt(key(topic, "attempts"), 0)
    fun correctCount(topic: String) = prefs.getInt(key(topic, "correct"), 0)
    fun dueAt(topic: String) = prefs.getLong(key(topic, "due"), 0L)
    fun isDue(topic: String, now: Long = System.currentTimeMillis()) = dueAt(topic) <= now

    fun recordAnswer(topic: String, wasCorrect: Boolean) {
        val attempts = attempts(topic) + 1
        val correct = correctCount(topic) + if (wasCorrect) 1 else 0
        val streak = prefs.getInt(key(topic, "streak"), 0)
        val nextStreak = if (wasCorrect) streak + 1 else 0
        val ease = prefs.getFloat(key(topic, "ease"), 2.2f)
        val nextEase = if (wasCorrect) (ease + 0.05f).coerceAtMost(2.8f) else (ease - 0.2f).coerceAtLeast(1.3f)
        val intervalMs = if (!wasCorrect) 15L * 60L * 1000L else when (nextStreak) {
            1 -> 24L * 60L * 60L * 1000L
            2 -> 3L * 24L * 60L * 60L * 1000L
            else -> (7.0 * 24.0 * 60.0 * 60.0 * 1000.0 * Math.pow(nextEase.toDouble(), (nextStreak - 2).coerceAtMost(5).toDouble())).toLong()
        }
        prefs.edit()
            .putInt(key(topic, "attempts"), attempts)
            .putInt(key(topic, "correct"), correct)
            .putInt(key(topic, "streak"), nextStreak)
            .putFloat(key(topic, "ease"), nextEase)
            .putLong(key(topic, "due"), System.currentTimeMillis() + intervalMs)
            .putLong(key(topic, "last"), System.currentTimeMillis())
            .apply()
    }

    fun markShown(topic: String) {
        if (attempts(topic) == 0 && prefs.getLong(key(topic, "last"), 0L) == 0L) {
            prefs.edit().putLong(key(topic, "last"), System.currentTimeMillis()).apply()
        }
    }

    fun nextIndex(topics: List<String>): Int {
        if (topics.isEmpty()) return 0
        val now = System.currentTimeMillis()
        val unseen = topics.indices.filter { attempts(topics[it]) == 0 && prefs.getLong(key(topics[it], "last"), 0L) == 0L }
        if (unseen.isNotEmpty()) return unseen.first()
        val due = topics.indices.filter { dueAt(topics[it]) <= now }
        val candidates = if (due.isNotEmpty()) due else topics.indices.toList()
        return candidates.minWithOrNull(
            compareBy<Int> { dueAt(topics[it]) }
                .thenByDescending { attempts(topics[it]) - correctCount(topics[it]) }
                .thenBy { prefs.getLong(key(topics[it], "last"), 0L) }
        ) ?: 0
    }

    fun dueCount(topics: List<String>): Int = topics.count { isDue(it) }
    fun accuracy(topic: String): Int = if (attempts(topic) == 0) 0 else (correctCount(topic) * 100 / attempts(topic))
}

/** Personal vocabulary lists are stored locally and work without internet. */
private class CustomWordStore(context: Context) {
    private val prefs = context.getSharedPreferences("vocapro_custom_words", Context.MODE_PRIVATE)
    fun load(): List<DictionaryEntry> = try {
        val array = org.json.JSONArray(prefs.getString("items", "[]"))
        buildList {
            for (i in 0 until array.length()) {
                val item = array.optJSONObject(i) ?: continue
                val word = item.optString("word").trim()
                val meaning = item.optString("meaning").trim()
                if (word.isNotBlank() && meaning.isNotBlank()) add(DictionaryEntry(word, meaning))
            }
        }
    } catch (_: Exception) { emptyList() }
    fun addAll(entries: List<DictionaryEntry>) {
        val merged = (load() + entries).distinctBy { it.word.trim().lowercase(Locale.ROOT) }
        val array = org.json.JSONArray()
        merged.forEach { array.put(org.json.JSONObject().put("word", it.word).put("meaning", it.meaning)) }
        prefs.edit().putString("items", array.toString()).apply()
    }
    fun remove(entry: DictionaryEntry) {
        addAll(load().filterNot { it.word.equals(entry.word, true) })
    }
    fun exportText(): String = load().joinToString("\n") { "\${it.word}\t\${it.meaning}" }
}

class MainActivity : ComponentActivity() {
    private var tts: TextToSpeech? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this) { status -> if (status == TextToSpeech.SUCCESS) tts?.language = Locale.UK }
        setContent { VocaProApp(onSpeak = { tts?.speak(it, TextToSpeech.QUEUE_FLUSH, null, "vocapro") }) }
    }
    override fun onDestroy() { tts?.shutdown(); super.onDestroy() }
}

@Composable
private fun VocaProApp(onSpeak: (String) -> Unit) {
    val context = LocalContext.current
    val store = remember { ProgressStore(context) }
    var dark by remember { mutableStateOf(store.int("dark", 1) == 1) }
    var page by remember { mutableStateOf("Start") }
    var xp by remember { mutableIntStateOf(store.int("xp")) }
    var streak by remember { mutableIntStateOf(store.int("streak")) }
    var todayCount by remember { mutableIntStateOf(if (store.string("count_day") == todayKey()) store.int("today_count") else 0) }
    val scheme = if (dark) {
        darkColorScheme(primary = Color(0xFF7B6DFF), secondary = Color(0xFF17B978), background = Color(0xFF12132B), surface = Color(0xFF1D2044), surfaceVariant = Color(0xFF2C3060), onSurface = Color(0xFFEEF0FF))
    } else {
        lightColorScheme(primary = Color(0xFF5A4DF5), secondary = Color(0xFF17B978), background = Color(0xFFF3F5FF), surface = Color.White, surfaceVariant = Color(0xFFE2E5F6), onSurface = Color(0xFF1C1F3A))
    }
    fun reward(points: Int) {
        xp += points; store.setInt("xp", xp)
        todayCount++; store.setInt("today_count", todayCount); store.setString("count_day", todayKey())
        streak = store.recordStudy()
    }
    MaterialTheme(colorScheme = scheme, shapes = Shapes(small = RoundedCornerShape(14.dp), medium = RoundedCornerShape(20.dp), large = RoundedCornerShape(24.dp))) {
        Scaffold(bottomBar = {
            NavigationBar {
                listOf("Start","Training","Grammatik","Vokabeln","Worttraining","Scan").forEach { item ->
                    NavigationBarItem(selected = page == item, onClick = { page = item }, icon = {
                        Icon(when(item) { "Start" -> Icons.Default.Home; "Training" -> Icons.Default.Bolt; "Grammatik" -> Icons.Default.MenuBook; "Vokabeln" -> Icons.Default.Style; "Worttraining" -> Icons.Default.Bolt; else -> Icons.Default.DocumentScanner }, contentDescription = item)
                    }, label = { Text(item) })
                }
            }
        }) { padding ->
            Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(padding).padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.weight(1f)) {
                        Text("VocaPro", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                        Text("Jeden Tag ein bisschen sicherer.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { dark = !dark; store.setInt("dark", if (dark) 1 else 0) }) { Icon(if (dark) Icons.Default.LightMode else Icons.Default.DarkMode, "Design wechseln") }
                }
                Spacer(Modifier.height(12.dp))
                when(page) {
                    "Start" -> Dashboard(xp, streak, todayCount, onNavigate = { page = it })
                    "Training" -> TrainingPage(onReward = ::reward)
                    "Grammatik" -> GrammarPage(onReward = ::reward)
                    "Vokabeln" -> VocabularyPage(onSpeak, onReward = ::reward, onTrain = { page = "Worttraining" })
                    "Worttraining" -> VocabularyTrainingPage(onSpeak, onReward = ::reward)
                    else -> ScanPage()
                }
            }
        }
    }
}

@Composable private fun Dashboard(xp: Int, streak: Int, todayCount: Int, onNavigate: (String) -> Unit) {
    val level = xp / 100 + 1
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) { Text("Dein Lern-Boost", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold); Text("Kurze Einheiten. Langfristiger Erfolg.") }
                        Text("", style = MaterialTheme.typography.headlineLarge)
                    }
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                        Metric(" $streak", "Tage Streak")
                        Metric("⭐ $xp", "XP gesammelt")
                        Metric(" $level", "Level")
                    }
                    Spacer(Modifier.height(14.dp))
                    Text("Tagesziel: $todayCount / 10 richtige Übungen")
                    LinearProgressIndicator(progress = { (todayCount / 10f).coerceIn(0f,1f) }, modifier = Modifier.fillMaxWidth().padding(top = 6.dp))
                    Text(if (todayCount >= 10) "Tagesziel geschafft – stark! " else "Noch ${10-todayCount} bis zum Tagesziel. Du schaffst das!", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 5.dp))
                }
            }
        }
        item { Text("Was möchtest du heute trainieren?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
        item { FeatureCard("Dein intelligentes Training","Übt zuerst deine Fehler und fällige Wiederholungen – passt sich dir an.","Training",onNavigate) }
        item { FeatureCard("Grammatik-Meister","Alle 13 Zeitformen verstehen und anwenden","Grammatik",onNavigate) }
        item { FeatureCard("Vokabel-Challenge","Wörter merken, hören und wiederholen","Vokabeln",onNavigate) }
        item { FeatureCard("Foto-Scan","Englische Texte aus Bildern erkennen","Scan",onNavigate) }
        item { Text("Lern-Tipp: Wiederhole lieber täglich 10 Minuten als einmal pro Woche eine Stunde. Verteiltes Üben hilft deinem Gedächtnis.", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall) }
    }
}
@Composable private fun Metric(value: String, label: String) { Column { Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold); Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) } }
@Composable private fun FeatureCard(title: String, subtitle: String, page: String, onNavigate: (String) -> Unit) {
    Card(Modifier.fillMaxWidth().clickable { onNavigate(page) }, shape = RoundedCornerShape(20.dp)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) { Text(title, fontWeight = FontWeight.Bold); Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall) }
            Icon(Icons.Default.ArrowForward, null)
        }
    }
}

@Composable private fun TrainingPage(onReward: (Int) -> Unit) {
    val context = LocalContext.current
    val learner = remember { AdaptiveLearning(context.applicationContext) }
    val topics = remember { grammarQuestions.map { it.category } }
    var index by remember { mutableIntStateOf(learner.nextIndex(topics)) }
    var answered by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf("") }
    var correct by remember { mutableStateOf(false) }
    var run by remember { mutableIntStateOf(0) }
    var sessionCount by remember { mutableIntStateOf(0) }
    var sessionCorrect by remember { mutableIntStateOf(0) }
    val q = grammarQuestions[index]
    val due = learner.dueCount(topics)
    val accuracy = learner.accuracy(q.category)

    Column(Modifier.fillMaxSize()) {
        // Scroll only the lesson content. The action button stays visible at the bottom.
        Column(
            Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(" Dein Lerntrainer", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(" $run Serie", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            Text("Wie bei Duolingo: antworte, lerne aus der Erklärung und mach direkt weiter.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                AssistChip(onClick = {}, label = { Text("$due fällig") }, leadingIcon = { Icon(Icons.Default.Replay, null) })
                AssistChip(onClick = {}, label = { Text("$sessionCorrect / $sessionCount richtig") }, leadingIcon = { Icon(Icons.Default.Insights, null) })
            }
            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(progress = { (sessionCount.coerceAtMost(10) / 10f) }, modifier = Modifier.fillMaxWidth())
            Text("Deine Übung · ${sessionCount.coerceAtMost(10)} / 10", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 4.dp))
            Spacer(Modifier.height(18.dp))
            Text(q.category.uppercase(Locale.GERMANY), color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text("Dein bisheriger Trefferwert: ${accuracy}%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(q.prompt, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 18.dp))

            q.options.forEach { option ->
                val isRightAnswer = answered && option == q.answer
                val isWrongSelection = answered && option == selected && !correct
                OutlinedButton(
                    onClick = {
                        if (!answered) {
                            selected = option
                            correct = option == q.answer
                            answered = true
                            sessionCount++
                            learner.recordAnswer(q.category, correct)
                            if (correct) { run++; sessionCorrect++; onReward(10) } else run = 0
                        }
                    },
                    enabled = !answered,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = when {
                            isRightAnswer -> Color(0xFF174D32)
                            isWrongSelection -> Color(0xFF5A2525)
                            else -> Color.Transparent
                        },
                        contentColor = if (isRightAnswer || isWrongSelection) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(option, modifier = Modifier.weight(1f))
                        if (isRightAnswer) Icon(Icons.Default.CheckCircle, contentDescription = "Richtige Antwort")
                        else if (isWrongSelection) Icon(Icons.Default.Cancel, contentDescription = "Deine Antwort war falsch")
                    }
                }
            }

            if (answered) {
                Card(
                    Modifier.fillMaxWidth().padding(top = 14.dp, bottom = 12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (correct) Color(0xFF174D32) else Color(0xFF492626),
                        contentColor = Color.White
                    )
                ) {
                    Column(Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(
                            if (correct) "Richtig! +10 XP " else "Nicht ganz – aus Fehlern lernst du!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Richtige Antwort: ${q.answer}", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp))
                        Text("Warum? ${q.explanation}", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 8.dp))
                        Text(
                            if (correct) "Super! Diese Regel wird später erneut abgefragt."
                            else "Kein Problem. Diese Aufgabe kommt in etwa 15 Minuten wieder.",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }
                }
            } else {
                Text(
                    "Tipp: Lies den Satz genau. Nach deiner Antwort erklären wir dir die Regel Schritt für Schritt.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 12.dp, bottom = 16.dp)
                )
            }
        }

        if (answered) {
            Button(
                onClick = {
                    index = learner.nextIndex(topics)
                    learner.markShown(topics[index])
                    answered = false
                    selected = ""
                    correct = false
                    if (sessionCount >= 10) { sessionCount = 0; sessionCorrect = 0 }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(if (sessionCount >= 10) "Runde abschließen · weiterlernen" else "Weiter  →")
            }
        } else {
            Text(
                "Wähle eine Antwort, um weiterzumachen.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
            )
        }
    }
}


@Composable private fun VocabularyTrainingPage(onSpeak: (String) -> Unit, onReward: (Int) -> Unit) {
    val context = LocalContext.current
    val dictionary = remember { OfflineDictionary(context.applicationContext) }
    val learner = remember { AdaptiveLearning(context.applicationContext) }
    var search by remember { mutableStateOf("") }
    var results by remember { mutableStateOf(emptyList<DictionaryEntry>()) }
    val selected = remember { mutableStateListOf<DictionaryEntry>() }
    var deck by remember { mutableStateOf(emptyList<DictionaryEntry>()) }
    var index by remember { mutableIntStateOf(0) }
    var choice by remember { mutableStateOf("") }
    var answered by remember { mutableStateOf(false) }
    var correct by remember { mutableStateOf(false) }
    var known by remember { mutableIntStateOf(0) }
    var started by remember { mutableStateOf(false) }

    LaunchedEffect(search) {
        results = withContext(Dispatchers.IO) {
            if (dictionary.count() > 0) dictionary.search(search, 120)
            else (words + level78Words).distinctBy { it.en }.filter {
                search.isBlank() || it.en.contains(search, true) || it.de.contains(search, true)
            }.map { DictionaryEntry(it.en, it.de) }.take(120)
        }
    }
    fun startTraining(items: List<DictionaryEntry>) {
        deck = items.distinctBy { it.word.lowercase(Locale.ROOT) }.shuffled()
        index = 0; choice = ""; answered = false; known = 0
        started = deck.isNotEmpty()
    }

    if (!started) {
        Column(Modifier.fillMaxSize()) {
            Text("Vokabeltraining", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Wähle deine Wörter aus oder starte eine zufällige Runde aus dem Offline-Wörterbuch.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Button(onClick = {
                val randomWords = buildList { repeat(10) { dictionary.random()?.let { add(it) } } }.distinctBy { it.word.lowercase(Locale.ROOT) }
                startTraining(if (randomWords.size >= 4) randomWords else results.shuffled().take(10))
            }, modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                Icon(Icons.Default.Shuffle, null); Spacer(Modifier.width(8.dp)); Text("10 zufällige Vokabeln lernen")
            }
            OutlinedTextField(value = search, onValueChange = { search = it }, modifier = Modifier.fillMaxWidth().padding(top = 10.dp), label = { Text("Vokabel suchen und auswählen") }, singleLine = true, leadingIcon = { Icon(Icons.Default.Search, null) })
            Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${selected.size} ausgewählt", fontWeight = FontWeight.SemiBold)
                Text("Bis zu 30 Wörter", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
            }
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                items(results, key = { it.word }) { item ->
                    val isSelected = selected.any { it.word.equals(item.word, true) }
                    Row(Modifier.fillMaxWidth().clickable {
                        if (isSelected) selected.removeAll { it.word.equals(item.word, true) }
                        else if (selected.size < 30) selected.add(item)
                    }.padding(vertical = 9.dp, horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(item.word, fontWeight = FontWeight.SemiBold)
                            Text(item.meaning, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                        }
                        Checkbox(checked = isSelected, onCheckedChange = { checked ->
                            if (checked && selected.size < 30) selected.add(item)
                            else selected.removeAll { it.word.equals(item.word, true) }
                        })
                    }
                    HorizontalDivider()
                }
            }
            Button(onClick = { startTraining(selected.toList()) }, enabled = selected.isNotEmpty(), modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
                Text("Ausgewählte Wörter lernen · ${selected.size}")
            }
        }
    } else {
        val current = deck.getOrNull(index)
        val choices = remember(current, deck) {
            if (current == null) emptyList() else (listOf(current.meaning) + deck.filter { it.word != current.word }.map { it.meaning }.distinct().shuffled().take(3)).shuffled()
        }
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Dein Vokabeltraining", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("${index + 1} / ${deck.size}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            LinearProgressIndicator(progress = { (index + if (answered) 1f else 0f) / deck.size.coerceAtLeast(1).toFloat() }, modifier = Modifier.fillMaxWidth().padding(top = 10.dp))
            if (current != null) {
                Text("Was bedeutet dieses Wort?", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 20.dp))
                Text(current.word, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 18.dp))
                OutlinedButton(onClick = { onSpeak(current.word) }) { Icon(Icons.Default.VolumeUp, null); Text(" Aussprache") }
                choices.forEach { option ->
                    OutlinedButton(onClick = {
                        if (!answered) {
                            choice = option; correct = option == current.meaning; answered = true
                            learner.recordAnswer("word:${current.word}", correct)
                            if (correct) { known++; onReward(5) }
                        }
                    }, enabled = !answered, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = when {
                            answered && option == current.meaning -> Color(0xFF174D32)
                            answered && option == choice && !correct -> Color(0xFF5A2525)
                            else -> Color.Transparent
                        },
                        contentColor = if (answered && (option == current.meaning || option == choice)) Color.White else MaterialTheme.colorScheme.onSurface
                    )) { Text(option, modifier = Modifier.fillMaxWidth()) }
                }
                if (answered) Card(Modifier.fillMaxWidth().padding(top = 12.dp), colors = CardDefaults.cardColors(containerColor = if (correct) Color(0xFF174D32) else Color(0xFF492626), contentColor = Color.White)) {
                    Column(Modifier.padding(14.dp)) {
                        Text(if (correct) "Richtig! +5 XP " else "Noch nicht – so lernst du es!", fontWeight = FontWeight.Bold)
                        Text("${current.word} = ${current.meaning}", modifier = Modifier.padding(top = 6.dp))
                        Text(if (correct) "Dieses Wort wird später wiederholt." else "Das Wort kommt in etwa 15 Minuten erneut.", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 6.dp))
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            if (answered) Button(onClick = {
                if (index < deck.lastIndex) { index++; choice = ""; answered = false }
                else { started = false; selected.clear() }
            }, modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                Text(if (index < deck.lastIndex) "Weiter  →" else "Runde abschließen")
            } else Text("Gewusst: $known von ${deck.size}", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 12.dp))
            TextButton(onClick = { started = false; selected.clear() }, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text("Auswahl ändern") }
        }
    }
}

@Composable private fun GrammarPage(onReward: (Int) -> Unit) {
    var lessonIndex by remember { mutableIntStateOf(0) }
    var answer by remember { mutableStateOf("") }
    var checked by remember { mutableStateOf(false) }
    var completed by remember { mutableIntStateOf(0) }

    val finished = lessonIndex >= tenses.size
    val tense = tenses.getOrNull(lessonIndex)
    val question = tense?.let { item -> grammarQuestions.firstOrNull { it.category == item.name } }

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Dein Lernpfad", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("⭐ $completed / ${tenses.size}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
        Text("Lektion für Lektion – nach jeder Antwort geht es direkt weiter.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(12.dp))
        LinearProgressIndicator(progress = { (lessonIndex + if (checked) 1f else 0f) / tenses.size.toFloat() }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(18.dp))

        if (!finished && tense != null) {
            Text("LEKTION ${lessonIndex + 1} VON ${tenses.size}", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text(tense.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
            Card(Modifier.fillMaxWidth().padding(top = 12.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("So bildest du die Zeitform", fontWeight = FontWeight.Bold)
                    Text(tense.pattern, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 4.dp))
                    Text("Wann benutzt man sie?", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 12.dp))
                    Text(tense.use)
                    Text("Beispiel: ${tense.example}", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 12.dp))
                    Text(tense.translation, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            if (question != null) {
                Text("Jetzt bist du dran!", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 18.dp))
                Text(question.prompt, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 10.dp))
                question.options.forEach { option ->
                    OutlinedButton(
                        onClick = {
                            if (!checked) {
                                answer = option
                                checked = true
                                if (option == question.answer) {
                                    completed++
                                    onReward(15)
                                }
                            }
                        },
                        enabled = !checked,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) { Text(option, modifier = Modifier.fillMaxWidth()) }
                }
                if (checked) {
                    Card(Modifier.fillMaxWidth().padding(top = 10.dp), colors = CardDefaults.cardColors(containerColor = if (answer == question.answer) Color(0xFF173D2C) else MaterialTheme.colorScheme.surface)) {
                        Column(Modifier.padding(14.dp)) {
                            Text(if (answer == question.answer) "Richtig! +15 XP " else "Fast! So lautet die richtige Antwort: ${question.answer}", fontWeight = FontWeight.Bold)
                            Text(question.explanation, modifier = Modifier.padding(top = 6.dp))
                        }
                    }
                }
            } else {
                Text("Merke dir die Regel und gehe zur nächsten Lektion.", modifier = Modifier.padding(top = 16.dp))
            }

            Button(
                onClick = {
                    if (lessonIndex < tenses.lastIndex) {
                        lessonIndex++
                        answer = ""
                        checked = false
                    } else {
                        lessonIndex = tenses.size
                    }
                },
                enabled = question == null || checked,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text(if (lessonIndex == tenses.lastIndex) "Lernpfad abschließen" else "Weiter zur nächsten Lektion  →")
            }
        } else {
            Spacer(Modifier.height(32.dp))
            Text("Geschafft! ", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Text("Du hast alle ${tenses.size} Zeitformen-Lektionen durchlaufen. Bleib dran und sammle weiter XP!", modifier = Modifier.padding(vertical = 12.dp))
            Text("Richtige Antworten: $completed", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Button(onClick = { lessonIndex = 0; answer = ""; checked = false; completed = 0 }, modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                Text("Lernpfad erneut starten")
            }
        }
    }
}

@Composable private fun VocabularyPage(onSpeak: (String) -> Unit, onReward: (Int) -> Unit, onTrain: () -> Unit) {
    val context = LocalContext.current
    val dictionary = remember { OfflineDictionary(context.applicationContext) }
    val customStore = remember { CustomWordStore(context.applicationContext) }
    var customWords by remember { mutableStateOf(customStore.load()) }
    var showAddWord by remember { mutableStateOf(false) }
    var ownEnglish by remember { mutableStateOf("") }
    var ownGerman by remember { mutableStateOf("") }
    val importWords = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            try {
                val content = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }.orEmpty()
                val parsed = content.lineSequence().mapNotNull { line ->
                    val parts = line.split('\t', ';', ',').map { it.trim() }
                    if (parts.size >= 2 && parts[0].isNotBlank() && parts[1].isNotBlank() && !parts[0].equals("english", true)) DictionaryEntry(parts[0], parts[1]) else null
                }.toList()
                customStore.addAll(parsed)
                customWords = customStore.load()
            } catch (_: Exception) { }
        }
    }
    var search by remember { mutableStateOf("") }
    var results by remember { mutableStateOf(words.map { DictionaryEntry(it.en, it.de + " · " + it.sentence) }) }
    var total by remember { mutableIntStateOf(0) }
    var current by remember { mutableStateOf<DictionaryEntry?>(null) }
    var reveal by remember { mutableStateOf(false) }
    var known by remember { mutableIntStateOf(0) }
    var usingFullDictionary by remember { mutableStateOf(false) }

    LaunchedEffect(search, customWords) {
        val loaded = withContext(Dispatchers.IO) {
            val count = dictionary.count()
            count to if (count > 0) dictionary.search(search, 120) else emptyList()
        }
        total = loaded.first
        usingFullDictionary = total > 0
        val base = if (usingFullDictionary) loaded.second else words.filter {
            it.en.contains(search.trim(), true) || it.de.contains(search.trim(), true)
        }.map { DictionaryEntry(it.en, it.de + " · " + it.sentence) }
        val personal = customWords.filter { search.isBlank() || it.word.contains(search, true) || it.meaning.contains(search, true) }
        results = (personal + base).distinctBy { it.word.lowercase(Locale.ROOT) }
        if (current == null || (search.isNotBlank() && results.none { it.word.equals(current?.word, true) })) {
            current = if (search.isBlank() && current == null) {
                level78Words.randomOrNull()?.let { DictionaryEntry(it.en, it.de + " · " + it.sentence) } ?: results.firstOrNull()
            } else results.firstOrNull()
            reveal = false
        }
    }

    val selected = current
    Column(Modifier.fillMaxSize()) {
        Text("Englisch–Deutsch Wörterbuch", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Button(onClick = onTrain, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) { Icon(Icons.Default.Bolt, null); Spacer(Modifier.width(8.dp)); Text("Eigene Vokabeln trainieren") }
        Text("Lernstart: Wortschatz ab Klasse 7/8 · einfache Grundwörter werden nicht als Startkarten verwendet.", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
        Text(
            if (usingFullDictionary) "${total} Einträge · vollständig offline durchsuchbar"
            else "Offline-Lernwortschatz · Wörterbuch wird im Build vorbereitet",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        OutlinedTextField(
            value = search, onValueChange = { search = it },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            label = { Text("Englisches oder deutsches Wort suchen") },
            singleLine = true, leadingIcon = { Icon(Icons.Default.Search, null) }
        )
        Text(
            if (usingFullDictionary) "${results.size} passende Treffer angezeigt" else "${results.size} Treffer",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(top = 6.dp, bottom = 4.dp)
        )
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            items(results, key = { it.word }) { item ->
                Row(
                    Modifier.fillMaxWidth().clickable { current = item; reveal = true }
                        .padding(vertical = 7.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(item.word, fontWeight = FontWeight.SemiBold)
                        Text(item.meaning, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                    }
                    IconButton(onClick = { onSpeak(item.word) }) { Icon(Icons.Default.VolumeUp, "Aussprache anhören") }
                }
                HorizontalDivider()
            }
            if (results.isEmpty()) item { Text("Kein Treffer. Prüfe die Schreibweise.", modifier = Modifier.padding(12.dp)) }
        }
        Text("Lernkarte · $known gewusst · +5 XP pro richtiger Karte", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 8.dp))
        Card(
            Modifier.fillMaxWidth().clickable { if (selected != null) reveal = !reveal },
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(Modifier.fillMaxWidth().padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(selected?.word ?: "Kein Wort ausgewählt", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                if (reveal && selected != null) Text(selected.meaning, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 5.dp))
                Text(if (reveal) "Tippe zum Verdecken" else "Tippe zum Aufdecken", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall)
            }
        }
        val learner = remember { AdaptiveLearning(context.applicationContext) }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            OutlinedButton(onClick = { selected?.let { onSpeak(it.word) } }, modifier = Modifier.weight(1f), enabled = selected != null) {
                Icon(Icons.Default.VolumeUp, null); Text("Anhören")
            }
            OutlinedButton(
                onClick = {
                    selected?.let { learner.recordAnswer("word:${it.word}", false) }
                    reveal = false
                    current = level78Words.map { DictionaryEntry(it.en, it.de + " · " + it.sentence) }
                        .filter { learner.isDue("word:${it.word}") }
                        .minByOrNull { learner.dueAt("word:${it.word}") }
                        ?: level78Words.randomOrNull()?.let { DictionaryEntry(it.en, it.de + " · " + it.sentence) }
                },
                modifier = Modifier.weight(1f),
                enabled = selected != null && reveal
            ) { Text("Nochmal") }
            Button(
                onClick = {
                    selected?.let {
                        learner.recordAnswer("word:${it.word}", true)
                        known++
                        onReward(5)
                    }
                    reveal = false
                    current = level78Words.map { DictionaryEntry(it.en, it.de + " · " + it.sentence) }
                        .filter { learner.isDue("word:${it.word}") }
                        .minByOrNull { learner.dueAt("word:${it.word}") }
                        ?: level78Words.randomOrNull()?.let { DictionaryEntry(it.en, it.de + " · " + it.sentence) }
                },
                modifier = Modifier.weight(1f),
                enabled = selected != null && reveal
            ) { Text("Gewusst +5 XP") }
        }
        Text(
            "Datenquelle: Wiktionary / Kaikki und WordNet · CC BY-SA 4.0. Offline-Nutzung ohne Konto oder Internet.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}

@Composable private fun ScanPage() {
    val context = LocalContext.current
    var result by remember { mutableStateOf("Wähle ein Foto mit englischem Text aus. Erkannter Text erscheint hier.") }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            result = "Bild wird gelesen …"
            try {
                val image = com.google.mlkit.vision.common.InputImage.fromFilePath(context, uri)
                com.google.mlkit.vision.text.TextRecognition.getClient(com.google.mlkit.vision.text.latin.TextRecognizerOptions.DEFAULT_OPTIONS).process(image)
                    .addOnSuccessListener { result = it.text.ifBlank { "Kein Text erkannt." } }
                    .addOnFailureListener { result = "Texterkennung fehlgeschlagen: ${it.localizedMessage}" }
            } catch (_: Exception) { result = "Bild konnte nicht geöffnet werden." }
        }
    }
    Column {
        Text("Foto-Scan / OCR", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("Fotografiere oder wähle englischen Text aus deiner Galerie. Die Erkennung läuft auf dem Gerät.", modifier = Modifier.padding(vertical = 12.dp))
        Button(onClick = { picker.launch("image/*") }) { Icon(Icons.Default.AddAPhoto, null); Spacer(Modifier.width(8.dp)); Text("Foto auswählen") }
        Card(Modifier.fillMaxWidth().padding(top = 16.dp)) { Text(result, Modifier.padding(16.dp)) }
    }
}
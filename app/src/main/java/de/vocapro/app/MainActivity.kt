package de.vocapro.app

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
    Word("experience","Erfahrung","Experience makes you better."), Word("to remember","sich erinnern","Remember the new words.")
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
    var dark by remember { mutableStateOf(true) }
    var page by remember { mutableStateOf("Start") }
    var xp by remember { mutableIntStateOf(store.int("xp")) }
    var streak by remember { mutableIntStateOf(store.int("streak")) }
    var todayCount by remember { mutableIntStateOf(if (store.string("count_day") == todayKey()) store.int("today_count") else 0) }
    val scheme = if (dark) darkColorScheme(primary = Color(0xFFB6C8FF), secondary = Color(0xFFB9D6C5), background = Color(0xFF10131A), surface = Color(0xFF1A202B)) else lightColorScheme(primary = Color(0xFF315DA8))
    fun reward(points: Int) {
        xp += points; store.setInt("xp", xp)
        todayCount++; store.setInt("today_count", todayCount); store.setString("count_day", todayKey())
        streak = store.recordStudy()
    }
    MaterialTheme(colorScheme = scheme) {
        Scaffold(bottomBar = {
            NavigationBar {
                listOf("Start","Training","Grammatik","Vokabeln","Scan").forEach { item ->
                    NavigationBarItem(selected = page == item, onClick = { page = item }, icon = {
                        Icon(when(item) { "Start" -> Icons.Default.Home; "Training" -> Icons.Default.Bolt; "Grammatik" -> Icons.Default.MenuBook; "Vokabeln" -> Icons.Default.Style; else -> Icons.Default.DocumentScanner }, contentDescription = item)
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
                    IconButton(onClick = { dark = !dark }) { Icon(if (dark) Icons.Default.LightMode else Icons.Default.DarkMode, "Design wechseln") }
                }
                Spacer(Modifier.height(12.dp))
                when(page) {
                    "Start" -> Dashboard(xp, streak, todayCount, onNavigate = { page = it })
                    "Training" -> TrainingPage(onReward = ::reward)
                    "Grammatik" -> GrammarPage(onReward = ::reward)
                    "Vokabeln" -> VocabularyPage(onSpeak, onReward = ::reward)
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
                        Text("🔥", style = MaterialTheme.typography.headlineLarge)
                    }
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                        Metric("🔥 $streak", "Tage Streak")
                        Metric("⭐ $xp", "XP gesammelt")
                        Metric("🏅 $level", "Level")
                    }
                    Spacer(Modifier.height(14.dp))
                    Text("Tagesziel: $todayCount / 10 richtige Übungen")
                    LinearProgressIndicator(progress = { (todayCount / 10f).coerceIn(0f,1f) }, modifier = Modifier.fillMaxWidth().padding(top = 6.dp))
                    Text(if (todayCount >= 10) "Tagesziel geschafft – stark! 🎉" else "Noch ${10-todayCount} bis zum Tagesziel. Du schaffst das!", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 5.dp))
                }
            }
        }
        item { Text("Was möchtest du heute trainieren?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
        item { FeatureCard("⚡","10-Minuten-Training","Gemischte Übungen für schnelles Lernen","Training",onNavigate) }
        item { FeatureCard("📚","Grammatik-Meister","Alle 13 Zeitformen verstehen und anwenden","Grammatik",onNavigate) }
        item { FeatureCard("🧠","Vokabel-Challenge","Wörter merken, hören und wiederholen","Vokabeln",onNavigate) }
        item { FeatureCard("📷","Foto-Scan","Englische Texte aus Bildern erkennen","Scan",onNavigate) }
        item { Text("Lern-Tipp: Wiederhole lieber täglich 10 Minuten als einmal pro Woche eine Stunde. Verteiltes Üben hilft deinem Gedächtnis.", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall) }
    }
}
@Composable private fun Metric(value: String, label: String) { Column { Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold); Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) } }
@Composable private fun FeatureCard(emoji: String, title: String, subtitle: String, page: String, onNavigate: (String) -> Unit) {
    Card(Modifier.fillMaxWidth().clickable { onNavigate(page) }, shape = RoundedCornerShape(20.dp)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(emoji, style = MaterialTheme.typography.headlineMedium)
            Column(Modifier.weight(1f).padding(horizontal = 14.dp)) { Text(title, fontWeight = FontWeight.Bold); Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall) }
            Icon(Icons.Default.ArrowForward, null)
        }
    }
}

@Composable private fun TrainingPage(onReward: (Int) -> Unit) {
    var index by remember { mutableIntStateOf(Random.nextInt(grammarQuestions.size)) }
    var answered by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf("") }
    var correct by remember { mutableStateOf(false) }
    var run by remember { mutableIntStateOf(0) }
    var best by remember { mutableIntStateOf(0) }
    val q = grammarQuestions[index]
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("⚡ Blitz-Training", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold); Text("🔥 $run richtig") }
        Text("Gemischte Grammatik-Fragen – lies den Satz und entscheide bewusst.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(18.dp))
        LinearProgressIndicator(progress = { ((index % grammarQuestions.size) + 1) / grammarQuestions.size.toFloat() }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(22.dp))
        Text(q.category.uppercase(Locale.GERMANY), color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Text(q.prompt, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 18.dp))
        q.options.forEach { option ->
            OutlinedButton(onClick = {
                if (!answered) {
                    selected = option; correct = option == q.answer; answered = true
                    if (correct) { run++; best = maxOf(best, run); onReward(10) } else run = 0
                }
            }, enabled = !answered, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), shape = RoundedCornerShape(16.dp)) { Text(option, modifier = Modifier.fillMaxWidth()) }
        }
        if (answered) {
            Card(Modifier.fillMaxWidth().padding(top = 14.dp), colors = CardDefaults.cardColors(containerColor = if (correct) Color(0xFF173D2C) else MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(16.dp)) {
                    Text(if (correct) "Richtig! +10 XP 🎉" else "Noch nicht – aus Fehlern lernt man.", fontWeight = FontWeight.Bold)
                    Text("Richtige Antwort: ${q.answer}", modifier = Modifier.padding(top = 5.dp))
                    Text(q.explanation, modifier = Modifier.padding(top = 7.dp))
                }
            }
            Button(onClick = { index = (index + Random.nextInt(1, grammarQuestions.size)).mod(grammarQuestions.size); answered = false; selected = "" }, modifier = Modifier.fillMaxWidth().padding(top = 14.dp)) { Text("Nächste Frage") }
        }
        Spacer(Modifier.height(8.dp))
        Text("Bestleistung in dieser Runde: $best richtige Antworten", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable private fun GrammarPage(onReward: (Int) -> Unit) {
    var selected by remember { mutableIntStateOf(-1) }
    var quizIndex by remember { mutableIntStateOf(0) }
    var answer by remember { mutableStateOf("") }
    var checked by remember { mutableStateOf(false) }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Text("Englische Zeitformen", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Lerne zuerst die Regel, dann erkenne sie im Satz. Tippe eine Zeitform an.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
        }
        items(tenses) { tense ->
            val i = tenses.indexOf(tense)
            Card(Modifier.fillMaxWidth().clickable { selected = if (selected == i) -1 else i }) {
                Column(Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) { Text(tense.name, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f)); Icon(if (selected == i) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null) }
                    Text(tense.pattern, color = MaterialTheme.colorScheme.primary)
                    if (selected == i) {
                        HorizontalDivider(Modifier.padding(vertical = 9.dp))
                        Text("Wann benutzt man das?", fontWeight = FontWeight.SemiBold)
                        Text(tense.use)
                        Text("Beispiel: ${tense.example}", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp))
                        Text(tense.translation, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        val q = grammarQuestions.firstOrNull { it.category == tense.name }
                        if (q != null) {
                            Text("Merke: ${q.explanation}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                }
            }
        }
        item {
            HorizontalDivider(Modifier.padding(vertical = 10.dp))
            Text("Mini-Prüfung", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            val q = grammarQuestions[quizIndex]
            Text(q.prompt, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 10.dp))
            q.options.forEach { option ->
                OutlinedButton(onClick = { if (!checked) { answer = option; checked = true; if (option == q.answer) onReward(15) } }, enabled = !checked, modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)) { Text(option, modifier = Modifier.fillMaxWidth()) }
            }
            if (checked) {
                Text(if (answer == q.answer) "Richtig! +15 XP" else "Richtige Antwort: ${q.answer}")
                Text(q.explanation, style = MaterialTheme.typography.bodySmall)
                Button(onClick = { quizIndex = (quizIndex + 1) % grammarQuestions.size; answer = ""; checked = false }) { Text("Weiter üben") }
            }
        }
    }
}

@Composable private fun VocabularyPage(onSpeak: (String) -> Unit, onReward: (Int) -> Unit) {
    var index by remember { mutableIntStateOf(0) }
    var reveal by remember { mutableStateOf(false) }
    var known by remember { mutableIntStateOf(0) }
    val word = words[index % words.size]
    Column(Modifier.fillMaxSize()) {
        Text("Vokabel-Challenge", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("Wort ${index % words.size + 1} von ${words.size} · $known sicher gewusst", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(18.dp))
        Card(Modifier.fillMaxWidth().height(260.dp).clickable { reveal = !reveal }, shape = RoundedCornerShape(26.dp)) {
            Column(Modifier.fillMaxSize().padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text(if (reveal) word.de else word.en, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(if (reveal) word.sentence else "Tippe auf die Karte für Bedeutung und Beispielsatz.", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 12.dp))
                if (!reveal) IconButton(onClick = { onSpeak(word.en) }) { Icon(Icons.Default.VolumeUp, "Aussprache anhören") }
                else Text("Beispiel anhören", color = MaterialTheme.colorScheme.primary, modifier = Modifier.clickable { onSpeak(word.sentence) }.padding(8.dp))
            }
        }
        Spacer(Modifier.height(14.dp))
        if (!reveal) Button(onClick = { reveal = true }, modifier = Modifier.fillMaxWidth()) { Text("Antwort aufdecken") }
        else {
            Text("Wie sicher sitzt das Wort?")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = { index++; reveal = false }, modifier = Modifier.weight(1f)) { Text("Nochmal") }
                Button(onClick = { known++; onReward(5); index++; reveal = false }, modifier = Modifier.weight(1f)) { Text("Gewusst! +5 XP") }
            }
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = { onSpeak(word.en) }, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.VolumeUp, null); Spacer(Modifier.width(6.dp)); Text("Englische Aussprache") }
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

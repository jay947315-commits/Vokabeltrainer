package de.vocapro.app

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import java.util.Locale

private data class Word(val en: String, val de: String)
private data class Tense(val name: String, val form: String, val use: String, val example: String, val translation: String)

private val words = listOf(
    Word("apple", "Apfel"), Word("beautiful", "schön"), Word("book", "Buch"), Word("to learn", "lernen"),
    Word("journey", "Reise"), Word("knowledge", "Wissen"), Word("quickly", "schnell"), Word("to improve", "verbessern"),
    Word("although", "obwohl"), Word("environment", "Umwelt")
)
private val tenses = listOf(
    Tense("Simple Present", "I play", "Gewohnheiten und Fakten", "She plays tennis every week.", "Sie spielt jede Woche Tennis."),
    Tense("Present Continuous", "I am playing", "Handlungen, die gerade passieren", "I am reading a book now.", "Ich lese gerade ein Buch."),
    Tense("Present Perfect", "I have played", "Vergangenheit mit Bezug zur Gegenwart", "We have finished our homework.", "Wir haben unsere Hausaufgaben beendet."),
    Tense("Present Perfect Continuous", "I have been playing", "Begann früher und dauert an oder wirkt nach", "She has been studying for two hours.", "Sie lernt seit zwei Stunden."),
    Tense("Simple Past", "I played", "Abgeschlossene Handlung in der Vergangenheit", "They visited London last year.", "Sie besuchten letztes Jahr London."),
    Tense("Past Continuous", "I was playing", "Im Verlauf befindliche Handlung in der Vergangenheit", "I was sleeping at ten.", "Ich schlief um zehn Uhr gerade."),
    Tense("Past Perfect", "I had played", "Vorvergangenheit: vor einem anderen Zeitpunkt", "He had left before I arrived.", "Er war gegangen, bevor ich ankam."),
    Tense("Past Perfect Continuous", "I had been playing", "Dauer bis zu einem Zeitpunkt in der Vergangenheit", "They had been waiting for an hour.", "Sie hatten eine Stunde gewartet."),
    Tense("Will Future", "I will play", "Spontane Entscheidungen und Vorhersagen", "I think it will rain.", "Ich glaube, es wird regnen."),
    Tense("Going-to Future", "I am going to play", "Pläne und begründete Absichten", "We are going to travel soon.", "Wir werden bald verreisen."),
    Tense("Future Continuous", "I will be playing", "Handlung zu einem Zeitpunkt in der Zukunft", "At six, I will be working.", "Um sechs werde ich arbeiten."),
    Tense("Future Perfect", "I will have played", "Bis zu einem Zeitpunkt abgeschlossen", "By Friday, I will have finished.", "Bis Freitag werde ich fertig sein."),
    Tense("Future Perfect Continuous", "I will have been playing", "Dauer bis zu einem zukünftigen Zeitpunkt", "In June, I will have been living here for a year.", "Im Juni werde ich seit einem Jahr hier wohnen.")
)

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
    var dark by remember { mutableStateOf(true) }
    var page by remember { mutableStateOf("Start") }
    val scheme = if (dark) darkColorScheme(primary = Color(0xFF9DBBFF), secondary = Color(0xFFB8C8E8), background = Color(0xFF10131A), surface = Color(0xFF1A1F29)) else lightColorScheme(primary = Color(0xFF315DA8))
    MaterialTheme(colorScheme = scheme) {
        Scaffold(bottomBar = {
            NavigationBar {
                listOf("Start", "Lernen", "Zeitformen", "Test", "Scan").forEach { item ->
                    NavigationBarItem(selected = page == item, onClick = { page = item }, icon = { Icon(when(item) { "Start" -> Icons.Default.Home; "Lernen" -> Icons.Default.Style; "Zeitformen" -> Icons.Default.MenuBook; "Test" -> Icons.Default.Quiz; else -> Icons.Default.DocumentScanner }, contentDescription = item) }, label = { Text(item) })
                }
            }
        }) { padding ->
            Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(padding).padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.weight(1f)) { Text("VocaPro", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold); Text("Englisch. Einfach besser.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    IconButton(onClick = { dark = !dark }) { Icon(if (dark) Icons.Default.LightMode else Icons.Default.DarkMode, "Design wechseln") }
                }
                Spacer(Modifier.height(18.dp))
                when(page) {
                    "Start" -> HomePage(onNavigate = { page = it })
                    "Lernen" -> FlashcardPage(onSpeak)
                    "Zeitformen" -> TensesPage()
                    "Test" -> QuizPage()
                    else -> ScanPage()
                }
            }
        }
    }
}

@Composable private fun HomePage(onNavigate: (String) -> Unit) {
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(20.dp)) {
            Text("Dein Lernplatz", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Lerne Vokabeln, übe alle wichtigen Zeitformen und teste dein Wissen.", modifier = Modifier.padding(top = 8.dp, bottom = 16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { Stat("13", "Zeitformen"); Stat("10", "Start-Vokabeln"); Stat("100%", "kostenlos") }
        }
    }
    Spacer(Modifier.height(16.dp))
    listOf("Lernen" to "Karteikarten mit Aussprache", "Zeitformen" to "Alle 13 englischen Zeitformen", "Test" to "Wissen direkt überprüfen", "Scan" to "Text aus einem Foto erkennen").forEach { (title, sub) ->
        Card(Modifier.fillMaxWidth().padding(vertical = 5.dp).clickable { onNavigate(title) }) { Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text(title, fontWeight = FontWeight.SemiBold); Text(sub, color = MaterialTheme.colorScheme.onSurfaceVariant) }; Icon(Icons.Default.ArrowForward, null) } }
    }
}
@Composable private fun Stat(value: String, label: String) { Column(Modifier.padding(end = 12.dp)) { Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold); Text(label, style = MaterialTheme.typography.labelSmall) } }

@Composable private fun FlashcardPage(onSpeak: (String) -> Unit) {
    var index by remember { mutableIntStateOf(0) }; var reveal by remember { mutableStateOf(false) }
    val word = words[index % words.size]
    Text("Karteikarten", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    Text("Karte ${index % words.size + 1} von ${words.size}", color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(Modifier.height(18.dp))
    Card(Modifier.fillMaxWidth().height(230.dp).clickable { reveal = !reveal }, shape = RoundedCornerShape(28.dp)) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(if (reveal) word.de else word.en, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(if (reveal) "Deutsch" else "Tippen zum Aufdecken", color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (!reveal) IconButton(onClick = { onSpeak(word.en) }) { Icon(Icons.Default.VolumeUp, "Aussprache") }
        }
    }
    Spacer(Modifier.height(16.dp)); Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedButton(onClick = { index = (index + words.size - 1) % words.size; reveal = false }, modifier = Modifier.weight(1f)) { Text("Zurück") }
        Button(onClick = { index++; reveal = false }, modifier = Modifier.weight(1f)) { Text("Nächste Karte") }
    }
}

@Composable private fun TensesPage() {
    var selected by remember { mutableIntStateOf(-1) }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(tenses) { tense ->
            Card(Modifier.fillMaxWidth().clickable { selected = tenses.indexOf(tense) }) {
                Column(Modifier.padding(14.dp)) {
                    Text(tense.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text(tense.form, color = MaterialTheme.colorScheme.primary)
                    if (selected == tenses.indexOf(tense)) { HorizontalDivider(Modifier.padding(vertical = 8.dp)); Text("Wann? ${tense.use}"); Text("Beispiel: ${tense.example}", modifier = Modifier.padding(top = 6.dp)); Text(tense.translation, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                }
            }
        }
    }
}

@Composable private fun QuizPage() {
    var index by remember { mutableIntStateOf(0) }; var score by remember { mutableIntStateOf(0) }; var answered by remember { mutableStateOf(false) }
    val q = words[index % words.size]
    val options = remember(index) { listOf(q.de, words[(index + 2) % words.size].de, words[(index + 4) % words.size].de).shuffled() }
    Text("Vokabel-Test", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    Text("Punkte: $score", color = MaterialTheme.colorScheme.primary)
    Spacer(Modifier.height(22.dp)); Text("Was bedeutet …", style = MaterialTheme.typography.titleMedium); Text(q.en, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(16.dp))
    options.forEach { option -> Button(onClick = { if (!answered && option == q.de) score++; answered = true }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), enabled = !answered) { Text(option) } }
    if (answered) { Text("Lösung: ${q.de}"); Button(onClick = { index++; answered = false }) { Text("Nächste Frage") } }
}

@Composable private fun ScanPage() {
    val context = LocalContext.current
    var result by remember { mutableStateOf("Wähle ein Foto mit englischem Text aus. Die Texterkennung wird vorbereitet.") }
    val picker = androidx.activity.compose.rememberLauncherForActivityResult(androidx.activity.result.contract.ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            result = "Bild wird gelesen …"
            try {
                val image = com.google.mlkit.vision.common.InputImage.fromFilePath(context, uri)
                com.google.mlkit.vision.text.TextRecognition.getClient(com.google.mlkit.vision.text.latin.TextRecognizerOptions.DEFAULT_OPTIONS).process(image)
                    .addOnSuccessListener { result = it.text.ifBlank { "Kein Text erkannt." } }
                    .addOnFailureListener { result = "Texterkennung fehlgeschlagen: ${it.localizedMessage}" }
            } catch (e: Exception) { result = "Bild konnte nicht geöffnet werden." }
        }
    }
    Text("Foto-Scan / OCR", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    Text("Wähle ein Foto oder eine Aufnahme aus deiner Galerie. Erkannter Text erscheint darunter.", modifier = Modifier.padding(vertical = 12.dp))
    Button(onClick = { picker.launch("image/*") }) { Icon(Icons.Default.AddAPhoto, null); Spacer(Modifier.width(8.dp)); Text("Foto auswählen") }
    Card(Modifier.fillMaxWidth().padding(top = 16.dp)) { Text(result, Modifier.padding(16.dp)) }
}

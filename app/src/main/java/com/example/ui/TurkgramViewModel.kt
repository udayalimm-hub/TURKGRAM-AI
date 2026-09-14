package com.example.ui

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.SavedPostDao
import com.example.data.local.SavedPostEntity
import com.example.data.local.TurkgramDatabase
import com.example.data.model.ConsultantCategory
import com.example.data.model.MessageSender
import com.example.data.model.QuickPrompt
import com.example.data.model.TurkgramContentParser
import com.example.data.model.TurkgramIdea
import com.example.data.model.TurkgramMessage
import com.example.data.remote.GeminiService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class UiEvent {
    data class ShowToast(val message: String) : UiEvent()
}

class TurkgramViewModel(application: Application) : AndroidViewModel(application) {

    private val geminiService = GeminiService()
    private val database = TurkgramDatabase.getInstance(application)
    private val savedPostDao: SavedPostDao = database.savedPostDao()

    private val _messages = MutableStateFlow<List<TurkgramMessage>>(emptyList())
    val messages: StateFlow<List<TurkgramMessage>> = _messages.asStateFlow()

    private val _selectedCategory = MutableStateFlow(ConsultantCategory.ALL)
    val selectedCategory: StateFlow<ConsultantCategory> = _selectedCategory.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow: SharedFlow<UiEvent> = _eventFlow.asSharedFlow()

    val savedPosts: StateFlow<List<SavedPostEntity>> = savedPostDao.getAllSavedPosts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val quickPrompts: List<QuickPrompt> = listOf(
        QuickPrompt(
            title = "Viral Instagram Reels",
            emoji = "🎬",
            query = "Instagram Reels için bu hafta sonu viral olabilecek yaratıcı ve akım senaryosu hazırla.",
            category = ConsultantCategory.IDEAS
        ),
        QuickPrompt(
            title = "Kahve & Estetik Carousel",
            emoji = "☕️",
            query = "Instagram için çok kaydırmalı (Carousel) estetik bir kahve ve çalışma masası gönderi konsepti hazırla.",
            category = ConsultantCategory.IDEAS
        ),
        QuickPrompt(
            title = "Midjourney Insta Promptu",
            emoji = "🎨",
            query = "Instagram akışında paylaşmak için sıcak, loş ışıklı estetik bir İstanbul kafesi Midjourney görsel promptu yaz.",
            category = ConsultantCategory.PROMPT
        ),
        QuickPrompt(
            title = "Keşfet & Saat Tüyoları",
            emoji = "🚀",
            query = "Instagram Keşfet algoritması nasıl çalışıyor ve Türkiye'de paylaşım için en iyi saatler hangileri?",
            category = ConsultantCategory.GROWTH
        ),
        QuickPrompt(
            title = "Kombin (OOTD) Caption",
            emoji = "👗",
            query = "Instagram'da şık bir sonbahar sokak kombini paylaşıyorum, etkileşim artıran samimi bir caption ve hashtag listesi ver.",
            category = ConsultantCategory.CAPTIONS
        ),
        QuickPrompt(
            title = "Story Anket & Etkileşim",
            emoji = "📱",
            query = "Instagram hikayelerinde (Story) takipçilerin cevap vermesini sağlayacak yaratıcı bir anket/soru serisi öner.",
            category = ConsultantCategory.IDEAS
        ),
        QuickPrompt(
            title = "DALL-E Galata Gün Batımı",
            emoji = "🌅",
            query = "DALL-E için Galata Kulesi ve gün batımı temalı ultra gerçekçi bir yapay zeka görsel promptu hazırla.",
            category = ConsultantCategory.PROMPT
        )
    )

    init {
        resetChat()
    }

    fun clearChat() {
        resetChat()
    }

    private fun resetChat() {
        val initialAssistantText = """
            Selam! Instagram Yapay Zeka Danışmanına hoş geldin! 🚀✨

            Instagram profilini büyütmek, Keşfet'e düşmek ve akışında viral içerikler üretmek için buradayım!

            Neler yapabiliriz?
            • 🎬 Viral Instagram Reels & Akım senaryoları
            • 🎨 Midjourney & DALL-E Instagram estetiği promptları
            • ✍️ Etkileşim patlatacak samimi caption ve trend hashtag'ler
            • 📈 Keşfet algoritması sırları, Carousel stratejileri ve paylaşım saatleri

            Hemen yukarıdaki hazır önerilerden birini seçebilir veya aklındaki konsepti yazabilirsin!
        """.trimIndent()

        val welcomeIdea = TurkgramIdea(
            concept = "Güne Başlarken Türk Kahvesi & Minimalist Carousel",
            scenario = "Sabah gün ışığı alan ahşap masada, yanında not defteri ve köpüklü kahve olan 4:5 estetik kare.",
            caption = "Günün ilk kahvesi içilmeden moda girilemiyor diyenler kimler? ☕️✨ Instagram akışına biraz sabah huzuru bırakıyorum.",
            hashtags = listOf("#Instagram", "#KahveKeyfi", "#GününKaresi", "#Keşfet", "#Reels", "#Aesthetic"),
            aiPrompt = "Aesthetic modern desk with Turkish coffee, warm morning light, notebook and laptop, Instagram aesthetic --ar 4:5"
        )

        _messages.value = listOf(
            TurkgramMessage(
                sender = MessageSender.ASSISTANT,
                text = initialAssistantText,
                parsedIdea = welcomeIdea,
                category = ConsultantCategory.ALL
            )
        )
    }

    fun onInputTextChanged(text: String) {
        _inputText.value = text
    }

    fun setCategory(category: ConsultantCategory) {
        _selectedCategory.value = category
    }

    fun selectQuickPrompt(prompt: QuickPrompt) {
        _selectedCategory.value = prompt.category
        sendMessage(prompt.query)
    }

    fun sendMessage(content: String = _inputText.value) {
        val trimmed = content.trim()
        if (trimmed.isBlank() || _isLoading.value) return

        val userMsg = TurkgramMessage(
            sender = MessageSender.USER,
            text = trimmed,
            category = _selectedCategory.value
        )

        _messages.value = _messages.value + userMsg
        _inputText.value = ""
        _isLoading.value = true

        viewModelScope.launch {
            val history = _messages.value.takeLast(6).map {
                (if (it.sender == MessageSender.USER) "user" else "model") to it.text
            }

            val result = geminiService.generateContent(
                userMessage = trimmed,
                category = _selectedCategory.value,
                chatHistory = history
            )

            _isLoading.value = false

            val replyText = result.getOrElse { "Bağlantı hatası oluştu. Lütfen tekrar dene!" }
            val parsed = TurkgramContentParser.parse(replyText)

            val assistantMsg = TurkgramMessage(
                sender = MessageSender.ASSISTANT,
                text = replyText,
                parsedIdea = parsed,
                category = _selectedCategory.value
            )

            _messages.value = _messages.value + assistantMsg
        }
    }

    fun toggleSaveIdea(idea: TurkgramIdea) {
        viewModelScope.launch {
            val existing = savedPostDao.countByConcept(idea.concept)
            if (existing > 0) {
                savedPostDao.deleteByContent(idea.concept, idea.caption)
                _eventFlow.emit(UiEvent.ShowToast("Kaydedilenlerden kaldırıldı."))
            } else {
                val entity = SavedPostEntity(
                    concept = idea.concept,
                    scenario = idea.scenario,
                    caption = idea.caption,
                    hashtagsJson = idea.hashtags.joinToString(" "),
                    aiPrompt = idea.aiPrompt,
                    category = _selectedCategory.value.name
                )
                savedPostDao.insertPost(entity)
                _eventFlow.emit(UiEvent.ShowToast("İçerik fikri kaydedildi! ⭐️"))
            }
        }
    }

    fun deleteSavedPost(id: Long) {
        viewModelScope.launch {
            savedPostDao.deleteById(id)
            _eventFlow.emit(UiEvent.ShowToast("İçerik silindi."))
        }
    }

    fun copyToClipboard(text: String, label: String = "Metin") {
        val clipboard = getApplication<Application>()
            .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        viewModelScope.launch {
            _eventFlow.emit(UiEvent.ShowToast("$label kopyalandı! ✨"))
        }
    }
}

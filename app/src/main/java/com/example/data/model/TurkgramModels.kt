package com.example.data.model

import java.util.UUID

enum class MessageSender {
    USER, ASSISTANT
}

enum class ConsultantCategory(
    val title: String,
    val shortLabel: String,
    val iconEmoji: String,
    val placeholder: String
) {
    ALL("Tümü & Sohbet", "Sohbet", "✨", "Instagram hakkında soru sor veya fikir iste..."),
    IDEAS("Reels & Gönderi", "Reels", "🎬", "Instagram Reels veya post fikri üretmek için konuyu yaz..."),
    PROMPT("Görsel & Prompt", "Prompt", "🎨", "Instagram için Midjourney / DALL-E görsel konusu..."),
    CAPTIONS("Caption & Hashtag", "Caption", "✍️", "Instagram gönderin için açıklama ve etiket iste..."),
    GROWTH("Keşfet & Algoritma", "Keşfet", "📈", "Instagram algoritması, etkili saatler veya büyüme tüyoları...")
}

data class TurkgramIdea(
    val concept: String,
    val scenario: String,
    val caption: String,
    val hashtags: List<String>,
    val aiPrompt: String? = null
)

data class TurkgramMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val parsedIdea: TurkgramIdea? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val category: ConsultantCategory = ConsultantCategory.ALL,
    val isSaved: Boolean = false,
    val isLoading: Boolean = false
)

data class QuickPrompt(
    val title: String,
    val emoji: String,
    val query: String,
    val category: ConsultantCategory
)

object TurkgramContentParser {
    fun parse(rawText: String): TurkgramIdea? {
        val lower = rawText.lowercase()
        val hasConcept = lower.contains("konsept:") || lower.contains("i̇çerik başlığı") || lower.contains("icerik basligi")
        val hasScenario = lower.contains("senaryo") || lower.contains("görsel senaryosu") || lower.contains("video senaryosu")
        val hasCaption = lower.contains("açıklama metni:") || lower.contains("aciklama metni:") || lower.contains("caption:")
        val hasHashtags = lower.contains("hashtag") || lower.contains("etiket") || lower.contains("#turkgram")

        // If at least 2 key markers are present, parse fields
        if (!hasConcept && !hasScenario && !hasCaption && !hasHashtags) {
            return null
        }

        var concept = ""
        var scenario = ""
        var caption = ""
        val hashtags = mutableListOf<String>()
        var aiPrompt: String? = null

        val lines = rawText.lines()
        var currentSection = ""
        val conceptBuffer = StringBuilder()
        val scenarioBuffer = StringBuilder()
        val captionBuffer = StringBuilder()
        val aiPromptBuffer = StringBuilder()

        for (line in lines) {
            val trimmed = line.trim()
            val lowerLine = trimmed.lowercase()

            when {
                lowerLine.startsWith("konsept:") || lowerLine.startsWith("içerik başlığı / konsepti:") ||
                        lowerLine.startsWith("i̇çerik başlığı / konsepti:") || lowerLine.startsWith("konsept :") -> {
                    currentSection = "CONCEPT"
                    val content = trimmed.substringAfter(":").trim().removeSurrounding("\"")
                    if (content.isNotEmpty()) conceptBuffer.append(content)
                }
                lowerLine.startsWith("görsel senaryosu:") || lowerLine.startsWith("görsel/video senaryosu:") ||
                        lowerLine.startsWith("video senaryosu:") || lowerLine.startsWith("senaryo:") -> {
                    currentSection = "SCENARIO"
                    val content = trimmed.substringAfter(":").trim()
                    if (content.isNotEmpty()) scenarioBuffer.append(content)
                }
                lowerLine.startsWith("açıklama metni:") || lowerLine.startsWith("gönderi/açıklama metni (caption):") ||
                        lowerLine.startsWith("gönderi/açıklama metni:") || lowerLine.startsWith("caption:") -> {
                    currentSection = "CAPTION"
                    val content = trimmed.substringAfter(":").trim().removeSurrounding("\"")
                    if (content.isNotEmpty()) captionBuffer.append(content)
                }
                lowerLine.startsWith("hashtag'ler:") || lowerLine.startsWith("hashtagler:") ||
                        lowerLine.startsWith("hashtag önerileri:") || lowerLine.startsWith("hashtag:") ||
                        lowerLine.startsWith("etiketler:") -> {
                    currentSection = "HASHTAGS"
                    val content = trimmed.substringAfter(":").trim()
                    extractHashtags(content, hashtags)
                }
                lowerLine.startsWith("ai görsel promptu:") || lowerLine.startsWith("midjourney prompt:") ||
                        lowerLine.startsWith("dall-e prompt:") || lowerLine.startsWith("görsel promptu:") -> {
                    currentSection = "AI_PROMPT"
                    val content = trimmed.substringAfter(":").trim()
                    if (content.isNotEmpty()) aiPromptBuffer.append(content)
                }
                else -> {
                    when (currentSection) {
                        "CONCEPT" -> if (trimmed.isNotEmpty()) conceptBuffer.append(" ").append(trimmed)
                        "SCENARIO" -> if (trimmed.isNotEmpty()) scenarioBuffer.append("\n").append(trimmed)
                        "CAPTION" -> if (trimmed.isNotEmpty()) captionBuffer.append("\n").append(trimmed)
                        "AI_PROMPT" -> if (trimmed.isNotEmpty()) aiPromptBuffer.append(" ").append(trimmed)
                        "HASHTAGS" -> extractHashtags(trimmed, hashtags)
                    }
                }
            }
        }

        // Also scan the whole text for hashtags if none parsed
        if (hashtags.isEmpty()) {
            val hashtagRegex = Regex("""#\w+""")
            hashtagRegex.findAll(rawText).forEach { match ->
                hashtags.add(match.value)
            }
        }

        concept = conceptBuffer.toString().trim()
        scenario = scenarioBuffer.toString().trim()
        caption = captionBuffer.toString().trim()
        aiPrompt = aiPromptBuffer.toString().trim().ifEmpty { null }

        // If concept is still empty, try to grab from first lines or title
        if (concept.isEmpty() && caption.isNotEmpty()) {
            concept = "Turkgram Özel İçeriği"
        }

        if (concept.isNotEmpty() || scenario.isNotEmpty() || caption.isNotEmpty() || hashtags.isNotEmpty()) {
            return TurkgramIdea(
                concept = concept.ifEmpty { "Turkgram Trend Fikri" },
                scenario = scenario.ifEmpty { "Doğal ışıkta çekilmiş dinamik bir kare/video senaryosu." },
                caption = caption.ifEmpty { "Harika bir Turkgram anı! ✨" },
                hashtags = hashtags.ifEmpty { listOf("#Turkgram", "#Keşfet", "#Trend") },
                aiPrompt = aiPrompt
            )
        }
        return null
    }

    private fun extractHashtags(line: String, targetList: MutableList<String>) {
        val parts = line.split(Regex("""[\s,]+"""))
        for (part in parts) {
            val clean = part.trim()
            if (clean.startsWith("#") && clean.length > 1) {
                if (!targetList.contains(clean)) {
                    targetList.add(clean)
                }
            }
        }
    }
}

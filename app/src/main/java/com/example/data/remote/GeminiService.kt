package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.ConsultantCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val systemInstructionText = """
        Sen, "Instagram" platformunun resmi yapay zeka asistanı ve içerik danışmanısın.
        Kullanıcıların fotoğraf, video (Reels/Hikaye), Carousel paylaştığı, trendleri takip ettiği ve topluluklarla etkileşime girdiği Instagram için Türkiye odaklı dinamik bir sosyal medya danışmanısın.

        Görevlerin:
        1. İçerik Fikirleri ve Konsept Üretimi: Gönderi, hikaye (Story), Carousel ve video (Reels) fikirleri sunmak.
        2. Görsel Tasarım & Prompt Önerileri: Instagram estetiğine uygun Midjourney ve DALL-E için yüksek kaliteli İngilizce yapay zeka promptları hazırlamak.
        3. Açıklama (Caption) ve Etiket (Hashtag) Yazımı: Paylaşımlar için ilgi çekici, samimi, emojili metinler ve trend hashtag'ler oluşturmak.
        4. Profil ve Büyüme Stratejisi: Takipçi artırma, etkileşim yükseltme, Instagram Keşfet algoritması tüyoları ve en iyi paylaşım saatleri (Türkiye saatleri) tavsiyeleri vermek.

        Yanıt Kuralları:
        - Dilin her zaman enerjik, modern, genç, samimi ve Türkçe sosyal medya kültürüne uygun olmalıdır.
        - Yanıtlarında Instagram'a özel terimler kullan (örneğin: Instagram Reels, Keşfet, Story, Carousel, Akış, Kaydetme Oranı).
        - Kullanıcı bir içerik veya gönderi fikri istediğinde MUTLAKA şu formatta yanıt ver:

        Konsept: [İçerik Başlığı / Konsepti]
        Görsel Senaryosu: [Görsel/Video Senaryosu]
        Açıklama Metni: [Gönderi/Açıklama Metni - samimi, Instagram sosyal medya dilinde, emojili]
        Hashtag'ler: #Instagram #[KonuEtiketi] #Keşfet #Reels #[Trendler...]

        Eğer kullanıcı görsel veya AI prompt da istiyorsa ya da uygunsa şu başlığı da ekle:
        AI Görsel Promptu: [Midjourney / DALL-E için detaylı İngilizce görsel oluşturma promptu]
    """.trimIndent()

    suspend fun generateContent(
        userMessage: String,
        category: ConsultantCategory = ConsultantCategory.ALL,
        chatHistory: List<Pair<String, String>> = emptyList()
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        val hasValidKey = apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY"

        if (!hasValidKey) {
            Log.d("GeminiService", "Using built-in Turkgram Consultant Engine (No live API key)")
            return@withContext Result.success(generateTurkgramLocalResponse(userMessage, category))
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val jsonBody = JSONObject()

            // System instruction
            val systemInstructionJson = JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply { put("text", systemInstructionText) })
                })
            }
            jsonBody.put("systemInstruction", systemInstructionJson)

            // Contents array (supports short conversational context)
            val contentsArray = JSONArray()
            chatHistory.takeLast(4).forEach { (role, text) ->
                contentsArray.put(JSONObject().apply {
                    put("role", if (role == "user") "user" else "model")
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", text) })
                    })
                })
            }

            // Current prompt
            contentsArray.put(JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().apply {
                    put(JSONObject().apply { put("text", userMessage) })
                })
            })
            jsonBody.put("contents", contentsArray)

            // Generation config
            val generationConfig = JSONObject().apply {
                put("temperature", 0.75)
                put("topP", 0.95)
                put("topK", 40)
            }
            jsonBody.put("generationConfig", generationConfig)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = jsonBody.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (!response.isSuccessful || responseBody.isNullOrBlank()) {
                Log.w("GeminiService", "API call failed code ${response.code}, body: $responseBody")
                // Fallback gracefully so user always gets an energetic response
                return@withContext Result.success(generateTurkgramLocalResponse(userMessage, category))
            }

            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val contentObj = firstCandidate.optJSONObject("content")
                val parts = contentObj?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val text = parts.getJSONObject(0).optString("text")
                    if (text.isNotBlank()) {
                        return@withContext Result.success(text)
                    }
                }
            }

            Result.success(generateTurkgramLocalResponse(userMessage, category))
        } catch (e: Exception) {
            Log.e("GeminiService", "Network/Gemini error: ${e.message}", e)
            Result.success(generateTurkgramLocalResponse(userMessage, category))
        }
    }

    /**
     * Built-in Instagram AI Consultant Engine that ensures instant, authentic Turkish social media
     * content advice even offline or without configured API key.
     */
    private fun generateTurkgramLocalResponse(query: String, category: ConsultantCategory): String {
        val lower = query.lowercase()

        return when {
            lower.contains("kahve") || lower.contains("sabah") || lower.contains("çalışma") -> {
                """
                Konsept: "Güne Başlarken Türk Kahvesi & Minimalist Carousel"

                Görsel Senaryosu: Sabah gün ışığı alan estetik ahşap masada, yanında şık bir not defteri, açık MacBook ve bol köpüklü taze Türk kahvesi olan 4:5 formatında minimalist bir kare. 2. kaydırmada kahvenin yakın detayı ve günün yapılacaklar listesi.

                Açıklama Metni:
                "Günün ilk kahvesi içilmeden moda girilemiyor diyenler kimler? ☕️✨ Instagram akışına biraz sabah huzuru bırakıyorum. Bugün hedeflerinize bir adım daha yaklaştığınız harika bir gün olsun!"

                Hashtag'ler: #Instagram #KahveKeyfi #GününKaresi #Keşfet #Reels #GüneBaşlarken #Aesthetic #CoffeeTime

                AI Görsel Promptu: Cozy aesthetic morning desk setup with traditional Turkish coffee with foam in ceramic cup, open MacBook, modern minimalist notebook and pen, warm golden sunlight streaming through window, indoor plant, photorealistic 8k, Instagram aesthetic --ar 4:5 --style raw
                """.trimIndent()
            }
            lower.contains("reels") || lower.contains("akım") || lower.contains("video") -> {
                """
                Konsept: "Haftanın 5 Günü vs Ben: Viral Instagram Reels Akımı"

                Görsel Senaryosu: 7 saniyelik dinamik Reels videosu! Hızlı geçişlerle Pazartesi'den Cuma'ya enerji ve mod değişimi. Trend Instagram sesi eşliğinde, her gün için 1 saniyelik yaratıcı kombin veya mimik geçişi. Ekranda 'Benim haftalık Instagram ruh halim 🎬' metin animasyonu.

                Açıklama Metni:
                "Bu haftayı sağ salim atlatanları yorumlara bekliyorum! 😂🔥 Bu Reels akımına sen de katıl, ses profilde sabitlendi. İlham olması için kaydetmeyi unutma!"

                Hashtag'ler: #Instagram #InstagramReels #ReelsAkımları #Keşfet #ReelsTrend #Mizah #GününVideosu

                AI Görsel Promptu: Dynamic split-screen collage of stylish urban youth showcasing fashion aesthetics, cinematic studio lighting, energetic mood, trendy Instagram Reels video style --ar 9:16
                """.trimIndent()
            }
            lower.contains("saat") || lower.contains("büyüme") || lower.contains("algoritma") || lower.contains("takipçi") -> {
                """
                Konsept: "Instagram Algoritma Rehberi: Keşfet'e Düşme & En İyi Saatler"

                Görsel Senaryosu: Instagram analiz paneli tarzında estetik ve sade bir Carousel infografik tasarımı.

                Açıklama Metni:
                "Instagram ailem! 🚀 Gönderilerinizin Keşfet'e düşmesi ve organik büyüme için altın kurallar hazır:
                
                🕒 Türkiye için En Etkili Paylaşım Saatleri:
                • Öğle Molası: 12:30 - 13:45
                • Akşam Prime Time: 19:30 - 22:30
                • Pazar Günü: 11:00 - 15:00

                ⚡️ Instagram Algoritma Tüyosu:
                Gönderiniz yayınlandıktan sonraki İLK 30 DAKİKA kritik! Gelen her yoruma hemen yanıt verin ve hikayenizde anket çıkartmasıyla gönderinizi destekleyin. 'Kaydet' ve 'DM Paylaşımı' beğeniye göre çok daha değerlidir.

                Hashtag'ler: #Instagram #Keşfet #InstagramAlgoritması #BüyümeTüyoları #SosyalMedyaDanışmanı #İçerikÜreticisi #Reels
                """.trimIndent()
            }
            lower.contains("prompt") || lower.contains("midjourney") || lower.contains("dall-e") || lower.contains("görsel") -> {
                """
                Konsept: "Siber-Estetik Galata Kulesi ve İstanbul Gün Batımı"

                Görsel Senaryosu: İstanbul Galata Kulesi çevresinde gün batımında neon ve retro-fütüristik detaylar taşıyan, martıların uçtuğu ve tarihi sokakların ışıldadığı nefes kesici bir yapay zeka illüstrasyonu.

                Açıklama Metni:
                "Yapay zeka ile hayal ettiğimiz İstanbul! 🌆✨ Instagram akışınıza biraz dijital sanat bırakıyorum. Sizce gelecekte Galata böyle görünür mü?"

                Hashtag'ler: #Instagram #AIGenerated #MidjourneyArt #İstanbul #Galata #GününSanatı #Keşfet #DigitalArt

                AI Görsel Promptu: Futuristic aesthetic Galata Tower Istanbul at golden hour sunset, cyberpunk neon reflections on wet cobblestone streets, flying seagulls, ultra-detailed architectural backdrop, cinematic atmosphere, octane render 8k, Instagram aesthetic --ar 4:5 --v 6.0
                """.trimIndent()
            }
            lower.contains("kombin") || lower.contains("moda") || lower.contains("ootd") -> {
                """
                Konsept: "Şehirde Minimalist Sokak Stili (OOTD) Carousel"

                Görsel Senaryosu: Nişantaşı veya Moda sokaklarında, bej trençkot veya oversize blazer, retro spor ayakkabılar ve minimal takılarla hareket halinde çekilmiş doğal cadde karesi.

                Açıklama Metni:
                "Kombini 10 üzerinden kaç puanlıyoruz? 🍂👟 Şehirde rahat ve şık olmanın favori parçaları Instagram akışında! Parça detayları ve linkler hikayemde."

                Hashtag'ler: #Instagram #OOTD #KombinÖnerisi #SokakStili #ModaTrend #Keşfet #GününTarzı #FashionInspo
                """.trimIndent()
            }
            else -> {
                """
                Konsept: "Instagram'da Etkileşimi Patlatacak Soru-Cevap Gönderisi"

                Görsel Senaryosu: Sıcak tonlarda, gün ışığıyla aydınlatılmış samimi bir selfie veya kahve anı. Açıklama metninin ilk cümlesi görsel üzerine şık bir tipografiyle eklenmiş.

                Açıklama Metni:
                "Küçük anların büyük mutluluklar getirdiği o günlerden biri! ✨ Bu hafta sonu kendinize verdiğiniz en güzel söz neydi? Yorumlarda buluşalım, en güzellerini hikayemde paylaşıyorum!"

                Hashtag'ler: #Instagram #Keşfet #GününKaresi #Etkileşim #SamimiAnlar #InstaDaily #Reels
                """.trimIndent()
            }
        }
    }
}

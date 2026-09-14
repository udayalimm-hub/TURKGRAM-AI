# 📱 Instagram AI - Yapay Zeka İçerik Danışmanı & Asistanı

![Instagram AI Banner](app/src/main/res/drawable/instagram_ai_hero.jpg)

Instagram için geliştirilmiş **yeni nesil yapay zeka asistanı ve içerik danışmanı**. Viral Reels akımları, çoklu kaydırmalı (Carousel) gönderiler, Keşfet algoritması tüyoları, Midjourney / DALL-E görsel promptları ve etkileşimi artıran Türkçe caption & hashtag önerileri sunar.

---

## 📥 APK İndir (Download Android APK)

Uygulamanın hazır Android kurulum (`.apk`) dosyasına aşağıdaki yollardan hemen ulaşabilirsiniz:

### 1. 🚀 Depodan Doğrudan İndir (apk/turkgramai.apk)
Proje deposundaki `apk` klasöründe yer alan güncel APK dosyasını doğrudan indirebilirsiniz:
* 📦 **[turkgramai.apk İndir (apk/turkgramai.apk)](./apk/turkgramai.apk?raw=true)** *(Dosya boyutu: ~26 MB)*

### 2. 🏷️ GitHub Releases & Actions Üzerinden
* Proje sayfasındaki **[Releases](../../releases)** sekmesinden `turkgramai.apk` dosyasını indirebilirsiniz.
* Depoya her kod gönderildiğinde (`push`), `.github/workflows/build-apk.yml` otomatik olarak en güncel APK'yı derler ve **apk/turkgramai.apk** olarak hem depoda günceller hem de **Actions > Artifacts** sekmesine yükler.

### 3. ⚡ Google AI Studio Üzerinden İndir
* AI Studio arayüzünde sağ üstteki ayarlar / menü simgesine tıklayın.
* **"Generate APK / Export"** seçeneğini seçerek cihazınıza doğrudan indirebilirsiniz.

---

## 📲 Android Cihaza APK Yükleme Adımları

1. Yukarıdaki linkten **`Instagram-AI.apk`** dosyasını Android telefonunuza veya tabletinize indirin.
2. Bildirim çubuğundan veya Dosya Yöneticisi / İndirilenler klasöründen indirilen `.apk` dosyasına dokunun.
3. Android güvenlik uyarısı gelirse:
   * **"Ayarlar"**'a gidin ve **"Bu kaynaktan izin ver / Bilinmeyen uygulamaları yükle"** seçeneğini aktif edin.
4. **"Yükle"** butonuna basın ve kurulum tamamlandığında **"Aç"** diyerek Instagram AI asistanınızı hemen kullanmaya başlayın!

---

## 🌟 Öne Çıkan Özellikler

| Özellik | Açıklama |
| :--- | :--- |
| 🎬 **Viral Reels Senaryoları** | İlk 2 saniye kancası (hook), 7-15 sn dinamik geçişler, trend ses önerileri. |
| 📸 **Carousel & Gönderi Fikirleri** | 4:5 dikey estetik, kahve, sokak stili (OOTD) ve merak uyandıran kaydırmalı kurgular. |
| 🎨 **Midjourney & DALL-E Promptları** | Instagram estetiğine uygun sinematik, fotogerçekçi 8K İngilizce görsel üretim promptları. |
| ✍️ **Caption & Hashtag Üretici** | Samimi, genç ve etkileşim odaklı Türkçe açıklamalar ile trend etiketler (`#Instagram`, `#Reels`, `#Keşfet`). |
| 📈 **Keşfet & Algoritma Rehberi** | Türkiye için en iyi paylaşım saatleri (öğle molası, 19:30-22:30 prime-time), kaydetme & DM paylaşım tüyoları. |
| 💾 **Yerel İçerik Kaydetme** | Beğendiğiniz gönderi fikirlerini tek dokunuşla Room veritabanına kaydedin ve panoya kopyalayın. |
| ⚡ **Offline & Hibrit Destek** | API anahtarı olmadan da zengin içerik üreten yerel danışman motoru + Gemini API entegrasyonu. |

---

## 🛠️ Projeyi Yerel Olarak Derleme (Build from Source)

Projeyi kendi bilgisayarınızda derlemek isterseniz:

```bash
# Depoyu klonlayın
git clone <repo-url>
cd <repo-folder>

# Gradle ile APK derleyin
./gradlew assembleDebug

# Oluşan APK dosyası şu dizinde hazır olacaktır:
# app/build/outputs/apk/debug/app-debug.apk
```

---

## 📋 Teknik Detaylar

* **Platform:** Android (Min SDK: 24, Target SDK: 35)
* **Dil & Arayüz:** Kotlin 2.0 & Jetpack Compose (Material 3)
* **Veritabanı:** Room Database
* **AI Entegrasyonu:** Google Gemini API (`generativeai`) + Yerleşik Instagram Danışman Motoru
* **CI/CD:** GitHub Actions (`.github/workflows/build-apk.yml`)

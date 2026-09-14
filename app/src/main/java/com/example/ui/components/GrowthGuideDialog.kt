package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.TurkgramCoral
import com.example.ui.theme.TurkgramGold
import com.example.ui.theme.TurkgramPink
import com.example.ui.theme.TurkgramPurple
import com.example.ui.theme.TurkgramRed

@Composable
fun GrowthGuideDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("growth_guide_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(listOf(TurkgramRed, TurkgramPink))),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FlashOn,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Instagram Büyüme Rehberi",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "Reels & Keşfet Algoritması Tüyoları",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Kapat")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 1: Paylaşım Saatleri
                GuideSection(
                    icon = Icons.Default.AccessTime,
                    iconTint = TurkgramCoral,
                    title = "Türkiye için En Etkili Paylaşım Saatleri",
                    tips = listOf(
                        "☀️ Öğle Molası (12:30 - 13:45): Ofis ve okul kitlelerinin Instagram akışını kontrol ettiği ilk tepe noktası.",
                        "🔥 Akşam Prime Time (19:30 - 22:30): Instagram'ın en yoğun etkileşim zamanı! Ana Reels ve Carousel postları için ideal.",
                        "☕️ Pazar Günü (11:00 - 15:00): Kahve, gezi ve rahatlama içeriklerinin en çok kaydedildiği saatler."
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Section 2: Keşfet Algoritması
                GuideSection(
                    icon = Icons.Default.FlashOn,
                    iconTint = TurkgramGold,
                    title = "Instagram Keşfet Algoritması",
                    tips = listOf(
                        "⚡️ İlk 30 Dakika Kuralı: Gönderi paylaşıldıktan sonra gelen ilk yorumlara anında samimi yanıtlar verin.",
                        "📌 Kaydetme & DM Paylaşımı: Algoritma beğeniye göre 'Kaydet' ve 'DM ile Arkadaşına Gönder' aksiyonlarını 4 kat daha değerli sayar.",
                        "💬 Hikaye Desteği: Paylaşımı hikayenizde anket veya test çıkartmasıyla destekleyin."
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Section 3: Reels Akımları
                GuideSection(
                    icon = Icons.Default.Movie,
                    iconTint = TurkgramPurple,
                    title = "Instagram Reels Akım Tüyoları",
                    tips = listOf(
                        "🎣 İlk 2 Saniye Kancası (Hook): İzleyiciyi tutacak merak uyandıran bir soru veya görsel geçişle başlayın.",
                        "🎵 Trend Müzik Seçimi: Instagram Reels akışında yükselen sesleri arka plana hafifçe ekleyin.",
                        "⏱️ İdeal Süre: 7 - 15 saniye arası dinamik videoların tekrar izlenme oranı en yüksektir."
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Section 4: Profil Optimizasyonu
                GuideSection(
                    icon = Icons.Default.Person,
                    iconTint = TurkgramPink,
                    title = "Profil & Biyo Düzeni",
                    tips = listOf(
                        "🎯 Net Biyo: Kim olduğunuzu ve kullanıcının sizi takip edince ne kazanacağını 2 satırda netleştirin.",
                        "📌 3 Sabit Gönderi: En iyi Reels'inizi, hakkınızda postunu ve en popüler içeriğinizi ızgaraya sabitleyin."
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "💡 Instagram AI Asistanına dilediğin an 'Benim profilim için büyüme stratejisi yaz' diyebilirsin!",
                    fontSize = 11.sp,
                    color = TurkgramRed,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun GuideSection(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    tips: List<String>
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            tips.forEach { tip ->
                Text(
                    text = "• $tip",
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
    }
}

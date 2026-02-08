package com.learining.AzkarApp.UI

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.learining.AzkarApp.R
import com.learining.AzkarApp.UI.BotNav.MainActivity
import com.learining.AzkarApp.utils.UsagePreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class Launcher_Activity : AppCompatActivity() {
    private val verses = listOf(
        "أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ",
        "فَاذْكُرُونِي أَذْكُرْكُمْ وَاشْكُرُوا لِي وَلَا تَكْفُرُونِ",
        "يَا أَيُّهَا الَّذِينَ آمَنُوا اذْكُرُوا اللَّهَ ذِكْرًا كَثِيرًا وَسَبِّحُوهُ بُكْرَةً وَأَصِيلًا",
        "وَاذْكُرِ اسْمَ رَبِّكَ وَتَبَتَّلْ إِلَيْهِ تَبْتِيلًا",
        "وَمَن يَعْشُ عَن ذِكْرِ الرَّحْمَٰنِ نُقَيِّضْ لَهُ شَيْطَانًا فَهُوَ لَهُ قَرِينٌ",
        "وَمَن أَعْرَضَ عَن ذِكْرِي فَإِنَّ لَهُ مَعِيشَةً ضَنكًا",
        "وَالذَّاكِرِينَ اللَّهَ كَثِيرًا وَالذَّاكِرَاتِ أَعَدَّ اللَّهُ لَهُم مَّغْفِرَةً وَأَجْرًا عَظِيمًا",
        "الَّذِينَ يَذْكُرُونَ اللَّهَ قِيَامًا وَقُعُودًا وَعَلَىٰ جُنُوبِهِمْ"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        val manager = UsagePreferences(this)
        
        // Apply theme immediately
        lifecycleScope.launch {
            val isDark = manager.isDarkMode.first()
            if (isDark) {
                androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        val tvVerse = findViewById<android.widget.TextView>(R.id.tvVerse)

        lifecycleScope.launch {
            // Get current index and update for next time
            var currentIndex = manager.verseIndex.first()
            if (currentIndex < 0 || currentIndex >= verses.size) {
                currentIndex = 0
            }
            val nextIndex = (currentIndex + 1) % verses.size
            manager.saveVerseIndex(nextIndex)

            // Set text and animate
            tvVerse.text = verses[currentIndex]
            tvVerse.alpha = 0f
            tvVerse.translationY = 30f
            tvVerse.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(1200)
                .start()

            kotlinx.coroutines.delay(3500) // Slightly longer to allow reading
            val intent = Intent(this@Launcher_Activity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
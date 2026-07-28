package com.habib.siratemustakeem.models

import java.io.Serializable

/**
 * A selectable Urdu translation/tafseer edition, backed by an AlQuran Cloud
 * edition identifier (see https://alquran.cloud/api -> Edition List).
 */
data class TranslationOption(
    val label: String,       // e.g. "کنزالایمان"
    val scholarName: String, // e.g. "امام احمد رضا خان"
    val editionIdentifier: String // e.g. "ur.kanzuliman"
) : Serializable

object QuranConstants {

    // Navigation modes
    const val MODE_TILAWAT = "tilawat"   // Arabic + audio only, no translation
    const val MODE_TAFSEER = "tafseer"   // Arabic + chosen Urdu translation + audio

    // Intent extra keys
    const val EXTRA_QURAN_MODE = "extra_quran_mode"
    const val EXTRA_TRANSLATION_EDITION = "extra_translation_edition"
    const val EXTRA_TRANSLATION_LABEL = "extra_translation_label"
    const val EXTRA_BROWSE_MODE = "extra_browse_mode" // "surah" | "juz"
    const val EXTRA_SURAH_OR_JUZ_NUMBER = "extra_number"
    const val EXTRA_TITLE = "extra_title"

    const val BROWSE_MODE_SURAH = "surah"
    const val BROWSE_MODE_JUZ = "juz"

    // All Urdu translations available for free via AlQuran Cloud that make
    // sense for this app's audience. "کنزالعرفان" as commonly meant in
    // Pakistan is Dr. Tahir-ul-Qadri's "عرفان القرآن" (Irfan-ul-Quran) —
    // included below alongside Kanz-ul-Iman and the other well known ones.
    val availableTranslations: List<TranslationOption> = listOf(
        TranslationOption("کنزالایمان", "امام احمد رضا خان", "ur.kanzuliman"),
        TranslationOption("عرفان القرآن", "ڈاکٹر طاہر القادری", "ur.qadri"),
        TranslationOption("مکمل ترجمہ", "فتح محمد جالندھری", "ur.jalandhry"),
        TranslationOption("تفہیم القرآن", "ابوالاعلیٰ مودودی", "ur.maududi"),
        TranslationOption("ترجمہ", "محمد جوناگڑھی", "ur.junagarhi"),
        TranslationOption("ترجمہ", "احمد علی", "ur.ahmedali"),
        TranslationOption("ترجمہ", "علامہ ذیشان حیدر جوادی", "ur.jawadi")
    )

    // Standard 30 Para (Juz) names, fixed across every Mushaf — no network needed.
    val paraNames: List<String> = listOf(
        "الم", "سیقول", "تلک الرسل", "لن تنالوا", "والمحصنات",
        "لا یحب اللہ", "وإذا سمعوا", "ولو أننا", "قال الملأ", "واعلموا",
        "یعتذرون", "وما من دابۃ", "وما أبرئ", "ربما", "سبحان الذی",
        "قال ألم", "اقترب للناس", "قد أفلح", "وقال الذین", "أمن خلق",
        "اتل ما أوحی", "ومن یقنت", "وما لی", "فمن أظلم", "إلیہ یرد",
        "حم", "قال فما خطبکم", "قد سمع اللہ", "تبارک الذی", "عم"
    )

    val juzList = listOf(

        Juz(1,  "الم",                 "Alif Lam Meem",           "الم",                 1, 1),
        Juz(2,  "سَيَقُولُ",            "Sayaqool",                "سیقول",              2, 142),
        Juz(3,  "تِلْكَ الرُّسُلُ",      "Tilka Ar-Rusul",          "تلک الرسل",          2, 253),
        Juz(4,  "لَنْ تَنَالُوا",        "Lan Tanaaloo",           "لن تنالوا",          3, 93),
        Juz(5,  "وَالْمُحْصَنَاتُ",      "Wal Muhsanat",           "والمحصنات",          4, 24),
        Juz(6,  "لَا يُحِبُّ اللَّهُ",    "La Yuhibbullah",         "لا یحب اللہ",        4, 148),
        Juz(7,  "وَإِذَا سَمِعُوا",      "Wa Iza Sami'u",          "وإذا سمعوا",         5, 82),
        Juz(8,  "وَلَوْ أَنَّنَا",       "Walaw Annana",           "ولو أننا",           6, 111),
        Juz(9,  "قَالَ الْمَلَأُ",       "Qalal Mala'",            "قال الملأ",          7, 88),
        Juz(10, "وَاعْلَمُوا",          "Wa'lamu",                "واعلموا",            8, 41),
        Juz(11, "يَعْتَذِرُونَ",         "Ya'taziroon",            "یعتذرون",            9, 94),
        Juz(12, "وَمَا مِنْ دَابَّةٍ",   "Wa Ma Min Daabbah",      "وما من دابۃ",        11, 6),
        Juz(13, "وَمَا أُبَرِّئُ",       "Wa Ma Ubarri'u",         "وما أبرئ",           12, 53),
        Juz(14, "رُبَمَا",              "Rubama",                 "ربما",               15, 1),
        Juz(15, "سُبْحَانَ الَّذِي",      "Subhanalladhi",          "سبحان الذی",         17, 1),
        Juz(16, "قَالَ أَلَمْ",          "Qala Alam",              "قال ألم",            18, 75),
        Juz(17, "اقْتَرَبَ لِلنَّاسِ",    "Iqtaraba Linnas",        "اقترب للناس",        21, 1),
        Juz(18, "قَدْ أَفْلَحَ",          "Qad Aflaha",             "قد أفلح",            23, 1),
        Juz(19, "وَقَالَ الَّذِينَ",      "Wa Qalalladhina",        "وقال الذین",         25, 21),
        Juz(20, "أَمَّنْ خَلَقَ",         "Aman Khalaq",            "أمن خلق",            27, 56),
        Juz(21, "اتْلُ مَا أُوحِيَ",      "Utlu Ma Oohiya",         "اتل ما أوحی",        29, 46),
        Juz(22, "وَمَنْ يَقْنُتْ",        "Wa Man Yaqnut",          "ومن یقنت",           33, 31),
        Juz(23, "وَمَا لِيَ",            "Wa Ma Liya",             "وما لی",             36, 28),
        Juz(24, "فَمَنْ أَظْلَمُ",        "Faman Azlam",            "فمن أظلم",           39, 32),
        Juz(25, "إِلَيْهِ يُرَدُّ",       "Ilayhi Yuraddu",         "إلیہ یرد",           41, 47),
        Juz(26, "حم",                   "Ha Meem",                "حم",                 46, 1),
        Juz(27, "قَالَ فَمَا خَطْبُكُمْ", "Qala Fama Khatbukum",    "قال فما خطبکم",      51, 31),
        Juz(28, "قَدْ سَمِعَ اللَّهُ",    "Qad Sami' Allah",        "قد سمع اللہ",        58, 1),
        Juz(29, "تَبَارَكَ الَّذِي",      "Tabarakalladhi",         "تبارک الذی",         67, 1),
        Juz(30, "عَمَّ",                "Amma",                   "عم",                 78, 1)

    )
}

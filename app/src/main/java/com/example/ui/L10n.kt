package com.example.ui

import com.example.viewmodel.AppLanguage

object L10n {
    fun get(key: String, lang: AppLanguage): String {
        return when (lang) {
            AppLanguage.ENGLISH -> when (key) {
                "app_name" -> "YM Organizer"
                "alarms_tab" -> "Alarms"
                "tasks_tab" -> "Tasks"
                "stopwatch_tab" -> "Timer"
                "settings_tab" -> "Customizer"
                "alarms_title" -> "Smart Alarms"
                "tasks_title" -> "Day Tasks Plan"
                "no_alarms" -> "No alarms active yet"
                "no_alarms_desc" -> "Organize your wake up and sleeping schedules. Click + to add."
                "no_tasks" -> "No tasks today"
                "no_tasks_desc" -> "Add tasks for today coupled with times & notifications."
                "upcoming_alarm" -> "Upcoming Alarm"
                "no_active_alarms" -> "No active alarms currently"
                "level_performance" -> "Daily Completion Level"
                "no_upcoming_alarms_track" -> "No active alarms currently."
                "tasks_perf_status" -> "Completed %d of %d daily tasks"
                "no_tasks_added" -> "Add daily tasks to organize your day!"
                "add_alarm_title" -> "Add New Alarm"
                "alarm_label_hint" -> "Alarm label (e.g. Wake up)"
                "alarm_repeat_days_title" -> "Weekly Repeat Days:"
                "save" -> "Save"
                "cancel" -> "Cancel"
                "add_task_title" -> "Add New Task"
                "task_title_hint" -> "What do you want to accomplish?"
                "task_desc_hint" -> "Details and description (optional)"
                "task_time_title" -> "Schedule execution time (optional):"
                "task_time_hint" -> "Tap to define execution time"
                "add" -> "Add"
                "about_title" -> "YM Organizer & Alarms"
                "about_version" -> "Current Version • 1.0 (Vision 2026)"
                "about_desc" -> "Your personal helper to schedule and organize your tasks. Designed with high-contrast elements, custom color themes, and comfortable rounded touch boundaries."
                "ok" -> "OK"
                "built_by" -> "Fully built by Yahya Mekalfa"
                "active_alarms" -> "Active Alarms"
                "daily_tasks_list" -> "Daily Tasks Checklist"
                "scheduled_at" -> "Scheduled in: %s"
                "alarm_tag_default" -> "Review tasks and day"
                "am" -> "AM"
                "pm" -> "PM"
                "stopwatch_title" -> "Stopwatch (Count Up)"
                "timer_title" -> "Countdown Timer"
                "start" -> "Start"
                "pause" -> "Pause"
                "reset" -> "Reset"
                "time_remaining_label" -> "Remaining time until next alarm:"
                "no_time_remaining" -> "No upcoming alarms to track"
                "settings_title" -> "Settings & Control Panel"
                "settings_lang" -> "Language Settings"
                "settings_mode" -> "App View Theme"
                "settings_color" -> "Theme Color Accent"
                "settings_sound" -> "Alarm Ringtone Sound"
                "settings_sound_sub" -> "Select built-in ringtones or custom audio file"
                "sound_modern" -> "Modern Ringtones"
                "sound_file" -> "Device Audio File"
                "choose_file" -> "Tap to select device file"
                "about_phone_section" -> "About Application & Phone"
                "about_phone_desc" -> "Manufacturer details & technology version"
                "days_header" -> "Weekly repeat settings"
                "file_selected" -> "Selected Audio File: %s"
                "file_not_selected" -> "No file selected (using default alarm)"
                "mode_light" -> "Light Mode"
                "mode_dark" -> "Dark Mode"
                "task_select_time_btn" -> "Tap to select target execution time"
                else -> key
            }
            AppLanguage.FRENCH -> when (key) {
                "app_name" -> "YM Organisateur"
                "alarms_tab" -> "Alarmes"
                "tasks_tab" -> "Tâches"
                "stopwatch_tab" -> "Minuteur"
                "settings_tab" -> "Réglages"
                "alarms_title" -> "Alarmes Intelligentes"
                "tasks_title" -> "Plan des Tâches"
                "no_alarms" -> "Aucune alarme pour le moment"
                "no_alarms_desc" -> "Planifiez votre sommeil. Cliquez sur + pour ajouter."
                "no_tasks" -> "Aucune tâche aujourd'hui"
                "no_tasks_desc" -> "Ajoutez vos tâches quotidiennes avec des heures d'exécution."
                "upcoming_alarm" -> "Prochaine Alarme"
                "no_active_alarms" -> "Aucune alarme active actuellement"
                "level_performance" -> "Niveau d'achèvement"
                "no_upcoming_alarms_track" -> "Aucune alarme active actuellement."
                "tasks_perf_status" -> "Achevé %d sur %d tâches quotidiennes"
                "no_tasks_added" -> "Ajoutez vos tâches de la journée !"
                "add_alarm_title" -> "Ajouter une alarme"
                "alarm_label_hint" -> "Nom de l'alarme (ex: Réveil)"
                "alarm_repeat_days_title" -> "Répétition hebdomadaire :"
                "save" -> "Sauver"
                "cancel" -> "Annuler"
                "add_task_title" -> "Ajouter une tâche"
                "task_title_hint" -> "Qu'allez-vous accomplir ?"
                "task_desc_hint" -> "Détails et notes (optionnel)"
                "task_time_title" -> "Heure programmée (optionnelle) :"
                "task_time_hint" -> "Définir l'heure d'exécution"
                "add" -> "Ajouter"
                "about_title" -> "YM Organisateur & Alarme"
                "about_version" -> "Version actuelle • 1.0 (Vision 2026)"
                "about_desc" -> "Votre assistant personnel pour planifier et organiser vos tâches. Conçu avec des éléments contrastés et des thèmes de couleur confortables."
                "ok" -> "OK"
                "built_by" -> "Entièrement conçu par Yahya Mekalfa"
                "active_alarms" -> "Alarmes Actives"
                "daily_tasks_list" -> "Liste de Tâches Quotidiennes"
                "scheduled_at" -> "Prévu à : %s"
                "alarm_tag_default" -> "Récapitulatif de la journée"
                "am" -> "AM"
                "pm" -> "PM"
                "stopwatch_title" -> "Chronomètre"
                "timer_title" -> "Minuteur Relatif"
                "start" -> "Démarrer"
                "pause" -> "Pause"
                "reset" -> "Réinitialiser"
                "time_remaining_label" -> "Temps restant avant la prochaine alarme :"
                "no_time_remaining" -> "Aucune alarme active à suivre"
                "settings_title" -> "Paramètres & Thème"
                "settings_lang" -> "Paramètres de Langue"
                "settings_mode" -> "Mode d'affichage"
                "settings_color" -> "Couleur du Thème"
                "settings_sound" -> "Sonnerie du Réveil"
                "settings_sound_sub" -> "Choisissez des alarmes intégrées ou de l'appareil"
                "sound_modern" -> "Sonneries Modernes"
                "sound_file" -> "Fichier audio de l'appareil"
                "choose_file" -> "Sélectionner un fichier audio"
                "about_phone_section" -> "À propos de l'application & téléphone"
                "about_phone_desc" -> "Concepteur et version technologique"
                "days_header" -> "Répétition hebdomadaire"
                "file_selected" -> "Fichier choisi : %s"
                "file_not_selected" -> "Aucun fichier choisi (alarme standard)"
                "mode_light" -> "Mode Clair"
                "mode_dark" -> "Mode Sombre"
                "task_select_time_btn" -> "Définir l'échéance"
                else -> key
            }
            else -> when (key) { // ARABIC fallback
                "app_name" -> "منبّه ومهام YM"
                "alarms_tab" -> "المنبهات"
                "tasks_tab" -> "المهام"
                "stopwatch_tab" -> "العداد والمؤقت"
                "settings_tab" -> "الإعدادات والثيم"
                "alarms_title" -> "المنبهات الذكية"
                "tasks_title" -> "جدول المهام اليومية"
                "no_alarms" -> "لا توجد منبهات حالياً"
                "no_alarms_desc" -> "رتب جدول استيقاظك ونومك، حدد منبهات ليومك بنقرة زر."
                "no_tasks" -> "لا توجد مهام اليوم"
                "no_tasks_desc" -> "ابدأ بوضع مهامك لليوم مقترنة بأوقات وتنبيهات لتنفيذها."
                "upcoming_alarm" -> "المنبه القادم"
                "no_active_alarms" -> "لا توجد منبهات نشطة حالياً"
                "level_performance" -> "مستوى الإنجاز اليومي"
                "no_upcoming_alarms_track" -> "لا توجد منبهات نشطة حالياً."
                "tasks_perf_status" -> "أنجزت %d من أصل %d من مَهام يومك"
                "no_tasks_added" -> "ابدأ بإضافة أولى مهامك لترتيب يومك بنجاح!"
                "add_alarm_title" -> "إضافة منبه جديد"
                "alarm_label_hint" -> "تسمية المنبه (مثال: الاستيقاظ)"
                "alarm_repeat_days_title" -> "تكرار التنبيه الأسبوعي:"
                "save" -> "حفظ"
                "cancel" -> "إلغاء"
                "add_task_title" -> "إضافة مهمة جديدة"
                "task_title_hint" -> "ما الذي تود إنجازه؟"
                "task_desc_hint" -> "تفاصيل وتوضيح (اختياري)"
                "task_time_title" -> "وقت إنجاز اختياري للجدول:"
                "task_time_hint" -> "اضغط لتحديد موعد إنجاز"
                "add" -> "إضافة"
                "about_title" -> "منظّم ومنبّه YM"
                "about_version" -> "الإصدار الحالي • 1.0 (رؤية تقنية 2026)"
                "about_desc" -> "مساعدك الخصوصي والذكي لتنظيم وترتيب يومك وجدولك، مصمم بطبقات متباينة وظلال لافتة للعين ونظام خطوط Bold متميزة بمستويات راحة بالغة الأناقة."
                "ok" -> "حسناً"
                "built_by" -> "مصنع بالكامل من طرف يحيى مخالفة"
                "active_alarms" -> "المنبهات النشطة"
                "daily_tasks_list" -> "قائمة المهام اليومية"
                "scheduled_at" -> "مجدول في: %s"
                "alarm_tag_default" -> "مراجعة المهام واليوم"
                "am" -> "ص"
                "pm" -> "م"
                "stopwatch_title" -> "عداد وقت متزايد (ساعة إيقاف)"
                "timer_title" -> "مؤقت تنازلي (تايمر)"
                "start" -> "ابدأ"
                "pause" -> "إيقاف مؤقت"
                "reset" -> "إعادة ضبط"
                "time_remaining_label" -> "الوقت المتبقي حتى المنبه القادم: "
                "no_time_remaining" -> "لا توجد منبهات نشطة لترقبها"
                "settings_title" -> "الإعدادات والتحكم"
                "settings_lang" -> "لغة تطبيق المنظّم"
                "settings_mode" -> "وضع العرض البصري"
                "settings_color" -> "تخصيص ثيم الألوان"
                "settings_sound" -> "أجراس رنين المنبه"
                "settings_sound_sub" -> "تحديد جرس التنبيه أو اختيار ملف من الهاتف"
                "sound_modern" -> "نغمات حديثة"
                "sound_file" -> "ملف صوتي خارجي"
                "choose_file" -> "اضغط لاختيار ملف صوتي من الهاتف"
                "about_phone_section" -> "قسم حول الهاتف والتطبيق"
                "about_phone_desc" -> "بيانات المصدر والمطور، إصدار التطبيق ونظام الهواتف"
                "days_header" -> "تكرار التنبيه الأسبوعي"
                "file_selected" -> "الملف الصوتي المحدد: %s"
                "file_not_selected" -> "لم يتم تحديد أي ملف (يتم تشغيل الصوت من رنين الهاتف الافتراضي)"
                "mode_light" -> "وضع العرض الساطع"
                "mode_dark" -> "وضع العرض الداكن"
                "task_select_time_btn" -> "اضغط لربط وقت تنفيذ"
                else -> key
            }
        }
    }

    fun translateRepeatDays(repeatDays: String, lang: AppLanguage): String {
        if (repeatDays.isEmpty()) return ""
        val mapAr = mapOf(
            "1" to "أ",
            "2" to "إ",
            "3" to "ث",
            "4" to "أ",
            "5" to "خ",
            "6" to "ج",
            "7" to "س"
        )
        val mapEn = mapOf(
            "1" to "Su",
            "2" to "Mo",
            "3" to "Tu",
            "4" to "We",
            "5" to "Th",
            "6" to "Fr",
            "7" to "Sa"
        )
        val mapFr = mapOf(
            "1" to "Di",
            "2" to "Lu",
            "3" to "Ma",
            "4" to "Me",
            "5" to "Je",
            "6" to "Ve",
            "7" to "Sa"
        )
        val map = when (lang) {
            AppLanguage.ENGLISH -> mapEn
            AppLanguage.FRENCH -> mapFr
            else -> mapAr
        }
        return repeatDays.split(",").mapNotNull { map[it] }.joinToString(" • ")
    }
}

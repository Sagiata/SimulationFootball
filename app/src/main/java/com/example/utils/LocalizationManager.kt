package com.example.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppLanguage(
    val code: String,
    val displayName: String,
    val nativeName: String,
    val flagEmoji: String
) {
    ENGLISH("en", "English", "English", "🇬🇧"),
    INDONESIAN("id", "Indonesian", "Bahasa Indonesia", "🇮🇩"),
    PORTUGUESE("pt", "Portuguese", "Português", "🇵🇹"),
    JAPANESE("ja", "Japanese", "日本語", "🇯🇵"),
    SUNDANESE("su", "Sundanese", "Basa Sunda", "🇮🇩"),
    KOREAN("ko", "Korean", "한국어", "🇰🇷")
}

object LocalizationManager {

    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
    }

    fun getString(key: String, language: AppLanguage = _currentLanguage.value): String {
        val map = when (language) {
            AppLanguage.ENGLISH -> englishStrings
            AppLanguage.INDONESIAN -> indonesianStrings
            AppLanguage.PORTUGUESE -> portugueseStrings
            AppLanguage.JAPANESE -> japaneseStrings
            AppLanguage.SUNDANESE -> sundaneseStrings
            AppLanguage.KOREAN -> koreanStrings
        }
        return map[key] ?: englishStrings[key] ?: key
    }

    private val englishStrings = mapOf(
        // Navigation & General
        "nav_hub" to "Hub",
        "nav_tactics" to "Tactics",
        "nav_match" to "Match",
        "nav_training" to "Training",
        "nav_transfers" to "Transfers",
        "nav_club" to "Club",
        "nav_calendar" to "Calendar",
        "nav_league" to "League",
        "nav_stats" to "Stats",
        "nav_history" to "History",
        "nav_profile" to "Profile",
        "nav_online" to "Online",
        "settings" to "Settings",
        "logout" to "Logout",
        "return_to_home" to "Return to Home",
        "next_fixture" to "Next Fixture",
        "continue_career" to "Continue Career",
        "new_career" to "New Career",
        "switch_profile" to "Switch Profile",
        "manager_name" to "Manager Name",
        "club_name" to "Club Name",
        "select_language" to "Select Language",
        "audio_sfx" to "Sound Effects (SFX)",
        "haptic_feedback" to "Haptic Vibration",
        "sim_speed_default" to "Default Match Speed",
        "save_and_continue" to "Start Career",
        "cancel" to "Cancel",
        "confirm_logout" to "Are you sure you want to log out and return to the main profile menu?",

        // Match
        "match_full_time" to "Full Time",
        "match_half_time" to "Half Time",
        "simulate_to_ft" to "Sim to FT",
        "touchline_instructions" to "Touchline Instructions",
        "substitution" to "Substitution",
        "match_stats" to "Match Stats",
        "match_completed" to "Match Completed",
        "xp_gained" to "XP Gained",

        // Relegation & League
        "relegation_zone" to "Relegation Zone",
        "champions_league_zone" to "Champions League Zone",
        "europa_league_zone" to "Europa League Zone",
        "safe_zone" to "Safe Zone",
        "points_to_safety" to "Pts to Safety",
        "points_ahead" to "Pts Ahead",
        "position" to "POS",
        "club" to "CLUB",
        "played" to "P",
        "won" to "W",
        "drawn" to "D",
        "lost" to "L",
        "goal_diff" to "GD",
        "points" to "PTS",
        "elo" to "ELO",

        // Calendar FC26
        "season_calendar" to "FC Career Calendar",
        "advance_day" to "Advance Day",
        "sim_to_date" to "Simulate to Date",
        "match_day" to "Matchday",
        "training_day" to "Training Day",
        "press_day" to "Press Conference",
        "rest_day" to "Rest Day",
        "transfer_deadline" to "Transfer Deadline",
        "schedule_agenda" to "Day Schedule",

        // Training
        "training_center" to "Training Center",
        "first_team" to "First Team",
        "youth_academy" to "Youth Academy",
        "run_training_drill" to "Execute Training Session",
        "drill_shooting" to "Finishing & Shooting",
        "drill_playmaking" to "Playmaking & Vision",
        "drill_defending" to "High Press & Defense",
        "drill_fitness" to "Stamina & Recovery",
        "promote_wonderkid" to "Promote to Senior Squad",
        "training_progress" to "Progression"
    )

    private val indonesianStrings = mapOf(
        // Navigation & General
        "nav_hub" to "Hub",
        "nav_tactics" to "Taktik",
        "nav_match" to "Tanding",
        "nav_training" to "Latihan",
        "nav_transfers" to "Transfer",
        "nav_club" to "Klub",
        "nav_calendar" to "Kalender",
        "nav_league" to "Klasemen",
        "nav_stats" to "Statistik",
        "nav_history" to "Riwayat",
        "nav_profile" to "Profil",
        "nav_online" to "Online",
        "settings" to "Pengaturan",
        "logout" to "Keluar Akun",
        "return_to_home" to "Kembali ke Home",
        "next_fixture" to "Laga Berikutnya",
        "continue_career" to "Lanjutkan Karir",
        "new_career" to "Karir Baru",
        "switch_profile" to "Ganti Profil",
        "manager_name" to "Nama Manajer",
        "club_name" to "Nama Klub",
        "select_language" to "Pilih Bahasa",
        "audio_sfx" to "Efek Suara (SFX)",
        "haptic_feedback" to "Getaran Haptik",
        "sim_speed_default" to "Kecepatan Simulasi",
        "save_and_continue" to "Mulai Karir",
        "cancel" to "Batal",
        "confirm_logout" to "Apakah Anda yakin ingin keluar dan kembali ke menu profil utama?",

        // Match
        "match_full_time" to "Peluit Akhir (FT)",
        "match_half_time" to "Turun Minum (HT)",
        "simulate_to_ft" to "Simulasi ke Selesai",
        "touchline_instructions" to "Instruksi Garis Lapangan",
        "substitution" to "Pergantian Pemain",
        "match_stats" to "Statistik Laga",
        "match_completed" to "Pertandingan Selesai",
        "xp_gained" to "XP Manajer Didapat",

        // Relegation & League
        "relegation_zone" to "Zona Degradasi",
        "champions_league_zone" to "Zona Liga Champions",
        "europa_league_zone" to "Zona Liga Europa",
        "safe_zone" to "Zona Aman",
        "points_to_safety" to "Poin ke Zona Aman",
        "points_ahead" to "Selisih Poin",
        "position" to "POS",
        "club" to "KLUB",
        "played" to "MAIN",
        "won" to "M",
        "drawn" to "S",
        "lost" to "K",
        "goal_diff" to "SG",
        "points" to "POIN",
        "elo" to "ELO",

        // Calendar FC26
        "season_calendar" to "Kalender Karir FC",
        "advance_day" to "Maju 1 Hari",
        "sim_to_date" to "Simulasi Sampai Tanggal Ini",
        "match_day" to "Hari Pertandingan",
        "training_day" to "Hari Latihan",
        "press_day" to "Konferensi Pers",
        "rest_day" to "Hari Istirahat",
        "transfer_deadline" to "Bursa Transfer Ditutup",
        "schedule_agenda" to "Jadwal Agenda Hari",

        // Training
        "training_center" to "Pusat Latihan",
        "first_team" to "Tim Utama",
        "youth_academy" to "Akademi Muda",
        "run_training_drill" to "Jalankan Sesi Latihan",
        "drill_shooting" to "Penyelesaian & Tembakan",
        "drill_playmaking" to "Kreativitas & Umpan",
        "drill_defending" to "Pressing & Bertahan",
        "drill_fitness" to "Stamina & Pemulihan Fisik",
        "promote_wonderkid" to "Promosikan ke Tim Senior",
        "training_progress" to "Progres Latihan"
    )

    private val portugueseStrings = mapOf(
        // Navigation & General
        "nav_tactics" to "Táticas",
        "nav_match" to "Jogo",
        "nav_training" to "Treino",
        "nav_transfers" to "Transferências",
        "nav_club" to "Clube",
        "nav_calendar" to "Calendário",
        "nav_league" to "Classificação",
        "nav_stats" to "Estatísticas",
        "nav_history" to "Histórico",
        "nav_profile" to "Perfil",
        "nav_online" to "Online",
        "settings" to "Definições",
        "logout" to "Terminar Sessão",
        "return_to_home" to "Voltar ao Início",
        "next_fixture" to "Próximo Jogo",
        "continue_career" to "Continuar Carreira",
        "new_career" to "Nova Carreira",
        "switch_profile" to "Trocar Perfil",
        "manager_name" to "Nome do Treinador",
        "club_name" to "Nome do Clube",
        "select_language" to "Selecionar Idioma",
        "audio_sfx" to "Efeitos Sonoros (SFX)",
        "haptic_feedback" to "Vibração Háptica",
        "sim_speed_default" to "Velocidade de Simulação",
        "save_and_continue" to "Iniciar Carreira",
        "cancel" to "Cancelar",
        "confirm_logout" to "Tem a certeza de que deseja sair e voltar ao menu principal?",

        // Match
        "match_full_time" to "Fim de Jogo (FT)",
        "match_half_time" to "Intervalo (HT)",
        "simulate_to_ft" to "Simular até ao Fim",
        "touchline_instructions" to "Instruções da Linha Lateral",
        "substitution" to "Substituição",
        "match_stats" to "Estatísticas do Jogo",
        "match_completed" to "Partida Concluída",
        "xp_gained" to "XP Obtido",

        // Relegation & League
        "relegation_zone" to "Zona de Despromoção",
        "champions_league_zone" to "Zona Liga dos Campeões",
        "europa_league_zone" to "Zona Liga Europa",
        "safe_zone" to "Zona Segura",
        "points_to_safety" to "Pontos p/ Manutenção",
        "points_ahead" to "Pontos de Vantagem",
        "position" to "POS",
        "club" to "CLUBE",
        "played" to "J",
        "won" to "V",
        "drawn" to "E",
        "lost" to "D",
        "goal_diff" to "DG",
        "points" to "PTS",
        "elo" to "ELO",

        // Calendar FC26
        "season_calendar" to "Calendário FC Career",
        "advance_day" to "Avançar Dia",
        "sim_to_date" to "Simular até à Data",
        "match_day" to "Dia de Jogo",
        "training_day" to "Dia de Treino",
        "press_day" to "Conferência de Imprensa",
        "rest_day" to "Dia de Descanso",
        "transfer_deadline" to "Fecho do Mercado",
        "schedule_agenda" to "Agenda do Dia",

        // Training
        "training_center" to "Centro de Treinos",
        "first_team" to "Equipa Principal",
        "youth_academy" to "Academia de Juniores",
        "run_training_drill" to "Executar Sessão de Treino",
        "drill_shooting" to "Finalização & Remates",
        "drill_playmaking" to "Construção de Jogo & Visão",
        "drill_defending" to "Pressão Alta & Defesa",
        "drill_fitness" to "Stamina & Recuperação Física",
        "promote_wonderkid" to "Promover à Equipa Principal",
        "training_progress" to "Progresso de Treino"
    )

    private val japaneseStrings = mapOf(
        // Navigation & General
        "nav_tactics" to "戦術",
        "nav_match" to "試合",
        "nav_training" to "トレーニング",
        "nav_transfers" to "移籍",
        "nav_club" to "クラブ",
        "nav_calendar" to "日程",
        "nav_league" to "順位表",
        "nav_stats" to "スタッツ",
        "nav_history" to "履歴",
        "nav_profile" to "監督情報",
        "nav_online" to "オンライン",
        "settings" to "設定",
        "logout" to "ログアウト",
        "return_to_home" to "ホームに戻る",
        "next_fixture" to "次の試合",
        "continue_career" to "キャリアを再開",
        "new_career" to "新規キャリア",
        "switch_profile" to "プロファイル切替",
        "manager_name" to "監督名",
        "club_name" to "クラブ名",
        "select_language" to "言語を選択",
        "audio_sfx" to "効果音 (SFX)",
        "haptic_feedback" to "触覚バイブレーション",
        "sim_speed_default" to "試合シミュレーション速度",
        "save_and_continue" to "キャリア開始",
        "cancel" to "キャンセル",
        "confirm_logout" to "ログアウトしてプロファイルメニューに戻りますか？",

        // Match
        "match_full_time" to "試合終了 (FT)",
        "match_half_time" to "ハーフタイム (HT)",
        "simulate_to_ft" to "終了まで即時シミュレート",
        "touchline_instructions" to "タッチライン指示",
        "substitution" to "選手交代",
        "match_stats" to "試合スタッツ",
        "match_completed" to "試合完了",
        "xp_gained" to "獲得監督XP",

        // Relegation & League
        "relegation_zone" to "降格圏 (Relegation)",
        "champions_league_zone" to "CL出場圏",
        "europa_league_zone" to "EL出場圏",
        "safe_zone" to "残留圏",
        "points_to_safety" to "残留ラインまでの勝ち点",
        "points_ahead" to "リード勝ち点",
        "position" to "順位",
        "club" to "クラブ",
        "played" to "試",
        "won" to "勝",
        "drawn" to "分",
        "lost" to "敗",
        "goal_diff" to "得失",
        "points" to "勝点",
        "elo" to "ELO",

        // Calendar FC26
        "season_calendar" to "FC カレンダー",
        "advance_day" to "1日進める",
        "sim_to_date" to "指定日まで進める",
        "match_day" to "試合日",
        "training_day" to "練習日",
        "press_day" to "記者会見",
        "rest_day" to "休養日",
        "transfer_deadline" to "移籍市場最終日",
        "schedule_agenda" to "当日のスケジュール",

        // Training
        "training_center" to "練習施設",
        "first_team" to "トップチーム",
        "youth_academy" to "ユース育成",
        "run_training_drill" to "練習メニューを実行",
        "drill_shooting" to "シュート・決定力強化",
        "drill_playmaking" to "ゲームメイク・パス視野",
        "drill_defending" to "ハイプレス・守備連携",
        "drill_fitness" to "スタミナ・コンディション回復",
        "promote_wonderkid" to "トップチーム昇格",
        "training_progress" to "成長進行度"
    )

    private val sundaneseStrings = mapOf(
        // Navigation & General
        "nav_tactics" to "Taktik",
        "nav_match" to "Maen",
        "nav_training" to "Latihan",
        "nav_transfers" to "Transfer",
        "nav_club" to "Klub",
        "nav_calendar" to "Kalénder",
        "nav_league" to "Klasemén",
        "nav_stats" to "Statistik",
        "nav_history" to "Riwajat",
        "nav_profile" to "Profil",
        "nav_online" to "Online",
        "settings" to "Setélan",
        "logout" to "Kaluar Akun",
        "return_to_home" to "Mulang ka Home",
        "next_fixture" to "Tanding Salajengna",
        "continue_career" to "Teraskeun Karir",
        "new_career" to "Karir Anyar",
        "switch_profile" to "Gentos Profil",
        "manager_name" to "Nami Palatih",
        "club_name" to "Nami Klub",
        "select_language" to "Pilih Basa",
        "audio_sfx" to "Sora Kaulinan (SFX)",
        "haptic_feedback" to "Geter Haptik",
        "sim_speed_default" to "Kagancangan Simulasi",
        "save_and_continue" to "Mimitian Karir",
        "cancel" to "Batal",
        "confirm_logout" to "Naha anjeun yakin palay kaluar ka ménu profil utama?",

        // Match
        "match_full_time" to "Waktos Réngsé (FT)",
        "match_half_time" to "Reureuh (HT)",
        "simulate_to_ft" to "Simulasi Dugi ka Réngsé",
        "touchline_instructions" to "Paréntah Sisi Lapang",
        "substitution" to "Gentos Pamaén",
        "match_stats" to "Statistik Maen",
        "match_completed" to "Pertandingan Réngsé",
        "xp_gained" to "XP Palatih Kenging",

        // Relegation & League
        "relegation_zone" to "Zona Dégradasi",
        "champions_league_zone" to "Zona Liga Champions",
        "europa_league_zone" to "Zona Liga Europa",
        "safe_zone" to "Zona Aman",
        "points_to_safety" to "Poin ka Zona Salamet",
        "points_ahead" to "Kacukupan Poin",
        "position" to "POS",
        "club" to "KLUB",
        "played" to "MAEN",
        "won" to "M",
        "drawn" to "S",
        "lost" to "É",
        "goal_diff" to "SG",
        "points" to "POIN",
        "elo" to "ELO",

        // Calendar FC26
        "season_calendar" to "Kalénder Karir FC",
        "advance_day" to "Majeng 1 Dinten",
        "sim_to_date" to "Simulasi Dugi ka Tanggal Ieu",
        "match_day" to "Dinten Tanding",
        "training_day" to "Dinten Latihan",
        "press_day" to "Konferénsi Pérs",
        "rest_day" to "Dinten Istirahat",
        "transfer_deadline" to "Bursa Transfer Ditutup",
        "schedule_agenda" to "Jadwal Agenda Dinten",

        // Training
        "training_center" to "Puseur Latihan",
        "first_team" to "Tim Utama",
        "youth_academy" to "Akademi Ngora",
        "run_training_drill" to "Jalankeun Sési Latihan",
        "drill_shooting" to "Nembak & Nyitak Gol",
        "drill_playmaking" to "Kréativitas & Operan",
        "drill_defending" to "Nahan Serangan & Pertahanan",
        "drill_fitness" to "Stamina & Cageur Fisik",
        "promote_wonderkid" to "Promosikeun ka Tim Kolot",
        "training_progress" to "Kamajuan Latihan"
    )

    private val koreanStrings = mapOf(
        // Navigation & General
        "nav_tactics" to "전술",
        "nav_match" to "경기",
        "nav_training" to "훈련",
        "nav_transfers" to "이적",
        "nav_club" to "구단",
        "nav_calendar" to "일정",
        "nav_league" to "순위표",
        "nav_stats" to "통계",
        "nav_history" to "기록",
        "nav_profile" to "프로필",
        "nav_online" to "온라인",
        "settings" to "설정",
        "logout" to "로그아웃",
        "return_to_home" to "홈으로 돌아가기",
        "next_fixture" to "다음 경기",
        "continue_career" to "커리어 이어하기",
        "new_career" to "새 커리어 시작",
        "switch_profile" to "프로필 전환",
        "manager_name" to "감독 이름",
        "club_name" to "구단 이름",
        "select_language" to "언어 선택",
        "audio_sfx" to "효과음 (SFX)",
        "haptic_feedback" to "진동 피드백",
        "sim_speed_default" to "기본 경기 속도",
        "save_and_continue" to "커리어 시작",
        "cancel" to "취소",
        "confirm_logout" to "로그아웃하고 프로필 메뉴로 돌아가시겠습니까?",

        // Match
        "match_full_time" to "경기 종료 (FT)",
        "match_half_time" to "전반 종료 (HT)",
        "simulate_to_ft" to "경기 종료까지 시뮬레이션",
        "touchline_instructions" to "터치라인 지시",
        "substitution" to "선수 교체",
        "match_stats" to "경기 통계",
        "match_completed" to "경기 완료",
        "xp_gained" to "획득 감독 XP",

        // Relegation & League
        "relegation_zone" to "강등권 (Relegation Zone)",
        "champions_league_zone" to "챔피언스리그 진출권",
        "europa_league_zone" to "유로파리그 진출권",
        "safe_zone" to "안정권",
        "points_to_safety" to "잔류까지 필요 승점",
        "points_ahead" to "승점 차",
        "position" to "순위",
        "club" to "구단",
        "played" to "경기",
        "won" to "승",
        "drawn" to "무",
        "lost" to "패",
        "goal_diff" to "득실",
        "points" to "승점",
        "elo" to "ELO",

        // Calendar FC26
        "season_calendar" to "FC 커리어 캘린더",
        "advance_day" to "하루 진행",
        "sim_to_date" to "해당 날짜까지 시뮬레이션",
        "match_day" to "경기일",
        "training_day" to "훈련일",
        "press_day" to "기자회견",
        "rest_day" to "휴식일",
        "transfer_deadline" to "이적시장 마감일",
        "schedule_agenda" to "당일 일정",

        // Training
        "training_center" to "훈련 센터",
        "first_team" to "1군 선수단",
        "youth_academy" to "유소년 아카데미",
        "run_training_drill" to "훈련 세션 실행",
        "drill_shooting" to "슈팅 & 골 결정력",
        "drill_playmaking" to "플레이메이킹 & 시야",
        "drill_defending" to "전방 압박 & 수비 조직력",
        "drill_fitness" to "체력 & 스태미나 회복",
        "promote_wonderkid" to "1군 승격",
        "training_progress" to "성장 진행도"
    )
}

class LocalizationManager(
    private val context: Context,
    private val preferences: SharedPreferences
) {
    private val supportedLanguages = listOf("en", "es", "fr", "de", "zh")
    
    fun setLanguage(languageCode: String) {
        if (languageCode in supportedLanguages) {
            preferences.edit().putString("language", languageCode).apply()
            updateConfiguration(languageCode)
        }
    }

    private fun updateConfiguration(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        
        val config = context.resources.configuration
        config.setLocale(locale)
        context.createConfigurationContext(config)
    }
} 
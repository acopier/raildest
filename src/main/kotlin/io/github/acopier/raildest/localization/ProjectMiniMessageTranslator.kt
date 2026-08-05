package io.github.acopier.raildest.localization

import net.kyori.adventure.key.Key
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.translation.MiniMessageTranslator
import net.kyori.adventure.translation.Translator
import java.text.MessageFormat
import java.util.Locale
import java.util.MissingResourceException
import java.util.ResourceBundle

class ProjectMiniMessageTranslator(miniMessage: MiniMessage) : MiniMessageTranslator(miniMessage) {
    override fun name(): Key {
        return Key.key("raildest:translator")
    }

    override fun getMiniMessageString(key: String, locale: Locale): String? {

        try {
            val bundle = ResourceBundle.getBundle("locale.messages", locale)
            if (bundle.containsKey(key)) {
                return "${bundle.getString(key)}"
            }
        } catch (error: MissingResourceException) {
            val bundle = ResourceBundle.getBundle("locale.messages", Locale.ENGLISH)
            if (bundle.containsKey(key)) {
                return "${bundle.getString(key)}"
            }
        }

        return null
    }
}
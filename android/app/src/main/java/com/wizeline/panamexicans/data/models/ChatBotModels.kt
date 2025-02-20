package com.wizeline.panamexicans.data.models

data class ChatMessage(
    val response: ChatBotResponseWithRouteImage,
    val author: Author,
    val prompt: String = ""
) {
    fun isMe() = author == Author.Me
    fun isMenu() = author == Author.BotMenu
    fun isPreferences() = author == Author.Preferences
}

sealed class Author {
    data object Me : Author()
    data object Bot : Author()
    data object BotMenu : Author()
    data object Preferences : Author()
}
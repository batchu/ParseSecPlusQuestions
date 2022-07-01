package net.batchu.parse_sec_plus_questions.model

data class Question(
    val id:Float,
    val question: String,
    val choices: List<Choice>,
    val explanation: String?
)

data class Choice(
    val choice:String,
    val isCorrect: Boolean
)
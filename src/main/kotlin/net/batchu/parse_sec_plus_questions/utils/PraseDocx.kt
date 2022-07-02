package net.batchu.parse_sec_plus_questions.utils

import net.batchu.parse_sec_plus_questions.model.Choice
import net.batchu.parse_sec_plus_questions.model.Question
import net.batchu.parse_sec_plus_questions.model.QuestionRepository
import org.apache.poi.xwpf.extractor.XWPFWordExtractor
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.io.File

@Component
class ParseDocx: CommandLineRunner{
    @Autowired
    private lateinit var repo: QuestionRepository
    var LOG: Logger = LoggerFactory.getLogger(ParseDocx::class.java)
    private fun readDoc(){
        val dir = File("./src/main/resources/files/")

        for ( file in dir.listFiles()) {
            if(file.isFile){
                val inputStream = file.inputStream()
                val targetDoc = XWPFDocument(inputStream)
                val wordExtractor = XWPFWordExtractor(targetDoc)
                val parseQuestions = parseQuestions(wordExtractor.text)
                repo.saveAll(parseQuestions)
                inputStream.close()
            }
        }
    }

    private fun parseQuestions(text: String): List<Question> {

          return text
            .trimIndent()
            .split("\n")
            .filter { it.isNotBlank() }
            .let { list ->
                var no = ""
                list.mapIndexed { index, it ->
                    val inQuestion = (index > 0 && list[index - 1].startsWith("QUESTION "))
                    when {
                        it.startsWith("QUESTION ") -> Triple(it.substringAfter("QUESTION ").trim().apply { no = this }, "id", null)
                        inQuestion                 -> Triple(no, "Question" , it.trim())
                        it.startsWith("A. ")       -> Triple(no, "Answer A" , it.substringAfter(". ").trim())
                        it.startsWith("B. ")       -> Triple(no, "Answer B" , it.substringAfter(". ").trim())
                        it.startsWith("C. ")       -> Triple(no, "Answer C" , it.substringAfter(". ").trim())
                        it.startsWith("D. ")       -> Triple(no, "Answer D" , it.substringAfter(". ").trim())
                        it.startsWith("E. ")       -> Triple(no, "Answer E" , it.substringAfter(". ").trim())
                        it.startsWith("Answer: ")  -> Triple(no, "Answer" , it.substringAfter(": ").trim())
                        else                       -> Triple(no, "Comment" , it)
                    }
                }
            }
            .groupBy { (id, _, _) -> id }
            .map { (id, list) ->
                val correctAnswer = list.first { it.second == "Answer" }.third!![0]
                Question(
                    id = id
                        .toFloat(),
                    question = list
                        .first { it.second == "Question" }.third!!,
                    choices = list
                        .filter { it.second.startsWith("Answer ") }
                        .map { Choice(choice = it.third!!, isCorrect = it.second.last() == correctAnswer) },
                    explanation = list
                        .filter { it.second == "Comment" }
                        .joinToString("\n") { it.third!! }
                )
            }
    }
    override fun run(vararg args: String?) {
        readDoc()
    }
}
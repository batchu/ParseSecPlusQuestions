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
import java.util.stream.Collectors

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
                LOG.info("Saved ${parseQuestions.size} entries to DB")
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
                    LOG.debug("Processing index ${index}")
                    var inQuestion = (index > 0 && ((list[index - 1]).startsWith("QUESTION ") || (list[index - 2]).startsWith("QUESTION ") || (list[index - 3]).startsWith("QUESTION ")))
                    when {
                        it.startsWith("QUESTION ") -> Triple(it.substringAfter("QUESTION ").trim().apply { no = this }, "id", null)
                        it.trimStart().startsWith("A. ")       -> Triple(no, "Answer A" , it.substringAfter(". ").trim().apply { inQuestion = false })
                        it.trimStart().startsWith("B. ")       -> Triple(no, "Answer B" , it.substringAfter(". ").trim())
                        it.trimStart().startsWith("C. ")       -> Triple(no, "Answer C" , it.substringAfter(". ").trim())
                        it.trimStart().startsWith("D. ")       -> Triple(no, "Answer D" , it.substringAfter(". ").trim())
                        it.trimStart().startsWith("E. ")       -> Triple(no, "Answer E" , it.substringAfter(". ").trim())
                        it.trimStart().startsWith("F. ")       -> Triple(no, "Answer F" , it.substringAfter(". ").trim())
                        it.trimStart().startsWith("G. ")       -> Triple(no, "Answer G" , it.substringAfter(". ").trim())
                        inQuestion                 -> Triple(no, "Question" , it.trim())
                        it.trimStart().startsWith("Answer: ") ||  it.contains("Answer:")  -> Triple(no, "Answer" , it.substringAfter(": ").trim())
                        else                       -> Triple(no, "Comment" , it)
                    }
                }
            }
            .groupBy { (id, _, _) -> id }
            .map { (id, list) ->
                val correctAnswer = list.first { it.second == "Answer" }.third!![0]
                LOG.debug("Creating question instance ${id}")
                Question(
                    id = id,
                    question = list
                        .filter { it.second == "Question" }.map{it.third}.stream().collect(Collectors.joining(" ")),
                    choices = list
                        .filter { it.second.startsWith("Answer ") }
                        .map { Choice(choice = it.third!!, isCorrect = it.second.last() == correctAnswer) },
                    explanation = list
                        .filter { it.second == "Comment" }
                        .joinToString("\n") { it.third!! },
                    imageUrl = ""
                )
            }
    }
    override fun run(vararg args: String?) {
        readDoc()
    }
}
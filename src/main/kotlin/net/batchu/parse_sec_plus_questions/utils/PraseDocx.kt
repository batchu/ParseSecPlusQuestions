package net.batchu.parse_sec_plus_questions.utils

import org.apache.poi.xwpf.extractor.XWPFWordExtractor
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.io.File

@Component
class ParseDocx: CommandLineRunner{
    var LOG: Logger = LoggerFactory.getLogger(ParseDocx::class.java)
    private fun readDoc(){
        val dir = File("./src/main/resources/files/")

        for ( file in dir.listFiles()) {
            if(file.isFile){
                val inputStream = file.inputStream()
                val targetDoc = XWPFDocument(inputStream)
                val wordExtractor = XWPFWordExtractor(targetDoc)
                val docText = wordExtractor.text
                LOG.info(docText)
            inputStream.close()
            }
        }
    }

    override fun run(vararg args: String?) {
        readDoc()
    }
}
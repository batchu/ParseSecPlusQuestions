package net.batchu.parse_sec_plus_questions.model

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable
import org.socialsignin.spring.data.dynamodb.repository.EnableScan
import org.springframework.data.repository.CrudRepository
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToMany

@EnableScan
interface QuestionRepository: CrudRepository<Question, Float>

@DynamoDBTable(tableName = "secplus")
data class Question(
    @DynamoDBHashKey
    val id:Float,
    val question: String,
    @OneToMany
    val choices: List<Choice>,
    val explanation: String?
)

data class Choice(
    val choice:String,
    val isCorrect: Boolean
)
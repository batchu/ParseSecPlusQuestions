package net.batchu.parse_sec_plus_questions.model

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable
import org.socialsignin.spring.data.dynamodb.repository.EnableScan
import org.springframework.context.annotation.Primary
import org.springframework.data.repository.CrudRepository


@EnableScan
interface QuestionRepository: CrudRepository<Question, Float>

@DynamoDBTable(tableName = "secplus")
data class Question(
    @DynamoDBHashKey
    val id:String,
    @DynamoDBAttribute
    val question: String,
    @DynamoDBAttribute
    val choices: List<Choice>,
    @DynamoDBAttribute
    val explanation: String?
)

data class Choice(
    val choice:String,
    val isCorrect: Boolean
)
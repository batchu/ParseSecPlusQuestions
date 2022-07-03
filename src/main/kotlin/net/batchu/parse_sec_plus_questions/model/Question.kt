package net.batchu.parse_sec_plus_questions.model

import com.amazonaws.services.dynamodbv2.datamodeling.*
import org.socialsignin.spring.data.dynamodb.repository.EnableScan
import org.springframework.context.annotation.Primary
import org.springframework.data.repository.CrudRepository


@EnableScan
interface QuestionRepository: CrudRepository<Question, Float>

@DynamoDBTable(tableName = "secplus")
data class Question(
    @DynamoDBHashKey
    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.S)
    val id:String,
    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.S)
    val question: String,
    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.L)
    val choices: List<Choice>,
    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.S)
    val explanation: String?
)
@DynamoDBDocument
data class Choice(
    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.S)
    val choice:String,
    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.B)
    val isCorrect: Boolean
)
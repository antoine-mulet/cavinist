package com.amulet.cavinist.web.graphql

import graphql.*
import graphql.execution.*
import graphql.language.SourceLocation
import org.slf4j.LoggerFactory

class CustomDataFetcherExceptionHandler : DataFetcherExceptionHandler {

    private val logger = LoggerFactory.getLogger(CustomDataFetcherExceptionHandler::class.java)

    override fun onException(handlerParameters: DataFetcherExceptionHandlerParameters): DataFetcherExceptionHandlerResult {
        val exception = handlerParameters.exception
        val sourceLocation = handlerParameters.sourceLocation
        val path = handlerParameters.path

        val error: GraphQLError = CustomGraphQLError(
            exception = exception,
            locations = listOf(sourceLocation),
            path = path.toList(),
            extensions = ErrorCodeMapper.errorCodeForException(exception)?.let { mapOf("code" to it) } ?: emptyMap())
        logger.warn(error.message, exception)
        return DataFetcherExceptionHandlerResult.newResult(error).build()
    }
}

class CustomGraphQLError(
    private val exception: Throwable,
    private val locations: List<SourceLocation> = emptyList(),
    private val path: List<Any>? = null,
    private val extensions: Map<String, ErrorCode> = emptyMap(),
    private val errorType: ErrorClassification = ErrorType.DataFetchingException) : GraphQLError {

    override fun getErrorType(): ErrorClassification = errorType

    override fun getExtensions(): Map<String, Any> = extensions.mapValues { it.value.name }

    override fun getLocations(): List<SourceLocation> = locations

    override fun getMessage(): String =
        exception.message ?: "Exception while fetching data (${path?.joinToString("/").orEmpty()})"

    override fun getPath(): List<Any>? = path
}
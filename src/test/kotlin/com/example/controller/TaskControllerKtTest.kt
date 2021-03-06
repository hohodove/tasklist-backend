package com.example.controller

import com.example.controller.request.CreateTaskRequest
import com.example.controller.request.UpdateTaskRequest
import com.example.controller.response.ErrorMessageResponse
import com.example.controller.response.FindAllTasksResponse
import com.example.controller.response.FindTaskByIdResponse
import com.example.domain.model.task.value_object.DueDate
import com.example.domain.model.task.value_object.TaskId
import com.example.domain.model.task.value_object.TaskName
import com.example.domain.model.task.value_object.TaskStatus
import com.example.test_util.withTestModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class TaskControllerKtTest {

    val mapper = jacksonObjectMapper().findAndRegisterModules()

    @Test
    fun `全タスク取得エンドポイントについて、全タスク件数が0件の場合、空の配列が返却されること`() {
        withTestModule {
            handleRequest(HttpMethod.Get, "/tasks").apply {
                assertEquals(HttpStatusCode.OK, response.status())

                val findAllTasksResponse = mapper.readValue<List<FindAllTasksResponse>>(response.content!!)
                assertEquals(0, findAllTasksResponse.size)
            }
        }
    }

    @Test
    fun `タスクの作成、取得、削除ができること`() {
        withTestModule {

            handleRequest(HttpMethod.Post, "/task") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())

                val createTaskRequest = CreateTaskRequest(
                    "タスク１"
                )

                val createTaskJsonBody = mapper.writeValueAsString(createTaskRequest)
                setBody(createTaskJsonBody)
            }.apply {
                    assertEquals(HttpStatusCode.OK, response.status())
                }

            handleRequest(HttpMethod.Post, "/task") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())

                val createTaskRequest = CreateTaskRequest(
                    name = "タスク２"
                )

                val createTaskJsonBody = mapper.writeValueAsString(createTaskRequest)
                setBody(createTaskJsonBody)
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }

            val taskIds: List<String>
            handleRequest(HttpMethod.Get, "/tasks").apply {
                assertEquals(HttpStatusCode.OK, response.status())

                val findAllTasksResponse = mapper.readValue<List<FindAllTasksResponse>>(response.content!!)
                assertEquals(2, findAllTasksResponse.size)

                taskIds = findAllTasksResponse
                    .map { it.id }
            }

            handleRequest(HttpMethod.Get, "/task/${taskIds[0]}").apply {
                assertEquals(HttpStatusCode.OK, response.status())

                val findTaskByIdResponse = mapper.readValue<FindTaskByIdResponse>(response.content!!)
                assertTrue(findTaskByIdResponse.name != "")
            }

            handleRequest(HttpMethod.Delete, "/task/${taskIds[0]}").apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }

            handleRequest(HttpMethod.Delete, "/task/${taskIds[1]}").apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }

            handleRequest(HttpMethod.Get, "/tasks").apply {
                assertEquals(HttpStatusCode.OK, response.status())

                val findAllTasksResponse = mapper.readValue<List<FindAllTasksResponse>>(response.content!!)
                assertEquals(0, findAllTasksResponse.size)
            }

        }
    }

    @Test
    fun `タスク取得時、該当タスクが存在しない場合、存在しない旨のメッセージを返却する`() {
        // 何でも良いのでタスクIDを生成。
        val taskId = TaskId.generate()

        withTestModule {
                handleRequest(HttpMethod.Get, "/task/${taskId.value()}").apply {
                    val response1 = mapper.readValue<ErrorMessageResponse>(response.content!!)
                    assertEquals(HttpStatusCode.BadRequest.value.toString(), response1.status)
                    assertEquals("The task is not found.", response1.message)
                }
        }
    }

    @Test
    fun `タスクの更新ができること`() {
        withTestModule {

            handleRequest(HttpMethod.Post, "/task") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())

                val createTaskRequest = CreateTaskRequest(
                    "タスク２"
                )

                val createTaskRequestJsonBody = mapper.writeValueAsString(createTaskRequest)
                setBody(createTaskRequestJsonBody)
            }

            val taskIds: List<String>
            handleRequest(HttpMethod.Get, "/tasks").apply {
                val findAllTasksResponse = mapper.readValue<List<FindAllTasksResponse>>(response.content!!)
                taskIds = findAllTasksResponse
                    .map { it.id }
            }

            val updatedTaskName = TaskName.valueOf("タスク２")
            val updatedTaskStatus = TaskStatus.DONE
            val updatedDueDate = DueDate.valueOf(LocalDate.now().plusDays(1))

            handleRequest(HttpMethod.Put,"/task/${taskIds[0]}") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())

                val updateTaskRequest = UpdateTaskRequest(
                    name = updatedTaskName.value(),
                    status = updatedTaskStatus.toString(),
                    dueDate = updatedDueDate.value()
                )

                val updateTaskRequestBody = mapper.writeValueAsString(updateTaskRequest)
                setBody(updateTaskRequestBody)
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }

            handleRequest(HttpMethod.Get, "/task/${taskIds[0]}").apply {
                assertEquals(HttpStatusCode.OK, response.status())

                val findTaskByIdResponse = mapper.readValue<FindTaskByIdResponse>(response.content!!)
                assertEquals(updatedTaskName.value(), findTaskByIdResponse.name)
                assertEquals(updatedTaskStatus.toString(), findTaskByIdResponse.status)
                assertEquals(updatedDueDate.value().toString(), findTaskByIdResponse.dueDate)
            }

                handleRequest(HttpMethod.Delete, "/task/${taskIds[0]}")
            }

        }
}

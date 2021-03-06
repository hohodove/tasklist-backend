package com.example.domain.model.task.value_object

import com.example.domain.model.task.exception.TaskInvalidRequestException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows

internal class TaskIdTest {

    @Test
    fun `タスクIDはUUID形式で自動生成できる`() {

        val taskId = TaskId.generate()
        println("generated taskId: ${taskId.value()}")

        val pattern = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}".toRegex()
        assertTrue(taskId.value().matches(pattern))
    }

    @Test
    fun `UUID形式の値を指定してタスクIDが作成できる`() {

        val taskId = TaskId.valueOf("1234abcd-56ef-78ab-90cd-123456efabcd")

        val pattern = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}".toRegex()
        assertTrue(taskId.value().matches(pattern))
    }

    @Test
    fun `UUID形式ではない値を指定したタスクIDの作成はできない`() {

        val error = assertThrows<TaskInvalidRequestException> {
            TaskId.valueOf("Invalid_TaskId")
        }
        assertEquals("Task id must be UUIDv4 format.", error.message)
    }
}

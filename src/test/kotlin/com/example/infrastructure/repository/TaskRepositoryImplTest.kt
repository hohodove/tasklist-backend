package com.example.infrastructure.repository

import com.example.domain.model.task.entity.Task
import com.example.domain.model.task.value_object.DueDate
import com.example.domain.model.task.value_object.TaskId
import com.example.domain.model.task.value_object.TaskName
import com.example.domain.model.task.value_object.TaskStatus
import com.example.domain.repository.TaskRepository
import com.example.test_util.TaskTestFactory
import com.example.test_util.withTestModule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.koin.test.KoinTest
import org.koin.test.inject
import java.time.LocalDate

internal class TaskRepositoryImplTest : KoinTest {

    val taskRepository by inject<TaskRepository>()

    @Test
    fun `タスクの保存、取得、削除ができる`() {
        withTestModule {

            val taskId1 = "1234abcd-56ef-78ab-90cd-123456efabcd"
            val task1 = TaskTestFactory.create2(taskId1)

            val task2 = Task.reconstruct(
                TaskId.valueOf("2345bcde-67fa-89bc-01de-234567fabcde"),
                TaskName.valueOf("タスク２"),
                TaskStatus.NOT_COMPLETED,
                DueDate.reconstruct(LocalDate.of(2002, 2, 2))
            )

            taskRepository.save(task1)
            taskRepository.save(task2)
            assertEquals(2, taskRepository.findAll().size)

            val selectedTask = taskRepository.findById(task1.taskId)
            assertEquals(taskId1, selectedTask?.taskId?.value())

            taskRepository.remove(task1.taskId)
            taskRepository.remove(task2.taskId)

            val tasks = taskRepository.findAll()
            assertEquals(0, tasks.size)
        }
    }

    @Test
    fun `取得結果が0件の場合にもエラーが発生しないこと`() {

        withTestModule {

            assertTrue(taskRepository.findAll().isEmpty())

            val notExistTaskId = TaskId.valueOf("1234abcd-56ef-78ab-90cd-123456efabcd")
            val result = taskRepository.findById(notExistTaskId)
            assertEquals(null, result)
        }
    }
}

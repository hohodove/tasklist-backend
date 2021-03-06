package com.example.test_util

import com.example.domain.model.task.entity.Task
import com.example.domain.model.task.value_object.DueDate
import com.example.domain.model.task.value_object.TaskId
import com.example.domain.model.task.value_object.TaskName
import com.example.domain.model.task.value_object.TaskStatus
import java.time.LocalDate

object TaskTestFactory {

    fun create(
        taskId: String = TaskId.generate().value(),
        taskName: String = "タスク",
        taskStatus: String = TaskStatus.NOT_COMPLETED.toString(),
        dueDate: LocalDate = DueDate.createDefault().value()
    ): Task =
        Task.reconstruct(
            TaskId.valueOf(taskId),
            TaskName.valueOf(taskName),
            TaskStatus.valueOf(taskStatus),
            DueDate.reconstruct(dueDate)
        )
}

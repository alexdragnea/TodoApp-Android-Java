package ro.unitbv.todoapp.adapter;

import ro.unitbv.todoapp.model.Task;

public interface OnToDoClickListener {
    void toDoOnclick(int position, Task task);

    void radioOnClick(Task task);

    void chipOnClick(Task task);
}

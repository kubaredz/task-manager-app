package pl.edu.pja.taskmanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Date;
import java.util.List;

import pl.edu.pja.taskmanager.adapter.TaskAdapter;
import pl.edu.pja.taskmanager.model.Task;
import pl.edu.pja.taskmanager.model.TaskViewModel;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private TaskAdapter taskAdapter;
    private TaskViewModel taskViewModel;
    private static final int ADD_TASK_REQUEST_CODE = 1;
    private static final int EDIT_TASK_REQUEST_CODE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_to_do);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: 30/04/2022 refactor button name
        recyclerView = findViewById(R.id.recyclerview);
        floatingActionButton = findViewById(R.id.floatingActionButton);

        // TODO : 30/04/2022 change place of methods
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter();
        recyclerView.setAdapter(taskAdapter);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewTaskActivity.class);
                startActivityForResult(intent, ADD_TASK_REQUEST_CODE);
            }
        });
        taskViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication()))
                .get(TaskViewModel.class);
        taskViewModel.getAllTasks().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                taskAdapter.submitList(tasks);
            }
        });
        // TODO: 30/04/2022 15:37
//        recyclerView..setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                taskViewModel.delete(taskAdapter.getTask(view.getVerticalScrollbarPosition()));
//                Toast.makeText(MainActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.RIGHT){
                    taskViewModel.delete(taskAdapter.getTask(viewHolder.getAdapterPosition()));
                    Toast.makeText(MainActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(MainActivity.this, NewTaskActivity.class);
                    intent.putExtra(NewTaskActivity.EXTRA_ID, taskAdapter.getTask(viewHolder.getAdapterPosition()).getId());
                    intent.putExtra(NewTaskActivity.EXTRA_TITLE, taskAdapter.getTask(viewHolder.getAdapterPosition()).getTitle());
                    intent.putExtra(NewTaskActivity.EXTRA_DESCRIPTION, taskAdapter.getTask(viewHolder.getAdapterPosition()).getDescription());
                    intent.putExtra(NewTaskActivity.EXTRA_PRIORITY, taskAdapter.getTask(viewHolder.getAdapterPosition()).getPriority());
                    intent.putExtra(NewTaskActivity.EXTRA_PROGRESS, taskAdapter.getTask(viewHolder.getAdapterPosition()).getProgress());
                    intent.putExtra(NewTaskActivity.EXTRA_YEAR, taskAdapter.getTask(viewHolder.getAdapterPosition()).getDate());
                    intent.putExtra(NewTaskActivity.EXTRA_MONTH, taskAdapter.getTask(viewHolder.getAdapterPosition()).getDate());
                    intent.putExtra(NewTaskActivity.EXTRA_DAY, taskAdapter.getTask(viewHolder.getAdapterPosition()).getDate());

                    startActivityForResult(intent, EDIT_TASK_REQUEST_CODE);
                }
            }
        }).attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ADD_TASK_REQUEST_CODE && resultCode == RESULT_OK) {
            String title = data.getStringExtra(NewTaskActivity.EXTRA_TITLE);
            String description = data.getStringExtra(NewTaskActivity.EXTRA_DESCRIPTION);
            int priority = data.getIntExtra(NewTaskActivity.EXTRA_PRIORITY, 1);
            int year = data.getIntExtra(NewTaskActivity.EXTRA_YEAR,2000);
            int month = data.getIntExtra(NewTaskActivity.EXTRA_MONTH,1);
            int day = data.getIntExtra(NewTaskActivity.EXTRA_DAY,25);
//            String date = data.getStringExtra(NewTaskActivity.EXTRA_DATE);
            Date date = new Date(year, month, day);
            int progress = data.getIntExtra(NewTaskActivity.EXTRA_PROGRESS, 0);

            Task task = new Task(title, description, priority, progress, date.getTime());
            taskViewModel.insert(task);

            Toast.makeText(this, "Task saved", Toast.LENGTH_SHORT).show();
        }else if(requestCode == EDIT_TASK_REQUEST_CODE && resultCode == RESULT_OK){
            int id = data.getIntExtra(NewTaskActivity.EXTRA_ID, -1);
            if (id == -1){
                Toast.makeText(this, "Task can't be updated", Toast.LENGTH_SHORT).show();
                return;
            }
            String title = data.getStringExtra(NewTaskActivity.EXTRA_TITLE);
            String description = data.getStringExtra(NewTaskActivity.EXTRA_DESCRIPTION);
            int priority = data.getIntExtra(NewTaskActivity.EXTRA_PRIORITY, 1);
            int year = data.getIntExtra(NewTaskActivity.EXTRA_YEAR,2000);
            int month = data.getIntExtra(NewTaskActivity.EXTRA_MONTH,1);
            int day = data.getIntExtra(NewTaskActivity.EXTRA_DAY,25);
//            String date = data.getStringExtra(NewTaskActivity.EXTRA_DATE);
            Date date = new Date(year, month, day);
            int progress = data.getIntExtra(NewTaskActivity.EXTRA_PROGRESS, 0);
            Task task = new Task(title, description, priority, progress,date.getTime());
            task.setId(id);
            taskViewModel.update(task);
            Toast.makeText(this, "Task updated!", Toast.LENGTH_SHORT).show();

        }else {
            Toast.makeText(this, "Task not saved", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
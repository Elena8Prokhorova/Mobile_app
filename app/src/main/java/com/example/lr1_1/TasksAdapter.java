package com.example.lr1_1;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> {
    private final LayoutInflater inflater;
    private final List<TaskInfo> tasks;
    private FragmentManager manager;
    //private final List<String> tasks;

    TasksAdapter(Context context, List<TaskInfo> tasks) {
    //TasksAdapter(Context context, List<String> tasks) {
        this.tasks = tasks;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public TasksAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TasksAdapter.ViewHolder holder, int position) {
        TaskInfo task = tasks.get(position);
        if (task.getStatus().equals("Выполнена")) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#C404082A"));
        }
        else if (task.getStatus().equals("В работе")) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#EB7444"));
        }
        else {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#000000"));
        }
        if (task.getName().isEmpty() || task.getAddress().isEmpty() ||
        task.getTime().isEmpty()) {
            holder.nameView.setText("newName");
            holder.addressView.setText("newAddress");
            holder.timeView.setText("00:00");
        }
        else {
            holder.nameView.setText(task.getName());
            holder.addressView.setText(task.getAddress());
            holder.timeView.setText(task.getTime());
        }
        holder.setClickMethod(position);

    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameView, addressView, timeView;
        final CardView cardView;

        public ViewHolder(View view) {
            super(view);
            nameView = view.findViewById(R.id.textName);
            addressView = view.findViewById(R.id.textPrice);
            timeView = view.findViewById(R.id.textTime);
            cardView = view.findViewById(R.id.card_view);
        }

        public void setClickMethod(int position) {

            /*imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String artist = gridArrayList.get(position).getArtist();
                    Toast.makeText(context, "Исполнитель - это "+artist,
                            Toast.LENGTH_SHORT).show();
                }
            });*/
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                    //DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                    String dateText = dateFormat.format(new Date());
                    //Log.i("DAteAnfTime", dateText+" "+timeFormat.format(new Date()));
                    String message = " КЛИЕНТ: " + tasks.get(position).getName() + "\n" +
                            " АДРЕС: " + tasks.get(position).getAddress() + "\n" +
                            " ВРЕМЯ: " + tasks.get(position).getTime() + "\n" +
                            " ОПИСАНИЕ: " + tasks.get(position).getDescription() + "\n" +
                            " СВЯЗЬ: " + tasks.get(position).getEmail() + ", " +
                            tasks.get(position).getTelephone() + "\n" +
                            " СТАТУС: " + tasks.get(position).getStatus() + "\n";

                    if (tasks.get(position).getStatus().equals("Выполнена")) {
                        new AlertDialog.Builder(view.getContext())
                                .setTitle("Заявка на " + dateText + " - ВЫПОЛНЕНА")
                                .setIcon(R.drawable.info)
                                .setMessage(message)
                                .setPositiveButton("OK", ((dialogCompleted, whichCompleted)
                                        -> dialogCompleted.dismiss()))
                                .show();
                    } else if (tasks.get(position).getStatus().equals("В работе")) {
                        new AlertDialog.Builder(view.getContext())
                                .setTitle("Заявка на " + dateText + " - В РАБОТЕ")
                                .setIcon(R.drawable.info)
                                .setMessage(message)
                                .setNeutralButton("OK", ((dialogInWork, whichInWork)
                                        -> dialogInWork.dismiss()))

                                .show();
                    } else {
                        new AlertDialog.Builder(view.getContext())
                                .setTitle("Заявка на " + dateText)
                                .setIcon(R.drawable.info)
                                .setMessage(message)
                                .setPositiveButton("В работу", (dialog, which) -> {
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    FirebaseDatabase database = FirebaseDatabase.getInstance("https://it-dragon-f7c11-default-rtdb.firebaseio.com/");
                                    DatabaseReference taskRef = database.getReference("Tasks").child(user.getUid());

                                    // Проверка количества задач со статусом "В работе"
                                    ArrayList<TaskInfo> taskInWork = new ArrayList<>();
                                    for (TaskInfo task : tasks) {
                                        if (task.getStatus().equals("В работе")) {
                                            taskInWork.add(task);
                                        }
                                    }
                                    // Изменение статуса в БД
                                    if (taskRef.get().hashCode() != 0 && taskInWork.isEmpty()) {
                                        taskRef.child("CurrentDate").child(String.valueOf(position + 1))
                                                .child("status").setValue("В работе");

                                        // Сообщение пользователю
                                        new AlertDialog.Builder(view.getContext())
                                                .setTitle("Задача в работе!")
                                                .setIcon(R.drawable.notification)
                                                .setMessage("Данная задача сменила статус на: \"В работе\".\n" +
                                                        "Перейдите в раздел \"ЗАЯВКА\" для её выполнения!")
                                                .setPositiveButton("OK", (dialogChangeStatus, whichChangeStatus) ->
                                                        dialogChangeStatus.dismiss())
                                                .show();
                                    } else {
                                        new AlertDialog.Builder(view.getContext())
                                                .setTitle("Ошибка!")
                                                .setIcon(R.drawable.info)

                                                .setMessage("Завершите выполнение текущей задачи перед выбором следующей!")
                                                .setNegativeButton("Отмена", (dialogCompletionWork, whichCompletionWork) -> dialog.dismiss())
                                                .show();
                                    }
                                })
                                .setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss())
                                .show();
                    }
                }
                
            });
        }
    }
}

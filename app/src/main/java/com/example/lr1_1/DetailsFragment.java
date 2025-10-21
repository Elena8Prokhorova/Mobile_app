package com.example.lr1_1;

import static com.example.lr1_1.MainActivity.REQUEST_CODE;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yandex.mapkit.MapKitFactory;
import com.google.android.gms.maps.MapView;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class DetailsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";

    private String mParam1;
    private String selectedTypePager;
    private TasksAdapter adapter;
    private static List<TaskInfo> tasks = new ArrayList<>();
    private ServicesAdapter adapterServices;
    private static List<String> servicesChangedList = new ArrayList<>();
    private static TaskInfo selectedCurrentTask = null;
    private org.osmdroid.views.MapView mapView;

    public DetailsFragment() {}

    public static DetailsFragment newInstance(String param1) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Log.w("ARG_PARAM1", getArguments().getString(ARG_PARAM1));
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mParam1.equals("map"))
            return inflater.inflate(R.layout.activity_map, container, false);
        else if (mParam1.equals("tasks"))
            return inflater.inflate(R.layout.activity_tasks, container, false);
        return inflater.inflate(R.layout.activity_proposal, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mParam1.equals("map")) {
            // Инициализация конфигурации OSMDroid
            Configuration.getInstance().load(view.getContext(), PreferenceManager.getDefaultSharedPreferences(view.getContext()));

            mapView = view.findViewById(R.id.map);
            mapView.setTileSource(TileSourceFactory.MAPNIK);


            List<TaskInfo> selectedTask = new ArrayList<>();
            List<TaskInfo> tasks = new ArrayList<>();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://it-dragon-f7c11-default-rtdb.firebaseio.com/");
            DatabaseReference taskRef = database.getReference("Tasks").child(user.getUid());
            if (taskRef.get().hashCode() != 0) {
                taskRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot tasksForUserSnapshot) {
                        tasks.clear();
                        for (DataSnapshot taskForCurrentUserSnapshot : tasksForUserSnapshot.child("CurrentDate").getChildren()) {
                            TaskInfo taskInfo = taskForCurrentUserSnapshot.getValue(TaskInfo.class);
                            if (taskInfo != null) {
                                tasks.add(taskInfo);
                            }
                        }

                        for (TaskInfo task : tasks) {
                            if (task.getStatus().equals("В работе")) {
                                selectedTask.add(task);
                            }
                        }

                        Geocoder geocoder = new Geocoder(view.getContext(), Locale.getDefault());
                        List<Address> addressList;
                        double latitude = 0.0;
                        double longitude = 0.0;
                        try {
                            if (!selectedTask.isEmpty() && selectedTask.get(0) != null) {
                                addressList = geocoder.getFromLocationName(selectedTask.get(0).getAddress(), 1);
                                if (addressList != null && !addressList.isEmpty()) {
                                    Address address = addressList.get(0);
                                    latitude = address.getLatitude();
                                    longitude = address.getLongitude();

                                } else {
                                    Log.i("ADDRESS", "Aдрес не найден");
                                }
                            }
                            else {
                                mapView.getController().setZoom(19);
                                mapView.getController().setCenter(new GeoPoint(48.493504, 135.063257)); // Координаты Хабаровска,текущая задача
                                mapView.invalidate(); // Обновление карты
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.i("ADDRESS2", "Aдрес не найден");
                        }

                        if (latitude!=0.0 && longitude!=0.0) {
                            // Установка начального положения карты
                            mapView.getController().setZoom(19);
                            mapView.getController().setCenter(new GeoPoint(latitude, longitude)); // Координаты Хабаровска,текущая задача

                            Marker marker = new Marker(mapView);
                            marker.setPosition(new GeoPoint(latitude, longitude));
                            marker.setTitle(selectedTask.get(0).getAddress());
                            mapView.getOverlays().add(marker);
                        }

                        mapView.invalidate(); // Обновление карты
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        mapView.getController().setZoom(19);
                        mapView.getController().setCenter(new GeoPoint(48.493504, 135.063257)); // Координаты Хабаровска,текущая задача
                        mapView.invalidate(); // Обновление карты
                    }
                });
            }

        }
        else if (mParam1.equals("tasks")) {
            Log.i("Date", getDate("day"));
            Log.i("Date_time", getDate("time"));

            TextView textLackTasks = view.findViewById(R.id.textLackTasks);
            TextView textExplanations = view.findViewById(R.id.textExplanations);
            TextView textEmail = view.findViewById(R.id.textEmail);
            TextView textTelephone = view.findViewById(R.id.textTelephone);
            RecyclerView recyclerView = view.findViewById(R.id.listTasks);
            ArrayList<TaskInfo> tasksCompleted = new ArrayList<>();
            ArrayList<TaskInfo> tasksInQueue = new ArrayList<>();
            ArrayList<TaskInfo> taskActive = new ArrayList<>();

            adapter = new TasksAdapter(view.getContext(), tasks);
            recyclerView.setAdapter(adapter);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://it-dragon-f7c11-default-rtdb.firebaseio.com/");
            DatabaseReference taskRef = database.getReference("Tasks").child(user.getUid());
            if (taskRef.get().hashCode() != 0) {
                taskRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot tasksForUserSnapshot) {
                        tasks.clear();

                        for (DataSnapshot taskForCurrentUserSnapshot : tasksForUserSnapshot.child("CurrentDate").getChildren()) {
                            TaskInfo taskInfo = taskForCurrentUserSnapshot.getValue(TaskInfo.class);
                            if (taskInfo != null) {
                                if (taskInfo.getStatus().equals("В работе")) {
                                    taskActive.add(taskInfo);
                                } else if (taskInfo.getStatus().equals("Выполнена")) {
                                    tasksCompleted.add(taskInfo);
                                }
                                else tasksInQueue.add(taskInfo);
                            }
                        }
                        tasks.addAll(taskActive);
                        tasks.addAll(tasksInQueue);
                        tasks.addAll(tasksCompleted);

                        adapter.notifyDataSetChanged();

                        if (tasks.isEmpty()) {
                            recyclerView.setVisibility(View.INVISIBLE);
                            textLackTasks.setVisibility(View.VISIBLE);
                            textExplanations.setVisibility(View.VISIBLE);
                            textEmail.setVisibility(View.VISIBLE);
                            textTelephone.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }
        else if (mParam1.equals("proposal")) {
            tasks = new ArrayList<>();

            TextView textView = view.findViewById(R.id.textView);
            TextView textTitle = view.findViewById(R.id.textTitle);
            Spinner spinner = view.findViewById(R.id.spinner);
            Button buttonAddFromList = view.findViewById(R.id.buttonAddFromList);
            Button buttonAddNewService = view.findViewById(R.id.buttonAddNewService);
            RecyclerView recyclerView = view.findViewById(R.id.recyclerServices);
            LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            Button buttonCreateInvoice = view.findViewById(R.id.buttonCreateInvoice);

            adapterServices = new ServicesAdapter(view.getContext(), servicesChangedList);
            recyclerView.setAdapter(adapterServices);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://it-dragon-f7c11-default-rtdb.firebaseio.com/");


            DatabaseReference taskRef = database.getReference("Tasks").child(user.getUid());
            if (taskRef.get().hashCode() != 0) {
                taskRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot tasksForUserSnapshot) {
                        tasks.clear();
                        for (DataSnapshot taskForCurrentUserSnapshot : tasksForUserSnapshot.child("CurrentDate").getChildren()) {
                            TaskInfo taskInfo = taskForCurrentUserSnapshot.getValue(TaskInfo.class);
                            if (taskInfo != null) {
                                tasks.add(taskInfo);
                            }
                        }

                        for (TaskInfo task : tasks) {
                            if (task.getStatus().equals("В работе")) {
                                selectedCurrentTask = task;
                            }
                        }
                        Log.i("IN WORK TASK", String.valueOf(selectedCurrentTask));
                        if (selectedCurrentTask != null) {
                            textView.setVisibility(View.INVISIBLE);
                            textTitle.setVisibility(View.VISIBLE);
                            spinner.setVisibility(View.VISIBLE);
                            buttonAddFromList.setVisibility(View.VISIBLE);
                            buttonAddNewService.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.VISIBLE);
                            buttonCreateInvoice.setVisibility(View.VISIBLE);

                            List<Services> services = new ArrayList<>();
                            List<String> servicesFullList = new ArrayList<>();
                            DatabaseReference serviceRef = database.getReference("Services").child("FullList");
                            if (serviceRef.get().hashCode() != 0) {
                                serviceRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot servicesFullListSnapshot) {
                                        services.clear();
                                        for (DataSnapshot serviceForCurrentSnapshot : servicesFullListSnapshot.getChildren()) {
                                            Services serviceInfo = new Services(serviceForCurrentSnapshot.getKey(),serviceForCurrentSnapshot.getValue(String.class));
                                            if (serviceInfo != null) {
                                                services.add(serviceInfo);
                                                servicesFullList.add(serviceInfo.serviceToString(serviceInfo));
                                            }
                                        }

                                        Spinner spinner = view.findViewById(R.id.spinner);
                                        // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
                                        ArrayAdapter<String> adapter = new ArrayAdapter(view.getContext(), android.R.layout.simple_spinner_item, servicesFullList);
                                        // Определяем разметку для использования при выборе элемента
                                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        // Применяем адаптер к элементу spinner
                                        spinner.setAdapter(adapter);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            }

                            AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    // Получаем выбранный объект
                                    String item = (String)parent.getItemAtPosition(position);
                                    Log.w("SERVICEEEEEEEEEEEEEEEEEEEE",item);

                                    buttonAddFromList.setOnClickListener(v -> {
                                        servicesChangedList.add(item);
                                        Log.i("KJHGFGHJKL:LK", String.valueOf(servicesChangedList.size()));
                                        adapterServices.notifyDataSetChanged();

                                        HashMap<String, String> serviceInfo = new HashMap<>();
                                        serviceInfo.put("servicename", item.substring(0, item.indexOf(":")));
                                        serviceInfo.put("price", item.substring(item.indexOf(":")+2));
                                        database.getReference().child("Services").child("CurrentList")
                                                .child(user.getUid()).child(String.valueOf(servicesChangedList.size())).setValue(serviceInfo);
                                    });
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                }
                            };
                            spinner.setOnItemSelectedListener(itemSelectedListener);

                            buttonAddNewService.setOnClickListener(v -> {
                                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                builder.setTitle("Добавление новой услуги")
                                        .setIcon(R.drawable.add_service)
                                        .setMessage("Введите название услуги, затем нажмите \"Далее\" и введите её цену (без указания валюты)");
                                final EditText inputNameService = new EditText(view.getContext());
                                builder.setView(inputNameService);
                                builder.setPositiveButton("Далее", ((dialog, which) -> {
                                    String nameService = inputNameService.getText().toString();
                                    if (nameService.isEmpty()) {
                                        new AlertDialog.Builder(view.getContext())
                                                .setTitle("Ошибка ввода данных")
                                                .setIcon(R.drawable.error_mark)
                                                .setMessage("Ошибка! Поля не могут быть пустыми!")
                                                .setPositiveButton("OK", (dialogCancel, whichCancel) -> dialog.dismiss())
                                                .show();
                                    }
                                    else {
                                        final EditText inputPriceService = new EditText(view.getContext());
                                        new AlertDialog.Builder(view.getContext())
                                                .setTitle("Добавление новой услуги")
                                                .setIcon(R.drawable.add_service)
                                                .setMessage("Введите цену услуги (без указания валюты)")
                                                .setView(inputPriceService)
                                                .setPositiveButton("Добавить", (dialog1, which1) -> {
                                                    String priceService = inputPriceService.getText().toString();
                                                    if (priceService.isEmpty()) {
                                                        new AlertDialog.Builder(view.getContext())
                                                                .setTitle("Ошибка ввода данных")
                                                                .setIcon(R.drawable.error_mark)
                                                                .setMessage("Ошибка! Поля не могут быть пустыми!")
                                                                .setPositiveButton("OK", (dialogCancel, whichCancel) -> dialog.dismiss())
                                                                .show();
                                                    }
                                                    else {
                                                        servicesChangedList.add(nameService+": "+priceService+" руб.");
                                                        Log.i("KJHGFGHJKL:{{LK", String.valueOf(servicesChangedList.size()));
                                                        adapterServices.notifyDataSetChanged();

                                                        HashMap<String, String> serviceInfo = new HashMap<>();
                                                        serviceInfo.put("servicename", nameService);
                                                        serviceInfo.put("price", priceService);
                                                        database.getReference().child("Services").child("CurrentList")
                                                                .child(user.getUid()).child(String.valueOf(servicesChangedList.size())).setValue(serviceInfo);
                                                    }
                                                })
                                                .setNegativeButton("Отмена", (dialog1, which1) -> dialog1.dismiss())
                                                .show();
                                    }
                                }))
                                        .setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss());
                                builder.show();
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }

            buttonCreateInvoice.setOnClickListener(v -> {
                if (adapterServices.getItemCount() == 0) {
                    new AlertDialog.Builder(view.getContext())
                            .setTitle("Ошибка!")
                            .setIcon(R.drawable.error_mark)
                            .setMessage("\tСчёт-фактура не может быть пустой!\n" +
                                    "Добавьте выполненные услуги!")
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                            .show();
                }
                else {
                    new AlertDialog.Builder(view.getContext())
                            .setTitle("Формирование счёт-фактуры")
                            .setIcon(R.drawable.question)
                            .setMessage("Вы уверены в своём выборе?")
                            .setPositiveButton("Да", (dialog, which) -> {
                                for (int i = 0; i < adapterServices.getItemCount(); i++) {
                                    database.getReference("Services").child("CurrentList")
                                            .child(user.getUid()).child(String.valueOf(i+1)).removeValue();
                                }

                                // Запрос разрешений
                                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
                                createPdf(view, selectedCurrentTask, servicesChangedList);
                                sendEmailWithAttachment(selectedCurrentTask.getEmail());

                                servicesChangedList.clear();
                                adapterServices.notifyDataSetChanged();

                                if (taskRef.get().hashCode() != 0) {
                                    taskRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot tasksForUserSnapshot) {
                                            tasks.clear();
                                            for (DataSnapshot taskForCurrentUserSnapshot : tasksForUserSnapshot.child("CurrentDate").getChildren()) {
                                                TaskInfo taskInfo = taskForCurrentUserSnapshot.getValue(TaskInfo.class);
                                                if (taskInfo != null) {
                                                    if (taskInfo.getStatus().equals("В работе")) {
                                                        taskRef.child("CurrentDate").child(taskForCurrentUserSnapshot.getKey()).child("status").setValue("Выполнена");
                                                        adapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }

                            })
                            .setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss())
                            .show();
                }
            });


            ImageButton buttonExit = view.findViewById(R.id.buttonExit);
            buttonExit.setOnClickListener(v -> {
                new AlertDialog.Builder(requireContext())
                        .setTitle("ВЫХОД ИЗ ЛИЧНОГО КАБИНЕТА")
                        .setIcon(R.drawable.question)
                        .setMessage("Вы уверены, что хотите выйти?")
                        .setPositiveButton("Да", (dialog, which) -> {
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(requireActivity(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        })
                        .setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss())
                        .show();
            });

        }

        Log.i("getInfo", mParam1);

    }

    public void onStart(@NonNull MapView mapView) {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    public void onStop(@NonNull MapView mapView) {
        super.onStop();
        MapKitFactory.getInstance().onStop();
        mapView.onStop();
    }


    public void createPdf(View view, TaskInfo taskInfo, List<String> services) {
        // Создание PDF-документа
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();

        // Создание страницы
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        paint.setTextSize(10);
        canvas.drawText("СЧЁТ-ФАКТУРА", 50, 50, paint);
        canvas.drawText("Клиент: "+taskInfo.getName(), 20, 70, paint);
        canvas.drawText("Адрес: "+taskInfo.getAddress(), 20, 85, paint);
        canvas.drawText("Время: "+getDate("day")+", "+taskInfo.getTime(), 20, 100, paint);
        canvas.drawText("Описание: "+taskInfo.getDescription(), 20, 115, paint);
        canvas.drawText("УСЛУГИ", 50, 140, paint);
        for (int i = 0; i < services.size(); i++) {
            canvas.drawText((i+1)+") "+services.get(i), 20, 155+i*15, paint);
        }
        canvas.drawText("ИТОГО: "+getTotalPrice(services)+" руб.", 20, 155+services.size()*15+10, paint);

        pdfDocument.finishPage(page);

        // Сохранение PDF-документа
        String directoryPath = Environment.getExternalStorageDirectory().getPath() + "/MyPDFs/";
        File dir = new File(directoryPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String filePath = directoryPath + "current_task.pdf";
        try {
            pdfDocument.writeTo(new FileOutputStream(filePath));
            Toast.makeText(view.getContext(), "PDF создан: " + filePath, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(view.getContext(), "Ошибка при создании PDF", Toast.LENGTH_SHORT).show();
        }

        pdfDocument.close();
    }

    public void sendEmailWithAttachment(String email) {
        // Путь к вашему PDF-файлу
        File file = new File(Environment.getExternalStorageDirectory() + "/MyPDFs/current_task.pdf");

        // Создаем Intent для отправки электронной почты
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("application/pdf");

        // Указываем адреса получателей (можно оставить пустым, чтобы выбрать позже)
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});

        // Указываем тему и текст сообщения
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "IT Dragon - Заявка");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Здравствуйте! Ваша заявка была выполнена! Оплатите работу сотруднику нашей компании!");

        // Добавляем вложение
        if (file.exists()) {
            Uri uri = FileProvider.getUriForFile(requireContext(), "com.example.lr1_1.fileprovider", file);
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Разрешаем доступ к файлу
        }

        // Запускаем Intent
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешение предоставлено
            } else {
                // Разрешение отклонено
                Toast.makeText(requireView().getContext(), "Разрешение на запись отклонено", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getTotalPrice(List<String> services) {
        int sum = 0;
        for (int i = 0; i < services.size(); i++) {
            sum += Integer.parseInt(services.get(i).substring(services.get(i).indexOf(":")+2, services.get(i).length()-5));
        }

        return String.valueOf(sum);
    }

    public ArrayList<TaskInfo> setInitialData() {
        ArrayList<TaskInfo> tasks = new ArrayList<TaskInfo>();
        tasks.add(new TaskInfo("name", "address", ""+new Date().getTime(),
                "89245678901", "test@gmail.com","Обычная проблема",
                "Не выполнена"));
        tasks.add(new TaskInfo("name2", "address2", ""+new Date().getTime(),
                "89245678902", "test@gmail.com","Обычная проблема2",
                "Выполнена"));
        tasks.add(new TaskInfo("name3", "address3", ""+new Date().getTime(),
                "89245678901", "test@gmail.com","Обычная проблема",
                "Не выполнена"));
        tasks.add(new TaskInfo("name4", "address4", ""+new Date().getTime(),
                "89245678901", "test@gmail.com","Обычная проблема",
                "Не выполнена"));
        tasks.add(new TaskInfo("name5", "address5", ""+new Date().getTime(),
                "89245678901", "test@gmail.com","Обычная проблема",
                "Не выполнена"));
        tasks.add(new TaskInfo("name6", "address6", ""+new Date().getTime(),
                "89245678901", "test@gmail.com","Обычная проблема",
                "Не выполнена"));
        tasks.add(new TaskInfo("name7", "address7", ""+new Date().getTime(),
                "89245678901", "test@gmail.com","Обычная проблема",
                "Не выполнена"));

        return tasks;
    }

    public String getDate(String type) {
        // Текущее время
        Date currentDate = new Date();
        // Форматирование времени как "день.месяц.год"
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String dateText = dateFormat.format(currentDate);
        // Форматирование времени как "часы:минуты:секунды"
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String timeText = timeFormat.format(currentDate);
        if (type.equals("day"))
            return dateText;
        return timeText;
    }

    public List<TaskInfo> getDatabase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://it-dragon-f7c11-default-rtdb.firebaseio.com/");
        DatabaseReference taskRef = database.getReference("Tasks").child(user.getUid());

        ArrayList<TaskInfo> tasksCompleted = new ArrayList<>();
        ArrayList<TaskInfo> tasksInQueue = new ArrayList<>();
        ArrayList<TaskInfo> taskActive = new ArrayList<>();

        if (taskRef.get().hashCode() != 0) {
            taskRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot tasksForUserSnapshot) {
                    tasks.clear();

                    for (DataSnapshot taskForCurrentUserSnapshot : tasksForUserSnapshot.child("CurrentDate").getChildren()) {
                        TaskInfo taskInfo = taskForCurrentUserSnapshot.getValue(TaskInfo.class);
                        if (taskInfo != null) {
                            if (taskInfo.getStatus().equals("В работе")) {
                                taskActive.add(taskInfo);
                            } else if (taskInfo.getStatus().equals("Выполнена")) {
                                tasksCompleted.add(taskInfo);
                            } else tasksInQueue.add(taskInfo);
                        }
                    }
                    tasks.addAll(taskActive);
                    tasks.addAll(tasksInQueue);
                    tasks.addAll(tasksCompleted);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

        return tasks;
    }
}

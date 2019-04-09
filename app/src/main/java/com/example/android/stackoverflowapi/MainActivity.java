package com.example.android.stackoverflowapi;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private StackOverflowAPI stackoverflowAPI;
    private String token;

    private Button authenticateButton;
    // Spinner for Questions
    private Spinner questionsSpinner;
    // List to display the response of thr API
    ListView questions_list, answers_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authenticateButton = findViewById(R.id.authenticate_button);
       // questionsSpinner = findViewById(R.id.questions_spinner);
        questions_list = findViewById(R.id.questions_list);
        answers_list = findViewById(R.id.answers_list);

        questions_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "A Question is selected", Toast.LENGTH_SHORT).show();
                // For every question in the spinner make a (Question) object to get its answer
                Question question = (Question) parent.getAdapter().getItem(position);
                stackoverflowAPI.getAnswersForQuestion(question.questionId).enqueue(answersCallback);
            }
        });

        // Make Adapters for Questions
        List<Question> questionList = FakeDataProvider.getQuestions();
        ArrayAdapter<Question> questionAdapter = new ArrayAdapter<Question>(MainActivity.this,
                android.R.layout.simple_list_item_1, questionList);
        questions_list.setAdapter(questionAdapter);

        // Make Adapters for  Answers
        List<Answer> answerList = FakeDataProvider.getAnswers();
        ArrayAdapter<Answer> answerAdapter = new ArrayAdapter<Answer>(MainActivity.this,
                android.R.layout.simple_list_item_1, answerList);
        answers_list.setAdapter(answerAdapter);

        createStackoverflowAPI();
        stackoverflowAPI.getQuestions().enqueue(questionsCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (token != null) {
            authenticateButton.setEnabled(false);
        }
    }

    // Use this method to  connect the Retrofit with the API interface
    private void createStackoverflowAPI() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(StackOverflowAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        stackoverflowAPI = retrofit.create(StackOverflowAPI.class);
    }


       public void onClick (View v){

            switch (v.getId()) {
                case android.R.id.text1:
                    if (token != null) {
                        // TODO
                    } else {
                        Toast.makeText(this, "You need to authenticate first", Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.authenticate_button:
                    Toast.makeText(MainActivity.this,"Authunticate!!", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1) {
            token = data.getStringExtra("token");
        }
    }

    // Set the Questions from the API in Callback list & Display them in the questions_list
    Callback<ListWrapper<Question>> questionsCallback = new Callback<ListWrapper<Question>>() {
        @Override
        public void onResponse(Call<ListWrapper<Question>> call, Response<ListWrapper<Question>> response) {

         if (response.isSuccessful()) {
            ListWrapper<Question> questions = response.body();
            ArrayAdapter<Question> arrayAdapter = new ArrayAdapter<Question>(MainActivity.this,
                    android.R.layout.simple_list_item_1, questions.items);
             questions_list.setAdapter(arrayAdapter);
            } else {
                Log.d("QuestionsCallback", "Code: " + response.code() + " Message: " + response.message());
            }
        }

        @Override
        public void onFailure(Call<ListWrapper<Question>> call, Throwable t) {

            t.printStackTrace();
        }
    };

    // Set the Answers from the API in Callback list & Display them in the answers_list
    Callback<ListWrapper<Answer>> answersCallback = new Callback<ListWrapper<Answer>>() {
        @Override
        public void onResponse(Call<ListWrapper<Answer>> call, Response<ListWrapper<Answer>> response) {

            if (response.isSuccessful()) {
                List<Answer> data = new ArrayList<>();
                data.addAll(response.body().items);
                ArrayAdapter<Answer> adapter = new ArrayAdapter<Answer>(MainActivity.this,
                        android.R.layout.simple_list_item_1, data);
                answers_list.setAdapter(adapter);
            } else {
                Log.d("QuestionsCallback", "Code: " + response.code() + " Message: " + response.message());
            }
        }

        @Override
        public void onFailure(Call<ListWrapper<Answer>> call, Throwable t) {

            t.printStackTrace();
        }
    };
}

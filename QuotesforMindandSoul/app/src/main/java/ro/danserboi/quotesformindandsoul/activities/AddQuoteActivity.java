package ro.danserboi.quotesformindandsoul.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ro.danserboi.quotesformindandsoul.Config;
import ro.danserboi.quotesformindandsoul.Utils;
import ro.danserboi.quotesformindandsoul.R;
import ro.danserboi.quotesformindandsoul.UserPrefs;
import ro.danserboi.quotesformindandsoul.models.Collection;
import ro.danserboi.quotesformindandsoul.responses.CollectionList;
import ro.danserboi.quotesformindandsoul.requests.AddQuoteRequest;
import ro.danserboi.quotesformindandsoul.retrofit.RetrofitAPI;
import ro.danserboi.quotesformindandsoul.retrofit.RetrofitClient;

public class AddQuoteActivity extends AppCompatActivity {
    ImageButton cameraButton;
    ImageButton imageButton;
    EditText quoteInput;
    EditText authorInput;
    EditText genreInput;
    SwitchCompat makeItPublicSwitch;
    Spinner selectCollectionSpinner;
    Button addQuoteButton;
    List<String> spinnerItems;
    List<Integer> collectionIds;

    private Uri imageUri;
    private Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quote);

        cameraButton = findViewById(R.id.cameraButton);
        imageButton = findViewById(R.id.imageButton);

        quoteInput = findViewById(R.id.quote_input);
        authorInput = findViewById(R.id.author_input);
        genreInput = findViewById(R.id.genre_input);


        makeItPublicSwitch = findViewById(R.id.make_it_public);
        selectCollectionSpinner = findViewById(R.id.select_collection_spinner);
        addQuoteButton = findViewById(R.id.add_quote);


        // get collections
        UserPrefs userPrefs = new UserPrefs(getApplicationContext());
        RetrofitAPI api = RetrofitClient.getRetrofitAPIInstance();
        Call<CollectionList> call = api.getCollections("Bearer " + userPrefs.getJWT());
        call.enqueue(new Callback<CollectionList>() {
            @Override
            public void onResponse(Call<CollectionList> call, Response<CollectionList> response) {
                if(response.isSuccessful()) {
                    spinnerItems =  new ArrayList<String>();
                    collectionIds = new ArrayList<>();
                    for(Collection c : response.body().getCollections()) {
                        spinnerItems.add(c.getName());
                        collectionIds.add(c.getId());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddQuoteActivity.this, android.R.layout.simple_spinner_item, spinnerItems);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    selectCollectionSpinner = (Spinner) findViewById(R.id.select_collection_spinner);
                    selectCollectionSpinner.setAdapter(adapter);
                } else {
                    assert response.errorBody() != null;
                    Utils.processErrorResponse(getApplicationContext(), response.errorBody());
                }
            }
            @Override
            public void onFailure(Call<CollectionList> call, Throwable t) {
                Utils.displayToast(getApplicationContext(), "Network connection error.");
            }
        });
        
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.buttonAnimation(view);
                startCameraIntentForResult();
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.buttonAnimation(view);
                startChooseImageIntentForResult();
            }
        });

        addQuoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String quoteText = quoteInput.getText().toString();
                final String authorText = authorInput.getText().toString();
                final String genreText = authorInput.getText().toString();
                if(TextUtils.isEmpty(quoteText) ||  TextUtils.isEmpty(authorText)) {
                    Utils.displayToast(AddQuoteActivity.this, "You must enter quote and author.");
                } else {
                    Integer collectionName;
                    if(spinnerItems.isEmpty()) {
                        collectionName = null;
                    } else {
                        int pos = 0;
                        for(String collName : spinnerItems) {
                            if(collName.equals(selectCollectionSpinner.getSelectedItem())) {
                                break;
                            }
                            pos++;
                        }
                        collectionName = collectionIds.get(pos);
                    }
                    Boolean isPublic = makeItPublicSwitch.isChecked();

                    UserPrefs userPrefs = new UserPrefs(AddQuoteActivity.this);
                    RetrofitAPI api = RetrofitClient.getRetrofitAPIInstance();
                    AddQuoteRequest addQuoteRequest = new AddQuoteRequest(authorText, quoteText, genreText, isPublic, collectionName);
                    Call<Void> call = api.addQuote("Bearer " + userPrefs.getJWT(), addQuoteRequest);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if(response.isSuccessful()) {
                                Utils.displayToast(AddQuoteActivity.this, "Quote has been added.");
                                quoteInput.setText(null);
                                authorInput.setText(null);
                                genreInput.setText(null);
                            } else {
                                assert response.errorBody() != null;
                                Utils.processErrorResponse(AddQuoteActivity.this, response.errorBody());
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Utils.displayToast(AddQuoteActivity.this, "Network connection error.");
                        }
                    });
                }
            }
        });
    }

    private void startCameraIntentForResult() {
        imageUri = null;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(takePictureIntent, Config.REQUEST_IMAGE_CAPTURE);
        }
    }

    private void startChooseImageIntentForResult() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Config.REQUEST_CHOOSE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Config.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                bitmap = Utils.getBitmapFromContentUri(getContentResolver(), imageUri);
                runTextRecognition(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == Config.REQUEST_CHOOSE_IMAGE && resultCode == RESULT_OK) {
            // In this case, imageUri is returned by the chooser, save it.
            imageUri = data.getData();
            try {
                bitmap = Utils.getBitmapFromContentUri(getContentResolver(), imageUri);
                runTextRecognition(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void runTextRecognition(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        TextRecognizer recognizer = TextRecognition.getClient(new TextRecognizerOptions.Builder().build());
        recognizer.process(image)
                .addOnSuccessListener(
                        new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text texts) {
                                quoteInput.setText(texts.getText());
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                            }
                        });
    }

}
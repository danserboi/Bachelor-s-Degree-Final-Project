package ro.danserboi.quotesformindandsoul;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ro.danserboi.quotesformindandsoul.activities.QuoteOfTheDayActivity;
import ro.danserboi.quotesformindandsoul.responses.DailyQuoteResponse;
import ro.danserboi.quotesformindandsoul.retrofit.RetrofitAPI;
import ro.danserboi.quotesformindandsoul.retrofit.RetrofitClient;

public class QuoteOfTheDayWorker extends Worker {

    public QuoteOfTheDayWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        UserPrefs userPrefs = new UserPrefs(getApplicationContext());
        RetrofitAPI api = RetrofitClient.getRetrofitAPIInstance();
        Call<DailyQuoteResponse> call = api.getDailyQuote();
        call.enqueue(new Callback<DailyQuoteResponse>() {
            @Override
            public void onResponse(Call<DailyQuoteResponse> call, Response<DailyQuoteResponse> response) {
                if(response.isSuccessful()) {
                    DailyQuoteResponse quoteOfTheDay = response.body();
                    userPrefs.setDailyQuoteId(quoteOfTheDay.getId());

                    if(userPrefs.getNotificationsOn()) {
                        showQuoteNotification(getApplicationContext(), quoteOfTheDay.getAuthor(), quoteOfTheDay.getText());
                    }
                } else {
                    assert response.errorBody() != null;
                    Utils.processErrorResponse(getApplicationContext(), response.errorBody());
                }
            }
            @Override
            public void onFailure(Call<DailyQuoteResponse> call, Throwable t) {
                Utils.displayToast(getApplicationContext(), "Network connection error.");
            }
        });

        return Result.success();
    }


    public static void showQuoteNotification(Context context, String author, String text) {
        // We open the QuoteOfTheDayActivity when we click on notification
        Intent intent = new Intent(context , QuoteOfTheDayActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelName = context.getString(R.string.channel_name);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new
                    NotificationChannel(Config.PRIMARY_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.enableVibration(true);
            channel.setDescription(context.getString(R.string.notification_description));
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Config.PRIMARY_CHANNEL_ID)
                .setContentIntent(resultPendingIntent)
                .setContentTitle(author)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_quote)
                .setAutoCancel(true);
        // This is for older Android devices
        builder.setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
        // We make the notification expandable
        builder.setStyle(new NotificationCompat.BigTextStyle()
                .setBigContentTitle(author)
                .bigText(text));

        manager.notify(1, builder.build());
    }
}

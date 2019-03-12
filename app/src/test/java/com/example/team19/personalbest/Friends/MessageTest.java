package com.example.team19.personalbest.Friends;

import android.app.Activity;
import android.net.Uri;
import android.os.Parcel;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.widget.EditText;
import android.widget.ImageButton;

import com.example.team19.personalbest.Chat.ChatActivity;
import com.example.team19.personalbest.Cloud;
import com.example.team19.personalbest.R;
import com.google.android.gms.internal.firebase_auth.zzcz;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.List;
import java.util.concurrent.Executor;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(RobolectricTestRunner.class)
@PowerMockIgnore({ "org.powermock.*", "org.mockito.*", "org.robolectric.*", "android.*", "androidx.*" })
@PrepareForTest({Cloud.class, FirebaseDatabase.class})
public class MessageTest {

    ChatActivity chatActivity;
    ImageButton send_btn;
    EditText message_text;
    DatabaseReference dbRef;

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Before
    public void before() {

        PowerMockito.mockStatic(Cloud.class);
        when(Cloud.getMUser()).thenReturn(new FirebaseUser() {
            @NonNull
            @Override
            public String getUid() {
                return "123456789";
            }

            @NonNull
            @Override
            public String getProviderId() {
                return null;
            }

            @Override
            public boolean isAnonymous() {
                return false;
            }

            @Nullable
            @Override
            public List<String> getProviders() {
                return null;
            }

            @NonNull
            @Override
            public List<? extends UserInfo> getProviderData() {
                return null;
            }

            @NonNull
            @Override
            public FirebaseUser zza(@NonNull List<? extends UserInfo> list) {
                return null;
            }

            @Override
            public FirebaseUser zzce() {
                return null;
            }

            @NonNull
            @Override
            public FirebaseApp zzcc() {
                return null;
            }

            @Nullable
            @Override
            public String getDisplayName() {
                return null;
            }

            @Nullable
            @Override
            public Uri getPhotoUrl() {
                return null;
            }

            @Nullable
            @Override
            public String getEmail() {
                return null;
            }

            @Nullable
            @Override
            public String getPhoneNumber() {
                return null;
            }

            @Nullable
            @Override
            public String zzcf() {
                return null;
            }

            @NonNull
            @Override
            public zzcz zzcg() {
                return null;
            }

            @Override
            public void zza(@NonNull zzcz zzcz) {

            }

            @NonNull
            @Override
            public String zzch() {
                return null;
            }

            @NonNull
            @Override
            public String zzci() {
                return null;
            }

            @Nullable
            @Override
            public FirebaseUserMetadata getMetadata() {
                return null;
            }

            @Override
            public void writeToParcel(Parcel parcel, int i) {

            }

            @Override
            public boolean isEmailVerified() {
                return false;
            }
        });

        // mock static firebase stuff
        FirebaseDatabase db = mock(FirebaseDatabase.class);
        dbRef = mock(DatabaseReference.class);
        when(db.getReference()).thenReturn(dbRef);
        when(dbRef.child(any())).thenReturn(dbRef);
        when(dbRef.push()).thenReturn(dbRef);
        when(dbRef.setValue(any())).thenReturn(new Task<Void>() {
            @Override
            public boolean isComplete() {
                return false;
            }

            @Override
            public boolean isSuccessful() {
                return false;
            }

            @Override
            public boolean isCanceled() {
                return false;
            }

            @Nullable
            @Override
            public Void getResult() {
                return null;
            }

            @Nullable
            @Override
            public <X extends Throwable> Void getResult(@NonNull Class<X> aClass) throws X {
                return null;
            }

            @Nullable
            @Override
            public Exception getException() {
                return null;
            }

            @NonNull
            @Override
            public Task<Void> addOnSuccessListener(@NonNull OnSuccessListener<? super Void> onSuccessListener) {
                return null;
            }

            @NonNull
            @Override
            public Task<Void> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super Void> onSuccessListener) {
                return null;
            }

            @NonNull
            @Override
            public Task<Void> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super Void> onSuccessListener) {
                return null;
            }

            @NonNull
            @Override
            public Task<Void> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
                return null;
            }

            @NonNull
            @Override
            public Task<Void> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
                return null;
            }

            @NonNull
            @Override
            public Task<Void> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
                return null;
            }
        });

        PowerMockito.mockStatic(FirebaseDatabase.class);
        when(FirebaseDatabase.getInstance()).thenReturn(db);

        chatActivity = Robolectric.buildActivity(ChatActivity.class).create().get();

        message_text = chatActivity.findViewById(R.id.chat_message);
        send_btn = chatActivity.findViewById(R.id.chat_send_btn);
    }

    @Test
    public void messageSend() {
        assertEquals(message_text.getText().toString(), "");
        message_text.setText("test message");
        assertEquals(message_text.getText().toString(), "test message");
        send_btn.performClick();

        verify(dbRef, atLeastOnce()).updateChildren(any(), any());
        verify(dbRef, atLeastOnce()).child(any());
        assertEquals(message_text.getText().toString(), "");
    }
}
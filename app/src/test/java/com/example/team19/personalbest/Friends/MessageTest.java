package com.example.team19.personalbest.Friends;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import android.net.Uri;
import android.os.Parcel;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.team19.personalbest.Chat.ChatActivity;
import com.example.team19.personalbest.Cloud;
import com.example.team19.personalbest.R;
import com.google.android.gms.internal.firebase_auth.zzcz;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.FirebaseDatabase;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.NoTypePermission;

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

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(RobolectricTestRunner.class)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*, org.powermock.*"})
@PrepareForTest({Cloud.class, FirebaseDatabase.class})
public class MessageTest {

    public MessageTest() {
        XStream xstream = new XStream();
        // clear out existing permissions and set own ones
        xstream.addPermission(NoTypePermission.NONE);
        xstream.allowTypesByWildcard(new String[] {
                "com.example.team19.personalbest.*"
        });
    }

    ChatActivity chatActivity;
    ImageButton send_btn;
    EditText message_text;
    RecyclerView mMessages_list;

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Rule
    public InstantTaskExecutorRule rule2 = new InstantTaskExecutorRule();

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

        FirebaseDatabase db = mock(FirebaseDatabase.class);
        PowerMockito.mockStatic(FirebaseDatabase.class);
        when(FirebaseDatabase.getInstance()).thenReturn(db);

        chatActivity = Robolectric.setupActivity(ChatActivity.class);

        message_text = chatActivity.findViewById(R.id.chat_message);
        send_btn = chatActivity.findViewById(R.id.chat_send_btn);
    }

    @Test
    public void messageSend() {
        message_text.setText("test message");
        send_btn.performClick();


    }
}

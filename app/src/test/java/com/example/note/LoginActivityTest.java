import android.os.Build;
import android.widget.TextView;

import com.example.note.LoginActivity;
import com.example.note.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1}) // Chọn phiên bản SDK
public class LoginActivityTest {

    private LoginActivity activity;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.setupActivity(LoginActivity.class);
    }

    @Test
    public void testshowPasswordCheckBoxNotNull() {
        CheckBox cb = findViewById(R.id.showPasswordCheckBox);
        assertNotNull(cb);
    }
}
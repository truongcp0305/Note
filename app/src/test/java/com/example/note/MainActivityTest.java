import android.os.Build;
import android.widget.TextView;

import com.example.note.MainActivity;
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
public class MainActivityTest {

    private MainActivity activity;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.setupActivity(MainActivity.class);
    }

    @Test
    public void testTextViewNotNull() {
        TextView textView = activity.findViewById(R.id.textView);
        assertNotNull(textView);
    }

    // Ví dụ khác
    @Test
    public void testTextViewText() {
        TextView textView = activity.findViewById(R.id.textView);
        assertEquals("Hello World!", textView.getText().toString());
    }

    @Test
    public void testdrawerLayoutNotNull() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        assertNotNull(drawerLayout);
    }
}
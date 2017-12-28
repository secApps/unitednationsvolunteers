package volunteers.un.unitednationsvolunteers.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import volunteers.un.unitednationsvolunteers.R;

public class ApprovalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.approval_layout);
    }

    public void clickLogin(View view) {
       startActivity(new Intent(ApprovalActivity.this,LoginActivityChat.class));
       finish();
    }
}

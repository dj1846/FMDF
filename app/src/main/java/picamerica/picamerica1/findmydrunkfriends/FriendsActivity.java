package picamerica.picamerica1.findmydrunkfriends;

import android.os.Bundle;


public class FriendsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle(R.string.title_activity_friends);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_friends;
    }

}

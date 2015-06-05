/*
 * Copyright (C) 2014 Antonio Leiva Gordillo.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package picamerica.picamerica1.findmydrunkfriends;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;



public abstract class BaseActivity extends AppCompatActivity {

    protected Toolbar toolbar;
    private static final String TAG = BaseActivity.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setActionBarIcon(R.drawable.back_arrow);
        }
    }

    /**
     * Abstract method must defined in every child activity
     * return main layout resource and in on
     * onCreate(Bundle savedInstanceState) must
     * not include setContentView(View) this is
     * already included in this activity.
     * @return layoutResourceId
     */
    protected abstract int getLayoutResource();

    /**
     * Set ActionBar icon from Drawable resource
     * if toolbar included in layout.
     * @param iconRes Drawable Resource id
     */
    protected void setActionBarIcon(int iconRes) {
        toolbar.setNavigationIcon(iconRes);
    }

    /**
     * @Override method for menu Item clickListener,
     * Here this is used for actionBar back Button actions.
     * @param item menu item
     * @return true if back button clicked.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Set ActionBar title from string resource
     * if toolbar included in layout.
     * @param titleRes String Resource id
     */
    protected void setActionBarTitle(int titleRes) {
        TextView title = (TextView) findViewById(R.id.title);
        if(title!=null){
            title.setText(titleRes);
        }
    }

    /**
     * Set ActionBar title from string
     * if toolbar included in layout.
     * @param strTitle title
     */
    protected void setActionBarTitle(String strTitle) {
        TextView title = (TextView) findViewById(R.id.title);
        if(title!=null){
            title.setText(strTitle);
        }
    }
}

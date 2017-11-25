/*
 *
 *  * Copyright © 2017 @ Pablo Grela
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package justforcommunity.radiocom.task;


import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import justforcommunity.radiocom.activities.FirebaseActivity;

public class FirebaseUtils extends AsyncTask<Boolean, Float, Boolean> {

    private String token;
    private FirebaseActivity firebaseActivity;

    public FirebaseUtils(FirebaseActivity firebaseActivity) {
        this.firebaseActivity = firebaseActivity;
    }

    // Do in background
    protected Boolean doInBackground(Boolean... urls) {
        token = getTokenFirebase();
        if (token == null || token.isEmpty()) {
            return false;
        }
        return true;
    }

    // Do when background finished
    protected void onPostExecute(Boolean result) {
        if (result) {
            firebaseActivity.setToken(token);
        }
    }

    // Use directly in another function in background
    public static String getTokenFirebase() {
        final StringBuilder tokenAux = new StringBuilder();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.getToken(false).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    tokenAux.append(task.getResult().getToken());
                    countDownLatch.countDown();
                }
            });
            try {
                countDownLatch.await(30L, TimeUnit.SECONDS);
                return tokenAux.toString();
            } catch (InterruptedException ie) {
                return null;
            }
        }
        return null;
    }
}

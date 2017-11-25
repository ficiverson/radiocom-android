/*
 *
 *  * Copyright © 2016 @ Fernando Souto González
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

package justforcommunity.radiocom.views.discrollview;

/**
 * Represents a View able to be discrollved.
 */
public interface Discrollvable {

    /**
     * Called to discrollve the View.
     * @param ratio discrollve ratio between 0.0 and 1.0.
     *              1.0 means the View is totally discrollved
     */
    public void onDiscrollve(float ratio);

    /**
     * Called to reset the discrollvation of the View.
     * In this method, you have to reset the View in order
     * to be discrollved again.
     */
    public void onResetDiscrollve();
}

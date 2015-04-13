
package com.projecty.sleepgroundbox.model.list;

import com.projecty.sleepgroundbox.model.base.StatisticsItem;
import com.projecty.sleepgroundbox.model.base.StatisticsPage;
import com.projecty.sleepgroundbox.model.base.VideoList;
import com.projecty.sleepgroundbox.model.item.SearchItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class SearchVideolist extends VideoList{

    public SearchVideolist(JSONObject jsonPlaylist) throws JSONException, ParseException {
        super(jsonPlaylist);
        addPage(jsonPlaylist);
    }


    public void addPage(JSONObject jsonPlaylist) throws JSONException, ParseException {
        statisticsPages.add(new StatisticsPage(
                jsonPlaylist.getJSONArray("items"),
                jsonPlaylist.getString("etag"),
                jsonPlaylist.optString("nextPageToken", null)) {
            @Override
            protected StatisticsItem getStatisticsItem(JSONObject item) throws JSONException, ParseException {
                return new SearchItem(item);
            }
        });
    }

}

package com.projecty.sleepgroundbox.model.item;

import com.projecty.sleepgroundbox.model.base.PlayItem;

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
public class PlaylistItem extends PlayItem {

    public PlaylistItem(JSONObject jsonItem) throws JSONException, ParseException {
        id = jsonItem.getString("id");
        JSONObject snippet = jsonItem.getJSONObject("snippet");

        title = snippet.getString("title");
        date = snippet.getString("publishedAt").substring(0, 10);
        thumbnailUrl = snippet.getJSONObject("thumbnails").getJSONObject("medium").getString("url");
        videoCount = String.valueOf(jsonItem.getJSONObject("contentDetails").getInt("itemCount"));

    }

}

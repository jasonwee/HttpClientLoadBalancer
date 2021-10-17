/*
 * Copyright [2021] [Jason Wee]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.weetech.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class JwResult {

    protected JsonObject jsonObject;
    protected String jsonString;
    protected String pathToResult;
    protected int responseCode;
    protected boolean isSucceeded;
    protected String errorMessage;
    protected Gson gson;

    public JwResult(Gson gson) {
        this.gson = gson;
    }

    public void setPathToResult(String pathToResult) {
        this.pathToResult = pathToResult;
    }

    public boolean isSucceeded() {
        return isSucceeded;
    }

    public void setSucceeded(boolean succeeded) {
        isSucceeded = succeeded;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    /**
     * manually set an error message, eg. for the cases where non-200 response code is received
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
        if (jsonObject.get("error") != null) {
            errorMessage = jsonObject.get("error").toString();
        }
    }

}

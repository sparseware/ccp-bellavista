/*
 * Copyright (C) SparseWare Inc.
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
package com.sparseware.bellavista;

import com.appnativa.rare.Platform;
import com.appnativa.util.json.JSONObject;

public class Consults extends Notes {
  public Consults() {
    super("consults", "Consults");
    JSONObject info = (JSONObject) Platform.getAppContext().getData("consultsInfo");
    attachmentColumn = info.optInt("attachmentColumn", -1);
    parentColumn = info.optInt("parentColumn", -1);
    infoName = "consultsInfo";
    documentPath = "/hub/main/consults/consult/";
  }

}

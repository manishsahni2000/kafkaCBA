/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package custom.writestrategy;

import com.mongodb.client.model.*;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.kafka.connect.sink.converter.SinkDocument;
import com.mongodb.kafka.connect.sink.writemodel.strategy.WriteModelStrategy;
import org.apache.kafka.connect.errors.DataException;
import org.bson.BsonDocument;


public class UpsertAsPartOfDocumentStrategy implements WriteModelStrategy {

  private final static String ID_FIELD_NAME = "orderid";
  private final static String EVENT_TYPE_FIELD_NAME = "itemid";

  @Override
  public WriteModel<BsonDocument> createWriteModel(SinkDocument sinkDocument) {
    // Get old document
    BsonDocument changeStreamDocument = sinkDocument.getValueDoc().orElseThrow(() -> new DataException("Missing Value Document"));

    // Extract event type from document
    String eventType = "";
    try {
      eventType = changeStreamDocument.getString(EVENT_TYPE_FIELD_NAME).getValue();
      //eventType += "Adding Custom Message";
    } catch (Exception ex) {
      String errorMessage = String.format("Encountered an exception when attempting to retrieve field %s from fulldocument: ", EVENT_TYPE_FIELD_NAME);
      System.out.println(errorMessage + ex);
      ex.printStackTrace();
      return null;
    }
    // Create new document where old one is nested
    BsonDocument newDocument = new BsonDocument("Custom_CBA_Write_Strategy_Example",changeStreamDocument);
    // Create WriteModel
    return new InsertOneModel<>(newDocument);
  }
}

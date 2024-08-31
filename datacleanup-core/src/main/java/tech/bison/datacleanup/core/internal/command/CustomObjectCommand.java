/*
 * Copyright (C) 2024 Bison Schweiz AG
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
package tech.bison.datacleanup.core.internal.command;

import static tech.bison.datacleanup.core.api.command.CleanableResourceType.CUSTOM_OBJECT;

import com.commercetools.api.models.custom_object.CustomObject;
import com.commercetools.api.models.custom_object.CustomObjectPagedQueryResponse;
import io.vrap.rmf.base.client.ApiHttpException;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.bison.datacleanup.core.api.command.CleanableResourceType;
import tech.bison.datacleanup.core.api.command.CleanupCommand;
import tech.bison.datacleanup.core.api.command.ResourceCleanupSummary;
import tech.bison.datacleanup.core.api.executor.Context;

public class CustomObjectCommand implements CleanupCommand {

  private final static Logger LOG = LoggerFactory.getLogger(CustomObjectCommand.class);
  private final String predicate;

  public CustomObjectCommand(String predicate) {
    this.predicate = predicate;
  }

  @Override
  public ResourceCleanupSummary execute(Context context) {
    CustomObjectPagedQueryResponse queryResponse = context.getProjectApiRoot().customObjects().get().withWhere(predicate).executeBlocking().getBody();
    LOG.info("Found {} custom objects to be deleted.", queryResponse.getCount());
    var deletedObjectsIds = new ArrayList<String>();
    for (CustomObject customObject : queryResponse.getResults()) {
      try {
        var response = context.getProjectApiRoot().customObjects().withContainerAndKey(customObject.getContainer(), customObject.getKey())
            .delete()
            .executeBlocking()
            .getBody();
        LOG.info("Deleted custom object with id '{}' and version '{}'.", response.getId(), response.getVersion());
        deletedObjectsIds.add(customObject.getId());
      } catch (ApiHttpException exception) {
        LOG.error("Failed to delete custom object with id '{}'.", customObject.getId(), exception);
      }
    }
    return new ResourceCleanupSummary(deletedObjectsIds.size());
  }

  @Override
  public CleanableResourceType getResourceType() {
    return CUSTOM_OBJECT;
  }
}

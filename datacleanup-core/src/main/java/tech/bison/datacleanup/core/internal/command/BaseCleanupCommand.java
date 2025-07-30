/*
 * Copyright (C) 2000 - 2024 Bison Schweiz AG
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.bison.datacleanup.core.internal.command;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.ResourcePagedQueryResponse;
import com.commercetools.api.models.common.BaseResource;
import io.vrap.rmf.base.client.ApiHttpException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.bison.datacleanup.core.api.command.CleanupCommand;
import tech.bison.datacleanup.core.api.command.ResourceCleanupSummary;
import tech.bison.datacleanup.core.api.executor.Context;

public abstract class BaseCleanupCommand<T extends BaseResource> implements CleanupCommand {

  private final static Logger LOG = LoggerFactory.getLogger(BaseCleanupCommand.class);
  final static Long QUERY_RESULT_LIMIT = 100L;
  private final List<String> predicates;

  protected BaseCleanupCommand(List<String> predicates) {
    this.predicates = predicates;
  }

  protected List<String> getPredicates() {
    return predicates;
  }

  protected abstract ResourcePagedQueryResponse<T> getResourcesToDelete(ProjectApiRoot projectApiRoot);

  protected abstract T delete(ProjectApiRoot projectApiRoot, T resource);


  @Override
  public ResourceCleanupSummary execute(Context context) {
    var queryResponse = getResourcesToDelete(context.getProjectApiRoot());
    LOG.info("Found {} resources ({}) to be deleted.", queryResponse.getCount(), getResourceType().getName());
    var deletedObjectsIds = new ArrayList<String>();
    for (var customObject : queryResponse.getResults()) {
      try {
        var response = delete(context.getProjectApiRoot(), customObject);
        LOG.info("Deleted {} with id '{}' and version '{}'.", getResourceType().getName(), response.getId(), response.getVersion());
        deletedObjectsIds.add(customObject.getId());
      } catch (ApiHttpException exception) {
        LOG.error("Failed to delete {} with id '{}'.", getResourceType().getName(), customObject.getId(), exception);
      }
    }
    return new ResourceCleanupSummary(deletedObjectsIds.size());
  }
}

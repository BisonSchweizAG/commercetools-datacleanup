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

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.ResourcePagedQueryResponse;
import com.commercetools.api.models.custom_object.CustomObject;
import java.util.List;
import tech.bison.datacleanup.core.api.command.CleanableResourceType;

public class CustomObjectCommand extends BaseCleanupCommand<CustomObject> {

  public CustomObjectCommand(List<String> predicates) {
    super(predicates);
  }

  @Override
  protected ResourcePagedQueryResponse<CustomObject> getResourcesToDelete(ProjectApiRoot projectApiRoot) {
    return projectApiRoot.customObjects().get().withWhere(getPredicates()).executeBlocking().getBody();
  }

  @Override
  protected CustomObject delete(ProjectApiRoot projectApiRoot, CustomObject resource) {
    return projectApiRoot.customObjects().withContainerAndKey(resource.getContainer(), resource.getKey())
        .delete()
        .executeBlocking()
        .getBody();
  }

  @Override
  public CleanableResourceType getResourceType() {
    return CUSTOM_OBJECT;
  }
}

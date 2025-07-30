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

import static tech.bison.datacleanup.core.api.command.CleanableResourceType.CART;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.ResourcePagedQueryResponse;
import com.commercetools.api.models.cart.Cart;
import java.util.List;
import tech.bison.datacleanup.core.api.command.CleanableResourceType;

public class CartCommand extends BaseCleanupCommand<Cart> {

  public CartCommand(List<String> predicates) {
    super(predicates);
  }

  @Override
  protected ResourcePagedQueryResponse<Cart> getResourcesToDelete(ProjectApiRoot projectApiRoot) {
    return projectApiRoot.carts().get()
        .withLimit(BaseCleanupCommand.QUERY_RESULT_LIMIT)
        .withWhere(getPredicates())
        .executeBlocking().getBody();
  }

  @Override
  protected Cart delete(ProjectApiRoot projectApiRoot, Cart resource) {
    return projectApiRoot.carts().withId(resource.getId())
        .delete()
        .executeBlocking()
        .getBody();
  }

  @Override
  public CleanableResourceType getResourceType() {
    return CART;
  }
}

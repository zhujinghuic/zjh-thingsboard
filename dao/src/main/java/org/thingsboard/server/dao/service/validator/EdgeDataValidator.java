/**
 * Copyright © 2016-2022 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.dao.service.validator;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.Tenant;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.cache.EntitiesCacheManager;
import org.thingsboard.server.dao.customer.CustomerDao;
import org.thingsboard.server.dao.edge.EdgeDao;
import org.thingsboard.server.dao.exception.DataValidationException;
import org.thingsboard.server.dao.service.DataValidator;
import org.thingsboard.server.dao.tenant.TenantDao;

import static org.thingsboard.server.dao.model.ModelConstants.NULL_UUID;

@Component
@AllArgsConstructor
public class EdgeDataValidator extends DataValidator<Edge> {

    private final EdgeDao edgeDao;
    private final TenantDao tenantDao;
    private final CustomerDao customerDao;
    private final EntitiesCacheManager cacheManager;

    @Override
    protected void validateCreate(TenantId tenantId, Edge edge) {
    }

    @Override
    protected void validateUpdate(TenantId tenantId, Edge edge) {
        Edge old = edgeDao.findById(edge.getTenantId(), edge.getId().getId());
        if (!old.getName().equals(edge.getName())) {
            cacheManager.removeEdgeFromCacheByName(tenantId, old.getName());
        }
    }

    @Override
    protected void validateDataImpl(TenantId tenantId, Edge edge) {
        if (org.springframework.util.StringUtils.isEmpty(edge.getType())) {
            throw new DataValidationException("Edge type should be specified!");
        }
        if (org.springframework.util.StringUtils.isEmpty(edge.getName())) {
            throw new DataValidationException("Edge name should be specified!");
        }
        if (org.springframework.util.StringUtils.isEmpty(edge.getSecret())) {
            throw new DataValidationException("Edge secret should be specified!");
        }
        if (StringUtils.isEmpty(edge.getRoutingKey())) {
            throw new DataValidationException("Edge routing key should be specified!");
        }
        if (edge.getTenantId() == null) {
            throw new DataValidationException("Edge should be assigned to tenant!");
        } else {
            Tenant tenant = tenantDao.findById(edge.getTenantId(), edge.getTenantId().getId());
            if (tenant == null) {
                throw new DataValidationException("Edge is referencing to non-existent tenant!");
            }
        }
        if (edge.getCustomerId() == null) {
            edge.setCustomerId(new CustomerId(NULL_UUID));
        } else if (!edge.getCustomerId().getId().equals(NULL_UUID)) {
            Customer customer = customerDao.findById(edge.getTenantId(), edge.getCustomerId().getId());
            if (customer == null) {
                throw new DataValidationException("Can't assign edge to non-existent customer!");
            }
            if (!customer.getTenantId().getId().equals(edge.getTenantId().getId())) {
                throw new DataValidationException("Can't assign edge to customer from different tenant!");
            }
        }
    }
}

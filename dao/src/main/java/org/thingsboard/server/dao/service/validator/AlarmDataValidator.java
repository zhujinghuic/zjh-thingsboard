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
import org.thingsboard.server.common.data.Tenant;
import org.thingsboard.server.common.data.alarm.Alarm;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.exception.DataValidationException;
import org.thingsboard.server.dao.service.DataValidator;
import org.thingsboard.server.dao.tenant.TenantDao;

@Component
@AllArgsConstructor
public class AlarmDataValidator extends DataValidator<Alarm> {

    private final TenantDao tenantDao;

    @Override
    protected void validateDataImpl(TenantId tenantId, Alarm alarm) {
        if (StringUtils.isEmpty(alarm.getType())) {
            throw new DataValidationException("Alarm type should be specified!");
        }
        if (alarm.getOriginator() == null) {
            throw new DataValidationException("Alarm originator should be specified!");
        }
        if (alarm.getSeverity() == null) {
            throw new DataValidationException("Alarm severity should be specified!");
        }
        if (alarm.getStatus() == null) {
            throw new DataValidationException("Alarm status should be specified!");
        }
        if (alarm.getTenantId() == null) {
            throw new DataValidationException("Alarm should be assigned to tenant!");
        } else {
            Tenant tenant = tenantDao.findById(alarm.getTenantId(), alarm.getTenantId().getId());
            if (tenant == null) {
                throw new DataValidationException("Alarm is referencing to non-existent tenant!");
            }
        }
    }
}

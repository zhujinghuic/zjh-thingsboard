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
package org.thingsboard.server.common.data.id;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AdminSettingsId extends UUIDBased {

    /**
     * @JsonCreator
     * 当json在反序列化时，默认选择类的无参构造函数创建类对象，没有无参构造函数时会报错，
     * @JsonCreator作用就是指定一个有参构造函数供反序列化时调用。
     * 该构造方法的参数前面需要加上@JsonProperty,否则会报错。
     * @param id
     */
    @JsonCreator
    public AdminSettingsId(@JsonProperty("id") UUID id){
        super(id);
    }
    
}

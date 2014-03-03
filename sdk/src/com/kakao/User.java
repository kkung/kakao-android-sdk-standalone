/**
 * Copyright 2014 Kakao Corp.
 *
 * Redistribution and modification in source or binary forms are not permitted without specific prior written permission. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kakao;

/**
 * UserManagement API의 결과 객체로 사용자의 id를 포함하고 있다.
 * @author MJ
 */
public class User {
    /**
     * 사용자 ID
     */
    protected long id;

    // for jackson
    public User(){}

    public User(final long id){
        this.id = id;
    }

    /**
     * UserManagement API 요청을 시도한 사용자 id
     *
     * @return UserManagement API 요청을 시도한 사용자 id
     */
    public long getId() {
        return id;
    }

}

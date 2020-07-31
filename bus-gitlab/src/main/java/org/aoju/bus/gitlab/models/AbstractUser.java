/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2020 aoju.org Greg Messner and other contributors.         *
 *                                                                               *
 * Permission is hereby granted, free of charge, to any person obtaining a copy  *
 * of this software and associated documentation files (the "Software"), to deal *
 * in the Software without restriction, including without limitation the rights  *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell     *
 * copies of the Software, and to permit persons to whom the Software is         *
 * furnished to do so, subject to the following conditions:                      *
 *                                                                               *
 * The above copyright notice and this permission notice shall be included in    *
 * all copies or substantial portions of the Software.                           *
 *                                                                               *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR    *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,      *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE   *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER        *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, *
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN     *
 * THE SOFTWARE.                                                                 *
 ********************************************************************************/
package org.aoju.bus.gitlab.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.aoju.bus.gitlab.JacksonJson;

import java.util.Date;

/**
 * @author Kimi Liu
 * @version 6.0.3
 * @since JDK 1.8+
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractUser<U extends AbstractUser<U>> {

    private String avatarUrl;
    private Date createdAt;
    private String email;
    private Integer id;
    private String name;
    private String state;
    private String username;
    private String webUrl;

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public U withAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        return (U) this;
    }

    public U withCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return (U) this;
    }

    public U withEmail(String email) {
        this.email = email;
        return (U) this;
    }

    public U withId(Integer id) {
        this.id = id;
        return (U) this;
    }

    public U withName(String name) {
        this.name = name;
        return (U) this;
    }

    public U withState(String state) {
        this.state = state;
        return (U) this;
    }

    public U withUsername(String username) {
        this.username = username;
        return (U) this;
    }

    public U withWebUrl(String webUrl) {
        this.webUrl = webUrl;
        return (U) this;
    }

    @Override
    public String toString() {
        return (JacksonJson.toJsonString(this));
    }

}

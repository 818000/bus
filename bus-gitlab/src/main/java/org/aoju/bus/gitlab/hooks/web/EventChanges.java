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
package org.aoju.bus.gitlab.hooks.web;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import org.aoju.bus.gitlab.JacksonJson;
import org.aoju.bus.gitlab.models.Assignee;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kimi Liu
 * @version 6.0.3
 * @since JDK 1.8+
 */
public abstract class EventChanges {

    private ChangeContainer<Integer> authorId;
    private ChangeContainer<Date> createdAt;
    private ChangeContainer<Date> updatedAt;
    private ChangeContainer<Integer> updatedById;
    private ChangeContainer<String> title;
    private ChangeContainer<String> description;
    private ChangeContainer<String> state;
    private ChangeContainer<Integer> milestoneId;
    private ChangeContainer<List<EventLabel>> labels;
    private ChangeContainer<List<Assignee>> assignees;
    private ChangeContainer<Integer> totalTimeSpent;
    private Map<String, ChangeContainer<Object>> otherProperties = new LinkedHashMap<>();

    public ChangeContainer<Integer> getAuthorId() {
        return authorId;
    }

    public void setAuthorId(ChangeContainer<Integer> authorId) {
        this.authorId = authorId;
    }

    public ChangeContainer<Date> getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ChangeContainer<Date> createdAt) {
        this.createdAt = createdAt;
    }

    public ChangeContainer<Date> getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(ChangeContainer<Date> updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ChangeContainer<Integer> getUpdatedById() {
        return updatedById;
    }

    public void setUpdatedById(ChangeContainer<Integer> updatedById) {
        this.updatedById = updatedById;
    }

    public ChangeContainer<String> getTitle() {
        return title;
    }

    public void setTitle(ChangeContainer<String> title) {
        this.title = title;
    }

    public ChangeContainer<String> getDescription() {
        return description;
    }

    public void setDescription(ChangeContainer<String> description) {
        this.description = description;
    }

    public ChangeContainer<String> getState() {
        return state;
    }

    public void setState(ChangeContainer<String> state) {
        this.state = state;
    }

    public ChangeContainer<Integer> getMilestoneId() {
        return milestoneId;
    }

    public void setMilestoneId(ChangeContainer<Integer> milestoneId) {
        this.milestoneId = milestoneId;
    }

    public ChangeContainer<List<EventLabel>> getLabels() {
        return labels;
    }

    public void setLabels(ChangeContainer<List<EventLabel>> labels) {
        this.labels = labels;
    }

    public ChangeContainer<List<Assignee>> getAssignees() {
        return assignees;
    }

    public void setAssignees(ChangeContainer<List<Assignee>> assignees) {
        this.assignees = assignees;
    }

    public ChangeContainer<Integer> getTotalTimeSpent() {
        return totalTimeSpent;
    }

    public void setTotalTimeSpent(ChangeContainer<Integer> totalTimeSpent) {
        this.totalTimeSpent = totalTimeSpent;
    }

    public <T> ChangeContainer<T> get(String property) {

        if (otherProperties.containsKey(property)) {
            try {
                final ChangeContainer<Object> container = otherProperties.get(property);
                // noinspection unchecked :  It's duty from caller to be sure to do that
                return container != null ? (ChangeContainer<T>) container : null;
            } catch (ClassCastException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    @JsonAnyGetter
    public Map<String, ChangeContainer<Object>> any() {
        return this.otherProperties;
    }

    @JsonAnySetter
    public void set(String name, ChangeContainer<Object> value) {
        otherProperties.put(name, value);
    }

    @Override
    public String toString() {
        return (JacksonJson.toJsonString(this));
    }
}

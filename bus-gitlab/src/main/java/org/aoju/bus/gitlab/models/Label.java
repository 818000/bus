/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2021 aoju.org Greg Messner and other contributors.         *
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
 *                                                                               *
 ********************************************************************************/
package org.aoju.bus.gitlab.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.aoju.bus.gitlab.GitLabApiForm;
import org.aoju.bus.gitlab.JacksonJson;

/**
 * @author Kimi Liu
 * @version 6.2.2
 * @since JDK 1.8+
 */
public class Label {

    private Integer id;
    private String name;
    private String color;
    private String description;
    private Integer openIssuesCount;
    private Integer closedIssuesCount;
    private Integer openMergeRequestsCount;
    private Boolean subscribed;
    private Integer priority;

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

    public Label withName(String name) {
        this.name = name;
        return (this);
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Label withColor(String color) {
        this.color = color;
        return (this);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Label withDescription(String description) {
        this.description = description;
        return (this);
    }

    public Integer getOpenIssuesCount() {
        return openIssuesCount;
    }

    public void setOpenIssuesCount(Integer openIssuesCount) {
        this.openIssuesCount = openIssuesCount;
    }

    public Integer getClosedIssuesCount() {
        return closedIssuesCount;
    }

    public void setClosedIssuesCount(Integer closedIssuesCount) {
        this.closedIssuesCount = closedIssuesCount;
    }

    public Integer getOpenMergeRequestsCount() {
        return openMergeRequestsCount;
    }

    public void setOpenMergeRequestsCount(Integer openMergeRequestsCount) {
        this.openMergeRequestsCount = openMergeRequestsCount;
    }

    public Boolean isSubscribed() {
        return subscribed;
    }

    public void setSubscribed(Boolean subscribed) {
        this.subscribed = subscribed;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Label withPriority(Integer priority) {
        this.priority = priority;
        return (this);
    }

    @Override
    public String toString() {
        return (JacksonJson.toJsonString(this));
    }

    /**
     * Get the form params specified by this instance.
     *
     * @param isCreate set to true if the params are for a create label call, false for an update
     * @return a GitLabApiForm instance holding the form parameters for this LabelParams instance
     */
    @JsonIgnore
    public GitLabApiForm getForm(boolean isCreate) {
        GitLabApiForm form = new GitLabApiForm()
                .withParam("description", description)
                .withParam("color", color, isCreate)
                .withParam("priority", priority);

        if (isCreate) {
            form.withParam("name", name, true);
        } else {
            form.withParam("new_name", name);
        }

        return (form);
    }
}

/*
 * Copyright (C) 2009 The Android Open Source Project
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

package vn.mbm.phimp.me.gallery3d.picasa;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.xml.sax.Attributes;

public abstract class Entry {
    public static final String[] ID_PROJECTION = { "_id" };

    // The primary key of the entry.
    @Column("_id")
    public long id = 0;

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Table {
        String value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Column {
        String value();

        boolean indexed() default false;

        boolean fullText() default false;
    }

    public void clear() {
        id = 0;
    }

    public void setPropertyFromXml(String uri, String localName, Attributes attrs, String content) {
        throw new UnsupportedOperationException("Entry class does not support XML parsing");
    }
}

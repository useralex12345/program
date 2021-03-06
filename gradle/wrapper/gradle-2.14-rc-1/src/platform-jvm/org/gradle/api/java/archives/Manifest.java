/*
 * Copyright 2010 the original author or authors.
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
package org.gradle.api.java.archives;

import groovy.lang.Closure;
import org.gradle.api.Incubating;

import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;

/**
 * Represents the manifest file of a JAR file.
 */
public interface Manifest {
    /**
     * Returns the main attributes of the manifest.
     */
    Attributes getAttributes();

    /**
     * Returns the sections of the manifest (excluding the main section).
     *
     * @return A map with the sections, where the key represents the section name and value the section attributes.
     */
    Map<String, Attributes> getSections();

    /**
     * Adds content to the main attributes of the manifest.
     *
     * @param attributes The values to add to the main attributes. The values can be any object. For evaluating the value objects
     * their {@link Object#toString()} method is used. This is done lazily either before writing or when {@link #getEffectiveManifest()}
     * is called.
     *
     * @return this
     * @throws ManifestException If a key is invalid according to the manifest spec or if a key or value is null.
     */
    Manifest attributes(Map<String, ?> attributes) throws ManifestException;

    /**
     * Adds content to the given section of the manifest.
     *
     * @param attributes The values to add to the section. The values can be any object. For evaluating the value objects
     * their {@link Object#toString()} method is used. This is done lazily either before writing or when {@link #getEffectiveManifest()}
     * is called.
     * @param sectionName The name of the section
     *
     * @return this
     * @throws ManifestException If a key is invalid according to the manifest spec or if a key or value is null.
     */
    Manifest attributes(Map<String, ?> attributes, String sectionName) throws ManifestException;

    /**
     * Returns a new manifest instance where all the attribute values are expanded (e.g. their toString method is called).
     * The returned manifest also contains all the attributes of the to be merged manifests specified in {@link #from(Object...)}.
     */
    Manifest getEffectiveManifest();

    /**
     * The character set used to encode this manifest content.
     * Defaults to UTF-8.
     * You can change this property but it is not recommended as JVMs expect manifests content to be encoded using UTF-8.
     *
     * @return the character set used to encode this manifest content
     * @since 2.14
     */
    @Incubating
    String getContentCharset();

    /**
     * The character set used to encode this manifest content.
     *
     * @param contentCharset the character set used to encode this manifest content
     * @since 2.14
     * @see #getContentCharset()
     */
    @Incubating
    void setContentCharset(String contentCharset);

    /**
     * Writes the manifest into a writer.
     *
     * If writing to bytes, prefer using the UTF-8 character set for encoding.
     *
     * @param writer The writer to write the manifest to
     * @return this
     * @see #getContentCharset()
     */
    Manifest writeTo(Writer writer);

    /**
     * Writes the manifest into a stream.
     *
     * The manifest will be encoded using the character set defined by the {@link #getContentCharset()} property.
     *
     * @param outputStream The stream to write the manifest to
     * @return this
     * @since 2.14
     */
    @Incubating
    Manifest writeTo(OutputStream outputStream);

    /**
     * Writes the manifest into a file. The path's are resolved as defined by {@link org.gradle.api.Project#files(Object...)}
     *
     * The manifest will be encoded using the character set defined by the {@link #getContentCharset()} property.
     *
     * @param path The path of the file to write the manifest into.
     * @return this
     */
    Manifest writeTo(Object path);

    /**
     * Specifies other manifests to be merged into this manifest. A merge path can either be another instance of
     * {@link org.gradle.api.java.archives.Manifest} or a file path as interpreted by {@link org.gradle.api.Project#files(Object...)}.
     *
     * The merge is not happening instantaneously. It happens either before writing or when {@link #getEffectiveManifest()}
     * is called.
     *
     * @param mergePath
     * @return this
     */
    Manifest from(Object... mergePath);

    /**
     * Specifies other manifests to be merged into this manifest. A merge path is interpreted as described in
     * {@link #from(Object...)}.
     *
     * The merge is not happening instantaneously. It happens either before writing or when {@link #getEffectiveManifest()}
     * is called.
     *
     * The closure configures the underlying {@link org.gradle.api.java.archives.ManifestMergeSpec}.
     *
     * @param mergePath
     * @param closure
     * @return this
     */
    Manifest from(Object mergePath, Closure<?> closure);
}

/*
 * Copyright (c) 2008 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.

 * Author: Dmitry Abramov
 * Created: 19.12.2008 15:15:51
 * $Id$
 */
package com.haulmont.cuba.gui.xml.layout;

import com.haulmont.cuba.gui.components.Component;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class LayoutLoader {
    protected ComponentLoader.Context context;
    private ComponentsFactory factory;
    private LayoutLoaderConfig config;

    private Locale locale;
    private ResourceBundle resourceBundle;

    public LayoutLoader(ComponentLoader.Context context, ComponentsFactory factory, LayoutLoaderConfig config) {
        this.context = context;
        this.factory = factory;
        this.config = config;
    }

    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public void setResourceBundle(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    public Component loadComponent(URL uri) {
        try {
            final InputStream stream = uri.openStream();

            SAXReader reader = new SAXReader();
            Document doc;
            try {
                doc = reader.read(stream);
            } catch (DocumentException e) {
                throw new RuntimeException(e);
            }

            Element element = doc.getRootElement();

            return loadComponent(element);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    protected ComponentLoader getLoader(Element element) {
        Class<? extends ComponentLoader> loaderClass = config.getLoader(element.getName());
        if (loaderClass == null) {
            throw new IllegalStateException(String.format("Unknown component '%s'", element.getName()));
        }

        ComponentLoader loader;
        try {
            final Constructor<? extends ComponentLoader> constructor =
                    loaderClass.getConstructor(ComponentLoader.Context.class, LayoutLoaderConfig.class, ComponentsFactory.class);
            loader = constructor.newInstance(context, config, factory);

            loader.setLocale(locale);
            loader.setResourceBundle(resourceBundle);
        } catch (Throwable e) {
            try {
                final Constructor<? extends ComponentLoader> constructor = loaderClass.getConstructor(ComponentLoader.Context.class);
                loader = constructor.newInstance(context);
                loader.setLocale(locale);
                loader.setResourceBundle(resourceBundle);
            } catch (Throwable e1) {
                throw new RuntimeException(e1);
            }
        }

        return loader;
    }

    public <T extends Component> T loadComponent(Element element) {
        try {
            ComponentLoader loader = getLoader(element);
            return (T) loader.loadComponent(factory, element);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}


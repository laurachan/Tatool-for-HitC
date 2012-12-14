/*******************************************************************************
 * Copyright (c) 2011 Michael Ruflin, André Locher, Claudia von Bastian.
 * 
 * This file is part of Tatool.
 * 
 * Tatool is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published 
 * by the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 * 
 * Tatool is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Tatool. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package ch.tatool.app.util;

import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Bean utilities - hides some Spring internal code. 
 * 
 * @author Michael Ruflin
 *
 */
public class ContextUtils implements ApplicationContextAware {

    /** Static reference to the ApplicationContext. */
    private static ApplicationContext applicationContext;
    
    /**
     * Implementation of interface ApplicationContextAware
     * 
     * Provides a reference to the ApplicationContext object. 
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ContextUtils.applicationContext = applicationContext;
    }
    
    /**
     * Create a new BeanFactory using the main ApplicationContext as parent.
     * 
     * @param classpath the path to the bean definition file in the classpath 
     * @param properties set of properties that should be replaced in the definitions
     * @return a BeanFactory instance
     */
    public static BeanFactory createBeanFactory(String classpath, Properties properties) {
        // create an empty bean factory 
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        
        // load the context file - has to be done here, otherwise properties won't get resolved
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        Resource resource = new ClassPathResource(classpath);
        reader.loadBeanDefinitions(resource);
        
        // apply the properties to the context
        PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
        configurer.setProperties(properties);
        configurer.setIgnoreUnresolvablePlaceholders(false);
        configurer.setIgnoreResourceNotFound(false);
        configurer.setSystemPropertiesMode(PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_FALLBACK);
        configurer.postProcessBeanFactory(beanFactory);
        
        return beanFactory;
    }

}

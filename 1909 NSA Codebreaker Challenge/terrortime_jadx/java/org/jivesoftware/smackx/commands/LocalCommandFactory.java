package org.jivesoftware.smackx.commands;

import java.lang.reflect.InvocationTargetException;

public interface LocalCommandFactory {
    LocalCommand getInstance() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;
}
